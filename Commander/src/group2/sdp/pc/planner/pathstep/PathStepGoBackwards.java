package group2.sdp.pc.planner.pathstep;

import group2.sdp.pc.breadbin.DynamicInfo;

import java.awt.geom.Point2D;


/**
 * 
 * @author  Shaun A.K.A the bringer of bad code!!! beware but chris was here so its kay :)
 *
 * Act: Start moving backwards, possibly specifying the distance to be covered (but not
 * speed as this messes up arc movement).
 * 
 * Parameters:
 * A position to reach, threshold distance for success, threshold angle for failure.
 * 
 */
public class PathStepGoBackwards implements PathStep {

	
	/**
	 * these variables are used in construction of the step
	 * and stored for use when the step is pulled of the queue
	 * 
	 */
	private Point2D destination;
	private int distance;
	private int distanceThreshold;
	private int angleThreshold;
	private int speed;
	
	/**
	 * constructor for the class
	 * 
	 * @param destination Point2D target destination
	 * @param distance int distance to the target
	 * @param threshold int how close we have to be to the target
	 * @param speed the speed we are to move forward
	 */
	public PathStepGoBackwards(Point2D destination, int distance, int distanceThreshold, int speed){
		this.destination = destination;
		this.distance = distance;
		this.distanceThreshold = distanceThreshold;
		this.angleThreshold = angleThreshold;
		this.speed = speed;
	}
	
	@Override
	public Type getType() {
		return Type.GO_BACKWARDS;
	}
	
	
	/**
	 * getter for the destination
	 * @return Point2D
	 */
	public Point2D getDestination(){
		return this.destination;
	}
	
	/**
	 * getter for the distance
	 * @return int
	 */
	public int getDistance(){
		return this.distance;
	}
	
	/**
	 * getter for the threshold
	 * @return int
	 */
	public int getDistThreshold(){
		return this.distanceThreshold;
	}
	
	/**
	 * getter for the angleThreshold
	 * @return
	 */
	public int getAngleThreshold(){
		return this.angleThreshold;
	}
	
	/**
	 * getter for the speed
	 * @return int
	 */
	public int getSpeed(){
		return this.speed;
	}
	
	/**
	 * Succeed:
	 * If Alfie is within the specified threshold distance from the target point.
	 */
	@Override
	public boolean isSuccessful(DynamicInfo dpi) {
		// TODO Auto-generated method stub 
		
		Point2D alfie = dpi.getAlfieInfo().getPosition();
		Point2D destination = this.destination;
				
		//should we check the angle?
		double distance = alfie.distance(destination);
		if(distance <=distanceThreshold){
			return true;
		} else {
			return false;
		}
	}

	/**
	 *
     * Fail: If Alfie's facing direction is not within the specified threshold angle from the same
     * point.
	 */
	@Override
	public boolean problemExists(DynamicInfo dpi) {
		// TODO Auto-generated method stub
		
		Point2D alfie = dpi.getAlfieInfo().getPosition();
		double facingDirection = dpi.getAlfieInfo().getFacingDirection();
		double angle = getAngleToTarget(destination, alfie, facingDirection);
		
		if(angle>angleThreshold){
			return true;
		} else {
			return false;
		}
	}
	
	//this is only here until we have a tools class to have these functions
	private double getAngleToTarget(Point2D targetPosition, Point2D alfiePosition, double facingDirection) {
		double dx = (targetPosition.getX() - alfiePosition.getX());
		double dy = (targetPosition.getY() - alfiePosition.getY());

		double angle = Math.toDegrees(Math.atan2(dy, dx));

		if (angle < 0) {
			angle = 360 + angle;
		}
		double result = angle - facingDirection;
		// Variables angle and facingDirection are between 0 and 360. Thus result is 
		// between -360 and 360. We need to normalize to -180 and 180. 
		if (result < -180) {
			result += 360;
		} else if (result > 180) {
			result -= 360;
		}
		return Math.abs(result);
	}
}
