package group2.sdp.pc.planner.operation;


/**
 * 
 * @author Alfie
 * 
 * <p><b>Description:</b>	Similar to military operation. Describes what actions should be taken</p>
 * 
 * <p><b>Producer:</b> {@link FieldMarshal}</p>
 * 
 * <p><b>Types:</b></br>
 * <b>Reallocation</b> - move to a position and face a direction</br>
 * <b>Strike</b> - kick</br>
 * <b>Charge</b> - dribble to a position and face a particular direction at the end</br>
 * <b>Overload</b> - stop on the spot</p>
 *
 */

public interface Operation {
	
	public enum Type {
		REALLOCATION, // 
		STRIKE, // http://en.wikipedia.org/wiki/Operation_Strike_of_the_Sword
		CHARGE, // http://en.wikipedia.org/wiki/Battle_of_Basra_%282008%29
		OVERLOAD // http://en.wikipedia.org/wiki/Operation_Overload
		// ADD OTHERS
	}
	
	public Type getType(); 
}
