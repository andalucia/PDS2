package group2.sdp.pc.planner.operation;

import java.awt.geom.Point2D;

public class OperationPenaltyTake implements Operation {

	Point2D opponentRobotPosition;
	
	public OperationPenaltyTake(Point2D opponentRobotPosition) {
		this.opponentRobotPosition = opponentRobotPosition;
	}
	
	public Point2D getOpponentRobotPosition() {
		return opponentRobotPosition;
	}
	
	@Override
	public Type getType() {
		return Type.PENALTY_TAKE;
	}

}
