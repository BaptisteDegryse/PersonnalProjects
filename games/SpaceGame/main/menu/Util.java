package menu;
import items.Building;
import items.Item;
import items.Planet;
import items.Unit;
import items.UnitR;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;

public class Util {
	
	public static void wait(int ms){
		long t= System.currentTimeMillis();
		while(System.currentTimeMillis()-t<ms){Thread.yield();}
	}
	public static boolean isIn(MouseEvent event,Item item){
		int x,x2,y,y2,X,X2,Y,Y2;
		x=x2=getMouseX(event);
		y=y2=getMouseY(event);
		X=(int)item.getPosition()[0];
		Y=(int)item.getPosition()[1];
		X2=X+(int)item.getSize()[0];
		Y2=Y+(int)item.getSize()[1];

		if(((X<x && x<X2) || (X<x2 && x2<X2) || (x<X && X<x2) || (x<X2 && X2<x2))
				&& ((Y<y && y<Y2) || (Y<y2 && y2<Y2) || (y<Y && Y<y2) || (y<Y2 && Y2<y2)))
			return true;
		return false;
	}
	public static int getMouseX(MouseEvent e){
		return (int)((e.getX()-Game.translate[0])/Game.scale);
	}
	public static int getMouseY(MouseEvent e){
		return (int)((e.getY()-Game.translate[1])/Game.scale);
	}
	public static boolean isInCircle(Item i,Item i2){
		return isInCircle(i.getPosition()[0]+i.getSize()[0]/2,i.getPosition()[1]+i.getSize()[1]/2,Math.max(i.getSize()[0]/2,i.getSize()[1]/2),
				i2.getPosition()[0]+i2.getSize()[0]/2,i2.getPosition()[1]+i2.getSize()[1]/2,Math.max(i2.getSize()[0]/2,i2.getSize()[1]/2));
	}
	public static boolean isIn(int[] select,Item item){
		int x,x2,y,y2,X,X2,Y,Y2;
		x=select[0];
		x2=x+select[2];
		y=select[1];
		y2=y+select[3];
		X=(int)item.getPosition()[0];
		Y=(int)item.getPosition()[1];
		X2=X+(int)item.getSize()[0];
		Y2=Y+(int)item.getSize()[1];

		if(((X<x && x<X2) || (X<x2 && x2<X2) || (x<X && X<x2) || (x<X2 && X2<x2))
				&& ((Y<y && y<Y2) || (Y<y2 && y2<Y2) || (y<Y && Y<y2) || (y<Y2 && Y2<y2)))
			return true;
		return false;
	}
	public static boolean isInCircle(float cx,float cy,float r,float Cx,float Cy,float R){
		return (cx-Cx)*(cx-Cx)+(cy-Cy)*(cy-Cy) < (r+R)*(r+R);
	}
	public static AlphaComposite makeComposite(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return(AlphaComposite.getInstance(type, alpha));
	}
	public static void drawCursorOn(Item item, Graphics2D g2d, boolean rect){
		//Graphics2D g2d= (Graphics2D) g;
		g2d.setComposite(makeComposite(0.3f));
		g2d.setColor(Color.white);
		float[]position=item.getPosition();
		float[]size=item.getSize();
		
		if(rect)
			g2d.fillRect((int)position[0], (int)position[1], (int)size[0], (int)size[1]);
		else
			g2d.fillOval((int)position[0], (int)position[1], (int)size[0], (int)size[1]);
		g2d.setComposite(makeComposite(1.0f));
	}
	public synchronized static float getDistance(float[] a, float[]b){
		return (float)Math.sqrt((double)(Math.abs(b[0]-a[0])*Math.abs(b[0]-a[0])+
				Math.abs(b[1]-a[1])*Math.abs(b[1]-a[1])));
	}
	
	public static void drawAndRotate(Item i, Graphics g)
	{
		AffineTransform identity = new AffineTransform();

		Graphics2D g2d = (Graphics2D)g;
		AffineTransform trans = new AffineTransform();
		trans.setTransform(identity);
		trans.translate(i.getPosition()[0],i.getPosition()[1]);
		if(i instanceof UnitR)
			trans.scale(UnitR.NORMALSIZEX/100f,UnitR.NORMALSIZEY/100f);
		else if(i instanceof Unit)
			trans.scale(Unit.NORMALSIZEX/100f,Unit.NORMALSIZEY/100f);
		else{
			//trans.translate(-(i.getSize()[0]*1.5),-(i.getSize()[1]*1.6));
			trans.scale(i.getSize()[0]/100f,i.getSize()[1]/100f);
		}
		
		if(i instanceof Unit)
			g2d.drawImage(Unit.img[((Unit)i).owner.num][((Unit)i).type][(int)(((Unit)i).orientation/360f*31f)], trans, null);
		else
			g2d.drawImage(Planet.img[((Planet)i).nImg], trans, null);
	}

	public static void drawReady(Unit u,Graphics2D g2d){
		g2d.setComposite(makeComposite(0.6f));
		g2d.setColor(Color.black);
		int largeur=(int)(u.getSize()[0]-(u.getSize()[0]*(System.currentTimeMillis()-u.readyTimer)/(u.creationTime[u.type])));
		g2d.fillRect((int)u.getPosition()[0],(int)u.getPosition()[1],largeur,(int)u.getSize()[1]);
		g2d.setComposite(makeComposite(1.0f));
	}
	public static void drawLife(Unit u,Graphics g){
		g.setColor(Color.green);
		int offset=5;
		int width=3;
		int largeur=(int)(u.getSize()[0]-(u.getSize()[0]*(Unit.lifeFull[Game.pTurn][u.type]-u.life[u.type])/(Unit.lifeFull[Game.pTurn][u.type])));
		g.fillRect((int)u.getPosition()[0],(int)u.getPosition()[1]-offset,largeur,width);
		g.setColor(Color.red);
		g.fillRect((int)u.getPosition()[0]+largeur,(int)u.getPosition()[1]-offset,(int)(u.getSize()[0]-largeur),width);
	}
	
	public synchronized static Item find(String ID){
		for (Item item : Game.items) {
			//System.out.println(item.getID()+"  ID  "+ID);
			if(item.getID().equals(ID))
				return item;
			Iterator<Unit> iter=((Planet)item).units.iterator();
			while(iter.hasNext()){
				Unit unit=iter.next();
				if(unit.ID.equals(ID))
					return unit;
			}
			for(Building b : ((Planet)item).buildings){
				if(b.ID.equals(ID))
					return b;
			}
		}
		//System.out.println("fail "+ID);
		return null;
	}
	public synchronized static ArrayList<Planet> getSelectedPlanets(){
		ArrayList<Planet> ps=new ArrayList<Planet>();
		for (Item item : Game.items) 
			if(item.isSelected() && item instanceof Planet)
				ps.add((Planet)item);
		return ps;
	}
	public synchronized static ArrayList<Unit> getSelectedUnits(){
		ArrayList<Unit> us=new ArrayList<Unit>();
		for(Planet p: getSelectedPlanets()){
			for(Unit u : p.units)
				if(u.isSelected())
					us.add(u);
		}
		return us;
	}
	public synchronized static ArrayList<Unit> getAllUnits(){
		ArrayList<Unit> us=new ArrayList<Unit>();
		for (Item item : Game.items) 
			if(item instanceof Planet)
				for(Unit u :((Planet)item).units)
					us.add(u);
		return us;
	}
	public synchronized static ArrayList<Planet> getAllPlanets(){
		ArrayList<Planet> ps=new ArrayList<Planet>();
		for (Item item : Game.items) 
			if(item instanceof Planet)
					ps.add((Planet)item);
		return ps;
	}
	
	public static boolean canPay(int[]res,int[]toPay){
		return (res[0]>=toPay[0] && res[1]>=toPay[1] && res[2]>=toPay[2]);
	}
}
