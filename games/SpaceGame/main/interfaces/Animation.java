package interfaces;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Animation {
	public static int N=1,
			nExplosion=0;
	private static Image[][] img=new Image[N][];
	private static int index=0;
	private long timer=0;
	private boolean loop=false;
	private int period=50;
	private int animationNo=0;
	public boolean done=false;

	public Animation(int animationNo){
		this.animationNo=animationNo;
	}
	public static void loadAnimation(String paths[]){
		int i=0;
		img[index]=new Image[paths.length];
		try{
			for(String path:paths){
				img[index][i]=ImageIO.read(new File(path));
				i++;
			}
		}
		catch(IOException e){
			System.out.println("erreur de chargement d'image dans animation : i= "+i+"  "+paths[i] );
		}
		index++;
	}
	public static void init(){
		Image imTouch=null;
		try{
			imTouch= ImageIO.read(new File("res/animation/fireball2.png"));
			img[index]=new Image[4];
			for(int i=0;i<=3;i++)
			{
				img[index][i]=((BufferedImage)imTouch).getSubimage(i*16,0, 16, 16);
			}
		}
		catch(Exception e){e.printStackTrace();}
		index++;
	}
	public void start(){
		timer=System.currentTimeMillis();
		done=false;
	}
	public void setLoop(boolean loop){
		this.loop=loop;
	}
	public void setPeriod(int ms){
		period=ms;
	}
	public void draw(Graphics g, int x, int y, int w, int h){
		if(loop || System.currentTimeMillis()-timer<img[animationNo].length*period){
			int i=(int)((System.currentTimeMillis()-timer)%(img[animationNo].length*period))/period;
			g.drawImage(img[animationNo][i], x, y, w, h, null);
		}
		else
			done=true;
	}
}
