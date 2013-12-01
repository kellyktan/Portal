public class Portal extends Wall
{
	// private variables
	private boolean blue; 		// two different colors, blue and orange
	
	// constructor constructs portal at x,y with certain color and direction
	public Portal(int x, int y, int dir, boolean blu) {
		super(x, y, false, dir);
		blue = blu;
	}
	
	public Portal(Wall w, boolean blu) {
		super (w.getX(), w.getY(), false, w.getDirection());
		blue = blu;
	}
	
	// pre: none
	// post: returns whether portal is blue or not
	public boolean isBlue() {
		return blue;
	}
}