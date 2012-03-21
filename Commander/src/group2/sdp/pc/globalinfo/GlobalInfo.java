package group2.sdp.pc.globalinfo;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

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
	private boolean attackingRight;
	
	/**
	 * Indicates whether Alfie is yellow or blue.
	 */
	private boolean yellowAlfie;
	
	/**
	 * The non-changing information about the pitch on which the game is being 
	 * played.
	 */
	private Pitch pitch;


	/**
	 * Fully initialising constructor.
	 * @param attackingRight Indicates whether Alfie is attacking right or 
	 * left.
	 * @param yellowAlfie Indicates whether Alfie is yellow or blue.
	 * @param pitch The non-changing information about the pitch on which the 
	 * game is being played.
	 */
	public GlobalInfo(boolean attackingRight, boolean yellowAlfie, 
			Pitch pitch) {
		super();
		this.attackingRight = attackingRight;
		this.yellowAlfie = yellowAlfie;
		this.pitch = pitch;
	}

	/**
	 * Get the camera of the pitch of the global info. 
	 * @return The camera of the pitch of the global info.
	 */
	public Camera getCamera() {
		return getPitch().getCamera();
	}
	
	/**
	 * Get the colour settings of the camera of the pitch of the global info. 
	 * @return The colour settings of the camera of the pitch of the global 
	 * info.
	 */
	public LCHColourSettings getColourSettings() {
		return getCamera().getColourSettings();
	}
	
	/**
	 * Is Alfie attacking right or left?
	 * @return If true, then Alfie is attacking right, otherwise it is 
	 * attacking left.
	 */
	public boolean isAttackingRight() {
		return attackingRight;
	}

	/**
	 * /**
	 * Is Alfie attacking right or left? 
	 * @param attackingRight If true, then Alfie is attacking right, otherwise
	 * it is attacking left.
	 */
	public void setAttackingRight(boolean attackingRight) {
		this.attackingRight = attackingRight;
	}

	/**
	 * Is Alfie yellow or blue?
	 * @return If true, then Alfie is yellow, otherwise it is blue.
	 */
	public boolean isYellowAlfie() {
		return yellowAlfie;
	}

	/**
	 * /**
	 * Is Alfie yellow or blue?
	 * @param yellowAlfie If true, then Alfie is yellow, otherwise it is blue.
	 */
	public void setYellowAlfie(boolean yellowAlfie) {
		this.yellowAlfie = yellowAlfie;
	}

	/**
	 * Get the non-changing information about the pitch on which the game is 
	 * being played.
	 * @return The non-changing information about the pitch on which the game 
	 * is being played.
	 */
	public Pitch getPitch() {
		return pitch;
	}

	/**
	 * Set the non-changing information about the pitch on which the game is 
	 * being played.
	 * @param pitch The non-changing information about the pitch on which the 
	 * game is being played.
	 */
	public void setPitch(Pitch pitch) {
		this.pitch = pitch;
	}
	
	/**
	 * Computes the middle of the goal Alfie shoots for.
	 * @return The middle of the goal Alfie shoots for.
	 */
	public Point2D getTargetGoalMiddle() {
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
	public Point2D getDefendingTopGoalPost() {
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
	public Point2D getDefendingBottomGoalPost() {
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
