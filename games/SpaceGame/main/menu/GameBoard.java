package menu;

import gamePanels.BButton;
import gamePanels.BoardPanel;
import gamePanels.Illustration;
import gamePanels.SpellsPanel;
import gamePanels.UpgradePanel;
import items.Building;
import items.Item;
import items.Planet;
import items.Player;
import items.Unit;
import items.UnitR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import libSave.Saver;
import reseau.Reseau;
import bot.CollectSmart;
import bot.MoveSmart;

public class GameBoard extends BoardPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	public static final int N=27,nCollectGold=2,nCollectUranium=3,nCollectCrystal=4,nLoad=6,nSave=7,
			nCreateUnitR=1,nCollectNothing=5,nNextTurn=8,nNewPlayer=9,nGold=10,nUranium=11,nCrystal=12,
			nCreateUnit1=13,nCreateUnit2=14,nCreateUnit3=15,
			nSmartCollector=16,nSmartMove=17,nDestroyPlanet=18;
	public static final int nUpgrade=19;
	public static final int nSpells=20;
	public static final int nBuild=21;
	public static final int nLab=22,nMagicTower=23,nGoldMine=24,nUraniumMine=25,nCrystalMine=26;

	public static BButton []buttons=new BButton[N];
	public static Illustration illustration;
	private JRadioButtonMenuItem createPlanet = new JRadioButtonMenuItem("Create Planets");
	private JRadioButtonMenuItem clear = new JRadioButtonMenuItem("clear");
	private JRadioButtonMenuItem none = new JRadioButtonMenuItem("None");
	private JCheckBox mirror=new JCheckBox("mirror");
	public static boolean mirrorBoo;
	private JPanel unitRPan=new JPanel();
	private JPanel unitPan=new JPanel();
	private JPanel planetPan=new JPanel();
	private JPanel resources=new JPanel();
	//private ItemPan planetUnitPan[]=new ItemPan[Unit.NTYPE+1];
	public static JPanel nothing=new JPanel();
	private JPanel editorPan=new JPanel();
	private JPanel helpEdPan=new JPanel();
	private UpgradePanel bonusPan=new UpgradePanel();
	private SpellsPanel spellsPan=new SpellsPanel();
	private JPanel buildingPan=new JPanel();
	public static int pan=-1;//-1=nothing,0=planet,1=unit,2=unit2,...,5=unitR

	private Saver saver=new Saver();
	public static JLabel infoPlanet[]=new JLabel[2];//info1=Ressources dispo, info2=SmartMove on/off
	public static int[] prix[]=new int[Unit.NTYPE+1+Building.NBuildings][3];
	public int radio=0;//0=none,1=moveSoldier,2=CP,-1=clear
	public static boolean editor=false;

	private Game game;

	public GameBoard(Game game) {
		this.game=game;
		setLayout(new BorderLayout());
		Dimension boardDimension=new Dimension(game.f.getWidth(),100);

		//setBackground(Color.black);
		this.setOpaque(false);
		//planetPan.setBackground(Color.black);
		planetPan.setOpaque(false);
		unitPan.setOpaque(false);
		buildingPan.setOpaque(false);
		unitRPan.setOpaque(false);
		nothing.setOpaque(false);
		editorPan.setOpaque(false);
		helpEdPan.setOpaque(false);

		editorPan.setLayout(new BorderLayout());

		ButtonGroup group = new ButtonGroup();
		group.add(createPlanet);
		group.add(clear);
		group.add(none);

		helpEdPan.add(createPlanet);
		helpEdPan.add(clear);
		helpEdPan.add(none);
		helpEdPan.add(buttons[nGold]=new BButton("add gold"));
		helpEdPan.add(buttons[nUranium]=new BButton("add uranium"));
		helpEdPan.add(buttons[nCrystal]=new BButton("add crystal"));
		helpEdPan.add(mirror);
		helpEdPan.add(buttons[nNewPlayer]=new BButton("Add Player"));//

		createPlanet.addActionListener(this);
		clear.addActionListener(this);
		none.addActionListener(this);
		mirror.addActionListener(this);

		Font font = new Font("Comic Sans MS",Font.BOLD,15);
		/*
		for(int i=0;i<Unit.NTYPE+1+Building.NBuildings;i++){//+1buiding
			prix[i]=new JLabel();
			prix[i].setForeground(Color.white);
			if(i<Unit.NTYPE+1)
				prix[i].setText(Resource.resToString(Unit.prix[i]));
			else
				prix[i].setText(Resource.resToString(Building.prix[i-Unit.NTYPE-1]));
			prix[i].setFont(font);
		}
		 */

		infoPlanet[0]=new JLabel("  --Usable resources-- ");
		infoPlanet[0].setAlignmentX(CENTER_ALIGNMENT);
		infoPlanet[1]=new JLabel("");

		resources.setBackground(Color.black);
		resources.setLayout(new GridLayout(2,1,5,5));
		for(int i=0;i<infoPlanet.length;i++){
			infoPlanet[i].setForeground(Color.white);
			infoPlanet[i].setFont(font);
			resources.add(infoPlanet[i]);
		}
		//planetPan.setLayout(new GridLayout(1,Unit.NTYPE+3,5,5));//+3 (1 pour planeteinfo,1 unitR, 1 espace)
		planetPan.add(resources);
		//planetPan.add(infoPlanet[1]);
		planetPan.add(buttons[nBuild]=new BButton("Build"));

		for(int i=0;i<Unit.NTYPE+1+Building.NBuildings;i++){//+1buiding
			if(i<Unit.NTYPE+1)
				prix[i]=Unit.prix[i];
			else
				prix[i]=Building.prix[i-Unit.NTYPE-1];
		}

		planetPan.add(buttons[Unit.NORMAL]=new BButton("Soldier",prix[0]));
		planetPan.add(buttons[nCreateUnitR]=new BButton("Collector",prix[4]));
		//planetPan.add(buttons[nCreateUnit1]=new BButton("BattleShip A22",prix[1]));
		buttons[nCreateUnit1]=new BButton("BattleShip A22",prix[1]);//
		//planetPan.add(buttons[nCreateUnit2]=new BButton("Rapido",prix[2]));
		buttons[nCreateUnit2]=new BButton("Rapido",prix[2]);
		//planetPan.add(buttons[nCreateUnit3]=new BButton("The Boss",prix[3]));
		buttons[nCreateUnit3]=new BButton("The Boss",prix[3]);//

		buildingPan.add(buttons[nLab]=new BButton("Lab",prix[5]));
		buildingPan.add(buttons[nMagicTower]=new BButton("Magic Tower",prix[6]));
		buildingPan.add(buttons[nGoldMine]=new BButton("Gold Mine",prix[7]));
		buildingPan.add(buttons[nUraniumMine]=new BButton("Uranium Mine",prix[8]));
		buildingPan.add(buttons[nCrystalMine]=new BButton("Crystal Mine",prix[9]));

		unitPan.add(buttons[nDestroyPlanet]=new BButton("Destroy Planet"));

		unitRPan.add(buttons[nCollectGold]=new BButton("Collect gold"));
		unitRPan.add(buttons[nCollectUranium]=new BButton("Collect uranium"));
		unitRPan.add(buttons[nCollectCrystal]=new BButton("Collect crystal"));
		unitRPan.add(buttons[nCollectNothing]=new BButton("Stop collecting"));
		buttons[nSmartCollector]=new BButton("Smart collecting");
		buttons[nSmartMove]=new BButton("Defensive Strategy");


		//nothing.add(buttons[nBonus]=new BButton("Upgrades"));
		buttons[nUpgrade]=new BButton("Upgrades");


		if(Reseau.serverBoo || editor)
			nothing.add(buttons[nNextTurn]=new BButton("Next Universe"));
		else
			buttons[nNextTurn]=new BButton("Next Universe");//

		if(editor){
			nothing.add(buttons[nLoad]=new BButton("Load"));
			nothing.add(buttons[nSave]=new BButton("Save"));
			nothing.add(buttons[nSpells]=new BButton("Spells"));
		}
		else{
			buttons[nLoad]=new BButton("Load");//
			buttons[nSave]=new BButton("Save");//
			buttons[nSpells]=new BButton("Spells");
		}

		editorPan.add(helpEdPan,BorderLayout.NORTH);
		editorPan.add(planetPan,BorderLayout.SOUTH);
		editorPan.add(unitRPan,BorderLayout.CENTER);
		editorPan.add(nothing,BorderLayout.EAST);

		if(!editor){
			planetPan.setPreferredSize(boardDimension);
			unitRPan.setPreferredSize(boardDimension);
			nothing.setPreferredSize(boardDimension);
			unitPan.setPreferredSize(boardDimension);
			bonusPan.setPreferredSize(boardDimension);
			spellsPan.setPreferredSize(boardDimension);			
			buildingPan.setPreferredSize(boardDimension);
		}

		if(editor)
			add(editorPan,BorderLayout.CENTER);
		else
			add(nothing,BorderLayout.CENTER);

		for(int i=0;i<N;i++)
			buttons[i].addActionListener(this);
		add(illustration=new Illustration(),BorderLayout.WEST);
		saver.read();
		Saver.maps.get(0).on();
	}
	private static int[] getInstanceSelected(){
		int[] nItems=new int[3];//0=planets,1=unit,2=unitR
		for(Item item : Game.items){

			if(item instanceof Planet){
				if(item.isSelected())
					nItems[0]++;
				for(Unit u : ((Planet)item).units){
					if(u.isSelected()){
						if(u instanceof UnitR)
							nItems[2]++;
						else
							nItems[1]++;
					}
				}
			}

		}
		return nItems;
	}
	public void removeAll(){
		this.remove(planetPan);
		this.remove(unitRPan);
		this.remove(nothing);
		this.remove(unitPan);
		this.remove(bonusPan);
		this.remove(spellsPan);
		this.remove(buildingPan);
	}
	public static void reloadUnits(){
		for(int i=0;i<Unit.NTYPE+1+Building.NBuildings;i++){//+1buiding
			if(i<Unit.NTYPE+1)
				prix[i]=Unit.prix[i];
			else
				prix[i]=Building.prix[i-Unit.NTYPE-1];
		}

		buttons[Unit.NORMAL].changePrice(prix[0]);
		buttons[nCreateUnitR].changePrice(prix[4]);
		buttons[nCreateUnit1].changePrice(prix[1]);
		buttons[nCreateUnit2].changePrice(prix[2]);
		buttons[nCreateUnit3].changePrice(prix[3]);
	}
	public void reload(){
		game.f.invalidate();
		game.f.validate();
		repaint();
	}
	public void adjustPanel(){
		if(!editor){
			int [] nItems= getInstanceSelected();
			removeAll();

			if(UpgradePanel.upgrades[UpgradePanel.nMoveStrategy][Game.pTurn]){
				UpgradePanel.upgrades[UpgradePanel.nMoveStrategy][Game.pTurn]=false;
				unitPan.add(buttons[nSmartMove]);
			}
			if(UpgradePanel.upgrades[UpgradePanel.nCollectStrategy][Game.pTurn]){
				UpgradePanel.upgrades[UpgradePanel.nCollectStrategy][Game.pTurn]=false;
				unitRPan.add(buttons[nSmartCollector]);
			}
			if(UpgradePanel.upgrades[UpgradePanel.nUnit1][Game.pTurn]){
				UpgradePanel.upgrades[UpgradePanel.nUnit1][Game.pTurn]=false;
				planetPan.add(buttons[nCreateUnit1]);
			}
			if(UpgradePanel.upgrades[UpgradePanel.nUnit2][Game.pTurn]){
				UpgradePanel.upgrades[UpgradePanel.nUnit2][Game.pTurn]=false;
				planetPan.add(buttons[nCreateUnit2]);
			}
			if(UpgradePanel.upgrades[UpgradePanel.nUnit3][Game.pTurn]){
				UpgradePanel.upgrades[UpgradePanel.nUnit3][Game.pTurn]=false;
				planetPan.add(buttons[nCreateUnit3]);
			}


			if(nItems[0]>0 && nItems[1]==0 && nItems[2]==0){
				add(planetPan,BorderLayout.CENTER);
				pan=0;
			}
			else if(nItems[2]>0 && nItems[1]>0){
				add(unitPan,BorderLayout.EAST);
				add(unitRPan,BorderLayout.CENTER);
				pan=1;
			}
			else if(nItems[2]>0){
				add(unitRPan,BorderLayout.CENTER);
				pan=5;
			}
			else if(nItems[1]>0){
				add(unitPan,BorderLayout.CENTER);
				pan=1;
			}
			else{
				Game.players[Game.pTurn].action();
				add(nothing,BorderLayout.CENTER);
				pan=-1;
			}
			//illustration.paintComponent(getGraphics());
			reload();
		}
	}
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if(s==buttons[Unit.NORMAL]){
			for(Item item : Game.items){
				if(item.isSelected() && item instanceof Planet){
					if(editor)
						((Planet)item).createFreeUnit(0,true);
					else
						((Planet)item).createUnit(0,true);
				}
			}
		}
		if(s==buttons[nCreateUnitR]){
			for(Item item : Game.items){
				if(item.isSelected() && item instanceof Planet){
					if(editor)
						((Planet)item).createFreeUnitR(true);
					else
						((Planet)item).createUnitR(true);
				}
			}
		}
		for(int i=nCreateUnit1;i<=nCreateUnit3;i++)
			if(s==buttons[i]){
				for(Planet p: Util.getSelectedPlanets()){

					if(editor)
						p.createFreeUnit(i-nCreateUnit1+1,true);
					else
						p.createUnit(i-nCreateUnit1+1,true);

				}
			}

		for(int i=nCollectGold;i<=nCollectNothing;i++){
			if(s==buttons[i])
				for(Item item : Game.items){
					if((item.isSelected() || true) && item instanceof Planet)
						for(UnitR unit : ((Planet)item).unitsR)
							if(unit.isSelected()){
								unit.changeMining(i-nCollectGold,true);
								CollectSmart.current.remove(unit);
							}
				}
		}
		if(s==buttons[nSmartCollector]){
			for(Item item : Game.items){
				if(item.isSelected() && item instanceof Planet)
					for(UnitR unit : ((Planet)item).unitsR)
						if(unit.isSelected())
							CollectSmart.current.add(unit);
			}

		}
		for(int i=nGold;i<=nCrystal;i++){
			if(s==buttons[i]){//gold
				for(Item item : Game.items){
					if(item.isSelected() && item instanceof Planet)
						((Planet)item).createResource(i-nGold, 40);
				}
			}
		}


		if(s==buttons[nNextTurn]){
			game.changePTurn(true);
		}

		if(s==buttons[nLoad]){
			saver.read();
			Saver.maps.get(0).on();
		}

		if(s==buttons[nSave]){
			saver.save(Game.items);
		}

		if(s==createPlanet){
			radio=2;
			createPlanet.setSelected(true);

		}
		else if(s==clear){
			radio=-1;
			clear.setSelected(true);
		}
		else{
			radio=0;
			none.setSelected(true);
		}
		if(s==mirror)
			mirrorBoo=mirror.isSelected();
		if(s==buttons[nNewPlayer]){
			Game.players=new Player[Player.nPlayers+1];
			Player.playerID=0;
			for(int i=0;i<Player.nPlayers+1;i++){
				Game.players[i]=new Player();
			}
			Player.nPlayers++;
		}
		if(s==buttons[nSmartMove]){
			boolean on=false;
			boolean first=true;
			for(Unit u: Util.getSelectedUnits()){
				if(first){
					if(u.moveSmart)
						on=true;
					first=false;
				}

				if(!on)
					MoveSmart.current.add(u);
				else
					MoveSmart.current.remove(u);

			}
		}
		if(s==buttons[nDestroyPlanet]){
			if(Util.getSelectedUnits().size()==0)
				return;
			Planet p=Util.getSelectedUnits().get(0).mother;
			p.attacked=!p.attacked;
			if(p.attacked){
				for(Unit u: p.units)
					u.battlePlace();
			}
			else {
				p.cleanUnit();
			}
		}
		if(s==buttons[nUpgrade]){
			removeAll();
			add(bonusPan);
			reload();
		}
		if(s==buttons[nSpells]){
			removeAll();
			add(spellsPan);
			reload();
		}
		if(s==buttons[nBuild]){
			removeAll();
			add(buildingPan);
			reload();
		}
		for(int i=0;i<Building.NBuildings;i++)
			if(s==buttons[nLab+i]){
				if((i>1 || !UpgradePanel.upgrades[UpgradePanel.nLab+i][Game.pTurn]) &&
						Util.canPay(Game.players[Game.pTurn].getMyResources(),Building.prix[i])){
					ArrayList<Planet> p=Util.getSelectedPlanets();
					if(p.size()==1)
						for(int j=0;j<2;j++)
							if(p.get(0).accesBuild[j] && p.get(0).buildings[j].type==-1 ){
								p.get(0).buildings[j].isBought(i,true);
								break;
							}
				}
			}

	}
}
