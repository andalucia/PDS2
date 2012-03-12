package group2.sdp.pc.planner.pathstep;

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
	public boolean isSuccessful() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Fail: Alfie is not on the pitch he is at the local disco dancing 
	 */
	@Override
	public boolean problemExists() {
		// TODO Auto-generated method stub
		return false;
	}
}
