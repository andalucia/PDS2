package group2.simulator.physical;

import java.awt.Color;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;

public class Ball extends BoardObject {

	public static int boardWidth = 630;
	public static int boardHeight = 330;
	public static int padding = 100;
	public static int wallThickness = 20;
	public static int goalWidth = 144;
	public static int goalThickness = 50;

	
	float radius;
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
	public void kick(double angle) {
		int force = 1000;
		float x = (force * (float) Math.cos(Math.toRadians(angle)));
		float y = (force * (float) Math.sin(Math.toRadians(angle)));
		this.body.addForce(new Vector2f(x, y));
		this.move(angle);

	}
		
	/**
	 * Move the ball forward by distance dist
	 * @param angle
	 */
	public void move(double angle) {
		int dist = 300;
		float x = (this.getX() + (dist * (float) Math
				.cos(Math.toRadians(angle))));
		float y = (this.getY() + (dist * (float) Math
				.sin(Math.toRadians(angle))));
		this.body.setPosition(x, y);
		
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

	public boolean doesItHitWall() {
		float x = this.getX();
		float y = this.getY();

		if (x < 107 || x > 720 || y < 112 || y > 420 )
			return true;
		else
			return false;
	}

	public float checkXPosition(float x) {
		if (x < 105)
			return 105;
		else if (x > 720)
			return 720;
		return x;
	}

	public float checkYPosition(float y) {
		if (y < 110 )
			return 110;
		else if (y > 422)
			return 422;
		return y;
	}

}