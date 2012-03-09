package group2.sdp.pc.planner.pathstep;

/**
 * 
 * 
 * @author Shaun A.K.A the bringer of bad code!!! beware but chris was here so its kay :)
 *
 * Act: Tell Alfie to start moving forward, possibly specifying the distance to be covered
 * (but not speed as this messes up arc movement).
 * 
 * Parameters:
 * A position to reach, threshold distance for success, threshold angle for failure.
 * 
 */
public class PathStepGoForwards implements PathStep {

	@Override
	public Type getType() {
		return Type.GO_FORWARDS;
	}

	
	/**
	 * Succeed:
 	 * If Alfie is within the specified threshold distance from the target point.
	 */
	@Override
	public boolean isSuccessful() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 *
	 * Fail: If Alfie's facing direction is not within the specified threshold angle from the same
	 * point.
	 */
	@Override
	public boolean problemExists() {
		// TODO Auto-generated method stub
		return false;
	}

	
}
