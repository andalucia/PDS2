package group2.simulator;

import java.awt.Color;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import net.phys2d.raw.shapes.Box;


public class Robot extends BoardObject{

	final int xSize;
	final int ySize;
	Double rectangle;
	int speed = 3;
	BufferedImage img = null;

	public Robot(int x, int y, int xSize, int ySize, Color color, BufferedImage img,
			int angle) {
		super(x, y, "Robot", new Box(xSize, ySize), 10000000, color, angle);
		this.img = img;
		this.body.setUserData(this);
		this.xSize = xSize;
		this.ySize = ySize;
		this.body.setFriction(100);
		this.body.setRestitution(1.0f);
		this.body.setDamping(2f);
		this.body.setRotatable(false);
	}

	public int getLength() {
		return xSize;
	}
	
	public int getWidth() {
		return ySize;
	}
	
	public BufferedImage getImage() {
		return img;
	}
	
	public void move()
	{
		int dist = speed * 1;
		double tempAngle = this.getAngle();
		float x = (this.getX() + (dist * (float) Math.cos(Math.toRadians(tempAngle))));
		float y = (this.getY() + (dist * (float) Math.sin(Math.toRadians(tempAngle))));
		this.body.move(x, y);
	}
}
