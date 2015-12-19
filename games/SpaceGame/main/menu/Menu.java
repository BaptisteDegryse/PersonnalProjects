package menu;

import gamePanels.BButton;
import gamePanels.BoardPanel;
import interfaces.Animation;
import items.Planet;
import items.Unit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import reseau.Reseau;


public class Menu extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	private BButton play = new BButton("Play");
	private BButton editor = new BButton("Editor");
	private BButton server= new BButton("Server");
	private JTextField ipAddress=new JTextField();
	private JLabel labIpAddress=new JLabel("Host IP address:");
	//private static Component parent;
	private static JFrame f;
	private static final int WIDTH = 600; 
	private static final int HEIGHT = 600; 
	public static void main(String args[]){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		f = new JFrame("SpaceGame");

		Menu m = new Menu();//f);
		f.getContentPane().add(m);
		
		f.setSize(WIDTH, HEIGHT);
		
		//f.pack();
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBackground(Color.blue);
		f.setVisible(true);
		
		BufferedImage img=null;
		try{
			img = ImageIO.read(new File("res/menuBack.jpg"));
			//img = ImageIO.read(new File("res/menuBack.jpg"));
		}
		catch(IOException e){
			e.printStackTrace();
		}
		Graphics g=f.getGraphics();
		if(img!=null)
			g.drawImage(img, 0, 100,WIDTH,HEIGHT-70,null);
		
		Unit.init();
		Planet.init();
		BoardPanel.init();
		Animation.init();

	}
	public Menu(){//Component parent){
		//Menu.parent = parent;

		play.addActionListener(this);
		editor.addActionListener(this);
		server.addActionListener(this);
		ipAddress.addActionListener(this);

		ipAddress.setPreferredSize(new Dimension(150, 30));
		ipAddress.setForeground(Color.BLUE);

		add(editor);
		add(play);
		add(labIpAddress);
		add(ipAddress);
		try {
			ipAddress.setText(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		add(server);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source=e.getSource();
		if(source==play){
			f.dispose();
			Reseau.ipAddress=ipAddress.getText();
			new Game();
		}
		if(source==editor){
			f.dispose();
			GameBoard.editor=true;
			new Game();
		}
		if(source==server){
			f.dispose();
			Reseau.serverBoo=true;
			new Game();
		}
	}
}
