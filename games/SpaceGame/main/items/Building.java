package items;

import gamePanels.UpgradePanel;

import java.awt.Color;
import java.awt.Graphics;

import menu.Game;
import menu.GameBoard;
import reseau.Reseau;

public class Building extends ItemParent{
	public static final int NORMALSIZEX=30,NORMALSIZEY=30;
	public static int NBuildings=5;
	public int type=-1;// -2= dead planet
	public static int[][]prix=new int[][]{{10,10,10},{15,15,15},{20,10,10},{10,20,10},{10,10,20}};
	public Planet mother;
	private long timer=0;
	public int resource=0;

	public Building(float[] position, float[] size) {
		super(position, size);
	}
	public Building(int type,Planet mother,int place){
		super(new float[]{mother.getPosition()[0]+mother.getSize()[0]
				,mother.getPosition()[1]+mother.getSize()[1]-(place+1)*(NORMALSIZEY+2)+2}
				,new float[]{NORMALSIZEX,NORMALSIZEY});
		this.type=type;
		this.mother=mother;
	}
	public void render(Graphics g,boolean usable){
		if(type==-2)
			return;
		g.setColor(Color.gray);
		if(!usable)
			g.drawRect((int)position[0],(int)position[1],(int)size[0],(int)size[1]);
		else if(type==0){
			g.fillRect((int)position[0],(int)position[1],(int)size[0],(int)size[1]);
			g.setColor(Color.red);
			g.fillOval((int)position[0]+1,(int)position[1]+1,(int)size[0]-2,(int)size[1]-2);
		}
		else if(type==1){
			g.fillRect((int)position[0],(int)position[1],(int)size[0],(int)size[1]);
			g.setColor(Color.orange);
			g.fillOval((int)position[0]+1,(int)position[1]+1,(int)size[0]-2,(int)size[1]-2);
		}
		else if(type>1 && type<5){
			g.fillRect((int)position[0],(int)position[1],(int)size[0],(int)size[1]);
			g.setColor(Resource.colors[type-2]);
			g.fillRect((int)position[0]+1,(int)position[1]+1,(int)size[0]-2,(int)size[1]-2);
		}
		else 
			g.fillRect((int)position[0],(int)position[1],(int)size[0],(int)size[1]);
		
		
	}
	public void isBought(int type, boolean res){
		if(this.type==-2)
			return;
		if(res)
			Reseau.write(Reseau.BUILDBOUGHT, this.ID, type+"", 0+"");
		this.type=type;
		Game.players[Game.pTurn].pay(Building.prix[type]);
		if(type==0){
			if(res)
				GameBoard.nothing.add(GameBoard.buttons[GameBoard.nUpgrade]);
			UpgradePanel.upgrades[UpgradePanel.nLab][Game.pTurn]=true;
		}
		if(type==1){
			if(res)
				GameBoard.nothing.add(GameBoard.buttons[GameBoard.nSpells]);
			UpgradePanel.upgrades[UpgradePanel.nSpells][Game.pTurn]=true;
		}
	}
	public void action(){
		if(type>1 && type<5){
			if(System.currentTimeMillis()-timer>3000){
				resource+=1;
				timer=System.currentTimeMillis();
			}
		}
	}
	
}
