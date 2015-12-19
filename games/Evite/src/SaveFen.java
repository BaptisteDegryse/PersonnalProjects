import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
/**
 * Write a description of class SaveFen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SaveFen extends JFrame
{
    public static String nom="";
    private static JTextField nb;
    public static boolean ok1=false;
    public SaveFen()
    {
        this.setTitle("High Score !"); 
        this.setSize(300,100); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        this.setLocationRelativeTo(null);
        this.setBackground(Color.YELLOW);
        ok1=false;
        Font font= new Font("Courier", Font.BOLD, 20);

        JLabel label= new JLabel("Votre nom (sans espace)");
        label.setFont(font);
        
        nb=new JTextField("nom");
        nb.setPreferredSize(new Dimension(60,20));
        
        JButton ok= new JButton("Save");
        ok.setPreferredSize(new Dimension(50,20));
        ok.addActionListener(new ItemListener());
        
        JPanel container= new JPanel();
        container.setLayout(new BorderLayout());
        container.add(label, BorderLayout.NORTH);
        container.add(nb, BorderLayout.CENTER);
        container.add(ok, BorderLayout.SOUTH);
        
        this.setContentPane(container);
        
        this.setVisible(true);
        
        while(!ok1){Thread.yield();}
        this.dispose();
    }
    
    
    class ItemListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            nom=nb.getText();
            
            ok1=!ok1;
            
        }
    }
}
