package group2.sdp.pc.planner.pathstep;

/**
 * 
 * 
 * @author Shaun A.K.A the bringer of bad code!!! beware but chris was here so its kay :)
 *
 * Act: Tell Alfie to start moving forward in an anti-clock-wise arc, specifying the radius and
 * angle of the arc. Thus the centre of the arc should be at the left hand side of Alfie.
 * 
 * Parameters:
 * Radius and angle of the arc and threshold distance. (Target position should be
 * computed on construction of the Arc Forward Left object.)
 * 
 */

public class PathStepArcForwardsLeft implements PathStep {

	private int radius;
	private int angle;
	private int threshold;
	
	public PathStepArcForwardsLeft(int radius, int angle, int threshold){
		this.angle = angle;
		this.radius = radius;
		this.threshold = threshold;
	}
	
	@Override
	public Type getType() {
		return Type.ARC_FORWARDS_LEFT;
	}
	
	
	
	public int getAngle(){
		return this.angle;
	}
	
	public int getThreshold(){
		return this.threshold;
	}
	
	public int getRadius(){
		return this.radius;
	}
	
	
	/**
	 * Succeed:
	 * If Alfie is within the specified threshold distance from the target position.
	 */
	@Override
	public boolean isSuccessful() {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * Fail: 
	 * 		 Easy:
	 * 			  If Alfie is moving away from the target position.
	 * 		 
	 * 		 Hard:
	 * 			  If the target position is farther from Alfie's circular trajectory than a specified
	 * 			  threshold. This is hard, as the vision system might not be accurate enough to allow a
	 * 		 	  precise computation of the circular trajectory of Alfie. However, if implemented, this
	 * 		 	  would provide Alfie with a lot quicker reactions to problems with this Path Step.
	 */
	@Override
	public boolean problemExists() {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
