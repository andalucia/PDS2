package group2.simulator.physical;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;

import net.phys2d.raw.Arbiter;
import net.phys2d.raw.ArbiterList;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.World;

public class Robot extends BoardObject {

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
	
	public void setSpeed(int newSpeed){
		if (speed > 50)
			speed = 5;
		else
			speed = 3;
	}
	// TODO not used atm
	// finds the positions of both wheels, used for sweeping movement
//	public void findWheelCoordinates() {
////		Shape b = getShape();
////		xRightWheel = (int) b.getBounds2D().getX();
////		yRightWheel = (int) b.getBounds2D().getY();
////		Box bb = (Box)b;
////		bb.getPoints(pos, rotation)
//		Box b = (Box)this.body.getShape();
//		Vector2f[] pts = ((Box) b).getPoints(this.body.getPosition(), this.body.getRotation());
//		xLeftWheel = (int) pts[3].getX();
//		yLeftWheel = (int) pts[3].getY();
//		xRightWheel = (int) pts[0].getX();
//		yRightWheel = (int) pts[0].getY();
//	}
	
	public void sweep(int direction){ // direction should be 1 or -1 only, same as for turning
		int radius = (int) Math.sqrt((Math.pow(xSize, 2) + Math.pow(ySize, 2)))/2;
		turn(direction);
		double dist = (1*Math.PI*radius)/180;
		double tempAngle = this.getAngle()+(55 * direction);
		float x = (float) (this.getX() + (dist * Math.cos(Math.toRadians(tempAngle))));
		float y = (float) (this.getY() + (dist * Math.sin(Math.toRadians(tempAngle))));
		this.body.move(x, y);
	}
	
	public void moveBackward(World w, Body b) {
		move(w,b,-1);
	}
	
	public void moveForwards(World w, Body b) {
		move(w,b,1);
	}
	
	private void move(World world, Body ball, int mult) {
		// moves 5px forwards unless touching a wall
		Ball b = (Ball)ball.getUserData();
		boolean squashingBall = false;
		if (isCloseToFront(b)) {
			ArbiterList arbs = world.getArbiters();
			for (int i=0;i<arbs.size();i++) {
				Arbiter arb = arbs.get(i);
				if (arb.concerns(this.body) && (arb.concerns(ball))) {
					float x = arb.getContacts()[0].getPosition().getX();
					float y = arb.getContacts()[0].getPosition().getY();
					x = b.getX() + (x-b.getX());
					y = b.getY() + (y-b.getY());
					for (int j=0;j<arbs.size(); j++) {
						Arbiter a = arbs.get(j);
						if (a.concerns(ball) && (!(a.concerns(this.body)))) {
							if (Math.abs(a.getContacts()[0].getPosition().getX() - x) < 2) {
					            if (Math.abs(a.getContacts()[0].getPosition().getY() - y) < 2) {
								    squashingBall = true;
								}
							}
						}
					}
				}
			}
		}
		if (squashingBall == false) {
			int dist = speed * mult;
			double tempAngle = this.getAngle();
			float x = (this.getX() + (dist * (float) Math.cos(Math.toRadians(tempAngle))));
			float y = (this.getY() + (dist * (float) Math.sin(Math.toRadians(tempAngle))));
			this.body.move(x, y);
		}
		try { 
			Thread.sleep(30);
		} catch (Exception e) {
			System.out.println("Exception when trying to sleep: "
					+ e.toString());
		}
	}
	
	// will calculate the arch, then go to next point on the arch with help of
	// move() method
	public void moveInArch(World world, Body ball, int radius) {
		// used formula:
		// this arc is part of a circle
		// radius is defining how large the arc is going to be
		// x = a + radius * cos(arcAngle); where 'a' is offset from (0,0) point and arcAngle is angle given that arc length is the value of variable dist
		// y coordinate is calculated in same way but with sin instead of cos
		// arcAngle = (180 * arcLength) / (pi * radius); arcAngle is in degrees
		// arcAngle is added to the robots current angle until stop command is received (currently until robot has "arced" for 180 degrees)
		double arcAngle;
		if (radius > 1000){
			radius = radius - 1000;
			arcAngle = - (180 * speed / (Math.PI * radius));
		} else
			arcAngle = 180 * speed / (Math.PI * radius);
		double t = this.getAngle() + arcAngle;
		this.setAngle((float)t);
		// reusing the move() method for the actual move to happen
		this.moveForwards(world, ball);
	}
	// The returned Shape object is the one displayed on the screen,
	// the actual rotation of the body is happening in turn() method
	public Shape getShape() {
		AffineTransform at = AffineTransform.getRotateInstance(
				(Math.toRadians(this.getAngle())), getX(),
				getY());
		this.rectangle = new Rectangle2D.Double(getX()-xSize/2, getY()-ySize/2,
				xSize, ySize);
		return at.createTransformedShape(this.rectangle);
	}
	
	public BufferedImage getImage() {
		return img;
	}
	
	// negative for right, positive for left
	public void turn(int value) {
		if (value < 0) {
			this.setAngle(this.getAngle() - (-value));
		} else if (value > 0) {
			this.setAngle(this.getAngle() + value);
		}
		// rotates the body
		this.getBody().setRotation((float) Math.toRadians(getAngle()));
	
	}
	
	// For some reason body.setRoation() method rotates the body in the opposite
	// direction, but the object on the screen is correctly displayed (the
	// ball will just bounce in the opposite direction)
	// Therefore this method will reflect the angle along x-axis to find the
	// real angle which will be needed by the planning system
	public float getRealAngle() {
		float angle = (float) (360 - Math.toDegrees(this.getBody().getRotation()));
		if (angle == 360)
			return 0;
		else
			return angle;
	}
	
	// computes if ball is within robotWidth * 'kickDist' square in front of robot and if so returns true
	public Boolean isCloseToFront(Ball ball){
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
			System.out.println("Simulator: Can't kick, too far away from the ball");
		}
	}
	
	public Point2D getPosition() {
		Point2D.Float position = new Point2D.Float(this.getX(), this.getY());
		return position;
	}

	public double getFacingDirection(){
		return this.getAngle();
	}
}
