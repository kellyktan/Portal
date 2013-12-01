public class Wall {
	// private variables
	private int x, y; 				// wall position
	private boolean portalable; 	// whether or not portal can be placed on it
	private int direction;			// direction portal can be made towards
									// 1 = up, 2 = right, 3 = down, 4 = left, 0 = not portalable
	
	// constructor constructs wall at x,y and sets portalability
	// 0 < x < 725, 0 < y < 475
	public Wall(int hereX, int hereY, boolean portal, int dir) {
		x = hereX;
		y = hereY;
		portalable = portal;
		direction = dir;
	}
	
	public Wall(Portal p) {
		x = p.getX();
		y = p.getY();
		portalable = true;
		direction = p.getDirection();
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
	public int getDirection() {
		return direction;
	}
}