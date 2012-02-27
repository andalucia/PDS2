package group2.sdp.pc.planner.operation;

public interface Operation {
	
	public enum Type {
		REALLOCATION, // reach destination
		STRIKE, // kick: http://en.wikipedia.org/wiki/Operation_Strike_of_the_Sword
		CHARGE, // dribble: http://en.wikipedia.org/wiki/Battle_of_Basra_%282008%29
		OVERLOAD // stop: http://en.wikipedia.org/wiki/Operation_Overload
		// ADD OTHERS
	}
	
	public Type getType(); 
}
