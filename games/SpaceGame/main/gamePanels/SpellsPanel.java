package gamePanels;

import items.Item;
import items.Planet;
import items.Portail;
import items.Unit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JPanel;

import menu.Game;
import menu.Util;
import reseau.Reseau;

public class SpellsPanel  extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;

	private static final int N=5;
	public static int nHeal=0,nLittleShock=1,nMediumShock=2,nHugeShock=3,nPortail=4;
	private static BButton buttons[]=new BButton[N];
	private static int prix[][]=new int [N][3];
	private static String title[]=new String[N];
	private static int spellRange[]=new int[N];
	public static int numSpell=-1;
	private static Portail p=null;

	public SpellsPanel(){
		//setLayout(new GridLayout(1,N,5,5));
		//setBackground(Color.black);
		setOpaque(false);

		title[0]="Heal"; 				prix[0]=new int[]{20,20,0};		spellRange[0]=50;
		title[1]="Small shock"; 		prix[1]=new int[]{10,10,10};	spellRange[1]=50;
		title[2]="Medium shock"; 		prix[2]=new int[]{20,20,20};	spellRange[2]=100;
		title[3]="Huge implosion"; 		prix[3]=new int[]{30,30,30};	spellRange[3]=150;
		title[4]="Portail"; 			prix[4]=new int[]{5,10,20};		spellRange[4]=50;

		for(int i=0;i<buttons.length;i++){
			//spells[i]=new Bonus(buttons[i]=new JButton(title[i]),
				//	prixLab[i]=new JLabel(Resource.resToString(prix[i])));
			buttons[i]=new BButton(title[i],prix[i]);
			buttons[i].addActionListener(this);
			add(buttons[i]);
		}
	}
	public static void executeSpell(int i,int[] mousePosition, boolean res){
		numSpell=i;
		executeSpell(mousePosition,res);
	}
	public static void executeSpell(int[] mousePosition, boolean res){
		if(res)
			Reseau.write(Reseau.SPELL, numSpell+"", mousePosition[0]+"", mousePosition[1]+"");
		Game.players[Game.pTurn].pay(prix[numSpell]);
		if(numSpell!=nPortail){
			Iterator<Item> iter=Game.items.iterator();
			while(iter.hasNext()){
				Item i=iter.next();
				if(i instanceof Planet){
					for(Unit u : ((Planet)i).units){
						if(Util.isInCircle(mousePosition[0], mousePosition[1], spellRange[numSpell],
								u.getPosition()[0]+u.getSize()[0]/2, u.getPosition()[1]+u.getSize()[1]/2, u.getSize()[0]/2))
						{
							if(numSpell==nHeal){
								u.life[u.type]=Unit.lifeFull[Game.pTurn][u.type];
							}
							else if(numSpell==nLittleShock){
								u.life[u.type]--;
							}
							else if(numSpell==nMediumShock){
								u.life[u.type]-=2;
							}
							else if(numSpell==nHugeShock){
								u.life[u.type]-=3;
							}
							else if(numSpell==nPortail){

							}
							if(u.life[u.type]<=0)
								u.shouldDie=true;
						}
					}
				}
			}
		}
		else{
			if(numSpell==nPortail && p==null){
				Game.items.add(p=new Portail(new float[]{mousePosition[0]-spellRange[nPortail],mousePosition[1]-spellRange[nPortail]},
						new float[]{2*spellRange[nPortail],2*spellRange[nPortail]}));
			}
			else if(numSpell==nPortail){
				Portail p2;
				Game.items.add(p2=new Portail(new float[]{mousePosition[0]-spellRange[nPortail],mousePosition[1]-spellRange[nPortail]},
						new float[]{2*spellRange[nPortail],2*spellRange[nPortail]}));
				p.associate(p2);
				p=null;
			}
		}

		if(p==null || !res)
			numSpell=-1;
	}
	public static void drawSpells(Graphics g){
		g.setColor(Color.blue);
		g.drawOval(Game.mousePosition[0]-spellRange[numSpell], Game.mousePosition[1]-spellRange[numSpell], 
				2*spellRange[numSpell], 2*spellRange[numSpell]);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object s=e.getSource();
		for(int i=0;i<buttons.length;i++)
			if(s==buttons[i]){
				if(Util.canPay(Game.players[Game.pTurn].getMyResources(),prix[i])){
					numSpell=i;
				}
			}
	}

}
