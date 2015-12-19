import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//import javax.sound.*;
/**
 * Write a description of class Fenetre here.
 * 
 * @author Baptiste Degryse 
 * @version 03/01/2013
 */
public class Fenetre extends JFrame
{
    public Panneau pan = new Panneau();
    private boolean play=true;
    public static int width=1000;
    public static int height=800;
    private static boolean q,c,h,o,p,l,k;
    private Map copie=null;
    public Fenetre()
    {
        this.setTitle("FatBoy"); 
        this.setSize(width,height); 
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        this.setLocationRelativeTo(null);
        this.setContentPane(pan);

        this.addKeyListener(new ClavierListener());
        this.setVisible(true);

        go();

        this.dispose();
    }

    public void go()
    {
        //        SoundManager s=SundManager.getInstance();
        //       s.addWaveSound(new file("Fatboy back.wav"),"Back");
        //       s.loop("Back");
        
        while(play)
        {
           long time=System.currentTimeMillis();
            if(!this.isShowing())
            {
                play=false;
                FatBoy.again=false;
            }
            if(q && c && h && o)
            {
                o=false;
                SaveMap.maps.remove(FatBoy.i);
            }
            while(p)
            {
                Thread.yield();
            }
            if(c&&h&&k)
            {
                copie=SaveMap.maps.get(FatBoy.i);
                k=false;
            }
            if(c&&h&&l)
            {
                SaveMap.maps.add(FatBoy.i,copie);
                l=false;
            }
            while(System.currentTimeMillis()-time<20){Thread.yield();}
            
            repaint();
            
        }
    }
    class ClavierListener implements KeyListener{

        public void keyPressed(KeyEvent e)
        {
            if(e.getKeyCode()==37)//g
            {
                FatBoy.b.keyL=true;
            }
            else if(e.getKeyCode()==39)//d
            {
                FatBoy.b.keyR=true;
            }
            else if(e.getKeyCode()==38)//h
            {
                FatBoy.b.pos[3]=5;
            }

            else if(e.getKeyCode()==40)//b
            {

            }
            else if(e.getKeyChar()=='a')
            { 
                if(FatBoy.i>0)
                {
                    FatBoy.i--;
                    FatBoy.b.alife=false;
                    FatBoy.b.mourant=false;
                }
            }
            else if(e.getKeyChar()=='z')
            {
                if(FatBoy.i<=SaveMap.maps.size()-1)
                {
                    FatBoy.i++;
                    FatBoy.b.alife=false;
                    FatBoy.b.mourant=false;
                }
            }
            else if(e.getKeyChar()=='e')
            {
                play=false;
                FatBoy.again=true;
            }
            else if(e.getKeyChar()=='q')
            {
                q=true;
            }
            else if(e.getKeyChar()=='c')
            {
                c=true;
            }
            else if(e.getKeyChar()=='h')
            {
                h=true;
            }
            else if(e.getKeyChar()=='o')
            {
                o=true;
            }
            else if(e.getKeyChar()=='p')
            {
                p=!p;
            }
            else if(e.getKeyChar()=='l')
            {
                l=true;
            }
            else if(e.getKeyChar()=='k')
            {
                k=true;
            }

        }

        public void keyReleased(KeyEvent e)
        {
            if(e.getKeyCode()==37)//g
            {
                FatBoy.b.keyL=false;
                FatBoy.b.pos[3]=0;
            }
            else if(e.getKeyCode()==39)//d
            {
                FatBoy.b.keyR=false;
                FatBoy.b.pos[3]=0;
            }
            else if(e.getKeyChar()=='q')
            {
                q=false;
            }
            else if(e.getKeyChar()=='c')
            {
                c=false;
            }
            else if(e.getKeyChar()=='h')
            {
                h=false;
            }
            else if(e.getKeyChar()=='o')
            {
                o=false;
            }
        }

        public void keyTyped(KeyEvent e)
        {

        }
    }
}
