package gamePanels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Bonus extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private int width=150,height=150;
	private Image img=null;
	
	public Bonus(JButton b,JLabel lab){
		setBackground(Color.black);
		setLayout(new GridLayout(2,1,5,5));
		lab.setForeground(Color.white);
		add(b);
		add(lab);
		try{
			img = ImageIO.read(new File("Resources/bonus.png"));
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	public void paintComponent(Graphics g){
		g.drawImage(img, 0, 0, null);
		//super.paintComponent(g);
		
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
}
