import java.awt.*;

import javax.swing.ImageIcon;

// portal
public class Portal extends Wall {
	
	// private variable
	private boolean blue; 		// two different colors, blue and orange
		
	// creates portal with same specifications as the wall it is replacing
	// and specified color
	public Portal(Wall w, boolean blu) {
		super (w.getX(), w.getY(), false, w.getDirection());
		blue = blu;
	}
	
	// returns portal color
	public boolean isBlue() {
		return blue;
	}
	
	@Override
	public void draw(Graphics g) {
		if (blue)
			g.drawImage(new ImageIcon("Images/blue.gif").getImage(), getX(), getY(), null);
		else
			g.drawImage(new ImageIcon("Images/orange.gif").getImage(), getX(), getY(), null);
	}
}