package group2.sdp.pc.planner.pathstep;

import group2.sdp.common.util.Geometry;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.controlstation.ControlStation;
import group2.sdp.pc.mouth.MouthInterface;

import java.awt.geom.Point2D;

public abstract class PathStepArc extends PathStep {

//	private static final double SPEED_STOP_THRESHOLD = 10.0;
	/**
	 * The radius of the circle containing the arc.
	 */
	protected double radius;
	/**
	 * The central angle of the arc.
	 */
	protected double angle;
	
	private double threshold;

	private double targetOrientation;

	private Point2D targetDestination;

	/**
	 * Fully initialising constructor.
	 * @param start The position from which the movement will start.
	 * @param startDirection The orientation from which the movement will start.
	 * @param radius The radius of the circle containing the arc.
	 * @param angle The central angle of the arc. Keep it positive.
	 * @param threshold The threshold for deciding success.
	 */
	public PathStepArc(Point2D start, double startDirection, 
			double radius, double angle, double threshold){
		setAngle(angle);
		setRadius(radius);

		this.threshold = threshold;
		
		inferTargetDestination(start, startDirection);
		inferTargetOrientation(startDirection);
	}
	
	/**
	 * Computes the target orientation, given the start direction.
	 * @param startDirection
	 */
	private void inferTargetOrientation(double startDirection) {
		this.targetOrientation = startDirection + angle;
		if (this.targetOrientation != 0.0) {
			this.targetOrientation %= 360.0; 
			// -360 < targetOrientation < 360
			
			this.targetOrientation += 360.0;
			if (this.targetOrientation != 0.0) {
				this.targetOrientation %= 360.0;
				// 0 < targetOrientation < 360
			}
		}
	}

	private void inferTargetDestination(Point2D start, double startDirection) {
		this.targetDestination = 
			Geometry.getArcEnd(start, startDirection, radius, angle);
	}

	protected abstract void setRadius(double radius);
	
	protected abstract void setAngle(double angle);
	
	public double getRadius() {
		return radius;
	}

	public double getAngle() {
		return angle;
	}

	public double getThreshold() {
		return threshold;
	}

	public double getTargetOrientation() {
		return targetOrientation;
	}

	public Point2D getTargetDestination() {
		return targetDestination;
	}
	

	/**
	 * Succeed:
	 * If Alfie is within the specified threshold distance from the target position.
	 */
	@Override
	public boolean isSuccessful(DynamicInfo pitchStatus) {
		double d = pitchStatus.getAlfieInfo().getPosition().distance(targetDestination);
		return d < threshold;
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
	public boolean hasFailed(DynamicInfo pitchStatus) {
		int STOP_THRESHOLD = 10;
		System.out.println("Travel speed: " + pitchStatus.getAlfieInfo().getTravelSpeed());
		if (pitchStatus.getAlfieInfo().getTravelSpeed() < STOP_THRESHOLD) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[radius=" + radius + ", angle=" + angle
		+ ", threshold=" + threshold + ", targetDestination="
		+ targetDestination + ", targetOrientation="
		+ targetOrientation + "]";
	}

	public static PathStepArc getShorterPathStepArc(
			Point2D arcStart, double startDirection, Point2D circleCentre,
			double angle, double threshold
	) {
		double angle2 = angle;
		double startDirection2;
		if (angle > 180.0) {
			angle2 -= 180.0;
			startDirection2 = Geometry.reverse(startDirection);
		} else {
			startDirection2 = startDirection;
		}
		
		double radius = circleCentre.distance(arcStart);
		Point2D arcEnd = Geometry.getArcEnd(arcStart, startDirection, radius, angle);
		
		boolean lefty = Geometry.isArcLeft(arcStart, startDirection, circleCentre);
		boolean behind = Geometry.isPointBehind(arcStart, startDirection, arcEnd);
		
		if (!behind)
			if (lefty)
				return new PathStepArcForwardsLeft(arcStart, startDirection2, radius, angle2, threshold);
			else
				return new PathStepArcForwardsRight(arcStart, startDirection2, radius, angle2, threshold);
		else
			if (lefty)
				return new PathStepArcBackwardsLeft(arcStart, startDirection2, radius, angle2, threshold);
			else
				return new PathStepArcBackwardsRight(arcStart, startDirection2, radius, angle2, threshold);
	}

	public static PathStepArc getForwardPathStepArc(Point2D arcStart,
			double startDirection, Point2D circleCentre,
			double angle, double threshold
	) {
		double radius = circleCentre.distance(arcStart);
		
		boolean lefty = Geometry.isArcLeft(arcStart, startDirection, circleCentre);
		
		if (lefty)
			return new PathStepArcForwardsLeft(arcStart, startDirection, radius, angle, threshold);
		else
			return new PathStepArcForwardsRight(arcStart, startDirection, radius, angle, threshold);
	}

	@Override
	public abstract Type getType();
	
	@Override
	public boolean whisper(MouthInterface mouth) {
		return super.whisper(mouth);
	}
}
