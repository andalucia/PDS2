
package group2.sdp.pc.planner;

import java.awt.Point;
import java.awt.geom.Point2D;

import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.planner.commands.KickCommand;
import group2.sdp.pc.planner.commands.ReachDestinationCommand;
import group2.sdp.pc.server.Server;

/**
 * Under construction...
 */
/**
 * @author s0930584
 *
 */
public class PlanExecutor {

	/**
	 * The server to use for executing the commands.
	 */
	private Server alfieServer; 
	
	public PlanExecutor(Server alfieServer) {
		this.alfieServer = alfieServer;
	}
	
	public void execute(ComplexCommand currentCommand) {
		switch (currentCommand.getType()) {
		case REACH_DESTINATION:
			executeReachDestinationCommand((ReachDestinationCommand)currentCommand);
			break;
		case KICK:
			executeKickCommand((KickCommand)currentCommand);
			break;
		// ADD OTHERS
		}
	}

	
	/**
	 * this function is the basic movement function. It will take the passed command and use its relevant information
	 * to work out how to navigate to the target
	 * @param currentCommand
	 * 
	 * @return 
	 */
	private void executeReachDestinationCommand(ReachDestinationCommand currentCommand) {
		// TODO Auto-generated method stub
		
		//this will need to be changed for after milestone 2 to handle pathFinding
		//target is were we are navigating to
		Point2D target = currentCommand.getTarget();
		Point2D Alfie = currentCommand.getOrigin();
		double facing = currentCommand.getFacing();
		
		
		/*
		 * this is the direct distance from the Alfie to the target
		 */
		int actualDistance = getDistance(target, Alfie);
		int distance = actualDistance;
		//System.out.print("the distance to the  ball is : " + distance  + "\n");
		
		//calls the angle to target function
		int angleToTurn = (int) getAngleToTarget(target, Alfie, facing);
		
		//angleToTurn function will give an angle >0 if angle is clockwise (right) of Alfie
		if(angleToTurn > 180 ){
			angleToTurn =  360 - angleToTurn;
			//System.out.print("Alie must turn Right at angle of : " + angleToTurn  + "\n");
			alfieServer.sendSpinRight(512, angleToTurn);
		}else{
			alfieServer.sendSpinLeft(512, angleToTurn);
			//System.out.print("Alie must turn left at angle of : " + angleToTurn  + "\n");
		}
		
		alfieServer.sendGoForward(512, distance);
				
	}

	

	private void executeKickCommand(KickCommand currentCommand) {
		alfieServer.sendKick(512);
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
		//System.out.print("the distance from Alfi to the ball is : X " + diffInX + " Y " + diffInY  + "\n");
		/*
		 *
		 * atan2 will give its angle in rads and it will be negative if
		 * it is in lower half ie if the counterclockwise angle is >180
		 * (starting at our zero) then the resulted angle is given as a
		 *  negative angle that represents the clockwise rotation
		 */
		double angle = Math.toDegrees(Math.atan2(diffInY, diffInX));
		
		if(angle<0){
			angle = 360 + angle;
		}
		angle = angle - facing;
		//System.out.print("the angle from the zero position to the ball is : " + angle  + "\n");
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
