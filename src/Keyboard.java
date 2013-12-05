import javax.swing.*;

import java.awt.event.*;
import java.util.*;
import java.io.*;

import sun.audio.*;

public class Keyboard extends KeyAdapter {
	// private variables
	private Mouse mouse;					   	// the mouse
	private GameComponent comp;					// the panel Chell moves around in
	private JTextArea status;					// the status of the game
	private Integer lives;						// keeps track of Chell's remaining lives
	private long totalTime;						// keeps track of the time
	private Boolean playing;					// whether or not currently playing
	private Boolean pause;						// whether or not currently paused
	private final int TOTALLEVELS;				// total number of levels
	private int levelNum;						// keeps track of level
	private int initialX, initialY;				// the initial x and y positions
	private int xPos, yPos;						// Chell's x and y position
	private double xVel, yVel;					// Chell's x and y velocities
	private ArrayList<Wall> walls;				// list of walls
	private int[][] grid;						// grid mapping wall indexes
	private final double GRAVITY = 0.05;		// gravity constant
	private final double FRICTION = 0.1;		// friction constant
	private final int TERMINALVEL = 25;			// Chell's terminal velocity
	
	// constructor constructs frame with walls, level number, and Chell's position (x,y) and velocity
    public Keyboard(JTextArea status, int lives, int totalLevels) {
    	this.status = status;
    	this.lives = lives;
    	totalTime = 0;
    	playing = false;
    	pause = false;
    	TOTALLEVELS = totalLevels;
    	grid = new int[32][22];
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
		JOptionPane.showMessageDialog(null, "Test Chamber 0" + num, 
				"Aperture Science", JOptionPane.PLAIN_MESSAGE,
				new ImageIcon("Images/aperture.png"));
		start();
		spaceTime();
	}

	public void nextLevel() {
		try {
			Level level = new Level("Levels/Level " + levelNum + ".txt", this, comp);
	    	status.replaceRange("" + levelNum, 20, 21);
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
	    	JOptionPane.showMessageDialog(null, "Test Chamber 0" + levelNum,
	    			"Aperture Science", JOptionPane.PLAIN_MESSAGE,
					new ImageIcon("Images/aperture.png"));
	    	start();
	    	spaceTime();
		} catch (IOException e) {System.out.println(e.getMessage());}
	}

	// pre: none
	// post: moves with WADS, sets portal direction with 1234 and 
	//       sets direction key pressed to true
	@Override
	public void keyPressed(KeyEvent e) {
		if (!pause) {
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
	}

	// MOVING IN GENERAL
    // pre: none
    // post: moves Chell with time, implementing gravity and friction when appropriate
    private void spaceTime() {
    	long lastTime = System.currentTimeMillis();
    	while (playing)	{
    		// keeps going until game is complete
    		while (!pause) {
    			comp.requestFocusInWindow();
	    		long time = System.currentTimeMillis();
	     		if (time - 10 > lastTime) {
	    			// increments every 10 milliseconds
	     			totalTime += time - lastTime;
	     			String display = getTime(totalTime);
	     	    	status.replaceRange(display, 47, status.getText().length());
	    			lastTime = time;
	    			// resets 'previous' time
	    			if (yVel + GRAVITY <= TERMINALVEL)
	    				// adds gravity constant to vertical velocity
	    				yVel += GRAVITY;
	    			else
	    				yVel = TERMINALVEL;
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
	    			else {
	    				int check = (int) xVel;
	    				if (xVel >= 0) {
	    					while (!isValid(xPos + check, yPos) && check > 0)
	    						check--;
	    				} else {
	    					while (!isValid(xPos + check, yPos) && check < 0)
	    						check++;
	    				}
	    				xVel = 0;
	    				xPos += check;
	    			}
	    			if (isValid(xPos, (int)(yPos + yVel)))
	    				yPos += yVel;
	    			else {
	    				int check = (int) yVel;
	    				if (yVel >= 0) {
	    					while (!isValid(xPos, yPos + check) && check > 0)
	    						check--;
	    				} else if (yVel < 0) {
	    					while (!isValid(xPos, yPos + check) && check < 0)
	    						check++;
	    				}
	    				yVel = 0;
	    				yPos += check;
	    			}
	    			comp.updateImage(xPos, yPos);
	    		}
	    	}
    		lastTime = System.currentTimeMillis();
    	}
    }
    
    // pre: walls is not null
    // post: returns whether or not certain potential position is valid
    private boolean isValid(int x, int y) {	
    	boolean answer = true;
    	int lx = (x + 10) / 32;		// left x
    	int mx = (x + 32) / 32;		// middle x
    	int rx = (x + 54) / 32;		// right x
    	int ty = (y + 6) / 32;		// top y
    	int my = (y + 32) / 32;		// middle y
    	int by = (y + 58) / 32;		// bottom y
    	
    	if (lx < 0 || rx >= 32 || ty < 0 || by >= 22)
    		return false;

    	int[] check = {grid[lx][ty], grid[mx][ty], grid[rx][ty], 
    				   grid[lx][my], grid[rx][my], 
    				   grid[lx][by], grid[mx][by], grid[rx][by]};
  
    	for (int i = 0; i < check.length; i++) {
    		// Checks list of walls to see if there's a wall at that position
    		if (check[i] != -1) {
    			Wall a = walls.get(check[i]);
				if (a instanceof Portal) {
					Portal b = (Portal)a;
					return throughPortal(b);	// moves through portal
				} else if (a instanceof Spike) {
					// you died...
					comp.updateImage(x,y);
					lives--;
			    	status.replaceRange(lives.toString(), 34, 35);
			    	pause = true;
					try {
						FileInputStream ending = new FileInputStream("Audio/Death.wav");
						AudioPlayer.player.start(ending);
						JOptionPane.showMessageDialog(null, "You died\nLives:  " + 
								lives, "Aperture Science", JOptionPane.PLAIN_MESSAGE,
								new ImageIcon("Images/GLaDOS.png"));
					if (lives == 0)
						JOptionPane.showMessageDialog(null, "Game Over",
								"Aperture Science", JOptionPane.PLAIN_MESSAGE,
								new ImageIcon("Images/GLaDOS.png"));		
						AudioPlayer.player.stop(ending);
					} catch (IOException e) {
						System.out.println("Error occurred: " + e.getMessage());
					}
			    	end();
			    	return false;
				} else if (a instanceof Door) {
					// you completed this level
					xVel = 0;
					yVel = 0;
					pause = true;
					if (levelNum == TOTALLEVELS) {
						String time = getTime(totalTime);
						try {
							FileInputStream ending = new FileInputStream("Audio/Win_Long.wav");
							AudioPlayer.player.start(ending);
							JOptionPane.showMessageDialog(null, "You win.  You" +
								" Monster.\n\nLives:  " + lives + "\n\nTime:  " + 
								time, "Aperture Science", JOptionPane.PLAIN_MESSAGE,
								new ImageIcon("Images/GLaDOS.png"));
							AudioPlayer.player.stop(ending);
						} catch (IOException e) {
							System.out.println("Error occurred: " + e.getMessage());
						}
					}
					levelNum++;
					end();
					return false;
				}
				answer = false;
    		}
    	}
    	return answer;
    }
    
    // MOVING BETWEEN PORTALS
    // pre: none
    // post: finds other portal and transfers Chell there, preserving velocity
    private boolean throughPortal(Portal here) {
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
    private void doDirections (int thisDir, int nextDir, int x, int y){
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
    private void doVelocity(int thisDir, int nextDir) {
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
		if (xVel > 0)
			comp.setImage("Images/chell_right.gif");
		else if (xVel < 0)
			comp.setImage("Images/chell_left.gif");
    }
    
    public void setMouse(Mouse mouse) {
		this.mouse = mouse;
	}

	public int getLives() {
		return lives;
	}

	private String getTime(long time) {
		double seconds = time / 1000.0;
		int minutes = (int) seconds / 60;
		seconds = Math.round((seconds % 60) * 1000) / 1000.0;
		if (seconds >= 10) 
			return minutes + ":" + seconds;
		else
			return minutes + ":0" + seconds;
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

	public void setPause(boolean pause) {
    	this.pause = pause;
    }
    
    public int getLevel() {
    	return levelNum;
    }
    
    public void start() {
		pause = false;
		playing = true;
	}

	public void end() {
    	pause = true;
    	playing = false;
    }
}