package group2.sdp.pc.breadbin;

/**
 * A class containing all the static information about the pitch at a given moment. 
 */
public class StaticPitchInfo {
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
	 * The information about our goal
	 */
	protected StaticGoalInfo alfieGoalInfo;
	
	/**
	 * The information about our opponent's goal;
	 */
	protected StaticGoalInfo opponentGoalInfo;
	
	/**
	 * A constructor that takes all required data as arguments.	
	 * @param ballInfo The information about the ball.
	 * @param alfieInfo The information about the Alfie.
	 * @param opponentInfo The information about the opponent.
	 * @param alfieGoalInfo The information about Alfie's goal.
	 * @param opponentGoalInfo The information about the opponent's goal.
	 */
	public StaticPitchInfo(StaticBallInfo ballInfo, StaticRobotInfo alfieInfo,
			StaticRobotInfo opponentInfo, StaticGoalInfo alfieGoalInfo,
			StaticGoalInfo opponentGoalInfo) {
		super();
		this.ballInfo = ballInfo;
		this.alfieInfo = alfieInfo;
		this.opponentInfo = opponentInfo;
		this.alfieGoalInfo = alfieGoalInfo;
		this.opponentGoalInfo = opponentGoalInfo;
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
	
	public StaticGoalInfo getAlfieGoalInfo() {
		return alfieGoalInfo;
	}
	/**
	 * Set the information about the opponent's goal
	 * 
	 */
	public void setAlfieGoalInfo(StaticGoalInfo alfieGoalInfo) {
		this.alfieGoalInfo = alfieGoalInfo;
	}
	/**
	 * Get the information about the opponent's goal
	 * 
	 */
	public StaticGoalInfo getOpponentGoalInfo() {
		return opponentGoalInfo;
	}
	/**
	 * Set the information about the opponent's goal
	 * 
	 */
	public void setOpponentGoalInfo(StaticGoalInfo opponentGoalInfo) {
		this.opponentGoalInfo = opponentGoalInfo;
	}
	
	/**
	 * Prints information available in this class. Currently does not 
	 * print goal information (because it's boring)
	 */
	public void printAllStaticInfo() {
		//print ball info
		System.out.println("Ball info: position = " + ballInfo.getPosition());
		//print alfie info
		System.out.println("Alfie info: position = " + alfieInfo.getPosition() + 
				"\t facing direction = " + alfieInfo.getFacingDirection());
		//print ENEMY info
		System.out.println("Enemy info: position = " + opponentInfo.getPosition() + 
				"\t facing direction = " + opponentInfo.getFacingDirection());

	}
	
	
}
