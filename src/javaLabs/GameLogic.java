package javaLabs;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The control logic and main display panel for game.
 */
public class GameLogic extends JPanel
{
	private static final int UPDATE_RATE = 60; // Frames per second (fps)

	private Ball ball; // A single bouncing Ball's instance
	private GameField field; // The container rectangular box
	private Player1 p1;
	private Player2 p2;
	public static int p1Score = 0;
	public static int p2Score = 0;

	private DrawCanvas canvas; // Custom canvas for drawing the box/ball
	private int canvasWidth;
	private int canvasHeight;

	/**
	 * Constructor to create the UI components and init the game objects. Set
	 * the drawing canvas to fill the screen (given its width and height).
	 * 
	 * @param width
	 *            : screen width
	 * @param height
	 *            : screen height
	 */
	public GameLogic(int width, int height)
	{
		canvasWidth = width;
		canvasHeight = height;

		// Init the ball at a random location (inside the box) and moveAngle
		Random rand = new Random();
		int radius = 30;
		int speed = 7;
		int angleInDegree = rand.nextInt(90+45);
		ball = new Ball(width/2, height/2, radius, speed, angleInDegree, Color.RED);

		// Init the Container Box to fill the screen
		field = new GameField(0, 0, canvasWidth, canvasHeight, Color.BLACK,
				Color.WHITE);
		p1 = new Player1(30, canvasHeight/2 - 100, 20, 200, Color.BLUE,
				Color.WHITE);
		p2 = new Player2(canvasWidth - 50, canvasHeight/2 - 100 , 20, 200, Color.BLUE,
				Color.WHITE);
		// Init the custom drawing panel for drawing the game
		canvas = new DrawCanvas();
		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);

		// Handling window resize.
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Component c = (Component) e.getSource();
				Dimension dim = c.getSize();
				canvasWidth = dim.width;
				canvasHeight = dim.height;
				// Adjust the bounds of the container to fill the window
				field.set(0, 0, canvasWidth, canvasHeight);
			}
		});

		// Start the ball bouncing
		gameStart();
	}

	/** Start the ball bouncing. */
	public void gameStart()
	{	
		// Run the game logic in its own thread.
		Thread gameThread = new Thread()
		{
			public void run()
			{
				while (true)
				{
					// Execute one time-step for the game
					gameUpdate(this);
					// Refresh the display
					repaint();
					// Delay and give other thread a chance
					try {
						Thread.sleep(1000 / UPDATE_RATE);
					} catch (InterruptedException ex) {
					}
				}
			}
		};
		Thread player1Thread = new Thread()
		{
			private JPanel panel;
			
			public void run()
			{
				panel = new JPanel();
				// HERE ARE THE KEY BINDINGS
				panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
						KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "up");
				panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
						KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "down");
				panel.getActionMap().put("up", new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						p1.moveUp(field);
						
					}
				});
				panel.getActionMap().put("down", new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						p1.moveDown(field);
					}
				});
				// END OF KEY BINDINGS
				add(panel, BorderLayout.CENTER);
			}
		};
		Thread player2Thread = new Thread()
		{
			private JPanel panel;
			
			public void run()
			{
				panel = new JPanel();
				// HERE ARE THE KEY BINDINGS
				panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
						KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "up");
				panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
						KeyStroke.getKeyStroke(KeyEvent.VK_L, 0), "down");
				panel.getActionMap().put("up", new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						p2.moveUp(field);
					}
				});
				panel.getActionMap().put("down", new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						p2.moveDown(field);
					}
				});
				// END OF KEY BINDINGS
				add(panel, BorderLayout.CENTER);
			}
		};
		gameThread.start(); // Invoke GaemThread.run()
		player1Thread.start();
		player2Thread.start();
	}

	/**
	 * One game time-step. Update the game objects, with proper collision
	 * detection and response.
	 */
	public void gameUpdate( Thread gameThread) {
		ball.moveOneStepWithCollisionDetection(field, p1, p2, gameThread);
	}

	/** The custom drawing panel for the bouncing ball (inner class). */
	class DrawCanvas extends JPanel {
		/** Custom drawing codes */
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g); // Paint background
			// Draw the box and the ball
			field.draw(g);
			ball.draw(g);
			p1.draw(g);
			p2.draw(g);
			// Display ball's information
			g.setColor(Color.WHITE);
			g.setFont(new Font("Courier New", Font.PLAIN, 18));
			g.drawString(p1Score + " : " + p2Score, 450, 30);
		}

		/** Called back to get the preferred size of the component. */
		@Override
		public Dimension getPreferredSize() {
			return (new Dimension(canvasWidth, canvasHeight));
		}
	}
}