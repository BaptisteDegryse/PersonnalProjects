import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * Write a description of class Fenetre here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Fenetre extends JFrame
{
    public static int width=800;
    public static int height=800;
    private Panneau pan = new Panneau();
    public static int [][] grille;
    public static int lvl=1;
    public static long start=System.currentTimeMillis();
    public static Joueurs [] score;
    public static boolean end=false;

    public Fenetre()
    {
        this.setTitle("Evite"); 
        this.setSize(width,height); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        this.setLocationRelativeTo(null);
        this.setBackground(Color.BLUE);
        this.setContentPane(pan);

        grille= new int [width+10][height+10];
        start=System.currentTimeMillis();
        Panneau.min=-1;
        Panneau.first=0;
        end=false;
        for(int i=0;i<width+10;i++)
        {
            for(int j=0; j<height+10;j++)
            {
                if(i<5||i>width-5||j<5||j>height-5)
                    grille[i][j]=1;
            }
        }
        this.addKeyListener(new ClavierListener());
        this.setVisible(true);

        go();
        finish();

        SaveData.save(SaveFen.nom, Panneau.min, Panneau.sec);
        this.dispose();
    }

    public boolean testEnd()
    {
        int i=0;
        for(int j=0; j<Evite.nbj;j++)
        {
            if(Evite.guys[j].alife || Evite.guys[j].mourant)
                i++;
        }
        return i==0;
    }

    public void go()
    {   int I=0;
        int K=0;
        int LVL;
        while(!end)
        {
            end=testEnd();
            if(10+3*lvl>K|| I<10)
            {
                Evite.blocks.add(Evite.blocks.creeRandom(lvl));
                K=Evite.blocks.check();
            }
            I++;
            if(I>100-lvl*5)
                I=9;
            Evite.wait(20-lvl);
            repaint();
            LVL=lvl;
            lvl=(int)(System.currentTimeMillis()-start)/30000;
            if(LVL!=lvl)
            {
                if(lvl<4)
                    Panneau.cadre= new Color(0,0,210-50*lvl);
                else if(lvl<8)
                    Panneau.cadre= new Color(0,250-50*(lvl-4),0);
                else if(lvl<12)
                    Panneau.cadre= new Color(50*(lvl-8),0,0);
                else
                    Panneau.cadre= new Color(0,0,0);
            }
        }
    }

    public void finish()
    {
        Evite.wait(4000);
        if(Panneau.min==score[9].min && Panneau.sec>score[9].sec ||
        Panneau.min>score[9].min)
        {
            SaveFen s= new SaveFen();
        }
    }
    class ClavierListener implements KeyListener{
        private int nbt=0;
        public void keyPressed(KeyEvent e)
        {
            if(e.getKeyCode()==37)//g
            {
                Evite.guys[0].dir=8;
            }
            else if(e.getKeyCode()==38)//h
            {
                Evite.guys[0].dir=5;
            }
            else if(e.getKeyCode()==39)//d
            {
                Evite.guys[0].dir=6;
            }
            else if(e.getKeyCode()==40)//b
            {
                Evite.guys[0].dir=7;
            }

            if(e.getKeyChar()=='q')//g
            {
                Evite.guys[1].dir=8;
            }
            else if(e.getKeyChar()=='z')//h
            {
                Evite.guys[1].dir=5;
            }
            else if(e.getKeyChar()=='d')//d
            {
                Evite.guys[1].dir=6;
            }
            else if(e.getKeyChar()=='s')//b
            {
                Evite.guys[1].dir=7;
            }

        }

        public void keyReleased(KeyEvent e)
        {

        }

        public void keyTyped(KeyEvent e)
        {

        }
    }
}
