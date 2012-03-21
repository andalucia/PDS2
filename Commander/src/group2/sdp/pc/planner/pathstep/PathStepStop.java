package group2.sdp.pc.planner.pathstep;

import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.mouth.MouthInterface;

/**
 * Act: Tell Alfie to kick.
 * 
 * Parameters: None.
 */
public class PathStepStop extends PathStep {

	public PathStepStop() {
		
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
	public boolean hasFailed(DynamicInfo pitchStatus) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean whisper(MouthInterface mouth) {
		if (super.whisper(mouth)) {
			mouth.sendStop();
			return true;
		}
		return false;
	}
}
