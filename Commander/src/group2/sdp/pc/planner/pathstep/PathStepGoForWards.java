package group2.sdp.pc.planner.pathstep;

import java.awt.geom.Point2D;

/**
 * 
 * 
 * @author Shaun A.K.A the bringer of bad code!!! beware but chris was here so its kay :)
 *
 * Act: Tell Alfie to start moving forward, possibly specifying the distance to be covered
 * (but not speed as this messes up arc movement).
 * 
 * Parameters:
 * A position to reach, threshold distance for success, threshold angle for failure.
 * 
 */
public class PathStepGoForWards implements PathStep {
	
	private Point2D destination;
	private int distance;
	private int threshold;
	
	public PathStepGoForWards(Point2D destination, int distance, int threshold){
		this.destination = destination;
		this.distance = distance;
		this.threshold = threshold;
	}
	
	@Override
	public Type getType() {
		return Type.GO_FORWARDS;
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
	
	
	/**
	 * Succeed:
 	 * If Alfie is within the specified threshold distance from the target point.
	 */
	@Override
	public boolean isSuccessful() {
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
	public boolean problemExists() {
		// TODO Auto-generated method stub
		//Coming soon Logic!
		return false;
	}

	
}
