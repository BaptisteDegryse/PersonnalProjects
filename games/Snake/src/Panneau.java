import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Color.*;
import java.io.*;
import javax.imageio.*;
/**
 * Write a description of class Panneau here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Panneau extends JPanel
{

        public static boolean [][] Gr;
        private static boolean first=true;
        public Image img;
    public void paintComponent(Graphics g)
    {
       
        //g.setColor(Color.BLUE);
        //g.fillRect(0,0,this.getWidth(),this.getHeight());

       
        for(int s=0;s<Fenetre.Sn.length ;s++)
        {    
            boolean exc=true;
            try{exc=Gr[Fenetre.Sn[s].x+45]  [Fenetre.Sn[s].y+45];
                exc=true;}
            catch(Exception e){exc=false;}

            if( exc && Gr[Fenetre.Sn[s].x+45]  [Fenetre.Sn[s].y+45] && Fenetre.Sn[s].L)
            {

                if(System.currentTimeMillis()-Snake.Debut>2000)
                {
                    Fenetre.Sn[s].L=false;

                }

            }
            else if (exc)
            {

                Gr[Fenetre.Sn[s].x+45] [Fenetre.Sn[s].y+45]=true;
                
                
            }
            g.setColor(Fenetre.Sn[s].color);
            Fenetre.Sn[s].draw(g);

        }
    
        int count=Snake.nbJ;

        for (int i=0; i<Snake.nbJ; i++)
        {
            if(!Fenetre.Sn[i].L)
                count--;
        }

        if(count<=1)
        {
            Font font = new Font("Courier", Font.BOLD, 50); 
            g.setFont(font); 
            g.setColor(Color.BLACK);       
            g.drawString("GAME OVER",50,50);
            font = new Font("Courier", Font.BOLD, 20);
            g.setFont(font);
            g.fillRect(40,75, 350,200);

            if (count==0)
            {
                g.setColor(Color.WHITE);
                g.drawString("Personne ne gagne !!!",50,100);
            }
            else if(Fenetre.Sn[0].L)
            {
                Snake.vic[0]++;
                g.setColor(Color.RED);
                g.drawString("Le Joueur Rouge Gagne !!!",50,100);
                for(int i=0;i<Snake.nbJ;i++)
                {
                    Fenetre.Sn[i].drawScore(g);
                }

             
            } 
            else if(Fenetre.Sn[1].L)
            {
                Snake.vic[1]++;

                g.setColor(Color.GREEN);    
                g.drawString("Le Joueur Vert Gagne !!!",50,100);
                for(int i=0;i<Snake.nbJ;i++)
                {
                    Fenetre.Sn[i].drawScore(g);
                }

            }
            else if(Fenetre.Sn[2].L)
            {
                Snake.vic[2]++;

                g.setColor(Color.cyan);       
                g.drawString("Le Joueur Bleu Gagne !!!",50,100);
                for(int i=0;i<Snake.nbJ;i++)
                {
                    Fenetre.Sn[i].drawScore(g);
                }
            }
            else if(Fenetre.Sn[3].L)
            {
                Snake.vic[3]++;

                g.setColor(Color.YELLOW);       
                g.drawString("Le Joueur Jaune Gagne !!!",50,100);
                for(int i=0;i<Snake.nbJ;i++)
                {
                    Fenetre.Sn[i].drawScore(g);
                }

            }

            long L=System.currentTimeMillis();
            while(System.currentTimeMillis()-L<1000){}

        }
       
        
        
    }
}
