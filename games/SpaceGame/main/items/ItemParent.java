package items;

import interfaces.Animation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import menu.Game;
import menu.Util;

public class ItemParent implements Item{
	protected float[] position=new float[2];
	protected float[] size= new float[2];
	public Image img=null;
	protected boolean selected=false,cursorOn=false;
	public boolean alife=true;
	public static int generalID[]=new int[5];
	public String ID;
	private Animation dead=new Animation(Animation.nExplosion);
	public int life;
	public boolean dying=false;
	public boolean shouldDie=false;

	public ItemParent(float[] position, float[] size){
		this.position[0]=position[0];
		this.position[1]=position[1];
		this.size[0]=size[0];
		this.size[1]=size[1];
		char p=(char) ('a'+Game.pTurn);
		if(!(this instanceof Bullet)){
			if(this instanceof Planet || this instanceof Resource || this instanceof Building){
				ID=generalID[4]+"";
				generalID[4]++;
			}
			else{
				ID=p+""+generalID[Game.pTurn];
				generalID[Game.pTurn]++;
			}
			/*
			System.out.print(ID+"  ");
			if(this instanceof UnitR)
				System.out.print("UnitR");
			else if(this instanceof Unit)
				System.out.print("Unit");
			System.out.println(" joueur: "+Game.pTurn +"  char:"+p);
			*/
		}
	}

	public void render(Graphics g){
		g.setColor(Color.red);
		if(cursorOn){
			Util.drawCursorOn(this, (Graphics2D) g,true);
		}
		if(selected)
			g.fillRect((int)position[0], (int)position[1], (int)size[0], (int)size[1]);
		else
			g.drawRect((int)position[0], (int)position[1], (int)size[0], (int)size[1]);
	}
	public void drawDeath(Graphics g){
		if(!dead.done){
			dead.draw(g, (int)position[0], (int)position[1], (int)size[0], (int)size[1]);
		}
		else if(shouldDie){
			dead.start();
			die();
			dying=true;
		}
		else if(dying && dead.done){
			alife=false;
			dying=false;
		}
	}
	public void die(){
		shouldDie=false;
	}
	@Override
	public float[] getPosition() {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public float[] getSize() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public void setSelect(boolean b) {
		selected=b;
	}
	public void setCursorOn(boolean b){
		cursorOn=b;
	}
	public void changeSelect(){
		selected=!selected;
	}
	public boolean isSelected(){
		if(!alife)
			return false;
		return selected;
	}
	public boolean equals(Object o){
		if(! (o instanceof Item))
			return false;
		return ID.equals(((Item)o).getID());
	}
	public String getID(){
		return ID;
	}
	public void action(){
		
	}

}
