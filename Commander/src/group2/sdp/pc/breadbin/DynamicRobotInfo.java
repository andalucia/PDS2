package group2.sdp.pc.breadbin;

import java.awt.geom.Point2D;

/**
 * Description: Extends the Static Robot Info type, thus containing all of SRI's
 *               contents. Also adds information about a robot that is 
 *               frame-specific, but that can only be extracted from multiple 
 *               frames.
 * Contains:    Physical attributes (width, height), absolute position and 
 *               orientation, the robot being Alfie or not, time-stamp, 
 *              'derivative' or movement information (speed, direction of travel)
 */
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
	 * @param timeStamp The time, in milliseconds, at which the information 
	 * was recorded.
	 */
	public DynamicRobotInfo(Point2D position, double facingDirection, 
			boolean alfie, boolean hasBall, double travelSpeed, 
			double travelDirection, long timeStamp) {
		super(position, facingDirection, alfie, hasBall, timeStamp);
		this.travelSpeed = travelSpeed;
		this.travelDirection = travelDirection;
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
	 */
	public DynamicRobotInfo(StaticRobotInfo info, double travelSpeed, 
			double travelDirection) {
		super(info);
		this.travelSpeed = travelSpeed;
		this.travelDirection = travelDirection;
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
}
