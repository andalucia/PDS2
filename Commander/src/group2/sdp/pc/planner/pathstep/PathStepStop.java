package group2.sdp.pc.planner.pathstep;

import group2.sdp.pc.breadbin.DynamicInfo;

/**
 * Act: Tell Alfie to kick.
 * 
 * Parameters: None.
 */
public class PathStepStop implements PathStep {

	public PathStepStop(){
		
	}

	
	@Override
	public Type getType() {
		return Type.STOP;
	}

	/**
	 * Succeed:
	 * If Alfie stops moving.
	 */
	@Override
	public boolean isSuccessful(DynamicInfo pitchStatus) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Fail: If Alfie is being pushed by the other robot.
	 */
	@Override
	public boolean problemExists(DynamicInfo pitchStatus) {
		// TODO Auto-generated method stub
		return false;
	}
}
