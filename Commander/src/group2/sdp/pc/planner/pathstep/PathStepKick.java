package group2.sdp.pc.planner.pathstep;

import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.mouth.MouthInterface;

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
	public boolean hasFailed(DynamicInfo pitchStatus) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void execute(MouthInterface mouth) {
		mouth.sendKick(getPower());
	}

}
