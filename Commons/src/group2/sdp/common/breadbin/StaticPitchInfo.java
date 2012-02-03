package group2.sdp.common.breadbin;

/**
 * A class containing all the needed information about the pitch at a given moment. 
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
	 * A constructor that takes all required data as arguments.	
	 * @param ballInfo The information about the ball.
	 * @param alfieInfo The information about the Alfie.
	 * @param opponentInfo The information about the opponent.
	 */
	public StaticPitchInfo(StaticBallInfo ballInfo, StaticRobotInfo alfieInfo,
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
	
	
}
