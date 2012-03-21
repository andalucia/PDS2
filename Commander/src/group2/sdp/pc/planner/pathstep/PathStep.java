package group2.sdp.pc.planner.pathstep;

import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.controlstation.ControlStation;
import group2.sdp.pc.mouth.MouthInterface;

/**
 * Description: - A step of a [path that aims to achieve an operation]. Describes the type of Candy
 * Packet to give to Alfie (see Communication section below), the state in which it
 * would be successful and the state in which it would fail.
 */
public abstract class PathStep {
	
	public enum Type{
		GO_FORWARDS,
		GO_BACKWARDS,
		SPIN_LEFT,
		SPIN_RIGHT,
		ARC_FORWARDS_LEFT,
		ARC_FORWARDS_RIGHT,
		ARC_BACKWARDS_LEFT,
		ARC_BACKWARDS_RIGHT,
		KICK,
		STOP,	
	}
	
	public abstract Type getType();
	
	/**
	 * Checks if the mouth is initialised and returns that.
	 */
	public boolean whisper(MouthInterface mouth) {
		if (mouth == null) {
			ControlStation.log("Initialize Mouth, I beg you!");
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if the path step is successful or not, given the current pitch status.
	 * @param pitchStatus The current pitch status.
	 * @return True if the path step is successful, false otherwise. 
	 */
	public abstract boolean isSuccessful(DynamicInfo pitchStatus);
	
	/**
	 * Checks if the path step failed or not, given the current pitch status.
	 * @param pitchStatus The current pitch status.
	 * @return True if the path step failed, false otherwise. 
	 */
	public abstract boolean hasFailed(DynamicInfo pitchStatus);
	
}
