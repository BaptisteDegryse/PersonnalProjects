package gamePanels;

import items.Player;
import items.Unit;
import items.UnitR;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import menu.Game;
import menu.GameBoard;
import menu.Util;
import reseau.Reseau;

public class UpgradePanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;

	public static final int N=10,nSpeedCollect=0,nMoveStrategy=1,nCollectStrategy=2,
			nUnit1=3,nRangeUp=4,nLinesUp=5,nLowCost=6,nStrongerUnit=7,nUnit2=8,nUnit3=9,
			nLab=10,nSpells=11;
	public static boolean upgrades[][]=new boolean[N+2][Player.nPlayers];
	private static BButton buttons[]=new BButton[N];
	private static int prix[][]=new int [N][3];
	//private static JLabel prixLab[]=new JLabel[N];
	private static String title[]=new String[N];
	//private static Bonus bonus[]=new Bonus[N];
	public static int speedCollectPeriod=1000,stronger=2;

	public UpgradePanel(){
		//setLayout(new GridLayout(1,N,5,5));
		//setBackground(Color.black);
		setOpaque(false);
		
		title[0]="Collect Faster"; 		prix[0]=new int[]{10,10,10};
		title[1]="Defense Strategy";	prix[1]=new int[]{10,10,10};
		title[2]="Collect Strategy";	prix[2]=new int[]{20,20,20};
		title[3]="Unblock SpaceShip";	prix[3]=new int[]{10,10,10};
		title[4]="Upgrade Range";		prix[4]=new int[]{20,20,20};
		title[5]="Planet Capacity";		prix[5]=new int[]{20,20,20};
		title[6]="Low cost";			prix[6]=new int[]{20,20,20};
		title[7]="Stronger Unit";		prix[7]=new int[]{20,20,20};
		title[8]="Unblock Rapido";		prix[8]=new int[]{20,20,20};
		title[9]="Unblock TheBoss";		prix[9]=new int[]{40,40,40};

		for(int i=0;i<buttons.length;i++){
			//bonus[i]=new Bonus(buttons[i]=new BButton(title[i]),
				//	prixLab[i]=new JLabel(Resource.resToString(prix[i])));
			buttons[i]=new BButton(title[i],prix[i]);//
			buttons[i].addActionListener(this);
			//add(bonus[i]);
			add(buttons[i]);
		}


	}
	public static void speedCollect(){
		for(Unit u : Game.players[Game.pTurn].getMyUnits())
			if(u instanceof UnitR)
				((UnitR)u).minePeriod-=speedCollectPeriod;
	}
	public static void strongerUnit(){
		for(Unit u : Game.players[Game.pTurn].getMyUnits())
			u.life[u.type]+=stronger;
		for(int i=0;i<Unit.lifeFull.length;i++)
			Unit.lifeFull[Game.pTurn][i]+=stronger;
	}
	public static void lowCost(){
		for(int k=0;k<Unit.NTYPE+1;k++)
			for(int j=0;j<3;j++)
				if(Unit.prix[k][j]>0)
					Unit.prix[k][j]--;
		GameBoard.reloadUnits();

	}
	public static void generalBonus(int i,boolean res){
		Game.players[Game.pTurn].pay(prix[i]);
		upgrades[i][Game.pTurn]=true;
		if(i==nSpeedCollect){
			speedCollect();
		}
		else if(i==nStrongerUnit)
			strongerUnit();
		else if(i==nLowCost && res){
			lowCost();
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		for(int i=0;i<buttons.length;i++)
			if(s==buttons[i]){
				if(Util.canPay(Game.players[Game.pTurn].getMyResources(),prix[i])){
					Reseau.write(Reseau.BONUS, i, 0, 0);
					generalBonus(i,true);
					//bonus[i].remove(buttons[i]);
					//prixLab[i].setText(title[i]+" Upgraded");
					buttons[i].upgrade();

				}

			}
	}
}
