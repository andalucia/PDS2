package group2.sdp.common.breadbin;

import java.awt.geom.Point2D;

/**
 * A class that represents information about a robot that is obtainable from a single image.
 */
public class StaticRobotInfo {
	/**
	 * The length of the robot.
	 */
	protected static final double LENGTH = 20;
	/**
	 * The width of the robot.
	 */
	protected static final double WIDTH = 18;
	
	/**
	 * The position of the robot w.r.t. the centre of the pitch. Units are in cm.
	 */
	protected Point2D position;
	/**
	 * The direction at which the T shape on top of the robot is pointing.
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 */
	protected double facingDirection;
	/**
	 * Indicates if the robot is Alfie or his opponent.
	 */
	protected boolean alfie;
	
	
	/**
	 * A constructor that takes all the needed information for a complete object as arguments.
	 * @param position The position of the robot w.r.t. the centre of the pitch. Units are in cm.
	 * @param facingDirection The direction at which the T shape on top of the robot is pointing.
	 * @param alfie Indicates if the robot is Alfie or his opponent.
	 */
	public StaticRobotInfo(Point2D position, double facingDirection, boolean alfie) {
		this.position = position;
		this.facingDirection = facingDirection;
		this.alfie = alfie;
	}

	/**
	 * A copy constructor.
	 * @param info The info to copy.
	 */
	public StaticRobotInfo(StaticRobotInfo info) {
		this.position = info.position;
		this.facingDirection = info.facingDirection;
		this.alfie = info.alfie;
	}

	
	/**
	 * Get the position of the robot w.r.t. the centre of the pitch. Units are in cm.
	 * @return The position of the robot w.r.t. the centre of the pitch. Units are in cm.
	 */
	public Point2D getPosition() {
		return position;
	}
	
	/**
	 * Set the position of the robot w.r.t. the centre of the pitch. Units are in cm.
	 * @param position The position of the robot w.r.t. the centre of the pitch. Units are in cm.
	 */
	public void setPosition(Point2D position) {
		this.position = position;
	}
	
	/**
	 * Get the direction at which the T shape on top of the robot is pointing.
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @return The direction at which the T shape on top of the robot is pointing.
	 */
	public double getFacingDirection() {
		return facingDirection;
	}
	
	/**
	 * Set the direction at which the T shape on top of the robot is pointing.
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @param facingDirection The direction at which the T shape on top of the robot is pointing.
	 */
	public void setFacingDirection(double facingDirection) {
		this.facingDirection = facingDirection;
	}
	
	/**
	 * Get the length of the robot.
	 * @return The length of the robot.
	 */
	public static double getLength() {
		return LENGTH;
	}
	
	/**
	 * Get the width of the robot.
	 * @return The width of the robot.
	 */
	public static double getWidth() {
		return WIDTH;
	}
}
