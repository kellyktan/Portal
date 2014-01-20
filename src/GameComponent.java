import java.awt.*;

import javax.swing.*;

import java.util.*;

@SuppressWarnings("serial")
// game component
public class GameComponent extends JComponent {
	//private variables
	private Image background; 				// the background image
  	private Image foreground; 				// Chell
  	private int xPos, yPos; 				// Chell's x and y position
  	private final int WIDTH = 1024; 		// component width
  	private final int HEIGHT = 704;			// component height
  	private ArrayList<Wall> walls;			// list of walls

  	// creates component with 'img' background
  	public GameComponent(Image img) {
		background = img;
		Dimension size = new Dimension(WIDTH, HEIGHT);	
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
	}

  	// creates component with 'img' background
	public GameComponent(String img) {
  		this(new ImageIcon(img).getImage());	
  	}

  	@Override
  	public void paintComponent(Graphics g) {
    	g.drawImage(background, 0, 0, null); 			// draws background first
    	g.drawImage(foreground, xPos, yPos, null);		// draws Chell
    	for (Wall a: walls) {							// draws Walls
    		a.draw(g);
    	}
  	}

  	// sets the list of walls
  	public void setWalls(ArrayList<Wall> walls) {
		this.walls = walls;
	}

  	// sets Chell's image
	public void setImage(String image) {
		foreground = new ImageIcon(image).getImage();
		repaint();
	}

	// updates image
	public void updateImage() {
		repaint();
	}

	// updates image with new coordinates for Chell
	public void updateImage(int x, int y) {
  		xPos = x;
  		yPos = y;
  		repaint();
  	}
  	
	// returns Chell's x position
  	public int getChellX() {
  		return xPos;
  	}
  	
  	// returns Chell's y position
  	public int getChellY() {
  		return yPos;
  	}
}