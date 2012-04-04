package group2.sdp.pc.planner.pathstep;

import group2.sdp.common.util.Geometry;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.globalinfo.DynamicInfoChecker;
import group2.sdp.pc.mouth.MouthInterface;

/**
 * Act: Tell Alfie to start spinning anti-clock-wise, possibly specifying the angle to be
 * covered.
 * 
 * Parameters:
 * An angle for the turn, threshold delta angle for success.
 */
public class PathStepSpinLeft extends PathStep {

	private double startingAngle;
	private double angle;
	private double targetAngle;
	private double threshold;
	private double speed;
	
	public PathStepSpinLeft(double startingAngle, double angle, double threshold, double speed) {
		this.startingAngle = startingAngle;
		this.angle = angle;
		this.targetAngle = startingAngle + angle;
		this.threshold = threshold;
		this.speed = speed;
	}
	
	@Override
	public Type getType() {
		return Type.SPIN_LEFT;
	}

	
	
	public double getAngle(){
		return this.angle;
	}
	
	public double getThreshold(){
		return this.threshold;
	}
	
	public double getSpeed(){
		return this.speed;
	}
	
	private long successStartTime = 0;
	
	/**
	 * Succeed:
	 * If Alfie is within the specified threshold delta from the specified angle.
	 */
	@Override
	public boolean isSuccessful(DynamicInfo pitchStatus) {
		double angle = pitchStatus.getAlfieInfo().getFacingDirection();
		
//		if (Math.abs(Geometry.normalizeToPositive(targetAngle - angle)) < threshold) {
		if (DynamicInfoChecker.isSimilarAngle(
				targetAngle, 
				angle, 
				threshold
				)) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * Fail:
	 * If Alfie is turning away from the destination angle.
	 */
	@Override
	public boolean hasFailed(DynamicInfo pitchStatus) {
		// TODO Auto-generated method stub
		//Coming soon Logic!
		return false;
	}

	@Override
	public boolean whisper(MouthInterface mouth) {
		if (super.whisper(mouth)) {
			mouth.sendSpinLeft((int)getSpeed(), (int)getAngle());
			return true;
		}
		return false;
	}

	
}

