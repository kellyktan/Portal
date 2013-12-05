import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import sun.audio.*;

public class Game {
	// private variables
	private JFrame frame;
	private JPanel panel;
	private JTextArea status;
	private GameComponent comp;
	private Keyboard keyboard;
	private Mouse mouse;
	private final int LIVES = 5;
	private final int TOTALLEVELS = 6;
	
	public Game() {
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
	
	private void levelSelect() {
		Object[] levels = new Object[TOTALLEVELS];
		for (int i = 0; i < TOTALLEVELS; i++)
			levels[i] = "Test Chamber 0" + (i + 1);
		int response = JOptionPane.showOptionDialog(null, "Select Test Chamber", "Select Test Chamber", 
			JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, 
			levels, levels[0]);	
		init(response + 1);
	}	
	
	private void init(int num) {
		try {
			frame = new JFrame("Aperture Laboratories");
			panel = new JPanel();
			frame.add(panel, BorderLayout.NORTH);
			panel.setLayout(new BorderLayout());

			status = new JTextArea("\n   Test Chamber 0" + num + "     " +
				"Lives:  " + LIVES + "     Time:  0:00.000", 3, 65);
			status.setEditable(false);
			comp = new GameComponent("Images/background.jpg");
			keyboard = new Keyboard(status, LIVES, TOTALLEVELS);
			mouse = new Mouse(comp, keyboard);
			keyboard.setMouse(mouse);
			comp.setFocusable(true);
			comp.addKeyListener(keyboard);
			comp.addMouseListener(mouse);
			Level start = new Level("Levels/Level " + num + ".txt", keyboard, comp);
			
	        final JButton instructions = new JButton("Instructions");
	        instructions.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                	keyboard.setPause(true);
	                    instructions();
	                    keyboard.setPause(false);
	                    comp.requestFocusInWindow();
	                }
	            });
	        
	        final JButton reset = new JButton("Reset");
	        reset.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                	keyboard.end();
	                }
	            });
	        
			panel.add(start.getComponent(), BorderLayout.SOUTH);
			frame.add(status, BorderLayout.WEST);
			frame.add(reset);
			frame.add(instructions, BorderLayout.EAST);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			comp.requestFocusInWindow();
			keyboard.startLevel(start, num);
			while (keyboard.getLives() != 0 && keyboard.getLevel() <= TOTALLEVELS)
				keyboard.nextLevel();
			frame.dispose();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}		
	}	


	private void introduction() {
		try {
			FileInputStream intro = new FileInputStream("Audio/Intro_1.wav");
			AudioPlayer.player.start(intro);
			JOptionPane.showMessageDialog(null, "\nHello and, again, welcome to the" + 
				" Aperture Science computer-aided enrichment center.\nYour specimen" + 
				" has been processed and we are now ready to begin the test proper" +
				".\nBefore we start, however, keep in mind that although fun and " +
				"learning are\nthe primary goals of all enrichment center activities," +
				" serious injuries may occur.\nFor your own safety and the safety of" +
				" others, please refrain from--\nPor favor bord—n de fallar.  Muchos " +
				"gracias de fallar gracias.", "Aperture Science Enrichment Center" +
				" welcomes Subject #1498", JOptionPane.PLAIN_MESSAGE);
			AudioPlayer.player.stop(intro);
		} catch (IOException e) {
			System.out.println("Error occurred: " + e.getMessage());
		}
		
		try {
			FileInputStream intro = new FileInputStream("Audio/Intro_2.wav");
			AudioPlayer.player.start(intro);
			JOptionPane.showMessageDialog(null, "\nYou are now in possession of the" +
				" Aperture Science Handheld Portal Device.\nWith it, you can create" +
				" your own portals.  These interdimensional gates have been\nproven" +
				" to be completely safe.  The Device, however, has not:\n\n  -  Do not" +
				" touch the operational end of The Device\n  -  Do not look directly" +
				" at the operational end of The Device\n  -  Do not submerge The " +
				"Device in liquid, even partially\n  -  Most importantly, under no" +
				" circumstances should you--", "Aperture Science Enrichment Center",
				JOptionPane.PLAIN_MESSAGE);
			AudioPlayer.player.stop(intro);
		} catch (IOException e) {
			System.out.println("Error occurred: " + e.getMessage());
		}
		instructions();
	}

	
	private void instructions() {
		JOptionPane.showMessageDialog(null, "\nINSTRUCTIONS:\n\nMake your way through" +
				" each test chamber in order to reach the door and move on to the next" +
				" chamber\n\nNAVIGATION:  W = move up, D = move right, S = move down," +
				" A = move left\n\nPLACING PORTALS:  Click either left (blue) or" +
				" right (orange) mouse button in the desired shooting direction\n" +
				"     (portals can be placed on the grey, portalable walls)\n\n" +
				"WARNING:  Spikes will kill you if you touch them",
				"Aperture Science Enrichment Center", JOptionPane.PLAIN_MESSAGE);		
	}
}