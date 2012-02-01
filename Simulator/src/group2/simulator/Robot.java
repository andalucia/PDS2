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

	public void moveForward(World w, Body b) {
		move(w,b, 3);
	}
	
	public void moveBackwards(World w, Body b) {
		move(w,b,-3);
	}
	
	public void move(World world, Body ball, int mult)
	{
		boolean squashingBall =false;
		Ball b = (Ball)ball.getUserData();
		// This bit is not working - it's supposed to detect another body
		if (isCloseToFront(b)) {
			//An arbiter resolves conflicts between a pair of bodies
			ArbiterList arbs = world.getArbiters();
			for (int i=0;i<arbs.size();i++) {
				Arbiter arb = arbs.get(i);
				System.out.println(arb);
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
								    System.out.println("over the ball");
								}
							}
						}
					}
				}
			}
		}
		if(squashingBall==false)
		{
		int dist = speed * mult;
		double tempAngle = this.getAngle();
		float x = (this.getX() + (dist * (float) Math.cos(Math.toRadians(tempAngle))));
		float y = (this.getY() + (dist * (float) Math.sin(Math.toRadians(tempAngle))));
		this.body.move(x, y);
		}
	}
	
	// negative for right, positive for left
	public void turn(int value) {
		if (value > 0) {
			this.setAngle(this.getAngle() - 1);
		} else if (value < 0) {
			this.setAngle(this.getAngle() + 1);
		}
		// rotates the body
		this.getBody().setRotation((float) Math.toRadians(getAngle()));
	
	}
}