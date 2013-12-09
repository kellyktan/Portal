import java.io.*;
import java.util.*;

// initializes level
public class Level {
	
	// private variables
	private GameComponent main;				// the component Chell moves around in
	private ArrayList<Wall> walls;			// list of walls in the level
	private int startX, startY;				// Chell's start coordinates

	// constructs level based on text file
	public Level (String file, GameComponent comp) throws IOException {
		walls = new ArrayList<Wall>();
		
		// file not found
		if (file == null)
			throw new IllegalArgumentException();
		
		// reads file and creates walls appropriately
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String s = br.readLine();
			int count = 0;				// keeps track of data type
										// 0 = Chell's initial coordinates
										// 1 = door position
										// 2/3 = nonportalable/portalable wall
										// 4 = spike
			while (s != null)
			{
				s = s.trim().toLowerCase();
				
				// certain area of wall (startX,startY,endX,endY:direction)
				if (s.matches("\\d+,\\d+-\\d+,\\d+:\\d")) {
					// starting coordinate
					int comma = s.indexOf(",");
					int end = s.indexOf("-");
					int x1 = Integer.parseInt(s.substring(0, comma)) * 32;
					int y1 = Integer.parseInt(s.substring(comma + 1, end)) * 32;
					// ending coordinate
					s = s.substring(end + 1);
					comma = s.indexOf(",");
					int x2 = Integer.parseInt(s.substring(0, comma)) * 32;
					int y2 = Integer.parseInt(s.substring(comma + 1, s.length() - 2)) * 32;
					// direction
					int dir = Integer.parseInt(s.substring(s.length() - 1));
					// adds walls to list
					for (int x = x1; x <= x2; x += 32) {
						for (int y = y1; y <= y2; y += 32) {
							if (count == 2) // non portalable wall
								walls.add(new Wall(x, y, false, dir));
							else if (count == 3)// portalable wall
								walls.add(new Wall(x, y, true, dir));
							else
								walls.add(new Spike(x, y, dir));
						}
					}	
				} 
				
				// specific wall or coordinate (x,y:direction)
				else if (s.matches("\\d+,\\d+:\\d")) {
					// coordinate
					int comma = s.indexOf(",");
					int x = Integer.parseInt(s.substring(0,comma)) * 32;
					int y = Integer.parseInt(s.substring(comma + 1, s.length() - 2)) * 32;
					int dir = Integer.parseInt(s.substring(s.length() - 1));	
					// adds wall to list
					if (count == 0) {
						startX = x - 10;
						startY = y + 5;
						count++;
					} else if (count == 1) {
						walls.add(new Door(x, y));
						count++;
					} else if (count == 2) {
						walls.add(new Wall(x, y, false, dir));
					} else if (count == 3){
						walls.add(new Wall(x, y, true, dir));
					} else {
						walls.add(new Spike(x, y, dir));
					}
				} 
				
				// indicates start of portalable wall coordinates
				else if (s.equals("portalable")) {
					count = 3;
				} 
				
				// indicates start of spike coordinates
				else if (s.equals("spike")) {
					count = 4;
				} 
				
				// not a valid line in text file
				else {
					br.close();
					throw new IOException("Expected start coordinate ('x,y') " +
						"or 'portalable' to indicate portalable walls");
				}
				
				// reads next line
				s = br.readLine();
			}
			
			// closes file
			br.close();
			
			// checks if level was input properly
			if (count < 2)
				throw new IOException("Missing elements for level in file");
		}
		catch (FileNotFoundException e) {
			throw e;
		}
		catch (IOException e) {
			System.out.println("Error occured: " + e.getMessage());
		}
		
		// sets various aspects of game component
		main = comp;
    	main.setImage("Images/chell_right.gif");
    	main.setWalls(walls);
    	main.updateImage(startX, startY);
	}
	
	// returns game component
	public GameComponent getComponent() {
		return main;
	}
	
	// returns wall list
	public ArrayList<Wall> getWalls() {
		return walls;
	}
	
	// returns Chell's initial x coordinate for this level
	public int getX() {
		return startX;
	}
	
	// returns Chell's initial y coordinate for this level
	public int getY() {
		return startY;
	}
}
