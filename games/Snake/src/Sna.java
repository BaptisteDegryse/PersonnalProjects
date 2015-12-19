import java.awt.*;
import java.util.*;
/**
 * Write a description of class Sna here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Sna
{
    
    public ArrayList<Integer> X=new ArrayList<Integer>();
    public ArrayList<Integer> Y=new ArrayList<Integer>();
    public int x,y;
    //public int []X= new int [Fenetre.HEIGHT*Fenetre.WIDTH];
    //public int []Y= new int [Fenetre.HEIGHT*Fenetre.WIDTH];
    public int dir;
    public boolean L;
    public Object []Touch=new Object [3];
    public Color color;
    
    public int I=0;
    
    public Sna(int X, int Y, int dir )
    {
        this.X.add(X);
        this.Y.add(Y);
        x=X;
        y=Y;
        this.dir=dir;
        this.L=true;
        if(dir==3)
        {
            Touch[0]=37;//les fleches
            Touch[1]=39;
            Touch[2]=38;
            if(!Options.j1T0.equals("fleche gauche"))
            Touch[0]=Options.j1T0;
            if(!Options.j1T1.equals("fleche droite"))
            Touch[1]=Options.j1T1;
            if(!Options.j1T2.equals("fleche haut"))
            Touch[2]=Options.j1T2;
            
            this.color=Color.RED;
        }
        if(dir==1)
        {
            Touch[0]=81;//Q,D,Z
            Touch[1]=68;
            Touch[2]=90;
            
            if(!Options.j2T0.equals("q"))
            Touch[0]=Options.j2T0;
            if(!Options.j2T1.equals("d"))
            Touch[1]=Options.j2T1;
            if(!Options.j2T2.equals("z"))
            Touch[2]=Options.j2T2;
            
            this.color=Color.GREEN;
        }
        if(dir==2)
        {
            Touch[0]=67;// c, v, f
            Touch[1]=86;
            Touch[2]=70;
            
            if(!Options.j3T0.equals("c"))
            Touch[0]=Options.j3T0;
            if(!Options.j3T1.equals("v"))
            Touch[1]=Options.j3T1;
            if(!Options.j3T2.equals("f"))
            Touch[2]=Options.j3T2;
            
            this.color=Color.cyan;
        }
        if(dir==0)
        {
            Touch[0]=73;//i,o,9
            Touch[1]=79;
            Touch[2]=57;
            
            if(!Options.j4T0.equals("i"))
            Touch[0]=Options.j4T0;
            if(!Options.j4T1.equals("o"))
            Touch[1]=Options.j4T1;
            if(!Options.j4T2.equals("9"))
            Touch[2]=Options.j4T2;
            
            this.color=Color.YELLOW;
        }
        
        
        
    }
    public void move(int X, int Y)
    {
        if(this.L)
        {
        //I++;
        this.X.add(x+X);
        this.Y.add(y+Y);
        //this.X[I]=this.X[I-1]+X;
        //this.Y[I]=this.Y[I-1]+Y;
        x=(Integer)this.X.get(this.X.size()-1);
        y=(Integer)this.Y.get(this.Y.size()-1);
        
       // if(this.x<0 || this.y<0 || x>Fenetre.WIDTH || y>Fenetre.HEIGHT)
            {
       //         this.L=false;
            }
        
       }
        
        

    }
    public void draw(Graphics g)
    {
        //for(int i=0; i<=X.size()-1;i++)
        {
                
           //     g.fillRect(X.get(i),Y.get(i),5,5);
           }
        for(int i=0;true;i++)
        {
            try{g.fillRect((Integer)X.get(i),(Integer)Y.get(i),5,5);}
            catch(Exception e)
            {break ;}
        }
    }
    public void drawScore(Graphics g)
    {
        g.setColor(color);
        
        if(color.equals(Color.RED))
            g.drawString("Victoires Rouge : " + Snake.vic[0],50,150);
        else if(color.equals(Color.GREEN))
            g.drawString("Victoires Vert : "+ Snake.vic[1],50,175);
        else if(color.equals(Color.cyan))
            g.drawString("Victoires Bleu : "+ Snake.vic[2],50,200);
        else if(color.equals(Color.YELLOW))
            g.drawString("Victoires Jaune : "+ Snake.vic[3],50,225);
        
    }
    
    
    
}
