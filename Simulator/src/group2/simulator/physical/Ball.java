package group2.simulator.physical;

import java.awt.Color;
import java.awt.geom.Point2D;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;

 
public class Ball extends BoardObject {
	

	Point2D position;
	private float radius;

	Body leftGoalLine;
	Body rightGoalLine;
	

	/**
	 * Constructor that fully initialises the ball object
	 * @param x
	 * @param y
	 * @param radius
	 * @param color
	 * @param angle
	 */
	public Ball(float x, float y, float radius, Color color, int angle) {
		super(x, y, "Ball", new Circle(radius), 5, color, angle);
		this.body.setUserData(this);
		this.body.setDamping(0.005f);
		this.body.setRestitution(0.8f);
		this.body.setCanRest(true);
		this.radius = radius;
		
		
	}
	
	
	/**
	 * Function to kick the ball and change its position 
	 * @param angle is angle at which the ball is kicked
	 */
	public void kick(double d) {
		int force = 10000;
		double angle = d;
		float x = (force * (float) Math.cos(Math.toRadians(angle)));
		float y = (force * (float) Math.sin(Math.toRadians(angle)));
		this.body.addForce(new Vector2f(x,y));
	}
		
	
	/**
	 * Function that stops the ball
	 */
	public void stop() {
		this.body.setForce(0, 0);
		this.body.adjustVelocity(((Vector2f) this.body.getVelocity()).negate());
	}
	
	public void setGoalLines(Body leftGL, Body rightGL) {
		this.leftGoalLine = leftGL;
		this.rightGoalLine = rightGL;
	}

	public void ignoreGoalLines() {
		this.body.addExcludedBody(leftGoalLine);
		this.body.addExcludedBody(rightGoalLine);
	}

	public void stayInGoal() {
		this.body.removeExcludedBody(leftGoalLine);
		this.body.removeExcludedBody(rightGoalLine);
	}
	
	
	public Point2D getPosition() {
		Point2D.Float position = new Point2D.Float(this.getX(), this.getY());
		return position;
	}

	

}