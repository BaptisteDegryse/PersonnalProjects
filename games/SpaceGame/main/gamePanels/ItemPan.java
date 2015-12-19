package gamePanels;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ItemPan extends JPanel{

	private static final long serialVersionUID = 1L;

	public ItemPan(JButton b,JLabel lab){
		setBackground(Color.black);
		setLayout(new GridLayout(2,1,5,5));
		lab.setForeground(Color.white);
		add(b);
		add(lab);
	}
}
