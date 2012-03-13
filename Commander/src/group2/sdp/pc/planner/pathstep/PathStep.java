package group2.sdp.pc.planner.pathstep;

import group2.sdp.pc.breadbin.DynamicInfo;


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
	
	/**
	 * 
	 * Type is the list of all possible small steps alfie can take to complete an Operation 
	 *
	 */
	
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
	
	/**
	 * a getter method to return the Type of step
	 * @return Type
	 */
	public Type getType();
	
	
	/**
	 * this method will be used by PathFinder to check if the current step
	 * is successful (to decide if PathFinder needs a new step)
	 * @return boolean
	 */
	public boolean isSuccessful(DynamicInfo dpi);
	
	
	/**
	 * this method will be used by PathFinder to check if the current step
	 * has gone wrong (to decide if PathFinder needs to replan)
	 * @return boolean
	 */
	public boolean problemExists(DynamicInfo dpi);
	
}
