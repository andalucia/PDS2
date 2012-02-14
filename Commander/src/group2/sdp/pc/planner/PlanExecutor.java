
package group2.sdp.pc.planner;

import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.planner.commands.DribbleCommand;
import group2.sdp.pc.planner.commands.KickCommand;
import group2.sdp.pc.planner.commands.ReachDestinationCommand;
import group2.sdp.pc.planner.commands.StopCommand;
import group2.sdp.pc.server.skeleton.ServerSkeleton;

import java.awt.geom.Point2D;

/**
 * Takes a command from the planner and executes it. This class is responsible for 
 * sending the "physical" instructions to Alfie or the simulator.
 * 
 * Every new command should be added to the switch statement and to the enum in ComplexCommand.
 */
public class PlanExecutor {

	/**
	 * Sets verbose mode on or off, for debugging
	 */
	private static final boolean VERBOSE = true;
	
	private static final int MAX_SPEED = 50;
	
	private static final int TURNING_SPEED = 10;
	private static final int TURNING_ERROR_THRESHOLD = 10;
	private static final int STOP_TURNING_THRESHOLD = 45;
	private static final int CRUISING_SPEED = 20;
	
	/**
	 * Default dribbling speed.
	 */
	private static final int DRIBBLE_SPEED = 30;
	
	
	
	/**
	 * The SeverSkeleton implementation to use for executing the commands. 
	 * Can be the Alfie bluetooth server or the simulator.
	 */
	private ServerSkeleton alfieServer;

	/**
	 * The command that is currently being executed.
	 */
	private ComplexCommand currentCommand; 
	
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
	public PlanExecutor(ServerSkeleton alfieServer) {
		this.alfieServer = alfieServer;
	}
	
	
	/**
	 * The main execution function, calls a function based on the command given, make sure you cast
	 * currentCommand to the actual command type
	 *  
	 * @param currentCommand The command to be executed
	 */
	public void execute(ComplexCommand currentCommand) {
		this.currentCommand = currentCommand;
		if (currentCommand == null) {
			executeStopCommand((StopCommand)currentCommand);
			return;
		}
		switch (currentCommand.getType()) {
		case REACH_DESTINATION:
			executeReachDestinationCommand((ReachDestinationCommand)currentCommand);
			break;
		case DRIBBLE:
			executeDribbleCommand((DribbleCommand) currentCommand);
			break;
		case KICK:
			executeKickCommand((KickCommand)currentCommand);
			break;
		case STOP:
			executeStopCommand((StopCommand)currentCommand);
		// ADD OTHERS
		}
	}

	
	/**
	 * This function is the basic movement function. It will take the passed command and use its 
	 * relevant information to work out how to navigate to the target.
	 *
	 * @param currentCommand Contains the state information for Alfi and the target 
	 */
	private void executeReachDestinationCommand(ReachDestinationCommand currentCommand) {
		Point2D targetPosition = currentCommand.getTarget();
		Point2D alfiePosition = currentCommand.getOrigin();
		double alfieDirection = currentCommand.getFacingDirection();
		
		int angleToTurn = (int)getAngleToTarget(targetPosition, alfiePosition, alfieDirection);
		if (VERBOSE) {
			System.out.println("Angle to turn to: " + angleToTurn);
		}
		// If Alfie is not facing the ball:
		if (Math.abs(angleToTurn) > TURNING_ERROR_THRESHOLD) {
			turning = true;
			if (angleToTurn < 0) {
				alfieServer.sendSpinLeft(TURNING_SPEED, 0);
				if(VERBOSE) {
					System.out.println("Turning right " + Math.abs(angleToTurn)  + " degrees");
				}
			} else {
				alfieServer.sendSpinRight(TURNING_SPEED, 0);
				if(VERBOSE) {
					System.out.println("Turning left " + angleToTurn  + " degrees");
				}
			}
		} else {
			// Alfie is facing the ball: go forwards
			turning = false;
			alfieServer.sendGoForward(CRUISING_SPEED, 0);
		}
	}
	
	/**
	 * This function is the basic dribbling function. Currently it just dribbles forward
	 * Later, logic should be added to steer Alfi towards goal and away from the opponent
	 *
	 * @param currentCommand Contains the state information for Alfi, the ball and the opponent robot
	 */	
	private void executeDribbleCommand(DribbleCommand currentCommand) {
		alfieServer.sendGoForward(DRIBBLE_SPEED, 30);
		alfieServer.sendStop();
	}
	
	/**
	 * This function is called directly before we kick ass and explode into rampant celebration 
	 * 
	 * @param currentCommand Contains absolutely no useful information at all
	 */	
	private void executeKickCommand(KickCommand currentCommand) {
		alfieServer.sendKick(MAX_SPEED);
	}
	
	/**
	 * This function stops Alfie and makes him wait for the next instruction
	 */
	private void executeStopCommand(StopCommand currentCommand) {
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
	private double getAngleToTarget(Point2D targetPosition, Point2D alfiePosition, double facingDirection) {
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


	public void updateInfo(DynamicPitchInfo dpi) {
		if (currentCommand instanceof ReachDestinationCommand) {
			ReachDestinationCommand cmd = (ReachDestinationCommand)currentCommand;
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
					cmd = new ReachDestinationCommand(
							cmd.getTarget(), 
							dpi.getAlfieInfo().getPosition(), 
							dpi.getAlfieInfo().getFacingDirection());
					executeReachDestinationCommand(cmd);
				}
			} else {
				// Alfie should automatically stop when he reaches the ball.
				if (Math.abs(angleToTurn) > TURNING_ERROR_THRESHOLD) {
					// Makes Alfie stop turning.
					cmd = new ReachDestinationCommand(
							cmd.getTarget(), 
							dpi.getAlfieInfo().getPosition(), 
							dpi.getAlfieInfo().getFacingDirection());
					executeReachDestinationCommand(cmd);
				}
			}
		}
	}
}
