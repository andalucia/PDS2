
package group2.sdp.pc.planner;

import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.planner.operation.Operation;
import group2.sdp.pc.planner.operation.OperationCharge;
import group2.sdp.pc.planner.operation.OperationOverload;
import group2.sdp.pc.planner.operation.OperationReallocation;
import group2.sdp.pc.planner.operation.OperationStrike;
import group2.sdp.pc.server.skeleton.ServerSkeleton;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

import java.awt.geom.Point2D;

/**
 * Takes a command from the planner and executes it. This class is responsible for 
 * sending the "physical" instructions to Alfie or the simulator.
 * 
 * Every new command should be added to the switch statement and to the enum in ComplexCommand.
 */
public class PathFinder implements DynamicInfoConsumer {

	/**
	 * Sets verbose mode on or off, for debugging
	 */
	private static final boolean VERBOSE = true;

	private static final int MAX_SPEED = 50;

	private static final int TURNING_SPEED = 10;

	private static final int SLOW_TURNING_SPEED = 2;

	/**
	 * Accuracy of the initial angle
	 * Maximum with current vision (to prevent stuttering) is 38 degrees
	 */
	private static final int LONG_TURNING_ERROR_THRESHOLD = 40;

	/**
	 * Defines the accuracy for Alfie's final angle
	 * Maximum accuracy with the current vision system seem to be 8 degrees  	
	 */
	private static final int SHORT_TURNING_ERROR_THRESHOLD = 5;

	/**
	 * Distance from the ball Alfie should be before trying to get to the SHORT_TURNING_ERROR_THRESHOLD accuracy
	 */
	private static final int TARGET_SHORT_THRESHOLD = 50;

	private static final int STOP_TURNING_THRESHOLD = 45;
	private static final int CRUISING_SPEED = 20;

	/**
	 * Default dribbling speed.
	 */
	private static final int DRIBBLE_SPEED = 7;
	private static final int FAST_DRIBBLE_SPEED = 54;



	/**
	 * The SeverSkeleton implementation to use for executing the commands. 
	 * Can be the Alfie bluetooth server or the simulator.
	 */
	private ServerSkeleton alfieServer;

	/**
	 * The command that is currently being executed.
	 */
	private Operation currentOperation; 

	/**
	 * If Alfie is turning right now.
	 */
	private boolean turning;

	/**
	 * Initialise the class and the ServerSkeleton to send commands to Alfie or the simulator, make sure
	 * the ServerSkeleton object passed is already initialised and connected
	 * 
	 * @param alfieServer The initialised bluetooth server or the simulator object
	 */
	public PathFinder(ServerSkeleton alfieServer) {
		this.alfieServer = alfieServer;
	}


	/**
	 * The main execution function, calls a function based on the command given, make sure you cast
	 * currentCommand to the actual command type
	 *  
	 * @param currentCommand The command to be executed
	 */
	public void setOperation(Operation currentCommand) {
		this.currentOperation = currentCommand;
		if (currentCommand == null) {
			executeOperationOverload((OperationOverload)currentCommand);
			return;
		}
		switch (currentCommand.getType()) {
		case REALLOCATION:
			executeOperationReallocation((OperationReallocation)currentCommand);
			break;
		case CHARGE:
			executeOperationCharge((OperationCharge) currentCommand);
			break;
		case STRIKE:
			executeOperationStrike((OperationStrike)currentCommand);
			break;
		case OVERLOAD:
			executeOperationOverload((OperationOverload)currentCommand);
			// ADD OTHERS
		}
	}


	/**
	 * This function is the basic movement function. It will take the passed command and use its 
	 * relevant information to work out how to navigate to the target.
	 *
	 * @param currentCommand Contains the state information for Alfie and the target 
	 */
	private void executeOperationReallocation(OperationReallocation currentCommand) {
		Point2D targetPosition = currentCommand.getTarget();
		Point2D alfiePosition = currentCommand.getOrigin();
		double alfieDirection = currentCommand.getFacingDirection();

		int angleToTurn = (int)getAngleToTarget(targetPosition, alfiePosition, alfieDirection);
		int distanceToTarget = (int) alfiePosition.distance(targetPosition);
		int threshold;

		if(distanceToTarget < TARGET_SHORT_THRESHOLD) {
			threshold = SHORT_TURNING_ERROR_THRESHOLD;
		} else {
			threshold = LONG_TURNING_ERROR_THRESHOLD;
		}

		if (VERBOSE) {
			System.out.println("Angle to turn to: " + angleToTurn);
			System.err.println("Distance: " + distanceToTarget);
		}

		// If Alfie is not facing the ball:
		if (Math.abs(angleToTurn) > threshold) {
			turning = true;
			if (angleToTurn < 0) {
				alfieServer.sendSpinLeft(TURNING_SPEED, 0);
				if(VERBOSE) {
					System.out.println("Turning right " + Math.abs(angleToTurn) + " degrees");
				}
			} else {
				alfieServer.sendSpinRight(TURNING_SPEED, 0);
				if(VERBOSE) {
					System.out.println("Turning left " + angleToTurn + " degrees");
				}
			}
		} else {
			// Alfie is facing the ball: go forwards
			turning = false;
			alfieServer.sendGoForward(CRUISING_SPEED, 0);
			if(VERBOSE) {
				System.err.println("Going forward at speed: " + CRUISING_SPEED);
			}
		}
	}

	/**
	 * This function is the basic dribbling function. Currently it just dribbles forward
	 * Later, logic should be added to steer Alfi towards goal and away from the opponent
	 *
	 * @param currentCommand Contains the state information for Alfi, the ball and the opponent robot
	 */	
	private void executeOperationCharge(OperationCharge currentCommand) {
		alfieServer.sendGoForward(DRIBBLE_SPEED, 30);
		//alfieServer.sendStop();
	}

	/**
	 * This function is called directly before we kick ass and explode into rampant celebration 
	 * 
	 * @param currentCommand Contains absolutely no useful information at all
	 */	
	private void executeOperationStrike(OperationStrike currentCommand) {
		alfieServer.sendKick(MAX_SPEED);
	}

	/**
	 * This function stops Alfie and makes him wait for the next instruction
	 */
	private void executeOperationOverload(OperationOverload currentCommand) {
		System.out.println("executeStopCommand() called");
		alfieServer.sendStop();
	}

	/**
	 * This function finds the smallest angle between Alfie and his target.
	 * 
	 * @param targetPosition Position of the target.
	 * @param alfiePosition Position of Alfie.
	 * @param facingDirection The angle Alfie is facing.
	 * 
	 * @return The angle to turn at.
	 */
	protected static double getAngleToTarget(Point2D targetPosition, Point2D alfiePosition, double facingDirection) {
		double dx = (targetPosition.getX() - alfiePosition.getX());
		double dy = (targetPosition.getY() - alfiePosition.getY());

		double angle = Math.toDegrees(Math.atan2(dy, dx));

		if (angle < 0) {
			angle = 360 + angle;
		}
		double result = angle - facingDirection;
		// Variables angle and facingDirection are between 0 and 360. Thus result is 
		// between -360 and 360. We need to normalize to -180 and 180. 
		if (result < -180) {
			result += 360;
		} else if (result > 180) {
			result -= 360;
		}
		return result;
	}

	@Override
	public void consumeInfo(DynamicPitchInfo dpi) {
		if (currentOperation instanceof OperationReallocation) {
			OperationReallocation cmd = (OperationReallocation)currentOperation;
			Point2D targetPosition = cmd.getTarget();

			int angleToTurn = (int)getAngleToTarget(
					targetPosition, 
					dpi.getAlfieInfo().getPosition(), 
					dpi.getAlfieInfo().getFacingDirection());
			if (VERBOSE) {
				System.out.println("Target at " + angleToTurn + " degrees");
			}
			if (turning) {
				// Should Alfie stop turning?
				if (Math.abs(angleToTurn) <= STOP_TURNING_THRESHOLD) {
					// Makes Alfie stop turning.
					cmd = new OperationReallocation(
							cmd.getTarget(), 
							dpi.getAlfieInfo().getPosition(), 
							dpi.getAlfieInfo().getFacingDirection());
					executeOperationReallocation(cmd);
				}
			} else {
				// Alfie should automatically stop when he reaches the ball.
				if (Math.abs(angleToTurn) > SHORT_TURNING_ERROR_THRESHOLD) {
					// Makes Alfie stop turning.
					cmd = new OperationReallocation(
							cmd.getTarget(), 
							dpi.getAlfieInfo().getPosition(), 
							dpi.getAlfieInfo().getFacingDirection());
					executeOperationReallocation(cmd);
				}
			}
		}
	}
}
