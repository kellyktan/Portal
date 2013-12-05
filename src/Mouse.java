import java.awt.event.*;
import java.util.*;
import java.io.*;

import sun.audio.*;

public class Mouse extends MouseAdapter {
	// private variables
	private GameComponent comp;				
	private ArrayList<Wall> walls;
	private int[][] grid;
	private int bluPortal;
	private int orgPortal;
	
	// constructor constructs MouseUser with list of walls, imagePanel, and keyboard
	public Mouse(GameComponent comp, Keyboard keyMove) {
		this.comp = comp;
		grid = new int[32][22];
		bluPortal = -1;
		orgPortal = -1;
	}
	// pre: none
	// post: places portal depending on which button clicked and if direction keys are pressed
	//       (left = blue portal, right = orange portal)
	@Override
	public void mouseClicked(MouseEvent e)
	{
		try {
			FileInputStream intro = new FileInputStream("Audio/Portal_Gun.wav");
			AudioPlayer.player.start(intro);
			int mouseCode = e.getButton();
			int mouseX = e.getX();
			int mouseY = e.getY();
			if (comp.contains(mouseX, mouseY)) {
				boolean blue = mouseCode == MouseEvent.BUTTON1;
		    	double x = comp.getChellX() + 32;
		    	double y = comp.getChellY() + 32;
		    	double slope = slope(x, y, mouseX, mouseY);
		    	boolean right = x < mouseX;
		    	boolean above = y < mouseY;
		    	boolean smallInc = slope < 10;
		    	
		    	if (right)
		    		comp.setImage("Images/chell_right.gif");
		    	else
		    		comp.setImage("Images/chell_left.gif");
		    	
		    	boolean hitWall = false;
		    	while (comp.contains((int)Math.round(x),(int)Math.round(y)) && !hitWall) {
		    		int gridX = (int) Math.round(x) / 32;
		    		int gridY = (int) Math.round(y) / 32;
		    		int index = grid[gridX][gridY];
		    		if (index != -1) {
		    			hitWall = true;
		    			Wall w = walls.get(index);
		    			if (w.isPortalable()) {
			    			if (blue && bluPortal != -1) {
			    				Portal replace = (Portal) walls.get(bluPortal);
			    				walls.set(bluPortal, new Wall(replace));
			    			} else if (!blue && orgPortal != -1) {
			    				Portal replace = (Portal) walls.get(orgPortal);
			    				walls.set(orgPortal, new Wall(replace));
			    			}
			    			walls.set(index, new Portal(w, blue));
			    			if (blue)
			    				bluPortal = index;
			    			else
			    				orgPortal = index;
		    			}
		    			comp.updateImage();
		    		}
					x = incX(smallInc, right, x);
					y = incY(smallInc, above, slope, y);
		    	}
			}
		} catch (IOException err) {
			System.out.println("Error occurred: " + err.getMessage());
		}	
	}
	// pre: wlls not null
	// post: sets list of walls to wlls
	public void setWalls(ArrayList<Wall> walls)
	{
		bluPortal = -1;
		orgPortal = -1;
		this.walls = walls;
		setGrid(walls);
	}
	
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
	private double slope(double x1, double y1, int x2, int y2) {
    	return Math.abs((y2-y1) / (x2-x1));
    }
    
    private double incX(boolean small, boolean right, double x) {
    	if (small) {
    		if (right) {return x + 1;}
    		else {return x - 1;}
    	} else {
    		if (right) {return x + 0.1;}
    		else {return x - 0.1;}
    	}
    }
    
    private double incY(boolean small, boolean above, double slope, double y) {
    	if (small) {
    		if (above) {return y + slope;}
    		else {return y - slope;}
    	} else {
    		if (above) {return y + slope / 10;}
    		else {return y - slope / 10;}
    	}
    }
    
    public int getPortal(boolean blue) {
    	if (blue)
    		return bluPortal;
    	else
    		return orgPortal;
    }
}