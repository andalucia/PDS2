package group2.sdp.pc.planner.pathstep;

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
