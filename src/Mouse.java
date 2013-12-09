import java.awt.event.*;
import java.util.*;
import java.io.*;

import sun.audio.*;

// mouse manages portal placing
public class Mouse extends MouseAdapter {
	
	// private variables
	private GameComponent comp;				// component Chell moves around in	
	private ArrayList<Wall> walls;			// list of walls
	private int[][] grid;					// grid of mapped indexes of walls
	private int bluPortal;					// index of blue portal
	private int orgPortal;					// index of orange portal
	
	// creates mouse listener
	public Mouse(GameComponent comp, Keyboard keyMove) {
		this.comp = comp;			// sets component
		grid = new int[32][22];
		bluPortal = -1;				// no initial blue portal index
		orgPortal = -1;				// no initial orange portal index
	}
	
	// places portal on nearest wall following the slope from Chell to the
	// position clicked if wall is portalable and replaces other same-colored portal
	@Override
	public void mouseClicked(MouseEvent e)
	{
		try {
			
			// plays audio
			FileInputStream gun = new FileInputStream("Audio/Portal_Gun.wav");
			AudioPlayer.player.start(gun);
			
			// gets button properties
			int mouseCode = e.getButton();
			int mouseX = e.getX();
			int mouseY = e.getY();
			
			if (comp.contains(mouseX, mouseY)) {
				boolean blue = mouseCode == MouseEvent.BUTTON1;
				
				// gets slope from Chell to position clicked
		    	double x = comp.getChellX() + 32;
		    	double y = comp.getChellY() + 32;
		    	double slope = slope(x, y, mouseX, mouseY);
		    	
		    	// determines where in relation to Chell mouse was clicked
		    	boolean right = x < mouseX;
		    	boolean above = y < mouseY;
		    	boolean smallInc = slope < 10;
		    	
		    	// updates image appropriately
		    	if (right)
		    		comp.setImage("Images/chell_right.gif");
		    	else
		    		comp.setImage("Images/chell_left.gif");
		    	
		    	// finds the first wall hit along slope
		    	boolean hitWall = false;
		    	while (comp.contains((int)Math.round(x),(int)Math.round(y)) && !hitWall) {
		    		
		    		// checks index
		    		int gridX = (int) Math.round(x) / 32;
		    		int gridY = (int) Math.round(y) / 32;
		    		int index = grid[gridX][gridY];
		    		
		    		// if not -1, a wall has been hit
		    		if (index != -1) {
		    			hitWall = true;
		    			Wall w = walls.get(index);
		    			
		    			// if wall is portalable, places/replaces 
		    			// appropriately-colored portal
		    			if (w.isPortalable()) {
			    			if (blue && bluPortal != -1) {
			    				Portal replace = (Portal) walls.get(bluPortal);
			    				walls.set(bluPortal, new Wall(replace));
			    			} else if (!blue && orgPortal != -1) {
			    				Portal replace = (Portal) walls.get(orgPortal);
			    				walls.set(orgPortal, new Wall(replace));
			    			}
			    			// updates portal index
			    			walls.set(index, new Portal(w, blue));
			    			if (blue)
			    				bluPortal = index;
			    			else
			    				orgPortal = index;
		    			}
		    			
		    			// updates image
		    			comp.updateImage();
		    		}
		    		
		    		// next position along slope
					x = incX(smallInc, right, x);
					y = incY(smallInc, above, slope, y);
		    	}
			}
		} catch (IOException err) {
			System.out.println("Error occurred: " + err.getMessage());
		}	
	}
	
	// sets the wall list
	public void setWalls(ArrayList<Wall> walls)
	{
		bluPortal = -1;
		orgPortal = -1;
		this.walls = walls;
		setGrid(walls);
	}
	
	// maps indexes in grid, -1 if no index at that position
	private void setGrid(ArrayList<Wall> wl) {
		for (int x = 0; x < 32; x++) {
			for (int y = 0; y < 22; y++) {
				grid[x][y] = -1;
			}
		}
		for (int i = 0; i < wl.size(); i++) {
			Wall w = wl.get(i);
			grid[w.getX()/32][w.getY()/32] = i;
		}
	}
	
	// calculates slope
	private double slope(double x1, double y1, int x2, int y2) {
    	return Math.abs((y2-y1) / (x2-x1));
    }
    
	// increments x along slope
    private double incX(boolean small, boolean right, double x) {
    	if (small) {
    		if (right) {return x + 1;}
    		else {return x - 1;}
    	} else {
    		if (right) {return x + 0.1;}
    		else {return x - 0.1;}
    	}
    }
    
    // increments y along slope
    private double incY(boolean small, boolean above, double slope, double y) {
    	if (small) {
    		if (above) {return y + slope;}
    		else {return y - slope;}
    	} else {
    		if (above) {return y + slope / 10;}
    		else {return y - slope / 10;}
    	}
    }
    
    // returns appropriate portal index
    public int getPortal(boolean blue) {
    	if (blue)
    		return bluPortal;
    	else
    		return orgPortal;
    }
}