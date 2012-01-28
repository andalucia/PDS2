package group2.simulator;

import java.awt.Color;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;

public class Ball extends BoardObject {

	float radius;
	Body leftGoalLine;
	Body rightGoalLine;

	public Ball(float x, float y, float radius, Color color, int angle) {
		super(x, y, "Ball", new Circle(radius), 5, color, angle);
		this.body.setUserData(this);
		this.body.setDamping(0.005f);
		this.body.setRestitution(0.8f);
		this.body.setCanRest(true);
		this.radius = radius;
	}
	
	public void kick(double d) {
		int force = 10000;
		double angle = d;
		float x = (force * (float) Math.cos(Math.toRadians(angle)));
		float y = (force * (float) Math.sin(Math.toRadians(angle)));
		this.body.addForce(new Vector2f(x,y));
	}
	
	public void stop() {
		this.body.setForce(0, 0);
		this.body.adjustVelocity(((Vector2f)this.body.getVelocity()).negate());
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
	
}
