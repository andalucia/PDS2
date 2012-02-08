package group2.sdp.pc.planner;

import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.planner.commands.ComplexCommand.Type;
import group2.sdp.pc.planner.commands.KickCommand;
import group2.sdp.pc.planner.commands.ReachDestinationCommand;
import group2.sdp.pc.server.skeleton.ServerSkeleton;


public class PlanExecutorSimulatorTest {

	/**
	 * !!!!Please ignore this class for now!!! I am using it to test the integration of the Simulator
	 */
	private ServerSkeleton simulator; 
	
	public PlanExecutorSimulatorTest(ServerSkeleton alfieServer) {
		this.simulator = alfieServer;
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

	private void executeReachDestinationCommand(ReachDestinationCommand currentCommand) {
		simulator.sendGoForward(3,10);
		System.out.println("Reach destination");
		
	}

	private void executeKickCommand(KickCommand currentCommand) {
		// TODO Auto-generated method stub
		
	}

	public void execute2(Type currentCommand) {
		switch(currentCommand){
		case REACH_DESTINATION:
			//executeReachDestinationCommand(currentCommand);
			simulator.sendGoForward(3,10);
			System.out.println("Reach destination");
			break;
		case KICK:
			//executeKickCommand((KickCommand)currentCommand);
			break;
		}
		
	}
	
}
