package gamePanels;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class BoardPanel extends JPanel{

	private static final long serialVersionUID = 1L;

private static Image img[]=new Image[8];
	
	public static void init(){
		
		try{
			for(int i=0;i<4;i++)
				img[i] = ImageIO.read(new File("res/Board/side"+i+".png"));
			for(int i=0;i<4;i++)
				img[4+i] = ImageIO.read(new File("res/Board/corner"+i+".png"));
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	public void paint(Graphics g){
		Graphics2D g2D=(Graphics2D)g;
		GradientPaint paint = new GradientPaint(0,0, Color.gray, getWidth(), getHeight(), Color.LIGHT_GRAY);
		g2D.setPaint(paint);
		g2D.fillRect(0, 0, getWidth(), getHeight());
		int t=50;//size
		for(int i=0;i<getWidth();i+=50){
			g.drawImage(img[0], i, 0, null);
			g.drawImage(img[2], i, getHeight()-t, null);
		}
		for(int i=0;i<getHeight();i+=50){
			g.drawImage(img[3], 0, i, null);
			g.drawImage(img[1], getWidth()-t, i, null);
		}
		g.drawImage(img[4], getWidth()-t, 0, null);//corners
		g.drawImage(img[5], getWidth()-t, getHeight()-t, null);
		g.drawImage(img[6], 0, getHeight()-t, null);
		g.drawImage(img[7], 0, 0, null);
		super.paint(g);
	}
}
