package menu;
import gamePanels.SpellsPanel;
import items.Bullet;
import items.Item;
import items.Planet;
import items.Player;
import items.Resource;
import items.Unit;
import items.UnitR;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import libSave.Map;
import libSave.Saver;
import reseau.Reseau;
import bot.CollectSmart;
import bot.MoveSmart;


public class Game extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 3000; 
	private static final int HEIGHT = 3000; 
	private boolean fini=false;
	private int[]selection=new int[4];
	private int[]click=new int[2];
	private GameBoard gameBoard;
	public static Game current;
	public static int mapID=0;
	public static int changeMap=0;
	public static ArrayList<Item> items= new ArrayList<Item>();
	public JFrame f;
	private JPanel container=new JPanel();
	//private Thread thread=null;
	public static Player players[];
	public static int pTurn=0;
	public boolean nextTurn=true;
	public boolean keys[] = new boolean[256];
	public boolean mouseTranslate[]=new boolean[4];//g,b,dr,h
	private JPanel infoPan=new JPanel();
	private JLabel infos[]=new JLabel[3];// 0=players's name,1=ipAddress,2=resourcesPlayer
	private AffineTransform af=new AffineTransform();
	public static float[]translate=new float[]{0,0};
	public static float[]decalage=new float[]{0,0};
	public static int[] mousePosition=new int[2];
	public static float scaleDecalage=0;
	public static float scale=1;
	private long tic=0;


	public Game(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		current=this;
		f = new JFrame("__--SpaceGame--__");

		//f.setSize(1000,800);//WIDTH, HEIGHT);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		gameBoard= new GameBoard(this);

		Font font = new Font("Comic Sans MS",Font.BOLD,15);
		for(int i=0;i<infos.length;i++){
			infos[i]=new JLabel();
			infos[i].setAlignmentX(CENTER_ALIGNMENT);
			infos[i].setFont(font);
			infoPan.add(infos[i]);
		}
		infoPan.setBackground(Color.white);

		container.setLayout(new BorderLayout());
		container.add(this,BorderLayout.CENTER);
		container.add(gameBoard,BorderLayout.SOUTH);
		container.add(infoPan,BorderLayout.NORTH);
		f.getContentPane().add(container);
		MouseL mkl=new MouseL();
		this.addMouseListener(mkl);
		this.addMouseMotionListener(mkl);
		this.addKeyListener(mkl);
		f.addMouseWheelListener(mkl);
		container.addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseDragged(MouseEvent arg0) {}
			@Override
			public void mouseMoved(MouseEvent arg0) {
				adaptMouseTranslate(arg0, false);				
			}
		});
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		f.setVisible(true);
		try {
			Reseau.startReseau();
		} catch (Exception e) {
			Reseau.connected=false;
			System.out.println("erreur de connexion");
		}
		infos[1].setText("   IPAddress: "+Reseau.ourIpAddress);

		(new Thread(this)).start();
		//run();
	}

	public void run(){
		
		createBufferStrategy(2);

		if(Reseau.serverBoo)
			pTurn=0;
		else
			pTurn=1;

		if(Player.nPlayers>0){
			infos[0].setForeground(players[pTurn].color);
			infos[0].setText(players[pTurn].name);
		}
		
		BufferStrategy bs = getBufferStrategy();
		Image img=null;
		try{
			img = ImageIO.read(new File("res/background.jpg"));
		}
		catch(IOException e){
			e.printStackTrace();
		}
		//players=new Player[Player.nPlayers];
		//for(int i=0;i<Player.nPlayers;i++){
		//	players[i]=new Player();
		//}

		while(!fini){
			tic=System.currentTimeMillis();

			af.setToIdentity();
			af.translate(current.getWidth()/2,current.getHeight()/2);
			af.scale(scale,scale);
			af.translate(-current.getWidth()/2, -current.getHeight()/2);
			af.translate(decalage[0],decalage[1]);
			translate[0]=(float)af.getTranslateX();
			translate[1]=(float)af.getTranslateY();
			try{
				moveAll();
				CollectSmart.current.action();
				MoveSmart.current.action();
			} catch(ConcurrentModificationException e){
				e.printStackTrace();
				System.out.println("attrapŽ");
			}
			try{
				if(Reseau.connected)
					Reseau.read();
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println("bug avec le reseau");
			}
			cleanDeads();
			adaptTranslate();
			Graphics g = bs.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0, WIDTH, HEIGHT);
			((Graphics2D) g).setTransform(af);
			if(img!=null)
				g.drawImage(img, 0, 0,WIDTH,HEIGHT,null);//getWidth(),getHeight(),null);

			this.render(g);
			if(GameBoard.editor && GameBoard.mirrorBoo){
				g.setColor(Color.yellow);
				g.drawLine(WIDTH/2,0,WIDTH/2,HEIGHT);
			}
			if(SpellsPanel.numSpell!=-1)
				SpellsPanel.drawSpells(g);
			displayInfo();
			GameBoard.illustration.repaint();
			checkEnd(g);
			g.dispose();
			bs.show();
			//System.out.println("Time :"+(30-(System.currentTimeMillis()-tic)));
			Util.wait((int)(30-(System.currentTimeMillis()-tic)));
		}
		Reseau.killAll();
	}
	private void displayInfo(){
		infos[2].setText(" --  Player's resources: "+ Resource.resToString(players[pTurn].getMyResources()));
	}
	private void checkEnd(Graphics g){
		int looser=-1;
		for(Player p : players)
			if(p.getMyUnits().size()==0)
				looser=p.num;
		for(Item i: items)
			if(((Planet)i).fight)
				looser=-1;
		if(looser!=-1){
			try{
				Image endImg;
				if(Game.pTurn==looser){
					endImg=ImageIO.read(new File("res/looser.png"));
				}
				else{
					endImg=ImageIO.read(new File("res/winner.png"));
				}
				g.drawImage(endImg,-(int)(translate[0]+getWidth()/2-scale*endImg.getWidth(null)/2),-(int)(translate[1]+getHeight()/2-scale*endImg.getHeight(null)/2),null);
			}
			catch(IOException e){}
		}
	}
	private void moveAll(){
		if(changeMap!=mapID){
			Saver.maps.get(changeMap).on();
			changeMap=mapID;
		}
		Iterator<Item> iter=items.iterator();
		boolean change=false;
		if(nextTurn){
			change=true;
			if(Player.nPlayers>0)
				infos[0].setText(players[pTurn].name);
			nextTurn=false;
		}

		while(iter.hasNext()){
			Item i=iter.next();
			if(i instanceof Planet){
				((Planet)i).action();
				for(Unit unit : ((Planet)i).units){
					unit.action();

					if(change && unit instanceof UnitR && unit.owner==players[pTurn]){
						((UnitR)unit).actionTurn();
					}
				}
			}
		}
	}
	private void cleanDeads(){
		Iterator<Item> iter=items.iterator();
		while(iter.hasNext()){
			Planet p=(Planet)iter.next();
			Iterator<Unit> iter2=p.units.iterator();
			while(iter2.hasNext()){
				Unit u=iter2.next();
				Iterator<Bullet> iter3=u.bullets.iterator();
				while(iter3.hasNext()){
					Bullet b=iter3.next();
					if(!b.alife){
						iter3.remove();
					}
				}
				if(!u.alife && u.bullets.size()==0){
					iter2.remove();
					if(u instanceof UnitR)
						p.unitsR.remove(u);
					else
						p.unitsN.remove(u);
				}
			}
			//if(!p.alife)
			//	iter.remove();
		}
	}
	private void render(Graphics g){
		boolean done=false;
		do{
			try{
				Iterator<Item> iter=items.iterator();
				while(iter.hasNext()){
					iter.next().render(g);
				}
				for(Unit u:Util.getAllUnits())
					u.render(g);
				done=true;
			}
			catch(ConcurrentModificationException e){
				e.printStackTrace();
			}
		}while(!done);

		if(gameBoard.radio==2){
			g.setColor(Color.white);
			g.drawString("planet", getWidth()/2, 30);
		}

		if(selection[0]!=0){
			Graphics2D g2d=(Graphics2D)g;
			g2d.setComposite(Util.makeComposite(0.3f));
			g2d.setColor(Color.white);

			if(gameBoard.radio==2){
				g2d.fillOval(selection[0],selection[1],selection[2],selection[3]);
				if(GameBoard.mirrorBoo)
					g2d.fillOval(WIDTH-selection[0]-selection[2],selection[1],selection[2],selection[3]);
			}
			else
				g2d.fillRect(selection[0],selection[1],selection[2],selection[3]);

		}

	}
	class MouseL implements MouseListener,MouseMotionListener,KeyListener,MouseWheelListener
	{

		public void moveSoldier(MouseEvent arg0){
			Planet p=null;
			for (Item item : items) {
				if(Util.isIn(arg0, item) && item instanceof Planet){
					p=(Planet)item;
				}
			}

			for (Item item : items) {
				if(item instanceof Planet){
					Iterator<Unit> iter=((Planet)item).units.iterator();

					while(iter.hasNext()){
						Unit unit=iter.next();
						if(unit.isSelected() && p!=null){
							if(p!=(Planet)item && ((Planet)item).sendUnit(unit, p,true)){
								iter.remove();
							}
						}
					}
					((Planet)item).cleanUnit();
				}
			}
			if(p!=null)
				p.setSelect(true);
		}
		public void selectItems(MouseEvent arg0,boolean move){
			for (Item item : items) {//selection
				if(Util.isIn(arg0, item)){
					item.setSelect(true);
				}
				else if(!keys[16]){//maj
					item.setSelect(false);
				}

				if(item instanceof Planet){
					for(Unit unit : ((Planet)item).units){
						if(Util.isIn(arg0, unit) && unit.owner==players[pTurn]){
							unit.setSelect(true);
							unit.mother.setSelect(true);
						}
						else if(!keys[16] && !move)
							unit.setSelect(false);
					}

				}


			}
			gameBoard.adjustPanel();
		}
		public void selectItemsBySquare(MouseEvent arg0){
			for (Item item : items) {
				item.setCursorOn(false);
				if(Util.isIn(selection, item)){
					item.setSelect(true);
				}
				else if(!keys[16]){
					item.setSelect(false);
				}
				if(item instanceof Planet){
					for(Unit unit : ((Planet)item).units){
						if(Util.isIn(selection, unit) && unit.owner==players[pTurn]){
							unit.setSelect(true);
							unit.mother.setSelect(true);
						}
						else if(!keys[16])
							unit.setSelect(false);
					}
				}	
				/*
					if(item instanceof Planet){
						for(Unit unit : ((Planet)item).units){
							unit.setSelect(false);
						}
					}
				}*/
				// TODO Auto-generated method stub
			}
			gameBoard.adjustPanel();
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {
			boolean move=false;
			if(keys[65] || SwingUtilities.isRightMouseButton(arg0)){// 'a'
				moveSoldier(arg0);
				move=true;
			}
			else if(SpellsPanel.numSpell!=-1){
				SpellsPanel.executeSpell(mousePosition, true);
			}
			else if(gameBoard.radio==-1){
				Iterator<Item> iter=items.iterator();

				while(iter.hasNext()){
					Item i=iter.next();
					if(Util.isIn(arg0, i) ){
						iter.remove();
					}
				}
			}
			selectItems(arg0,move);
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {

		}

		@Override
		public void mouseExited(MouseEvent arg0) {

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			click[0]=Util.getMouseX(arg0);
			click[1]=Util.getMouseY(arg0);
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			if(keys[65] || SwingUtilities.isRightMouseButton(arg0)){}
			else if(gameBoard.radio==2){
				items.add(new Planet(new float[]{selection[0],selection[1]},new float[]{selection[2],selection[3]}));
				if(GameBoard.mirrorBoo)
					items.add(new Planet(new float[]{WIDTH-selection[0]-selection[2],selection[1]},new float[]{selection[2],selection[3]}));
			}
			else{
				selectItemsBySquare(arg0);
			}
			selection=new int[4];
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int x=Util.getMouseX(e);
			int y=Util.getMouseY(e);
			selection = new int[] {Math.min(click[0],x), Math.min(click[1],y),
					Math.abs(click[0]-x), Math.abs(click[1]-y)};
			for (Item item : items) {

				if(Util.isIn(selection, item))
					item.setCursorOn(true);
				else
					item.setCursorOn(false);
				for(Unit u : ((Planet)item).units)
					if(Util.isIn(selection, u))
						u.setCursorOn(true);
					else
						u.setCursorOn(false);
			}
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			// TODO Auto-generated method stub
			for (Item item : items) {
				if(Util.isIn(arg0, item))
					item.setCursorOn(true);
				else
					item.setCursorOn(false);
				for(Unit u : ((Planet)item).units)
					if(Util.isIn(arg0, u))
						u.setCursorOn(true);
					else
						u.setCursorOn(false);
			}
			adaptMouseTranslate(arg0,true);	
			mousePosition[0]=Util.getMouseX(arg0);
			mousePosition[1]=Util.getMouseY(arg0);
		}
		public void keyPressed(KeyEvent e) {
			int k = e.getKeyCode();
			if(k < keys.length){
				keys[k] = true;
				//int i='a';
				//System.out.println("touche "+e.getKeyCode());
			}

			if(k ==79 && Game.mapID>0)// o
				Game.changeMap=Game.mapID-1;
			else if(k==80 && Game.mapID<Saver.maps.size()-1){//p
				Game.changeMap=Game.mapID+1;
				//System.out.println("check");
			}
			else if(k== 80 && GameBoard.editor){//p
				//System.out.println("new map "+Saver.maps.size());
				Player p[]=new Player[2];
				Player.playerID=0;
				p[0]=new Player();
				p[1]=new Player();
				Saver.maps.add(new Map(p,new ArrayList<Item>()));
				Game.changeMap=Game.mapID+1;
			}
			else if(k==83){
				for(Unit u : Util.getSelectedUnits())
					System.out.println(u.ID);
			}
			else if(k==73)// i
				Reseau.fakeConnected=true;
			//System.out.print("new map --");
			//System.out.println("touche "+k);
		}

		public void keyReleased(KeyEvent e) {
			int k = e.getKeyCode();
			if(k < keys.length)
				keys[k] = false;
		}

		public void keyTyped(KeyEvent e) {}

		@Override
		public void mouseWheelMoved(MouseWheelEvent arg0) {
			float rot=(float)arg0.getWheelRotation();
			if(WIDTH*(scale+rot/10)>current.getWidth() && HEIGHT*(scale+rot/10)>current.getHeight()){
				scale+=rot/10f;
				if(rot>0)
					adaptZoomOut();
			}
		}

	}
	public void adaptZoomOut(){
		int speed=40;
		if(!(translate[0]<0))//gauche
			decalage[0]-=speed;
		if(!( translate[1]>-(scale*HEIGHT-current.getHeight()))){//bas
			decalage[1]+=speed;			//System.out.println(translate[1]);
		}
		if(!( translate[0]>-(scale*WIDTH-current.getWidth())))//droite
			decalage[0]+=speed;
		if(!( translate[1]<0))//haut
			decalage[1]-=speed;
	}
	public void adaptTranslate(){
		int speed;
		try{
			if((int)scale>0)
				speed=20/(int)scale;
			else
				speed=20;
		}
		catch(ArithmeticException e){
			speed=20;
		}
		//float decalage[]=new float[]{-(scale-1)*WIDTH/2,-(scale-1)*HEIGHT/2};

		if((keys[37] || mouseTranslate[0]) && translate[0]<0)//gauche
			decalage[0]+=speed;
		if((keys[40] || mouseTranslate[1]) && translate[1]>-(scale*HEIGHT-current.getHeight()))//bas
			decalage[1]-=speed;

		if((keys[39] || mouseTranslate[2]) && translate[0]>-(scale*WIDTH-current.getWidth()))//droite
			decalage[0]-=speed;
		if((keys[38] || mouseTranslate[3]) && translate[1]<0)//haut
			decalage[1]+=speed;
		//System.out.println(translate[0] +"   "+translate[1]);
		//adaptZoomOut();
	}
	
	public void adaptMouseTranslate(MouseEvent e, boolean fen){
		int x=20;
		for(int i=0;i<4;i++)
			mouseTranslate[i]=false;
		if(e.getX()<x)//gauche
			mouseTranslate[0]=true;
		if(!fen && e.getY()>container.getHeight()-x)//bas
			mouseTranslate[1]=true;		
		if(e.getX()>container.getWidth()-x )//droite
			mouseTranslate[2]=true;
		if(!fen && e.getY()<x )//haut
			mouseTranslate[3]=true;
		//System.out.println(e.getX() +"   "+(f.getWidth()-50));
	}
	public void changePTurn(boolean res){
		if(!Reseau.connected){
			pTurn++;
			if(pTurn==Player.nPlayers)
				pTurn=0;
			nextTurn=true;
			//System.out.println(pTurn);
		}
		else{
			if(res)
				Reseau.write(Reseau.CHANGE_UNIVERSE, 0, 0, 0);
			if(Game.mapID<Saver.maps.size()-1){//p
				Game.changeMap=Game.mapID+1;
			}
			else
				Game.changeMap=0;
		}
	}

}
