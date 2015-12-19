import java.awt.*;
/**
 * Write a description of class Boy here.
 * 
 * @author Baptiste Degryse 
 * @version 03/01/2013
 */
public class Boy
{
    public int [] pos = new int [4];
    public boolean keyR,keyL;
    public int [] lastPos = new int [7];
    public int [] pl= new int[]{0,0};
    public boolean alife=false, mourant=false;
    public final static int JUMP=5, TOP=6, FIX=0, LEFT=1, RIGHT=2, DEAD=9;
    public Color color1,color2;
    public int jump=0;
    public boolean finish=false;
    public boolean unlimitedJump=false;
    public boolean invincible=false;
    public long invincTime=0;
    public Boy()
    {
        color1=Color.GRAY;
        color2=Color.BLACK;
        pos[0]=415;
        pos[1]=380;
    }

    public void drawEyes0(Graphics g)
    {
        g.setColor(color1);//tete
        g.fillOval(pos[0]-15,pos[1]-15,30,30);

        g.setColor(color2);//yeux
        g.fillOval(pos[0]-7,pos[1]-5,4,10);
        g.fillOval(pos[0]+3,pos[1]-5,4,10);
    }

    public void drawEyes1(Graphics g)
    {
        g.setColor(color1);//tete
        g.fillOval(pos[0]-15,pos[1]-15,30,30);

        g.setColor(color2);//yeux
        g.fillOval(pos[0]-12,pos[1]-5,4,10);
        g.fillOval(pos[0]-2,pos[1]-5,4,10);

    }

    public void drawEyes2(Graphics g)
    {
        g.setColor(color1);//tete
        g.fillOval(pos[0]-15,pos[1]-15,30,30);

        g.setColor(color2);//yeux
        g.fillOval(pos[0]-2,pos[1]-5,4,10);
        g.fillOval(pos[0]+8,pos[1]-5,4,10);
    }

    public void draw0(Graphics g)
    {
        g.setColor(color2);//pieds
        g.fillOval(pos[0]-18,pos[1]+7,10,7);
        g.fillOval(pos[0]+8,pos[1]+7,10,7);
    }

    public void draw1(Graphics g)
    {
        g.setColor(color2);//pieds
        g.fillOval(pos[0]+8,pos[1]+7,10,10);
    }

    public void draw2(Graphics g)
    {
        g.setColor(color2);//pieds
        g.fillOval(pos[0]-18,pos[1]+7,10,10);
    }

    public void draw5(Graphics g)
    {
        g.setColor(color2);//pieds
        g.fillOval(pos[0]-18,lastPos[6]+7,10,10);
        g.fillOval(pos[0]+8,lastPos[6]+7,10,10);
    }

    public void draw(Graphics g)
    {
        if(lastPos[3]==JUMP || lastPos[3]==6)
        {
            this.draw5(g);
        }
        else if(pos[2]==FIX)
        {
            this.draw0(g);
        }
        else if(pos[2]==LEFT)
        {
            this.draw1(g);
        }
        else if(pos[2]==RIGHT)
        {
            this.draw2(g);
        }

        if(pos[2]==FIX)
        {
            this.drawEyes0(g);
        }
        else if(pos[2]==LEFT)
        {
            this.drawEyes1(g);
        }
        else if(pos[2]==RIGHT)
        {
            this.drawEyes2(g);
        }

        if(invincible)
        {
            g.setColor(Color.RED);
            g.drawOval(pos[0]-15,pos[1]-15,30,30);
            g.drawString(""+(((int)(-System.currentTimeMillis()+invincTime)/1000)+1),pos[0]-5,pos[1]-20);
        }
    }

    public void invincible(int s)
    {
        invincible=true;
        invincTime=System.currentTimeMillis()+s;
    }

    public void lagY()
    {
        lastPos[4]=lastPos[1];
        for(int i=6; i>4; i--)
        {
            lastPos[i]=lastPos[i-1];
        }
        lastPos[0]=pos[0];
    }

    public void adjust()
    {
        int [] adjustBL= new int[]{0,0};//sol et droite
        SaveMap.maps.get(FatBoy.i).obstacle(pos[0]-1,pos[1]-1, adjustBL, 30,30);

        int [] adjustTR= new int[]{0,0};//haut et gauche
        SaveMap.maps.get(FatBoy.i).obstacle(pos[0]+1,pos[1]+1, adjustTR,30,30);

        if(adjustBL[1] == JUMP)
            pos[1]--;
        else if(adjustTR[1] == TOP)
            pos[1]++;

    }

    public void deadOrAlife(int [] pl)
    {
        if(pos[0]<0 || pos[1] <0 || pos[0]>1000 || pos[1] > 800)
            alife=false;

        if(pl[1]==DEAD || pl[0] ==DEAD)
            alife=false;

    }

    public void move(Graphics g)
    {

        pl[0]=0;
        pl[1]=0;
        this.lagY();
        lastPos[1]=pos[1]; 
        int[] pl= new int[]{0,0};
        SaveMap.maps.get(FatBoy.i).obstacle(pos[0],pos[1], pl,30,30);
        this.adjust();
        this.deadOrAlife(pl);
        if(System.currentTimeMillis()-invincTime>0)
            invincible=false;

        SaveMap.maps.get(FatBoy.i).draw(g);
        
        if(keyR && keyL)
            pos[2]=FIX;
        else if(keyR)
            pos[2]=RIGHT;
        else if(keyL)
            pos[2]=LEFT;
        else
            pos[2]=FIX;

        if(pos[2]==FIX){}
        else if(pos[2]==LEFT && pl[0]!=LEFT)
        {
            pos[0]=pos[0]-4;
        }
        else if(pos[2]==RIGHT && pl[0] !=RIGHT)
        {
            pos[0]=pos[0]+4;
        }

        if(pos[3]==JUMP && (pl[1]==JUMP || lastPos[3]==JUMP ||(lastPos[3] != JUMP && pl[1]!=JUMP))
        &&(lastPos[3]!=6 || unlimitedJump))
        {
            jump=15;

            if(lastPos[3]!=pos[3] && pl[1]==JUMP)
                lastPos[3]=pos[3];
            else
                lastPos[3]=6;
            pos[3]=0;

        }
        else if(pl[1]!=JUMP)
        {
            jump--;
        }
        else if (pl[1]==JUMP)
        {
            jump=0;
            lastPos[3]=0;
        }

        if(pl[1] !=TOP || jump<=0)
        {
            jump=checkDown();
            pos[1]=pos[1]-jump;
        }

    }

    public int checkDown()
    {
        int i=0;
        int [] d= new int [2];

        try
        {
            if(jump<0)
                for(i=0; i<-jump;i++)
                {
                    SaveMap.maps.get(FatBoy.i).obstacle(pos[0],pos[1]+i, d,30,30);

                    if(d[1]==5)
                        break;
            }
            else
                for(i=0; i>-jump;i--)
                {
                    SaveMap.maps.get(FatBoy.i).obstacle(pos[0],pos[1]+i, d,30,30);

                    if(d[1]==6)
                        break;
            }
        }
        catch(IndexOutOfBoundsException e){}
        return -i;
    }

    private int[] die=new int[4];
    public void die(Graphics g, int [] sPos)
    {
        if(invincible)
            mourant=false;
        else
        {
            g.setColor(color1.brighter());
            if(die[0]==0 && die[1]==0 && die[2]==0 && die[3]==0 &&
            Map.contains(pos[0]-15,pos[1]-15,pos[0]+15,pos[1]+15,sPos[0]-25,sPos[1]-25,sPos[0]+25,sPos[1]+25))
            {
                mourant=false;
                alife=false;
                pos[0]=sPos[0];
                pos[1]=sPos[1];
            }
            
            else if(die[0]==0 && die[1]==0 && die[2]==0 && die[3]==0)
            {
                die[0]=Math.abs(sPos[0]-pos[0]);
                die[1]=Math.abs(sPos[1]-pos[1]);
                
            }
            else if(die[0]>0)
            {
                if(die[2] <=20)
                {
                    die[2]+=2;
                }
                die[0]-=5;
                if(sPos[0]-pos[0]>0)
                    g.fillOval(sPos[0]-die[0]-10,pos[1]+die[2]/2,30,30-die[2]);
                else
                    g.fillOval(sPos[0]+die[0]-10,pos[1]+die[2]/2,30,30-die[2]);
                
            }

            else if(die[1]>0)
            {
                if(die[3] <=20)
                {
                    die[3]+=2;
                    
                }
                die[1]-=5;
                if(sPos[1]-pos[1]>0)
                    g.fillOval(sPos[0]-die[3]/2-10,sPos[1]-die[1]-die[3]/2+10,30-die[3],10+die[3]);
                else
                    g.fillOval(sPos[0]-die[3]/2-10,sPos[1]+die[1]-die[3]/2+10,30-die[3],10+die[3]);
                
            }
            else
            {
                die=new int [] {0,0,0,0};
                mourant=false;
                alife=false;
                pos[0]=sPos[0];
                pos[1]=sPos[1];
               
            }
        }

    }
}
