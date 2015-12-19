import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.*;

/**
 * Write a description of class Fenetre here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Fenetre extends JFrame
{   public Panneau pan=new Panneau();
    //private int dir=3;//1 haut, 2 droite, 3 bas, 0 gauche
    public static Sna [] Sn= new Sna [Snake.nbJ];
    public static int WIDTH=1200;
    public static int HEIGHT=600;
    public static int temps=30; // 1/vitesse
    public static boolean pause=false;
    public Fenetre()
    {

        this.setTitle("Snake"); 
        this.setSize(WIDTH,HEIGHT); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        this.setLocationRelativeTo(null); 
        this.setContentPane(pan);
        this.addKeyListener(new ClavierListener());
        this.setBackground(Color.BLUE);
        this.setVisible(true);

      
        Panneau.Gr= new boolean [WIDTH+90][HEIGHT+90];
        Sn= new Sna [Snake.nbJ];
        temps=Snake.vitesse;
        for(int i = 0; i<WIDTH+90; i++)
        {
            for(int j=0; j<HEIGHT+90;j++)
            {
                if(j<45 || j>HEIGHT+24 || i<45 || i>WIDTH+45)
                {
                    Panneau.Gr[i][j]=true;
                }
            }
        }

      
        Snake.Debut=System.currentTimeMillis();

        if(Snake.nbJ==2)
        {
            Sn[0]= new Sna(WIDTH/2,30,3);
            Sn[1]=new Sna(WIDTH/2,HEIGHT-50,1);
        }
        else if (Snake.nbJ==3)
        {
            Sn[0]= new Sna(WIDTH/2,30,3);
            Sn[1]=new Sna(WIDTH/2,HEIGHT-50,1);
            Sn[2]= new Sna(30,HEIGHT/2,2);
        }
        else
        {
            Sn[0]= new Sna(WIDTH/2,30,3);
            Sn[1]=new Sna(WIDTH/2,HEIGHT-50,1);
            Sn[2]= new Sna(30,HEIGHT/2,2);
            Sn[3]= new Sna(WIDTH-30,HEIGHT/2,0);
        }

        int alife=Snake.nbJ;
        while(alife>1)
        {
            alife=Snake.nbJ;

            go();

            for(int s=0; s<Snake.nbJ;s++)
            {
                if(!Sn[s].L)
                    alife--;
            }
        }

        long L =System.currentTimeMillis();
        while(System.currentTimeMillis()-L<3000){}

    
        this.dispose();
    }
    class ClavierListener implements KeyListener{

        public void keyPressed(KeyEvent e)
        {
            for(int s=0; s<Snake.nbJ;s++)
            {
                if(e.getKeyChar()==' ')
                {
                    //long K= System.currentTimeMillis();
                    //while(System.currentTimeMillis()-K<1000){}
                    pause=!pause;
                }
                if(Sn[s].L)
                {

                    if(Sn[s].Touch[0] instanceof String)
                    {
                        if (e.getKeyChar()==((String)Sn[s].Touch[0]).charAt(0) )
                        {
                            if(Sn[s].dir==0)
                                Sn[s].dir=3;
                            else
                                Sn[s].dir--;
                        }
                    }
                    else if (e.getKeyCode()==(Integer)Sn[s].Touch[0] )
                    {
                        if(Sn[s].dir==0)
                            Sn[s].dir=3;
                        else
                            Sn[s].dir--;
                    }
                    if(Sn[s].Touch[1] instanceof String)
                    {
                        if (e.getKeyChar()==((String)Sn[s].Touch[1]).charAt(0))
                        {
                            if(Sn[s].dir==3)
                                Sn[s].dir=0;
                            else
                                Sn[s].dir++;
                        }
                    }
                    else if (e.getKeyCode()==(Integer)Sn[s].Touch[1])
                    {
                        if(Sn[s].dir==3)
                            Sn[s].dir=0;
                        else
                            Sn[s].dir++;
                    }
                    if(Sn[s].Touch[2] instanceof String)
                    {
                        if (e.getKeyChar()==((String)Sn[s].Touch[2]).charAt(0) )
                        {
                            if(Sn[s].dir==0){
                                Sn[s].move(-45,0);
                                //    if((Sn[s].y<0))
                                //    Sn[s].L=false;
                            }
                            else if(Sn[s].dir==1){
                                Sn[s].move(0,-45);
                                //    if(Sn[s].y<0)
                                //    Sn[s].L=false;
                            }
                            else if(Sn[s].dir==2){
                                Sn[s].move(45,0);
                                //     if(Sn[s].x>WIDTH)
                                //     Sn[s].L=false;
                            }
                            else if(Sn[s].dir==3){
                                Sn[s].move(0,45);
                                //     if(Sn[s].y>HEIGHT)
                                //     Sn[s].L=false;
                            }
                            else
                            {} 
                        }
                    }
                    else if (e.getKeyCode()==(Integer)Sn[s].Touch[2])//up
                    {

                        if(Sn[s].dir==0){
                            Sn[s].move(-45,0);
                            //    if((Sn[s].y<0))
                            //    Sn[s].L=false;
                        }
                        else if(Sn[s].dir==1){
                            Sn[s].move(0,-45);
                            //    if(Sn[s].y<0)
                            //    Sn[s].L=false;
                        }
                        else if(Sn[s].dir==2){
                            Sn[s].move(45,0);
                            //     if(Sn[s].x>WIDTH)
                            //     Sn[s].L=false;
                        }
                        else if(Sn[s].dir==3){
                            Sn[s].move(0,45);
                            //     if(Sn[s].y>HEIGHT)
                            //     Sn[s].L=false;
                        }
                        else
                        {} 
                    }

            
                }
                //System.out.println(e.getKeyCode());
            }

        }
        public void keyReleased(KeyEvent e)
        {

        }

        public void keyTyped(KeyEvent e)
        {

        }
    }
    public void go()
    {

        for(int s=0;s<Sn.length;s++){
            if(Sn[s].L)
            {
                if(Sn[s].dir==0){
                    Sn[s].move(-5,0);
                    //         if(Sn[s].x<0)
                    //        Sn[s].L=false;
                }
                else if(Sn[s].dir==1){
                    Sn[s].move(0,-5);
                    //         if(Sn[s].y<0)
                    //         Sn[s].L=false;
                }
                else if(Sn[s].dir==2){
                    Sn[s].move(5,0);
                    //         if(Sn[s].x>WIDTH)
                    //         Sn[s].L=false;
                }
                else if(Sn[s].dir==3){
                    Sn[s].move(0,5);
                    //         if(Sn[s].y>HEIGHT)
                    //         Sn[s].L=false;
                }
                else
                {} 
            }
        }
        //while(pause){}
        repaint();
        long L=System.currentTimeMillis();
        while(System.currentTimeMillis()-L<(temps-(System.currentTimeMillis()-Snake.Debut)/5000)){}

    }
}
