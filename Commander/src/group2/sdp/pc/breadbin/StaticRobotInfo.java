package group2.sdp.pc.breadbin;

import java.awt.geom.Point2D;

/**
 * Description: Information about a robot that is frame-specific and can be
 *               extracted from a single frame (as opposed to multiple frames).
 * Contains:    Physical attributes (width, height), absolute position and 
 *               orientation, the robot being Alfie or not, time-stamp
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
	 * The position of the robot with respect to the centre of the pitch. The 
	 * units are centimetres.
	 */
	protected Point2D position;

	/**
	 * The time, in milliseconds, at which the information was recorded.
	 */
	protected long timeStamp;

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
	 * A constructor that takes all the needed information for a complete 
	 * object as arguments.
	 * @param position The position of the robot with respect to the centre 
	 * of the pitch. The units are centimetres.
	 * @param facingDirection The direction, in degrees, at which the T shape 
	 * on the top of the robot is pointing at. The range is [0, 360), 3 o'clock
	 * is 0 degrees, and the angle grows counter clock-wise. Thus 12 o'clock is
	 * 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @param alfie Indicates if the robot is Alfie or his opponent.
	 * @param timeStamp The time, in milliseconds, at which the information 
	 * was recorded.
	 */
	public StaticRobotInfo(Point2D position, double facingDirection, 
			boolean alfie, long timeStamp) {
		this.position = position;
		this.facingDirection = facingDirection;
		this.alfie = alfie;
		this.timeStamp=timeStamp;
	}

	/**
	 * A copy constructor.
	 * @param info The info to copy.
	 */
	public StaticRobotInfo(StaticRobotInfo info) {
		this.position = info.position;
		this.facingDirection = info.facingDirection;
		this.alfie = info.alfie;
		this.timeStamp= info.timeStamp;	
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
	 * Get the time, in milliseconds, at which the information was recorded.
	 * @return The time, in milliseconds, at which the information was 
	 * recorded.
	 */
	public long getTimeStamp(){
		return timeStamp;
	}

	/**
	 * Returns true if robot is Alfie
	 * @return Is the robot Alfie
	 */
	public boolean isAlfie() {
		return alfie;
	}
}


