package group2.sdp.pc.planner.pathstep;

import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.planner.pathstep.PathStep.Type;

/**
 * Act: Tell Alfie to start moving backwards in an anti-clock-wise arc, specifying the radius and
 * angle of the arc. Thus the centre of the arc should be at the left hand side of Alfie.
 * 
 * Parameters:
 * Radius and angle of the arc and threshold distance. (Target position should be
 * computed on construction of the Arc Forward Left object.)
 */
public class PathStepArcBackwardsLeft extends PathStepArc {

	public PathStepArcBackwardsLeft(Point2D start, double startDirection,
			double radius, double angle, double threshold) {
		super(start, startDirection, radius, angle, threshold);
	}

	@Override
	public Type getType() {
		return PathStep.Type.ARC_BACKWARDS_LEFT;
	}

	@Override
	protected void setRadius(double radius) {
		this.radius = -radius;
	}

	@Override
	protected void setAngle(double angle) {
		this.angle = angle; // TODO: TEST
	}
}
