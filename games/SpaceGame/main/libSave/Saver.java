package libSave;

import items.Item;
import items.Planet;
import items.Player;
import items.Resource;
import items.Unit;
import items.UnitR;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import menu.Game;

public class Saver {

	private PrintWriter pr=null;
	private File outFile=null;
	private Scanner sc;
	public static ArrayList<Map> maps=new ArrayList<Map>();

	public void save(ArrayList<Item> items)
	{
		try{
			outFile= new File("res/data.dat");
			pr= new PrintWriter(outFile);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		if(maps.size()==0)
			maps.add(new Map(new Player[2],null));
		maps.get(Game.mapID).items=items;
		Iterator<Map> iterM=maps.iterator();
		while(iterM.hasNext()){
			Map m=iterM.next();
			Iterator<Item> iter= m.items.iterator();
			pr.println("START "+m.players.length);
			while(iter.hasNext())
			{
				Item item= iter.next();
				if(item instanceof Planet){
					Planet p=(Planet)item;
					pr.println("Planet "+p.getPosition()[0]+" "+p.getPosition()[1]+" "+p.getSize()[0]+" "+p.getSize()[1]);
					pr.print("Resources");
					for(int i=0;i<Resource.NTYPE;i++){
						if(p.resources[i]==null)
							pr.print(" 0");
						else
							pr.print(" "+p.resources[i].quantite);
					}
					pr.print("\n");
					if(p.units.size()>0)
						pr.println("Player "+ p.units.get(0).owner);
					else
						pr.println("Player -1");
					pr.print("Units");
					int [] nType=new int[Unit.NTYPE+1];
					for(Unit unit: p.units){
						if(unit instanceof UnitR)
							nType[Unit.NTYPE]++;
						else
							nType[unit.type]++;
					}
					for(int i=0; i<Unit.NTYPE+1;i++)
						pr.print(" "+nType[i]);
					pr.print("\n");

				}


			}
			pr.println("END");
		}
		pr.close();

	}
	public void read()
	{
		try{
			sc=new Scanner(new FileReader("res/data.dat"));
		}
		catch(IOException e){
			System.err.println("erreur de lecture");
		}
		Map.generalID=0;
		maps=new ArrayList<Map>();
		ArrayList<Item> items=null;
		while(sc.hasNext()){
			String n=sc.next();
			if(n.equals("START")){
				items=new ArrayList<Item>();
				int nPlayers=Integer.parseInt(sc.next());
				//Player.nPlayers=nPlayers;
				Player.playerID=0;
				Game.players=new Player[nPlayers];
				for(int i=0;i<nPlayers;i++){
					Game.players[i]=new Player();
				}
			}
			else if(n.equals("END")){
				//System.out.println("add 1");
				maps.add(new Map(Game.players,items));
			}
			else if(n.equals("Planet")){
				float [] pos=new float[]{Float.parseFloat(sc.next()),Float.parseFloat(sc.next())};
				float [] size=new float[]{Float.parseFloat(sc.next()),Float.parseFloat(sc.next())};
				items.add(new Planet(pos,size));
				if(sc.next().equals("Resources")){
					for(int i=0;i<Resource.NTYPE;i++)
						((Planet)items.get(items.size()-1))
						.createResource(i,(int)Float.parseFloat(sc.next()));
				}
				if(sc.next().equals("Player")){
					Game.pTurn=Integer.parseInt(sc.next());
				}
				if(sc.next().equals("Units")){
					for(int i=0;i<Unit.NTYPE;i++){
						int num=Integer.parseInt(sc.next());
						for(int j=0;j<num;j++)
							((Planet)items.get(items.size()-1))
							.createFreeUnit(i,false);
					}
					int num=Integer.parseInt(sc.next());
					for(int j=0;j<num;j++)
						((Planet)items.get(items.size()-1))
						.createFreeUnitR(false);
				}
			}
		}
		//System.out.println(maps.size());
	}
}
