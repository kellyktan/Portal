import java.awt.*;

import javax.swing.ImageIcon;

// wall
public class Wall {
	
	// private variables
	private int x, y; 				// wall position
	private boolean portalable; 	// whether or not portal can be placed on it
	private int direction;			// direction portal can be made towards
									// 1 = up, 2 = right, 3 = down, 4 = left, 
									// 0 = not portalable
	
	// creates wall at x,y with specified portalability and direction
	public Wall(int x, int y, boolean portal, int dir) {
		this.x = x;
		this.y = y;
		portalable = portal;
		direction = dir;
	}
	
	// creates wall with specifications of portal it is replacing
	public Wall(Portal p) {
		x = p.getX();
		y = p.getY();
		portalable = true;
		direction = p.getDirection();
	}
	
	// returns x coordinate
	public int getX() {
		return x;
	}
	
	// returns y coordinate
	public int getY() {
		return y;
	}
	
	// returns portalability
	public boolean isPortalable() {
		return portalable;
	}
	
	// returns direction
	public int getDirection() {
		return direction;
	}
	
	// draws wall
	public void draw(Graphics g) {
		if (portalable)
			g.drawImage(new ImageIcon("Images/grey.jpg").getImage(), x, y, null);
		else
			g.drawImage(new ImageIcon("Images/black.jpg").getImage(), x, y, null);
	}
}