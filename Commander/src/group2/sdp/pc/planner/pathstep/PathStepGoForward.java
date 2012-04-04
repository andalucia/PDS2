package group2.sdp.pc.planner.pathstep;

import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.mouth.MouthInterface;

import java.awt.geom.Point2D;

/**
 * Act: Tell Alfie to start moving forward, possibly specifying the distance to be covered
 * (but not speed as this messes up arc movement).
 * 
 * Parameters:
 * A position to reach, threshold distance for success, threshold angle for failure.
 */
public class PathStepGoForward extends PathStep {
	
	private Point2D destination;
	private int distance;
	private int threshold;
	private int speed;
	
	public PathStepGoForward(Point2D destination, int distance, int threshold, int speed){
		this.destination = destination;
		this.distance = distance;
		this.threshold = threshold;
		this.speed = speed;
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
	
	public int getSpeed(){
		return this.speed;
	}
	
	
	/**
	 * Succeed:
 	 * If Alfie is within the specified threshold distance from the target point.
	 */
	@Override
	public boolean isSuccessful(DynamicInfo pitchStatus) {
		double d = pitchStatus.getAlfieInfo().getPosition().distance(destination);
		return d < threshold;
	}

	long failureStartTime = 0;
	
	/**
	 *
	 * Fail: If Alfie's facing direction is not within the specified threshold angle from the same
	 * point.
	 */
	public boolean hasFailed(DynamicInfo pitchStatus) {
		int STOP_THRESHOLD = 10;
		
		long FAILURE_TIMEOUT = 800;
		
		if (pitchStatus.getAlfieInfo().getTravelSpeed() < STOP_THRESHOLD) {
			long now = System.currentTimeMillis();
			if (failureStartTime  > 0 && now - failureStartTime > FAILURE_TIMEOUT) {
				failureStartTime = 0;
				return true;
			}
			if (failureStartTime == 0)
				failureStartTime = now;
		} else {
			failureStartTime = 0;
		}
		
		return false;
	}

	@Override
	public boolean whisper(MouthInterface mouth) {
		if (super.whisper(mouth)) {
			mouth.sendGoForward(getSpeed(), getDistance());
			return true;
		}
		return false;
	}	
}
