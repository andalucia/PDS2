package group2.sdp.pc.planner.pathstep;

import group2.sdp.pc.breadbin.DynamicInfo;

/**
 * 
 * 
 * @author Shaun A.K.A the bringer of bad code!!! beware but chris was here so its kay :)
 *
 * Act: Tell Alfie to kick.
 * 
 * Parameters: None.
 * 
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
	public boolean isSuccessful(DynamicInfo dpi) {
		// TODO Auto-generated method stub
		double speed = dpi.getAlfieInfo().getTravelSpeed();
		if(speed >0){
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Fail: Alfie is not on the pitch he is at the local disco dancing 
	 */
	@Override
	public boolean problemExists(DynamicInfo dpi) {
		// TODO Auto-generated method stub
		return false;
	}
}
