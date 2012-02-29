package group2.sdp.pc.server.skeleton;

/**
 * Interface for a server for communication with Alfie. Implemented by
 * the simulator too (apart from the obvious implementor Server).
 */
public interface ServerSkeleton {
	
	/**
	 * Tells Alfie to stop moving.
	 */
	public void sendStop();
	
	/**
	 * Tells Alfie to start moving forward. 
	 * @param speed The speed for the command.
	 * @param distance The distance to travel in an inspecified unit, 0 to travel indefinitely
	 */
	public void sendGoForward(int speed, int distance);
	/**
	 * Tells Alfie to start moving backwards. 
	 * @param speed The speed for the command.
	 */
	public void sendGoBackwards(int speed, int distance);
	
	/**
	 * Tells Alfie to become aggressive.
	 * @param power The power for the kick.
	 */
	public void sendKick(int power);
	
	/**
	 * Tells Alfie to spin on the spot counter-clock wise.
	 * @param speed The speed for the spin.
	 * @param angle The angle for the spin.
	 */
	public void sendSpinLeft(int speed, int angle);
	
	/**
	 * Tells Alfie to spin on the spot clock wise.
	 * @param speed The speed for the spin.
	 * @param angle The angle for the spin.
	 */
	public void sendSpinRight(int speed, int angle);
	
	public void sendMoveArc(int radius, int angle);
}
