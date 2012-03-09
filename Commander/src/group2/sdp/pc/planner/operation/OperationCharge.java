package group2.sdp.pc.planner.operation;


import java.awt.geom.Point2D;

public class OperationCharge implements Operation{

	Point2D ball;
	Point2D alfie;
	double facing;
	Point2D goalMiddle;
	
	
	public OperationCharge(Point2D ball, Point2D alfie, double facing, Point2D goalMiddle) {
		this.ball = ball;
		this.alfie = alfie;
		this.facing = facing;
		this.goalMiddle = goalMiddle;
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
	
	public Point2D getMiddle() {
		return goalMiddle;
	}
	
	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return Type.CHARGE;
	}

}
