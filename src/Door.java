import java.awt.*;

import javax.swing.ImageIcon;

// doors
public class Door extends Wall {
	// constructs non-portalable doors at (x,y)
	public Door(int x, int y) {
		super(x, y, false, 0);
	}
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(new ImageIcon("Images/door.jpg").getImage(), getX(), getY(), null);
	}
}