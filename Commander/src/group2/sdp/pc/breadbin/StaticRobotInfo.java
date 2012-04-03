package group2.sdp.pc.breadbin;

import java.awt.geom.Point2D;

/**
 * Description: Information about a robot that is frame-specific and can be
 *               extracted from a single frame (as opposed to multiple frames).
 * Contains:    Physical attributes (width, height), absolute position and 
 *               orientation, the robot being Alfie or not, ball possession, and
 *               time-stamp.
 */
public class StaticRobotInfo {
	/**
	 * The centroid of alfie.
	 */
	protected static final Point2D ALFIE_CENTROID = new Point2D.Double(0, -2);
	
	/**
	 * The length of the robot.
	 */
	protected static final double LENGTH = 20;
	/**
	 * The width of the robot.
	 */
	protected static final double WIDTH = 17.5;
	/**
	 * The height of the robot.
	 */
	protected static final double HEIGHT = 19;
	/**
	 * The position of the robot with respect to the centre of the pitch. The 
	 * units are centimetres.
	 */
	protected Point2D position;
	/**
	 * The direction, in degrees, at which the T shape on the top of the robot 
	 * is pointing at. The range is [0, 360), 3 o'clock is 0 degrees, and the 
	 * angle grows counter clock-wise. Thus 12 o'clock is 90 degrees, 9 o'clock
	 * is 180 degrees and 6 o'clock is 270 degrees.
	 */
	protected double facingDirection;
	/**
	 * Indicates if the robot is Alfie or his opponent.
	 */
	protected boolean alfie;
	/**
	 * Indicates if the robot has the ball or not.
	 */
	protected boolean hasBall;
	/**
	 * The time, in milliseconds, at which the information was recorded.
	 */
	protected long timeStamp;
	/**
	 * The centroid of the T on top of the robot.
	 */
	protected Point2D centrePoint;

	
	/**
	 * A constructor that takes all the needed information for a complete 
	 * object as arguments.
	 * @param position The position of the robot with respect to the centre 
	 * of the pitch. The units are centimetres.
	 * @param facingDirection The direction, in degrees, at which the T shape 
	 * on the top of the robot is pointing at. The range is [0, 360), 3 o'clock
	 * is 0 degrees, and the angle grows counter clock-wise. Thus 12 o'clock is
	 * 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @param alfie Indicates if the robot is Alfie or his opponent.
	 * @param hasBall Indicates if the robot has the ball or not.
	 * @param timeStamp The time, in milliseconds, at which the information 
	 * was recorded.
	 */
	public StaticRobotInfo(Point2D position, double facingDirection, 
			boolean alfie, boolean hasBall, long timeStamp) {
		this.position = position;
		this.facingDirection = facingDirection;
		this.alfie = alfie;
		this.timeStamp = timeStamp;
		if (!alfie)
			this.centrePoint = new Point2D.Double(0.0, 0.0);
		else 
			this.centrePoint = ALFIE_CENTROID;
	}
	
	/**
	 * A copy constructor.
	 * @param info The info to copy.
	 */
	public StaticRobotInfo(StaticRobotInfo info) {
		this.position = info.position;
		this.facingDirection = info.facingDirection;
		this.alfie = info.alfie;
		this.timeStamp = info.timeStamp;
		this.hasBall = info.hasBall;
		if (!alfie)
			this.centrePoint = new Point2D.Double(0.0, 0.0);
		else 
			this.centrePoint = ALFIE_CENTROID;
	}


	/**
	 * Get the position of the robot with respect to the centre of the pitch. 
	 * The units are centimetres.
	 * @return The position of the robot with respect to the centre of the 
	 * pitch. The units are centimetres.
	 */
	public Point2D getPosition() {
		return position;
	}

	/**
	 * Set the position of the robot with respect to the centre of the pitch. 
	 * The units are centimetres.
	 * @param position The position of the robot with respect to the centre of 
	 * the pitch. The units are centimetres.
	 */
	public void setPosition(Point2D position) {
		this.position = position;
	}

	/**
	 * Get the direction, in degrees, at which the T shape on the top of the 
	 * robot is pointing at. The range is [0, 360), 3 o'clock is 0 degrees, 
	 * and the angle grows counter clock-wise. Thus 12 o'clock is 90 degrees, 
	 * 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @return The direction at which the T shape on the top of the robot is 
	 * pointing at.
	 */
	public double getFacingDirection() {
		return facingDirection;
	}

	/**
	 * Set the direction, in degrees, at which the T shape on the top of the 
	 * robot is pointing at. The range is [0, 360), 3 o'clock is 0 degrees, and
	 * the angle grows counter clock-wise. Thus 12 o'clock is 90 degrees, 9 
	 * o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @param facingDirection The direction at which the T shape on the top of 
	 * the robot is pointing at.
	 */
	public void setFacingDirection(double facingDirection) {
		this.facingDirection = facingDirection;
	}

	/**
	 * Get the width of the robot.
	 * @return The width of the robot.
	 */
	public static double getWidth() {
		return WIDTH;
	}
	
	/**
	 * Get the length of the robot.
	 * @return The length of the robot.
	 */
	public static double getLength() {
		return LENGTH;
	}
	
	/**
	 * Get the height of the robot.
	 * @return The height of the robot.
	 */
	public static double getHeight() {
		return HEIGHT;
	}

	/**
	 * Returns true if robot is Alfie.
	 * @return True if the robot is Alfie, false otherwise.
	 */
	public boolean isAlfie() {
		return alfie;
	}

	/**
	 * Returns true if the robot has the ball.
	 * @return True if the robot has the ball.
	 */
	public boolean isHasBall() {
		return hasBall;
	}

	/**
	 * Set the if the robot has the ball or not.
	 * @param hasBall If the robot has the ball or not.
	 */
	public void setHasBall(boolean hasBall) {
		this.hasBall = hasBall;
	}
	
	/**
	 * Get the time, in milliseconds, at which the information was recorded.
	 * @return The time, in milliseconds, at which the information was 
	 * recorded.
	 */
	public long getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * Get the coordinates of the centroid of the T, with respect to the 
	 * centre of the robot.
	 * @return The coordinates of the centroid of the T, with respect to the 
	 * centre of the robot.
	 */
	public Point2D getCentrePoint() {
		return centrePoint;
	}
	
	/**
	 * Gets the smallest distance from the centre of the robot, that parts of 
	 * it can not reach. (Smallest circle enclosing the robot.) 
	 * @return
	 */
	public double getSafeDistance() {
		double dx = Math.max(
				WIDTH / 2 - centrePoint.getX(), 
				WIDTH / 2 + centrePoint.getX()
		);
		double dy = Math.max(
				LENGTH / 2 - centrePoint.getY(), 
				LENGTH / 2 + centrePoint.getY()
		);
		return Math.sqrt(dx * dx + dy * dy);
	}

	@Override
	public String toString() {
		return "pos: " + position + " dir:" + facingDirection + " alfie?: " + alfie + 
				" has ball?: " + hasBall + " T: " + timeStamp;
	}
}


