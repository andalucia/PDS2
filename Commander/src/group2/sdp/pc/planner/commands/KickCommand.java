package group2.sdp.pc.planner.commands;

public class KickCommand implements ComplexCommand {

	@Override
	public Type getType() {
		return Type.KICK;
	}

}
