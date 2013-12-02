import javax.swing.*;

import java.awt.event.*;
import java.util.*;
import java.io.*;

public class Keyboard extends KeyAdapter {
	// private variables
	private GameComponent comp;					// the panel Chell moves around in
	private Mouse mouse;					   	// the mouse
	private JTextArea status;
	private long totalTime;						// keeps track of the time
	private int levelNum;						// keeps track of level
	private int initialX, initialY;				// the initial x and y positions
	private int xPos, yPos;						// Chell's x and y position
	private ArrayList<Wall> walls;				// list of walls
	private int[][] grid;						// grid mapping wall indexes
	private double xVel, yVel;					// Chell's x and y velocities
	private final double GRAVITY = 0.05;		// gravity constant
	private final double FRICTION = 0.1;		// friction constant
	private final int TERMINALVEL = 25;			// Chell's terminal velocity
	private Boolean playing;					// whether or not currently playing
	private Integer lives;						// keeps track of Chell's remaining lives
	
	// constructor constructs frame with walls, level number, and Chell's position (x,y) and velocity
    public Keyboard(JTextArea status, Integer lives) {
    	playing = false;
    	this.status = status;
    	totalTime = 0;
    	grid = new int[32][22];
    	this.lives = lives;
    }
    
    // MOVING IN GENERAL
    // pre: none
    // post: moves Chell with time, implementing gravity and friction when appropriate
    public void spaceTime() {
    	long lastTime = System.currentTimeMillis();
    	while (playing)	{
    		// keeps going until game is complete
    		long time = System.currentTimeMillis();
     		if (time - 10 > lastTime) {
    			// increments every 10 milliseconds
     			totalTime += time - lastTime;
    			updateStatus(totalTime);
    			lastTime = time;
    			// resets 'previous' time
    			if (yVel + GRAVITY < TERMINALVEL)
    				// adds gravity constant to vertical velocity
    				yVel += GRAVITY;
    			if (!isValid(xPos, yPos + 25)) {
    				// determines whether or not Chell is on a surface/has friction
    				if (xVel > 0) {
    					xVel -= FRICTION;
    					if (xVel < 0)
    						xVel = 0;
    				} else if (xVel < 0) {
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
    public void keyPressed(KeyEvent e) {
    	int keyCode = e.getKeyCode();
    	switch(keyCode) {
    		case KeyEvent.VK_A:											// A - moves left
	    		comp.setImage("Images/chell_left.gif");
    			if (isValid(xPos - 10, yPos)) {
	    			xPos -= 10;
	    			xVel -= 1;
	    			comp.updateImage(xPos, yPos);
    			} break;
    		case KeyEvent.VK_W:											// W - jumps
    			if (!isValid(xPos, yPos + 25)) {
	    			if (isValid(xPos, yPos - 10)) {
		    			yPos -= 10;
		    			yVel -= 2;
		    			comp.updateImage(xPos, yPos);
	    			}
    			} break;
    		case KeyEvent.VK_S:											//S - moves down
    			if (isValid(xPos, yPos + 10)) {
	    			yPos += 10;
	    			yVel += 1;
	    			comp.updateImage(xPos, yPos);
    			} break;
    		case KeyEvent.VK_D:											// D - moves right
	    		comp.setImage("Images/chell_right.gif");
    			if (isValid(xPos + 10, yPos)) {
	    			xPos += 10;
	    			xVel += 1;
	    			comp.updateImage(xPos, yPos);
    			} break;
    		default:
    			//other key pressed: ignore
    			break;
    	}
    }
    
    // pre: walls is not null
    // post: returns whether or not certain potential position is valid
    public boolean isValid(int x, int y) {	
    	boolean answer = true;
    	int rx = (x + 54) / 32;		// right x
    	int mx = (x + 32) / 32;		// middle x
    	int lx = (x + 10) / 32;		// left x
    	int ty = (y + 6) / 32;		// top y
    	int my = (y + 32) / 32;		// middle y
    	int by = (y + 58) / 32;		// bottom y
    	int[] check = {grid[mx][ty], grid[rx][my], grid[mx][by], grid[lx][my]};
  
    	for (int i = 0; i < 4; i++) {
    		// Checks list of walls to see if there's a wall at that position
    		if (check[i] != -1) {
    			Wall a = walls.get(check[i]);
				if (a instanceof Portal) {
					Portal b = (Portal)a;
					return throughPortal(b);	// moves through portal
				} else if (a instanceof Spike) {
					// you died...
					lives--;
			    	status.replaceRange(lives.toString(), 32, 33);
			    	playing = false;
					JOptionPane.showMessageDialog(null, "You died");
					if (lives == 0)
						JOptionPane.showMessageDialog(null, "Game Over");		
				} else if (a instanceof Door) {
					// you completed this level
					levelNum++;
					playing = false;
					if (levelNum == 3)
						JOptionPane.showMessageDialog(null, "You win. You" +
							" Monster.", "Aperture Science", JOptionPane.PLAIN_MESSAGE);
				}
				answer = false;
    		}
    	}
    	return answer;
    }
    
    // MOVING BETWEEN PORTALS
    // pre: none
    // post: finds other portal and transfers Chell there, preserving velocity
    public boolean throughPortal(Portal here) {
    	boolean blue = here.isBlue();
    	int thisDir = here.getDirection();
    	int otherInd = mouse.getPortal(!blue);
    	if (otherInd != -1) {
	    	Portal other = (Portal) walls.get(otherInd);
			int nextDir = other.getDirection();
			int nextX = other.getX();
			int nextY = other.getY();
			doDirections(thisDir, nextDir, nextX, nextY);
	    	comp.updateImage(xPos, yPos);
	    	return true;
	    } return false;
    }
    // pre: none
    // post: updates position and velocity if valid portals
    public void doDirections (int thisDir, int nextDir, int x, int y){
    	boolean valid = true;
		if (nextDir == 1) {
			if (isValid(x - 16, y - 64)) {xPos = x - 16; yPos = y - 64;} 
			else if (isValid(x, y - 64)) {xPos = x; yPos = y - 64;} 
			else if (isValid(x - 32, y - 64)) {xPos = x - 32; yPos = y - 64;} 
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
			if (isValid(x - 64, y - 16)) {xPos = x - 64; yPos = y - 16;} 
			else if (isValid(x - 64, y)) {xPos = x - 64; yPos = y;} 
			else if (isValid(x - 64, y - 32)) {xPos = x - 64; yPos = y - 32;} 
			else {valid = false;}
		} else
			valid = false;
		if (valid)
			doVelocity(thisDir, nextDir);
    }
    // pre: none
    // post: updates velocity to be appropriate direction
    public void doVelocity(int thisDir, int nextDir) {
		if (thisDir == nextDir) {
			xVel = 0 - xVel;
			yVel = 0 - yVel;
		} else if (thisDir + 1 == nextDir || thisDir - 3 == nextDir) {
			double temp = yVel;
			yVel = 0 - xVel;
			xVel = temp;
		} else if (thisDir - 1 == nextDir || thisDir + 3 == nextDir) {
			double temp = yVel;
			yVel = xVel;
			xVel = 0 - temp;
		}
    }
    
    public void nextLevel() {
    	try {
    		Level level = new Level("Levels/Level " + levelNum + ".txt", this, comp);
	    	status.replaceRange("" + levelNum, 18, 19);
	    	walls = level.getWalls();
	    	setGrid(walls);
	    	mouse.setWalls(walls);
	    	initialX = level.getX();
	    	initialY = level.getY();
	    	xPos = initialX;
	    	yPos = initialY;
	    	xVel = 0;
	    	yVel = 0;
	    	comp.updateImage(xPos,yPos);
        	JOptionPane.showMessageDialog(null, "Test Chamber 0" + levelNum);
        	playing = true;
        	spaceTime();
    	} catch (IOException e) {System.out.println(e.getMessage());}
    }
    
    public void startLevel(Level level, int num) {
    	levelNum = num;
    	comp = level.getComponent();
    	walls = level.getWalls();
    	setGrid(walls);
    	mouse.setWalls(walls);
    	initialX = level.getX();
    	initialY = level.getY();
    	xPos = initialX;
    	yPos = initialY;
    	xVel = 0;
    	yVel = 0;
    	comp.updateImage(xPos,yPos);
    	JOptionPane.showMessageDialog(null, "Test Chamber 0" + num);
    	playing = true;
    	spaceTime();
    }
    
    public void setMouse(Mouse mouse) {
    	this.mouse = mouse;
    }
    
    public long getTime() {
    	return totalTime;
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
    
    public void updateStatus(long time) {
    	double seconds = time / 1000.0;
    	int minutes = (int) seconds / 60;
    	seconds = Math.round((seconds % 60) * 1000) / 1000.0;
    	/*if (seconds >= 60) {
    		minutes++;
    		seconds = seconds % 60;
    	}*/
    	String display = "";
    	if (seconds >= 10) 
    		display = minutes + ":" + seconds;
    	else
    		display = minutes + ":0" + seconds;
    	status.replaceRange(display, 45, status.getText().length());
    }
    
    public int getLevel() {
    	return levelNum;
    }
}