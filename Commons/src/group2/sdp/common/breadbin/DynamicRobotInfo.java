package group2.sdp.common.breadbin;

import java.awt.geom.Point2D;

/**
 * A class that represents robot information that is extracted from multiple, time consequent
 * images.
 */
public class DynamicRobotInfo extends StaticRobotInfo {
	/**
	 * The speed at which the robot is travelling. Units are cm/s.
	 */
	private double travelSpeed;
	/**
	 * The direction in which the robot is travelling. 
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 */
	private double travelDirection;
	
	
	/**
	 * A constructor taking all the information needed to build a complete object as arguments.
	 * @param position The position of the robot w.r.t. the centre of the field. Units are cm.
	 * @param facingDirection The direction at which the T shape on top of the robot is pointing.
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @param alfie Indicates if the robot is Alfie or his opponent.
	 * @param travelSpeed The speed at which the robot is travelling. Units are cm/s.
	 * @param travelDirection The speed at which the robot is travelling. Units are cm/s.
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 */
	public DynamicRobotInfo(Point2D position, double facingDirection, 
			boolean alfie, double travelSpeed, double travelDirection) {
		super(position, facingDirection, alfie);
		this.travelSpeed = travelSpeed;
		this.travelDirection = travelDirection;
	}
	
	/**
	 * A constructor that builds on a given static robot info.
	 * @param info The info to build on.
	 * @param travelSpeed The speed at which the robot is travelling. Units are cm/s.
	 * @param travelDirection The speed at which the robot is travelling. Units are cm/s.
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 */
	public DynamicRobotInfo(StaticRobotInfo info, double travelSpeed, double travelDirection) {
		super(info);
		this.travelSpeed = travelSpeed;
		this.travelDirection = travelDirection;
	}

	
	/**
	 * Get the speed at which the robot is travelling. Units are cm/s.
	 * @return The speed at which the robot is travelling. Units are cm/s.
	 */
	public double getTravelSpeed() {
		return travelSpeed;
	}
	
	/**
	 * Set the speed at which the robot is travelling. Units are cm/s.
	 * @param travelSpeed The speed at which the robot is travelling. Units are cm/s.
	 */
	public void setTravelSpeed(double travelSpeed) {
		this.travelSpeed = travelSpeed;
	}
	
	/**
	 * Get the direction in which the robot is travelling. 
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @return The direction in which the robot is travelling.
	 */
	public double getTravelDirection() {
		return travelDirection;
	}
	
	/**
	 * Set the direction in which the robot is travelling. 
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @param travelDirection The direction in which the robot is travelling.
	 */
	public void setTravelDirection(double travelDirection) {
		this.travelDirection = travelDirection;
	}
	
	
}
