package group2.sdp.pc.planner;

import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.planner.commands.DribbleCommand;
import group2.sdp.pc.planner.commands.KickCommand;
import group2.sdp.pc.planner.commands.ReachDestinationCommand;
import group2.sdp.pc.planner.commands.StopCommand;
import group2.sdp.pc.planner.skeleton.PlannerSkeleton;

public class Planner extends PlannerSkeleton {

	private boolean verbose;
	
	public Planner(PlanExecutor executor) {
		super(executor);
		verbose = true;
	}

	
	
	@Override
	/**
	 * plans the next move of our robot this is where the majority
	 * of our high level situational analysis happens
	 */
	protected ComplexCommand planNextCommand(DynamicPitchInfo dpi) {
		if (verbose) {
			System.out.println("Planning next command.");
		}
		// TODO: Implement the planning here

		DynamicRobotInfo AlfieInfo = dpi.getAlfieInfo();
		DynamicBallInfo ballInfo = dpi.getBallInfo();
		
		
		Point2D ball = ballInfo.getPosition();
		//System.out.print("the position of the ball is : " + ball + "\n");
		Point2D Alfie = AlfieInfo.getPosition();
		//System.out.print("the position of Alfie is : " + Alfie + "\n");
		double facing = AlfieInfo.getFacingDirection();
		//System.out.print("the angle were are being told we are facing is : " + facing  + "\n");
		
		
		if( executor.getDistance(ball, Alfie) > 30){
			ReachDestinationCommand reachDestination = new ReachDestinationCommand(ball, Alfie, facing);
			return reachDestination;
		}else if(executor.getDistance(ball, Alfie) > 10) {
			DribbleCommand dribble = new DribbleCommand(ball, Alfie, facing);
			return dribble;
		} else {
			StopCommand stop = new StopCommand();
			return stop;
		}
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
}
