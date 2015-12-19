package items;

import java.awt.Graphics;

public interface Item {
	public String getID();
	public float[] getPosition();
	public float[] getSize();
	public void action();
	public void render(Graphics g);
	public void setSelect(boolean b);
	public void changeSelect();
	public void setCursorOn(boolean b);
	public boolean isSelected();
	public boolean equals(Object o);
}
