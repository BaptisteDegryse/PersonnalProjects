import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Color.*;
/**
 * Write a description of class Panneau here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Panneau extends JPanel
{
    public static Color cadre;
    private static boolean add= false;
    public static int first=0;
    public static int min=-1;
    public static int sec=0;

    public void paintComponent(Graphics g)
    {

        for(int j=0; j<Evite.nbj;j++)
        {
            Evite.guys[j].att(g);
            Evite.guys[j].move(Evite.guys[j].dir);
            Evite.guys[j].draw(g);
        }
        Evite.blocks.draw(g);
        g.setColor(cadre);
        g.fillRect(0,0,Fenetre.width,100);
        g.fillRect(0,0,100,Fenetre.height);
        g.fillRect(Fenetre.width-100,0,100,Fenetre.height);
        g.fillRect(0,Fenetre.height-100,Fenetre.width,100);
        drawAc(g);
        if( first<3 && Fenetre.end)
            drawScore(g);
    }
    
    public static boolean Contains(int X1, int X2, int Y1, int Y2, int x, int y)
    {
       

        int x2=x+25;
        int y2=y+25;
        if(((X1<x && x<X2) || (X1<x2 && x2<X2) || (x<X1 && X1<x2) || (x<X2 && X2<x2))
        && ((Y1<y && y<Y2) || (Y1<y2 && y2<Y2) || (y<Y1 && Y1<y2) || (y<Y2 && Y2<y2)))
            return true;
        return false;
    }

    public void drawAc(Graphics g)
    {
        g.setColor(Color.WHITE);
        int timeM=(int)(Fenetre.start-System.currentTimeMillis())/1000;
        sec= -timeM%60;

        if(timeM%60==0 && !add)
        {
            min++;
            add=true;
        }
        if(timeM%60==-1)
            add=false;
        //int min= -(timeM-sec)/60;
        Font fonte = new Font(" TimesRoman ",Font.BOLD,30);
        g.setFont(fonte);
        g.drawString(min+":"+(sec<10? "0"+sec : sec )+"   Lvl " + (Fenetre.lvl+1), 50,50);
    }

    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return(AlphaComposite.getInstance(type, alpha));
    }

    public void drawScore(Graphics g)
    {
        Graphics2D g2d= (Graphics2D) g;
        g2d.setComposite(makeComposite(0.5f));
        g2d.setColor(Color.WHITE);
        g2d.fillRect(100,100,Fenetre.width-200,Fenetre.height-200);
        Fenetre.score = SaveData.getData();
        g2d.setColor(Color.BLACK);
        for(int i=0; i<10 && i< Fenetre.score.length && Fenetre.score[i]!= null && first ==1; i++)
        {
            g2d.drawString(Fenetre.score[i]+"",150,200+30*i);
        }
        first++;
    }
}
