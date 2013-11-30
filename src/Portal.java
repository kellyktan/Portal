public class Portal extends Wall
{
	// private variables
	private boolean blue; 		// two different colors, blue and orange
	private int direction; 		// direction portal is facing
						   		// 1 = up, 2 = right, 3 = down, 4 = left
	
	// constructor constructs portal at x,y with certain color and direction
	public Portal(int x, int y, boolean blu, int dir)
	{
		super(x, y, false);
		blue = blu;
		direction = dir;
	}
	
	// pre: none
	// post: returns whether portal is blue or not
	public boolean isBlue()
	{
		return blue;
	}
	// pre: none
	// post: returns direction portal is facing (1, 2, 3, or 4)
	public int getDirection()
	{
		return direction;
	}
}