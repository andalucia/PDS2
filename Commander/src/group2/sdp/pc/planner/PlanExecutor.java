
package group2.sdp.pc.planner;

import java.awt.geom.Point2D;

import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.planner.commands.KickCommand;
import group2.sdp.pc.planner.commands.ReachDestinationCommand;
import group2.sdp.pc.server.Server;

/**
 * Under construction...
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

	
	
	
	//Reach destination
	private void executeReachDestinationCommand(ReachDestinationCommand currentCommand) {
		// TODO Auto-generated method stub
		
		//this will need to be changed for after milestone 2 to handle pathFinding
		Point2D target = currentCommand.getTarget();
		Point2D Alfie = currentCommand.getOrigin();
		double facing = currentCommand.getFacing();
		
		int distance = getDistance(target, Alfie);
		
		int angleToTurn = (int) getAngleToTarget(target, Alfie, facing);
		
		if(angleToTurn < 0 ){
			angleToTurn = Math.abs(angleToTurn);
			alfieServer.sendSpinRight(512, angleToTurn);
		}else{
			alfieServer.sendSpinLeft(512, angleToTurn);
		}
		
		alfieServer.sendGoForward(512, distance);
		
		
	}

	

	private void executeKickCommand(KickCommand currentCommand) {
		// TODO Auto-generated method stub
		
	}
	
	private double getAngleToTarget(Point2D target, Point2D alfie, double facing) {
		
		double diffInX = (alfie.getX() - target.getX() );
		double diffInY = (alfie.getY() - target.getY() );
		double angle = Math.toDegrees(Math.atan2(diffInY, diffInX));
		if(angle>0){
			angle = 360 + angle;
		}
		angle = angle - facing;
		
		return angle;
		
	}

	
	private int getDistance(Point2D ball, Point2D alfie) {

		double diffInX = (ball.getX() - alfie.getX());
		double diffInY = (ball.getY() - alfie.getY());
		
		double xSquared = Math.pow(diffInX, 2);
		double ySquared = Math.pow(diffInY, 2);
		
		int distance = (int) Math.sqrt(xSquared + ySquared);
		
		return distance;
	}
}
