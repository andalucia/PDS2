package group2.sdp.pc.planner.pathstep;


/**
 * 
 * 
 * @author Shaun A.K.A the bringer of bad code!!! beware but chris was here so its kay :)
 *
 * Description: - A step of a [path that aims to achieve an operation]. Describes the type of Candy
 * Packet to give to Alfie (see Communication section below), the state in which it
 * would be successful and the state in which it would fail.
 */
public interface PathStep {
	
	public enum Type{
		GO_FORWARDS,
		GO_BACKWARDS,
		SPIN_LEFT,
		SPIN_RIGHT,
		ARC_FORWARDS_LEFT,
		ARC_FORWARDS_RIGHT,
		ARC_BACKWARDS_LEFT,
		ARC_BACKWARDS_RIGHT,
		KICK,
		STOP,	
	}
	
	public Type getType();
	
	public boolean isSuccessful();
	
	public boolean problemExists();
	
}
