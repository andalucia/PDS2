package group2.sdp.pc.planner;

import group2.sdp.common.util.Geometry;
import group2.sdp.common.util.Pair;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.controlstation.ControlStation;
import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.mouth.MouthInterface;
import group2.sdp.pc.planner.operation.Operation;
import group2.sdp.pc.planner.operation.OperationReallocation;
import group2.sdp.pc.planner.pathstep.PathStep;
import group2.sdp.pc.planner.pathstep.PathStepArc;
import group2.sdp.pc.planner.pathstep.PathStepKick;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

import java.awt.geom.Point2D;
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

	private static final double DISTANCE_THRESHOLD = 20.0;
	
	/**
	 * This is a queue which stores a list of paths 
	 */
	private LinkedList<PathStep> pathStepList = new LinkedList<PathStep>();
	private PathStep currentStep = null;
	private Operation currentOperation;
	
	@SuppressWarnings("unused")
	private GlobalInfo globalInfo;
	private MouthInterface mouth;
	
	private boolean replan;
	
	/**
	 * Create the path finder object and setup the GlobalInfo and Mouth
	 * 
	 * @param Persistant global pitch info
	 * @param the mouth used for communicating with Alfie or the simulator
	 */
	public PathFinder(GlobalInfo globalInfo, MouthInterface mouth) {
		this.globalInfo = globalInfo;
		this.mouth = mouth;
	}
	
	private boolean plannedBefore;
	
	@Override
	public void consumeInfo(DynamicInfo dpi) {
		if (replan || currentStep == null) {
			if (!plannedBefore) {
				plan(dpi);
				executeNextStep(dpi);
				replan = false;
				plannedBefore = true;
			}
		} else {
			if (currentStep.isSuccessful(dpi)) {
				if (verbose) {
					System.out.println("Step succeeded :D");
				}
				executeNextStep(dpi);
			} else if (currentStep.hasFailed(dpi)) {
				if (verbose) {
					System.out.println("Step failed D:");
				}
				plan(dpi);
			}
		}
	}
	
	public void executeNextStep(DynamicInfo dpi) {	
		// Get the next PathStep from the queue
		System.out.println(">>>>>>>>>");
		System.out.println(pathStepList);
		
		currentStep = pathStepList.pollFirst();
		
		System.out.println(currentStep);
		System.out.println(pathStepList);
		System.out.println("<<<<<<<<");
		// If it is successful, try and execute the next PathStep in the queue
		if(currentStep != null) {
			execute();
		} else {
			// The queue is empty so re-plan
			// plan(dpi);
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
	
	/**
	 * This method works out all the logic of the pathFinder and adds each step the queue which will
	 * them be executed in turn.
	 * 
	 * Every time this method is called the pathStepList should be cleared and re-planning should take
	 * place from scratch
	 * @param dpi 
	 */

	private void plan(DynamicInfo dpi) {
		
		ControlStation.log("Looking for a path...");
		
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
	}
	
	/**
	 * Create the PathSteps for an OperationReallocation
	 * 
	 * This method returns void but it should populate the pathStepList with pathSteps
	 */
	private void planReallocation(DynamicInfo dpi) {
		OperationReallocation op = (OperationReallocation) currentOperation;
		
		LinkedList<PathStep> pathStepListSecondCW = getDoubleArcPath(dpi, op, false);
		LinkedList<PathStep> pathStepListSecondCCW = getDoubleArcPath(dpi, op, true);
		
		double lengthCCW = 0.0;
		for (PathStep ps : pathStepListSecondCCW) {
			double r = ((PathStepArc) ps).getRadius();
			double ang = ((PathStepArc) ps).getAngle();
			
			lengthCCW += Math.abs(r * Math.toRadians(ang));
		}
		
		double lengthCW = 0.0;
		for (PathStep ps : pathStepListSecondCW) {
			double r = ((PathStepArc) ps).getRadius();
			double ang = ((PathStepArc) ps).getAngle();
			
			lengthCW += Math.abs(r * Math.toRadians(ang));
		}
		System.out.println("CCW arc length: " + lengthCCW);
		System.out.println("CW arc length: " + lengthCW);
		
		this.pathStepList =
			lengthCCW < lengthCW
			? pathStepListSecondCCW
			: pathStepListSecondCW;
		this.pathStepList.add(new PathStepKick(1000));
		this.pathStepList.add(new PathStepKick(1000));
		this.pathStepList.add(new PathStepKick(1000));
	}

	public static LinkedList<PathStep> getDoubleArcPath(DynamicInfo dpi,
			OperationReallocation op, boolean secondArcCCW) {
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
		
		double x2 = op.getPosition().getX();
		double y2 = op.getPosition().getY();
		
		double x0 = x2 + orientedRadius * Math.cos(Math.toRadians(d2t));
		double y0 = y2 + orientedRadius * Math.sin(Math.toRadians(d2t));
		
		Point2D secondCircleCentre = new Point2D.Double(x0, y0);
	
		double startDirection = dpi.getAlfieInfo().getFacingDirection();
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
			
		
		// Centre of the first arc:#
		Point2D firstCircleCentre = Geometry.getLinesIntersection(secondCircleCentre, transitionPoint, startPosition, temp);
		// The radius of the first arc:
		//PathStep firstArc;
		double firstRadius = startPosition.distance(firstCircleCentre);

		double firstArcAngle = 
			Geometry.getArcOrientedAngle(
					startPosition,
					startDirection,
					transitionPoint,
					firstCircleCentre,
					firstRadius
			);
		
		PathStepArc firstArc = PathStepArc.getShorterPathStepArc(
				startPosition, 
				startDirection,
				firstCircleCentre, 
				firstArcAngle,
				DISTANCE_THRESHOLD
		);
		
		double secondAngle = 
			Geometry.getArcOrientedAngle(
					transitionPoint,
					firstArc.getTargetOrientation(),
					op.getPosition(),
					secondCircleCentre,
					secondRadius
			);
		
		PathStepArc secondArc = PathStepArc.getForwardPathStepArc(
				firstArc.getTargetDestination(), 
				firstArc.getTargetOrientation(),
				secondCircleCentre, 
				secondAngle, 
				DISTANCE_THRESHOLD
		);
		
		pathStepList.add(firstArc);
		pathStepList.add(secondArc);

		if (verbose) {
			System.out.println();
			System.out.println("ARC THINGY:");
			System.out.println("startPosition      = " + startPosition);
			System.out.println("startDirection     = " + startDirection);
			System.out.println("targetPosition     = " + op.getPosition());
			System.out.println("startDirection     = " + startDirection);
			System.out.println("secondCircleCentre = " + secondCircleCentre);
			System.out.println("p3                 = " + p3);
			System.out.println("secondRadius       = " + secondRadius);
			System.out.println("transitionOri      = " + firstArc.getTargetOrientation());
			System.out.println();
			System.out.println("transitionPoint    = " + transitionPoint);
			System.out.println("firstCircleCentre  = " + firstCircleCentre);
			System.out.println("firstRadius        = " + firstRadius);
			System.out.println("firstArcAngle      = " + firstArcAngle);
			System.out.println("target_ori         = " + secondArc.getTargetOrientation());
		}
		
		return pathStepList;
	}
	
	/**
	 * Create the PathSteps for an OperationStrike
	 * 
	 * This method returns void but it should populate the pathStepList with pathSteps
	 */
	private void planStrike() {
		/**
		 * TODO: Make magic happen
		 */	
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
		plannedBefore = false;
	}

	@Override
	public void stop() {
		plannedBefore = false;
	}
}
