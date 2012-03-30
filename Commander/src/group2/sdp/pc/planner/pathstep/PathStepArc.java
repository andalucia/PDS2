package group2.sdp.pc.planner.pathstep;

import group2.sdp.common.util.Geometry;
import group2.sdp.pc.breadbin.DynamicInfo;
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

	protected Point2D start;
	
	protected double startDirection;
	
	protected double endDirection;

	protected Point2D endPoint;


	/**
	 * Fully initialising constructor.
	 * @param start The position from which the movement will start.
	 * @param startDirection The orientation from which the movement will start.
	 * @param radius The radius of the circle containing the arc. Keep it positive.
	 * @param angle The central angle of the arc. Keep it positive.
	 * @param threshold The threshold for deciding success.
	 */
	public PathStepArc(Point2D start, double startDirection, 
			double radius, double angle, double threshold){
		this.angle = angle;
		this.radius = radius;
		this.startDirection = startDirection;

		this.threshold = threshold;
		this.start = start;
		inferEndPoint();
		inferEndDirection();
	}
	

	/**
	 * Computes the target orientation, given the start direction.
	 * @param startDirection
	 */
	protected abstract void inferEndDirection();

	protected abstract void inferEndPoint();
	
	public double getThreshold() {
		return threshold;
	}

	public double getTargetOrientation() {
		return endDirection;
	}

	public Point2D getTargetDestination() {
		return endPoint;
	}
	
	public Point2D getStartPoint() {
		return start;
	}
	
	public double getStartDirection() {
		return startDirection;
	}

	public abstract Point2D getCentrePoint();
	
	public double getLength() {
		return radius * Math.toRadians(angle);
	}
	
	/**
	 * Succeed:
	 * If Alfie is within the specified threshold distance from the target position.
	 */
	@Override
	public boolean isSuccessful(DynamicInfo pitchStatus) {
		double d = pitchStatus.getAlfieInfo().getPosition().distance(endPoint);
		return d < threshold;
	}

	long failureStartTime = 0;
	
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
		
		long FAILURE_TIMEOUT = 800;
		
		if (pitchStatus.getAlfieInfo().getTravelSpeed() < STOP_THRESHOLD) {
			long now = System.currentTimeMillis();
			if (failureStartTime  > 0 && now - failureStartTime > FAILURE_TIMEOUT) {
				failureStartTime = 0;
				return true;
			}
			if (failureStartTime == 0)
				failureStartTime = now;
		} else {
			failureStartTime = 0;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "[radius=" + radius + ", angle=" + angle
		+ ", threshold=" + threshold + ", targetDestination="
		+ endPoint + ", targetOrientation="
		+ endDirection + "]";
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

	public static PathStepArc getBackwardsPathStepArc(Point2D arcStart,
			double startDirection, Point2D circleCentre,
			double angle, double threshold) {
		angle = angle - 180;
		startDirection = Geometry.reverse(startDirection);
		
		double radius = circleCentre.distance(arcStart);
		boolean lefty = !Geometry.isArcLeft(arcStart, startDirection, circleCentre);
		
		if (lefty)
			return new PathStepArcBackwardsLeft(arcStart, startDirection, radius, angle, threshold);
		else
			return new PathStepArcBackwardsRight(arcStart, startDirection, radius, angle, threshold);
	}
	
	@Override
	public abstract Type getType();
	
	@Override
	public boolean whisper(MouthInterface mouth) {
		return super.whisper(mouth);
	}
}
