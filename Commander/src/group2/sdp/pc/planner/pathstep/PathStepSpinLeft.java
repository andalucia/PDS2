package group2.sdp.pc.planner.pathstep;

import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.DynamicInfo;

/**
 * 
 * 
 * @author Shaun A.K.A the bringer of bad code!!! beware but chris was here so its kay :)
 *
 * Act: Tell Alfie to start spinning anti-clock-wise, possibly specifying the angle to be
 * covered.
 * 
 * Parameters:
 * An angle for the turn, threshold delta angle for success.
 * 
 */
public class PathStepSpinLeft implements PathStep {

	/**
	 * these variables are used in construction of the step
	 * and stored for use when the step is pulled of the queue
	 * 
	 */
	private int angleToTurn;
	private Point2D target;
	private int threshold;
	private int speed;
	private double oldAngle;
	
	
	/**
	 * constructor for the class
	 * 
	 * @param angleToTurn
	 * @param target
	 * @param threshold
	 * @param speed
	 */
	public PathStepSpinLeft(int angleToTurn, Point2D target, int threshold, int speed){
		this.angleToTurn = angleToTurn;
		this.target = target;
		this.threshold = threshold;
		this.speed = speed;
	}
	
	@Override
	public Type getType() {
		return Type.SPIN_LEFT;
	}

	
	/**
	 * getter for the variable to turn
	 * @return Point2D
	 */
	public int getAngleToTurn(){
		return this.angleToTurn;
	}
	
	public Point2D getTarget(){
		return this.target;
	}
	
	public int getThreshold(){
		return this.threshold;
	}
	
	public int getSpeed(){
		return this.speed;
	}
	
	/**
	 * Succeed:
	 * If Alfie is within the specified threshold delta from the specified angle.
	 */
	@Override
	public boolean isSuccessful(DynamicInfo dpi) {
		// TODO Auto-generated method stub 
		// logic yet to be added
		
		double facing = dpi.getAlfieInfo().getFacingDirection();
		Point2D alfie = dpi.getAlfieInfo().getPosition();
		double angle = PathStepGoForwards.getAngleToTarget(target, alfie, facing);
		
		if(angle<=threshold){
			return true;
		} else {
			oldAngle = angle;
			return false;
		}
	}

	/**
	 *
	 * Fail:
	 * If Alfie is turning away from the destination angle.
	 */
	@Override
	public boolean problemExists(DynamicInfo dpi) {
		// TODO Auto-generated method stub
		//Coming soon Logic!
		double facing = dpi.getAlfieInfo().getFacingDirection();
		Point2D alfie = dpi.getAlfieInfo().getPosition();
		double angle = PathStepGoForwards.getAngleToTarget(target, alfie, facing);
		if(angle > oldAngle){
			return true;
		} else {
			return false;
		}
	}
	
}

