import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * Write a description of class Bouton here.
 * 
 * @author Baptiste Degryse 
 * @version 03/01/2013
 */
public class Bouton extends JButton implements MouseListener
{
    private String name;
    
    private GradientPaint gp;
    public Bouton (String name)
    {
        super(name);
        this.name=name;
        gp=new GradientPaint(0,0,Color.GRAY, 0,this.getHeight(), Color.RED,true);
        repaint();
        this.addMouseListener(this);
        
    }
    
    public void setText(String n)
    {
        name=n;
    }
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setPaint(gp);
        g2d.fillRect(10,0,this.getWidth()+50,this.getHeight()-5);
        g2d.setColor(Color.WHITE);
        g2d.drawString(this.name,this.getWidth()/2-(this.getWidth()/3),this.getHeight()/2+((this.getHeight()-5)/4));
    }
    
    public void mouseClicked(MouseEvent event)
    {
        
    }
    public void mouseEntered(MouseEvent event)
    {
        gp=new GradientPaint(0,0,Color.GREEN, 0,this.getHeight(), Color.RED,true);
    }
    public void mouseExited(MouseEvent event)
    {
        gp=new GradientPaint(0,0,Color.GRAY, 0,this.getHeight(), Color.RED,true);
    }
    public void mousePressed(MouseEvent event)
    {
        gp=new GradientPaint(0,0,Color.BLACK, 0,this.getHeight(), Color.RED,true);
    }
    public void mouseReleased(MouseEvent event)
    {
        if(event.getX()>0 && event.getX()<this.getWidth() && event.getY()>0 && event.getY()<this.getHeight())
        gp=new GradientPaint(0,0,Color.BLACK, 0,this.getHeight(), Color.RED,true);
        else
        gp=new GradientPaint(0,0,Color.GRAY, 0,this.getHeight(), Color.RED,true);
    }
}
