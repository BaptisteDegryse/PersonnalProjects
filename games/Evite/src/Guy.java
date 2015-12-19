import java.awt.*;
import java.util.*;
/**
 * Write a description of class Guy here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Guy
{
    public boolean alife=true;
    public boolean mourant=false;
    public int taille=25;
    public Color color;
    public int [] touches=new int[4];
    public int joueur;
    public int [] pos= new int [2];
    public int dir=0;
    public int [] lastAtt= new int [4];
    public boolean one=true;
    public long [] pushed = new long [2];
    public Guy(int x, int y, int joueur)
    {
        pos[0]=x;
        pos[1]=y;
        this.joueur=joueur;
        if(joueur==1)
        {
            color=Color.RED;
        }
        if(joueur==2)
        {
            color= Color.GREEN;
        }
    }

    public void draw(Graphics g)
    {

        g.setColor(color);
        if(alife)
            g.fillRect(pos[0],pos[1],taille,taille);

        if(mourant)
        {
            taille=taille-2;
            g.fillOval(pos[0],pos[1],taille,taille);
            if(taille<1)
                mourant=false;
        }
    }

    public void move(int dir)// 1=haut, 2=droite, 3= bas , 4= gauche , 0 = ne bouge pas
    {
        if(alife)
        {
            if(System.currentTimeMillis()-pushed[1]>300)
            {
                if(dir==1)
                    pos[1]-=3;
                if(dir==2)
                    pos[0]+=3;
                if(dir==3)
                    pos[1]+=3;
                if(dir==4)
                    pos[0]-=3;
            }
            else
            {
                dir=(int)pushed[0]-4;
                if(dir==3)
                    pos[1]-=5;
                if(dir==4)
                    pos[0]+=5;
                if(dir==1)
                    pos[1]+=5;
                if(dir==2)
                    pos[0]-=5;

            }

            for(int i=0; i<=taille; i=i+2)
            {
                for(int j=0; j<=taille;j=j+2)
                {
                    try{
                        if(Fenetre.grille[pos[1]+5+j][pos[0]+5+i]==0)
                        ;
                        else
                            for(long I= 5; I<=8; I++)
                            {
                                if(Fenetre.grille[pos[1]+5+j][pos[0]+5+i]==I)
                                {
                                    pushed[0]=I;
                                    pushed[1]=System.currentTimeMillis();
                                }
                        }
                    }
                    catch(java.lang.ArrayIndexOutOfBoundsException e)
                    {
                        alife=false;
                        mourant=true;
                    }
                }
            }
        }

    }

    public void att(Graphics g)
    {

        g.setColor(color);

        if(one)
        {
            if(dir==5)
            {
                g.fillRect(pos[0]+taille/2-5 , pos[1]+taille+4 , 10 , 15);
                dir-=4;
                lastAtt[0]=pos[0]+taille/2-5;
                lastAtt[1]=pos[1]+taille+4;
                lastAtt[2]=1;
                lastAtt[3]=5;
                one=false;
            }
            if(dir==6)
            {
                g.fillRect(pos[0]-14 , pos[1]+taille/2-5 , 15 , 10);
                dir-=4;
                lastAtt[0]=pos[0]-14;
                lastAtt[1]=pos[1]+taille/2-5;
                lastAtt[2]=0;
                lastAtt[3]=6;
                one=false;
            }
            if(dir==7)
            {
                g.fillRect(pos[0]+taille/2-5 , pos[1]-14 , 10 , 15);
                dir-=4;
                lastAtt[0]=pos[0]+taille/2-5;
                lastAtt[1]=pos[1]-14;
                lastAtt[2]=1;
                lastAtt[3]=7;
                one=false;
            }
            if(dir==8)
            {
                g.fillRect(pos[0]+taille+4 , pos[1]+taille/2-5 , 15 , 10);
                dir-=4;
                lastAtt[0]=pos[0]+taille+4;
                lastAtt[1]=pos[1]+taille/2-5;
                lastAtt[2]=0;
                lastAtt[3]=8;
                one=false;
            }
            if(!one)
            {
                if(lastAtt[2]==0)
                {
                    for(int i=lastAtt[0]; i<lastAtt[0]+15; i++)
                    {
                        for(int j=lastAtt[1]; j<lastAtt[1]+10;j++)
                        {
                            try{Fenetre.grille[j+5][i+5]=lastAtt[3];}
                            catch(ArrayIndexOutOfBoundsException e)
                            {
                            }
                        }
                    }
                }
                if(lastAtt[2]==1)
                {
                    for(int i=lastAtt[0]; i<lastAtt[0]+10; i++)
                    {
                        for(int j=lastAtt[1]; j<lastAtt[1]+15;j++)
                        {
                            try{Fenetre.grille[j+5][i+5]=lastAtt[3];}
                            catch(ArrayIndexOutOfBoundsException e)
                            {
                            }
                        }
                    }
                }
            }
        }
        else
        {
            if(lastAtt[2]==0)
            {
                for(int i=lastAtt[0]; i<lastAtt[0]+15; i++)
                {
                    for(int j=lastAtt[1]; j<lastAtt[1]+10;j++)
                    {

                        try{Fenetre.grille[j+5][i+5]=0;}
                        catch(ArrayIndexOutOfBoundsException e)
                        {
                        }
                    }
                }
            }
            if(lastAtt[2]==1)
            {
                for(int i=lastAtt[0]; i<lastAtt[0]+10; i++)
                {
                    for(int j=lastAtt[1]; j<lastAtt[1]+15;j++)
                    {

                        try{Fenetre.grille[j+5][i+5]=0;}
                        catch(ArrayIndexOutOfBoundsException e)
                        {
                        }
                    }
                }
            }
            one=true;
        }
    }
}

