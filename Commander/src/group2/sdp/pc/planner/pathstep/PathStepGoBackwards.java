package group2.sdp.pc.planner.pathstep;

import group2.sdp.pc.breadbin.DynamicInfo;

import java.awt.geom.Point2D;

/**
 * Act: Start moving backwards, possibly specifying the distance to be covered (but not
 * speed as this messes up arc movement).
 * 
 * Parameters:
 * A position to reach, threshold distance for success, threshold angle for failure.
 */
public class PathStepGoBackwards implements PathStep {

	private Point2D destination;
	private int distance;
	private int threshold;
	private int speed;
	
	public PathStepGoBackwards(Point2D destination, int distance, int threshold, int speed){
		this.destination = destination;
		this.distance = distance;
		this.threshold = threshold;
		this.speed = speed;
	}
	
	@Override
	public Type getType() {
		return Type.GO_BACKWARDS;
	}
	
	
	
	public Point2D getDestination(){
		return this.destination;
	}
	
	public int getDistance(){
		return this.distance;
	}
	
	public int getThreshold(){
		return this.threshold;
	}
	
	public int getSpeed(){
		return this.speed;
	}
	
	/**
	 * Succeed:
	 * If Alfie is within the specified threshold distance from the target point.
	 */
	@Override
	public boolean isSuccessful(DynamicInfo pitchStatus) {
		// TODO Auto-generated method stub 
		// logic yet to be added
		return false;
	}

	/**
	 *
     * Fail: If Alfie's facing direction is not within the specified threshold angle from the same
     * point.
	 */
	@Override
	public boolean problemExists(DynamicInfo pitchStatus) {
		// TODO Auto-generated method stub
		//Coming soon Logic!
		return false;
	}
}
