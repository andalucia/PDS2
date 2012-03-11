package group2.sdp.pc.breadbin;

import java.awt.geom.Point2D;

/**
 * <p><b>Description:</b></br>
 * Extends the {@link StaticRobotInfo} type, thus containing all of
 * StaticRobotInfo's contents. Also adds information about a robot that is 
 * frame-specific, but that can only be extracted from multiple frames.</p>
 * <p><b>Contains:</b></br>
 * Physical attributes (width, height), absolute position and 
 * orientation, is the robot Alfie or not, time-stamp, 
 * 'derivative' or movement information (speed, direction of 
 * travel, turning speed and direction), and whether the robot is 
 * kicking the ball or not.</p>
 */

//TODO Check whether the robot is kicking the ball or not
// (do this by checking if the robot had the ball and if
//the ball suddenly accelerated)

public class DynamicRobotInfo extends StaticRobotInfo {
	/**
	 * The speed, in centimetres per second, at which the robot is travelling.
	 */
	private double travelSpeed;
	/**
	 * The direction in which the robot is travelling. The range is [0, 360), 
	 * 3 o'clock is 0 degrees, and the angle grows counter clock-wise. Thus 12 
	 * o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 
	 * degrees.
	 */
	private double travelDirection;
	/**
	 * The speed, in degrees per second, at which the robot is turning.
	 */
	private double rotatingSpeed;
	/**
	 * If the robot is rotating counter-clock-wise or clock-wise.	
	 */
	private boolean rotatingCounterClockWise;
	
	/**
	 * Fully initialising constructor.
	 * @param position The position of the robot with respect to the centre 
	 * of the pitch. The units are centimetres.
	 * @param facingDirection The direction, in degrees, at which the T shape 
	 * on the top of the robot is pointing at. The range is [0, 360), 3 o'clock
	 * is 0 degrees, and the angle grows counter clock-wise. Thus 12 o'clock is
	 * 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @param alfie Indicates if the robot is Alfie or his opponent.
	 * @param hasBall Indicates if the robot has the ball or not.
	 * @param travelSpeed The speed, in centimetres per second, at which the 
	 * robot is travelling.
	 * @param travelDirection The direction in which the robot is travelling. 
	 * The range is [0, 360), 3 o'clock is 0 degrees, and the angle grows 
	 * counter clock-wise. Thus 12 o'clock is 90 degrees, 9 o'clock is 180 
	 * degrees and 6 o'clock is 270 degrees.
	 * @param rotatingSpeed The speed, in degrees per second, at which the 
	 * robot is turning.
	 * @param rotatingCounterClockWise If the robot is rotating 
	 * counter-clock-wise or clock-wise.
	 * @param timeStamp The time, in milliseconds, at which the information 
	 * was recorded.
	 */
	public DynamicRobotInfo(Point2D position, double facingDirection, 
			boolean alfie, boolean hasBall, double travelSpeed, 
			double travelDirection, double rotatingSpeed, 
			boolean rotatingCounterClockWise, long timeStamp) {
		super(position, facingDirection, alfie, hasBall, timeStamp);
		this.travelSpeed = travelSpeed;
		this.travelDirection = travelDirection;
		this.rotatingSpeed = rotatingSpeed;
		this.rotatingCounterClockWise = rotatingCounterClockWise;
	}
	
	/**
	 * A constructor that builds on a given static robot info.
	 * @param info The info to build on.
	 * @param travelSpeed The speed, in centimetres per second, at which the 
	 * robot is travelling.
	 * @param travelDirection The direction in which the robot is travelling. 
	 * The range is [0, 360), 3 o'clock is 0 degrees, and the angle grows 
	 * counter clock-wise. Thus 12 o'clock is 90 degrees, 9 o'clock is 180 
	 * degrees and 6 o'clock is 270 degrees.
	 * @param rotatingSpeed The speed, in degrees per second, at which the 
	 * robot is turning.
	 * @param rotatingCounterClockWise If the robot is rotating 
	 * counter-clock-wise or clock-wise.
	 */
	public DynamicRobotInfo(StaticRobotInfo info, double travelSpeed, 
			double travelDirection, double rotatingSpeed, 
			boolean rotatingCounterClockWise) {
		super(info);
		this.travelSpeed = travelSpeed;
		this.travelDirection = travelDirection;
		this.rotatingSpeed = rotatingSpeed;
		this.rotatingCounterClockWise = rotatingCounterClockWise;
	}

	
	/**
	 * Get the speed, in centimetres per second, at which the robot is 
	 * travelling.
	 * @return The speed, in centimetres per second, at which the robot is 
	 * travelling.
	 */
	public double getTravelSpeed() {
		return travelSpeed;
	}
	
	/**
	 * Set the speed, in centimetres per second, at which the robot is 
	 * travelling.
	 * @param travelSpeed The speed, in centimetres per second, at which the 
	 * robot is travelling.
	 */
	public void setTravelSpeed(double travelSpeed) {
		this.travelSpeed = travelSpeed;
	}
	
	/**
	 * Get the direction in which the robot is travelling. The range is 
	 * [0, 360), 3 o'clock is 0 degrees, and the angle grows counter 
	 * clock-wise. Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees 
	 * and 6 o'clock is 270 degrees.
	 * @return The direction in which the robot is travelling.
	 */
	public double getTravelDirection() {
		return travelDirection;
	}
	
	/**
	 * Set the direction in which the robot is travelling. The range is 
	 * [0, 360), 3 o'clock is 0 degrees, and the angle grows counter 
	 * clock-wise. Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees 
	 * and 6 o'clock is 270 degrees.
	 * @param travelDirection The direction in which the robot is travelling.
	 */
	public void setTravelDirection(double travelDirection) {
		this.travelDirection = travelDirection;
	}

	/**
	 * Get the speed, in degrees per second, at which the robot is turning.
	 * @return The speed, in degrees per second, at which the robot is turning.
	 */
	public double getRotatingSpeed() {
		return rotatingSpeed;
	}

	/**
	 * Set the speed, in degrees per second, at which the robot is turning.
	 * @param rotatingSpeed The speed, in degrees per second, at which the 
	 * robot is turning.
	 */
	public void setRotatingSpeed(double rotatingSpeed) {
		this.rotatingSpeed = rotatingSpeed;
	}

	/**
	 * True if the robot is rotating counter-clock-wise.
	 * @return True if the robot is rotating counter-clock-wise.
	 */
	public boolean isRotatingCounterClockWise() {
		return rotatingCounterClockWise;
	}

	/**
	 * Is the robot is rotating counter-clock-wise?
	 * @param rotatingCounterClockWise True if the robot is rotating 
	 * counter-clock-wise.
	 */
	public void setRotatingCounterClockWise(boolean rotatingCounterClockWise) {
		this.rotatingCounterClockWise = rotatingCounterClockWise;
	}
}
