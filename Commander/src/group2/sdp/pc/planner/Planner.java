package group2.sdp.pc.planner;

import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.planner.commands.ReachDestinationCommand;
import group2.sdp.pc.planner.skeleton.PlannerSkeleton;

public class Planner extends PlannerSkeleton {

	private boolean verbose;
	
	public Planner(PlanExecutor executor) {
		super(executor);
		verbose = true;
	}

	@Override
	protected ComplexCommand planNextCommand(DynamicPitchInfo dpi) {
		if (verbose) {
			System.out.println("Planning next command.");
		}
		// TODO: Implement the planning here

		DynamicRobotInfo AlfieInfo = dpi.getAlfieInfo();
		DynamicBallInfo ballInfo = dpi.getBallInfo();
		
		
		Point2D ball = ballInfo.getPosition();
		Point2D Alfie = AlfieInfo.getPosition();
		double facing = AlfieInfo.getFacingDirection();
		
		
		if( getDistance(ball, Alfie)> 30){
			ReachDestinationCommand reachDestination = new ReachDestinationCommand(ball, Alfie, facing);
			return reachDestination;
		}
		
		
		
		return null;
	}


	@Override
	protected boolean commandSuccessful(DynamicPitchInfo dpi) {
		if (!super.commandSuccessful(dpi)) {
			//TODO: implement here and remove return false;
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected boolean problemExists(DynamicPitchInfo dpi) {
		if (!super.problemExists(dpi)) {
			//TODO: implement here and remove return false;
			return false;
		} else {
			return true;
		}
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
