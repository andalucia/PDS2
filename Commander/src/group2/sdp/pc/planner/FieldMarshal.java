package group2.sdp.pc.planner;

import group2.sdp.common.util.Geometry;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.breadbin.StaticBallInfo;
import group2.sdp.pc.breadbin.StaticRobotInfo;
import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.planner.operation.Operation;
import group2.sdp.pc.planner.operation.OperationOverload;
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

	private static final double SAFE_FACTOR = 20.0;

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
			System.err.println("No current strategy. Stopping.");
			System.exit(1);
			return null;
		}

		switch (currentStrategy) {
		case TEST_PATH_FINDER:
			if (!dpi.getAlfieInfo().isHasBall()) {
				System.out.println("Don't have ball.");
				Point2D ballPosition = dpi.getBallInfo().getPosition();
				Point2D goalMiddle = GlobalInfo.getTargetGoalMiddle();
				double shootingDirection = Geometry.getVectorDirection(ballPosition, goalMiddle);
				
				return new OperationReallocation(
						ballPosition,
						shootingDirection
				);
			} else {
				return new OperationStrike();
			}
			
		case DEFENSIVE:
			return planNextDefensive(dpi);

		case OFFENSIVE:
			return planNextOffensive(dpi);

		case STOP:
			return new OperationOverload();
		default:
			System.err.println("No current strategy. Exiting.");
			System.exit(1);
			return null;
		}
	}

	private Operation planNextDefensive(DynamicInfo dpi) {
		Point2D ballPosition = dpi.getBallInfo().getPosition();
		double opponentDirection = dpi.getOpponentInfo().getFacingDirection();
		Point2D g1 = GlobalInfo.getDefendingTopGoalPost();
		Point2D g2 = GlobalInfo.getDefendingBottomGoalPost();
		
		Point2D temp = Geometry.generateRandomPoint(ballPosition, opponentDirection);
		
		System.out.println("temp: " + temp);
		System.out.println("opponentDirection: " + opponentDirection);
		
		System.out.println("G1: " + g1);
		System.out.println("G2: " + g2);
		Point2D d = Geometry.getLinesIntersection(ballPosition, temp, g1, g2);
		System.out.println("D point: " + d);
		double reverseDirection = Geometry.reverse(opponentDirection);
		
		double factor = SAFE_FACTOR + 
			PathFinder.HARDCODED_SECOND_RADIUS_REMOVEME + StaticRobotInfo.getWidth();
		
		Point2D directionVector = Geometry.getDirectionVector(reverseDirection);
		Point2D s = Geometry.translate(d, factor, directionVector);
		System.out.println("S point: " + s);
		return new OperationReallocation(s, reverseDirection);
	}

	private Operation planNextOffensive(DynamicInfo dpi) {
		Point2D ballPosition = dpi.getBallInfo().getPosition();
		Point2D goalMiddle = GlobalInfo.getTargetGoalMiddle();
		double shootingDirection = Geometry.getVectorDirection(ballPosition, goalMiddle);
		
		return new OperationReallocation(
				ballPosition,
				shootingDirection
		);
	}

	/**
	 * Sets a new strategy as the current one. Sets the re-plan flag afterwards.
	 * @param strategy The new strategy to employ.
	 */
	public void setStrategy(Strategy strategy) {
		currentStrategy = strategy;
		replan = true;
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
					System.out.println("Operation successful.");
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
	 * TODO implement
	 */
	protected boolean operationFailed(DynamicInfo dpi) {
		if (currentStrategy == null) {
			return true;
		}
		
		if (currentOperation instanceof OperationReallocation) {
			OperationReallocation op = (OperationReallocation)currentOperation;
			
			int STOP_THRESHOLD = 10;
			double ROLL_THRESHOLD = 5.0;
			long FAILURE_TIMEOUT = 800;
			
			if (didNotFail && dpi.getAlfieInfo().getTravelSpeed() < STOP_THRESHOLD) {
				long now = System.currentTimeMillis();
				if (failureStartTime > 0 && now - failureStartTime > FAILURE_TIMEOUT) {
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
			System.out.println("Replan: " + replan);
			System.out.println("Success: " + success);
			System.out.println("Fail: " + fail);
			currentOperation = planNextOperation(dpi);
			operationConsumer.consumeOperation(currentOperation);
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
