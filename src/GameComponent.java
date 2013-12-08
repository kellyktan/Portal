import java.awt.*;

import javax.swing.*;

import java.util.*;

@SuppressWarnings("serial")
public class GameComponent extends JComponent {
	//private variables
	private Image background; 				// the background image
  	private Image foreground; 				// moving foreground image
  	private int xPos, yPos; 		// x and y position of foreground image
  	private final int WIDTH = 1024; 			// window width (including actual window bars)
  	private final int HEIGHT = 704;			// window height (including actual window bars)
  											// frame dimensions: 512 x 768
  	private ArrayList<Wall> walls;			// list of walls

  	public GameComponent(Image img) {
		background = img;
		Dimension size = new Dimension(WIDTH, HEIGHT);	
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
	}

	public GameComponent(String img) {
  		this(new ImageIcon(img).getImage());	
  	}

  	@Override
  	public void paintComponent(Graphics g) {
    	g.drawImage(background, 0, 0, null); 
    	g.drawImage(foreground, xPos, yPos, null);
    	for (Wall a: walls) {
    		if (a instanceof Portal) {
    			Portal b = (Portal)a;
    			if (b.isBlue())
    				g.drawImage(new ImageIcon("Images/blue.gif").getImage(), a.getX(), a.getY(), null);
    			else
    				g.drawImage(new ImageIcon("Images/orange.gif").getImage(), a.getX(), a.getY(), null);
	
    		} else if (a instanceof Spike) {
    			int dir = a.getDirection();
    			g.drawImage(new ImageIcon("Images/spike_" + dir + ".png").getImage(), a.getX(), a.getY(), null);
    		} else if (a instanceof Door) {
    			g.drawImage(new ImageIcon("Images/door.jpg").getImage(), a.getX(), a.getY(), null);
    		} else {
    			if (a.isPortalable())
	    			g.drawImage(new ImageIcon("Images/grey.jpg").getImage(), a.getX(), a.getY(), null);
	    		else
	    			g.drawImage(new ImageIcon("Images/black.jpg").getImage(), a.getX(), a.getY(), null);
    		}
    	}
  	}

  	public void setWalls(ArrayList<Wall> walls) {
		this.walls = walls;
	}

	public void setImage(String image) {
		foreground = new ImageIcon(image).getImage();
		repaint();
	}

	public void updateImage() {
		repaint();
	}

	public void updateImage(int x, int y) {
  		xPos = x;
  		yPos = y;
  		repaint();
  	}
  	
  	public int getChellX() {
  		return xPos;
  	}
  	
  	public int getChellY() {
  		return yPos;
  	}
}