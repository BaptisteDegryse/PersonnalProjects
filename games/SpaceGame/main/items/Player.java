package items;

import java.awt.Color;
import java.util.ArrayList;

import menu.Game;
import menu.GameBoard;

public class Player {

	public static int nPlayers=3;
	public static int playerID=0;
	public Color color;
	public String name;
	public int num;
	public boolean lab=false, spells=false;
	//public ArrayList<Item> items=new ArrayList<Item>();
	
	public Player(){
		name="Player "+(playerID+1);
		num=playerID;
		if(num==0)
			color=Color.blue;
		else if(num==1)
			color=Color.red;
		else if(num==2)
			color=Color.green;
		else if(num==3)
			color=Color.orange;
		playerID++;
	}
	public String toString(){
		return ""+num;
	}
	public boolean isMyPlanet(Item item){
		return (item instanceof Planet && ((Planet)item).units.size()>0 && ((Planet)item).units.get(0).owner==this);
	}
	public ArrayList<Planet> getMyPlanets(){
		ArrayList<Planet> ps=new ArrayList<Planet>();
		for(Item i: Game.items)
			if( i instanceof Planet && isMyPlanet((Planet)i) )
				ps.add((Planet)i);
		
		return ps;
	}
	public int[] getMyResources(){
		int res[]=new int[3];
		for(Item i:Game.items)
			if(isMyPlanet(i))
				for(int j=0;j<3;j++)
					res[j]+=((Planet)i).usableResources[j].quantite;
		
		return res;
	}
	public ArrayList<Unit> getMyUnits(){
		ArrayList<Unit> us=new ArrayList<Unit>();
		for(Item item : Game.items)
			if(isMyPlanet(item)){
				for(Unit u : ((Planet)item).units)
					us.add(u);
			}
		return us;
	}
	public void action(){
		boolean lab2=false, spells2=false;
		for(Planet p : getMyPlanets())
			for(int i=0;i<2;i++)
				if(p.buildings[i].type==0)
					lab2=true;
				else if(p.buildings[i].type==1)
					spells2=true;
		if(lab2==lab && spells2==spells)
			return;
		if(lab2 && !lab)
			GameBoard.nothing.add(GameBoard.buttons[GameBoard.nUpgrade]);
		if(!lab2 && lab)
			GameBoard.nothing.remove(GameBoard.buttons[GameBoard.nUpgrade]);
		if(spells2 && !spells)
			GameBoard.nothing.add(GameBoard.buttons[GameBoard.nSpells]);
		if(!spells2 && spells)
			GameBoard.nothing.remove(GameBoard.buttons[GameBoard.nSpells]);
		
		lab=lab2;
		spells=spells2;
	}
	public boolean pay(int[]toPay){
		int []res=getMyResources();
		for(int i=0;i<3;i++)
			if(res[i]<toPay[i])
				return false;
		for(Item i:Game.items)
			if(isMyPlanet(i)){
				Planet p=(Planet)i;
				for(int j=0;j<3;j++){
					if(toPay[0]==0 && toPay[1]==0 && toPay[2]==0)
						return true;
					else if(toPay[j]==0 || p.usableResources[j].quantite==0){}
					else if(p.usableResources[j].quantite>=toPay[j]){
						p.payOne(j, toPay[j]);
						toPay[j]=0;
					}
					else if(p.usableResources[j].quantite<toPay[j]){
						toPay[j]-=p.usableResources[j].quantite;
						p.payOne(j, p.usableResources[j].quantite);
					}
				}
			}
		return false;
	}
}
