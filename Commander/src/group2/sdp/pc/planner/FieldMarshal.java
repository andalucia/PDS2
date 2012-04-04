package group2.sdp.pc.planner;

import group2.sdp.common.util.Geometry;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.breadbin.StaticBallInfo;
import group2.sdp.pc.breadbin.StaticRobotInfo;
import group2.sdp.pc.globalinfo.DynamicInfoChecker;
import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.planner.operation.Operation;
import group2.sdp.pc.planner.operation.OperationOverload;
import group2.sdp.pc.planner.operation.OperationPenaltyDefend;
import group2.sdp.pc.planner.operation.OperationPenaltyTake;
import group2.sdp.pc.planner.operation.OperationReallocation;
import group2.sdp.pc.planner.operation.OperationStrike;
import group2.sdp.pc.planner.skeleton.OperationConsumer;
import group2.sdp.pc.planner.skeleton.StrategyConsumer;
import group2.sdp.pc.planner.strategy.Strategy;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

import java.awt.geom.Point2D;

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

	private static final boolean VERBOSE = true;
	
//	private static final double SAFE_FACTOR = 20.0;

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
	
	protected OperationConsumer operationConsumer;
	protected DynamicInfoConsumer dynamicInfoConsumer;

	private Strategy plannedStrategy;

	public FieldMarshal(
			OperationConsumer operationConsumer, 
			DynamicInfoConsumer dynamicInfoConsumer
	) {
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
		
		if (currentStrategy == null) {
			if (VERBOSE)
				System.err.println("No current strategy. Stopping.");
			System.exit(1);
			return null;
		}

		switch (currentStrategy) {
		case TEST:
//			if (!DynamicInfoChecker.wouldHaveBall(dpi.getAlfieInfo(), dpi.getBallInfo())) {
//				if (VERBOSE)
//					System.out.println("Don't have ball.");
//				Point2D ballPosition = dpi.getBallInfo().getPosition();
//				Point2D goalMiddle = GlobalInfo.getTargetGoalMiddle();
//				double shootingDirection = Geometry.getVectorDirection(ballPosition, goalMiddle);
//				
//				return new OperationReallocation(
//						ballPosition,
//						shootingDirection
//				);
////				return new OperationOverload();
//			} else {
//				if (VERBOSE)
//					System.out.println("Has ball.");
//				return new OperationStrike();
//			}
			System.out.println("ASD!");
			return planSweep(dpi);
			
		case DEFENSIVE:
			return planNextDefensive(dpi);

		case OFFENSIVE:
			return planNextOffensive(dpi);

		case STOP:
			return new OperationOverload();
		case PENALTY_DEFEND:
			return new OperationPenaltyDefend(dpi.getAlfieInfo().getFacingDirection());
		case PENALTY_TAKE:
			return new OperationPenaltyTake(dpi.getOpponentInfo().getPosition());
		default:
			if (VERBOSE)
				System.err.println("No current strategy. Exiting.");
			System.exit(1);
			return null;
		}
	}

	
	/**
	 * Tries to sweep the ball off the wall.
	 */
	private Operation planSweep(DynamicInfo dpi) {
		Point2D ballPosition = dpi.getBallInfo().getPosition();
		
		Point2D endPosition;
		
		double SPECIAL_THRESHOLD = 7.0;
		
		double distanceFromWall = SPECIAL_THRESHOLD + StaticRobotInfo.getWidth() / 2;
		
		if (ballPosition.getY() < 0.0) {
			endPosition = new Point2D.Double(
					ballPosition.getX(),
					GlobalInfo.getPitch().getMinimumEnclosingRectangle().getMinY() + 
					distanceFromWall
			);
		} else {
			endPosition = new Point2D.Double(
					ballPosition.getX(),
					GlobalInfo.getPitch().getMinimumEnclosingRectangle().getMaxY() - 
					distanceFromWall
			);
		}
		
		double endOrientation;
		if (GlobalInfo.isAttackingRight()) {
			endOrientation = 0.0;
		} else {
			endOrientation = 180.0;
		}
		
		return new OperationReallocation(endPosition, endOrientation);
	}
	
	/**
	 * Position Alfie between the ball and the centre of the goal it needs to
	 * defend. Make him face the ball. 
	 */
	private Operation planNextDefensive(DynamicInfo dpi) {
		Point2D ballPosition = dpi.getBallInfo().getPosition();
		Point2D defensiveGoalMiddlePosition = GlobalInfo.getDefensiveGoalMiddle();
		Point2D defensiveGoalRightPost = GlobalInfo.getDefensiveGoalRightPost();
		
		Point2D v1 = Geometry.getVectorDifference(
				defensiveGoalRightPost, 
				defensiveGoalMiddlePosition
		);
		
		Point2D v2 = Geometry.getVectorDifference(
				ballPosition, 
				defensiveGoalMiddlePosition
		); 
		
		// get the sine of the angle between the vectors by using cross product
		
		double cp = Geometry.crossProduct(v1, v2);
		
		double sine = cp / 
			(Geometry.getVectorLength(v1) * Geometry.getVectorLength(v2)); 
		
//		double safeDistance = 			
//			(PathFinder.HARDCODED_SECOND_RADIUS_REMOVEME + 
//			dpi.getAlfieInfo().getSafeDistance()) / sine ;
		double safeDistance = 25.0;
		
		double goalToBallDirection = 
			Geometry.getVectorDirection(defensiveGoalMiddlePosition, ballPosition);
		
		Point2D destination = Geometry.translate(defensiveGoalMiddlePosition, safeDistance, 
				goalToBallDirection);
		
//		double unsafeDistance = 0.0;
//		if (defensiveGoalMiddlePosition.distance(destination) > 
//			defensiveGoalMiddlePosition.distance(ballPosition)) {
//			// Safe distance won't work. Try a sweep.
//			unsafeDistance = dpi.getAlfieInfo().getSafeDistance();
//			destination = Geometry.translate(defensiveGoalMiddlePosition, unsafeDistance, 
//					goalToBallDirection);
//		}
		
		if (VERBOSE) {
			System.out.println("Ball position: " + ballPosition);
			System.out.println("cp: " + cp);
			System.out.println("v1: " + v1);
			System.out.println("v2: " + v2);
			System.out.println("Sine: " + sine);
			System.out.println("Goal-to-ball direction: " + goalToBallDirection);
			System.out.println("Safe distance: " + safeDistance);
//			System.out.println("Unsafe distance: " + unsafeDistance);
			System.out.println("Defensive destination: " + destination);
		}
		double destinationX;
		//TODO use previous stuff
		if (defensiveGoalMiddlePosition.getX() < 0) {
			//left goal
			destinationX = defensiveGoalMiddlePosition.getX() + 35;
		} else {
			//right goal
			destinationX = defensiveGoalMiddlePosition.getX() + 35;
		}
		destination = new Point2D.Double(destinationX,0);
		return new OperationReallocation(destination, goalToBallDirection);
	}

	private Operation planNextOffensive(DynamicInfo dpi) {
		// Check for ball possession.
//		if (!DynamicInfoChecker.wouldHaveBall(dpi.getAlfieInfo(), dpi.getBallInfo())) {
			if (VERBOSE)
				System.out.println("Don't have ball.");
			
			Point2D ballPosition = dpi.getBallInfo().getPosition();
			double SWEEPING_DISTANCE = StaticRobotInfo.getWidth();
				
			// Check if the ball is close to a wall.
			if (Geometry.pointToRectangleDistance(
					ballPosition, 
					GlobalInfo.getPitch().getMinimumEnclosingRectangle()
				) < SWEEPING_DISTANCE) {
				return planSweep(dpi);
			} else {
				Point2D goalMiddle = GlobalInfo.getTargetGoalMiddle();
				double shootingDirection = Geometry.getVectorDirection(ballPosition, goalMiddle);
				
				return new OperationReallocation(
						ballPosition,
						shootingDirection
				);
			}
//		} else {
//			if (VERBOSE)
//				System.out.println("Has ball.");
//			return new OperationStrike();
//		}
	}

	/**
	 * Sets a new strategy as the current one. Sets the re-plan flag afterwards.
	 * @param strategy The new strategy to employ.
	 */
	public void setStrategy(Strategy strategy) {
		currentStrategy = strategy;
		if ((plannedStrategy == Strategy.DEFENSIVE && currentStrategy == Strategy.OFFENSIVE) || 
			(plannedStrategy == Strategy.OFFENSIVE && currentStrategy == Strategy.DEFENSIVE)) {
			replan = false;
		} else {
			replan = true;
		}
	}

	private long successStartTime = 0;
	
	/**
	 * Checks if the current operation succeeded, given the current pitch info.
	 * @param dpi Current pitch info.
	 * @return True if the current operation is null, false otherwise.
	 */
	protected boolean operationSuccessful(DynamicInfo dpi) {
		int REALLOCATION_DISTANCE_THRESHOLD = 20;
		
		if (currentStrategy == null)
			return true;
		
		long SUCCESS_TIMEOUT = 1000;
		
		if (currentOperation instanceof OperationReallocation) {
			Point2D pos = dpi.getAlfieInfo().getPosition();
			Point2D aim = ((OperationReallocation) currentOperation).getPosition();
			if (pos.distance(aim) < REALLOCATION_DISTANCE_THRESHOLD) {
				long now = System.currentTimeMillis();
				if (successStartTime > 0 && now - successStartTime > SUCCESS_TIMEOUT) {
					successStartTime = 0;
					if (VERBOSE) {
						System.out.println("Operation successful.");
					}
					return true;
				}
				if (successStartTime == 0)
					successStartTime = now;
			} else {
				successStartTime = 0;
			}
		}
		
		return false;
	}

	private long failureStartTime = 0;
	private boolean didNotFail = false;
	
	/**
	 * Checks if there is a problem with executing the current operation.
	 * @param dpi Current pitch info.
	 * @return True if the current operation is null, false otherwise.
	 */
	protected boolean operationFailed(DynamicInfo dpi) {
		if (currentStrategy == null) {
			return true;
		}
		
		if (currentOperation instanceof OperationReallocation) {
			OperationReallocation op = (OperationReallocation)currentOperation;
			
			int STOP_THRESHOLD = 10;
			double ROLL_THRESHOLD = 5.0;
			long FAILURE_TIMEOUT = 500;
			
			if (didNotFail && dpi.getAlfieInfo().getTravelSpeed() < STOP_THRESHOLD) {
				long now = System.currentTimeMillis();
				if (failureStartTime > 0 && now - failureStartTime > FAILURE_TIMEOUT) {
					System.out.println("Failed reaching " + op.getPosition());
					failureStartTime = 0;
					didNotFail = false;
					return true;
				}
				if (failureStartTime == 0) {
					failureStartTime = now;
				}
			} else {
				didNotFail = true;
				failureStartTime = 0;	
			}
			Point2D ballPosition = dpi.getBallInfo().getPosition(); 
			if (ballPosition.distance(op.getPosition()) > ROLL_THRESHOLD) {
				failureStartTime = 0;
				return true;
			}
		}
		// If Overload - fail all the time
		if (currentOperation instanceof OperationOverload) {
			long FAILURE_TIMEOUT = 1;
			long now = System.currentTimeMillis();
			
			if (failureStartTime > 0 && now - failureStartTime > FAILURE_TIMEOUT) {
				failureStartTime = 0;
				return true;
			}
			if (failureStartTime == 0) {
				failureStartTime = now;
			}
		}
		
		// If OperationPenaltyDefend - fail all the time
		if (currentOperation instanceof OperationPenaltyDefend) {
			long FAILURE_TIMEOUT = 500;
			long now = System.currentTimeMillis();
			if (failureStartTime > 0 && now - failureStartTime > FAILURE_TIMEOUT) {
				failureStartTime = 0;
				return true;
			}
			if (failureStartTime == 0) {
				failureStartTime = now;
			}
		}
		
		// If OperationPenaltyTake - fail all the time
		if (currentOperation instanceof OperationPenaltyDefend) {
			long FAILURE_TIMEOUT = 500;
			long now = System.currentTimeMillis();
			if (failureStartTime > 0 && now - failureStartTime > FAILURE_TIMEOUT) {
				failureStartTime = 0;
				return true;
			}
			if (failureStartTime == 0) {
				failureStartTime = now;
			}
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
		boolean fail = operationFailed(dpi);
		if (replan || success || fail) {
			if (VERBOSE) {
				System.out.println("Replan: " + replan);
				System.out.println("Success: " + success);
				System.out.println("Fail: " + fail);	
			}
			currentOperation = planNextOperation(dpi);
			operationConsumer.consumeOperation(currentOperation);
			plannedStrategy = currentStrategy;
			replan = false;
		}
		dynamicInfoConsumer.consumeInfo(dpi);
	}
	
	/**
	 * The butt rule is that the ray opposite the end ray, starting from the 
	 * same point, should not cross the danger zone of the ball.
	 * @param ballInfo The ball info to use.
	 * @param robotInfo Physical dimensions of this robot are used. 
	 * @param endPosition The position where a particular path might lead to.
	 * @param endAngle The direction of the robot after following a particular path.
	 * @return If the butt rule is valid or not, i.e. if the ray opposite the end ray, 
	 * starting from the same point, does not cross the danger zone of the ball.
	 */
	public static boolean checkButtRule(StaticBallInfo ballInfo, 
			StaticRobotInfo robotInfo, Point2D endPosition, double endAngle) {
		double zoneRadius = ballInfo.getDangerZoneRadius(robotInfo);
		endAngle = Math.toRadians(endAngle);
		Point2D v = new Point2D.Double(Math.cos(endAngle),Math.sin(endAngle));
		
		int n = Geometry.getNumberOfRayCircleIntersections(
				endPosition, 
				v, 
				ballInfo.getPosition(), 
				zoneRadius
		);

		return n <= 1;
	}

	@Override
	public void start() {
		operationConsumer.start();
	}

	@Override
	public void stop() {
		operationConsumer.stop();
	}
}
