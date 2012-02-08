package group2.sdp.pc.planner.commands;

import java.awt.geom.Point2D;


public class ReachDestinationCommand implements ComplexCommand {

	Point2D target;
	Point2D origin;
	double facing;
	
	
	public ReachDestinationCommand(Point2D target, Point2D Alfie, double angle){
		this.target = target;
		this.origin = Alfie;
		this.facing = angle;
	}

	@Override
	public Type getType() {
		return ComplexCommand.Type.REACH_DESTINATION;
	}
	
	public Point2D getOrigin(){
		return this.origin;
	}
	
	public Point2D getTarget(){
		return this.target;
	}
	
	public double getFacing(){
		return this.facing;
	}

}
