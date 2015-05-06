package javaLabs;

import java.awt.*;
import java.util.Formatter;

/**
 * The bouncing ball.
 */
public class Ball {
	float x, y; // Ball's center x and y (package access)
	float speedX, speedY; // Ball's speed per step in x and y (package access)
	float radius; // Ball's radius (package access)
	private Color color; // Ball's color

	/**
	 * Constructor: For user friendliness, user specifies velocity in speed and
	 * moveAngle in usual Cartesian coordinates. Need to convert to speedX and
	 * speedY in Java graphics coordinates for ease of operation.
	 */
	public Ball(float x, float y, float radius, float speed,
			float angleInDegree, Color color) {
		this.x = x;
		this.y = y;
		// Convert (speed, angle) to (x, y), with y-axis inverted
		this.speedX = (float) (speed * Math.cos(Math.toRadians(angleInDegree)));
		this.speedY = (float) (-speed * (float) Math.sin(Math.toRadians(angleInDegree)));
		this.radius = radius;
		this.color = color;
	}

	/** Draw itself using the given graphics context. */
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillOval((int) (x - radius), (int) (y - radius), (int) (2 * radius),
				(int) (2 * radius));
	}

	/**
	 * Make one move, check for collision and react accordingly if collision
	 * occurs.
	 * 
	 * @param box
	 *            : the container (obstacle) for this ball.
	 */
	public void moveOneStepWithCollisionDetection(GameField box, Player1 p1, Player2 p2, Thread mainThread) {
		// Get the ball's bounds, offset by the radius of the ball
		float ballMinX = box.minX + radius;
		float ballMinY = box.minY + radius;
		float ballMaxX = box.maxX - radius;
		float ballMaxY = box.maxY - radius;

		// Calculate the ball's new position
		x += speedX;
		y += speedY;
		// Check if the ball moves over the bounds. If so, adjust the position
		// and speed.
		
		if (x < p1.x + p1.width + radius
				&& y > p1.y
				&& y < p1.y + p1.height) {
			speedX = -speedX; // Reflect along normal
			x = p1.x + p1.width + radius + 1; // Re-position the ball at the edge
		} else if (x > p2.x - radius
				&& y > p2.y
				&& y < p2.y + p2.height) {
			speedX = -speedX;
			x = p2.x - radius - 1;
		}
		
		if (x < ballMinX) {
			speedX = -speedX; // Reflect along normal
			try {
				mainThread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			x = (box.maxX - box.minX) / 2;
			y = (box.maxY - box.minY) / 2;
			GameLogic.p2Score++;
			
		} else if (x > ballMaxX) {
			speedX = -speedX;
			try {
				mainThread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			x = (box.maxX - box.minX) / 2;
			y = (box.maxY - box.minY) / 2;
			GameLogic.p1Score++;
		}
		// May cross both x and y bounds
		if (y < ballMinY) {
			speedY = -speedY;
			y = ballMinY;
		} else if (y > ballMaxY) {
			speedY = -speedY;
			y = ballMaxY;
		}
	}

	/** Return the magnitude of speed. */
	public float getSpeed() {
		return (float) Math.sqrt(speedX * speedX + speedY * speedY);
	}

	/** Return the direction of movement in degrees (counter-clockwise). */
	public float getMoveAngle() {
		return (float) Math.toDegrees(Math.atan2(-speedY, speedX));
	}

	/** Return mass */
	public float getMass() {
		return radius * radius * radius / 1000f; // Normalize by a factor
	}

	/** Return the kinetic energy (0.5mv^2) */
	public float getKineticEnergy() {
		return 0.5f * getMass() * (speedX * speedX + speedY * speedY);
	}

	/** Describe itself. */
	public String toString() {
		sb.delete(0, sb.length());
		formatter.format("@(%3.0f,%3.0f) r=%3.0f V=(%2.0f,%2.0f) "
				+ "S=%4.1f \u0398=%4.0f KE=%3.0f", x, y, radius, speedX,
				speedY, getSpeed(), getMoveAngle(), getKineticEnergy()); // \u0398
																			// is
																			// theta
		return sb.toString();
	}

	// Re-use to build the formatted string for toString()
	private StringBuilder sb = new StringBuilder();
	private Formatter formatter = new Formatter(sb);
}