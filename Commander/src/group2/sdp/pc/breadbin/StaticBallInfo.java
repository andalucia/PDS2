package group2.sdp.pc.breadbin;

import java.awt.geom.Point2D;

/**
 * Represents information that can be extracted for a ball from a single image.
 */
public class StaticBallInfo {
	
	/**
	 * The radius of the ball in cm.
	 */
	protected final static double BALL_RADIUS = 2;

	/**
	 * The position of the ball w.r.t the centre of the pitch.
	 * The units are cm.
	 */
	protected Point2D position;
	
	/**
	 * The time at which Info was recorded . Units are milliseconds. Set automatically
	 * in constructor and if position is updated
	 */
	protected long timeStamp;

	
	/**
	 * A constructor that sets the position of the ball w.r.t. the centre of 
	 * the pitch. Units are in cm.
	 * @param position The position of the ball. Units are in cm.
	 */
	public StaticBallInfo(Point2D position) {
		super();
		this.position = position;
		this.timeStamp=System.currentTimeMillis();
	}

	/**
	 * Set the position of the ball w.r.t. the centre of the pitch. Units are in cm.
	 * @param position The position of the ball w.r.t. the centre of the pitch. Units are in cm.
	 */
	public void setPosition(Point2D position) {
		this.position = position;
		this.timeStamp=System.currentTimeMillis();
	}
	
	/**
	 * Get the position of the ball w.r.t. the centre of the pitch. Units are in cm.
	 * @return The position of the ball w.r.t. the centre of the pitch. Units are in cm.
	 */
	public Point2D getPosition() {
		return position;
	}
	
	/**
	 * Get the timeStamp at which info was recorded. milliseconds
	 * @return The timeStamp at which info was recorded. Units ar milliseconds
	 */
	public long getTimeStamp(){
		return timeStamp;
	}
	
	/**
	 * Gets the radius of the ball in cm.
	 * @return The radius of the ball in cm.
	 */
	public static double getBallRadius() {
		return BALL_RADIUS;
	}	
}
