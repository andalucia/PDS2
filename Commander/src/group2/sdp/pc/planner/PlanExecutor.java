package group2.sdp.pc.planner;

import group2.sdp.pc.planner.commands.ComplexCommand;
import group2.sdp.pc.planner.commands.KickCommand;
import group2.sdp.pc.planner.commands.ReachDestinationCommand;
import group2.sdp.pc.server.Server;
import group2.sdp.pc.server.skeleton.ServerSkeleton;

/**
 * Under construction...
 */
public class PlanExecutor {

	/**
	 * The server to use for executing the commands.
	 */
	private ServerSkeleton alfieServer; 
	
	public PlanExecutor(ServerSkeleton alfieServer) {
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

	private void executeReachDestinationCommand(ReachDestinationCommand currentCommand) {
		// TODO Auto-generated method stub
		
	}

	private void executeKickCommand(KickCommand currentCommand) {
		// TODO Auto-generated method stub
		
	}
	

}
