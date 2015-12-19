import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
/**
 * Write a description of class Editor here.
 * 
 * @author Baptiste Degryse 
 * @version 03/01/2013
 */
public class Editor extends JFrame
{
    public static ArrayList<int []> listeObj= new ArrayList<int []>();
    public static ArrayList<int []> listeBadBlock= new ArrayList<int []>();
    private static int [] bb;
    private static int [] bon;
    private int[] recd=new int[4];
    public static int [] sp= new int[]{100,100}, ep= new int []{600,600};
    private JPanel pan2= new Panel();
    private boolean ok=true;
    public static Editor ed;
    public static int tNum=1;
    public int [] rect;
    public int [] bdl;
    public int [] boutonRec;
    public static ArrayList<Color> c=new ArrayList<Color>();
    public static Color bon_c;
    public static String name="";
    public static boolean b_1,b_3,b_4,b_5=true, b_d,b_5x2,inutileAdd,b_6;
    public static int b_2=1, bon_=0;
    private JLabel label1= new JLabel("Editor");
    private JPanel container= new JPanel();
    private ArrayList<Integer> lines=new ArrayList<Integer>();
    public static ArrayList<String> text=new ArrayList<String>();

    private Bouton bd=new Bouton("Delete one");
    private Bouton bD=new Bouton("Delete all");
    private Bouton b= new Bouton("Color");
    private Bouton b1=new Bouton("Rectangle");
    private Bouton b6=new Bouton("Framework OFF");

    private JComboBox bad= new JComboBox();
    private JComboBox bonus= new JComboBox();
    private Bouton b3=new Bouton("Start");
    private Bouton b4=new Bouton("Finish");
    private Bouton b5= new Bouton("Quit and save");
    private Bouton b5x2= new Bouton("Replace last");
    private Bouton [] bAll= new Bouton[]{b3,b4,b,b1,b6,bd,bD,b5,b5x2};
    public JTextField nameJT= new JTextField("Title");
    public JPanel pan= new JPanel();

    private JTextArea comm=new JTextArea();
    private JScrollPane scroll=new JScrollPane(comm);
    private int lastCom=0;
    public static void main(String []args)
    {
        ed= new Editor();
    }

    public Editor()
    {
        this.setTitle("Editor"); 
        this.setSize(1250,820); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        this.setLocationRelativeTo(null);
        this.setLayout( new BorderLayout());
        this.getContentPane().add(label1,BorderLayout.NORTH);

        if(listeObj.size()!=0)
        {
            listeObj.remove(listeObj.size()-1);
            listeObj.remove(listeObj.size()-1);
        }
        else
            c.add(Color.GREEN);

        bad.addItem("BadBlock");
        bad.addItem("BadBall");
        bad.addItem("Spider");
        bad.addItem("Cannon");
        bad.addItem("Cannon2");
        bad.addItem("Mine");
        bad.addItem("Lateral");
        bad.addItem("Vertical");
        bad.addItemListener( new ItemState());

        bonus.addItem("Invincible");
        bonus.addItem("Teleporter");
        bonus.addItem("Jump");
        bonus.addItem("Button");
        bonus.addItem("Color");
        bonus.addItem("Text");
        bonus.addItemListener( new ItemState()
            {
                public void itemStateChanged(ItemEvent e)
                {
                    allFalse();
                    if(e.getItem().equals("Color"))
                    {
                        comment(3);
                        bon_=1;
                    }
                    else if(e.getItem().equals("Teleporter"))
                    {
                        comment(4);
                        bon_=2;
                    }
                    else if(e.getItem().equals("Jump"))
                    {
                        comment(6);
                        bon_=3;
                    }
                    else if(e.getItem().equals("Button"))
                    {
                        comment(5);
                        bon_=4;
                    }
                    else if(e.getItem().equals("Invincible"))
                    {
                        comment(11);
                        bon_=5;
                    }
                    else if(e.getItem().equals("Text"))
                    {
                        bon_=6;
                    }
                }
            }
        );

        JPanel boutons= new JPanel();
        boutons.setLayout(new GridLayout(bAll.length+4,1,10,15));
        Box box1 = Box.createVerticalBox();
        boutons.add(nameJT);

        for(int i=0; i<bAll.length-4;i++)
        {
            boutons.add(bAll[i]);
        }
        boutons.add(bad);
        boutons.add(bonus);
        for(int i=bAll.length-4; i<bAll.length;i++)
        {
            boutons.add(bAll[i]);
        }

        for(int i=0; i<bAll.length;i++)
        {
            bAll[i].addActionListener(new BListener());
            bAll[i].setPreferredSize(new Dimension(150,25));
        }
        nameJT.setPreferredSize(new Dimension(150,25));
        bonus.setPreferredSize(new Dimension(150,20));
        bad.setPreferredSize(new Dimension(150,20));

        pan.setLayout(new BorderLayout());
        pan.add(boutons,BorderLayout.NORTH);
        comm.setFont(new Font("Comic Sans MS",Font.ITALIC,15));
        pan.add(scroll, BorderLayout.CENTER);
        pan.setBackground(Color.GRAY);

        container.setBackground(Color.RED);
        container.setLayout(new BorderLayout());
        //container.add(pan, BorderLayout.EAST);
        JSplitPane split = new JSplitPane(  
                JSplitPane.HORIZONTAL_SPLIT,pan2,pan); 
        split.setDividerLocation(1000);
        container.add(label1,BorderLayout.NORTH);
        container.add(split,BorderLayout.CENTER);
        this.setContentPane(container);
        pan2.addMouseListener(new MouseL());

        pan2.addMouseMotionListener(new MouseMotionListener()
            {
                public void mouseDragged(MouseEvent e)
                {
                    if(b_1)
                    {
                        int x=e.getX();
                        int y=e.getY();
                        recd = new int[] {Math.min(rect[0],x), Math.min(rect[1],y),
                            Math.abs(rect[0]-x), Math.abs(rect[1]-y)};

                    }
                    else
                    {
                        recd = new int[4];
                    }
                }

                public void mouseMoved(MouseEvent e){}
            }
        );
        this.setVisible(true);

        StringBuffer buf= new StringBuffer();
        String line="-------------------------\n";
        buf.append("\n"+line);
        buf.append("Welcome to the Editor :D");
        buf.append("\nHere are some explanations...");

        comm.append(buf.toString());
        go();
    }

    public void go()
    {
        comment(0);
        while(b_5)
        {
            try{
                Thread.sleep(20);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            repaint();
        }
        repaint();
        if(b_6)
        {
            listeObj.add(new int[]{0,0,1000,10,1});
            listeObj.add(new int[]{0,0,10,800,1});
            listeObj.add(new int[]{990,0,10,800,1});
            listeObj.add(new int[]{0,730,1000,70,1});
        }
        int [] objSp= new int [] {sp[0],sp[1],0,1,0};
        int [] objEp= new int [] {ep[0],ep[1],0,2,0};
        listeObj.add(objSp);
        listeObj.add(objEp);

        b_5=true;
        if(!(nameJT.getText()).equals("Title"))
            name= nameJT.getText();
        SaveMap.save();
        this.dispose();

    }

    public void allFalse()
    {
        b_1=false;
        b_2=0;
        bon_=0;
        b_3=false;
        b_4=false;
        b_d=false;
    }
    class Panel extends JPanel
    {

        public void paintComponent(Graphics g)
        {

            GradientPaint gp=new GradientPaint(0,0,Color.cyan, 0,800, Color.ORANGE,true);
            Graphics2D g2d= (Graphics2D)g;
            g2d.setPaint(gp);
            g2d.fillRect(0,0,1000,800);

            boolean inutile=inutileAdd;
            Iterator<int[]> iter=listeObj.iterator();
            Iterator<Color> cIter= c.iterator();
            Iterator<String> sIter= text.iterator();
            Color nulle=cIter.next();
            while(iter.hasNext()&&(inutile==inutileAdd))
            {

                int []pos=iter.next();

                if(pos[4]==1)
                {
                    g.setColor(c.get(0));
                    g.fillRect(pos[0],pos[1],pos[2],pos[3]);
                }
                else if(pos[4]==2)
                {
                    if(pos[3]==1)
                    {
                        g.setColor(Color.BLACK);
                        g.fillRect(pos[0],pos[1]-20,20,20);
                        g.setColor(Color.RED);
                        g.drawOval(pos[0]+5,pos[1]-15,10,10);
                    }
                    else if(pos[3]==2)
                    {
                        g.setColor(Color.BLACK);
                        g.fillRect(pos[0]-10,pos[1]-10, 20,20);
                        g.setColor(Color.RED);
                        if(pos[2]!=1)
                            g.drawRect(pos[0]-7,pos[1]-2,14,4);
                        else
                            g.drawRect(pos[0]-2,pos[1]-7,4,14);
                    }
                    else if(pos[3]==4)
                    {
                        g.setColor(Color.BLACK);
                        g.drawLine(pos[0]-10,pos[1],pos[0]+10,pos[1]);
                        g.drawLine(pos[0],pos[1]-10,pos[0],pos[1]+10);
                        g.drawLine(pos[0]-10,pos[1]+10,pos[0]+10,pos[1]-10);
                        g.drawLine(pos[0]-10,pos[1]-10,pos[0]+10,pos[1]+10);

                        g.setColor(Color.RED);
                        g.fillOval(pos[0]-5,pos[1]-5,10,10);
                    }
                    else if(pos[3]==5)
                    {
                        g.setColor(Color.BLACK);
                        g.fillOval(pos[0]-10,pos[1]-10, 20,20);
                        g.setColor(Color.RED);
                        g.fillOval(pos[0]-5,pos[1]-5,10,10);
                    }
                    else if(pos[3]==6 || pos[3]==7)
                    {
                        if(pos[3]==6)
                            g.setColor(Color.WHITE);
                        else
                            g.setColor(Color.YELLOW);
                        g.fillOval(pos[0]-10,pos[1]-10,20,20);
                        if(!inutileAdd)
                        {
                            listeObj.add(0,new int[]{1000,1000,0,6,2});
                            inutileAdd=true;
                        }
                    }
                }
                else if(pos[4]==3)
                {
                    Font font = new Font("TimesRoman",Font.BOLD,10);
                    g.setFont(font);
                    if(pos[3]==1)
                    {
                        g.setColor(cIter.next());
                        g.drawOval(pos[0],pos[1],15,15);

                        g.setColor(Color.BLACK);
                        g.drawString("C",pos[0]+3,pos[1]+12);
                    }
                    else if(pos[3]==3)
                    {
                        g.setColor(Color.BLUE);
                        g.drawRect(pos[0],pos[1],15,15);
                        g.drawRect(pos[0]+2,pos[1]+2,11,11);
                        g.drawRect(pos[0]+4,pos[1]+4,7,7);
                    }
                    else if(pos[3]==4)
                    {

                        g.setColor(Color.WHITE);
                        g.fillRect(pos[5],pos[6],pos[7],pos[8]);

                        g.setColor(Color.BLUE);
                        g.fillRect(pos[0],pos[1],15,15);

                        if(pos[2]==-2 || pos[2]>200)
                            g.setColor(Color.GREEN);
                        else
                            g.setColor(Color.RED);

                        g.fillOval(pos[0]+2,pos[1]+2,11,11);

                        int n=-1;
                        if(pos[2]>200)
                            n=pos[2]-200;
                        else if(pos[2]>100)
                            n=pos[2]-100;

                        font = new Font("TimesRoman",Font.PLAIN,10);
                        g.setFont(font);
                        g.setColor(Color.BLACK);
                        if(n>=0)
                        {
                            g.drawString(""+n,pos[0]+5,pos[1]+12);
                            g.drawString(""+n,pos[5]+5,pos[6]+12);
                        }

                    }
                    else if(pos[3]==5)
                    {
                        g.setColor(Color.BLUE);
                        g.drawRect(pos[0],pos[1],15,15);

                        g.setColor(Color.RED);
                        g.drawOval(pos[0],pos[1],15,15);

                        g.drawString(""+pos[2]/1000,pos[0]+3,pos[1]+12);
                    }
                    else if(pos[3]==6)
                    {
                        if(pos[2]==1)
                            font = new Font("Comic Sans MS",Font.BOLD,14);
                        else if(pos[2]==2)
                            font = new Font("Comic Sans MS",Font.BOLD,20);
                        else
                            font = new Font("Comic Sans MS",Font.BOLD,30);
                        g.setColor(Color.BLACK);
                        g.setFont(font);
                        g.drawString(sIter.next(),pos[0],pos[1]);
                    }
                    else 
                    {
                        g.setColor(Color.WHITE);
                        g.drawOval(pos[0],pos[1],15,15);

                        g.setColor(Color.BLACK);
                        g.drawString("t"+pos[5],pos[0]+3,pos[1]+12);

                        g.drawOval(pos[2],pos[3],15,15);
                        g.drawString("t"+pos[5],pos[2]+3,pos[3]+12);
                    }
                }
                else if(pos.length>10)
                {
                    g.setColor(Color.BLACK);
                    for(int i=0; i<pos.length-4;i+=2)
                    {
                        if(pos[i]!=0&&pos[i+1]!=0&&pos[i+2]!=0 && pos[i+3]!=0)
                            g.drawLine(pos[i],pos[i+1],pos[i+2],pos[i+3]);
                    }
                    if(pos[pos.length-2]!=0&&pos[pos.length-1]!=0)
                        g.drawLine(pos[pos.length-2],pos[pos.length-1],pos[0],pos[1]);
                    g.setColor(Color.BLUE);
                    g.drawLine(pos[0]-10,pos[1],pos[0]+10,pos[1]+10);
                    g.drawLine(pos[0]-10,pos[1],pos[0]+10,pos[1]-10);
                    g.drawLine(pos[0]+10,pos[1],pos[0]-10,pos[1]+10);
                    g.drawLine(pos[0]+10,pos[1],pos[0]-10,pos[1]-10);
                    g.setColor(Color.BLACK);
                    g.fillOval(pos[0]-5,pos[1]-5,10,10);
                    g.setColor(Color.ORANGE);
                    g.drawOval(pos[0]-5,pos[1]-5,10,10);
                }
            }
            g.setColor(Color.RED);
            g.fillRect(sp[0],sp[1],40,30);

            g.setColor(Color.BLUE);
            g.fillRect(ep[0],ep[1],40,30);
            if(lines.size()>0)
            {
                g.setColor(Color.BLACK);
                for(int i=0; i<lines.size()-2;i+=2)
                {
                    g.drawLine(lines.get(i),lines.get(i+1),lines.get(i+2),lines.get(i+3));
                }

                g.setColor(Color.BLUE);
                g.drawLine(lines.get(0)-10,lines.get(1),lines.get(0)+10,lines.get(1)+10);
                g.drawLine(lines.get(0)-10,lines.get(1),lines.get(0)+10,lines.get(1)-10);
                g.drawLine(lines.get(0)+10,lines.get(1),lines.get(0)-10,lines.get(1)+10);
                g.drawLine(lines.get(0)+10,lines.get(1),lines.get(0)-10,lines.get(1)-10);
                g.setColor(Color.BLACK);
                g.fillOval(lines.get(0)-5,lines.get(1)-5,10,10);
                g.setColor(Color.ORANGE);
                g.drawOval(lines.get(0)-5,lines.get(1)-5,10,10);
            }

            if(b_6)
            {
                g.setColor(c.get(0));
                g.fillRect(0,0,1000,10);
                g.fillRect(0,0,10,800);
                g.fillRect(990,0,10,800);
                g.fillRect(0,730,1000,70);
            }

            if(bon_==45)
            {
                g.setColor(Color.BLUE);
                g.fillRect(bon[0],bon[1],15,15);

                g.setColor(Color.WHITE);
            }
            else
                g.setColor(c.get(0));
            g.fillRect(recd[0],recd[1],recd[2],recd[3]);
        }
    }
    class MouseL implements MouseListener
    {

        public void mouseClicked(MouseEvent event)
        {

        }

        public void mouseEntered(MouseEvent event)
        {
            JScrollBar vert=scroll.getVerticalScrollBar();
            vert.setValue(vert.getMaximum());
        }

        public void mouseExited(MouseEvent event)
        {

        }

        public void mousePressed(MouseEvent event)
        {
            if(b_1)
            {
                rect= new int [5];
                rect[4]=1;
                rect[0]=event.getX();
                rect[1]=event.getY();
            }
            else if(b_2!=0)
            {
                bb= new int[5];
                bb[4]=2;
                bb[3]=b_2;

                bb[0]=event.getX();
                bb[1]=event.getY();
                if(b_2==3)
                {
                    bb[3]=2;
                    bb[2]=1;
                }
            }

            else if(b_3)
            {
                sp[0]=event.getX();
                sp[1]=event.getY()-30;
            }
            else if(b_4)
            {
                ep[0]=event.getX();
                ep[1]=event.getY()-30;
            }
            else if(b_d)
            {
                bdl= new int[4];
                bdl[0]=event.getX();
                bdl[1]=event.getY();
            }
            else if(bon_!=0)
            {
                if(bon_==1)
                    c.add(JColorChooser.showDialog(null, "Changement de Color", Color.WHITE));
                bon= new int [9];
                bon[0]=event.getX();
                bon[1]=event.getY()-15;
                bon[4]=3;
                bon[3]=bon_;
                if(bon_==4)
                {
                    Object[] options={"Start ON","Start OFF"};
                    JPanel panel= new JPanel();
                    panel.add(new JLabel("Enter the number max of use, \"-1\" for ulimited"));
                    JTextField num= new JTextField("-1",2);
                    panel.add(num);
                    int result = JOptionPane.showOptionDialog(null, panel, "Button",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, options, null);
                    int number= Integer.parseInt(num.getText());
                    if(result==0)
                    {
                        if(number<0)
                            bon[2]=-2;
                        else
                            bon[2]=200+number;
                    }
                    else
                    {
                        if(number<0)
                            bon[2]=-1;
                        else
                            bon[2]=100+number;
                    }
                    b_1=true;
                }
                else if(bon_==5)
                {
                    Object[] options={"OK","Cancel"};
                    JPanel panel= new JPanel();
                    panel.add(new JLabel("Enter the time of invincibility (seconds)"));
                    JTextField num= new JTextField("1",3);
                    panel.add(num);
                    int result = JOptionPane.showOptionDialog(null, panel, "Invincibility",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, options, null);
                    if(result==0)
                        bon[2]=(int)(Double.parseDouble(num.getText())*1000);
                }
                else if(bon_==6)
                {
                    Object[] options={"Size 14","Size 20","Size 30"};
                    JPanel panel= new JPanel();
                    panel.add(new JLabel("Enter the text"));
                    JTextField num= new JTextField("",25);
                    panel.add(num);
                    int result = JOptionPane.showOptionDialog(null, panel, "Text",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, options, null);
                    bon[2]=result+1;

                    text.add(num.getText());
                }
                if(bon_!=2 && bon_!=4)
                    listeObj.add(bon);
            }
        }

        public void mouseReleased(MouseEvent event)
        {
            if(b_1)
            {
                int [] rec=new int[1];
                if(bon_==4)
                {
                    bon_=44;
                    rect=new int[4];
                }
                else
                {
                    rect[2]=event.getX();
                    rect[3]=event.getY();
                    rec = new int[] {Math.min(rect[0],rect[2]), Math.min(rect[1],rect[3]),
                        Math.abs(rect[0]-rect[2]), Math.abs(rect[1]-rect[3]),1};
                }
                if(bon_==0)
                    listeObj.add(rec);
                else if(bon_==45)
                {
                    bon[5]=rec[0];
                    bon[6]=rec[1];
                    bon[7]=rec[2];
                    bon[8]=rec[3];
                    listeObj.add(bon);
                    bon_=4;
                    b_1=false;
                }
                else
                {
                    bon_=45;
                }
                recd=new int[4];
            }
            else if(b_d)
            {
                bdl[2]=event.getX();
                bdl[3]=event.getY();
                int []rec = new int[] {Math.min(bdl[0],bdl[2]), Math.min(bdl[1],bdl[3]),
                        Math.abs(bdl[0]-bdl[2]), Math.abs(bdl[1]-bdl[3]),1};
                listeObj.add(rec);
                Iterator<int[]> iter=listeObj.iterator();
                while(iter.hasNext())
                {
                    int []j=iter.next();
                    if(j[4]==1)
                    {
                        if(Map.contains(rec[0],rec[1],rec[2]-rec[0],rec[3]-rec[1],j[0],j[1],j[2]-j[0],j[3]-j[1]))
                            iter.remove();
                    }
                    else if(j[4]==2)
                    {
                        if(Map.contains(rec[0],rec[1],rec[2]-rec[0],rec[3]-rec[1],j[0],j[1]-20,j[1]+20,j[1]))
                            iter.remove();
                    }
                }
            }
            else if(b_2!=0)
            {
                if(b_2==5)
                {
                    bb[2]=event.getX();
                    bb[2]-=bb[0];
                    bb[2]/=20;
                }
                if(b_2!=8)
                    listeObj.add(bb);
                else
                {
                    if(lines.size()-2>=0 && event.getX()==lines.get(lines.size()-2) && event.getY()==lines.get(lines.size()-1))
                    {   int[] pos;
                        if(lines.size()+2>10)
                            pos= new int[lines.size()+4];
                        else
                            pos=new int[11];
                        for(int i=0;i<lines.size();i++)
                        {
                            pos[i]=lines.get(i);
                        }
                        pos[lines.size()]=pos[0];
                        pos[lines.size()+1]=pos[1];
                        lines.clear();
                        listeObj.add(pos);
                    }
                    else
                    {
                        lines.add(bb[0]);
                        lines.add(bb[1]);
                    }
                }
            }
            else if(bon_==2)
            {
                bon[2]=event.getX();
                bon[3]=event.getY()-15;
                bon[5]=tNum;
                tNum++;
                listeObj.add(bon);
            }

        }
    }
    public class ItemState implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
            allFalse();
            if(e.getItem().equals("BadBlock"))
            {
                b_2=1;
                comment(2);
            }

            else if(e.getItem().equals("BadBall"))
            {
                b_2=5;
                comment(1);
            }

            else if(e.getItem().equals("Cannon"))
            {
                b_2=6;
                comment(7);
            }
            else if(e.getItem().equals("Cannon2"))
            {
                b_2=7;
                comment(8);
            }
            else if(e.getItem().equals("Mine"))
                b_2=4;
            else if(e.getItem().equals("Lateral"))
            {
                b_2=2;
                comment(9);
            }
            else if(e.getItem().equals("Vertical"))
            {
                b_2=3;
                comment(10);
            }
            else if(e.getItem().equals("Spider"))
            {
                b_2=8;
            }

        }
    }
    class BListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            Object s= e.getSource();
            if(s!=bd)
                allFalse();

            if(s==b)
            {
                c.add(0, JColorChooser.showDialog(null, "Color des blocs", Color.GREEN));
                c.remove(1);
            }
            else if(s==b1)
                b_1=true;
            else if(s==b3)
                b_3=true;
            else if(s==b4)
                b_4=true;
            else if(s==b5)
                b_5=false;
            else if(s==b6)
            {
                b_6=!b_6;
                if(b_6)
                    b6.setText("FrameWork ON");
                else
                    b6.setText("FrameWork OFF");
            }
            else if(s==b5x2)
            {  
                b_5x2=false;
                SaveMap.loadMaps();
                if(SaveMap.maps.size()>0)
                    SaveMap.maps.remove(SaveMap.maps.size()-1);
                b_5=false;
            }
            else if(s==bD)
            {
                listeObj= new ArrayList<int []>();
                sp= new int[]{100,100};
                ep= new int []{600,600};
                c.clear();
                text.clear();
                c.add(Color.GREEN);
                tNum=0;
            }
            else if(s==bd && listeObj.size()!=0)
            {
                if(lines.size()>0)
                    lines.clear();
                else if(listeObj.get(listeObj.size()-1)[3]==6)
                {
                    text.remove(text.size()-1);
                    listeObj.remove(listeObj.size()-1);
                }
                else
                    listeObj.remove(listeObj.size()-1);
            }

            repaint();
        }
    }
    public void comment(int b)
    {
        if(b!=lastCom)
        {
            StringBuffer buf= new StringBuffer();
            String line="-------------------------\n";
            buf.append("\n"+line);
            if(b==0)
            {
                buf.append("Welcome to the Editor :D");
                buf.append("\nHere are some explanations...");

            }
            else if(b==1)
            {
                buf.append("BadBall");
                buf.append("\n\nDid you know that if you");
                buf.append("\nmove your mouse horizontally");
                buf.append("\nwhile clicking, the ball");
                buf.append("\nwill have horizontal speed.");
            }
            else if(b==2)
            {
                buf.append("BadBlock");
                buf.append("\n\nThey stay on a block.");
                buf.append("\nIf they don't touch any blocks,");
                buf.append("\nthey will slowly move down");
                buf.append("\nand may stop in their way.");
            }
            else if(b==3)
            {
                buf.append("Color");
                buf.append("\n\nThis bonus changes the");
                buf.append("\nbody color of Fatboy.");
                buf.append("\nIt does nothing else...");
            }
            else if(b==4)
            {
                buf.append("Teleporter");
                buf.append("\n\nWorks only in one way.");
                buf.append("\nClick at the start point,");
                buf.append("\nkeep it down");
                buf.append("\nand let it go at the end point");
            }
            else if(b==5)
            {
                buf.append("Button");
                buf.append("\n\nClick once at the button place.");
                buf.append("\nThen, draw the Rectangle.");
                buf.append("\nON means that you can go through.");
            }
            else if(b==6)
            {
                buf.append("Jump");
                buf.append("\n\nClick once at the Bonus place.");
                buf.append("\nIt's unlimited Jump!.");
            }
            else if(b==7)
            {
                buf.append("Cannon");
                buf.append("\n\nClick once at the Cannon place.");
                buf.append("\nThe bullets go trough everything.");
            }
            else if(b==8)
            {
                buf.append("Cannon2");
                buf.append("\n\nClick once at the Cannon place.");
                buf.append("\nThe bullets bounce twice.");
            }  
            else if(b==9)
            {
                buf.append("Lateral");
                buf.append("\n\nClick once where you want it");
                buf.append("\nIt starts by going to the right.");
            }
            else if(b==10)
            {
                buf.append("Vertical");
                buf.append("\n\nClick once where you want it");
                buf.append("\nIt starts downwards.");
            }
            else if(b==11)
            {
                buf.append("Invincible");
                buf.append("\n\nClick once where you want it");
                buf.append("\nThen enter the time (in sec).");
            }
            comm.append(buf.toString());
        }
        lastCom=b;
    }
}
