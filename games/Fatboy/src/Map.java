import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
/**
 * Write a description of class Map here.
 * 
 * @author Baptiste Degryse 
 * @version 03/01/2013
 */
public class Map extends JPanel implements Serializable
{
    public String name="";
    public int n=0;
    private Viseur v=new Viseur(20);
    public ArrayList<int []> lim= new ArrayList<int []>();
    public ArrayList<int []> boutLim= new ArrayList<int []>();
    private ArrayList<Rect> r= new ArrayList<Rect>();
    private static GradientPaint back=new GradientPaint(0,0,Color.cyan, 0,800, Color.ORANGE,true);
    private static Graphics2D g2d;
    public StartPoint sp;
    public End end;
    private ArrayList<BadBlock> bb= new ArrayList<BadBlock>();
    private ArrayList<MapComponent> all= new ArrayList<MapComponent>();
    private Color cb;
    private Color[] colors;
    private int tNum=1;
    private ArrayList<int []> allEd;
    private ArrayList<Color> c;
    private ArrayList<String> text;
    private boolean first=true;
    public Map(String name, ArrayList<int []> allEd, ArrayList<Color> c,ArrayList<String> text)
    {
        this.name=name;
        this.c=c;
        this.allEd=allEd;
        this.text=text;
        reinitialise();
    }

    public void reinitialise()
    {
        Iterator<Color> iter= c.iterator();
        cb=iter.next();
        tNum=1;
        Iterator<String> sIter= text.iterator();
        for(int i= 0; i<allEd.size();i++)
        {
            int [] obj=allEd.get(i);
            {
                if(obj.length>10)
                    all.add(new Lines(obj));
                else
                    switch(obj[4])
                    {
                        case 0:
                        if(first)
                        {
                            if(obj[3]==1)
                            {
                                System.out.println("Start");
                                sp=new StartPoint(obj[0],obj[1]);
                                all.add(sp);
                            }

                            else if(obj[3]==2)
                                all.add(new End(obj[0],obj[1]));
                        }
                        break;

                        case 1:
                        if(first)
                            all.add(new Rect(obj[0],obj[1],obj[2],obj[3],cb));
                        break;

                        case 2:
                        if(obj[3]==1)
                            all.add(new BadBlock(obj[0],obj[1]));
                        else if(obj[3]==2)
                        {
                            if(obj[2]==1)
                                all.add(new BadSimple(obj[0],obj[1],false));
                            else
                                all.add(new BadSimple(obj[0],obj[1],true));
                        }

                        else if(obj[3]==4)
                            all.add(new BadMine(obj[0],obj[1]));
                        else if(obj[3]==5)
                            all.add(new BadBall(obj[0],obj[1], obj[2]));
                        else if(obj[3]==6 && first)
                            all.add(new Canon(obj[0],obj[1],v,1));
                        else if(obj[3]==7 && first)
                            all.add(new Canon(obj[0],obj[1],v,2));
                        break;

                        case 3:

                        if(obj[3]==1)
                        {
                            all.add(new ChangeColor(obj[0],obj[1],iter.next()));
                        }
                        else if(obj[3]==3)
                        {
                            all.add(new Jump(obj[0],obj[1]));
                        }
                        else if(obj[3]==4)
                        {

                            Rect rec=new Rect(obj[5],obj[6],obj[7], obj[8],Color.WHITE);
                            if(obj[2]==-1)
                                all.add(new BoutonBlock(obj[0],obj[1],rec,false,-1));
                            else if(obj[2]==-2)
                                all.add(new BoutonBlock(obj[0],obj[1],rec,true,-1));
                            else if(obj[2]>200)
                                all.add(new BoutonBlock(obj[0],obj[1],rec,true,obj[2]-200));
                            else if(obj[2]>100)
                                all.add(new BoutonBlock(obj[0],obj[1],rec,false,obj[2]-100));

                            all.add(new Door(rec));
                        }
                        else if(obj[3]==5)
                        {
                            all.add(new Invincible(obj[0],obj[1],obj[2]));
                        }
                        else if(obj[3]==6)
                        {
                            all.add(new Text(sIter.next(),obj[0],obj[1],obj[2]));
                        }
                        else
                        {
                            all.add(new Teleport(obj[0],obj[1],obj[2], obj[3]));
                        }
                        break;

                }
            }
        }
        first=false;
    }

    public void draw(Graphics g)
    {   
        g2d= (Graphics2D) g;
        g2d.setPaint(back);
        g2d.fillRect(0,0,1000,800);
        
        
        for(int i=0; i<all.size(); i++)
        {
            all.get(i).move();
            if(!FatBoy.b.mourant)
                all.get(i).action();
            all.get(i).draw(g);
        }
        v.moved=false;
        v.on=false;
        if(FatBoy.b.mourant)
            FatBoy.b.die(g,sp.sPos);
        
        
        
        Font font = new Font(" TimesRoman ",Font.BOLD,20);
        g2d.setComposite(makeComposite(0.5f));
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(" TimesRoman ",Font.BOLD,15));
        g2d.drawString("Previous:'a'  Next:'z'  Editor:'e'"  ,50,50);
        g2d.drawString("Finished "+n+" times"  ,50,70);

        g2d.setFont(font);
        g2d.drawString("Map " +(FatBoy.i+1)+"     "+name,350,50);
        g2d.setComposite(makeComposite(1f));
    }

    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return(AlphaComposite.getInstance(type, alpha));
    }

    public void obstacle(int pos0, int pos1, int [] pl, int size0, int size1)
    {
        Iterator<int[]> iter= lim.iterator();
        //int[] pl= new int[]{0,0};
        int x=pos0-size0/2, y=pos1-size1/2, x2=x+size0 , y2=y+size1;

        boolean cancel;
        while(iter.hasNext())
        {
            cancel=false;
            int [] l= iter.next();
            int [] can=new int [4];
            for(Iterator<int[]> iterC= boutLim.iterator();iterC.hasNext();)
            {
                can=iterC.next();
                if(can[0]==l[0] && can[1] == l[1])
                    cancel=true;
            }

            if(!cancel)
            {
                if((l[1]<y2 && y2<l[3]) && ((l[0]<x+5 && x+5<l[2]) || (l[0]<x2-5 && x2-5<l[2]))) //sol
                    pl[1]= 5;
                else if((l[1]<y && y<l[3]) && ((l[0]<x+5 && x+5<l[2]) || (l[0]<x2-5 && x2-5<l[2])))//plafond
                    pl[1]=6;

                if(((l[1]<y+5 && y+5<l[3]) || (l[1]<y2-5 && y2-5<l[3])) && (l[0]<x2 && x2<l[2]))//mur à droite
                    pl[0]=2;
                else if(((l[1]<y+5 && y+5<l[3]) || (l[1]<y2-5 && y2-5<l[3])) && (l[0]<x && x<l[2]))//mur gauche
                    pl[0]=1;
            }
        }

    }

    public static boolean contains(int x, int y, int x2, int y2 , int X, int Y, int X2, int Y2)
    {
        if(((X<x && x<X2) || (X<x2 && x2<X2) || (x<X && X<x2) || (x<X2 && X2<x2))
        && ((Y<y && y<Y2) || (Y<y2 && y2<Y2) || (y<Y && Y<y2) || (y<Y2 && Y2<y2)))
            return true;
        return false;
    }

    class Rect implements Serializable,MapComponent{
        private int [] random;
        public int x, y, sizeX, sizeY;
        protected Color c;
        public Rect( int x, int y, int sizeX, int sizeY, Color c)
        {
            this.x=x;
            this.y=y;
            this.sizeX=sizeX;
            this.sizeY=sizeY;
            this.c=c;
            random=new int[sizeX/5];
            for(int i=0; i<sizeX/5; i++)
            {
                random[i]=(int)(Math.random()*(sizeY-5));
            }
            lim.add(new int[]{x,y,x+sizeX,y+sizeY});

        }

        public void draw(Graphics g)
        {
            g.setColor(c);
            g.fillRect(x,y,sizeX,sizeY);

            g.setColor(Color.BLACK);
            for(int i=0; i<sizeX/5; i++)
            {
                g.fillRect(x+5*i,y,2,random[i]+5);
            }

        }

        public boolean equals(Rect rec)
        {
            return this.x==x&&this.y==y&& this.sizeX==sizeX&&this.sizeY==sizeY;
        }

        public void move(){}

        public void action(){

        }
    }

    class StartPoint implements Serializable,MapComponent{
        public int [] sPos= new int[2];
        private boolean ok=false;
        public StartPoint( int x, int y)
        {
            sPos[0]=x+20;
            sPos[1]=y+30;
        }

        public void draw(Graphics g)
        {
            Color c= g.getColor();
            g.setColor(Color.RED);
            for(int i=0; i<= 30; i=i+5)
            {
                g.fillOval(sPos[0]-20,sPos[1]-i,40,3);
            }
            g.setColor(c);
        }

        public int[] getPos()
        {
            return new int [] {sPos[0],sPos[1]-15};
        }

        public void move(){}

        public void action(){
            if(!FatBoy.b.alife && !FatBoy.b.mourant)
            { 
                boolean remove=false;
                Iterator<MapComponent> iter=all.iterator();
                Iterator<int []> iter1=allEd.iterator();
                boutLim.clear();
                //System.out.println(all.size()+"   "+allEd.size());
                while(iter.hasNext())
                {
                    int[] j=new int[]{0,0,0,0};
                    try{
                        j=iter1.next();
                    }
                    catch(java.util.NoSuchElementException e)
                    {
                        System.out.println("erreur");
                    }
                    MapComponent mc=iter.next();
                    if(mc instanceof Canon)
                        ((Canon)mc).b.clear();
                    else if(mc instanceof BadBlock)
                    {
                        ((BadBlock)mc).pos[0]=j[0];
                        ((BadBlock)mc).pos[1]=j[1];
                        if(mc instanceof BadBall)
                        {
                            ((BadBall)mc).jump=0;
                            ((BadBall)mc).v=j[2];
                        }
                    }
                    else if(mc instanceof Lines)
                    {
                        ((Lines)mc).xen.alife=false;
                        ((Lines)mc).xen.start=true;
                    }
                    else if(mc instanceof Bonus)
                    {
                        ((Bonus)mc).alife=true;
                        if(mc instanceof BoutonBlock)
                        {
                            iter.next();
                            if(j[2]==-1)
                            {
                                ((BoutonBlock)mc).n=-1;
                                ((BoutonBlock)mc).on=false;
                            }

                            else if(j[2]==-2)
                            {
                                ((BoutonBlock)mc).n=-1;
                                ((BoutonBlock)mc).on=true;
                            }

                            else if(j[2]>200)
                            {
                                ((BoutonBlock)mc).n=j[2]-200;
                                ((BoutonBlock)mc).on=true;
                            }

                            else if(j[2]>100)
                            {
                                ((BoutonBlock)mc).n=j[2]-100;
                                ((BoutonBlock)mc).on=false;
                            }
                            if(((BoutonBlock)mc).on)
                                boutLim.add(new int [] {((BoutonBlock)mc).rec.x,((BoutonBlock)mc).rec.y
                                    ,((BoutonBlock)mc).rec.sizeX+((BoutonBlock)mc).rec.x,((BoutonBlock)mc).rec.sizeY+((BoutonBlock)mc).rec.y});
                        }
                    }

                }
                FatBoy.b.unlimitedJump=false;
                FatBoy.b.pos[0]=sPos[0];
                FatBoy.b.pos[1]=sPos[1]-20;
                FatBoy.b.jump=0;
                //FatBoy.b.color1=Color.LIGHT_GRAY;

                ok=false;
                FatBoy.b.alife=true;
            }
        }
    }

    class End implements Serializable,MapComponent{
        private int [] ePos= new int[2];
        Color c= new Color(25,100,200);
        public End( int x, int y)
        {
            ePos[0]=20+x;
            ePos[1]=30+y;
        }

        public void draw(Graphics g)
        {
            Color c1= g.getColor();
            g.setColor(c);
            for(int i=0; i<= 30; i=i+5)
            {
                g.fillOval(ePos[0]-20,ePos[1]-i,40,3);
            }
            g.setColor(c1);
        }

        public int[] getPos()
        {
            return new int [] {ePos[0],ePos[1]-15};
        }

        public void move(){}

        public void action(){
            int X1= ePos[0]-20, X2=ePos[0]+20;
            int Y1= ePos[1]-30, Y2=ePos[1];

            int x=FatBoy.b.pos[0]-15;
            int y=FatBoy.b.pos[1]-15;
            int x2=x+30;
            int y2=y+30;
            if( contains(x,y,x2,y2,X1,Y1,X2,Y2))
            {
                FatBoy.b.alife=false;
                if(FatBoy.i<=SaveMap.maps.size()-1)
                    FatBoy.i++;
                n++;
            }
        }
    }

    class BadBlock implements Serializable,MapComponent
    {
        public int [] pos= new int [2];
        protected boolean right=true;
        private long timer=System.currentTimeMillis();
        private boolean moves=true;
        public BadBlock(int x, int y)
        {
            pos[0]=x;
            pos[1]=y-9;
        }

        public void draw(Graphics g)
        {
            g.setColor(Color.BLACK);
            g.fillRect(pos[0]-10,pos[1]-10, 20,20);
            g.setColor(Color.RED);
            g.drawOval(pos[0]-5,pos[1]-5,10,10);

        }

        public void action(){

            if(contains(FatBoy.b.pos[0]-15,FatBoy.b.pos[1]-15,FatBoy.b.pos[0]+15,FatBoy.b.pos[1]+15,pos[0]-10,pos[1]-10,pos[0]+10,pos[1]+10))
            {
                FatBoy.b.mourant=!false;
            }
        }

        public void move()
        {
            int [] obs=new int [2];
            if(right)
                obstacle(pos[0]+12, pos[1], obs, 20,20);
            else
                obstacle(pos[0]-12, pos[1], obs, 20,20);

            if(moves)
            {
                if(obs[0]==1)
                    right=true;
                else if(obs[0]==2)
                    right=false;

                if(obs[1]!=5)
                {
                    pos[1]--;
                }
                if(obs[1]==5 && !right)
                    pos[0]-=2;

                else if(obs[1]==5  && right)
                    pos[0]+=2;

                else if(obs[1]!=5)
                {
                    right=!right;
                    if(System.currentTimeMillis()-timer<50)
                        moves=false;
                    timer=System.currentTimeMillis();
                    if( !right )
                    {
                        pos[0]-=2;
                    }
                    else if( right)
                    {
                        pos[0]+=2;
                    }
                }
            }
            else
            {
                if(obs[1]!=5)
                {
                    pos[1]+=2;
                    moves=true;
                    right=!right;
                }
                else if(obs[1]==5)
                {
                    pos[1]-=2;
                    moves=true;
                    right=!right;
                }
            }
        }
    }
    class BadMine extends BadBlock
    {
        public BadMine(int x, int y)
        {
            super(x,y+10);
        }

        public void move()
        {

        }

        public void draw(Graphics g)
        {
            g.setColor(Color.BLACK);
            g.drawLine(pos[0]-10,pos[1],pos[0]+10,pos[1]);
            g.drawLine(pos[0],pos[1]-10,pos[0],pos[1]+10);
            g.drawLine(pos[0]-10,pos[1]+10,pos[0]+10,pos[1]-10);
            g.drawLine(pos[0]-10,pos[1]-10,pos[0]+10,pos[1]+10);

            g.setColor(Color.RED);
            g.fillOval(pos[0]-5,pos[1]-5,10,10);
        }

    }
    class BadSimple extends BadBlock
    {
        private boolean horiz=true;
        public BadSimple(int x, int y, boolean horiz)
        {
            super(x,y+10);
            this.horiz=horiz;
        }

        public void move()
        {
            if(horiz)
            {
                int [] obs=new int [2];
                if(right)
                    obstacle(pos[0]+4, pos[1], obs, 20,20);
                else
                    obstacle(pos[0]-4, pos[1], obs, 20,20);

                if(obs[0]!=0)
                    right=!right;

                if(right)
                    pos[0]+=3;
                else
                    pos[0]-=3;
            }
            else
            {
                int [] obs=new int [2];
                if(right)
                    obstacle(pos[0], pos[1]+4, obs, 20,20);
                else
                    obstacle(pos[0], pos[1]-4, obs, 20,20);

                if(obs[0]!=0)
                    right=!right;

                if(right)
                    pos[1]+=3;
                else
                    pos[1]-=3;
            }
        }

        public void draw(Graphics g)
        {
            g.setColor(Color.BLACK);
            g.fillRect(pos[0]-10,pos[1]-10, 20,20);

            g.setColor(Color.RED);
            if(horiz)
                g.drawRect(pos[0]-7,pos[1]-2,14,4);
            else
                g.drawRect(pos[0]-2,pos[1]-7,4,14);
        }
    }

    class BadBall extends BadBlock
    {
        public int jump=0;
        public int v=0;
        public BadBall(int x, int y, int v)
        {
            super(x,y);
            this.v=v;
        }

        public void draw(Graphics g)
        {
            g.setColor(Color.BLACK);
            g.fillOval(pos[0]-10,pos[1]-10, 20,20);
            g.setColor(Color.RED);
            g.fillOval(pos[0]-5,pos[1]-5,10,10);

        }

        public void move()
        {

            jump--;
            int x= pos[1]-jump;
            if(jump<0 || true)
                pos[1]=pos[1]-checkDown();
            else
                pos[1]-=x;

            if(x!=pos[1])
                jump=-jump;

            int [] obs=new int [2];
            if(right)
                obstacle(pos[0]+v+1, pos[1], obs, 20,20);
            else
                obstacle(pos[0]-v-1, pos[1], obs, 20,20);

            if(obs[0]!=0)
                right=!right;

            if(right)
                pos[0]+=v;
            else
                pos[0]-=v;
        }

        public int checkDown()
        {
            int i=0;
            int [] d= new int [2];

            if(jump<0)
                for(i=0; i<-jump;i++)
                {
                    obstacle(pos[0],pos[1]+i, d,20,20);

                    if(d[1]==5)
                        break;
            }
            else
                for(i=0; i>-jump;i--)
                {
                    obstacle(pos[0],pos[1]+i, d,20,20);

                    if(d[1]==6)
                        break;
            }
            return -i;
        }

    }

    class Canon implements Serializable,MapComponent
    {
        public int [] pos= new int [2];
        public Viseur v;
        private ArrayList<Bullet> b=new ArrayList<Bullet>();
        private long timer=System.currentTimeMillis();
        private int num;
        public Canon(int x, int y, Viseur v, int number)
        {
            pos[0]=x;
            pos[1]=y;
            this.v=v;
            num=number;
        }

        public void move()
        {

        }

        public void draw(Graphics g)
        {
            if(num==1)
                g.setColor(Color.WHITE);
            else if(num==2)
                g.setColor(Color.YELLOW);
            g.fillOval(pos[0]-10,pos[1]-10,20,20);

            if(!v.moved)
            {
                v.move();
                v.draw(g);
                v.action();
            }
            for(int i=0; i<b.size();i++)
            {
                b.get(i).move();
                b.get(i).draw(g);
                b.get(i).action();
                if(!b.get(i).alife)
                    b.remove(i);
            }

        }

        public void action()
        {
            if(v.on && System.currentTimeMillis()-timer>300)
            {
                if(num==1)
                    b.add( new Bullet(pos[0],pos[1],v.pos[0],v.pos[1],1));
                else if(num==2)
                    b.add(new Bullet(pos[0],pos[1],v.pos[0],v.pos[1],1.2));
                timer=System.currentTimeMillis();
            }
        }
    }

    class Bullet implements Serializable,MapComponent
    {
        public int[] pos= new int [2];
        private int[] vec= new int[2];
        private double vitesse;
        private int touch=2;
        public boolean alife=true;
        public Bullet(int x, int y, int x2, int y2, double v)
        {
            pos[0]=x;
            pos[1]=y;
            vitesse=v/100;
            double X=x,Y=y,X2=x2,Y2=y2;

            double racine= Math.sqrt((Y2-Y)*(Y2-Y)+(X2-X)*(X2-X));
            vec[0]=(int)(-1000*(X-X2)/racine);
            vec[1]=(int)(-1000*(Y-Y2)/racine);
        }

        public void move()
        {
            if(vitesse==1.2/100)
            {
                int []obs0= new int[2];
                int []obs1= new int[2];
                obstacle(pos[0]+(int)(vec[0]*vitesse),pos[1],obs0,12,12);
                obstacle(pos[0],pos[1]+(int)(vec[1]*vitesse),obs1,12,12);

                if(obs0[0]!=0 || obs1[1]!=0)
                {
                    touch--;
                }
                if(obs0[0]!=0 )
                    vec[0]=-vec[0];
                else if(obs1[1]!=0 )
                    vec[1]=-vec[1];

            }
            pos[0]+=vec[0]*vitesse;
            pos[1]+=vec[1]*vitesse;
            if(pos[1]<0 || pos[0]<0 || pos[1]>800 || pos[0]> 1000 || touch<0)
                alife=false;
        }

        public void action(){
            if(contains(FatBoy.b.pos[0]-15,FatBoy.b.pos[1]-15,FatBoy.b.pos[0]+15,FatBoy.b.pos[1]+15,pos[0]-5,pos[1]-5,pos[0]+5,pos[1]+5))
            {
                FatBoy.b.mourant=!false;
            }
        }

        public void draw(Graphics g)
        {
            g.setColor(Color.BLACK);
            g.fillOval(pos[0]-5,pos[1]-5,10,10);
            if(vitesse==1)
                g.setColor(Color.LIGHT_GRAY);
            else
                g.setColor(Color.ORANGE);
            g.drawOval(pos[0]-5,pos[1]-5,10,10);
        }
    }
    class Viseur implements Serializable,MapComponent
    {
        private int lag;
        private int [] pos= new int [2];
        public int [] lastPosX;
        public int [] lastPosY;
        public boolean on=false;
        public boolean moved=false;
        public Viseur(int l)
        {
            lag=l;
            lastPosX = new int [l];
            lastPosY = new int [l];
        }

        public void draw(Graphics g)
        {
            g.setColor(Color.BLACK);
            g.drawLine(pos[0]-10,pos[1],pos[0]+10,pos[1]);
            g.drawLine(pos[0],pos[1]-10,pos[0],pos[1]+10);
            g.drawOval(pos[0]-10,pos[1]-10,20,20);
        }

        public void move()
        {
            lastPosX[0]=FatBoy.b.pos[0];
            lastPosY[1]=FatBoy.b.pos[1];
            for(int i=lag-1; i>0; i--)
            {
                lastPosX[i]=lastPosX[i-1];
                lastPosY[i]=lastPosY[i-1];
            }
            pos[0]=lastPosX[lag-1];
            pos[1]=lastPosY[lag-1];
            moved=true;
        }

        public void action()
        {
            if(pos[0]==FatBoy.b.pos[0] && pos[1]==FatBoy.b.pos[1])
                on=true;
        }
    }
    class Lines implements Serializable,MapComponent
    {   public int[] pos;
        public Xen xen;
        public int i;
        public int[] lastXen= new int[2];
        public Lines(int[] pos)
        {
            initialise(pos);
            i=0;
            xen=new Xen(pos[i],pos[i+1],pos[i+2],pos[i+3]);
        }

        public void initialise(int[] pos)
        {
            int count=0;
            for(int i:pos)
            {
                if(i!=0)
                    count++;
            }
            int[] extPos=new int [2*count-2];
            for(int i=0;i<(extPos.length/2-2);i+=2)
            {
                int j=2*i;
                extPos[j]=pos[i];
                extPos[j+1]=pos[i+1];
                extPos[j+2]=(pos[i+2]+pos[i])/2;
                extPos[j+3]=(pos[i+3]+pos[i+1])/2;
            }
            extPos[extPos.length-2]=extPos[0];
            extPos[extPos.length-1]=extPos[1];
            int[] extPos2=new int [2*extPos.length-6];
            for(int i=0;i<extPos.length-4;i+=2)
            {
                int j=2*i;
                extPos2[j]=extPos[i];
                extPos2[j+1]=extPos[i+1];
                extPos2[j+2]=(extPos[i+2]+extPos[i])/2;
                extPos2[j+3]=(extPos[i+3]+extPos[i+1])/2;
            }
            extPos2[extPos2.length-2]=extPos2[0];
            extPos2[extPos2.length-1]=extPos2[1];

            int[] extPos3=new int [2*extPos2.length-6];
            for(int i=0;i<extPos2.length-4;i+=2)
            {
                int j=2*i;
                extPos3[j]=extPos2[i];
                extPos3[j+1]=extPos2[i+1];
                extPos3[j+2]=(extPos2[i+2]+extPos2[i])/2;
                extPos3[j+3]=(extPos2[i+3]+extPos2[i+1])/2;
            }
            extPos3[extPos3.length-2]=extPos3[0];
            extPos3[extPos3.length-1]=extPos3[1];

            this.pos=extPos3;
        }

        public void draw(Graphics g)
        {
            g.setColor(Color.BLACK);
            for(int i=0; i<pos.length-2&&false;i+=2)
            {
                g.drawLine(pos[i],pos[i+1],pos[i+2],pos[i+3]);
            }
            xen.draw(g);
        }

        public void action()
        {

        }

        public void move()
        {
            if(!xen.alife)
            {
                lastXen=xen.pos;
                i+=2;
                if(i+3>=pos.length)
                    i=0;
                if(xen.start)
                {
                    i=0;
                    xen=new Xen(pos[0],pos[1],pos[2],pos[3]);
                    xen.start=false;
                }
                else
                    xen=new Xen(lastXen[0],lastXen[1],pos[i+2],pos[i+3]);

            }
            xen.move();
            xen.action();
        }
    }

    class Xen extends Bullet
    {
        public int[] ePos=new int[2];
        public boolean start=false;
        public Xen(int x, int y, int x2, int y2)
        {
            super(x,y,x2,y2,0.5);
            ePos[0]=x2;
            ePos[1]=y2;
        }

        public void action()
        {
            if(contains(FatBoy.b.pos[0]-15,FatBoy.b.pos[1]-15,FatBoy.b.pos[0]+15,FatBoy.b.pos[1]+15,pos[0]-10,pos[1]-10,pos[0]+10,pos[1]+10))
            {
                FatBoy.b.mourant=true;
            }
            if(contains(ePos[0],ePos[1],ePos[0],ePos[1],pos[0]-30,pos[1]-30,pos[0]+30,pos[1]+30))
            {
                alife=false;
            }
        }

        public void draw(Graphics g)
        {
            g.setColor(Color.BLUE);
            g.drawLine(pos[0]-10,pos[1],pos[0]+10,pos[1]+10);
            g.drawLine(pos[0]-10,pos[1],pos[0]+10,pos[1]-10);
            g.drawLine(pos[0]+10,pos[1],pos[0]-10,pos[1]+10);
            g.drawLine(pos[0]+10,pos[1],pos[0]-10,pos[1]-10);
            super.draw(g);
        }

    }

    abstract class Bonus implements Serializable,MapComponent
    {
        protected int [] pos= new int [4];
        public boolean alife=true;
        protected Color col;
        protected String name="";
        public Bonus(int x, int y)
        {
            pos[0]=x;
            pos[1]=y;
        }

        public void move(){}

        public void action()
        {
            if(contains(FatBoy.b.pos[0]-15,FatBoy.b.pos[1]-15,FatBoy.b.pos[0]+15,FatBoy.b.pos[1]+15,pos[0],pos[1],pos[0]+15,pos[1]+15)&&alife)
            {
                alife=false;
                active();
            }

        }

        public void active()
        {}

        public void draw(Graphics g){
            if(alife)
            {
                g.setColor(col);
                g.drawOval(pos[0],pos[1],15,15);
                Font font = new Font("TimesRoman",Font.PLAIN,12);
                g.setFont(font);
                g.setColor(Color.BLACK);
                g.drawString(name,pos[0]+3,pos[1]+12);

                if(pos[2]!=0)
                {
                    g.drawOval(pos[2],pos[3],15,15);
                    g.drawString(name,pos[2]+3,pos[3]+12);
                }
            }
        }
    }
    class Invincible extends Bonus
    {
        private int t=1000;//ms
        public Invincible(int x, int y, int t)
        {
            super(x,y);
            this.t=t;
            name=t/1000+"";
            col=Color.RED;
        }

        public void active()
        {
            FatBoy.b.invincible(t);
        }

        public void draw(Graphics g)
        {
            g.setColor(Color.BLUE);
            g.drawRect(pos[0],pos[1],15,15);
            super.draw(g);
        }
    }
    class ChangeColor extends Bonus
    {
        public ChangeColor(int x, int y, Color c)
        {
            super(x,y);
            super.col=c;
            name="C";
        }

        public void draw(Graphics g){
            if(alife)
            {
                g.setColor(col);
                g.fillOval(pos[0],pos[1],15,15);
                Font font = new Font("TimesRoman",Font.PLAIN,12);
                g.setFont(font);
                g.setColor(Color.BLACK);
                g.drawString(name,pos[0]+3,pos[1]+12);

                if(pos[2]!=0)
                {
                    g.drawOval(pos[2],pos[3],15,15);
                    g.drawString(name,pos[2]+3,pos[3]+12);
                }
            }
        }

        public void active()
        {
            FatBoy.b.color1=col;
            alife=true;
        }
    }
    class Teleport extends Bonus
    {
        public int num;
        public Teleport(int x, int y,int x2, int y2)
        {
            super(x,y);
            pos[2]=x2;
            pos[3]=y2;
            num=tNum;
            name=""+tNum;
            col=Color.WHITE;
            tNum++;
        }

        public void active()
        {
            FatBoy.b.pos[0]=pos[2]+7;
            FatBoy.b.pos[1]=pos[3]+7;
            alife=true;
        }

    }
    class Jump extends Bonus
    {

        public Jump (int x, int y)
        {
            super(x,y);
            col=Color.BLUE;
        }

        public void draw(Graphics g){
            if(alife)
            {
                g.setColor(col);
                g.drawRect(pos[0],pos[1],15,15);
                g.drawRect(pos[0]+2,pos[1]+2,11,11);
                g.drawRect(pos[0]+4,pos[1]+4,7,7);
            }
        }

        public void active()
        {
            FatBoy.b.unlimitedJump=true;
        }
    }
    class BoutonBlock extends Bonus
    {
        public Rect rec;
        public boolean on,reutil;
        private long timer=0;
        private int n;
        public BoutonBlock(int x, int y , Rect rec, boolean on,int n)
        {
            super(x,y);
            this.rec=rec;
            this.on=on;
            this.n=n;
            if(on)
                boutLim.add(new int [] {rec.x,rec.y,rec.sizeX+rec.x,rec.sizeY+rec.y});

            if(n>0)
                name=""+n;
        }

        public void draw(Graphics g)
        {

            if(on)
                col=Color.GREEN;
            else
                col=Color.RED;

            g.setColor(Color.BLUE);
            g.fillRect(pos[0],pos[1],15,15);
            g.setColor(col);
            g.fillOval(pos[0]+2,pos[1]+2,11,11);

            Font font = new Font("TimesRoman",Font.PLAIN,10);
            g.setFont(font);
            g.setColor(Color.BLACK);
            if(n!=-1)
                g.drawString(""+n,pos[0]+5,pos[1]+12);

        }

        public void active()
        {

            if(System.currentTimeMillis()-timer>1000 && n!=0)
            {
                on=!on;
                timer=System.currentTimeMillis();
                if(on)
                    boutLim.add(new int [] {rec.x,rec.y,rec.sizeX+rec.x,rec.sizeY+rec.y});
                else
                {
                    Iterator<int []> iter= boutLim.iterator();
                    while(iter.hasNext())
                    {
                        int []n= iter.next();
                        if(n[0]==rec.x && n[1]== rec.y && n[2]==rec.sizeX+rec.x && n[3]==rec.sizeY+rec.y)
                            iter.remove();
                    }
                }
                if(on)
                    col=Color.GREEN;
                else
                    col=Color.RED;

                if(n>0)
                    n--;

            }
            alife=true;
        }
    }
    class Door extends Rect
    {
        public boolean on;
        public Rect rec;
        public Door(Rect rec)
        {
            super(rec.x,rec.y,rec.sizeX,rec.sizeY,Color.WHITE);
            this.rec=rec;
        }

        public void draw(Graphics g)
        {
            g.setColor(Color.BLACK);
            g.drawRect(x,y,sizeX,sizeY);
            if(!on)
                g.setColor(Color.GREEN);
            else
                g.setColor(Color.RED);
            for(int i=3; i<=sizeX/2 || i<= sizeY/2; i+=3)
            {
                g.drawRect(x+i,y+i,sizeX-2*i,sizeY-2*i);
            }

        }

        public void action()
        {

            if(boutLim.size()<=0)
                on=true;
            else
            {
                Iterator<int[]> iter=boutLim.iterator();
                boolean off=false;
                while(iter.hasNext())
                {
                    int []n= iter.next();
                    if(n.length!=4){}
                    else if(rec==null)
                    {System.out.println("rec is null");}
                    else if(n[0]==rec.x && n[1]== rec.y && n[2]==rec.sizeX+rec.x && n[3]==rec.sizeY+rec.y)
                        off=true;
                }
                on=!off;
            }
        }
    }
    class Text implements Serializable,MapComponent
    {
        private String s="";
        private int[]pos=new int[2];
        private int n;
        public Text(String s,int x, int y,int n)
        {
            this.s=s;
            pos[0]=x;
            pos[1]=y;
            this.n=n;
        }

        public void move(){}

        public void action(){}

        public void draw(Graphics g)
        {
            Font font;
            if(n==1)
                font = new Font("Comic Sans MS",Font.BOLD,14);
            else if(n==2)
                font = new Font("Comic Sans MS",Font.BOLD,20);
            else
                font = new Font("Comic Sans MS",Font.BOLD,30);
            g.setColor(Color.BLACK);
            g.setFont(font);
            g.drawString(s,pos[0],pos[1]);
        }
    }
}

