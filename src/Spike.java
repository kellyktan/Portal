import java.awt.*;

import javax.swing.ImageIcon;

// spike
public class Spike extends Wall {
	
	// creates spike at x,y with specified direction
	public Spike(int x, int y, int dir) {
		super(x, y, false, dir);
	}
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(new ImageIcon("Images/spike_" + getDirection() + ".png").getImage(), 
			getX(), getY(), null);
	}
}