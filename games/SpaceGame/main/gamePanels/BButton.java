package gamePanels;

import items.Resource;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JButton;

public class BButton extends JButton implements MouseListener {

	private static final long serialVersionUID = 1L;

	private boolean in=false;
	private String name="";
	private String[]  names=null;
	Image img=null;
	private int width=100,height=70;
	public int [] price=null;
	public boolean upgraded=false,first=true;
	private static Image done=null;

	public BButton(String name){
		super();
		enableInputMethods(true);   
		addMouseListener(this);
		//setBackground(Color.black);
		try{
			if(first){
				first=false;
				done= ImageIO.read(new File("res/Buttons/done.png"));
			}
			img = ImageIO.read(new File("res/Buttons/"+name.replaceAll("\\s","")+".png"));
		}
		catch(Exception e){
			//e.printStackTrace();
		}
		this.name=name;
		names=name.split("\\s");
	}
	public BButton(String name,int[] price){
		this(name);
		this.price=new int[]{price[0],price[1],price[2]};
		height=70;
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
	public void paintComponent(Graphics g){
		//super.paintComponent(g);
		if(img!=null)
			g.drawImage(img, 0, 0, null);
		else{
			g.setColor(Color.black);
			g.fillRect(0, 0, width, height-20);
			in=true;
		}
		if(upgraded)
			g.drawImage(done, 0, 0, null);
		if(in && names.length==1){
			g.setColor(Color.lightGray);
			g.fillRect(0,15,100,20);
			g.setColor(Color.black);
			g.drawString(name,5,30);//getX(),getY()-20);
			//System.out.println("paint in :D");
		}
		else if(in && names.length==2){
			g.setColor(Color.lightGray);
			g.fillRect(0,10,100,30);
			g.setColor(Color.black);
			g.drawString(names[0],5,22);//getX(),getY()-20);
			g.drawString(names[1],5,38);
			//System.out.println("paint in :D");
		}
		if(in && price!=null){
			g.setColor(Color.black);
			g.fillRect(0,50,100,20);
			Resource.drawRes(g, price, 5, 65);
		}

		//System.out.println("paint out");
	}
	public void changePrice(int[]newPrice){
		price[0]=newPrice[0];
		price[1]=newPrice[1];
		price[2]=newPrice[2];
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		in=true;
		//System.out.println("in");
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		in=false;
		//System.out.println("out");
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void upgrade(){
		upgraded=true;
		this.setEnabled(false);
	}
}
