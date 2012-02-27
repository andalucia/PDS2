package group2.sdp.pc.planner;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.planner.operation.Operation;
import group2.sdp.pc.planner.operation.OperationCharge;
import group2.sdp.pc.planner.operation.OperationOverload;
import group2.sdp.pc.planner.operation.OperationReallocation;
import group2.sdp.pc.planner.operation.OperationStrike;
import group2.sdp.pc.planner.strategy.Strategy;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

import java.awt.geom.Point2D;

/**
 * A field marshal decides what operations to start, knowing what strategy 
 * should be currently executed.
 */
public class FieldMarshal implements DynamicInfoConsumer {

	/**
	 * The current strategy to employ.
	 */
	protected Strategy currentStrategy;
	/**
	 * The path finder that will be executing the operations.
	 */
	protected PathFinder pathFinder;
	/**
	 * The operation that is currently executing.
	 */
	protected Operation currentOperation;
	/**
	 * True if the FieldMarshal need to re-plan the current operation. 
	 */
	protected boolean replan;

	public FieldMarshal(PathFinder pathFinder) {
		this.pathFinder = pathFinder;
	}


	/**
	 * Most important method of the class. According to the current strategy
	 * and dpi, plans the next operation that should be executed.
	 * @param dpi The DynamicPitchInfo to use for planning the next operation
	 * to execute.
	 * @return The next operation to execute.
	 */
	private Operation planNextOperation(DynamicPitchInfo dpi) {
		DynamicRobotInfo AlfieInfo = dpi.getAlfieInfo();
		DynamicBallInfo ballInfo = dpi.getBallInfo();
		DynamicRobotInfo opponentInfo = dpi.getOpponentInfo();

		Point2D ball = ballInfo.getPosition();
		Point2D alfie = AlfieInfo.getPosition();
		double facing = AlfieInfo.getFacingDirection();

		if (currentStrategy == null) {
			System.err.println("No current strategy. Stopping.");
			System.exit(1);
			return null;
		}

		switch (currentStrategy) {
		case DEFENSIVE:
			if (currentOperation instanceof OperationReallocation && operationSuccessful(dpi)) {
				return null;
			} else {
				OperationReallocation cmd = new OperationReallocation(ball, alfie, facing);
				return cmd;
			}

		case OFFENSIVE:
			if(Overlord.hasBall(AlfieInfo, ball)){
				if(shotOnGoal(AlfieInfo, opponentInfo, ball)){
					return new OperationStrike();
				}else{
					return new OperationCharge(ball, alfie, facing);
				}

			}
			return new OperationReallocation(ball, alfie, facing);

		case STOP:
			return new OperationOverload();
		default:
			System.err.println("No current strategy. Exiting.");
			System.exit(1);
			return null;
		}
	}

	/**
	 * Sets a new strategy as the current one. Sets the re-plan flag afterwards.
	 * @param strategy The new strategy to employ.
	 */
	public void setStrategy(Strategy strategy) {
		if (strategy == null) {
			System.out.println("Setting strategy to null");
			currentStrategy = null;
		}
		currentStrategy = strategy;
		replan = true;
	}

	/**
	* Checks if the current operation succeeded, given the current pitch info.
	* @param dpi Current pitch info.
	* @return True if the current operation is null, false otherwise.
	* WARNING: Override in children classes and call this method first thing.
	*/
	protected boolean operationSuccessful(DynamicPitchInfo dpi) {
		if (currentStrategy == null)
			return true;
		return false;
	}

	/**
	* Checks if there is a problem with executing the current operation.
	* @param dpi Current pitch info.
	* @return True if the current operation is null, false otherwise.
	* WARNING: Override in children classes and call this method first thing.
	*/
	protected boolean problemExists(DynamicPitchInfo dpi) {
		if (currentStrategy == null) {
			System.out.println("Returning true");
			return true;
			
		}
		return false;
	}

	/**
	 * Checks if re-planing is necessary and passes the DynamicPitchInfo to 
	 * the PathFinder.
	 * @param dpi The DynamicPitchInfo to use when deciding if re-planing is
	 * necessary or not.
	 */
	@Override
	public void consumeInfo(DynamicPitchInfo dpi) {
		boolean success = operationSuccessful(dpi);
		boolean problem = problemExists(dpi);
		if (replan || success || problem) {
			currentOperation = planNextOperation(dpi);;
			pathFinder.setOperation(currentOperation);
			replan = false;
		}
		pathFinder.consumeInfo(dpi);
	}
	
	/**
	 * this function will tell us if we are facing the oppositions goal
	 * it compares the angle we are facing with the angle to the extremes
	 * of the goal, it must act differently for each goal as one of the goals has the zero angle in the middle 
	 * @param alfieInfo alfies info
	 * @param opponentInfo opponent info used to get the points of the goal where shooting for
	 * @param ball nuff said
	 * @return boolean
	 */
	public static boolean shotOnGoal(DynamicRobotInfo alfieInfo, DynamicRobotInfo opponentInfo, Point2D ball){
		//TODO take account of other robots location
		Point2D topGoal = opponentInfo.getTopGoalPost();
		Point2D bottomGoal = opponentInfo.getBottomGoalPost();
		Point2D alfiePos = alfieInfo.getPosition();
		double facing = alfieInfo.getFacingDirection();
		
		Point2D ourGoal = alfieInfo.getTopGoalPost();
		double ourGoalLine = ourGoal.getX();
		double theirGoalLine = topGoal.getX();
		
		double topAngle = getAngleFromOrigin(alfiePos,topGoal);
		double bottomAngle = getAngleFromOrigin(alfiePos, bottomGoal);
		
		
		if(theirGoalLine > ourGoalLine){
			if(facing>bottomAngle && facing<topAngle){
				return true;
			}else{
				return false;
			}
		}else{
			if(facing<bottomAngle && facing>topAngle){
				return true;
			}else{
				return false;
			}
		}
		
		
	}


	/**
	 * returns the angle from a point to another point with repect the plane of are zero angle. 
	 * @param alfiePos postion of our robot
	 * @param targetPosition position of the target we are working out the angle to the ball
	 * @return double
	 */
	protected static double getAngleFromOrigin(Point2D alfiePos, Point2D targetPosition) {
		double dx = (targetPosition.getX() - alfiePos.getX());
		double dy = (targetPosition.getY() - alfiePos.getY());
		
		double angle = Math.toDegrees(Math.atan2(dy, dx));
		if(angle<0){
			angle = 360 +angle;
		}
		return angle;
	}
}
