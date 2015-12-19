package gamePanels;

import items.Planet;
import items.Unit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import menu.Game;
import menu.GameBoard;

public class Illustration extends Component {

	private static final long serialVersionUID = 1L;
	private int width=150,height=150;
	private long timer=1000;
	
	public Illustration(){
		super();
	}
	public Dimension getPreferredSize(){
		return new Dimension(width,height);
	}
	public Dimension getMinimumSize(){
		return new Dimension(width,height);
	}
	public Dimension getMaximumSize(){
		return new Dimension(width,height);
	}
	public void paint(Graphics g){
		g.setColor(Color.white);
		g.drawRoundRect(1, 1, width-2, height-2, 10, 10);
		Graphics2D g2=(Graphics2D)g;
		g2.scale(1.5, 1.5);
		if(GameBoard.pan==-1)
			return;
		else if(GameBoard.pan>0 && GameBoard.pan<6)
			g2.drawImage(Unit.img[Game.pTurn][GameBoard.pan-1]
					[(int) ((System.currentTimeMillis()%( (Unit.img[Game.pTurn][GameBoard.pan-1].length) *timer ))/timer)],
					0, 0, null);
		else if(GameBoard.pan==0)
			g2.drawImage(Planet.img[(int) ((System.currentTimeMillis()%(Planet.img.length*timer))/timer)], 0, 0, null);
	}
}
