import java.awt.*;

import javax.swing.*;

import java.util.*;

public class GameComponent extends JComponent {
	//private variables
	private Image background; 				// the background image
  	private Image foreground; 				// moving foreground image
  	private int xPos, yPos; 		// x and y position of foreground image
  	private final int WIDTH = 1024; 			// window width (including actual window bars)
  	private final int HEIGHT = 704;			// window height (including actual window bars)
  											// frame dimensions: 512 x 768
  	private ArrayList<Wall> walls;			// list of walls

	//Constructs a new ImagePanel with the background image specified by the file path given
  	public GameComponent(String img) {
  		this(new ImageIcon(img).getImage());	
  			//The easiest way to make images from file paths in Swing
  	}

	//Constructs a new ImagePanel with the background image given
  	public GameComponent(Image img) {
    	background = img;
    	Dimension size = new Dimension(WIDTH, HEIGHT);	
    		//Get the size of the image
    	//Thoroughly make the size of the panel equal to the size of the image
    	//(Various layout managers will try to mess with the size of things to fit everything)
    	setPreferredSize(size);
    	setMinimumSize(size);
    	setMaximumSize(size);
    	setSize(size);
  	}

	//This is called whenever the computer decides to repaint the window
	//It's a method in JPanel that I've overwritten to paint the background and foreground images
	// draws different types of walls
	// pre: walls is not null
  	public void paintComponent(Graphics g) {
  		//Paint the background with its upper left corner at the upper left corner of the panel
    	g.drawImage(background, 0, 0, null); 
    	//Paint the image in the foreground where it should go
    	g.drawImage(foreground, xPos, yPos, null);
    	for (Wall a: walls) {
    		if (a instanceof Portal) {
    			Portal b = (Portal)a;
    			if (b.isBlue())
	    			g.drawImage(new ImageIcon("Images/blue.png").getImage(), a.getX(), a.getY(), null);
    			else
    				g.drawImage(new ImageIcon("Images/orange.png").getImage(), a.getX(), a.getY(), null);
	
    		} else if (a instanceof Spike) {
    			g.drawImage(new ImageIcon("Images/spike.png").getImage(), a.getX(), a.getY(), null);
    		} else if (a instanceof Door) {
    			g.drawImage(new ImageIcon("Images/door.jpeg").getImage(), a.getX(), a.getY(), null);
    			g.drawImage(new ImageIcon("Images/door.jpeg").getImage(), a.getX(), a.getY() + 32, null);
    		} else {
    			if (a.isPortalable())
	    			g.drawImage(new ImageIcon("Images/grey.jpg").getImage(), a.getX(), a.getY(), null);
	    		else
	    			g.drawImage(new ImageIcon("Images/black.jpg").getImage(), a.getX(), a.getY(), null);
    		}
    	}
  	}

  	// sets walls with list of walls
  	public void setWalls(ArrayList<Wall> walls) {
  		this.walls = walls;
  	}
  	
  	//Sets the foreground image to display
  	public void setImage(String image) {
  		foreground = new ImageIcon(image).getImage();
  	}

  	//Updates the image's position
  	public void updateImage(int x, int y) {
  		xPos = x;
  		yPos = y;
  		repaint();
  	}
  	
  	public void updateImage() {
  		repaint();
  	}
  	
  	// pre: none
  	// post: returns Chell's position (x or y)
  	public int getChellX() {
  		return xPos;
  	}
  	
  	public int getChellY() {
  		return yPos;
  	}
  	
  	/*public boolean contains(int x, int y) {
  		if (x >= 0 && x < 1024 && y >= 0 && y < 704)
  			return true;
  		return false;
  	}*/
}