package group2.sdp.pc.planner.pathstep;

import group2.sdp.pc.breadbin.DynamicInfo;

/**
 * Act: Tell Alfie to start moving forward in an clock-wise arc, specifying the radius and
 * angle of the arc. Thus the centre of the arc should be at the right hand side of Alfie.
 * 
 * Parameters:
 * Radius and angle of the arc and threshold distance. (Target position should be
 * computed on construction of the Arc Forward Left object.)
 */
public class PathStepArcForwardsRight implements PathStep {
	
	private int radius;
	private int angle;
	private int threshold;
	
	public PathStepArcForwardsRight(int radius, int angle, int threshold){
		this.angle = angle;
		this.radius = radius;
		this.threshold = threshold;
	}
	
	@Override
	public Type getType() {
		return Type.ARC_FORWARDS_RIGHT;
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
	 *	  If Alfie is within the specified threshold distance from the target position.
	 */
	@Override
	public boolean isSuccessful(DynamicInfo pitchStatus) {
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
	public boolean problemExists(DynamicInfo pitchStatus) {
		// TODO Auto-generated method stub
		return false;
	}

}
