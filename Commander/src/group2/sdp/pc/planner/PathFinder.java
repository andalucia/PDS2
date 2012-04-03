package group2.sdp.pc.planner;

import group2.sdp.common.util.Geometry;
import group2.sdp.common.util.Pair;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.breadbin.StaticRobotInfo;
import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.mouth.MouthInterface;
import group2.sdp.pc.planner.operation.Operation;
import group2.sdp.pc.planner.operation.OperationPenaltyDefend;
import group2.sdp.pc.planner.operation.OperationPenaltyTake;
import group2.sdp.pc.planner.operation.OperationReallocation;
import group2.sdp.pc.planner.pathstep.PathStep;
import group2.sdp.pc.planner.pathstep.PathStepArc;
import group2.sdp.pc.planner.pathstep.PathStepArcBackwardsLeft;
import group2.sdp.pc.planner.pathstep.PathStepArcBackwardsRight;
import group2.sdp.pc.planner.pathstep.PathStepArcForwardsLeft;
import group2.sdp.pc.planner.pathstep.PathStepArcForwardsRight;
import group2.sdp.pc.planner.pathstep.PathStepKick;
import group2.sdp.pc.planner.pathstep.PathStepSpinLeft;
import group2.sdp.pc.planner.pathstep.PathStepSpinRight;
import group2.sdp.pc.planner.pathstep.PathStepStop;
import group2.sdp.pc.planner.skeleton.OperationConsumer;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

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

	private static final boolean VERBOSE = true;

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
				if (VERBOSE) {
					System.out.println("Step succeeded :D");
				}
				executeNextStep(dpi);
			} else if (now - lastPlanIssuedTime > WARMUP_TIMEOUT &&
					currentStep.hasFailed(dpi)) {
				if (VERBOSE) {
					System.out.println("Step failed D:");
				}
				plan(dpi);
			}
		}
	}
	
	public void executeNextStep(DynamicInfo dpi) {	
		// Get the next PathStep from the queue
		do {
			currentStep = pathStepList.pollFirst();
		} while (currentStep != null && currentStep.isSuccessful(dpi));
		// If it is successful, try and execute the next PathStep in the queue
		if (currentStep != null) {
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
		
		if (VERBOSE)
			System.out.println("Looking for a path...");
		// Clear the PathStep queue
		pathStepList.clear();
		
		// Plan based on the current operation type
		if (currentOperation != null) {
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
				
			case PENALTY_DEFEND:
				planPenaltyDefend(dpi);
				break;
				// TODO: tell Paul to add the class 
			case PENALTY_TAKE:
				planPenaltyTake(dpi.getAlfieInfo().getFacingDirection());
				break;
			}
			lastPlanIssuedTime = System.currentTimeMillis();
		}
	}
	

	private long lastTimeSpinning = 0;
	

	public LinkedList<PathStep> getDoubleArcPath(Point2D startPosition, 
			double startDirection, Point2D endPosition, double endDirection,
			boolean secondArcCCW, double angleCorrect) {
		// TODO: remove angle correct
		LinkedList<PathStep> pathStepList = new LinkedList<PathStep>();
		double orientedRadius;
		if (secondArcCCW)
			orientedRadius = HARDCODED_SECOND_RADIUS_REMOVEME;
		else 
			orientedRadius = -HARDCODED_SECOND_RADIUS_REMOVEME;
		double secondRadius = Math.abs(orientedRadius);
		
		double d2t = Geometry.perpendicularisePaul(endDirection);
		
		// Target position
		double x2 = endPosition.getX();
		double y2 = endPosition.getY();
		
		// TODO: use translate
		double x0 = x2 + orientedRadius * Math.cos(Math.toRadians(d2t));
		double y0 = y2 + orientedRadius * Math.sin(Math.toRadians(d2t));
		
		Point2D secondCircleCentre = new Point2D.Double(x0, y0);
	
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
					firstArc.getEndDirection(),
					endPosition,
					secondCircleCentre
			);
		
		PathStepArc secondArc = PathStepArc.getForwardPathStepArc(
				firstArc.getTargetDestination(), 
				firstArc.getEndDirection(),
				secondCircleCentre, 
				secondArcAngle, 
				DISTANCE_THRESHOLD
		);
		
		pathStepList.add(firstArc);
		pathStepList.add(secondArc);

		if (VERBOSE) {
			System.out.println();
			System.out.println("ARC THINGY:");
			System.out.println("startPosition      = " + startPosition);
			System.out.println("startDirection     = " + startDirection);
			System.out.println();
			System.out.println("transitionPoint    = " + transitionPoint);
			System.out.println("transitionDirection= " + firstArc.getEndDirection());
			System.out.println();
			System.out.println("endPosition     = " + endPosition);
			System.out.println("endDirection    = " + secondArc.getEndDirection());
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

private void planPretzelExclamationMark(DynamicInfo dpi) {
		pathStepList.clear();
		pathStepList.add(new PathStepArcBackwardsLeft(
				dpi.getAlfieInfo().getPosition(), 
				dpi.getAlfieInfo().getFacingDirection(), 
				30.0, 
				45.0, 
				5.0
		));
		pathStepList.add(new PathStepArcBackwardsLeft(
				dpi.getAlfieInfo().getPosition(), 
				dpi.getAlfieInfo().getFacingDirection(), 
				10.0, 
				180.0, 
				5.0
		));
		pathStepList.add(new PathStepArcBackwardsLeft(
				dpi.getAlfieInfo().getPosition(), 
				dpi.getAlfieInfo().getFacingDirection(), 
				24.0, 
				90.0, 
				5.0
		));
		pathStepList.add(new PathStepArcBackwardsLeft(
				dpi.getAlfieInfo().getPosition(), 
				dpi.getAlfieInfo().getFacingDirection(), 
				10.0, 
				180.0, 
				5.0
		));
		pathStepList.add(new PathStepArcBackwardsLeft(
				dpi.getAlfieInfo().getPosition(), 
				dpi.getAlfieInfo().getFacingDirection(), 
				30.0, 
				45.0, 
				5.0
		));
	}
	
	public boolean isGoodPath(LinkedList<PathStep> path, DynamicInfo dpi) {
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
				if (!isGoodArc((PathStepArc) arc, ps[i], ps[(i + 1) % 4], dpi.getAlfieInfo())) {
					return false;
				}
			}
			if (robotInTheWay(dpi.getAlfieInfo(), dpi.getOpponentInfo(), (PathStepArc) arc)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if the robot moving on the arc will cross the line passing through the 
	 * points. 
	 */
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
		double oriEnd = arc.getEndDirection();
		
		// Counter-clock-wise start and stop angles of the arc.
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
		
		if (VERBOSE) {
			System.out.println();
			System.out.println("Centre: " + o);
			System.out.println("Radius: " + r);
			System.out.println("PhiStart: " + phiStart);
			System.out.println("PhiEnd: " + phiEnd);
			System.out.println("P1: " + p1);
			System.out.println("P2: " + p2);
		}
		
		int[] dx;
		int[] dy;
		
		if (CCW) {
			dx = new int [] {1, -1, -1, 1};
			dy = new int [] {1, 1, -1, -1};
		} else {
			dx = new int [] {-1, 1, 1, -1};
			dy = new int [] {1, 1, -1, -1};
		}
		
		for (int i = 0; i < 4; ++i) {
			double a = - (w / 2 * dx[i] + c.getX());
			double b =    l / 2 * dy[i] - c.getY();
			  
			M = updateMax(r, M, alphaStart, alphaStart, alphaEnd, i, a, b, CCW);
			M = updateMax(r, M, alphaEnd, alphaStart, alphaEnd, i, a, b, CCW);
			double alpha; 
			if (CCW) { 
				alpha = Math.toDegrees(Math.atan2(b, -(a + r)));
				if (VERBOSE)
					System.out.println("0 == " + 
							(Math.cos(Math.toRadians(alpha)) * b + 
							 Math.sin(Math.toRadians(alpha)) * (a + r)));
			} else { 
				alpha = Math.toDegrees(Math.atan2(b, a + r));
				if (VERBOSE)
					System.out.println("0 == " + 
							(Math.cos(Math.toRadians(alpha)) * b - 
							 Math.sin(Math.toRadians(alpha)) * (a + r)));
			}
			
			alpha = Geometry.normalizeToPositive(alpha);

			M = updateMax(r, M, alpha, alphaStart, alphaEnd, i, a, b, CCW);
			if (VERBOSE) {
				System.out.println("a: " + a);
				System.out.println("b: " + b);
				System.out.println("M: " + M);
			}			
		}
		if (VERBOSE)
			System.out.println("Safe distance: " + safeDistance);
		return M < safeDistance;
	}
	
	private boolean robotInTheWay(StaticRobotInfo alfieInfo, StaticRobotInfo opponentInfo,
			PathStepArc arc) {
		boolean CCW = 
			arc instanceof PathStepArcForwardsLeft ||
			arc instanceof PathStepArcBackwardsRight
		;
		
		double oriStart = arc.getStartDirection();
		double oriEnd = arc.getEndDirection();
		
		// Counter-clock-wise start and stop angles of the arc.
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
		
		Point2D arcCentre = arc.getCentrePoint();
		Point2D opponentCentre = opponentInfo.getPosition();
		
		double centreToOpponentAngle = Geometry.getVectorDirection(
				arcCentre, opponentCentre);
		
		boolean angleInBounds = Geometry.angleWithinBounds(
				centreToOpponentAngle, 
				phiStart, 
				phiEnd
		);
		
		double opRadius = arcCentre.distance(opponentCentre);
		// times two - for two robots
		boolean inRange = Math.abs(opRadius - arc.getRadius()) < alfieInfo.getSafeDistance() * 2;
		return inRange && angleInBounds;
	}

	/**
	 * A snippet of a bigger function. Makes no sense on its own.
	 */
	public double updateMax(double r, double M, double alpha, double alphaMin, double alphaMax,
			int i, double a, double b, boolean CCW) {

		boolean alphaValid = 
			Geometry.angleWithinBounds(alpha, 90.0 * i, 90.0 * (i + 1)) && 
			Geometry.angleWithinBounds(alpha, alphaMin, alphaMax) && 
			true;
		if (VERBOSE) {
			System.out.println();
			System.out.println(i);
			
			System.out.println("alpha: " + alpha);
			System.out.println("alpha valid: " + alphaValid);
		}
		if (alphaValid) {
			double p;
			double d;
			if (CCW) {
				p = -Math.cos(Math.toRadians(alpha)) * r;
				d = 
					Math.sin(Math.toRadians(alpha)) * b - 
					Math.cos(Math.toRadians(alpha)) * a;
			} else {
				p = Math.cos(Math.toRadians(alpha)) * r;
				d = 
					Math.sin(Math.toRadians(alpha)) * b + 
					Math.cos(Math.toRadians(alpha)) * a;
			}
			if (VERBOSE) {
				System.out.println("then p: " + p);
				System.out.println("then d: " + d);
			}
			// Update maximum if necessary.
			if (p + d > M) {
				M = p + d;
			}
		}
		return M;
	}
	
	/**
	 * Returns the arc necessary to move from Alfie's current sector to 
	 * the sector the opponent is facing.
	 * @param alfieSector != opSector
	 * @return
	 */
	public PathStepArc getPenaltyArc(int alfieSector, int opSector,
			Point2D start, double startDirection) {
		double radius = 60; //TODO
		double angle = 10; //TODO
		if (alfieSector < opSector) {
			//backwards arc
			if (GlobalInfo.isAttackingRight()) {
				return new PathStepArcBackwardsRight(start, startDirection, radius, angle, DISTANCE_THRESHOLD);
			} else {
				return new PathStepArcBackwardsLeft(start, startDirection, radius, angle, DISTANCE_THRESHOLD);
			}
		} else {
			//forwards arc
			if (GlobalInfo.isAttackingRight()) {
				return new PathStepArcForwardsRight(start, startDirection, radius, angle, DISTANCE_THRESHOLD);
			} else {
				return new PathStepArcForwardsLeft(start, startDirection, radius, angle, DISTANCE_THRESHOLD);
			}
		}
	}

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
								StaticRobotInfo.getLength() - dpi.getAlfieInfo().getCentrePoint().getY(),
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
				
				if (VERBOSE)
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
		
		LinkedList<PathStep> pathStepListSecondCW = 
			getDoubleArcPath(
					dpi.getAlfieInfo().getPosition(),
					dpi.getAlfieInfo().getFacingDirection(),
					op.getPosition(),
					op.getOrientation(),
					false,
					angleCorrect
			);
		LinkedList<PathStep> pathStepListSecondCCW = 
			getDoubleArcPath(
					dpi.getAlfieInfo().getPosition(),
					dpi.getAlfieInfo().getFacingDirection(),
					op.getPosition(),
					op.getOrientation(),
					true,
					angleCorrect
			);
		
		if (!isGoodPath(pathStepListSecondCCW, dpi)) {
			if (!isGoodPath(pathStepListSecondCW, dpi)) {
				pathStepList.add(
						new PathStepArcBackwardsLeft(
								dpi.getAlfieInfo().getPosition(), 
								dpi.getAlfieInfo().getFacingDirection(), 
								10.0, 
								180, 
								5.0
						)
				);
			} else {
				pathStepList = pathStepListSecondCW;
			}
		} else {
			if (!isGoodPath(pathStepListSecondCW, dpi)) {
				pathStepList = pathStepListSecondCCW;
			} else {
				double lengthCCW = 0.0;
				for (PathStep ps : pathStepListSecondCCW) {
					lengthCCW += ((PathStepArc) ps).getLength();
				}
				
				double lengthCW = 0.0;
				for (PathStep ps : pathStepListSecondCW) {
					lengthCW += ((PathStepArc) ps).getLength();
				}
				if (VERBOSE) {
					System.out.println("CCW arc length: " + lengthCCW);
					System.out.println("CW arc length: " + lengthCW);
				}
				
				pathStepList.addAll(
					lengthCCW < lengthCW
					? pathStepListSecondCCW
					: pathStepListSecondCW
				);
			}
		}
		pathStepList.add(new PathStepKick(1000));
	}
	
	/**
	 * Create the PathSteps for an OperationStrike
	 * 
	 * This method returns void but it should populate the pathStepList with pathSteps
	 */
	private void planStrike() {
		pathStepList = new LinkedList<PathStep>();
		pathStepList.add(new PathStepKick(1000));
		if (VERBOSE)
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
	 * Decide what to do when defending a penalty. This looks at the direction 
	 * the opponent robot is facing and our current position and then tries 
	 * to block the ball. Also uses the speed and direction the opponent is turning 
	 * to try plan ahead.
	 * @param dpi
	 */
	private void planPenaltyDefend(DynamicInfo dpi) {
		OperationPenaltyDefend op = (OperationPenaltyDefend) currentOperation;
		
		DynamicRobotInfo opponentInfo = dpi.getOpponentInfo();
		DynamicRobotInfo alfieInfo = dpi.getAlfieInfo();
		
		int opponentFacingSector = op.getOpponentFacingSector(opponentInfo.getFacingDirection());
		int currentSector = op.getCurrentSector(alfieInfo.getPosition());
		
		int desiredSector = -1;
		
		switch (opponentFacingSector) {
		case 1: 
			if (op.isAngleIncreasing(opponentInfo.isRotatingCounterClockWise(), 
					opponentInfo.getRotatingSpeed())) {
				// move to 2
				desiredSector = 2;
			} else {
				// move to 1
				desiredSector = 1;
			}
			break;
		case 2:
			if (op.isAngleIncreasing(opponentInfo.isRotatingCounterClockWise(), 
					opponentInfo.getRotatingSpeed())) {
				// move to 3
				desiredSector = 3;
			} else if (op.isAngleDecreasing(opponentInfo.isRotatingCounterClockWise(), 
					opponentInfo.getRotatingSpeed())) {
				// move to 1
				desiredSector = 1;
			} else {
				// move to 2
				desiredSector = 2;
			}
			break;
		case 3:
			if (op.isAngleDecreasing(opponentInfo.isRotatingCounterClockWise(), 
					opponentInfo.getRotatingSpeed())) {
				// move to 2
				desiredSector = 2;
			} else {
				// move to 3
				desiredSector = 3;
			}
			break;
		}

		if (currentSector != desiredSector) {
			PathStepArc penaltyArc = getPenaltyArc(
					currentSector, 
					desiredSector, 
					alfieInfo.getPosition(), 
					alfieInfo.getFacingDirection()
					);
			pathStepList.add(penaltyArc);
		}
		pathStepList.add(new PathStepStop());
	}
	
	private void planPenaltyTake(double alfieFacingAngle) {
		//TODO check threshold values of spins
		PathStep turn;
		OperationPenaltyTake op = (OperationPenaltyTake) currentOperation;
		double opponentRobotPositionY = op.getOpponentRobotPosition().getY();
		if (GlobalInfo.isAttackingRight()) {
			if (opponentRobotPositionY > 0) {
				//turn left
				turn = new PathStepSpinLeft(alfieFacingAngle, 20, 10, 50);
			} else {
				//turn right
				turn = new PathStepSpinRight(alfieFacingAngle, 20, 10, 50);

			}
		} else {
			if (opponentRobotPositionY > 0) {
				//turn right
				turn = new PathStepSpinRight(alfieFacingAngle, 20, 10, 50);
			} else {
				//turn left
				turn = new PathStepSpinLeft(alfieFacingAngle, 20, 10, 50);
			}
		}
		pathStepList = new LinkedList<PathStep>();
		pathStepList.add(turn);
		pathStepList.add(new PathStepKick(1024));
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
