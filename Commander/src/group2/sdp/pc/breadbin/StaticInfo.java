package group2.sdp.pc.breadbin;

/**
 * Description: Basically, a structure containing two Static Robot Info objects 
 *               and one Static Ball Info object. Formally, information that is
 *               frame-specific and can be extracted from a single frame (as 
 *               opposed to multiple frames).
 * Contains:    1 SBI, 2 SRIs - Alfie and opponent
 */
public class StaticInfo {
	/**
	 * The information about the ball.
	 */
	protected StaticBallInfo ballInfo;
	/**
	 * The information about the Alfie.
	 */
	protected StaticRobotInfo alfieInfo;
	/**
	 * The information about the opponent.
	 */
	protected StaticRobotInfo opponentInfo;

	
	/**
	 * A constructor that takes all required data as arguments.	
	 * @param ballInfo The information about the ball.
	 * @param alfieInfo The information about the Alfie.
	 * @param opponentInfo The information about the opponent.
	 * @param alfieGoalInfo The information about Alfie's goal.
	 * @param opponentGoalInfo The information about the opponent's goal.
	 */
	public StaticInfo(StaticBallInfo ballInfo, StaticRobotInfo alfieInfo,
			StaticRobotInfo opponentInfo) {
		super();
		this.ballInfo = ballInfo;
		this.alfieInfo = alfieInfo;
		this.opponentInfo = opponentInfo;
	}
	
	
	/**
	 * Get the information about the ball.
	 * @return The information about the ball.
	 */
	public StaticBallInfo getBallInfo() {
		return ballInfo;
	}
	
	/**
	 * Set the information about the ball.
	 * @param ballInfo The information about the ball.
	 */
	public void setBallInfo(StaticBallInfo ballInfo) {
		this.ballInfo = ballInfo;
	}
	
	/**
	 * Get the information about the Alfie.
	 * @return The information about the Alfie.
	 */
	public StaticRobotInfo getAlfieInfo() {
		return alfieInfo;
	}
	
	/**
	 * Set the information about the Alfie.
	 * @param alfieInfo The information about the Alfie.
	 */
	public void setAlfieInfo(StaticRobotInfo alfieInfo) {
		this.alfieInfo = alfieInfo;
	}
	
	/**
	 * Get the information about the opponent. 
	 * @return The information about the opponent.
	 */
	public StaticRobotInfo getOpponentInfo() {
		return opponentInfo;
	}
	
	/**
	 * Set the information about the opponent.
	 * @param opponentInfo The information about the opponent.
	 */
	public void setOpponentInfo(StaticRobotInfo opponentInfo) {
		this.opponentInfo = opponentInfo;
	}
	
	/**
	 * Returns information about Alfie's goal
	 * 
	 */

	
	/**
	 * Prints information available in this class. Currently does not 
	 * print goal information (because it's boring)
	 */
	public void printAllStaticInfo() {
		System.out.println("Ball info: position = " + ballInfo.getPosition());
		System.out.println("Alfie info: position = " + alfieInfo.getPosition() + 
				"\t facing direction = " + alfieInfo.getFacingDirection());
		System.out.println("Enemy info: position = " + opponentInfo.getPosition() + 
				"\t facing direction = " + opponentInfo.getFacingDirection());

	}
	
	@Override
	public String toString() {
		return "A:[" + alfieInfo + "] O:[" + opponentInfo + "] B:[" + ballInfo + "]";
	}
}
