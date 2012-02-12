package group2.sdp.pc.planner.commands;

/**
 * A special command for Alfie that tells him that he's doing really well and should keep
 * doing whatever he's doing.
 */
public class ContinueCommand implements ComplexCommand {

	@Override
	public Type getType() {
		return Type.CONTINUE;
	}
	
}
