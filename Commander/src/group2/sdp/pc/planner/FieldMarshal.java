package group2.sdp.pc.planner;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.globalinfo.GlobalInfo;
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
	
	protected GlobalInfo globalInfo;
	
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

	public FieldMarshal(GlobalInfo globalInfo, PathFinder pathFinder) {
		this.globalInfo = globalInfo;
		this.pathFinder = pathFinder;
	}

	/**
	 * Most important method of the class. According to the current strategy
	 * and dpi, plans the next operation that should be executed.
	 * @param dpi The DynamicPitchInfo to use for planning the next operation
	 * to execute.
	 * @return The next operation to execute.
	 */
	private Operation planNextOperation(DynamicInfo dpi) {
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
			} else if (inDefensivePosition(AlfieInfo, ball)) {
				OperationReallocation cmd = new OperationReallocation(ball, alfie, facing, opponentInfo.getPosition());
				return cmd;
			} else {
				//get to defensive position
				double y1 = globalInfo.getPitch().getTopGoalPostYCoordinate();
				double y2 = globalInfo.getPitch().getBottomGoalPostYCoordinate();
				Point2D middleOfGoal = 
					new Point(
							(int) (
									globalInfo.isAttackingRight() 
									? globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX()
									: globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX()
							),
							(int) (y1 + y2) / 2
					);
				OperationReallocation cmd = new OperationReallocation(middleOfGoal, alfie, facing, opponentInfo.getPosition());
				return cmd;
			}

		case OFFENSIVE:
			if(Overlord.hasBall(AlfieInfo, ball)){
				System.out.println("HAS BALL");
				if(shotOnGoal(AlfieInfo, opponentInfo, ball)){
					System.out.println("SHOT ON GOAL");
					return new OperationStrike();
				} else {
					double y1 = globalInfo.getPitch().getTopGoalPostYCoordinate();
					double y2 = globalInfo.getPitch().getBottomGoalPostYCoordinate();
					Point2D middleOfGoal = 
						new Point(
								(int) (
										!globalInfo.isAttackingRight() 
										? globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX()
										: globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX()
								),
								(int) (y1 + y2) / 2
						);
					System.out.println("CHAAAAARGE");
					return new OperationCharge(ball, alfie, facing, middleOfGoal);
				}
			}
			return new OperationReallocation(ball, alfie, facing,opponentInfo.getPosition());

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
	protected boolean operationSuccessful(DynamicInfo dpi) {
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
	protected boolean problemExists(DynamicInfo dpi) {
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
	public void consumeInfo(DynamicInfo dpi) {
		boolean success = operationSuccessful(dpi);
		boolean problem = problemExists(dpi);
		if (replan || success || problem) {
			//System.out.println("REPLANNING");
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
	public boolean shotOnGoal(DynamicRobotInfo robotInfo, DynamicRobotInfo opponentInfo, Point2D ball){
		float x = (float) (
				globalInfo.isAttackingRight() 
				? globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX()
				: globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX()
		);
		float y1 = globalInfo.getPitch().getTopGoalPostYCoordinate();
		float y2 = globalInfo.getPitch().getBottomGoalPostYCoordinate();
		
		
		Point2D topGoal = new Point2D.Float(x, y1);
		Point2D bottomGoal = new Point2D.Float(x, y2);
		Point2D alfiePos = robotInfo.getPosition();
		Point2D enemyPos = opponentInfo.getPosition();
		double facing = robotInfo.getFacingDirection();

		x = (float) (
				!globalInfo.isAttackingRight() 
				? globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX()
				: globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX()
		);
		float y = globalInfo.getPitch().getTopGoalPostYCoordinate();

		Point2D ourGoal = new Point2D.Float(x, y);
		double ourGoalLine = ourGoal.getX();
		double theirGoalLine = topGoal.getX();

		double topAngle = getAngleFromOrigin(alfiePos,topGoal);
		double bottomAngle = getAngleFromOrigin(alfiePos, bottomGoal);
		//if other robot is in the way threshold can be changed, current uses 30 degree angle and 10cm distance
		if((alfiePos.distance(enemyPos)<30)&&(isSimilarAngle(getAngleFromOrigin(alfiePos,enemyPos),robotInfo.getFacingDirection(),30))){
			System.out.println("ENEMY CLOSE NO SHOT");
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
	public boolean inDefensivePosition(DynamicRobotInfo robotInfo, Point2D ball) {
		float y1 = globalInfo.getPitch().getTopGoalPostYCoordinate();
		
		double goalX = 		
				globalInfo.isAttackingRight() 
				? globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX()
				: globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX()
		;
		double ballX = ball.getX();
		double robotX = robotInfo.getPosition().getX();
		double betweenBallAndGoalX = (goalX + ballX)/2;

		int threshold = 30;

		if (!correctSide(robotInfo, ball)) {
			return false;
		} else {
			float x = (float) (
					globalInfo.isAttackingRight() 
					? globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX()
					: globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX()
			);
			float y = globalInfo.getPitch().getTopGoalPostYCoordinate();
			Point2D topGoalPost = new Point2D.Float(x, y);
			double angleToGoal = PathFinder.getAngleToTarget(topGoalPost, robotInfo.getPosition(), robotInfo.getFacingDirection());
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
	
	/**
	 * FIXME: restructure
	 * checks to see if the robot is on the right side of the ball, does this by
	 * comparing the distance along x axis of the robot to its goal line and distance of ball to goal line.
	 * if the robot is closer its on the right/correct side so return true 
	 * @param robotInfo
	 * @param ballPos
	 * @return
	 */
	public boolean correctSide(DynamicRobotInfo robotInfo, Point2D ballPos){
		float x = (float) (
				globalInfo.isAttackingRight() 
				? globalInfo.getPitch().getMinimumEnclosingRectangle().getMinX()
				: globalInfo.getPitch().getMinimumEnclosingRectangle().getMaxX()
		);
		float y = globalInfo.getPitch().getTopGoalPostYCoordinate();
		Point2D TopGoal = new Point2D.Float(x, y);
		double goalLine = TopGoal.getX();
		
		Point2D robotPos = robotInfo.getPosition();
		double robot = robotPos.getX();
		double ball = ballPos.getX();
		
		double robotDis = Math.abs(goalLine - robot);
		double ballDis = Math.abs(goalLine - ball);
		
		if(robotDis < ballDis) {
			return true;
		} else{ 
			return false;
		}
	}
}
