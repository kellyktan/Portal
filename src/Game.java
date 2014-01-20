import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import sun.audio.*;

// contains main game logistics
public class Game {
	
	// main method
	public static void main(String[] args) {
		Game a = new Game();
		a.play();
	}

	// private variables
	private JFrame frame;				 // the frame the main game exists within
	private JPanel gamePanel;				 // the panel the game exists within
	private JPanel menuPanel;
	private JTextArea status;			 // the status of the game: test chamber #,
										 // lives, time
	private GameComponent comp;			 // the game component within the panel
	private Keyboard keyboard;			 // the keyboard listener
	private Mouse mouse;				 // the mouse listener
	private final int LIVES = 5;		 // the # of lives you start with
	private final int TOTALLEVELS = 7;	 // the total # of levels
	
	// starts playing the game
	public void play() {
		introduction();		// introduces the game and shows instructions
		menu();
	}
	
	// start menu
	public void menu() {		
		Object[] startOptions = {"Begin", "Select Test Chamber", "Quit"};	// menu options
		// shows menu options, default is 'begin'
		int response = JOptionPane.showOptionDialog(null, "Menu", 
			"Aperture Science Enrichment Center", JOptionPane.DEFAULT_OPTION, 
			JOptionPane.PLAIN_MESSAGE, new ImageIcon("Images/aperture.png"), 
			startOptions, startOptions[0]);
		if (response == 0)			// if 'begin' is selected starts with level 1
			init(1);
		else if (response == 1)		// opens menu with different menu options
			levelSelect();		
		// else if response is 2 (Quit)
		// does nothing and program ends
	}
	
	// menu for selecting a level
	private void levelSelect() {
		Object[] levels = new Object[TOTALLEVELS]; // creates array with each level
		for (int i = 0; i < TOTALLEVELS; i++)
			levels[i] = "Test Chamber 0" + (i + 1);
		// shows option for each level
		String response = (String) JOptionPane.showInputDialog(null, "Select Test Chamber", 
			"Select Test Chamber", JOptionPane.PLAIN_MESSAGE, 
			new ImageIcon("Images/aperture.png"), levels, levels[0]);
		// gets selected level number from selected string
		if (response != null) {
			int respLevel = Integer.parseInt(response.substring(response.length() - 1));
			init(respLevel);	// initializes selected level
		} else
			menu();
	}	
	
	// initializes the game, starting at level 'num'
	private void init(int num) {
		try {
			frame = new JFrame("Aperture Laboratories");	// creates frame
			menuPanel = new JPanel();						// creates panels
			gamePanel = new JPanel();
			frame.add(menuPanel, BorderLayout.NORTH);		// adds panels to frame
			frame.add(gamePanel, BorderLayout.SOUTH);	
			menuPanel.setLayout(new BorderLayout());
			gamePanel.setLayout(new BorderLayout());			
			// the status text
			status = new JTextArea("\n     Test Chamber 0" + num + "     " +
				"Lives:  " + LIVES + "     Time:  0:00.000", 3, 65);
			status.setEditable(false);		// prevents user from editing status text
			comp = new GameComponent("Images/background.jpg");	// creates game component
			keyboard = new Keyboard(status, LIVES);		// creates keyboard
			mouse = new Mouse(comp, keyboard);			// creates mouse
			keyboard.setMouse(mouse);
			comp.setFocusable(true);
			comp.addKeyListener(keyboard);
			comp.addMouseListener(mouse);
			Level start = new Level("Levels/Level " + num + ".txt", comp);
			// button for showing instructions/pausing game
	        final JButton instructions = new JButton("Instructions");
	        	instructions.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                	keyboard.setPause(true);		
	                    instructions();
	                    keyboard.setPause(false);
	                    frame.toFront();
	                    comp.requestFocus();
	                }
	            });
	        // button for main menu
	        final JButton menu = new JButton("Main Menu");
	        	menu.addActionListener(new ActionListener() {
	        		public void actionPerformed(ActionEvent e) {
	        			keyboard.setPause(true);

	        			Object[] options = {"Yes", "No"};	// menu options
	        			int response = JOptionPane.showOptionDialog(null, 
        					"Current progress will be lost.\nAre you sure?", "Warning",
        					JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
        					new ImageIcon("Images/aperture.png"), options, options[0]);
	        			if (response == 0) {
	        				keyboard.setLevel(TOTALLEVELS + 2);
        					keyboard.end();
	        			}
        				else if (response == 1) {
        					keyboard.setPause(false);
        					frame.toFront();
        					comp.requestFocus();
        				}
	        		}
	        	});
	        // adds various components to the frame and panel
			gamePanel.add(start.getComponent());
			menuPanel.add(status, BorderLayout.WEST);
			menuPanel.add(menu);
			menuPanel.add(instructions, BorderLayout.EAST);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			comp.requestFocus();
			// starts appropriate level
			keyboard.startLevel(start, num);
			// proceeds to appropriate level
			while (keyboard.getLives() > 0 && keyboard.getLevel() <= TOTALLEVELS) {
				keyboard.nextLevel();
			}
			frame.dispose();
			// shows appropriate game over frame
			if (keyboard.getLives() == 0) {
					FileInputStream cake = new FileInputStream("Audio/Game_Over.wav");
					AudioPlayer.player.start(cake);
					JOptionPane.showMessageDialog(null, "Game Over",
							"Aperture Science", JOptionPane.PLAIN_MESSAGE,
							new ImageIcon("Images/GLaDOS.png"));		
					AudioPlayer.player.stop(cake);
			}
			else if (keyboard.getLevel() == TOTALLEVELS + 1) {
				String time = keyboard.getTime();
					FileInputStream ending = new FileInputStream("Audio/Win.wav");
					AudioPlayer.player.start(ending);
					JOptionPane.showMessageDialog(null, "You win.  You" +
						" Monster.\n\nLives:  " + keyboard.getLives() + "\n\nTime:  " + 
						time, "Aperture Science", JOptionPane.PLAIN_MESSAGE,
						new ImageIcon("Images/GLaDOS.png"));
					AudioPlayer.player.stop(ending);
			}
			menu();
		} catch (IOException e) {
			System.out.println("Error Occurred: " + e.getMessage());
		}
	}	

	// shows introduction
	private void introduction() {
		try {
			FileInputStream intro = new FileInputStream("Audio/Intro_1.wav");
			AudioPlayer.player.start(intro);
			JOptionPane.showMessageDialog(null, "Hello and, again, welcome to the" + 
				" Aperture Science computer-aided enrichment center.\nYour specimen" + 
				" has been processed and we are now ready to begin the test proper" +
				".\nBefore we start, however, keep in mind that although fun and " +
				"learning are\nthe primary goals of all enrichment center activities," +
				" serious injuries may occur.\nFor your own safety and the safety of" +
				" others, please refrain from--\nPor favor bordon de fallar.  Muchos " +
				"gracias de fallar gracias.", "Aperture Science Enrichment Center" +
				" welcomes Subject #1498", JOptionPane.PLAIN_MESSAGE,
				new ImageIcon("Images/GLaDOS.png"));
			AudioPlayer.player.stop(intro);
		} catch (IOException e) {
			System.out.println("Error occurred: " + e.getMessage());
		}
		
		try {
			FileInputStream intro = new FileInputStream("Audio/Intro_2.wav");
			AudioPlayer.player.start(intro);
			JOptionPane.showMessageDialog(null, "You are now in possession of the" +
				" Aperture Science Handheld Portal Device.\nWith it, you can create" +
				" your own portals.  These interdimensional gates have been\nproven" +
				" to be completely safe.  The Device, however, has not:\n\n  -  Do not" +
				" touch the operational end of The Device\n  -  Do not look directly" +
				" at the operational end of The Device\n  -  Do not submerge The " +
				"Device in liquid, even partially\n  -  Most importantly, under no" +
				" circumstances should you--", "Aperture Science Enrichment Center",
				JOptionPane.PLAIN_MESSAGE, new ImageIcon("Images/GLaDOS.png"));
			AudioPlayer.player.stop(intro);
		} catch (IOException e) {
			System.out.println("Error occurred: " + e.getMessage());
		}
		instructions();
	}
	
	// shows instructions
	private void instructions() {
		JOptionPane.showMessageDialog(null, "\nINSTRUCTIONS:\n\nMake your way through" +
			" each test chamber in order to reach the door and move on to the next" +
			" chamber\n\nNAVIGATION:  W = jump, A = left, D = right" +
			"\n\nPLACING PORTALS:  Click with either the left (blue) or " +
			"right (orange) mouse button in the desired shooting\n     direction," +
			" relative to Test Subject #1498  (Portals can be placed on the " +
			"grey, portalable walls)\n\nHINT:  Momentum is conserved through portals" +
			"\n\nWARNING:  Spikes will kill you if you touch them",
			"Aperture Science Enrichment Center", JOptionPane.PLAIN_MESSAGE,
			new ImageIcon("Images/aperture.png"));		
	}
}