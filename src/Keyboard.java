import javax.swing.*;

import java.awt.event.*;
import java.util.*;
import java.io.*;

import sun.audio.*;

// keyboard manages physics properties (gravity, terminal velocity, etc.) and moves Chell
public class Keyboard extends KeyAdapter {
	
	// private variables
	private Mouse mouse;					   	// the mouse
	private GameComponent comp;					// the component Chell moves around in
	private JTextArea status;					// the status of the game
	private AudioStream music;					// the background music
	private Integer lives;						// keeps track of Chell's remaining lives
	private long totalTime;						// keeps track of the time
	private Boolean playing;					// whether or not currently playing
	private Boolean pause;						// whether or not currently paused
	private int levelNum;						// keeps track of level #
	private int initialX, initialY;				// Chell's initial x and y positions
	private int xPos, yPos;						// Chell's x and y position
	private double xVel, yVel;					// Chell's x and y velocities
	private ArrayList<Wall> walls;				// list of walls
	private int[][] grid;						// grid mapping wall indexes
	private final double GRAVITY = 0.05;		// gravity constant
	private final double FRICTION = 0.1;		// friction constant
	private final double TERMINALVELY = 25;		// Chell's vertical terminal velocity
	private final double TERMINALVELX = 3;		// Chell's horizontal terminal velocity
	
	// creates keyboard listener
    public Keyboard(JTextArea status, int lives) {
    	this.status = status;
    	this.lives = lives;			// initial lives
    	totalTime = 0;
    	playing = false;			// not playing when created
    	pause = false;				// paused when created
    	grid = new int[32][22];		// creates array for wall indexes
    	// sets background music
    	try {
    		music = new AudioStream(new FileInputStream("Audio/background.wav"));
    	} catch (IOException e) {
    		System.out.println("Error Occurred: " + e.getMessage());
    	}
    }
    
    // initializes starting level
    public void startLevel(Level level, int num) {
		levelNum = num;
		comp = level.getComponent();
		walls = level.getWalls();		// sets the list of walls appropriately
		setGrid(walls);					// fills grid appropriately
		mouse.setWalls(walls);			// passes on the wall list to the mouse
		// Chell's initial x and y position for this level
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
	}

    // initializes next level (or same if levelNum is the same)
	public void nextLevel() {
		try {
			// initializes level
			Level level = new Level("Levels/Level " + levelNum + ".txt", comp);
	    	status.replaceRange("" + levelNum, 20, 21);		// updates status
	    	walls = level.getWalls();						// sets wall list
	    	setGrid(walls);									// and grid
	    	mouse.setWalls(walls);							// and passes it on
	    	// new initial positions for Chell because new level
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
		} catch (IOException e) {System.out.println(e.getMessage());}
	}

	// WASD moving controls for Chell
	@Override
	public void keyPressed(KeyEvent e) {
		comp.requestFocus();
		if (!pause) {
			int keyCode = e.getKeyCode();
			switch(keyCode) {
				// W - jump
				case KeyEvent.VK_W:
					if (!isValid(xPos, yPos + 1) && isValid(xPos, yPos - 10)) {
		    			yPos -= 10;
		    			yVel -= 2;
		    			comp.updateImage(xPos, yPos);
					} break;
				// A - moves left
				case KeyEvent.VK_A:
					comp.setImage("Images/chell_left.gif");
					if (isValid(xPos - 10, yPos)) {
		    			xPos -= 10;
		    			if (xVel - 1 >= 0 - TERMINALVELX)
		    				xVel -= 0.5;
		    			comp.updateImage(xPos, yPos);
					} break;
				// S - moves down
				case KeyEvent.VK_S:
					if (isValid(xPos, yPos + 10)) {
		    			yPos += 10;
		    			if (yVel + 1 <= TERMINALVELY)
		    				yVel += 0.5;
		    			else
		    				yVel = TERMINALVELY;
		    			comp.updateImage(xPos, yPos);
					} break;
				// D - moves right
				case KeyEvent.VK_D:
		    		comp.setImage("Images/chell_right.gif");
					if (isValid(xPos + 10, yPos)) {
		    			xPos += 10;
		    			if (xVel + 1 <= TERMINALVELX)
		    				xVel += 0.5;
		    			comp.updateImage(xPos, yPos);
					} break;
				//other key pressed - ignore
				default:
					break;
			}
		}
	}

	// maintains physics properties (gravity, friction, momentum)
    private void spaceTime() {
    	long lastTime = System.currentTimeMillis();
    	// keeps going until game is complete
    	while (playing)	{
    		while (!pause) {
    			comp.requestFocus();
    			
    			// determines whether or not background music has ended,
    			// if so restarts music
    			try {
	    			if (music.available() == 0) {
	    				music = new AudioStream(new FileInputStream("Audio/background.wav"));
	    				AudioPlayer.player.start(music);
	    			}
    			} catch (IOException e) {
    				System.out.println("Error Occurred: " + e);
    			}
	    		long time = System.currentTimeMillis();
	    		
	    		// if it has been 10 milliseconds, does physics stuff
	     		if (time - 10 > lastTime) {
	     			
	     			// updates time in status
	     			totalTime += time - lastTime;
	     			String display = getTime();
	     	    	status.replaceRange(display, 47, status.getText().length());
	     	    	
	     	    	// resets 'previous' time
	    			lastTime = time;
	    			
	    			// adds gravity if appropriate
	    			if (yVel + GRAVITY <= TERMINALVELY)
	    				yVel += GRAVITY;
	    			else
	    				yVel = TERMINALVELY;
	    			
	    			// adds friction if appropriate
	    			if (!pause && !isValid(xPos, yPos + 1)) {
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
	    			
	    			// handles x and y momentum
	    			if (isValid((int)(xPos + xVel), yPos))
	    				xPos += xVel;
	    			else if (!pause) {
	    				int check = (int) xVel;
	    				if (xVel >= 0) {
	    					while (!pause && !isValid(xPos + check, yPos) && check > 0)
	    						check--;
	    				} else {
	    					while (!pause && !isValid(xPos + check, yPos) && check < 0)
	    						check++;
	    				}
	    				xVel = 0;
	    				if (!pause)
	    					xPos += check;
	    			}
	    			if (isValid(xPos, (int)(yPos + yVel)))
	    				yPos += yVel;
	    			else if (!pause) {
	    				int check = (int) yVel;
	    				if (yVel >= 0) {
	    					while (!pause && !isValid(xPos, yPos + check) && check > 0)
	    						check--;
	    				} else if (yVel < 0) {
	    					while (!pause && !isValid(xPos, yPos + check) && check < 0)
	    						check++;
	    				}
	    				yVel = 0;
	    				if (!pause)
	    					yPos += check;
	    			}
	    			
	    			// updates image appropriately
	    			comp.updateImage(xPos, yPos);
	    		}
	    	}
    		System.out.print("");
    		lastTime = System.currentTimeMillis();
    	}
    }
    
    // determines whether x,y position is a valid position
    private boolean isValid(int x, int y) {	
    	// not valid if game is paused
    	if (pause)
    		return false;
    	
    	// coordinates to check
    	boolean answer = true;
    	int lx = (x + 10) / 32;		// left x
    	int mx = (x + 32) / 32;		// middle x
    	int rx = (x + 54) / 32;		// right x
    	int ty = (y + 6) / 32;		// top y
    	int my = (y + 32) / 32;		// middle y
    	int by = (y + 58) / 32;		// bottom y
    	
    	// not valid if out of bounds
    	if (lx < 0 || rx >= 32 || ty < 0 || by >= 22)
    		return false;

    	// array of coordinates to check
    	int[] check = {grid[lx][ty], grid[mx][ty], grid[rx][ty], 
    				   grid[lx][my], grid[rx][my], 
    				   grid[lx][by], grid[mx][by], grid[rx][by]};
  
    	// checks coordinates
    	for (int i = 0; i < check.length; i++) {
    		if (!pause) {
	    		if (check[i] != -1) {
	    			Wall a = walls.get(check[i]);
	    			
	    			// handles case of portal
					if (a instanceof Portal) {
						Portal b = (Portal)a;
						return throughPortal(b);	// moves through portal
					} 
					
					// handles case of spike (aka you died)
					else if (a instanceof Spike) {
						comp.updateImage(x,y);
						// decrements lives and updates status
						lives--;
				    	status.replaceRange(lives.toString(), 34, 35);
				    	end();
				    	// plays audio
						try {
							FileInputStream death = new FileInputStream("Audio/Death.wav");
							AudioPlayer.player.start(death);
							JOptionPane.showMessageDialog(null, "You died\nLives:  " + 
									lives, "Aperture Science", JOptionPane.PLAIN_MESSAGE,
									new ImageIcon("Images/GLaDOS.png"));
							AudioPlayer.player.stop(death);
						} catch (IOException e) {
							System.out.println("Error occurred: " + e.getMessage());
						} 
				    	return false;
					} 
					
					// handles case of door (aka you completed the level)
					else if (a instanceof Door) {
						xVel = 0;
						yVel = 0;
						levelNum++;
						end();
						return false;
					}
					
					// if case of wall, invalid
					answer = false;
	    		}
	    	}
    	}
    	return answer;
    }
    
    // moving through portals
    private boolean throughPortal(Portal here) {
    	
    	// checks which portal you entered
    	boolean blue = here.isBlue();
    	int thisDir = here.getDirection();
    	
    	// checks for index of other portal
    	int otherInd = mouse.getPortal(!blue);
    	if (otherInd != -1) {
	    	Portal other = (Portal) walls.get(otherInd);
			int nextDir = other.getDirection();
			int nextX = other.getX();
			int nextY = other.getY();
			
			// transfers Chell through portal
			doDirections(thisDir, nextDir, nextX, nextY);
	    	comp.updateImage(xPos, yPos);
	    	
	    	// successfully moved through portal
	    	return true;
	    } 
    	
    	// no other portal to move through
    	return false;
    }
    
    // reconfigures Chell's position, based on the direction of the next portal
    private void doDirections (int thisDir, int nextDir, int x, int y){
    	boolean valid = true;
    	switch (nextDir) {
    		// up
    		case 1:
				if (isValid(x - 16, y - 64)) {xPos = x - 16; yPos = y - 64;} 
				else if (isValid(x, y - 64)) {xPos = x; yPos = y - 64;} 
				else if (isValid(x - 32, y - 64)) {xPos = x - 32; yPos = y - 64;} 
				else {valid = false;}
				break;
			// right
    		case 2:
				if (isValid(x + 32, y - 16)) {xPos = x + 32; yPos = y - 16;} 
				else if (isValid(x + 32, y)) {xPos = x + 32; yPos = y;} 
				else if (isValid(x + 32, y - 32)) {xPos = x + 32; yPos = y - 32;} 
				else {valid = false;}
				break;
			// down
    		case 3:
				if (isValid(x - 16, y + 32)) {xPos = x - 16; yPos = y + 32;} 
				else if (isValid(x, y + 32)) {xPos = x; yPos = y + 32;} 
				else if (isValid(x - 32, y + 32)) {xPos = x - 32; yPos = y + 32;} 
				else {valid = false;}
				break;
			// left
    		case 4:
				if (isValid(x - 64, y - 16)) {xPos = x - 64; yPos = y - 16;} 
				else if (isValid(x - 64, y)) {xPos = x - 64; yPos = y;} 
				else if (isValid(x - 64, y - 32)) {xPos = x - 64; yPos = y - 32;} 
				else {valid = false;}
				break;
    		default:
    			valid = false;
    			break;
    	} 
    	
    	// if valid portal, adjusts Chell's velocity appropriately
    	if (valid)
			doVelocity(thisDir, nextDir);
    }
    
    // reconfigures Chell's velocity so momentum is conserved,
    // based on directions of two portals
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
		if (nextDir == 1)
			yVel += 1;
		if (xVel > 0)
			comp.setImage("Images/chell_right.gif");
		else if (xVel < 0)
			comp.setImage("Images/chell_left.gif");
    }
    
    // sets the mouse
    public void setMouse(Mouse mouse) {
		this.mouse = mouse;
	}

    // returns Chell's current number of lives
	public int getLives() {
		return lives;
	}

	// returns the current time, as a string
	public String getTime() {
		
		// totalTime is in milliseconds, so divides by 1000 to get seconds
		double seconds = totalTime / 1000.0;
		
		// determines the number of minutes passed
		int minutes = (int) seconds / 60;
		seconds = Math.round((seconds % 60) * 1000) / 1000.0;
		
		// formats returned time string
		if (seconds >= 10) 
			return minutes + ":" + seconds;
		else
			return minutes + ":0" + seconds;
	}

	// sets the grid of indexes based on wall list
	private void setGrid(ArrayList<Wall> wl) {
		
		// -1 if no wall at that position
		for (int x = 0; x < 32; x++) {
			for (int y = 0; y < 22; y++) {
				grid[x][y] = -1;
			}
		}
		
		// replaces -1 with actual wall index for all walls in list
		for (int i = 0; i < wl.size(); i++) {
			Wall w = wl.get(i);
			grid[w.getX()/32][w.getY()/32] = i;
		}
	}

	// pauses or unpauses the game and audio appropriately
	public void setPause(boolean pause) {
    	this.pause = pause;
    	// stops or starts audio
    	try {
	    	if (pause) {
    			AudioPlayer.player.stop(music);
	    	} else {
	    		if (music.available() == 0)
	    			music = new AudioStream(new FileInputStream("Audio/background.wav"));
	    		AudioPlayer.player.start(music);
	    		comp.requestFocus();
	    	}
		} catch (IOException e) {
			System.out.println("Error Occurred: " + e.getMessage());
		}
    }
    
	// returns current level number
    public int getLevel() {
    	return levelNum;
    }
    
    // sets current level number
    public void setLevel(int newLevel) {
    	levelNum = newLevel;
    }
    
    // starts game/level
    public void start() {
		setPause(false);
		playing = true;
		spaceTime();
	}

    // ends game/level
	public void end() {
    	playing = false;
    	setPause(true);
    }
}