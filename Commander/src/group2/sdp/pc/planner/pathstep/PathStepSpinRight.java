package group2.sdp.pc.planner.pathstep;

import group2.sdp.pc.breadbin.DynamicInfo;


/**
 * Act: Tell Alfie to start spinning clock-wise, possibly specifying the angle to be
 * covered.
 * 
 * Parameters:
 * An angle for the turn, threshold delta angle for success.
 */
public class PathStepSpinRight implements PathStep {

	
	private int angle;
	private int threshold;
	private int speed;
	
	public PathStepSpinRight(int angle, int threshold, int speed){
		this.angle = angle;
		this.threshold= threshold;
		this.speed = speed;
	}
	
	@Override
	public Type getType() {
		return Type.SPIN_RIGHT;
	}


	
	
	public int getAngle(){
		return this.angle;
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
	public boolean isSuccessful(DynamicInfo pitchStatus) {
		// TODO Auto-generated method stub 
		// logic yet to be added
		return false;
	}

	/**
	 *
	 * Fail:
	 * If Alfie is turning away from the destination angle.
	 */
	@Override
	public boolean problemExists(DynamicInfo pitchStatus) {
		// TODO Auto-generated method stub
		//Coming soon Logic!
		return false;
	}

	
}

