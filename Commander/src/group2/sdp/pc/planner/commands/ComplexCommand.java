package group2.sdp.pc.planner.commands;

public interface ComplexCommand {
	
	public enum Type {
		REACH_DESTINATION,
		KICK,
		DRIBBLE,
		STOP
		// ADD OTHERS
	}
	
	public Type getType(); 
}
