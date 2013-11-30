import java.io.IOException;

import javax.swing.*;

public class Game {
	// private variables
	private JFrame frame;
	private JPanel panel;
	private GameComponent comp;
	private Keyboard keyboard;
	private long time;
	private Boolean playing;
	
	// constructor constructs list of levels and initializes menu
	public Game() {
		//allLevel = new AllLevels();
		init();
	}
	
	// pre: none
	// post: intilizes menu with options to play or choose level
	public void init()
	{
		Object[] startOptions = {"Play", "Level Select"}; 					// start options
		String title = "Aperture Science Enrichment Center"; 				// title of window
		int response = JOptionPane.showOptionDialog(null, "Menu", title, 
			JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, 
			startOptions, startOptions[0]);
		if (response == 0) 													// starts with level 1
		{
			introduction();
			try {
				frame = new JFrame("Aperture Laboratories");
				panel = new JPanel();
				frame.add(panel);
				
				comp = new GameComponent("Images/background.gif");
				playing = false;
				keyboard = new Keyboard(playing);
				comp.setFocusable(true);
				comp.addKeyListener(keyboard);
				
				Level first = new Level("Levels/Level 1.txt", keyboard, comp);
				panel.add(first.getComponent());
				panel.add(new JButton());
				frame.pack();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				System.out.println(first.getComponent().getHeight());
				keyboard.setLevel(first);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		else if (response == 1) 											// opens menu with different menu options
			levelSelect();
	}
	// pre: none
	// post: initilizes menu with different levels to select
	public void levelSelect()
	{
		Object[] levels = {"Level 1", "Level 2", "Level 3", "Level 4", "Level 5"};
		
		int response = JOptionPane.showOptionDialog(null, "Select Level", "Level Select", 
			JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, 
			levels, levels[0]);
		
		// level options	
		if (response == 0)
		{
			introduction();
		//	allLevel.start(1);
		}
		else if (response == 1)
		{
			introduction();
		//	allLevel.start(2);
		}
		else if (response == 2)
		{
			introduction();
		//	allLevel.start(3);
		}
		else if (response == 3)
		{
			introduction();
		//	allLevel.start(4);
		}
		else if (response == 4)
		{
			introduction();
			//allLevel.start(5);
		}
	}
	public void introduction()
	{
		JOptionPane.showMessageDialog(null, "Hello and, again, welcome to the Aperture Science computer-aided enrichment" +
			" center.\nYour specimen has been processed and we are now ready to begin the test proper.\nBefore we start," +
			" however, keep in mind that although fun and learning are\nthe primary goals of all enrichment center" +
			" activities, serious injuries may occur.\n\nYou are now in possession of the Aperture Science Handheld" +
			" Portal Device.\nWith it, you can create your own portals.  These interdimensional gates have been\nproven" +
			" to be completely safe.");
		JOptionPane.showMessageDialog(null, "Instructions:\nMake way through testing chamber in order to reach the " +
			"door and move on to the next level\n\nNavigation:  W = up arrow, D = right arrow," +
			" S = down arrow, A = left arrow\n\nPlacing Portals:  hold direction key (1 = up, 2 = right, 3 = left," + 
			" 4 = down) while clicking with \neither left (blue) or right (orange) mouse button on a grey portalable" +
			" surface\n\nWarning: Spikes will kill you if you land on them\n\nDisclaimer:  We are using the honor" +
			" system and trusting you to only shoot portals on walls\nwithin your view");
	}
}