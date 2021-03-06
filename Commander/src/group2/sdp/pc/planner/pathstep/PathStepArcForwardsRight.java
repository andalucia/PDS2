package group2.sdp.pc.planner.pathstep;

import group2.sdp.common.util.Geometry;
import group2.sdp.pc.mouth.MouthInterface;

import java.awt.geom.Point2D;

/**
 * Act: Tell Alfie to start moving forward in a clock-wise arc, specifying the radius and
 * angle of the arc. Thus the centre of the arc should be at the right hand side of Alfie.
 * 
 * Succeed:
 * If Alfie is within the specified threshold distance from the target position.
 * 
 * Fail: 
 * 		 Easy:
 * 			  If Alfie is moving away from the target position.
 * 		 
 * 		 Hard:
 * 			  If the target position is farther from Alfie's circular trajectory than a specified
 * 			  threshold. This is hard, as the vision system might not be accurate enough to allow a
 * 		 	  precise computation of the circular trajectory of Alfie. However, if implemented, this
 * 		 	  would provide Alfie with a lot quicker reactions to problems with this Path Step.
 * 
 * Parameters:
 * Radius and angle of the arc and threshold distance. (Target position should be
 * computed on construction of the Arc Forward Right object.)
 */
public class PathStepArcForwardsRight extends PathStepArc {
	
	/**
	 * Fully initialising constructor.
	 * @param start The position from which the movement will start.
	 * @param startDirection The orientation from which the movement will start.
	 * @param radius The radius of the circle containing the arc. Keep it positive.
	 * @param angle The central angle of the arc. Keep it positive.
	 * @param threshold The threshold for deciding success.
	 */
	public PathStepArcForwardsRight(Point2D startPosition,
			double startOrientation, double radius, double angle,
			double threshold) {
		super(startPosition, startOrientation, radius, angle, threshold);
	}

	@Override
	public Type getType() {
		return Type.ARC_FORWARDS_RIGHT;
	}

	@Override
	protected void inferEndDirection()  {
		this.endDirection = startDirection - angle;
		endDirection = Geometry.normalizeToPositive(endDirection);
	}
	
	@Override
	protected void inferEndPoint() {
		this.endPoint = 
			Geometry.getArcEnd(
					start, 
					startDirection, 
					radius, 
					-angle
			);
	}
	
	@Override
	public Point2D getCentrePoint() {
		Point2D end = Geometry.getArcEnd(
				start, 
				startDirection, 
				radius, 
				-180
		);
		return Geometry.getMidPoint(start, end);
	}
	
	@Override
	public String toString() {
		return "PathStepArcForwardsRight: " + super.toString();
	}

	@Override
	public boolean whisper(MouthInterface mouth) {
		if (super.whisper(mouth)) {
			mouth.sendForwardArcRight(Math.abs(radius), Math.abs(angle));
			return true;
		}
		return false;
	}
}
