import javax.swing.*;
import java.awt.event.*;
import java.util.*;
public class Mouse extends MouseAdapter
{
	// private variables
	private GameComponent imagePanel;				
	private ArrayList<Wall> walls;
	private Keyboard keyboard;
	
	// constructor constructs MouseUser with list of walls, imagePanel, and keyboard
	public Mouse(ArrayList<Wall> wlls, GameComponent imgPnl, Keyboard keyboardMove)
	{
		imagePanel = imgPnl;
		walls = wlls;
		keyboard = keyboardMove;
	}
	// pre: wlls not null
	// post: sets list of walls to wlls
	public void setWalls(ArrayList<Wall> wlls)
	{
		walls = wlls;
	}
	
	// pre: none
	// post: places portal depending on which button clicked and if direction keys are pressed
	//       (left = blue portal, right = orange portal)
	public void mouseClicked(MouseEvent e)
    {
    	int mouseCode = e.getButton();
    	int mouseX = e.getX();
    	int mouseY = e.getY();
    	switch(mouseCode)
    	{
    		case MouseEvent.BUTTON1:	//left click
    			for (int i = 0; i < walls.size(); i++)
    			{
    				int wallX = walls.get(i).getX();
    				int wallY = walls.get(i).getY();
    				if (walls.get(i) instanceof Portal) // removes previous portal
    				{
    					Portal replace = (Portal)(walls.get(i));
    					if (replace.isBlue() == true)
    						walls.set(i, new Wall(wallX, wallY, true));
    				}
    				if (isValid(mouseX, mouseY, wallX, wallY) && walls.get(i).isPortalable()) // places new portal
					{
						if (keyboard.dirPressed())	// will only place portal if direction key is pressed
						{
	    					Portal portal = new Portal(wallX, wallY, true, keyboard.getDirection());
	    					walls.set(i, portal);
	    					imagePanel.updateImage(imagePanel.getChellX(), imagePanel.getChellY());
						}
					}
    			}
    			break;
    		case MouseEvent.BUTTON3:	//right click
    			for (int i = 0; i < walls.size(); i++)
    			{
    				int wallX = walls.get(i).getX();
    				int wallY = walls.get(i).getY();
    				if (walls.get(i) instanceof Portal) // removes previous portal
    				{
    					Portal replace = (Portal)(walls.get(i));
    					if (replace.isBlue() == false)
    						walls.set(i, new Wall(wallX, wallY, true));
    				}
    				if (isValid(mouseX, mouseY, wallX, wallY) && walls.get(i).isPortalable()) // places new portal
					{
						if (keyboard.dirPressed())	// will only place portal if direction key is pressed
						{
	    					Portal portal = new Portal(wallX, wallY, false, keyboard.getDirection());
	    					walls.set(i, portal);
	    					imagePanel.updateImage(imagePanel.getChellX(), imagePanel.getChellY());
						}
					}
    			}
    			break;
    		default:
    			//other key pressed: ignore
    			break;
    	}

    }
    // pre: none
    // post: checks if clicked spot is valid, portalable wall to place portal
    public boolean isValid(int mouseX, int mouseY, int wallX, int wallY)
    {
    	if (mouseX >= 0 && mouseX <= 750 && mouseY >= 0 && mouseY <= 500)
    	{
			if (mouseX >= wallX && mouseX <= wallX + 25 &&
				mouseY >= wallY + 25 && mouseY <= wallY + 50)
				return true;
    	}
    	return false;
    }

}