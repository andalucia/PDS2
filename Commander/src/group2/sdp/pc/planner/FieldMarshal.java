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
 * <p> <b>Field Marshal</b>:	A {@link StrategyConsumer} and a {@link DynamicInfoConsumer}</p>
<p><b>Description</b>:	The highest military rank in the army. Decides what Operations 
				should be executed. Passes them to a {@link OperationConsumer}, supplied
				on construction of the FieldMarshal. Also passes down the 
				{@link DynamicInfo} it was given to a DynamicInfoConsumer, supplied
				on construction of the Field Marshal. </p>
<p><b>Main client</b>:	{@link PathFinder}</p>
<p><b>Produces</b>:	{@link Operation}</p>
<p><b>Responsibilities</b>:</br>
		Producing an Operation and monitoring if it is successful or if
		a problem occurs.</p>
<p><b>Policy</b>:</br>      
	Planning:</br>   Analysing the DynamicInfo (*how*), the FieldMarshal comes up with an
	Operation. After that, it checks the success of the operation or
	if a problem occurred on each DynamicInfo it receives. If there is either,
	the FieldMarshal comes up with a new Operation. Otherwise, just 
	passes the DynamicInfo to its DynamicInfoConsumer.</p>
 */
public class FieldMarshal implements DynamicInfoConsumer, StrategyConsumer {

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
	
	protected OperationConsumer operationConsumer;
	protected DynamicInfoConsumer dynamicInfoConsumer;

	public FieldMarshal(GlobalInfo globalInfo, OperationConsumer operationConsumer, DynamicInfoConsumer dynamicInfoConsumer) {
		this.globalInfo = globalInfo;
		this.operationConsumer = operationConsumer;
		this.dynamicInfoConsumer = dynamicInfoConsumer;
	}

	/**
	 * Most important method of the class. According to the current strategy
	 * and dpi, plans the next operation that should be executed.
	 * @param dpi The DynamicPitchInfo to use for planning the next operation
	 * to execute.
	 * @return The next operation to execute.
	 */
	private Operation planNextOperation(DynamicInfo dpi) {
		DynamicRobotInfo alfieInfo = dpi.getAlfieInfo();
		DynamicBallInfo ballInfo = dpi.getBallInfo();
		DynamicRobotInfo opponentInfo = dpi.getOpponentInfo();
		
		Point2D opponentPosition = opponentInfo.getPosition();
		Point2D ballPosition = ballInfo.getPosition();
		Point2D alfiePosition = alfieInfo.getPosition();
		double alfieFacing = alfieInfo.getFacingDirection();
		
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
			} else {
				//get to defensive position
				//TODO check for obstacle
				double y1 = globalInfo.getPitch().getTopGoalPostYCoordinate();
				double y2 = globalInfo.getPitch().getBottomGoalPostYCoordinate();
				int x = dynamicInfoChecker.getDefensiveGoalOfRobot(true);
				int middleY = (int)(y1 + y2) / 2;
				Point2D middleOfGoal = new Point(x,middleY);
				OperationReallocation cmd = new OperationReallocation(middleOfGoal, alfiePosition, alfieFacing, opponentInfo.getPosition());
				return cmd;
			}

		case OFFENSIVE:
			if(dynamicInfoChecker.hasBall(alfieInfo, ballPosition) 
					|| kickingPosition.distance(alfiePosition) < 5){
				if(dynamicInfoChecker.shotOnGoal(alfieInfo, opponentInfo, ballPosition) 
						){
					return new OperationStrike();
				} else {
					// no shot on goal
					double y1 = globalInfo.getPitch().getTopGoalPostYCoordinate();
					double y2 = globalInfo.getPitch().getBottomGoalPostYCoordinate();
					int middleY = (int)(y1 + y2) / 2;
					int x = dynamicInfoChecker.getDefensiveGoalOfRobot(false);
					Point2D middleOfGoal = new Point(x, middleY);
					return new OperationCharge(ballPosition, alfiePosition, alfieFacing, middleOfGoal);
					}
			} else {
				// we don't have the ball in our possession so we need to go get it
				return new OperationReallocation(ballPosition, alfiePosition, alfieFacing, opponentPosition);
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
			currentStrategy = null;
		}
		currentStrategy = strategy;
		replan = true;
	}

	/**
	 * Checks if the current operation succeeded, given the current pitch info.
	 * @param dpi Current pitch info.
	 * @return True if the current operation is null, false otherwise.
	 * TODO implement
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
	 * TODO implement
	 */
	protected boolean problemExists(DynamicInfo dpi) {
		if (currentStrategy == null) {
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
			currentOperation = planNextOperation(dpi);
			operationConsumer.consumeOperation(currentOperation);
			replan = false;
		}
		dynamicInfoConsumer.consumeInfo(dpi);
	}
}
