import javax.swing.*;
import java.awt.event.*;
import java.util.*;
public class Mouse extends MouseAdapter
{
	// private variables
	private GameComponent comp;				
	private ArrayList<Wall> walls;
	private Keyboard keyboard;
	private int[][] grid;
	private int bluPortal;
	private int orgPortal;
	
	// constructor constructs MouseUser with list of walls, imagePanel, and keyboard
	public Mouse(GameComponent comp, Keyboard keyMove) {
		this.comp = comp;
		keyboard = keyMove;
		grid = new int[32][22];
		bluPortal = -1;
		orgPortal = -1;
	}
	// pre: wlls not null
	// post: sets list of walls to wlls
	public void setWalls(ArrayList<Wall> walls)
	{
		this.walls = walls;
		setGrid(walls);
	}
	
	// pre: none
	// post: places portal depending on which button clicked and if direction keys are pressed
	//       (left = blue portal, right = orange portal)
	public void mouseClicked(MouseEvent e)
    {
    	int mouseCode = e.getButton();
    	int mouseX = e.getX();
    	int mouseY = e.getY();
    	if (comp.contains(mouseX, mouseY)) {
    		System.out.println("click");
    		final boolean blue = mouseCode == MouseEvent.BUTTON1;
	    	double x = comp.getChellX() + 32;
	    	double y = comp.getChellY() + 32;
	    	final double slope = slope(x, y, mouseX, mouseY);
	    	final boolean right = x < mouseX;
	    	final boolean above = y < mouseY;
	    	final boolean smallInc = slope < 10;
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

    }
    // pre: none
    // post: checks if clicked spot is valid, portalable wall to place portal
    public boolean isValid(int x, int y, int wallX, int wallY){
    	if (x >= wallX && x < wallX + 32 && y >= wallY + 32 && y < wallY + 32)
			return true;
    	return false;
    }
    
    public double slope(double x1, double y1, int x2, int y2) {
    	return Math.abs((y2-y1) / (x2-x1));
    }
    
    public void setGrid(ArrayList<Wall> wl) {
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
    
    public double incX(boolean small, boolean right, double x) {
    	if (small) {
    		if (right) {return x + 1;}
    		else {return x - 1;}
    	} else {
    		if (right) {return x + 0.1;}
    		else {return x - 0.1;}
    	}
    }
    
    public double incY(boolean small, boolean above, double slope, double y) {
    	if (small) {
    		if (above) {return y + slope;}
    		else {return y - slope;}
    	} else {
    		if (above) {return y + slope / 10;}
    		else {return y - slope / 10;}
    	}
    }
}