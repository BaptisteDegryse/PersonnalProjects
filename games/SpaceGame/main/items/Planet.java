package items;

import gamePanels.UpgradePanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import menu.Game;
import menu.GameBoard;
import menu.Util;
import reseau.Reseau;

public class Planet extends ItemParent{


	public ArrayList<Unit> units=new ArrayList<Unit>();//all
	public ArrayList<UnitR> unitsR=new ArrayList<UnitR>();//only unitR
	public ArrayList<Unit> unitsN=new ArrayList<Unit>();//others
	public Resource[] resources=new Resource[3];
	public Resource[] usableResources=new Resource[3];
	public int distanceMax[]=new int[]{300,400};
	//public boolean alife=true;
	public boolean fight=false;
	public boolean attacked=false;
	public static Image img[]=new Image[90];
	public int nImg=0;
	public long imgTimer=0;

	public Building[] buildings=new Building[2];
	public boolean accesBuild[]=new boolean[2];


	public Planet(float[] position, float[] size) {
		super(position, size);
		for(int i=0;i<resources.length;i++){
			resources[i]=new Resource(i,0,this);
		}
		for(int i=0;i<usableResources.length;i++){
			usableResources[i]=new Resource(i,0,this,true);
		}
		imgTimer=System.currentTimeMillis();

		for(int i=0;i<buildings.length;i++){
			buildings[i]=new Building(-1,this,i);
		}
		nImg=(int)(Math.random()*89);

		life=(int)size[0];
	}
	public static void init(){

		try{
			for(int i=0;i<90;i++){//<32

				if(i<10)
					img[i]=ImageIO.read(new File("res/planet1/000"+i+".png"));
				else
					img[i]=ImageIO.read(new File("res/planet1/00"+i+".png"));
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}


	}
	public void drawRange(Graphics g){
		if(UpgradePanel.upgrades[UpgradePanel.nRangeUp][Game.pTurn]){
			g.setColor(Color.green);
			g.drawOval((int)(position[0]+size[0]/2-distanceMax[1]),
					(int)(position[1]+size[1]/2-distanceMax[1]),2*distanceMax[1],2*distanceMax[1]);
		}
		else{
			g.setColor(Color.white);
			g.drawOval((int)(position[0]+size[0]/2-distanceMax[0]),
					(int)(position[1]+size[1]/2-distanceMax[0]),2*distanceMax[0],2*distanceMax[0]);
		}
	}
	public void drawLife(Graphics g){
		if(life==(int)size[0] || !alife)
			return;
		g.setColor(Color.green);
		int offset=10;
		int width=3;
		g.fillRect((int)position[0],(int)position[1]+(int)size[1]+offset,life,width);
		g.setColor(Color.red);
		g.fillRect((int)position[0]+life,(int)position[1]+(int)size[1]+offset,(int)(size[0]-life),width);
	}
	public void render(Graphics g){
		if(cursorOn){
			Util.drawCursorOn(this, (Graphics2D) g,false);
			drawRange(g);
		}
		if(!alife){
			g.setColor(Color.red);
			g.fillOval((int)position[0], (int)position[1], (int)size[0], (int)size[1]);
		}
		else
			Util.drawAndRotate(this, g);
		if(selected){
			if(fight)
				g.setColor(Color.red);
			else
				g.setColor(Color.white);
			g.drawOval((int)position[0], (int)position[1], (int)size[0], (int)size[1]);
			GameBoard.infoPlanet[1].setText("  "+Resource.resToString(usableResources[0],usableResources[1],usableResources[2]));
			drawRange(g);
			drawLife(g);
		}

		drawDeath(g);
		/*
		try{

			Iterator<Unit> iter=units.iterator();
			while(iter.hasNext()){
				iter.next().render(g);
			}
		}
		catch(ConcurrentModificationException e){
			e.printStackTrace();
		}
		 */
		for(int i=0;i<buildings.length;i++){
			buildings[i].render(g,accesBuild[i]);
		}
		for(int i=0;i<resources.length;i++){
			resources[i].render(g);
		}
		for(int i=0;i<usableResources.length;i++){
			usableResources[i].render(g);
		}
	}

	public void action(){
		if(!alife)
			attacked=false;
		for(int i=0;i<usableResources.length;i++){
			usableResources[i].setTo(0);
		}
		for(UnitR unitR : unitsR){
			for(int i=0;i<Resource.NTYPE;i++)
				usableResources[i].add(unitR.resource[i]);
		}
		if(alife)
			for(Building b : buildings){
				if(b.type>1 && b.type<5)
					usableResources[b.type-2].add(b.resource);
			}
		fight=!checkStopFighting();
		if(System.currentTimeMillis()-imgTimer>100){
			if(nImg<89)//31
				nImg++;
			else
				nImg=0;
			imgTimer=System.currentTimeMillis();
		}
		checkAccessBuid();
		for(Building b:buildings)
			b.action();


	}
	public void die(){
		alife=false;
		attacked=false;
		buildings[0].type=-2;
		buildings[1].type=-2;
		for(int i=0;i<2;i++){
			resources[i].setTo(0);
		}
		resources[2].setTo((int)(size[1]/Resource.height)/3);
		shouldDie=false;
	}
	public int createUnit(int unitType,boolean res){// 0..2= pas assez de sous ,-1= pas assez de place, -2=Ok, 3=fighting
		if(res)
			Reseau.write(Reseau.CREATE_U, ID, unitType+"", 0+"");
		if(!Game.players[Game.pTurn].isMyPlanet(this))
			return 4;
		if(fight)
			return 3;
		if(check(Unit.prix[unitType])!=-2)
			return check(Unit.prix[unitType]);
		if(!Unit.createYourSelf(this, unitType))
			return -1;
		pay(Unit.prix[unitType]);
		return -2;
	}
	public int createFreeUnit(int unitType,boolean res){// 0..2= pas assez de sous ,-1= pas assez de place, -2=Ok
		if(res)
			Reseau.write(Reseau.CREATEF_U, ID, ""+unitType, 0+"");
		if(!Unit.createYourSelf(this,unitType))
			return -2;
		return -1;
	}
	public int createUnitR(boolean res){// 0..2= pas assez de sous ,-1= pas assez de place, -2=Ok , 3=fighting
		if(res)
			Reseau.write(Reseau.CREATE_UR, ID, 0+"", 0+"");
		if(!Game.players[Game.pTurn].isMyPlanet(this))
			return 4;
		if(fight)
			return 3;
		if(check(UnitR.prix)!=-2)
			return check(UnitR.prix);
		if(!UnitR.createYourSelf(this,0))
			return -1;
		pay(UnitR.prix);
		return -2;

	}
	public int createFreeUnitR(boolean res){// 0..2= pas assez de sous ,-1= pas assez de place, -2=Ok
		if(res)
			Reseau.write(Reseau.CREATEF_UR, ID, 0+"", 0+"");
		if(!UnitR.createYourSelf(this,0))
			return -1;
		return -2;

	}

	public boolean sendUnit(Unit unit,Planet p,boolean res){
		if(fight && !(this instanceof Portail))
			return false;
		if(res)
			Reseau.write(Reseau.SEND, this.ID, unit.ID, p.ID);

		if(unit instanceof UnitR){
			return ((UnitR)unit).changePlanet(this, p);
		}
		else{
			return unit.changePlanet(this, p);
		}
	}
	public void cleanUnit(){

		for(int i=0;i<unitsR.size();i++){
			unitsR.get(i).goToYourPlace(i);
		}
		for(int i=0;i<unitsN.size();i++){
			unitsN.get(i).goToYourPlace(i);
		}

	}

	public void createResource(int type,int quantite){
		if(resources[type]==null)
			resources[type]=new Resource(type,quantite,this);
		else
			resources[type].add(quantite);
	}
	public boolean payOne(int type,int quantite){
		if(usableResources[type].quantite<quantite)
			return false;
		int collected=0;

		for(int i=0;i<unitsR.size() && collected<quantite;i++){
			if(collected==quantite){break;}
			else if(quantite-collected>unitsR.get(i).resource[type]){
				collected+=unitsR.get(i).resource[type];
				usableResources[type].use(unitsR.get(i).resource[type]);
				unitsR.get(i).resource[type]=0;
			}
			else{
				usableResources[type].use(quantite-collected);
				unitsR.get(i).resource[type]-=quantite-collected;
				collected=quantite;
			}
		}
		for(Building b: buildings){
			if(b.type-2==type)
				if(collected==quantite){break;}
				else if(quantite-collected>b.resource){
					collected+=b.resource;
					usableResources[type].use(b.resource);
					b.resource=0;
				}
				else{
					usableResources[type].use(quantite-collected);
					b.resource-=quantite-collected;
					collected=quantite;
				}
		}
		return true;
	}
	public boolean pay(int[] toPay){//tjs check avant !
		for(int i=0;i<Resource.NTYPE;i++){
			payOne(i,toPay[i]);
		}
		return true;
	}
	public int check(int[] toPay){
		for(int i=0;i<Resource.NTYPE;i++){
			if(usableResources[i].quantite<toPay[i])
				return i;
		}
		return -2;
	}
	public boolean checkStopFighting(){//true=stopFighting
		Player first=null;
		if(units.size()==0){
			fight=false;
			return true;
		}
		first=units.get(0).owner;
		for(Unit u : units){
			if(u.owner!=first)
				return false;
		}
		if(!attacked)
			cleanUnit();
		fight=false;
		return true;
	}
	public ArrayList<Planet> reachable(){
		ArrayList<Planet> ps=new ArrayList<Planet>();
		for(Item item :Game.items){
			if(item instanceof Planet){
				Planet p=(Planet) item;
				int bonus=0;
				if(UpgradePanel.upgrades[UpgradePanel.nRangeUp][Game.pTurn])
					bonus=1;
				if(p!=this 
						&& Util.isInCircle(p.getPosition()[0]+p.getSize()[0]/2, p.getPosition()[1]+p.getSize()[1]/2, Math.max(p.getSize()[0],p.getSize()[1])/2,
								position[0]+size[0]/2, position[1]+size[1]/2,distanceMax[bonus]))
					ps.add(p);
			}
		}
		return ps;
	}
	public void checkAccessBuid(){
		int n0=(int)resources[0].quantite;
		int n1=(int)resources[1].quantite;
		int n2=(int)resources[2].quantite;
		int max=Math.max(n0, Math.max(n1,n2));
		for(int i=0;i<buildings.length;i++)
			if(max*Resource.height<=i*Building.NORMALSIZEY)
				accesBuild[i]=true;

	}

}
