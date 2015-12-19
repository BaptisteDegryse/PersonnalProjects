package items;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import menu.Util;

public class Portail extends Planet{
	public Portail other;

	public Portail(float[] position, float[] size) {
		super(position, size);
	}
	public void associate(Portail other){
		other.other=this;
		this.other=other;
	}
	public void action(){
		Iterator<Unit> iter=units.iterator();
		while(iter.hasNext()){
			Unit u=iter.next();
			if(Util.isInCircle(u,this) && !u.recentPort){
				u.position[0]=other.position[0]+other.size[0]/2;
				u.position[1]=other.position[1]+other.size[1]/2;
				sendUnit(u,other,false);
				u.recentPort=true;
				iter.remove();
				other.selected=true;
				selected=false;
			}
		}
		fight=true;
	}
	public void render(Graphics g){
		g.setColor(Color.blue);
		g.fillOval((int)position[0], (int)position[1], (int)size[0], (int)size[1]);

		try{
			Iterator<Unit> iter=units.iterator();
			while(iter.hasNext()){
				iter.next().render(g);
			}
		}
		catch(ConcurrentModificationException e){
			e.printStackTrace();
		}
		if(selected || cursorOn){
			drawRange(g);
			if(other!=null){
				g.setColor(Color.blue);
				g.drawLine((int)(position[0]+size[0]/2), (int)(position[1]+size[1]/2), (int)(other.position[0]+other.size[0]/2), (int)(other.position[1]+other.size[1]/2));
			}
		}
	}
	public boolean checkStopFighting(){//true=stopFighting
		return false;
	}

}
