import javax.swing.*;
import java.awt.*;
/**
 * Write a description of class JPanel here.
 * 
 * @author Baptiste Degryse 
 * @version 03/01/2013
 */
public class Panneau extends JPanel
{
    public void paintComponent(Graphics g)
    {

        if(FatBoy.i<SaveMap.maps.size()&&FatBoy.i>=0)
        {
            
            //System.out.println((SaveMap.maps.get(FatBoy.i)).sp);
            if(!FatBoy.b.mourant)
            {
                FatBoy.b.move(g);
                FatBoy.b.draw(g);
            }
            else if(FatBoy.b.mourant && FatBoy.b.alife )
            {
                SaveMap.maps.get(FatBoy.i).draw(g);
                FatBoy.b.die(g,SaveMap.maps.get(FatBoy.i).sp.sPos);
                
            }
            
            //FatBoy.wait((int)(20-(System.currentTimeMillis()-t)));
            FatBoy.delta=0.3;
        }
        else
        {
            GradientPaint back=new GradientPaint(0,0,Color.cyan, 0,800, Color.ORANGE,true);
            Graphics2D g2d= (Graphics2D) g;
            g2d.setPaint(back);
            g2d.fillRect(0,0,1000,800);

            Font font = new Font(" TimesRoman ",Font.BOLD,50);
            g.setColor(Color.BLACK);
            g.setFont(font);

            g.drawString("Finish",100,400);
            g.drawString("Create your own map",100,450);
            g.drawString("with Editor (that's easy)",100,500);
            g.drawString("Press 'e' to Edit your map",100,550);
        }
    }
}
