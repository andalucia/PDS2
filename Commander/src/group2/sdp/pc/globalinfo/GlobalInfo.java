package group2.sdp.pc.globalinfo;

import java.awt.geom.Point2D;

/**
 * Description: Information that is match-specific (changes between matches; 
 *               does not change in a single match).
 * Contains:    Direction of attack (left / right), Alfie being yellow or blue, 
 *               the pitch that is being played on (characteristics, rather than 
 *               a boolean).
 */
public class GlobalInfo {
	
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
	private static Pitch pitch;

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
	 * /**
	 * Is Alfie attacking right or left? 
	 * @param attackingRight If true, then Alfie is attacking right, otherwise
	 * it is attacking left.
	 */
	public static void setAttackingRight(boolean attackingRight) {
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
	public static void setPitch(Pitch pitch) {
		GlobalInfo.pitch = pitch;
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
}
