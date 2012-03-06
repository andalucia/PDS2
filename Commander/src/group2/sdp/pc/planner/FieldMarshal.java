package group2.sdp.pc.planner;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.globalinfo.DynamicInfoChecker;
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
	
	protected DynamicInfoChecker dynamicInfoChecker;
	
	protected int DANGER_ZONE = 70;

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
		
		Point2D opponentPosition = opponentInfo.getPosition();
		Point2D ballPosition = ballInfo.getPosition();
		Point2D alfiePosition = AlfieInfo.getPosition();
		double alfieFacing = AlfieInfo.getFacingDirection();
		
		Point2D kickingPosition = dynamicInfoChecker.getKickingPosition(ballPosition);

		if (currentStrategy == null) {
			System.err.println("No current strategy. Stopping.");
			System.exit(1);
			return null;
		}

		switch (currentStrategy) {
		case DEFENSIVE:
			if (currentOperation instanceof OperationReallocation && operationSuccessful(dpi)) {
				return null;
			} else if (dynamicInfoChecker.inDefensivePosition(AlfieInfo, ballPosition)) {
				//TODO check for obstacle
				OperationReallocation cmd = new OperationReallocation(ballPosition, alfiePosition, alfieFacing, opponentInfo.getPosition());
				return cmd;
			} else {
				//get to defensive position
				//TODO check for obstacle
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
				OperationReallocation cmd = new OperationReallocation(middleOfGoal, alfiePosition, alfieFacing, opponentInfo.getPosition());
				return cmd;
			}

		case OFFENSIVE:
			if(dynamicInfoChecker.hasBall(AlfieInfo, ballPosition)){
				System.out.println("HAS BALL");
				if(dynamicInfoChecker.shotOnGoal(AlfieInfo, opponentInfo, ballPosition)){
					System.out.println("SHOT ON GOAL");
					return new OperationStrike();
				} else {
					// no shot on goal
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
					return new OperationCharge(middleOfGoal, alfiePosition, alfieFacing, middleOfGoal);
				}
			} else {
				// no ball
				// check if enemy robot is in the way
				//FIXME use robbie's version
				if((alfiePosition.distance(opponentPosition)<DANGER_ZONE)&&
								dynamicInfoChecker.opponentBlockingPath(AlfieInfo, opponentInfo)){
					DANGER_ZONE = 90;
					// they are in the way!
					System.out.println("Using checkpoint");
					Point2D.Double checkpoint = dynamicInfoChecker.findTangentIntersect(alfiePosition, ballPosition, opponentPosition, 40);
					return new OperationReallocation(checkpoint, alfiePosition, alfieFacing, opponentPosition);
				} else {
					DANGER_ZONE = 70;
					return new OperationReallocation(kickingPosition, alfiePosition, alfieFacing, opponentPosition);
				}
			}

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
		dynamicInfoChecker = new DynamicInfoChecker(globalInfo,dpi);
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
}
