package group2.sdp.pc.breadbin;

import java.awt.geom.Point2D;

/**
 * A class that represents ball information that is extracted from multiple, time consequent
 * images.
 */
public class DynamicBallInfo extends StaticBallInfo {
	
	/**
	 * The speed at which the ball is moving. Units are cm/s.
	 */
	private double rollingSpeed;
	/**
	 * The direction at which the ball is moving. 
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 */
	private double rollingDirection;
	


	
	/**
	 * Construct a dynamic ball info giving all the information that is required for a complete object.
	 * @param position The position of the ball w.r.t. the centre of the pitch. Units are cm.
	 * @param rollingSpeed The speed at which the ball is moving. Units are cm/s.
	 * @param rollingDirection The direction in which the ball is moving.
	 * @param timeStamp The time at which Info was recorded . Units are milliseconds.
	 *  Units are degrees.
	 *  The range is [0, 360)
	 *  3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 *  Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 */
	public DynamicBallInfo(Point2D position, double rollingSpeed, double rollingDirection, long timeStamp) {
		super(position,timeStamp);
		this.rollingDirection = rollingDirection;
		this.rollingSpeed = rollingSpeed;
		
	}

	
	/**
	 * Get the speed at which the ball is moving. Units are cm/s.
	 * @return The speed at which the ball is moving. Units are cm/s.
	 */
	public double getRollingSpeed() {
		return rollingSpeed;
	}
	
	/**
	 * Set the speed at which the ball is moving. Units are cm/s.
	 * @param rollingSpeed The speed at which the ball is moving. Units are cm/s.
	 */
	public void setRollingSpeed(double rollingSpeed) {
		this.rollingSpeed = rollingSpeed;
	}
	
	/**
	 * Get the direction at which the ball is moving. 
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @return The direction at which the ball is moving. 
	 */
	public double getRollingDirection() {
		return rollingDirection;
	}

	/**
	 * Set the direction at which the ball is moving. 
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @param rollingDirection The direction at which the ball is moving.
	 */
	public void setRollingDirection(double rollingDirection) {
		this.rollingDirection = rollingDirection;
	}

	
}
