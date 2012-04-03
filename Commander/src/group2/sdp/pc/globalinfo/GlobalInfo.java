package group2.sdp.pc.globalinfo;

import java.awt.geom.Point2D;

/**
 * <p><b><br>Description:</br></b> Information that is match-specific (changes between matches; 
 *               does not change in a single match).</p>
 * <p><b><br>Contains:</br></b>    Direction of attack (left / right), Alfie being yellow or blue, 
 *               the pitch that is being played on (characteristics, rather than 
 *               a boolean).</p>
 */
public class GlobalInfo {
	
	/**
	 * The position we take penalties from
	 */
	private static Point2D attackingPenalty;
	/**
	 * The position the opponent takes penalties from
	 */
	private static Point2D defendingPenalty;
	
	private static final Point2D PENALTY_SPOT_LEFT = new Point2D.Double(-60,0);
	private static final Point2D PENALTY_SPOT_RIGHT = new Point2D.Double(60,0);
	
	/**
	 * If we are on pitch one or not.
	 */
	private static boolean pitchOne;
	
	/**
	 * Indicates whether Alfie is attacking right or left.
	 */
	private static boolean attackingRight;
	
	/**
	 * Indicates whether Alfie is yellow or blue.
	 */
	private static boolean yellowAlfie;
	
	/**
	 * The non-changing information about the pitch on which the game is being 
	 * played.
	 */
	private static Pitch pitch = new Pitch();

	/**
	 * Get the camera of the pitch of the global info. 
	 * @return The camera of the pitch of the global info.
	 */
	public static Camera getCamera() {
		return getPitch().getCamera();
	}
	
	/**
	 * Get the colour settings of the camera of the pitch of the global info. 
	 * @return The colour settings of the camera of the pitch of the global 
	 * info.
	 */
	public static LCHColourSettings getColourSettings() {
		return getCamera().getColourSettings();
	}
	
	/**
	 * Is Alfie attacking right or left?
	 * @return If true, then Alfie is attacking right, otherwise it is 
	 * attacking left.
	 */
	public static boolean isAttackingRight() {
		return attackingRight;
	}

	/**
	 * Is Alfie attacking right or left? 
	 * @param attackingRight If true, then Alfie is attacking right, otherwise
	 * it is attacking left.
	 */
	public static void setAttackingRight(boolean attackingRight) {
		if (attackingRight) {
			attackingPenalty = PENALTY_SPOT_RIGHT;
			defendingPenalty = PENALTY_SPOT_LEFT;
		} else {
			attackingPenalty = PENALTY_SPOT_LEFT;
			defendingPenalty = PENALTY_SPOT_RIGHT;
		}
		GlobalInfo.attackingRight = attackingRight;
	}

	/**
	 * Is Alfie yellow or blue?
	 * @return If true, then Alfie is yellow, otherwise it is blue.
	 */
	public static boolean isYellowAlfie() {
		return yellowAlfie;
	}

	/**
	 * /**
	 * Is Alfie yellow or blue?
	 * @param yellowAlfie If true, then Alfie is yellow, otherwise it is blue.
	 */
	public static void setYellowAlfie(boolean yellowAlfie) {
		GlobalInfo.yellowAlfie = yellowAlfie;
	}
	
	/**
	 * Computes the middle of the goal Alfie shoots for.
	 * @return The middle of the goal Alfie shoots for.
	 */
	public static Point2D getTargetGoalMiddle() {
		double x;
		if (attackingRight) {
			x = pitch.getMinimumEnclosingRectangle().getMaxX();
		} else {
			x = pitch.getMinimumEnclosingRectangle().getMinX();
		}
		double y = pitch.getGoalCentreY();
		return new Point2D.Double(x, y);
	}

	/**
	 * Gets the top goal post of the goal that Alfie tries not to score in.
	 */
	public static Point2D getDefendingTopGoalPost() {
		double x;
		if (attackingRight) {
			x = pitch.getMinimumEnclosingRectangle().getMinX();
		} else {
			x = pitch.getMinimumEnclosingRectangle().getMaxX();
		}
		double y = pitch.getTopGoalPostYCoordinate();
		return new Point2D.Double(x, y);
	}
	
	/**
	 * Gets the bottom goal post of the goal that Alfie tries not to score in.
	 */
	public static Point2D getDefendingBottomGoalPost() {
		double x;
		if (attackingRight) {
			x = pitch.getMinimumEnclosingRectangle().getMinX();
		} else {
			x = pitch.getMinimumEnclosingRectangle().getMaxX();
		}
		double y = pitch.getBottomGoalPostYCoordinate();
		return new Point2D.Double(x, y);
	}
	
	/**
	 * Computes the middle of the goal Alfie defends.
	 * @return The middle of the goal Alfie defends.
	 */
	public static Point2D getDefensiveGoalMiddle() {
		double x;
		if (attackingRight) {
			x = pitch.getMinimumEnclosingRectangle().getMinX();
		} else {
			x = pitch.getMinimumEnclosingRectangle().getMaxX();
		}
		double y = pitch.getGoalCentreY();
		return new Point2D.Double(x, y);
	}
	
	public static Point2D getDefensiveGoalRightPost() {
		Point2D result;
		if (attackingRight) {
			result = getDefendingBottomGoalPost();
		} else {
			result = getDefendingTopGoalPost();
		}
		return result;
	}
	
	public static boolean isPitchOne() {
		return pitchOne;
	}
	
	/**
	 * Returns the position the opponent will take penalties from
	 */
	public static Point2D getDefendingPenalty() {
		return defendingPenalty;
	}
	
	/**
	 * Returns the position we will take penalties from
	 */
	public static Point2D getAttackingPenalty() {
		return attackingPenalty;
	}

	/**
	 * Sets the pitch depending on the boolean: if pitchOne is true then 
	 * we are on pitch one, otherwise we are on pitch two.
	 * @param pitchOne
	 */
	public static void setPitchOne(boolean pitchOne) {
		GlobalInfo.pitchOne = pitchOne;
		if (pitchOne) {
			setPitch(Pitch.ONE);
		} else {
			setPitch(Pitch.TWO);
		}
	}
	
	/**
	 * Get the non-changing information about the pitch on which the game is 
	 * being played.
	 * @return The non-changing information about the pitch on which the game 
	 * is being played.
	 */
	public static Pitch getPitch() {
		return pitch;
	}

	/**
	 * Set the non-changing information about the pitch on which the game is 
	 * being played.
	 * @param pitch The non-changing information about the pitch on which the 
	 * game is being played.
	 */
	private static void setPitch(Pitch pitch) {
		GlobalInfo.pitch = pitch;
	}
}
