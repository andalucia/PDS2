package group2.simulator;



import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;

import net.phys2d.raw.Arbiter;
import net.phys2d.raw.ArbiterList;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;
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

	public Boolean isCloseToFront(Ball ball){
		AffineTransform at = AffineTransform.getRotateInstance(
				(Math.toRadians(this.getAngle()*(-1))), getX(), getY());
		Point2D.Double p = new Point2D.Double((double) ball.getX(), (double) ball.getY());
		at.transform(p,p);
		int kickDist = 10;
		if ( (Math.abs(p.getX() - getX() - (xSize/2)) <= kickDist) && (Math.abs(p.getY() - getY()) <= (ySize/2)) ) {
			
			return true;
		}
		return false;
	}
	// a copy isCloseTofront just with changed kickDist value - must be refactored later
	// really sorry for messy code :(
	public boolean canRobotKick(Ball ball){
		AffineTransform at = AffineTransform.getRotateInstance(
				(Math.toRadians(this.getAngle()*(-1))), getX(), getY());
		Point2D.Double p = new Point2D.Double((double) ball.getX(), (double) ball.getY());
		at.transform(p,p);
		int kickDist = 20;
		if ( (Math.abs(p.getX() - getX() - (xSize/2)) <= kickDist) && (Math.abs(p.getY() - getY()) <= (ySize/2)) ) {
			
			return true;
			
		}
		return false;
	}

	public void kick(Ball ball) {
		if (isCloseToFront(ball)){
			ball.kick(getAngle());
		} else {
			System.out.println("Cannot kick - the ball is too far away");
		}
	}



	public BufferedImage getImage() {
		return img;
	}

	public void moveForward(World w, Ball b) {
		move(w,b, 3);
	}
	
	public void moveBackwards(World w, Ball b) {
		move(w,b,-3);
	}
	
	/*
	 * Calculating robot' next position
	 * 
	 */
	
	public float countNextPositionX(float x, int dist, double angle)
	{
		return x + (dist * (float) Math.cos(Math.toRadians(angle)));
	}
	
	public float countNextPositionY(float y, int dist, double angle)
	{
		return y + (dist * (float) Math.sin(Math.toRadians(angle)));
	}
	
	
	/*
	 * 
	 *  Deleted a large part of move fucntion as i found it faulty
	 *  Counting next position of the ball.
	 *  
	 *  Function moves robot and ball provided 
	 *  it is moving forward and ball is in front
	 *  
	 *  TO DO: PUSHING BACKWARDS AND SIDES
	 */
	public void move(World world, Ball ball, int mult)
	{
		
		int dist = speed * mult;
		double tempAngle = this.getAngle();
		float x = countNextPositionX(this.getX(),dist,tempAngle);
		float y = countNextPositionY(this.getY(),dist,tempAngle);
		
		if (isCloseToFront(ball) && dist > 0) {
			
		
			float ballCoordX = countNextPositionX(ball.getX(),dist,tempAngle);
			float ballCoordY = countNextPositionY(ball.getY(),dist,tempAngle);
			this.body.move(x, y);
			ball.body.move(ballCoordX, ballCoordY);
		}
			else
		{
				this.body.move(x, y);
		}
	}
	
	// negative for left, positive for right
	public void turn(int value) {
		if (value > 0) {
			this.setAngle(this.getAngle() + 5);
		} else if (value < 0) {
			this.setAngle(this.getAngle() - 5);
		}
		// rotates the body
		this.getBody().setRotation((float) Math.toRadians(getAngle()));
	
	}
}