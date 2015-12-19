package items;

import gamePanels.SpellsPanel;
import gamePanels.UpgradePanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;

import menu.Game;
import menu.Util;
import reseau.Reseau;

public class Unit extends ItemParent {

	public final static int NORMAL=0;
	public final static float NORMALSIZEX=30f, NORMALSIZEY=30f;
	public final static int NTYPE=4; //		normal	battleship	rap	Boss		unitR
	public static int prix[][]=new int[][]{ {2,1,0},{5,5,2},{6,6,4},{20,20,20},{1,4,0}};// dernier=UnitR
	public float speed[]=new float[]{5f,2f,7f,2f,3f};// dernier=UnitR
	public float angleSpeed[]=new float[]{5f,5f,5f,2f,3f};//degrees // dernier=UnitR
	public int creationTime[]=new int[]{5000,10000,2000,30000,10000};//ms
	protected float [] destination= new float[2];//x,y
	protected float [] origin= new float[2];
	public boolean ready=false;
	public boolean recentPort=false;
	public long readyTimer=0;
	public Planet mother;
	public Player owner;
	

	private long bulletTimer=0;				//
	private int shootingSpeed[]=new int[]{1000,500,700,500};//ms
	private int shootingRange[]=new int[]{100,50,120,250};
	public int life[]=new int[]{3,10,9,25,5};//dernier=UnitR
	public static int lifeFull[][]=new int[][]{{3,10,9,25,5},{3,10,9,25,5},{3,10,9,25,5}};//dernier=UnitR
	public ArrayList<Bullet> bullets=new ArrayList<Bullet>();
	public int battlePlaceN=0;

	public float orientation=0;
	protected Color color;
	public int type=NTYPE;
	public static Image[][][] img=new Image[Player.nPlayers][NTYPE+1][32];

	public boolean moveSmart=false;


	public Unit(float[] position, float[] size) {
		super(position, size);
		readyTimer=System.currentTimeMillis();
	}
	public static void init(){
		try{
			for(int type=0;type<NTYPE+1;type++)
				for(int turn=0;turn<Player.nPlayers;turn++){
					for(int i=0;i<32;i++){
						if(i<10)
							img[turn][type][31-i]=ImageIO.read(new File("res/unit"+type+""+turn%2+"/spaceship000"+i+".png"));
						else// %2
							img[turn][type][31-i]=ImageIO.read(new File("res/unit"+type+""+turn%2+"/spaceship00"+i+".png"));

						if(turn==2){
							Toolkit toolkit= Toolkit.getDefaultToolkit();
							ImageFilter filter = new GrayFilter(true, 30);  
							ImageProducer producer = new FilteredImageSource(img[turn][type][31-i].getSource(), filter);  
							img[turn][type][31-i] = toolkit.createImage(producer); 
						}

					}
				}

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public Unit(int typeUnit, Planet mother, float[]decalage,Player owner){
		super(new float[]{mother.getPosition()[0]+decalage[0],mother.getPosition()[1]+decalage[1]},new float[]{NORMALSIZEX,NORMALSIZEY});
		this.mother=mother;
		if(typeUnit==NORMAL)
			color=Color.red;
		else if(typeUnit==1)
			color=Color.green;
		else if(typeUnit==2)
			color=Color.yellow;
		else if(typeUnit==3)
			color=Color.orange;

		this.type=typeUnit;
		/*try{
			img=ImageIO.read(new File("res/unit"+type+".png"));
		}
		catch(IOException e){
			e.printStackTrace();
		}*/
		destination[0]=position[0];
		destination[1]=position[1];
		this.owner=owner;
		readyTimer=System.currentTimeMillis();

		if(UpgradePanel.upgrades[UpgradePanel.nStrongerUnit][Game.pTurn]){
			life[typeUnit]+=UpgradePanel.stronger;
		}
	}
	public void render(Graphics g){
		if(!alife)
			return;

		g.setColor(color);
		if(cursorOn){
			Util.drawCursorOn(this, (Graphics2D) g,true);
			Util.drawLife(this, g);
		}
		Util.drawAndRotate(this, g);
		g.setColor(color);
		if(selected){
			g.drawRect((int)position[0], (int)position[1], (int)size[0], (int)size[1]);
			Util.drawLife(this, g);
		}
		else if(SpellsPanel.numSpell==SpellsPanel.nHeal)
			Util.drawLife(this, g);
		//g.setColor(owner.color);
		//g.drawLine((int)position[0], (int)position[1], (int)(position[0]+size[0]), (int)(position[1]+size[1]));

		if(moveSmart){
			g.setColor(Color.yellow);
			g.drawLine((int)position[0], (int)(position[1]+size[1]), (int)(position[0]+size[0]), (int)(position[1]+size[1]));
		}
		if(!Reseau.connected && !Reseau.fakeConnected)
			readyTimer=System.currentTimeMillis();
		if(!ready)
			Util.drawReady(this, (Graphics2D)g);
		drawDeath(g);

		Iterator<Bullet> iter=bullets.iterator();
		while(iter.hasNext()){
			iter.next().render(g);
		}
		g.setColor(color);
		g.drawString(type+"", (int)position[0]+5, (int)position[1]+20);
		//if(selected)
		//System.out.println("position : "+position[0]+" "+position[1]+" orientation : "+ orientation);

	}

	public boolean newDestination(float[] destination,Planet p){
		int bonus=0;
		if(UpgradePanel.upgrades[UpgradePanel.nRangeUp][Game.pTurn])
			bonus=1;
		if(Util.isInCircle(p.getPosition()[0]+p.getSize()[0]/2, p.getPosition()[1]+p.getSize()[1]/2, Math.max(p.getSize()[0],p.getSize()[1])/2,
				mother.position[0]+mother.size[0]/2, mother.position[1]+mother.size[1]/2, mother.distanceMax[bonus])
				|| (p instanceof Portail && mother instanceof Portail)){
			this.destination=destination;
			origin[0]=position[0];
			origin[1]=position[1];
			return true;
		}
		else{
			System.out.println("trop loin");
			return false;
		}
	}
	public static boolean createYourSelf(Planet mother,int type){
		Unit unit=new Unit(type,mother,new float[]{0f,0f},Game.players[Game.pTurn]);
		boolean place=unit.changePlanet(null, mother);
		unit.position[0]=unit.destination[0];
		unit.position[1]=unit.destination[1];
		return place;
	}

	public boolean changePlanet(Planet old,Planet pnew){
		//System.out.println("change");
		int n=((int)pnew.getSize()[0])/((int)size[0]);
		int nx=pnew.unitsN.size()%n;
		int ny=pnew.unitsN.size()/n;
		int n2=((int)pnew.getSize()[0])/((int)UnitR.NORMALSIZEX);
		int ny2=pnew.unitsR.size()/(n2+1);
		int maxLine=2;
		if(UpgradePanel.upgrades[UpgradePanel.nLinesUp][Game.pTurn])
			maxLine=3;
		if((ny>maxLine || pnew.size[1]-(ny2+1)*UnitR.NORMALSIZEY<(ny+1)*size[1]) && 
				(old==null ||(old.units.size()>1 && pnew.units.size()>1 && old.units.get(0).owner==pnew.units.get(0).owner))
				&& !(pnew instanceof Portail && old instanceof Portail && pnew == ((Portail)old).other) && !pnew.fight)
			return false;
		float []decalage = new float[]{nx*size[0],ny*size[1]};
		boolean boo=newDestination(new float[]{pnew.getPosition()[0]+decalage[0],pnew.getPosition()[1]+decalage[1]}, pnew);
		if(boo && old!=pnew){
			mother=pnew;
			if(old!=null)
				old.unitsN.remove(this);
			pnew.units.add(this);
			pnew.unitsN.add(this);
			recentPort=false;
		}
		return boo;
	}
	public void goToYourPlace(int place){
		int n=((int)mother.size[0])/((int)size[0]);
		int nx=place%n;
		int ny=place/n;
		float []decalage = new float[]{nx*Unit.NORMALSIZEX,ny*Unit.NORMALSIZEY};
		newDestination(new float[]{mother.position[0]+decalage[0],mother.position[1]+decalage[1]}, mother);	
	}
	public void battlePlace(){
		if(battlePlaceN>3)
			battlePlaceN-=4;
		int out=20;
		if(battlePlaceN==0)
			newDestination(new float[]{mother.position[0]-out,mother.position[1]-out},mother);
		else if(battlePlaceN==2)
			newDestination(new float[]{mother.position[0]+mother.size[0]+out,mother.position[1]-out},mother);
		else if(battlePlaceN==1)
			newDestination(new float[]{mother.position[0]+mother.size[0]+out,mother.position[1]+mother.size[1]+out},mother);
		else if(battlePlaceN==3)
			newDestination(new float[]{mother.position[0]-out,mother.position[1]+mother.size[1]+out},mother);
		battlePlaceN++;
	}

	public void turn(float degres){
		orientation+=degres;
	}
	public void turnTo(float degres){
		orientation=degres;
	}
	public void action(){
		if(ready){
			move();
			for(Bullet b : bullets){
				//System.out.print(bullets.size());
				b.move();
				b.action();
			}
			shoot();
		}
		else if(System.currentTimeMillis()-readyTimer>creationTime[type])
			ready=true;
	}
	public void shoot(){
		Item item=needsToShoot();
		if(item==null){
			mother.checkStopFighting();
			return;
		}
		if(System.currentTimeMillis()-bulletTimer>shootingSpeed[type]){
			bullets.add(new Bullet(this,(ItemParent)item));
			bulletTimer=System.currentTimeMillis();
		}
	}
	public Item needsToShoot(){
		for(Unit unit : mother.units){
			if(unit.owner != owner && shootingRange[type]>Util.getDistance(new float[]{position[0]+size[0]/2, position[1]+size[1]/2},new float[]{unit.position[0]+unit.size[0]/2,unit.position[1]+unit.size[1]/2})){
				return unit;
			}
		}
		if(mother.attacked)
			return mother;
		return null;
	}
	public void move(){
		
		if(orientation>360)
			orientation=orientation%360;
		if(orientation<0)
			orientation+=360;
		//if(selected)
		//	System.out.println("");
		
		

		if(Math.abs(destination[0]-position[0])<speed[type]/2+1 && Math.abs(destination[1]-position[1])<speed[type]/2+1){ 
			if(orientation>180)
				turn(angleSpeed[type]*0.5f);
			else if(orientation<3){orientation=0;}
			else
				turn(-angleSpeed[type]*0.5f);
			position[0]=destination[0];
			position[1]=destination[1];
			return;
		}

		float alpha=1f;
		float distanceA=Util.getDistance(position, destination);
		if((mother.fight || mother.attacked) && distanceA<80){
			battlePlace();
			//distanceA=Util.getDistance(position, destination);
		}
		distanceA=Util.getDistance(position, destination);
		
		
		float[]realVec=new float[]{destination[0]-position[0],destination[1]-position[1]};
		float length=(float)Math.sqrt(realVec[0]*realVec[0]+realVec[1]*realVec[1]);
		realVec[0]/=length;
		realVec[1]/=length;

		alpha=1;
		float angle;
		if(distanceA!=0)
			angle=(float)Math.toDegrees(Math.atan2(realVec[1], realVec[0]));
		else
			angle=0;
		if(angle<0)
			angle+=360;

		if(Math.abs(angle-orientation)<6)
			alpha=Math.abs(angle-orientation)/angleSpeed[type];

		if((angle-orientation<180 && angle>orientation) ||( orientation-angle>180 && angle<orientation)){
			turn(angleSpeed[type]*alpha);
			//System.out.println("turn reverse : angle="+angle+" orientation actuelle:"+orientation);
		}
		else{
			turn(-angleSpeed[type]*alpha);
			//System.out.println("turn normal : angle="+angle+" orientation actuelle:"+orientation);
		}
		boolean end=false;


		if(distanceA<60){
			alpha=1;//distanceA/30+0.2f;
			end=true;
		}
		//System.out.println("Stat DO="+distanceO+"  DA="+distanceA+"alpha="+alpha);

		float[]vec=new float[]{(float)Math.cos(Math.toRadians((double)orientation))*speed[type],
				(float)Math.sin(Math.toRadians((double)orientation))*speed[type]};
		alpha=0.3f;
		if(end){
			turnTo(angle);
			position[0]+=alpha*realVec[0]*speed[type];
			position[1]+=alpha*realVec[1]*speed[type];
		}
		else{
			position[0]+=alpha*vec[0];
			position[1]+=alpha*vec[1];
		}
		//System.out.println("vecteur: "+vec[0]+" "+vec[1]+"  RealVec : " +realVec[0]+" "+realVec[1]+ 
		//		" planete mre :"+mother.ID);

	}
	public Planet fightNearby(){
		for(Planet p: mother.reachable())
			if(p.fight)
				return p;

		return null;
	}
}
