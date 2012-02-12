package group2.sdp.pc.planner;

import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.planner.commands.ContinueCommand;
import group2.sdp.pc.planner.commands.DribbleCommand;
import group2.sdp.pc.planner.commands.KickCommand;
import group2.sdp.pc.planner.commands.ReachDestinationCommand;
import group2.sdp.pc.planner.commands.StopCommand;
import group2.sdp.pc.planner.skeleton.PlannerSkeleton;

public class Planner extends PlannerSkeleton {

	private boolean verbose = true;
	public boolean dribbling = false;	
	
	/**
	 * Constants for the command status
	 */
	private static final int COMMAND_STARTING = 0;
	private static final int COMMAND_RUNNING = 1;
	private static final int COMMAND_FINISHING = 2;
	private static final int COMMAND_FINISHED = 3;
	private static final int COMMAND_PROBLEM = 4;
	private static final int COMMAND_STOPPED = 5;
	
	/**
	 * Store the status of the most recent command
	 */
	private int command_status = COMMAND_STARTING;
	
	private boolean started = false;
	
	
	public Planner(PlanExecutor executor) {
		super(executor);
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
		
		System.out.println(Alfie.distance(ball));
		if( Alfie.distance(ball) > 30){
			if(!dribbling){
				if(command_status != COMMAND_RUNNING){
					ReachDestinationCommand reachDestination = new ReachDestinationCommand(ball, Alfie, facing);
					command_status = COMMAND_RUNNING;
					System.err.println("GO!!!");
					return reachDestination;
				}
			
			}
		} else {
			if(command_status != COMMAND_FINISHED) {
				if(dribbling){
					System.err.println("DRIBBLE");
					DribbleCommand dribble = new DribbleCommand(ball, Alfie, facing);
					command_status = COMMAND_FINISHED;
					this.dribbling = false;
					return dribble;
				}
				System.err.println("STOP!");
				StopCommand stop = new StopCommand();
				command_status = COMMAND_FINISHED;
				return stop;
			}
		}
		
		System.err.println("CONTINUE!");
		
		return new ContinueCommand();
	}


	@Override
	protected boolean commandSuccessful(DynamicPitchInfo dpi) {
		if (command_status == COMMAND_FINISHED || started != true) {
			started = true;
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean problemExists(DynamicPitchInfo dpi) {
		if (command_status == COMMAND_PROBLEM) {
			return true;
		} else {
			return false;
		}
	}
}
