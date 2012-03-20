package group2.sdp.pc.planner.pathstep;

import group2.sdp.common.util.Geometry;
import group2.sdp.pc.breadbin.DynamicInfo;

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
public class PathStepArcForwardsRight implements PathStep {

	private static final boolean verbose = true;
	
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
	public PathStepArcForwardsRight(Point2D startPosition, double startOrientation, 
			double radius, double angle, double threshold){
		this.setAngle(angle);
		this.setRadius(radius);
		this.threshold = threshold;
		
		targetDestination = 
			Geometry.getArcEnd(
					startPosition, 
					startOrientation, 
					radius, 
					-angle
			);
		targetOrientation = (startOrientation - angle) % 360; //TODO is minus correct?
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
		return Type.ARC_FORWARDS_RIGHT;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(angle);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(radius);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime
				* result
				+ ((targetDestination == null) ? 0 : targetDestination
						.hashCode());
		temp = Double.doubleToLongBits(targetOrientation);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(threshold);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PathStepArcForwardsRight other = (PathStepArcForwardsRight) obj;
		if (Double.doubleToLongBits(angle) != Double
				.doubleToLongBits(other.angle))
			return false;
		if (Double.doubleToLongBits(radius) != Double
				.doubleToLongBits(other.radius))
			return false;
		if (targetDestination == null) {
			if (other.targetDestination != null)
				return false;
		} else if (!targetDestination.equals(other.targetDestination))
			return false;
		if (Double.doubleToLongBits(targetOrientation) != Double
				.doubleToLongBits(other.targetOrientation))
			return false;
		if (Double.doubleToLongBits(threshold) != Double
				.doubleToLongBits(other.threshold))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PathStepArcForwardsRight [radius=" + radius + ", angle="
				+ angle + ", threshold=" + threshold + ", targetDestination="
				+ targetDestination + ", targetOrientation="
				+ targetOrientation + "]";
	}
	
}
