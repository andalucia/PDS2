package group2.sdp.pc.planner;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.planner.operation.Operation;
import group2.sdp.pc.planner.operation.OperationCharge;
import group2.sdp.pc.planner.operation.OperationReallocation;
import group2.sdp.pc.planner.strategy.Strategy;

import java.awt.geom.Point2D;

/**
 * A field marshal decides what operations to start, knowing what strategy 
 * should be currently executed.
 */
public class FieldMarshal {
	
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
	 * Checks if re-planing is necessary and passes the DynamicPitchInfo to 
	 * the PathFinder.
	 * @param dpi The DynamicPitchInfo to use when deciding if re-planing is
	 * necessary or not.
	 */
	public void updateInfo(DynamicPitchInfo dpi) {
		boolean success = operationSuccessful(dpi);
		boolean problem = problemExists(dpi);
		if (replan || success || problem) {
			currentOperation = planNextOperation(dpi);;
			pathFinder.setOperation(currentOperation);
			replan = false;
		}
		pathFinder.updateInfo(dpi);
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

		Point2D ball = ballInfo.getPosition();
		Point2D alfie = AlfieInfo.getPosition();
		double facing = AlfieInfo.getFacingDirection();

		if (currentStrategy == null) {
			System.err.println("No current strategy. Exiting.");
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
			return new OperationCharge(ball, alfie, facing);

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
		if (currentStrategy == null)
			return true;
		return false;
	}
}
