package items;

import gamePanels.UpgradePanel;
import gamePanels.SpellsPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import menu.Game;
import menu.Util;
import reseau.Reseau;

public class UnitR extends Unit{
	
	public static final float NORMALSIZEX=35f,NORMALSIZEY=35f;
	public static int prix []=new int[]{1,4,0};
	public int resource[]=new int[3];
	public int full=40;
	public int miningSpeed=1;
	private long mineTimer=0;
	public int minePeriod=4000;
	public int mining=3;

	public UnitR(float[] position, float[] size) {
		super(position, size);
	}
	public UnitR(Planet mother,float[] decalage,Player owner){
		super(new float[]{mother.getPosition()[0]+decalage[0],mother.getPosition()[1]+decalage[1]},new float[]{NORMALSIZEX,NORMALSIZEY});
		destination[0]=position[0];
		destination[1]=position[1];
		this.mother=mother;
		this.owner=owner;
		
		if(UpgradePanel.upgrades[UpgradePanel.nStrongerUnit][Game.pTurn]){
			life[Unit.NTYPE]+=UpgradePanel.stronger;
		}
		if(UpgradePanel.upgrades[UpgradePanel.nSpeedCollect][Game.pTurn]){
			minePeriod-=UpgradePanel.speedCollectPeriod;
		}
	}
	public void render(Graphics g){
		//super.render(g);
		g.setColor(Resource.colors[mining]);
		if(cursorOn){
			Util.drawCursorOn(this, (Graphics2D) g,true);
			Util.drawLife(this, g);
		}
		Util.drawAndRotate(this,g);
		g.setColor(Resource.colors[mining]);
		if(selected){
			g.drawRect((int)position[0], (int)position[1], (int)size[0], (int)size[1]);
			Util.drawLife(this, g);
		}
		else if(SpellsPanel.numSpell==SpellsPanel.nHeal)
			Util.drawLife(this, g);

		if(selected || cursorOn)
		for(int i=0;i<3;i++){
			g.setColor(Resource.colors[i]);
			g.fillRect((int)position[0]+(int)(Resource.width+2)*i, (int)((int)position[1]+(int)size[1]-(int)(resource[i]*Resource.height)),
					(int)Resource.width, (int)(resource[i]*Resource.height));
		}
		//g.setColor(owner.color);
		//g.drawLine((int)position[0], (int)position[1], (int)(position[0]+size[0]), (int)(position[1]+size[1]));
		//if(selected){
			//System.out.println(resource[0]+"g "+resource[1]+"u "+resource[2]+"c");
		//}
		if(!Reseau.connected && !Reseau.fakeConnected)
			readyTimer=System.currentTimeMillis();
		if(!ready)
			Util.drawReady(this, (Graphics2D)g);
		g.setColor(Color.white);
		g.drawString("X", (int)position[0]+5, (int)position[1]+20);
		
		drawDeath(g);
	}
	public static boolean createYourSelf(Planet mother,int type){
		Unit unit=new UnitR(mother,new float[]{0f,0f},Game.players[Game.pTurn]);
		boolean place=unit.changePlanet(null, mother);
		unit.position[0]=unit.destination[0];
		unit.position[1]=unit.destination[1];
		return place;
	}
	public boolean changePlanet(Planet old,Planet pnew){
		int n=((int)pnew.getSize()[0])/((int)size[0]);
		int nx=pnew.unitsR.size()%n;
		int ny=pnew.unitsR.size()/n;
		int n2=((int)pnew.getSize()[0])/((int)Unit.NORMALSIZEX);
		int ny2=pnew.unitsN.size()/(n2+1);
		int maxLine=1;
		if(UpgradePanel.upgrades[UpgradePanel.nLinesUp][Game.pTurn])
			maxLine=2;
		if(ny>maxLine || pnew.size[1]-(ny2+1)*Unit.NORMALSIZEY<(ny+1)*size[0]
				&& !(pnew instanceof Portail && old instanceof Portail && pnew == ((Portail)old).other))
			return false;
		float []decalage = new float[]{nx*size[0],pnew.size[1]-(ny+1)*size[1]};
		boolean boo=newDestination(new float[]{pnew.getPosition()[0]+decalage[0],pnew.getPosition()[1]+decalage[1]}, pnew);
		if(boo && old!=pnew){
			mother=pnew;
			pnew.units.add(this);
			pnew.unitsR.add(this);
			if(old!=null)
				old.unitsR.remove(this);
			recentPort=false;
		}
		return boo;
	}
	public void goToYourPlace(int place){
		int n=((int)mother.size[0])/((int)size[0]);
		int nx=place%n;
		int ny=place/n;
		float []decalage = new float[]{nx*size[0],mother.size[1]-(ny+1)*size[1]};
		newDestination(new float[]{mother.position[0]+decalage[0],mother.position[1]+decalage[1]}, mother);	
	}
	public void action(){
		if(ready){
		move();
		
		if(System.currentTimeMillis()-mineTimer>minePeriod){
			mine();
			mineTimer=System.currentTimeMillis();
		}
		}
		else if(System.currentTimeMillis()-readyTimer>creationTime[type])
			ready=true;
		
	}
	public void actionTurn(){
		//mine();
	}
	public void changeMining(int toMine,boolean res){
		if(res)
			Reseau.write(Reseau.CHANGE_MINING, ID, toMine+"", 0+"");
		mineTimer=System.currentTimeMillis();
		mining=toMine;
		color=Resource.colors[toMine];
	}
	public boolean mine(){
		if(mining==3)
			return false;
		if(position[0]!=destination[0] || position[1]!=destination[1])
			return false;
		if(resource[mining]>=full)
			return false;
		if(resource[mining]+miningSpeed>full){
			if(!mother.resources[mining].use(full-resource[mining]))
				return false;
			else{
				resource[mining]=full;
				return true;
			}
		}
		if(mother.resources[mining].quantite<miningSpeed){
			resource[mining]+=mother.resources[mining].quantite;
			if(!mother.resources[mining].use(mother.resources[mining].quantite))
				return false;
			return true;
		}
		
		if(!mother.resources[mining].use(miningSpeed))
			return false;
		resource[mining]+=miningSpeed;
		return true;
	}
	public Planet whereCanIGo(){
		for(Planet p : mother.reachable())
			if(haveResourcesForMe(p))
				return p;
		return null;
	}
	public boolean haveResourcesForMe(Planet p){
		for(int i=0;i<3;i++)
			if(p.resources[i].quantite>0 && resource[i]<full)
				return true;
		return false;
	}

}
