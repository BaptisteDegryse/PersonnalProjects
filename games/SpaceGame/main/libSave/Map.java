package libSave;

import items.Item;
import items.Player;

import java.util.ArrayList;

import menu.Game;

public class Map {
	public ArrayList<Item> items=new ArrayList<Item>();
	public Player players[];
	public int ID;
	public static int generalID=0;

	public Map(Player players[],ArrayList<Item> items){
		this.players=players;
		this.items=items;
		ID=generalID;
		generalID++;
	}
	public void on(){
		Player.nPlayers=players.length;
		Game.items=items;
		Game.pTurn=0;
		Game.players=players;
		Game.mapID=ID;
		/*
		Game.players=new Player[Player.nPlayers];
		for(int i=0;i<Player.nPlayers;i++){
			players[i]=new Player();
		}
		*/
	}
}
