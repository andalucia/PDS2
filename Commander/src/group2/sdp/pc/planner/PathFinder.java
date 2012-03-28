package group2.sdp.pc.planner;

import group2.sdp.common.util.Geometry;
import group2.sdp.common.util.Pair;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.breadbin.StaticRobotInfo;
import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.mouth.MouthInterface;
import group2.sdp.pc.planner.operation.Operation;
import group2.sdp.pc.planner.operation.OperationReallocation;
import group2.sdp.pc.planner.pathstep.PathStep;
import group2.sdp.pc.planner.pathstep.PathStepArc;
import group2.sdp.pc.planner.pathstep.PathStepArcBackwardsLeft;
import group2.sdp.pc.planner.pathstep.PathStepArcBackwardsRight;
import group2.sdp.pc.planner.pathstep.PathStepArcForwardsLeft;
import group2.sdp.pc.planner.pathstep.PathStepKick;
import group2.sdp.pc.planner.pathstep.PathStepSpinLeft;
import group2.sdp.pc.planner.skeleton.OperationConsumer;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

import java.awt.AlphaComposite;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

/**
 * The PathFinder decides how to execute a particular operation.
 * 
 * It does this by checking the operation to be executed and creating a queue of pathStepList. It then
 * proceeds to execute them in order and checking whether they are success or have failed in every
 * consumeInfo() cycle
 */
public class PathFinder implements DynamicInfoConsumer, OperationConsumer{
	
	public static final double HARDCODED_SECOND_RADIUS_REMOVEME = 20.0;

	private static final boolean verbose = true;

	private static final double DISTANCE_THRESHOLD = 10.0;
	
	/**
	 * This is a queue which stores a list of paths 
	 */
	private LinkedList<PathStep> pathStepList = new LinkedList<PathStep>();
	private PathStep currentStep = null;
	private Operation currentOperation;
	
	private MouthInterface mouth;
	
	private boolean replan;
	
	/**
	 * Create the path finder object and setup the GlobalInfo and Mouth
	 * 
	 * @param Persistant global pitch info
	 * @param the mouth used for communicating with Alfie or the simulator
	 */
	public PathFinder(MouthInterface mouth) {
		this.mouth = mouth;
	}
	
	private final int WARMUP_TIMEOUT = 800;
	
	@Override
	public void consumeInfo(DynamicInfo dpi) {
		if (replan || currentStep == null) {
			plan(dpi);
			executeNextStep(dpi);
			replan = false;
		} else {
			long now = System.currentTimeMillis();
			if (currentStep.isSuccessful(dpi)) {
				if (verbose) {
					System.out.println("Step succeeded :D");
				}
				executeNextStep(dpi);
			} else if (now - lastPlanIssuedTime > WARMUP_TIMEOUT &&
					currentStep.hasFailed(dpi)) {
				if (verbose) {
					System.out.println("Step failed D:");
				}
				plan(dpi);
			}
		}
	}
	
	public void executeNextStep(DynamicInfo dpi) {	
		// Get the next PathStep from the queue
		currentStep = pathStepList.pollFirst();
		// If it is successful, try and execute the next PathStep in the queue
		if(currentStep != null) {
			execute();
		} else {
			// The queue is empty so re-plan
			 plan(dpi);
		}		
	}
	
	/**
	 * This method is called by the FieldMarshal every time the operation changes. If the operation
	 * changes something drastic and eventful has happened on the pitch and we should re-plan
	 * @param newOperation The operation we should now execute
	 */
	public void setOperation(Operation newOperation) {
		this.currentOperation = newOperation;
		replan = true;
	}
	
	private long lastPlanIssuedTime = 0; 
	
	/**
	 * This method works out all the logic of the pathFinder and adds each step the queue which will
	 * them be executed in turn.
	 * 
	 * Every time this method is called the pathStepList should be cleared and re-planning should take
	 * place from scratch
	 * @param dpi 
	 */
	private void plan(DynamicInfo dpi) {
		
		System.out.println("Looking for a path...");
		// Clear the PathStep queue
		pathStepList.clear();
		
		// Plan based on the current operation type
		switch(currentOperation.getType()) {
			
		case REALLOCATION:
			planReallocation(dpi);
			break;
		
		case STRIKE:
			planStrike();
			break;			
			
		case CHARGE:
			planCharge();
			break;	
		
		case OVERLOAD:
			planOverload();
			break;
		}
		lastPlanIssuedTime = System.currentTimeMillis();
	}
	
	private long lastTimeSpinning = 0;
	
	/**
	 * Create the PathSteps for an OperationReallocation
	 * 
	 * This method returns void but it should populate the pathStepList with pathSteps
	 */
	private void planReallocation(DynamicInfo dpi) {
		OperationReallocation op = (OperationReallocation) currentOperation;
		
		pathStepList = new LinkedList<PathStep>();
		double angleCorrect = 0.0;
		if (
				Geometry.isPointBehind(
						Geometry.translate(
								dpi.getAlfieInfo().getPosition(),
								dpi.getAlfieInfo().getLength() - dpi.getAlfieInfo().getCentrePoint().getY(),
								dpi.getAlfieInfo().getFacingDirection()
						), 
						dpi.getAlfieInfo().getFacingDirection(), 
						op.getPosition()
				)
		) {
			long SPINNING_TIMEOUT = 1000; // Stan and Paul know why.
			
			long now = System.currentTimeMillis();
			if (lastTimeSpinning == 0 || now - lastTimeSpinning > SPINNING_TIMEOUT) {
				lastTimeSpinning = now;
				Point2D p1 = dpi.getAlfieInfo().getPosition(); 
				Point2D p2 = dpi.getBallInfo().getPosition();
				double targetAngle = Geometry.getVectorDirection(p1, p2);
				angleCorrect = targetAngle - dpi.getAlfieInfo().getFacingDirection();
				angleCorrect = Geometry.normalizeToPositive(angleCorrect);
				
				System.out.println("Angle correct: " + angleCorrect);
				
				pathStepList.add(
						new PathStepSpinLeft(
								dpi.getAlfieInfo().getFacingDirection(),
								angleCorrect,
								10.0,
								1000.0
						)
				);
			}
		}
		
		LinkedList<PathStep> pathStepListSecondCW = getDoubleArcPath(dpi, op, false, angleCorrect);
		LinkedList<PathStep> pathStepListSecondCCW = getDoubleArcPath(dpi, op, true, angleCorrect);
		
//		if (!isGoodPath(pathStepListSecondCCW, (StaticRobotInfo)dpi.getAlfieInfo())) {
//			if (!isGoodPath(pathStepListSecondCW, (StaticRobotInfo)dpi.getAlfieInfo())) {
//				pathStepList.add(
//						new PathStepArcBackwardsLeft(
//								dpi.getAlfieInfo().getPosition(), 
//								dpi.getAlfieInfo().getFacingDirection(), 
//								10.0, 
//								180, 
//								5.0
//						)
//				);
//			} else {
//				pathStepList = pathStepListSecondCW;
//			}
//		} else {
//			if (!isGoodPath(pathStepListSecondCW, (StaticRobotInfo)dpi.getAlfieInfo())) {
//				pathStepList = pathStepListSecondCCW;
//			} else {
				double lengthCCW = 0.0;
				for (PathStep ps : pathStepListSecondCCW) {
					lengthCCW += ((PathStepArc) ps).getLength();
				}
				
				double lengthCW = 0.0;
				for (PathStep ps : pathStepListSecondCW) {
					lengthCW += ((PathStepArc) ps).getLength();
				}
				System.out.println("CCW arc length: " + lengthCCW);
				System.out.println("CW arc length: " + lengthCW);
				
				pathStepList.addAll(
					lengthCCW < lengthCW
					? pathStepListSecondCCW
					: pathStepListSecondCW
				);
//			}
//		}
		
		
		pathStepList.add(new PathStepKick(1000));
	}

	public static LinkedList<PathStep> getDoubleArcPath(DynamicInfo dpi,
			OperationReallocation op, boolean secondArcCCW, double angleCorrect) {
		LinkedList<PathStep> pathStepList = new LinkedList<PathStep>();
		double orientedRadius;
		if (secondArcCCW)
			orientedRadius = HARDCODED_SECOND_RADIUS_REMOVEME;
		else 
			orientedRadius = -HARDCODED_SECOND_RADIUS_REMOVEME;
		double secondRadius = Math.abs(orientedRadius);
		
		double d2 = op.getOrientation();
		double d2t = Geometry.perpendicularisePaul(d2);
		
		Point2D startPosition = dpi.getAlfieInfo().getPosition();
		
		// Target position
		double x2 = op.getPosition().getX();
		double y2 = op.getPosition().getY();
		
		double x0 = x2 + orientedRadius * Math.cos(Math.toRadians(d2t));
		double y0 = y2 + orientedRadius * Math.sin(Math.toRadians(d2t));
		
		Point2D secondCircleCentre = new Point2D.Double(x0, y0);
	
		double startDirection = dpi.getAlfieInfo().getFacingDirection();
		startDirection += angleCorrect;
		startDirection = Geometry.normalizeToPositive(startDirection);
		
		double startDirectionPauled = Geometry.perpendicularisePaul(startDirection);
		double x3 = secondCircleCentre.getX() - orientedRadius * Math.cos(Math.toRadians(startDirectionPauled));  
		double y3 = secondCircleCentre.getY() - orientedRadius * Math.sin(Math.toRadians(startDirectionPauled));  
	
		Point2D p3 = new Point2D.Double(x3, y3);
		Pair<Point2D, Point2D> intersections = Geometry.getLineCircleIntersections(
				startPosition,
				p3, 
				secondCircleCentre,
				secondRadius
		);

		double dist1 = intersections.getFirst().distance(p3);
		double dist2 = intersections.getSecond().distance(p3);
		
		// Point of transition between the arcs:
		Point2D transitionPoint = dist1 > dist2 ? intersections.getFirst() : intersections.getSecond();
		
		Point2D temp = Geometry.generateRandomPoint(startPosition, startDirectionPauled);
		
		// Centre of the first arc:
		Point2D firstCircleCentre = Geometry.getLinesIntersection(secondCircleCentre, transitionPoint, startPosition, temp);
		// The radius of the first arc:
		//PathStep firstArc;
		double firstRadius = startPosition.distance(firstCircleCentre);

		double firstArcAngle = 
			Geometry.getArcOrientedAngle(
					startPosition,
					startDirection,
					transitionPoint,
					firstCircleCentre
			);
		
		PathStepArc firstArc;
		
//		if (Geometry.isPointBehind(startPosition, startDirection, op.getPosition())) {
//			firstArc = PathStepArc.getBackwardsPathStepArc(
//					startPosition, 
//					startDirection,
//					firstCircleCentre, 
//					firstArcAngle,
//					DISTANCE_THRESHOLD
////			);	
//		} else {
		firstArc = PathStepArc.getForwardPathStepArc(
				startPosition, 
				startDirection,
				firstCircleCentre, 
				firstArcAngle,
				DISTANCE_THRESHOLD
		);	
//		}
		
		
		
		double secondArcAngle = 
			Geometry.getArcOrientedAngle(
					transitionPoint,
					firstArc.getTargetOrientation(),
					op.getPosition(),
					secondCircleCentre
			);
		
		PathStepArc secondArc = PathStepArc.getForwardPathStepArc(
				firstArc.getTargetDestination(), 
				firstArc.getTargetOrientation(),
				secondCircleCentre, 
				secondArcAngle, 
				DISTANCE_THRESHOLD
		);
		
		pathStepList.add(firstArc);
		pathStepList.add(secondArc);

		if (verbose) {
			System.out.println();
			System.out.println("ARC THINGY:");
			System.out.println("startPosition      = " + startPosition);
			System.out.println("startDirection     = " + startDirection);
			System.out.println();
			System.out.println("transitionPoint    = " + transitionPoint);
			System.out.println("transitionDirection= " + firstArc.getTargetOrientation());
			System.out.println();
			System.out.println("targetPosition     = " + op.getPosition());
			System.out.println("targetDirection    = " + secondArc.getTargetOrientation());
			System.out.println();
			System.out.println("p3                 = " + p3);
			System.out.println();
			System.out.println("firstRadius        = " + firstRadius);
			System.out.println("firstCircleCentre  = " + firstCircleCentre);
			System.out.println("firstArcAngle      = " + firstArcAngle);
			System.out.println();
			System.out.println("secondRadius       = " + secondRadius);
			System.out.println("secondCircleCentre = " + secondCircleCentre);
			System.out.println("secondArcAngle     = " + secondArcAngle);

		}
		
		return pathStepList;
	}
	
	public boolean isGoodPath(LinkedList<PathStep> path, StaticRobotInfo robotInfo) {
		Rectangle2D r = GlobalInfo.getPitch().getMinimumEnclosingRectangle();
		Point2D.Double [] ps = {
				new Point2D.Double(
						r.getMaxX(),
						r.getMaxY()
				),
				new Point2D.Double(
						r.getMinX(),
						r.getMaxY()
				),
				new Point2D.Double(
						r.getMinX(),
						r.getMinY()
				),
				new Point2D.Double(
						r.getMaxX(),
						r.getMinY()
				)
		};
		
		for (PathStep arc : path) {
			for (int i = 0; i < 4; ++i) {
				if (!isGoodArc((PathStepArc) arc, ps[i], ps[(i + 1) % 4], robotInfo)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isGoodArc(PathStepArc arc, Point2D p1, Point2D p2, 
			StaticRobotInfo robotInfo) {
		boolean CCW = 
			arc instanceof PathStepArcForwardsLeft ||
			arc instanceof PathStepArcBackwardsRight
		;
		double w = StaticRobotInfo.getWidth();
		double l = StaticRobotInfo.getLength();
		Point2D c = robotInfo.getCentrePoint();
		
		Point2D o = arc.getCentrePoint();
		double r = o.distance(arc.getStartPoint());
		double safeDistance = 
			Line2D.Double.ptLineDist(
					p1.getX(), p1.getY(), 
					p2.getX(), p2.getY(), 
					o.getX(), o.getY()
			);
		
		
		double delta = Geometry.getVectorDirection(p1, p2);
		delta = Geometry.normalizeToPositive(delta);
		
		double k = 180 - delta + 90 * (CCW ? 1 : -1);
		
		double M = 0.0;
		
		double oriStart = arc.getStartDirection();
		double oriEnd = arc.getTargetOrientation();
		
		double phiStart;
		double phiEnd;
		
		if (CCW) {
			phiStart = oriStart - 90;
			phiEnd = oriEnd - 90;
		} else {
			phiStart = oriEnd + 90;
			phiEnd = oriStart + 90;
			
			phiStart = Geometry.normalizeToPositive(phiStart);
			phiEnd = Geometry.normalizeToPositive(phiEnd);
		}
		
		double alphaStart = phiStart + k;
		double alphaEnd = phiEnd + k;
		
		alphaStart = Geometry.normalizeToPositive(alphaStart);
		alphaEnd = Geometry.normalizeToPositive(alphaEnd);
			
		int[] dx = {1, -1, -1, 1};
		int[] dy = {-1, -1, 1, 1};
		
		System.out.println();
		System.out.println("Centre: " + o);
		System.out.println("Radius: " + r);
		System.out.println("PhiStart: " + phiStart);
		System.out.println("PhiEnd: " + phiEnd);
		System.out.println("P1: " + p1);
		System.out.println("P2: " + p2);
		
		for (int i = 0; i < 4; ++i) {
			double a = w / 2 + c.getX() * dx[i];
			double b = l / 2 + c.getY() * dy[i];
			  
			M = updateMax(r, M, phiStart, alphaStart, i, a, b, phiStart, phiEnd);
			M = updateMax(r, M, phiEnd, alphaEnd, i, a, b, phiStart, phiEnd);
			
			double phi = 
				Math.atan2(
						Math.cos(Math.toRadians(k)) * b - Math.sin(Math.toRadians(k)) * a + r,
						Math.cos(Math.toRadians(k)) * a + Math.sin(Math.toRadians(k)) * b
				);
			phi = Math.toDegrees(phi);
			phi = Geometry.normalizeToPositive(phi);
			System.out.println("phi " + i + ": " + phi);
			double alpha = phi + k;
			alpha = Geometry.normalizeToPositive(alpha);
			System.out.println("alpha " + i + ": " + alpha);

			System.out.println("M: " + M);
			M = updateMax(r, M, phi, alpha, i, a, b, phiStart, phiEnd);
		}
		System.out.println("Safe distance: " + safeDistance);
		return M < safeDistance;
	}

	/**
	 * A snippet of a bigger function. Makes no sense on its own.
	 */
	public double updateMax(double r, double M, double phi, double alpha,
			int i, double a, double b, double phiStart, double phiEnd) {

		boolean alphaValid = alpha > 90.0 * i && alpha < 90.0 * (i + 1);
		boolean phiValid = phi >= phiStart && phi <= phiEnd;
		if (alphaValid && phiValid) {
			double p = Math.sin(Math.toRadians(alpha - 90)) * r;
			System.out.println(">> Phi: " + phi);
			System.out.println(">> Alpha: " + alpha);
			double d = Math.sin(Math.toRadians(alpha)) * b + Math.cos(Math.toRadians(alpha)) * a;
			// Update maximum if necessary.
			if (p + d > M) {
				M = p + d;
			}
		}
		return M;
	}
	
	/**
	 * Create the PathSteps for an OperationStrike
	 * 
	 * This method returns void but it should populate the pathStepList with pathSteps
	 */
	private void planStrike() {
		pathStepList = new LinkedList<PathStep>();
		pathStepList.add(new PathStepKick(1000));
		System.out.println(">>> Striking!");
	}

	/**
	 * Create the PathSteps for an OperationCharge
	 * 
	 * This method returns void but it should populate the pathStepList with pathSteps
	 */
	private void planCharge() {
		/**
		 * TODO: Make magic happen
		 */	
	}
	
	/**
	 * Create the PathSteps for an OperationOverload
	 * 
	 * This method returns void but it should populate the pathStepList with pathSteps
	 */	
	private void planOverload() {
		/**
		 * TODO: Make magic happen
		 */
	}

	/**
	 * Creates a candy packet based on the current operation and send it to MouthInstance thats been
	 * passed in
	 */
	private void execute() {
		currentStep.whisper(mouth);
	}

	
	@Override
	public void consumeOperation(Operation operation) {
		setOperation(operation);
	}

	@Override
	public void start() {
//		plannedBefore = false;
	}

	@Override
	public void stop() {
//		plannedBefore = false;
	}
}
