package javaLabs;

import java.awt.*;

/**
 * A rectangular container box, containing the bouncing ball.
 */
public class Player1
{
	int x, width, y, height; // Box's bounds (package access)
	private Color colorFilled; // Box's filled color (background)
	private Color colorBorder; // Box's border color

	/** Constructors */
	public Player1(int x, int y, int width, int height, Color colorFilled, Color colorBorder)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.colorFilled = colorFilled;
		this.colorBorder = colorBorder;
	}

	/** Set or reset the boundaries of the box. */
	public void set(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height =height;
	}
	
	public void moveDown(GameField field)
	{
		y += 25;
		if( y + height > field.maxY) y = field.maxY - height;
	}

	public void moveUp(GameField field)
	{
		y -= 25;
		if(y < field.minY) y = field.minY;
	}
	
	/** Draw itself using the given graphic context. */
	public void draw(Graphics g)
	{
		g.setColor(colorFilled);
		g.fillRect(x, y, width, height);
		g.setColor(colorBorder);
		g.drawRect(x, y, width, height);
	}
}