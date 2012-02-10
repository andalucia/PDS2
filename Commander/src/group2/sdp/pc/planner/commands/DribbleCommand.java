package group2.sdp.pc.planner.commands;

import java.awt.geom.Point2D;

public class DribbleCommand implements ComplexCommand{

	Point2D ball;
	Point2D alfie;
	double facing;
	
	
	public DribbleCommand(Point2D ball, Point2D alfie, double facing) {
		this.ball = ball;
		this.alfie = alfie;
		this.facing = facing;	
	}
	

	public Point2D getBall(){
		return ball;
	}
	
	public Point2D getAlfie(){
		return alfie;
	}
	
	public double getFacing(){
		return facing;
	}
	
	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return Type.DRIBBLE;
	}

}
