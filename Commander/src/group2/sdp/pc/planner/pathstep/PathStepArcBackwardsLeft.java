package group2.sdp.pc.planner.pathstep;

import group2.sdp.common.util.Geometry;
import group2.sdp.pc.mouth.MouthInterface;

import java.awt.geom.Point2D;

/**
 * Act: Tell Alfie to start moving backwards on an anti-clock-wise arc, specifying the radius and
 * angle of the arc. The actual movement is clock-wise. Thus the centre of the arc should be at the left hand side of Alfie.
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
	protected void inferTargetOrientation()  {
		this.targetDirection = startDirection - angle;
		targetDirection = Geometry.normalizeToPositive(targetDirection);
	}
	
	@Override
	protected void inferTargetDestination(Point2D start) {
		this.targetDestination = 
			Geometry.getArcEnd(
					start, 
					startDirection, 
					-radius, 
					-angle
			);
	}
	
	@Override
	public Point2D getCentrePoint() {
		Point2D end = Geometry.getArcEnd(
				start, 
				startDirection, 
				-radius, 
				-180
		);
		return Geometry.getMidPoint(start, end);
	}

	@Override
	public boolean whisper(MouthInterface mouth) {
		if (super.whisper(mouth)) {
			mouth.sendBackwardsArcLeft(Math.abs(radius), Math.abs(angle));
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "PathStepArcBackwardsLeft: " + super.toString();
	}
}
