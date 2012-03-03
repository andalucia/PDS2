package group2.sdp.pc.breadbin;

import java.awt.geom.Point2D;


/**
 * Description: Information about the ball that is frame-specific and can be
 *               extracted from a single frame (as opposed to multiple frames).
 * Contains:    Physical attributes (radius), absolute position, and time-stamp.
 */
public class StaticBallInfo {
	
	/**
	 * The radius of the ball in centimetres.
	 */
	protected final static double BALL_RADIUS = 2;

	/**
	 * The position of the ball with respect to the centre of the pitch. The 
	 * units are centimetres.
	 */
	protected Point2D position;
	
	/**
	 * The time, in milliseconds, at which the information was recorded.  
	 */
	protected long timeStamp;

	
	/**
	 * Fully initialising constructor.
	 * @param position The position of the ball. The units are centimetres.
	 * @param timeStamp The time, in milliseconds, at which Info was recorded.
	 */
	public StaticBallInfo(Point2D position, long timeStamp) {
		super();
		this.position = position;
		this.timeStamp = timeStamp;
	}

	
	/**
	 * Set the position of the ball with respect to the centre of the pitch. 
	 * The units are centimetres.
	 * @param position The position of the ball with respect to the centre of 
	 * the pitch. The units are centimetres.
	 */
	public void setPosition(Point2D position) {
		this.position = position;
	}
	
	/**
	 * Get the position of the ball with respect to the centre of the pitch. 
	 * The units are centimetres.
	 * @return The position of the ball with respect to the centre of the 
	 * pitch. The units are centimetres.
	 */
	public Point2D getPosition() {
		return position;
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
	 * Gets the radius of the ball in centimetres.
	 * @return The radius of the ball in centimetres.
	 */
	public static double getBallRadius() {
		return BALL_RADIUS;
	}	
}
