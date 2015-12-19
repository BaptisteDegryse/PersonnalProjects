package reseau;

import gamePanels.SpellsPanel;
import gamePanels.UpgradePanel;
import items.Building;
import items.Item;
import items.Planet;
import items.Unit;
import items.UnitR;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;

import menu.Game;
import menu.Util;


public class Reseau {
	public static final int SEND=0,CREATEF_U=1,CREATEF_UR=2,CREATE_U=3,CREATE_UR=4,
			CHANGE_MINING=5,KILL=6,CHANGE_UNIVERSE=7,BONUS=8,SPELL=9,BUILDBOUGHT=10;
	public static boolean serverBoo=false;
	private static Server serverClient=null;
	public static boolean connected=false,fakeConnected=false;
	public static String ipAddress="";
	public static String ourIpAddress;

	public static void startReseau() throws Exception{
		ourIpAddress=InetAddress.getLocalHost().getHostAddress();
		if(serverBoo){
			serverClient=new Server(1025);
			serverClient.start();
		}
		else
		{
			Socket s;
			if(!ipAddress.equals(""))
				s=new Socket(ipAddress,1025);
			else
				s=new Socket(InetAddress.getLocalHost(),1025);
			serverClient=new Server(s);
			serverClient.start();
		}
	}
	public static void write(int fct,int a,int b,int c){
		write(fct,a+"",b+"",c+"");
	}
	public static void write(int fct,String arg1,String arg2, String arg3){
		if(connected){
			try{
				serverClient.writer.write(fct+" "+arg1+" "+arg2+" "+arg3+"\n");
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public static void read(){

		try {
			serverClient.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			killAll();
		}

		String line;
		String str[];
		int fct;

		if(serverBoo)
			Game.pTurn=1;
		else
			Game.pTurn=0;

		Iterator<String> iter=serverClient.toRead.iterator();
		serverClient.mutex.lock();
		while(iter.hasNext()){
			line=iter.next();
			iter.remove();
			//System.out.println(line);
			str=line.split(" ");
			fct=Integer.parseInt(str[0]);

			Item i=Util.find(str[1]);

			//if(i==null){System.out.println("erreur, find -> null");}
			if(fct==SEND){
				Unit u=((Unit)Util.find(str[2]));
				Planet p=((Planet)i);
				if(u!=null &&
						p.sendUnit(u, (Planet)Util.find(str[3]),false))
					p.units.remove(u);
			}
			else if(fct==CREATEF_U)
				((Planet)i).createFreeUnit(Integer.parseInt(str[2]), false);
			else if(fct==CREATEF_UR)
				((Planet)i).createFreeUnitR(false);
			else if(fct==CREATE_U)
				((Planet)i).createUnit(Integer.parseInt(str[2]), false);
			else if(fct==CREATE_UR)
				((Planet)i).createUnitR(false);
			else if(fct==CHANGE_MINING){
				UnitR u=((UnitR)i);
				if(u!=null)
					u.changeMining(Integer.parseInt(str[2]),false);
			}
			else if(fct==KILL){
				Unit u=((Unit)i);
				if(u!=null)
					u.alife=false;
			}
			else if(fct==CHANGE_UNIVERSE)
				Game.current.changePTurn(false);
			else if(fct==BONUS)
				UpgradePanel.generalBonus(Integer.parseInt(str[1]),false);
			else if(fct==SPELL)
				SpellsPanel.executeSpell(Integer.parseInt(str[1]),new int []{Integer.parseInt(str[2]),Integer.parseInt(str[3])},false);
			else if(fct==BUILDBOUGHT)
				((Building)i).isBought(Integer.parseInt(str[2]),false);
		}
		serverClient.mutex.unlock();
		if(serverBoo)
			Game.pTurn=0;
		else
			Game.pTurn=1;
	}
	public static void killAll(){
		serverClient.kill();
	}
}
