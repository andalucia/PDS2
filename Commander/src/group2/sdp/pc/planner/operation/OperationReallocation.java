package group2.sdp.pc.planner.operation;


import java.awt.geom.Point2D;


public class OperationReallocation implements Operation {

	Point2D target;
	Point2D origin;
	double facing;
	
	
	public OperationReallocation(Point2D target, Point2D Alfie, double angle){
		this.target = target;
		this.origin = Alfie;
		this.facing = angle;
	}

	@Override
	public Type getType() {
		return Operation.Type.REALLOCATION;
	}
	
	public Point2D getOrigin(){
		return this.origin;
	}
	
	public Point2D getTarget(){
		return this.target;
	}
	
	public double getFacingDirection(){
		return this.facing;
	}

}
