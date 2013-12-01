import java.io.*;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Level {
	private GameComponent main;					// the panel Chell moves around
	private Keyboard keyboard;					// the keyboard listener
	private ArrayList<Wall> walls;				// list of walls in the level
	private int startX, startY;					// Chell's start coordinates

	public Level (String file, Keyboard keyboard, GameComponent comp) throws IOException {
		walls = new ArrayList<Wall>();
		if (file == null)
			throw new IllegalArgumentException();
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String s = br.readLine();
			int count = 0;				// keeps track of data type
										// 0 = Chell's initial coords
										// 1 = door position
										// 2/3 = nonportalable/portalable wall
										// 4 = spike
			while (s != null)
			{
				s = s.trim().toLowerCase();
				if (s.matches("\\d+,\\d+-\\d+,\\d+:\\d")) {
					// certain area of wall (startX,startY,endX,endY:direction)
					
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
					
				} else if (s.matches("\\d+,\\d+:\\d")) {
					int comma = s.indexOf(",");
					int x = Integer.parseInt(s.substring(0,comma)) * 32;
					int y = Integer.parseInt(s.substring(comma + 1, s.length() - 2)) * 32;
					int dir = Integer.parseInt(s.substring(s.length() - 1));
					
					if (count == 0) {
						startX = x;
						startY = y;
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
				} else if (s.equals("portalable")) {
					count = 3;
				} else if (s.equals("spike")) {
					count = 4;
				} else {
					br.close();
					throw new IOException("Expected start coordinate ('x,y')" +
						"or 'portalable' to indicate portalable walls");
				}
				s = br.readLine();
			}
			br.close();
			if (count < 2)
				throw new IOException("Missing elements for level in file");
		}
		catch (FileNotFoundException e) {
			throw e;
		}
		catch (IOException e) {
			System.out.println("Error occured: " + e.getMessage());
		}
		main = comp;
    	main.setImage("Images/chell_right.gif");
    	main.setWalls(walls);
    	main.updateImage(startX, startY);
	}
	
	public GameComponent getComponent() {
		return main;
	}
	
	public ArrayList<Wall> getWalls() {
		return walls;
	}
	
	public int getX() {
		return startX;
	}
	
	public int getY() {
		return startY;
	}

}
