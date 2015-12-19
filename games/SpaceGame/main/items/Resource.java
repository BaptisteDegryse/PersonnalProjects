package items;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import menu.Util;

public class Resource extends ItemParent{

	public static final int GOLD=0,URANIUM=1,CRYSTAL=2;
	public final static int NTYPE=3;
	public static final Color [] colors=new Color[]{new Color(220,220,170),new Color(60,120,140),new Color(150,80,90),Color.black};
	public int type;
	public Color color;
	public static float width=5f;
	public static float height=0.5f;
	public int quantite;
	
	
	public Resource(float[] position, float[] size) {
		super(position, size);	
	}
	public Resource(int type,int quantite,Planet mother){
		super(new float[]{mother.getPosition()[0]+(width+2)*type+mother.getSize()[0]
				,mother.getPosition()[1]+mother.getSize()[1]}
				,new float[]{width,((float)quantite)*height});
		this.quantite=quantite;
		color=colors[type];
	}
	public Resource(int type,int quantite,Planet mother,boolean dipo){//inutile, pour differencier les constructs
		super(new float[]{mother.getPosition()[0]+(width+2)*type-3*(width+2)
						,mother.getPosition()[1]+mother.getSize()[1]}
				,new float[]{width,((float)quantite)*height});
		this.quantite=quantite;
		color=colors[type];
	}
	public void render(Graphics g){
		size[1]=quantite*height;
		g.setColor(color);
		if(cursorOn){
			Util.drawCursorOn(this, (Graphics2D) g,true);
		}
		g.fillRect((int)(position[0]), (int)position[1]-(int)size[1],
				(int)width,(int)(size[1]));// (int)(((float)quantite)*height));
		
	}
	public boolean use(int quantite){
		if(this.quantite>=quantite){
			this.quantite-=quantite;
			//System.out.println("q :"+this.quantite);
			//position[1]+=height*quantite;
			return true;
		}
		return false;
	}
	public void add(int quantite){
		this.quantite+=quantite;
		//position[1]-=quantite*height;
	}
	public void setTo(int quantite){
		//position[1]=position[1]+(this.quantite-quantite)*height;
		this.quantite=quantite;
	}
	public static String resToString(int g,int u,int c){
		return g+" g "+u+" u "+c+" c";
	}
	public static String resToString(Resource gold,Resource uranium,Resource crystal){
		return resToString(gold.quantite,uranium.quantite,crystal.quantite);
	}
	public static String resToString(int[]res){
		return resToString(res[0],res[1],res[2]);
	}
	/*
	public static JLabel makeJLabel(int[]res){
		for(int i=0;i<3;i++)
	}
	*/
	public static void drawRes(Graphics g,int[]price, int x,int y){
		for(int i=0;i<3;i++){
			g.setColor(Resource.colors[i]);
			g.drawString(price[i]+"", x+20*i, y);
		}
	}

}
