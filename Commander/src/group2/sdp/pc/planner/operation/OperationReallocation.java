package group2.sdp.pc.planner.operation;

import java.awt.geom.Point2D;

/**
 * Move to a position and face a direction
 */
public class OperationReallocation implements Operation {

	/**
	 * The position to reach.
	 */
	private Point2D position;
	/**
	 * The direction to face at the end.
	 */
	private double orientation;

	/**
	 * Fully initialising constructor.
	 * @param position The position to reach.
	 * @param orientation The direction to face at the end.
	 */
	public OperationReallocation(Point2D position, double orientation) {
		this.setPosition(position);
		this.setOrientation(orientation);
	}
	
	@Override
	public Type getType() {
		return Operation.Type.REALLOCATION;
	}

	/**
	 * Set the position to reach.
	 * @param position The position to reach.
	 */
	public void setPosition(Point2D position) {
		this.position = position;
	}

	/**
	 * Get the position to reach.
	 * @return The position to reach.
	 */
	public Point2D getPosition() {
		return position;
	}

	/**
	 * Set the direction to face at the end of the reallocation.
	 * @param orientation The direction to face at the end of the reallocation.
	 */
	public void setOrientation(double orientation) {
		this.orientation = orientation;
	}

	/**
	 * Get the direction to face at the end of the reallocation.
	 * @return the orientation The direction to face at the end of the reallocation.
	 */
	public double getOrientation() {
		return orientation;
	}
}
