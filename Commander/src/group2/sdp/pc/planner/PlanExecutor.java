
package group2.sdp.pc.planner;

import java.awt.geom.Point2D;

import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.planner.commands.DribbleCommand;
import group2.sdp.pc.planner.commands.KickCommand;
import group2.sdp.pc.planner.commands.ReachDestinationCommand;
import group2.sdp.pc.planner.commands.StopCommand;
import group2.sdp.pc.server.skeleton.ServerSkeleton;

/**
 * Takes a command from the planner and executes it. This class is responsible for sending the "physical"
 * instructions to Alfie or the simulator.
 * 
 * Every new command should be added to the switch statement and to the enum in ComplexCommand.
 */
public class PlanExecutor {

	/**
	 * The SeverSkeleton implementation to use for executing the commands. Can be the Alfi bluetooth 
	 * server or (in the future) the simulator
	 */
	private ServerSkeleton alfieServer; 
	
	/**
	 * Default dribble speed
	 */
	private static final int DRIBBLE_SPEED = 30;
	
	/**
	 * Sets verbose mode on or off, for debugging
	 */
	private static final boolean VERBOSE = false;
	
	/**
	 * Initialise the class and the ServerSkeleton to send commands to Alfi or the simulator, make sure
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
	 * this function is the basic movement function. It will take the passed command and use its relevant
	 * information to work out how to navigate to the target
	 *
	 * @param currentCommand Contains the state information for Alfi and the target 
	 */
	private void executeReachDestinationCommand(ReachDestinationCommand currentCommand) {
	
		// This will need to be changed for after milestone 2 to handle pathFinding
		// Target is were we are navigating to
		Point2D target = currentCommand.getTarget();
		Point2D Alfie = currentCommand.getOrigin();
		double facing = currentCommand.getFacing();
		
		// Calculate direct distance from the Alfie to the target
		int actualDistance = getDistance(target, Alfie);
		int distance = actualDistance;
		
		if(VERBOSE) {
			System.out.print("the distance to the  ball is : " + distance  + "\n");
		}
		
		// Calculate the angle required to face the ball
		int angleToTurn = (int) getAngleToTarget(target, Alfie, facing);
		
		// angleToTurn is always given as the anti-clockwise angle needed, if the angle is above 180
		// then it's quicker for us to turn right
		if(angleToTurn > 180 ){
			
			angleToTurn =  360 - angleToTurn;
			
			if(VERBOSE) {
				System.out.print("Alie must turn Right at angle of : " + angleToTurn  + "\n");
			}
			
			alfieServer.sendSpinRight(512, angleToTurn);
			
		} else {
			
			alfieServer.sendSpinLeft(512, angleToTurn);
			
			if(VERBOSE) {
				System.out.print("Alie must turn left at angle of : " + angleToTurn  + "\n");
			}
		}
		
		// After we've turned start moving forward until we're at the ball		
		alfieServer.sendGoForward(512, 0);
				
	}
	
	/**
	 * This function is the basic dribbling function. Currently it just dribbles forward
	 * Later, logic should be added to steer Alfi towards goal and away from the opponent
	 *
	 * @param currentCommand Contains the state information for Alfi, the ball and the opponent robot
	 */	
	private void executeDribbleCommand(DribbleCommand currentCommand) {
		alfieServer.sendGoForward(DRIBBLE_SPEED, 30);
	}
	
	/**
	 * This function is called directly before we kick ass and explode into rampant celebration 
	 * 
	 * @param currentCommand Contains absolutely no useful information at all
	 */	
	private void executeKickCommand(KickCommand currentCommand) {
		alfieServer.sendKick(512);
	}
	
	/**
	 * This function stops Alfi and makes him wait for the next instruction
	 */
	private void executeStopCommand(StopCommand currentCommand) {
		alfieServer.sendStop();
	}
		
	/**
	 * this function finds the smallest angle between Alfie and his target
	 * uses arc tan 2 function that gives it's result in rads
	 * 
	 * @param target co-ordinates of our target to navigate to
	 * @param alfie position of Alfie
	 * @param facing the angle we are facing
	 * 
	 * @return the angle to turn it will be positive if counterclockwise and negative for clockwise
	 */
	private double getAngleToTarget(Point2D target, Point2D alfie, double facing) {
		
		double diffInX = (target.getX() - alfie.getX() );
		double diffInY = (target.getY() - alfie.getY() );
		
		if(VERBOSE) {
			System.out.print("the distance from Alfi to the ball is : X " + diffInX + " Y " + diffInY  + "\n");
		}
		
		/*
		 * atan2 will give its angle in rads and it will be negative if
		 * it is in lower half ie if the counterclockwise angle is >180
		 * (starting at our zero) then the resulted angle is given as a
		 *  negative angle that represents the clockwise rotation
		 */
		double angle = Math.toDegrees(Math.atan2(diffInY, diffInX));
		
		if(angle < 0){
			angle = 360 + angle;
		}
		angle = angle - facing;
		
		if(VERBOSE) {
			System.out.print("the angle from the zero position to the ball is : " + angle  + "\n");
		}
		
		return angle;
		
	}

	/**
	 * simple function to calculate the distance between two points
	 * 
	 * @param ball this is the position of the ball
	 * @param alfie this is the position of Alfie
	 * @return distance to the ball
	 */
	public int getDistance(Point2D ball, Point2D alfie) {

		double diffInX = (ball.getX() - alfie.getX());
		double diffInY = (ball.getY() - alfie.getY());
		
		double xSquared = Math.pow(diffInX, 2);
		double ySquared = Math.pow(diffInY, 2);
		
		int distance = (int) Math.sqrt(xSquared + ySquared);
		
		return distance;
	}
}
