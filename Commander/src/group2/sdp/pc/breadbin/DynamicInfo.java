package group2.sdp.pc.breadbin;

/**
 * Description: 
 * <br/>
 * Basically, contains one Dynamic Ball Info object and two Static 
 *               Ball Info objects. Actually, extends the Static Info type, thus 
 *               containing 1 SBI and 2 SRIs. Thanks to DBI and DRI extending SBI
 *               and SRI, there is no need of adding new fields in the class. On 
 *               construction or setting, the parameters are cast down, while on 
 *               getting, they are cast back up. 
 *   
 *               Alternative approach would be to create a simple container for
 *               the DBI and two DRI, that duplicates the code in SI. This might
 *               be faster, due to less casting, but would reduce flexibility as
 *               SI objects would not have the ability to be reused as DI objects.
 *               However, we are not taking benefit from this flexibility at the 
 *               time of writing this document.
 * <br/>
 * <br/>         
 * Contents:
 * <br/>    
 * 
 * One DBI and two DRI's - one for Alfie and two for its opponent. 
 */
public class DynamicInfo extends StaticInfo {

	/**
	 * A constructor that takes all required data as arguments.	
	 * @param ballInfo The information about the ball.
	 * @param alfieInfo The information about the Alfie.
	 * @param opponentInfo The information about the opponent.
	 */
	public DynamicInfo(DynamicBallInfo ballInfo, DynamicRobotInfo alfieInfo, DynamicRobotInfo opponentInfo) {
		super(ballInfo, alfieInfo, opponentInfo);
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
