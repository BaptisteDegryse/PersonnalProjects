import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Write a description of class Options here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Options extends JFrame
{
    private JPanel container= new JPanel();
    public static boolean start=false;
    private JComboBox combo= new JComboBox();
    public static String j1T0,j1T1,j1T2,j2T0,j2T1,j2T2,j3T0,j3T1,j3T2,j4T0,j4T1,j4T2;
    public Options()
    {
      this.setTitle("Options"); 
      this.setSize(500, 400);
      this.setLocationRelativeTo(null); 
      this.setBackground(Color.ORANGE);
      
      this.setAlwaysOnTop(true);
      
      Font font = new Font("Courier", Font.BOLD, 16);
      
      JLabel label= new JLabel(" ======================================");
      JLabel label1= new JLabel("  Bienvenue sur SNAKE Version Baps !!!");
      JLabel label2= new JLabel(" ======================================");
      label1.setFont(font);
      
      JPanel top= new JPanel();
      top.setLayout(new BoxLayout(top,BoxLayout.PAGE_AXIS));
      top.add(label);
      top.add(label1);
      top.add(label2);
      
      combo.setPreferredSize(new Dimension(150,20));
      combo.addItem("2 Joueurs");
      combo.addItem("3 Joueurs");
      combo.addItem("4 Joueurs");
      combo.addItemListener( new ItemState());
      
      JComboBox vitesse= new JComboBox();
      vitesse.setPreferredSize(new Dimension(150,20));
      vitesse.addItem("lent");
      vitesse.addItem("moyen");
      vitesse.addItem("rapide");
      vitesse.addItemListener( new ItemState2());
    
      
      String [] tab={"1200 x 600", "1200 x 400", "400 x 400", "600 x 600", "400 x 750"};
      JComboBox fen= new JComboBox(tab);
      fen.setPreferredSize(new Dimension(150,20));
      fen.addItemListener(new FenState());
      
      Box center= Box.createVerticalBox();
      
      JLabel joueur1= new JLabel("Joueur 1 ");
      joueur1.setForeground(Color.RED);
      JLabel joueur2= new JLabel("Joueur 2 ");
      joueur2.setForeground(Color.GREEN);
      JLabel joueur3= new JLabel("Joueur 3 ");
      joueur3.setForeground(Color.cyan);
      JLabel joueur4= new JLabel("Joueur 4 ");
      joueur4.setForeground(Color.YELLOW);
      
      JTextField j1t0= new JTextField("fleche gauche");
      JTextField j1t1= new JTextField("fleche droite");
      JTextField j1t2= new JTextField("fleche haut");
      
      
      JTextField j2t0= new JTextField("q");
      JTextField j2t1= new JTextField("d");
      JTextField j2t2= new JTextField("z");
      
      JTextField j3t0= new JTextField("c");
      JTextField j3t1= new JTextField("v");
      JTextField j3t2= new JTextField("f");
      
      JTextField j4t0= new JTextField("i");
      JTextField j4t1= new JTextField("o");
      JTextField j4t2= new JTextField("9");
      
      
      
      Box b1= Box.createHorizontalBox();
      b1.add(joueur1);
      b1.add(j1t0);
      b1.add(j1t1);
      b1.add(j1t2);
      
      Box b2= Box.createHorizontalBox();
      b2.add(joueur2);
      b2.add(j2t0);
      b2.add(j2t1);
      b2.add(j2t2);
      
      Box b3= Box.createHorizontalBox();
      b3.add(joueur3);
      b3.add(j3t0);
      b3.add(j3t1);
      b3.add(j3t2);
      
      Box b4= Box.createHorizontalBox();
      b4.add(joueur4);
      b4.add(j4t0);
      b4.add(j4t1);
      b4.add(j4t2);
      
      center.add(b1);
      center.add(b2);
      center.add(b3);
      center.add(b4);
      
      Box west= Box.createVerticalBox();
      west.add(combo);
      west.add(fen);
      west.add(vitesse);
      
      JButton go= new JButton("GO !!!");
      go.addActionListener(new GOListener());
      
      container.setLayout(new BorderLayout());
      container.add(top, BorderLayout.NORTH);
      container.add(west, BorderLayout.WEST);
      container.add(go,BorderLayout.SOUTH);
      container.add(center, BorderLayout.CENTER);
      
     
      
      this.setContentPane(container);
      this.setVisible(true);
      while(!start)
      {
            go.addActionListener(new GOListener());
      }
      
      j1T0=j1t0.getText();
      j1T1=j1t1.getText();
      j1T2=j1t2.getText();
      
      j2T0=j2t0.getText();
      j2T1=j2t1.getText();
      j2T2=j2t2.getText();
      
      j3T0=j3t0.getText();
      j3T1=j3t1.getText();
      j3T2=j3t2.getText();
      
      j4T0=j4t0.getText();
      j4T1=j4t1.getText();
      j4T2=j4t2.getText();
      
      this.dispose();
    }
    public class ItemState implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
            if(e.getItem().equals("2 Joueurs"))
            Snake.nbJ=2;
            else if(e.getItem().equals("3 Joueurs"))
            Snake.nbJ=3;
            else if(e.getItem().equals("4 Joueurs"))
            Snake.nbJ=4;
            else{}
        }
    }
    public class ItemState2 implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
            if(e.getItem().equals("lent"))
            Snake.vitesse=60;
            else if(e.getItem().equals("moyen"))
            Snake.vitesse=45;
            else if(e.getItem().equals("rapide"))
            Snake.vitesse=30;
            else{}
        }
    }
    public class FenState implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
            if(e.getItem().equals("1200 x 400"))
            {
                Fenetre.WIDTH=1200;
                Fenetre.HEIGHT=400;
            }
            else if(e.getItem().equals( "400 x 400"))
            {
                Fenetre.WIDTH=400;
                Fenetre.HEIGHT=400;
            }
            else if(e.getItem().equals( "600 x 600"))
            {
                Fenetre.WIDTH=600;
                Fenetre.HEIGHT=600;
            }
            
            else if(e.getItem().equals( "400 x 750"))
            {
                Fenetre.WIDTH=400;
                Fenetre.HEIGHT=760;
            }
            else 
            {
                Fenetre.WIDTH=1200;
                Fenetre.HEIGHT=600;
            }
        }
    }
    public class GOListener implements ActionListener
    {
        public void actionPerformed(ActionEvent a)
        {
            start=true;
        }
    }
}
