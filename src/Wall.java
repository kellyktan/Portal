public class Wall {
	// private variables
	private int x, y; 				// wall position
	private boolean portalable; 	// whether or not portal can be placed on it
	
	// constructor constructs wall at x,y and sets portalability
	// 0 < x < 725, 0 < y < 475
	public Wall(int hereX, int hereY, boolean portal) {
		x = hereX;
		y = hereY;
		portalable = portal;
	}
	
	// pre: none
	// post: returns x position
	public int getX() {
		return x;
	}
	// pre: none
	// post: returns y position
	public int getY() {
		return y;
	}
	// pre: none
	// post: returns portalablity
	public boolean isPortalable() {
		return portalable;
	}
}