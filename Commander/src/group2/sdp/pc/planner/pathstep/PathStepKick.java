package group2.sdp.pc.planner.pathstep;

import group2.sdp.pc.breadbin.DynamicInfo;

/**
 * Act: Tell Alfie to kick.
 * 
 * Parameters: power.
 */
public class PathStepKick implements PathStep {

	private int power;
	
	public PathStepKick (int power) {
		this.power = power; 
	}
	
	@Override
	public Type getType() {
		return Type.KICK;
	}

	
	public int getPower(){
		return this.power;
	}
	
	/**
	 * Succeed:
	 * If Alfie managed to kick the ball.
	 */
	@Override
	public boolean isSuccessful(DynamicInfo pitchStatus) {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * Fail: If Alfie did not kick the ball.
	 */
	@Override
	public boolean problemExists(DynamicInfo pitchStatus) {
		// TODO Auto-generated method stub
		return false;
	}

}