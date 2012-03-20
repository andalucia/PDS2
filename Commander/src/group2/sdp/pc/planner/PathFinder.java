package group2.sdp.pc.planner;

import group2.sdp.common.util.Geometry;
import group2.sdp.common.util.Pair;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.mouth.MouthInterface;
import group2.sdp.pc.planner.operation.Operation;
import group2.sdp.pc.planner.operation.OperationReallocation;
import group2.sdp.pc.planner.pathstep.PathStep;
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
	
	private static final boolean verbose = true;
	
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
					System.out.println("Successful");
				}
				executeNextStep(dpi);
			} else if (currentStep.problemExists(dpi)) {
				if (verbose) {
					System.out.println("Fail");
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
	}
	
	/**
	 * Create the PathSteps for an OperationReallocation
	 * 
	 * This method returns void but it should populate the pathStepList with pathSteps
	 */
	private void planReallocation(DynamicInfo dpi) {
		OperationReallocation op = (OperationReallocation) currentOperation;
		
		LinkedList<PathStep> pathStepList = getDoubleArcPath(dpi, op);
		
		this.pathStepList = pathStepList;
	}

	public static LinkedList<PathStep> getDoubleArcPath(DynamicInfo dpi,
			OperationReallocation op) {
		LinkedList<PathStep> pathStepList = new LinkedList<PathStep>();
		
		double d2 = op.getOrientation();
		double d2t = (d2 + 90.0) % 360.0;
		
		double x1 = dpi.getAlfieInfo().getPosition().getX();
		double y1 = dpi.getAlfieInfo().getPosition().getY();
		
		double x2 = op.getPosition().getX();
		double y2 = op.getPosition().getY();
		
		double r = 10.0; // ?!?!?
		
		double x0 = x2 + /* - */ r * Math.cos(Math.toRadians(d2t));
		double y0 = y2 + /* - */ r * Math.sin(Math.toRadians(d2t));
		
		double d1 = dpi.getAlfieInfo().getFacingDirection();
		double d1t = (d1 + 90.0) % 360.0;
		
		double x3 = x0 + /* - */ r * Math.cos(Math.toRadians(d1t));
		double y3 = y0 + /* - */ r * Math.sin(Math.toRadians(d1t));
		
		// Centre of the second arc.
		Point2D p0 = new Point2D.Double(x0, y0);
		Point2D p1 = new Point2D.Double(x1, y1);
		Point2D p3 = new Point2D.Double(x3, y3);
		
		Pair<Point2D, Point2D> intersections = Geometry.getLineCircleIntersections(
				p1,
				p3, 
				p0,
				r
		);
		
		double dist1 = intersections.getFirst().distance(p3);
		double dist2 = intersections.getSecond().distance(p3);
		
		// Point of transition between the arcs:
		Point2D p5 = dist1 > dist2 ? intersections.getFirst() : intersections.getSecond();
		Point2D temp = 
			new Point2D.Double(
					p1.getX() + Math.cos(Math.toRadians(d1t)),
					p1.getY() + Math.sin(Math.toRadians(d1t))
			);
		
		// Centre of the first arc:
		Point2D p7 = Geometry.getLinesIntersection(p0, p5, p1, temp);
		
		// The radius of the first arc:
		double radius1 = p1.distance(p7);
		
		double angle = Geometry.getArcAngle(
				dpi.getAlfieInfo().getPosition(), 
				p5, 
				radius1);
		
		PathStepArcForwardsRight firstArc = 
			new PathStepArcForwardsRight(
					dpi.getAlfieInfo().getPosition(), 
					dpi.getAlfieInfo().getFacingDirection(), 
					radius1, 
					angle,
					10
			);
		
		angle = Geometry.getArcAngle(
				p5,
				op.getPosition(), 
				r);
		
		PathStepArcForwardsLeft secondArc = 
			new PathStepArcForwardsLeft(
					firstArc.getTargetDestination(), 
					firstArc.getTargetOrientation(),
					r, 
					angle, 
					10
			);
		
		pathStepList.add(firstArc);
		pathStepList.add(secondArc);
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
