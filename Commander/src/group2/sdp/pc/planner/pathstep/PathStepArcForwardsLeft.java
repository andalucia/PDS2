package group2.sdp.pc.planner.pathstep;

import group2.sdp.common.util.Geometry;
import group2.sdp.pc.breadbin.DynamicInfo;

import java.awt.geom.Point2D;

/**
 * Act: Tell Alfie to start moving forward in an anti-clock-wise arc, specifying the radius and
 * angle of the arc. Thus the centre of the arc should be at the left hand side of Alfie.
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
 * computed on construction of the Arc Forward Left object.)
 */
public class PathStepArcForwardsLeft implements PathStep {

	private static final boolean verbose = false;
	
	/**
	 * The radius of the circle containing the arc.
	 */
	private double radius;
	/**
	 * The central angle of the arc.
	 */
	private double angle;
	/**
	 * The threshold for deciding success.
	 */
	private double threshold;
	/**
	 * The destination to be reached.
	 */
	private Point2D targetDestination;
	/**
	 * The orientation at the end of the movement.
	 */
	private double targetOrientation;
	
	/**
	 * Fully initialising constructor.
	 * @param startPosition The position from which the movement will start.
	 * @param startOrientation The orientation from which the movement will start.
	 * @param radius The radius of the circle containing the arc.
	 * @param angle The central angle of the arc.
	 * @param threshold The threshold for deciding success.
	 */
	public PathStepArcForwardsLeft(Point2D startPosition, double startOrientation, 
			double radius, double angle, double threshold){
		this.setAngle(angle);
		this.setRadius(radius);
		this.threshold = threshold;
		
		targetDestination = 
			Geometry.getArcEnd(
					startPosition, 
					startOrientation, 
					radius, 
					angle
			);
		targetOrientation = startOrientation + angle;
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
	public boolean problemExists(DynamicInfo pitchStatus) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Type getType() {
		return Type.ARC_FORWARDS_LEFT;
	}

	/**
	 * Set the radius of the circle containing the arc.
	 * @param radius The radius of the circle containing the arc.
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

	/**
	 * Get the radius of the circle containing the arc.
	 * @return The radius of the circle containing the arc.
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Set the central angle of the arc.
	 * @param angle The central angle of the arc.
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}

	/**
	 * Get the central angle of the arc.
	 * @return The central angle of the arc.
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * Get the destination to be reached.
	 * @return
	 */
	public Point2D getTargetDestination() {
		return targetDestination;
	}

	public double getTargetOrientation() {
		return targetOrientation;
	}
}
