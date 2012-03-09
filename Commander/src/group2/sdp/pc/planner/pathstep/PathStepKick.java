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
public class PathStepKick implements PathStep {

	public PathStepKick () {
		
	}
	
	@Override
	public Type getType() {
		return Type.KICK;
	}

	
	/**
	 * Succeed:
	 * If Alfie managed to kick the ball.
	 */
	@Override
	public boolean isSuccessful() {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * Fail: If Alfie did not kick the ball.
	 */
	@Override
	public boolean problemExists() {
		// TODO Auto-generated method stub
		return false;
	}

}
