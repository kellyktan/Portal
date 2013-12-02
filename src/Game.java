import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.*;

public class Game {
	// private variables
	private JFrame frame;
	private JPanel panel;
	private JTextArea status;
	private GameComponent comp;
	private Keyboard keyboard;
	private Mouse mouse;
	private Integer lives;
	private final int TOTALLEVELS = 2;
	
	// constructor constructs list of levels and initializes menu
	public Game() {
		//allLevel = new AllLevels();
		init();
	}
	
	// pre: none
	// post: intilizes menu with options to play or choose level
	public void init()
	{
		introduction();
		Object[] startOptions = {"Begin", "Select Test Chamber"}; 					// start options
		String title = "Aperture Science Enrichment Center"; 				// title of window
		int response = JOptionPane.showOptionDialog(null, "Menu", title, 
			JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, 
			startOptions, startOptions[0]);
		if (response == 0) 													// starts with level 1
			init(1);
		else if (response == 1) 											// opens menu with different menu options
			levelSelect();
	}
	// pre: none
	// post: initilizes menu with different levels to select
	public void levelSelect()
	{
		Object[] levels = {"Test Chamber 01", "Test Chamber 02", "Test Chamber 03", 
			"Test Chamber 04", "Test Chamber 05"};
		
		int response = JOptionPane.showOptionDialog(null, "Select Test Chamber", "Select Test Chamber", 
			JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, 
			levels, levels[0]);
		
		init(response + 1);
	}

	public void introduction()
	{
		JOptionPane.showMessageDialog(null, "\nHello and, again, welcome to the" + 
			" Aperture Science computer-aided enrichment center.\nYour specimen" + 
			" has been processed and we are now ready to begin the test proper" +
			".\nBefore we start, however, keep in mind that although fun and " +
			"learning are\nthe primary goals of all enrichment center activities," +
			" serious injuries may occur.\nFor your own safety and the safety of" +
			" others, please refrain from--\nPor favor bord—n de fallar.  Muchos " +
			"gracias de fallar gracias.", "Aperture Science Enrichment Center" +
			" welcomes Subject #1498", JOptionPane.PLAIN_MESSAGE);
		JOptionPane.showMessageDialog(null, "\nYou are now in possession of the" +
			" Aperture Science Handheld Portal Device.\nWith it, you can create" +
			" your own portals.  These interdimensional gates have been\nproven" +
			" to be completely safe.  The Device, however, has not:\n\n  -  Do not" +
			" touch the operational end of The Device\n  -  Do not look directly" +
			" at the operational end of The Device\n  -  Do not submerge The " +
			"Device in liquid, even partially\n  -  Most importantly, under no" +
			" circumstances should you--", "Aperture Science Enrichment Center",
			JOptionPane.PLAIN_MESSAGE);
		JOptionPane.showMessageDialog(null, "\nINSTRUCTIONS:\n\nMake your way through" +
			" each test chamber in order to reach the door and move on to the next" +
			" chamber\n\nNAVIGATION:  W = move up, D = move right, S = move down," +
			" A = move left\n\nPLACING PORTALS:  click either left (blue) or" +
			" right (orange) mouse button in the desired shooting direction\n\n" +
			"WARNING:  spikes will kill you if you touch them",
			"Aperture Science Enrichment Center", JOptionPane.PLAIN_MESSAGE);
	}
	
	public void init(int num) {
		try {
			frame = new JFrame("Aperture Laboratories");
			panel = new JPanel();
			frame.add(panel);
			panel.setLayout(new BorderLayout());
		
			lives = 5;
			status = new JTextArea("\n   Test Chamber 0" + num + "\n\n   " +
				"Lives:  5\n\n   Time:  0:00.000", 7, 15);
			status.setEditable(false);
			comp = new GameComponent("Images/background.gif");
			keyboard = new Keyboard(status, lives);
			mouse = new Mouse(comp, keyboard);
			keyboard.setMouse(mouse);
			comp.setFocusable(true);
			comp.addKeyListener(keyboard);
			comp.addMouseListener(mouse);
			
			Level start = new Level("Levels/Level " + num + ".txt", keyboard, comp);
			panel.add(start.getComponent(), BorderLayout.WEST);
			panel.add(status);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			keyboard.startLevel(start, num);
			while (lives != 0 && keyboard.getLevel() <= TOTALLEVELS)
				keyboard.nextLevel();
			frame.dispose();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}		
	}
}