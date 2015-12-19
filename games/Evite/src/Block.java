import java.awt.*;
import java.util.*;
/**
 * Write a description of class Block here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Block
{
    public int[] pos= new int [2];
    public int [] size= new int [2];
    public int speed;
    public boolean vertical=true;
    public boolean bd=false;
    public boolean alife=true;
    public boolean mourant=false;
    public Block(int x, int y, int largeur, int longueur, int speed)
    {
        pos[0]=x;
        pos[1]=y;
        size[0]=longueur;
        size[1]=largeur;
        this.speed=speed;
        if(x==0||x==Fenetre.height)
            this.vertical=false;

        if(y==0 || x== 0)
            this.bd=true;
    }

    public void draw(Graphics g)
    {
        if(alife||mourant)
        {
            g.setColor(Color.BLACK);
            if(pos[0]+5+size[0]> Fenetre.width+9 || pos[0]+5<1 
            || pos[1]+5+size[1]>Fenetre.height+9 || pos[1]+5<1)
            {
                alife=false;
            }
            if(vertical)
            {
                if(bd)
                    pos[1]=pos[1]+speed;
                else
                    pos[1]=pos[1]-speed;
            }
            else
            {
                if(bd)
                    pos[0]=pos[0]+speed;
                else
                    pos[0]=pos[0]-speed;
            }
            g.fillRect(pos[0],pos[1], size[1], size[0]);
        }

    }

    public boolean Contains(int x, int y)
    {
        int X1= this.pos[0], X2=this.pos[0]+this.size[1];
        int Y1= this.pos[1], Y2=this.pos[1]+this.size[0];

        int x2=x+25;
        int y2=y+25;
        if(((X1<x && x<X2) || (X1<x2 && x2<X2) || (x<X1 && X1<x2) || (x<X2 && X2<x2))
        && ((Y1<y && y<Y2) || (Y1<y2 && y2<Y2) || (y<Y1 && Y1<y2) || (y<Y2 && Y2<y2)))
            return true;
        return false;
    }
}
