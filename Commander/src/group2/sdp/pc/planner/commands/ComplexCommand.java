package group2.sdp.pc.planner.commands;

public interface ComplexCommand {
	
	public enum Type {
		REACH_DESTINATION,
		KICK,
		// ADD OTHERS
	}
	
	public Type getType(); 
}
