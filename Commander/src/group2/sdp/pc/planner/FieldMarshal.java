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

import lejos.geom.Point;

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
			} else if (inDefensivePosition(AlfieInfo,ball)) {
				OperationReallocation cmd = new OperationReallocation(ball, alfie, facing);
				return cmd;
			} else {
				//get to defensive position
				double middleOfGoalY = (AlfieInfo.getTopGoalPost().getY() + AlfieInfo.getBottomGoalPost().getY())/2;
				Point2D middleOfGoal = new Point((int)(AlfieInfo.getTopGoalPost().getX()),(int) (middleOfGoalY));
				OperationReallocation cmd = new OperationReallocation(middleOfGoal, alfie, facing);
				return cmd;
			}

		case OFFENSIVE:
			if(Overlord.hasBall(AlfieInfo, ball)){
				System.out.println("HAS BALL");
				if(shotOnGoal(AlfieInfo, opponentInfo, ball)){
					System.out.println("SHOT ON GOAL");
					return new OperationStrike();
				} else {
					int goalMiddlex = (int)(AlfieInfo.getTopGoalPost().getX()+AlfieInfo.getBottomGoalPost().getX()/2);
					int goalMiddley = (int) (AlfieInfo.getTopGoalPost().getY()+AlfieInfo.getBottomGoalPost().getY()/2);
					Point2D goalMiddle = new Point(goalMiddlex,goalMiddley);
					System.out.println("CHAAAAARGE");
					return new OperationCharge(ball, alfie, facing,goalMiddle);
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
	 * Checks if re-planning is necessary and passes the DynamicPitchInfo to 
	 * the PathFinder.
	 * @param dpi The DynamicPitchInfo to use when deciding if re-planing is
	 * necessary or not.
	 */
	@Override
	public void consumeInfo(DynamicPitchInfo dpi) {
		boolean success = operationSuccessful(dpi);
		boolean problem = problemExists(dpi);
		if (replan || success || problem) {
			System.out.println("REPLANNING");
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
	 * @param robotInfo robot's info
	 * @param opponentInfo opponent info used to get the points of the goal where shooting for
	 * @param ball nuff said
	 * @return boolean
	 */
	public static boolean shotOnGoal(DynamicRobotInfo robotInfo, DynamicRobotInfo opponentInfo, Point2D ball){

		Point2D topGoal = opponentInfo.getTopGoalPost();
		Point2D bottomGoal = opponentInfo.getBottomGoalPost();
		Point2D alfiePos = robotInfo.getPosition();
		Point2D enemyPos = opponentInfo.getPosition();
		double facing = robotInfo.getFacingDirection();

		Point2D ourGoal = robotInfo.getTopGoalPost();
		double ourGoalLine = ourGoal.getX();
		double theirGoalLine = topGoal.getX();

		double topAngle = getAngleFromOrigin(alfiePos,topGoal);
		double bottomAngle = getAngleFromOrigin(alfiePos, bottomGoal);
		//if other robot is in the way threshold can be changed, current uses 30 degree angle and 10cm distance
		if((alfiePos.distance(enemyPos)<10)&&(isSimilarAngle(getAngleFromOrigin(alfiePos,enemyPos),robotInfo.getFacingDirection(),30))){
			return false;
		}

		if(theirGoalLine > ourGoalLine) {
			if(facing>bottomAngle || facing<topAngle) {
				return true;
			}else{
				return false;
			}
		} else {
			if (facing<bottomAngle && facing>topAngle) {
				return true;
			} else {
				return false;
			}
		}
	}


	/**
	 * returns the angle from a point to another point with repect the plane of are zero angle. 
	 * @param origin 
	 * @param targetPosition position of the target we are working out the angle to the ball
	 * @return double
	 */
	protected static double getAngleFromOrigin(Point2D origin, Point2D targetPosition) {
		double dx = (targetPosition.getX() - origin.getX());
		double dy = (targetPosition.getY() - origin.getY());

		double angle = Math.toDegrees(Math.atan2(dy, dx));
		if(angle<0){
			angle = 360 +angle;
		}
		return angle;
	}

	/**
	 * Checks if the robot is in a defensive position. If true it means the robot is closer 
	 * to the robot's goal than the ball and is not facing the robot's goal. Or it is facing 
	 * the robot's goal and is around halfway between the ball and the goal (see threshold)
	 * @param robotInfo
	 * @param ballInfo
	 * @return
	 */
	public static boolean inDefensivePosition(DynamicRobotInfo robotInfo, Point2D ball) {
		double goalX = robotInfo.getTopGoalPost().getX();
		double ballX = ball.getX();
		double robotX = robotInfo.getPosition().getX();
		double betweenBallAndGoalX = (goalX + ballX)/2;

		int threshold = 30;

		if (!Overlord.correctSide(robotInfo, ball)) {
			return false;
		} else {
			double angleToGoal = PathFinder.getAngleToTarget(robotInfo.getTopGoalPost(), robotInfo.getPosition(), robotInfo.getFacingDirection());
			if (Math.abs(angleToGoal) > 90) {
				return true;
			} else {
				if (Math.abs(robotX - betweenBallAndGoalX) < threshold) {
					return true;
				} else {
					return false;
				}
			}
		}
	}


	/**
	 * Method for comparison of angles. If angle within certain threshold of each other then
	 * return true else false
	 * @param angle1 first angle for comparison
	 * @param angle2 second angle for comparison
	 * @param threshold max difference for angles to be similar
	 * @return are angles within threshold of each other
	 */
	protected static boolean isSimilarAngle(double angle1, double angle2, double threshold){
		double bigAngle;
		double smallAngle;
		if (angle1==angle2){
			return true;
		}
		if (angle1>=angle2){
			bigAngle=angle1;
			smallAngle=angle2;
		}else{
			bigAngle=angle2;
			smallAngle=angle1;
		}
		if(bigAngle-smallAngle<=threshold){
			return true;
		}
		//check to solve 360-0 problem
		if(bigAngle>=(360-threshold) && smallAngle<=0+(threshold-(360-bigAngle))){
			return true;
		}
		return false;
	}
}
