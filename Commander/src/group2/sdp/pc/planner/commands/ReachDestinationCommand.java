package group2.sdp.pc.planner.commands;


public class ReachDestinationCommand implements ComplexCommand {

	@Override
	public Type getType() {
		return ComplexCommand.Type.REACH_DESTINATION;
	}

}
