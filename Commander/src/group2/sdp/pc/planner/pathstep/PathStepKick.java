package group2.sdp.pc.planner.pathstep;

import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.DynamicInfo;

/**
 * 
 * 
 * @author Shaun A.K.A the bringer of bad code!!! beware but chris was here so its kay :)
 *
 * Act: Tell Alfie to kick.
 * 
 * Parameters: power.
 * 
 */
public class PathStepKick implements PathStep {

	/**
	 * these variables are used in construction of the step
	 * and stored for use when the step is pulled of the queue
	 * 
	 */
	private int power;
	private Point2D ball;
	
	/**
	 * constructor for the class
	 * 
	 * @param destination Point2D target destination
	 * @param distance int distance to the target
	 * @param threshold int how close we have to be to the target
	 * @param speed the speed we are to move forward
	 */
	public PathStepKick (int power, Point2D ball) {
		this.power = power;
		this.ball = ball;
	}
	
	@Override
	public Type getType() {
		return Type.KICK;
	}

	/**
	 * getter for variable power
	 * @return int
	 */
	public int getPower(){
		return this.power;
	}

	/**
	 * getter for the variable ball
	 * @return Point2D
	 */
	public Point2D getBall(){
		return this.ball;
	}
	
	/**
	 * Succeed:
	 * If Alfie managed to kick the ball.
	 */
	@Override
	public boolean isSuccessful(DynamicInfo dpi) {
		// TODO Auto-generated method stub
		
		//this is not the logic we should use should check within a threshold
		double newBallDir = dpi.getBallInfo().getRollingDirection();
		double facing = dpi.getAlfieInfo().getFacingDirection();
		if(newBallDir == facing){
			return true;
		} else {
			return false;
		}
	}

	
	/**
	 * Fail: If Alfie did not kick the ball.
	 */
	@Override
	public boolean problemExists(DynamicInfo dpi) {
		// TODO Auto-generated method stub
		return false;
	}

}
