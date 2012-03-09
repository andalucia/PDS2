package group2.sdp.pc.planner.operation;

/**
 * 
 * @author Shaun A.K.A the bringer of bad code!!! beware
 * 
 * Description: - Similar to military operation. Describes what actions should be taken.
 * 
 * Producer: Field Marshal (see Planning Pipeline section below).
 * 
 * Types:
 * 1. Reallocation - move to a position and face a direction;
 * 2. Strike - kick (hopefully the ball);
 * 3. Charge - dribble with the ball to a position and face a particular direction at the end; and
 * 4. Overload - stop on the spot.
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
