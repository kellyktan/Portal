import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class Keyboard implements KeyListener {
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
	private boolean dirPressed;					// whether or not the direction keys are pressed (1, 2, 3, 4)
	private int direction;						// direction (1, 2, 3, 4)
	private Boolean playing;					// whether or not game is complete
	
	// constructor constructs frame with walls, level number, and Chell's position (x,y) and velocity
    public Keyboard(Boolean playing) 
    {
    	xVel = 0;				// starts with 0 velocity
    	yVel = 0;
    	this.playing = playing;
    }

    /*// pre: valid parameters (lvl is valid level number, wlls is not null, x and y are correct)
    // post: resets the imagePanel and other components with new walls/positions
    public void nextLevel(int lvl, ArrayList<Wall> wlls, int x, int y)
    {
    	frame.setVisible(false);
    	// resets the walls
    	level = lvl;
    	walls = wlls;
    	mouse.setWalls(wlls);
    	imagePanel.setWalls(wlls);
    	// resets Chell's position and velocity
    	initialX = x;
    	initialY = y;
    	xPosition = x;
    	yPosition = y;
    	xVel = 0;
    	yVel = 0;
    	// updates the image with new level
    	imagePanel.updateImage(xPosition, yPosition);
    	JOptionPane.showMessageDialog(null, "Test Chamber " + level);
    	frame.setVisible(true);
    }*/
    
    // MOVING IN GENERAL
    // pre: none
    // post: moves Chell with time, implementing gravity and friction when appropriate
    public void spaceTime()
    {
    	totalTime = 0;
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
	    			comp.updateImage(xPos, yPos);
    			}
    			break;
    		case KeyEvent.VK_1:	 										// 1 - up direction
    			dirPressed = true;
    			direction = 1;
    			break;
    		case KeyEvent.VK_2: 										// 2 - right direction
    			dirPressed = true;
    			direction = 2;
    			break;
    		case KeyEvent.VK_3:											// 3 - down direction
    			dirPressed = true;
    			direction = 3;
    			break;
    		case KeyEvent.VK_4:											// 4 - left direction
    			dirPressed = true;
    			direction = 4;
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
    	dirPressed = false;
    	System.out.println("key released");
    }
    
    // pre: none
    // post: does nothing (needed to be implemented)
    public void keyTyped(KeyEvent e)
    {
    	System.out.println("key typed");
    }
    // pre: walls is not null
    // post: returns whether or not certain potential position is valid
    public boolean isValid(int xPos, int yPos)
    {
		if (walls.size() != 0)
		{
    		for (Wall a: walls)				// checks list of walls to see if there is a wall at position
    		{
    			if (xPos + 20 > a.getX() && xPos + 5 < a.getX() + 25 &&
    				yPos + 25 > a.getY() && yPos < a.getY() + 25)
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
    				return false;
    			}
    		}
		}
    	return true;
    }
    
    // MOVING BETWEEN PORTALS
    // pre: none
    // post: finds other portal and transfers Chell there, preserving velocity
    public void throughPortal(Portal here)
    {
    	boolean blue = here.isBlue();
    	int thisDirection = here.getDirection();
    	Portal other = null;
    	for (Wall wall: walls)
    	{
    		if (wall instanceof Portal)
    		{
    			Portal port = (Portal)wall;
    			if (port.isBlue() != blue)
    				other = port;
    		}
    	}
    	if (other != null)
    	{
			int direction = other.getDirection();
			int othX = other.getX();
			int othY = other.getY();
			doDirections(thisDirection, direction, othX, othY);
    	}
    	comp.updateImage(xPos, yPos);	
    }
    // pre: none
    // post: updates position and velocity if valid portals
    public void doDirections (int thisDirection, int nextDirection, int x, int y)
    {
		if (nextDirection == 1 && isValid(x, y - 25))
		{
			xPos = x;
			yPos = y - 25;
			doVelocity(thisDirection, nextDirection);
		}
		else if (nextDirection == 2 && isValid(x + 25, y))
		{
			xPos = x + 25;
			yPos = y;
			doVelocity(thisDirection, nextDirection);
		}
		else if (nextDirection == 3 && isValid(x, y + 25))
		{
			xPos = x;
			yPos = y + 25;
			doVelocity(thisDirection, nextDirection);
		}
		else if (nextDirection == 4 && isValid(x - 25, y))
		{
			xPos = x - 25;
			yPos = y;
			doVelocity(thisDirection, nextDirection);
		}
    }
    // pre: none
    // post: updates velocity to be appropriate direction
    public void doVelocity(int thisDirection, int nextDirection)
    {
		if (thisDirection == nextDirection)
		{
			xVel = 0 - xVel;
			yVel = 0 - yVel;
		}
		else if (thisDirection + 1 == nextDirection || thisDirection - 3 == nextDirection)
		{
			double hold = yVel;
			yVel = 0 - xVel;
			xVel = hold;
		}
		else if (thisDirection - 1 == nextDirection || thisDirection + 3 == nextDirection)
		{
			double hold = yVel;
			yVel = xVel;
			xVel = 0 - hold;
		}
    }
    // pre: none
    // post: returns if direction key for portal is pressed
    public boolean dirPressed()
    {
    	return dirPressed;
    }
    // pre: none
    // post: returns which direction key for portal is pressed
    public int getDirection()
    {
    	return direction;
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
    	initialX = level.getX();
    	initialY = level.getY();
    	xPos = initialX;
    	yPos = initialY;
    	playing = true;
    	spaceTime();
    }
}