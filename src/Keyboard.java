import javax.swing.*;

import java.awt.event.*;
import java.util.*;

public class Keyboard extends KeyAdapter {
	// private variables
	private GameComponent comp;					// the panel Chell moves around in
	private Mouse mouse;					   	// the mouse
	private long totalTime;							// keeps track of the time
	private int initialX, initialY;				// the initial x and y positions
	private int xPos, yPos;			// Chell's x and y position
	private ArrayList<Wall> walls;				// list of walls
	private double xVel, yVel;					// Chell's x and y velocities
	private final double GRAVITY = 0.05;		// gravity constant
	private final double FRICTION = 0.1;		// friction constant
	private final int TERMINALVEL = 25;			// Chell's terminal velocity
	private Boolean playing;					// whether or not game is complete
	
	// constructor constructs frame with walls, level number, and Chell's position (x,y) and velocity
    public Keyboard(Boolean playing) 
    {
    	xVel = 0;				// starts with 0 velocity
    	yVel = 0;
    	this.playing = playing;
    	totalTime = 0;
    }
    
    // MOVING IN GENERAL
    // pre: none
    // post: moves Chell with time, implementing gravity and friction when appropriate
    public void spaceTime()
    {
    	long lastTime = System.currentTimeMillis();
    	while (playing)										// keeps going until game is complete
    	{
    		long time = System.currentTimeMillis();
    		if (time - 10 > lastTime)							// increments every 10 milliseconds
    		{
    			totalTime += time - lastTime;
    			lastTime = time;
    			// resets 'previous' time
    			if (yVel + GRAVITY < TERMINALVEL)
	    			yVel += GRAVITY;							// adds gravity constant to vertical velocity
    			if (!isValid(xPos, yPos + 25))		// determines whether or not Chell is
    															// is on a surface and has friction
    			{
    				if (xVel > 0)
    				{
    					xVel -= FRICTION;
    					if (xVel < 0)
    						xVel = 0;
    				}
    				else if (xVel < 0)
    				{
    					xVel += FRICTION;
    					if (xVel > 0)
    						xVel = 0;
    				}
    			}
    			if (isValid((int)(xPos + xVel), yPos))	// gives Chell inertia
    				xPos += xVel;
    			else
    				xVel = 0;
    			if (isValid(xPos, (int)(yPos + yVel)))
    				yPos += yVel;
    			else
    				yVel = 0;
    			comp.updateImage(xPos, yPos);
    		}
    	}
    }
    // pre: none
    // post: moves with WADS, sets portal direction with 1234 and 
    //       sets direction key pressed to true
    public void keyPressed(KeyEvent e)
    {
    	System.out.println("key pressed");
    	int keyCode = e.getKeyCode();
    	switch(keyCode)
    	{
    		case KeyEvent.VK_A:											// A - moves left
    			if (isValid(xPos - 10, yPos))
    			{
	    			xPos -= 10;
	    			xVel -= 1;
	    			comp.setImage("Images/chell_left.gif");
	    			comp.updateImage(xPos, yPos);
    			}
    			break;
    		case KeyEvent.VK_W:											// W - jumps
    			if (!isValid(xPos, yPos + 25))
    			{
	    			if (isValid(xPos, yPos - 10))
	    			{
		    			yPos -= 10;
		    			yVel -= 2;
		    			comp.updateImage(xPos, yPos);
	    			}
    			}
    			break;
    		case KeyEvent.VK_S:											//S - moves down
    			if (isValid(xPos, yPos + 10))
    			{
	    			yPos += 10;
	    			yVel += 1;
	    			comp.updateImage(xPos, yPos);
    			}
    			break;
    		case KeyEvent.VK_D:											// D - moves right
    			if (isValid(xPos + 10, yPos))
    			{
	    			xPos += 10;
	    			xVel += 1;
	    			comp.setImage("Images/chell_right.gif");
	    			comp.updateImage(xPos, yPos);
    			}
    			break;
    		default:
    			//other key pressed: ignore
    			break;
    	}
    }
    
    // pre: none
    // post: resets direction key pressed to false
    public void keyReleased(KeyEvent e)
    {
    	System.out.println("key released");
    }
    
    // pre: walls is not null
    // post: returns whether or not certain potential position is valid
    public boolean isValid(int xPos, int yPos)
    {	
    	boolean answer = true;
		if (walls.size() != 0)
		{
    		for (int i = 0; i < walls.size(); i++)				// checks list of walls to see if there is a wall at position
    		{
    			Wall a = walls.get(i);
    			if (xPos + 54 > a.getX() && xPos + 10 < a.getX() + 32 &&
    				yPos + 58 > a.getY() && yPos + 6 < a.getY() + 32)
    			{
    				if (a instanceof Portal)
    				{
    					Portal b = (Portal)a;
    					throughPortal(b);	// moves through portal
    				}
    				else if (a instanceof Spike) // die if you touch
    				{
    					JOptionPane.showMessageDialog(null, "You died");
    					// reset to beginning of level
    					xPos = initialX;
    					yPos = initialY;
    					xVel = 0;
    					yVel = 0;
    				}
    				else if (a instanceof Door) // you win
    				{
    					/*if (level < 5)
    					{
    						nextLevel(level + 1, allLevel.getWalls(level + 1), allLevel.getStartX(level + 1), 
    							allLevel.getStartY(level + 1));
    					}
    					else
    					{
    						complete = true; // stops moveIt()
    						frame.dispose(); // closes frame
    						JOptionPane.showMessageDialog(null, "You win. You Monster.");
    					}*/
    				}
    				answer = false;
    			}
    		}
		}
    	return answer;
    }
    
    // MOVING BETWEEN PORTALS
    // pre: none
    // post: finds other portal and transfers Chell there, preserving velocity
    public void throughPortal(Portal here)
    {
    	boolean blue = here.isBlue();
    	int thisDir = here.getDirection();
    	Portal other = null;
    	for (Wall wall: walls)
    	{
    		if (wall instanceof Portal)
    		{
    			Portal portal = (Portal)wall;
    			if (portal.isBlue() != blue)
    				other = portal;
    		}
    	}
    	if (other != null)
    	{
			int nextDir = other.getDirection();
			int nextX = other.getX();
			int nextY = other.getY();
			doDirections(thisDir, nextDir, nextX, nextY);
    	}
    	comp.updateImage(xPos, yPos);	
    }
    // pre: none
    // post: updates position and velocity if valid portals
    public void doDirections (int thisDir, int nextDir, int x, int y){
    	boolean valid = true;
		if (nextDir == 1) {
			if (isValid(x - 16, y - 32)) {xPos = x - 16; yPos = y - 32;} 
			else if (isValid(x, y - 32)) {xPos = x; yPos = y - 32;} 
			else if (isValid(x - 32, y - 32)) {xPos = x - 32; yPos = y - 32;} 
			else {valid = false;}
		} else if (nextDir == 2) {
			if (isValid(x + 32, y - 16)) {xPos = x + 32; yPos = y - 16;} 
			else if (isValid(x + 32, y)) {xPos = x + 32; yPos = y;} 
			else if (isValid(x + 32, y - 32)) {xPos = x + 32; yPos = y - 32;} 
			else {valid = false;}
		} else if (nextDir == 3) {
			if (isValid(x - 16, y + 32)) {xPos = x - 16; yPos = y + 32;} 
			else if (isValid(x, y + 32)) {xPos = x; yPos = y + 32;} 
			else if (isValid(x - 32, y + 32)) {xPos = x - 32; yPos = y + 32;} 
			else {valid = false;}
		} else if (nextDir == 4) {
			if (isValid(x - 32, y - 16)) {xPos = x - 32; yPos = y - 16;} 
			else if (isValid(x - 32, y)) {xPos = x - 32; yPos = y;} 
			else if (isValid(x - 32, y - 32)) {xPos = x - 32; yPos = y - 32;} 
			else {valid = false;}
		} else
			valid = false;
		if (valid)
			doVelocity(thisDir, nextDir);
    }
    // pre: none
    // post: updates velocity to be appropriate direction
    public void doVelocity(int thisDir, int nextDir)
    {
		if (thisDir == nextDir)
		{
			xVel = 0 - xVel;
			yVel = 0 - yVel;
		}
		else if (thisDir + 1 == nextDir || thisDir - 3 == nextDir)
		{
			double temp = yVel;
			yVel = 0 - xVel;
			xVel = temp;
		}
		else if (thisDir - 1 == nextDir || thisDir + 3 == nextDir)
		{
			double temp = yVel;
			yVel = xVel;
			xVel = 0 - temp;
		}
    }
    
    public void setInitPos(int x, int y) {
    	initialX = x;
    	initialY = y;
    	xPos = x;
    	yPos = y;
    }
    
    public void setLevel(Level level) {
    	comp = level.getComponent();
    	walls = level.getWalls();
    	mouse.setWalls(walls);
    	initialX = level.getX();
    	initialY = level.getY();
    	xPos = initialX;
    	yPos = initialY;
    	playing = true;
    	spaceTime();
    }
    
    public void setMouse(Mouse mouse) {
    	this.mouse = mouse;
    }
    
    public long getTime() {
    	return totalTime;
    }
}