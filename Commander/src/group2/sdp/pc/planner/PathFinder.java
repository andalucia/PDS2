package group2.sdp.pc.planner;

import group2.sdp.common.util.Geometry;
import group2.sdp.common.util.Pair;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.mouth.MouthInterface;
import group2.sdp.pc.planner.operation.Operation;
import group2.sdp.pc.planner.operation.OperationReallocation;
import group2.sdp.pc.planner.pathstep.PathStep;
import group2.sdp.pc.planner.pathstep.PathStepArc;
import group2.sdp.pc.planner.pathstep.PathStepArcBackwardsLeft;
import group2.sdp.pc.planner.pathstep.PathStepArcBackwardsRight;
import group2.sdp.pc.planner.pathstep.PathStepArcForwardsLeft;
import group2.sdp.pc.planner.pathstep.PathStepArcForwardsRight;
import group2.sdp.pc.planner.pathstep.PathStepGoBackwards;
import group2.sdp.pc.planner.pathstep.PathStepGoForwards;
import group2.sdp.pc.planner.pathstep.PathStepKick;
import group2.sdp.pc.planner.pathstep.PathStepSpinLeft;
import group2.sdp.pc.planner.pathstep.PathStepSpinRight;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.LinkedList;

/**
 * The PathFinder decides how to execute a particular operation.
 * 
 * It does this by checking the operation to be executed and creating a queue of pathStepList. It then
 * proceeds to execute them in order and checking whether they are success or have failed in every
 * consumeInfo() cycle
 */
public class PathFinder implements DynamicInfoConsumer, OperationConsumer{
	
	private static final boolean verbose = true;

	private static final double DISTANCE_THRESHOLD = 10.0;
	
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
	
	@Override
	public void consumeInfo(DynamicInfo dpi) {
		if (replan || currentStep == null) {
			plan(dpi);
			executeNextStep(dpi);
			replan = false;
		} else {
			if (currentStep.isSuccessful(dpi)) {
				if (verbose) {
					//System.out.println("Successful");
				}
				executeNextStep(dpi);
			} else if (currentStep.hasFailed(dpi)) {
				if (verbose) {
					//System.out.println("Fail");
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
	
	/**
	 * This method works out all the logic of the pathFinder and adds each step the queue which will
	 * them be executed in turn.
	 * 
	 * Every time this method is called the pathStepList should be cleared and re-planning should take
	 * place from scratch
	 * @param dpi 
	 */

	private void plan(DynamicInfo dpi) {
		
		//System.out.println("Looking for a path...");
		
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
		
		//TODO	decide on which curve to add
		this.pathStepList = pathStepListSecondCCW;
	}

	public static LinkedList<PathStep> getDoubleArcPath(DynamicInfo dpi,
			OperationReallocation op, boolean secondArcCCW) {
		LinkedList<PathStep> pathStepList = new LinkedList<PathStep>();
		double orientedRadius;
		if (secondArcCCW)
			orientedRadius = 10;
		else 
			orientedRadius = -10;
		
		double d2 = op.getOrientation();
		double d2t = Geometry.perpendicularisePaul(d2);
		
		double x1 = dpi.getAlfieInfo().getPosition().getX();
		double y1 = dpi.getAlfieInfo().getPosition().getY();
		
		double x2 = op.getPosition().getX();
		double y2 = op.getPosition().getY();
		
		double x0 = x2 + orientedRadius * Math.cos(Math.toRadians(d2t));
		double y0 = y2 + orientedRadius * Math.sin(Math.toRadians(d2t));
	
		Point2D p1 = new Point2D.Double(x1, y1);
		Point2D p0 = new Point2D.Double(x0, y0);
	
		////System.out.println("p2 = " + x2 + ", " + y2);
		//System.out.println("p0 = " + p0.getX() + ", " + p0.getY());
		
		double d1 = dpi.getAlfieInfo().getFacingDirection();
		double d1t = Geometry.perpendicularisePaul(d1);
		double x3 = p0.getX() - orientedRadius * Math.cos(Math.toRadians(d1t));  
		double y3 = p0.getY() - orientedRadius * Math.sin(Math.toRadians(d1t));  
	
		Point2D p3 = new Point2D.Double(x3, y3);
		Pair<Point2D, Point2D> intersections = Geometry.getLineCircleIntersections(
				p1,
				p3, 
				p0,
				orientedRadius
		);

		double dist1 = intersections.getFirst().distance(p3);
		double dist2 = intersections.getSecond().distance(p3);
		
		// Point of transition between the arcs:
		Point2D transitionPoint = dist1 > dist2 ? intersections.getFirst() : intersections.getSecond();
		
		Point2D temp = Geometry.generateRandomPoint(p1, d1t);
			
		
		// Centre of the first arc:#
		Point2D firstCircleCentre = Geometry.getLinesIntersection(p0, transitionPoint, p1, temp);
		
		// The radius of the first arc:
		//PathStep firstArc;
		double firstRadius = p1.distance(firstCircleCentre);
		double firstAngle = 
			Geometry.getArcOrientedAngle(
					dpi.getAlfieInfo().getPosition(),
					dpi.getAlfieInfo().getFacingDirection(),
					transitionPoint,
					firstCircleCentre,
					firstRadius
			);
		
		PathStepArc firstArc = PathStepArc.getShorterPathStepArc(
				dpi.getAlfieInfo().getPosition(), 
				dpi.getAlfieInfo().getFacingDirection(),
				firstCircleCentre, 
				firstAngle,
				DISTANCE_THRESHOLD
		);
		
		double secondRadius = Math.abs(orientedRadius);
		double secondAngle = 
			Geometry.getArcOrientedAngle(
					transitionPoint,
					firstArc.getTargetOrientation(),
					op.getPosition(),
					p0,
					secondRadius
			);
		
		PathStepArc secondArc = PathStepArc.getForwardPathStepArc(
				firstArc.getTargetDestination(), 
				firstArc.getTargetOrientation(),
				p0, 
				secondAngle, 
				DISTANCE_THRESHOLD
		);
		
		pathStepList.add(firstArc);
		pathStepList.add(secondArc);

		if (verbose) {
			System.out.println("p0 = " + p0);
			System.out.println("p3 = " + p3);
			System.out.println("radius1 = " + firstRadius);
			System.out.println("target_ori = " + firstArc.getTargetOrientation());
			System.out.println();
			System.out.println("p5 = " + transitionPoint);
			System.out.println("p7 = " + firstCircleCentre);
			System.out.println("radius2 = " + secondRadius);
			System.out.println("target_ori = " + secondArc.getTargetOrientation());
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
		
		switch(currentStep.getType()) {
	
		case GO_FORWARDS:
			PathStepGoForwards goForwards = (PathStepGoForwards) currentStep;
			mouth.sendGoForward(goForwards.getSpeed(), goForwards.getDistance());
			break;
			
		case GO_BACKWARDS:
			PathStepGoBackwards goBackwards = (PathStepGoBackwards) currentStep;
			mouth.sendGoBackwards(goBackwards.getSpeed(), goBackwards.getDistance());
			break;
			
		case SPIN_LEFT:
			PathStepSpinLeft spinLeft = (PathStepSpinLeft) currentStep;
			mouth.sendSpinLeft(spinLeft.getSpeed(), spinLeft.getAngle());
			break;
			
		case SPIN_RIGHT:
			PathStepSpinRight spinRight = (PathStepSpinRight) currentStep;
			mouth.sendSpinRight(spinRight.getSpeed(), spinRight.getAngle());
			break;
			
		case ARC_FORWARDS_LEFT:
			PathStepArcForwardsLeft arcForwardsLeft = (PathStepArcForwardsLeft) currentStep;
			mouth.sendForwardArcLeft(arcForwardsLeft.getRadius(), arcForwardsLeft.getAngle());
			break;
			
		case ARC_FORWARDS_RIGHT:
			PathStepArcForwardsRight arcForwardsRight = (PathStepArcForwardsRight) currentStep;
			mouth.sendForwardArcRight(arcForwardsRight.getRadius(), arcForwardsRight.getAngle());
			break;
			
		case ARC_BACKWARDS_LEFT:
			PathStepArcBackwardsLeft arcBackwardsLeft = (PathStepArcBackwardsLeft) currentStep;
			mouth.sendBackwardsArcLeft(arcBackwardsLeft.getRadius(), arcBackwardsLeft.getAngle());
			break;
			
		case ARC_BACKWARDS_RIGHT:
			PathStepArcBackwardsRight arcBackwardsRight = (PathStepArcBackwardsRight) currentStep;
			mouth.sendBackwardsArcRight(arcBackwardsRight.getRadius(), arcBackwardsRight.getAngle());
			break;
			
		case KICK:
			PathStepKick kick = (PathStepKick) currentStep;
			mouth.sendKick(kick.getPower());
			break;
			
		case STOP:
			mouth.sendStop();
			break;
		}
	}

	
	@Override
	public void consumeOperation(Operation operation) {
		setOperation(operation);
	}
}
