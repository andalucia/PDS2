package group2.sdp.pc.breadbin;

/**
 * A class containing all the needed information about the pitch at a given moment. 
 */
public class DynamicPitchInfo extends StaticPitchInfo {

	/**
	 * A constructor that takes all required data as arguments.	
	 * @param ballInfo The information about the ball.
	 * @param alfieInfo The information about the Alfie.
	 * @param opponentInfo The information about the opponent.
	 */
	public DynamicPitchInfo(DynamicBallInfo ballInfo, DynamicRobotInfo alfieInfo, DynamicRobotInfo opponentInfo,
			StaticGoalInfo alfieGoalInfo, StaticGoalInfo opponentGoalInfo) {
		super(ballInfo, alfieInfo, opponentInfo, alfieGoalInfo, opponentGoalInfo);
	}
	
	/**
	 * Get the information about the ball.
	 * @return The information about the ball.
	 */
	public DynamicBallInfo getBallInfo() {
		return (DynamicBallInfo) ballInfo;
	}
	
	/**
	 * Set the information about the ball.
	 * @param ballInfo The information about the ball.
	 */
	public void setBallInfo(DynamicBallInfo ballInfo) {
		this.ballInfo = ballInfo;
	}
	
	/**
	 * Get the information about the Alfie.
	 * @return The information about the Alfie.
	 */
	public DynamicRobotInfo getAlfieInfo() {
		return (DynamicRobotInfo) alfieInfo;
	}
	
	/**
	 * Set the information about the Alfie.
	 * @param alfieInfo The information about the Alfie.
	 */
	public void setAlfieInfo(DynamicRobotInfo alfieInfo) {
		this.alfieInfo = alfieInfo;
	}
	
	/**
	 * Get the information about the opponent. 
	 * @return The information about the opponent.
	 */
	public DynamicRobotInfo getOpponentInfo() {
		return (DynamicRobotInfo) opponentInfo;
	}
	
	/**
	 * Set the information about the opponent.
	 * @param opponentInfo The information about the opponent.
	 */
	public void setOpponentInfo(DynamicRobotInfo opponentInfo) {
		this.opponentInfo = opponentInfo;
	}
	
	
}
