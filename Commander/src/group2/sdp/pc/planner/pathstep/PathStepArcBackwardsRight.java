package group2.sdp.pc.planner.pathstep;

import group2.sdp.common.util.Geometry;
import group2.sdp.pc.mouth.MouthInterface;

import java.awt.geom.Point2D;

/**
 * Act: Tell Alfie to start moving backwards in an clock-wise arc, specifying the radius and
 * angle of the arc. Thus the centre of the arc should be at the right hand side of Alfie.
 * 
 * Parameters:
 * Radius and angle of the arc and threshold distance. (Target position should be
 * computed on construction of the Arc Backwards Right object.)
 */
public class PathStepArcBackwardsRight extends PathStepArc {

	/**
	 * Fully initialising constructor.
	 * @param start The position from which the movement will start.
	 * @param startDirection The orientation from which the movement will start.
	 * @param radius The radius of the circle containing the arc. Keep it positive.
	 * @param angle The central angle of the arc. Keep it positive.
	 * @param threshold The threshold for deciding success.
	 */
	public PathStepArcBackwardsRight(Point2D start, double startDirection,
			double radius, double angle, double threshold) {
		super(start, startDirection, radius, angle, threshold);
	}

	@Override
	public Type getType() {
		return PathStep.Type.ARC_BACKWARDS_RIGHT;
	}

	@Override
	protected void inferTargetOrientation()  {
		this.targetDirection = startDirection + angle;
		targetDirection = Geometry.normalizeToPositive(targetDirection);
	}
	
	@Override
	protected void inferTargetDestination(Point2D start) {
		this.targetDestination = 
			Geometry.getArcEnd(
					start, 
					startDirection, 
					-radius, 
					angle
			);
	}
	
	@Override
	public Point2D getCentrePoint() {
		Point2D end = Geometry.getArcEnd(
				start, 
				startDirection, 
				-radius, 
				180
		);
		return Geometry.getMidPoint(start, end);
	}

	
	@Override
	public boolean whisper(MouthInterface mouth) {
		if (super.whisper(mouth)) {
			mouth.sendBackwardsArcRight(Math.abs(radius), Math.abs(angle));
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "PathStepArcBackwardsRight: " + super.toString();
	}
}
