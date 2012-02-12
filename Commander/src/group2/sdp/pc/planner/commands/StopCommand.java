package group2.sdp.pc.planner.commands;

public class StopCommand implements ComplexCommand {

	@Override
	public Type getType() {
		return Type.STOP;
	}
	
}
