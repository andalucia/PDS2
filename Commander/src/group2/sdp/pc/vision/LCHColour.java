package group2.sdp.pc.vision;

import java.awt.Color;

/**
 * A luma-chroma-hue colour. The definitions of the terms can be found here:
 * http://en.wikipedia.org/wiki/Luma_%28video%29#Rec._601_luma_versus_Rec._709_luma_coefficients
 * http://en.wikipedia.org/wiki/HSL_and_HSV#Hue_and_chroma
 */
public class LCHColour {

	// Chroma settings:
	/**
	 * Chroma levels above this one are considered high.
	 */
	private final int HIGH_CHROMA_THRESHOLD = 80;
	/**
	 * Chroma levels below this one are considered low.
	 */
	private final int LOW_CHROMA_THRESHOLD = 20;

	// Luma settings:
	/**
	 * Luma levels above this one are considered high.
	 */
	private final int HIGH_LUMA_THRESHOLD = 90;
	/**
	 * Luma levels below this one are considered low.
	 */
	private final int LOW_LUMA_THRESHOLD = 60;	
	
	// Hue settings:
	/**
	 * Lower boundary of the green hue.
	 */
	private static int greenHueStart = 80;
	/**
	 * Upper boundary of the green hue.
	 */
	private static int greenHueEnd = 150;
	
	/**
	 * Lower boundary of the blue hue.
	 */
	private static int blueHueStart = 150;
	/**
	 * Upper boundary of the blue hue.
	 */
	private static int blueHueEnd = 300;
	
	/**
	 * Lower boundary of the red hue (note that it is higher than the upper
	 * boundary: the check is special here).
	 */
	private static int redHueStart = 300;
	/**
	 * Upper boundary of the red hue (note that it is smaller than the lower
	 * boundary: the check is special here).
	 */
	private static int redHueEnd = 30;

	/**
	 * Lower boundary of the yellow hue.
	 */
	private static int yellowHueStart = 30;
	/**
	 * Upper boundary of the yellow hue.
	 */
	private static int yellowHueEnd = 80;

	
	/**
	 * Sets the hue border between blue and red. Should be between 0 and 360.
	 * @param value The new value of the border. Default is 300.
	 */
	public static void setBlueToRedHue(int value) {
		redHueStart = blueHueEnd = value;
	}
	
	/**
	 * Sets the hue border between red and yellow. Should be between 0 and 360.
	 * @param value The new value of the border. Default is 30.
	 */
	public static void setRedToYellowHue(int value) {
		yellowHueStart = redHueEnd = value;
	}
	
	/**
	 * Sets the hue border between yellow and green. Should be between 0 and 360.
	 * @param value The new value of the border. Default is 80.
	 */
	public static void setYellowToGreenHue(int value) {
		greenHueStart = yellowHueEnd = value;
	}
	
	/**
	 * Sets the hue border between green and blue. Should be between 0 and 360.
	 * @param value The new value of the border. Default is 150.
	 */
	public static void setGreenToBlueHue(int value) {
		blueHueStart = greenHueEnd = value;
	}
	
	
	/**
	 * The luma of the colour.
	 */
	private int luma;

	/**
	 * The chroma of the colour.
	 */
	private int chroma;
	/**
	 * The hue of the colour.
	 */
	private int hue;

	
	/**
	 * Converts the specified red-green-blue colour to LCHColour.
	 * @param c A red-green-blue colour to model the new LCHColour after.
	 */
	public LCHColour(Color c) {
		int [] lch = convertRGBtoLCh(c.getRed(), c.getGreen(), c.getBlue());
		luma = lch[0];
		chroma = lch[1];
		hue = lch[2];
	}
	
	/**
	 * Get the luma of the colour.
	 * @return The luma of the colour.
	 */
	public int getLuma() {
		return luma;
	}

	/**
	 * Set the luma of the colour.
	 * @param luma The luma of the colour.
	 */
	public void setLuma(int luma) {
		this.luma = luma;
	}

	/**
	 * Get the chroma of the colour.
	 * @return The chroma of the colour.
	 */
	public int getChroma() {
		return chroma;
	}

	/**
	 * Set the chroma of the colour.
	 * @param chroma The chroma of the colour.
	 */
	public void setChroma(int chroma) {
		this.chroma = chroma;
	}

	/**
	 * Get the hue of the colour.
	 * @return The hue of the colour.
	 */
	public int getHue() {
		return hue;
	}

	/**
	 * Set the hue of the colour.
	 * @param hue The hue of the colour.
	 */
	public void setHue(int hue) {
		this.hue = hue;
	}
	
	
	/**
	 * Converts a red-green-blue colour to our approximation of L*C*h* (lookup CIELAB).
	 * L* is taken from Wikipedia's entry for Luma:
	 * Y = 0.299 * R + 0.587 * G + 0.114 * B    or
	 * Y = 0.2126* R + 0.7152* G + 0.0722* B    or
	 * Y = 0.212 * R + 0.701 * G + 0.087 * B
	 * 
	 * The hue and chroma (h* and C*) are again taken from Wikipedia: 
	 * http://en.wikipedia.org/wiki/HSL_and_HSV#Hue_and_chroma
	 * @return A three integer array, where the first integer is the luma from
	 * 0 to 255, the second is the chroma from 0 to 255 and the third one is
	 * the hue from 0 to 360 
	 */
	private int[] convertRGBtoLCh(int red, int green, int blue) {
		int L = (2990 * red + 5870 * green + 1140 * blue) / 10000;
		
		double alpha = 0.5 * (2*red - green - blue);
		double beta = 0.866025404 * (green - blue);
		
		int C = (int) Math.sqrt(alpha * alpha + beta * beta);
		
		int h = (int) Math.toDegrees(Math.atan2(beta, alpha));
		if (h < 0)
			h = 360 + h;
		return new int [] {L, C, h};
	}
	
	
	/**
	 * Defines a list of colour classes, modelled after the colours on the pitch.
	 */
	public enum ColourClass {
		RED,
		GREEN_PLATE,
		GREEN_PITCH,
		BLUE,
		YELLOW,
		GRAY,
		UNKNOWN
	}
	
	/**
	 * Gets the class of a colour, depending on its (non-RGB) properties. 
	 * @return The class of the colour.
	 * @see ColourClass
	 */
	public ColourClass getColourClass() {
		return getClass(luma, chroma, hue);
	}
	
	/**
	 * Gets the class of a colour, depending on its (non-RGB) properties. 
	 * @param luma The luma of the colour.
	 * @param chroma The chroma of the colour.
	 * @param hue The hue of the colour.
	 * @return The class of the colour.
	 * @see ColourClass
	 */
	private ColourClass getClass(int luma, int chroma, int hue) {
		int ys = yellowScore();
		int bs = blueScore();
		int gpls = greenPlateScore();
		int gpis = greenPitchScore();
		int rs = redScore();
		int gs = grayScore();
		
		// If we are certain: [note that the order matters; yellow and green
		// are more likely to be categorized correctly; red and gray have 
		// probabilities in the guess so they should be last]
		if (ys == 3) {
			return ColourClass.YELLOW;
		} if (gpls == 3) {
			return ColourClass.GREEN_PLATE;
		} if (gpis == 3) {
			return ColourClass.GREEN_PITCH;
		} if (bs == 3) {
			return ColourClass.BLUE;
		} if (rs == 3) {
			return ColourClass.RED;
		} if (gs == 3) {
			return ColourClass.GRAY;
		} 
		
		// Less certain
		if (ys == 2) {
			return ColourClass.YELLOW;
		} if (gpls == 2) {
			return ColourClass.GREEN_PLATE;
		} if (gpis == 2) {
			return ColourClass.GREEN_PITCH;
		}
		return ColourClass.GREEN_PITCH;
	}
	
	
	/**
	 * Get the yellow score of the current colour. It is the number of 
	 * 'yellow' properties that the colour satisfies: being yellow in hue,
	 * having high luma and having low chroma. 
	 * @return The yellow score of the current colour.
	 */
	private int yellowScore() {
		int score = 0;
		score += hasYellowHue() ? 1 : 0;
		score += hasHighLuma() ? 1 : 0;
		score += hasLowChroma() ? 1 : 0;
		return score;
	}

	/**
	 * Get the blue score of the current colour. It is the number of 
	 * 'blue' properties that the colour satisfies: being blue in hue
	 * and having medium chroma. 
	 * @return The blue score of the current colour.
	 */
	private int blueScore() {
		int score = 1;
		score += hasBlueHue() ? 1 : 0;
		score += !hasLowLuma() ? 1 : 0;
		// Not adding random chance as the blue range is quite big. If we are out of it,
		// it is definitely not blue. (TODO: or is it?)
		return score;
	}

	/**
	 * Get the green-plate score of the current colour. It is the number of 
	 * 'green-plate' properties that the colour satisfies: being green in hue,
	 * having high luma and having high chroma. 
	 * @return The green-pitch score of the current colour.
	 */
	private int greenPlateScore() {
		int score = 0;
		score += hasGreenHue() ? 1 : 0;
		score += hasHighLuma() ? 1 : 0;
		score += hasHighChroma() ? 1 : 0;
		return score;
	}

	/**
	 * Get the green-pitch score of the current colour. It is the number of 
	 * 'green-pitch' properties that the colour satisfies: being green in hue,
	 * having medium luma and *not* having high chroma. 
	 * @return The green-pitch score of the current colour.
	 */
	private int greenPitchScore() {
		int score = 0;
		score += hasGreenHue() ? 1 : 0;
		score += !hasHighLuma() && !hasLowLuma() ? 1 : 0;
		score += !hasHighChroma() ? 1 : 0;
		return score;
	}

	/**
	 * Get the gray score of the current colour. It is the number of 
	 * 'gray' properties that the colour satisfies: having low luma 
	 * and having low chroma. If the score is 1 there is 50% chance that
	 * it spontaneously jumps to 2. This is since there are only two properties,
	 * and yes, it is not well justified.
	 * @return The gray score of the current colour.
	 */
	private int grayScore() {
		int score = 1;
		score += hasLowLuma() ? 1 : 0;
		score += hasLowChroma() ? 1 : 0;
		if (score == 1) {
			// If the pixel has chroma or luma that is not low enough 
			// (but not both), there is a 50% chance to classify the pixel 
			// as gray. 
			score += Math.random() >= 0.5 ? 1 : 0;
		}
		return score;
	}
	
	/**
	 * Get the red score of the current colour. It is the number of 
	 * 'red' properties that the colour satisfies: being red in hue
	 * and having low chroma. If the score is 1 there is 50% chance that
	 * it spontaneously jumps to 2. This is since there are only two properties,
	 * and yes, it is not well justified.
	 * @return The red score of the current colour.
	 */
	private int redScore() {
		int score = 1;
		score += hasRedHue() ? 1 : 0;
		score += hasHighChroma() ? 1 : 0;
		if (score == 1) {
			// If the pixel is either not red or has chroma that is not high 
			// enough (but not both), there is a 50% chance to classify the 
			// pixel as red. 
			score += Math.random() >= 0.5 ? 1 : 0;
		}
		return score;
	}
	
	
	/**
	 * Returns whether some hue is red or not.
	 * @param hue The hue level to check for being red or not.
	 * @return True if the given hue level is red, false otherwise.
	 */
	public boolean hasYellowHue() {
		return yellowHueStart <= hue && hue <= yellowHueEnd;
	}
		
	/**
	 * Returns whether some hue is red or not.
	 * @param hue The hue level to check for being red or not.
	 * @return True if the given hue level is red, false otherwise.
	 */
	public boolean hasRedHue() {
		// Note the or, instead of and.
		return redHueStart <= hue || hue < redHueEnd;
	}
	
	/**
	 * Returns whether some hue is blue or not.
	 * @param hue The hue level to check for being blue or not.
	 * @return True if the given hue level is blue, false otherwise.
	 */
	public boolean hasBlueHue() {
		return blueHueStart < hue && hue < blueHueEnd;
	}
	
	/**
	 * Returns whether some hue is green or not.
	 * @param hue The hue level to check for being green or not.
	 * @return True if the given hue level is green, false otherwise.
	 */
	public boolean hasGreenHue() {
		return greenHueStart < hue && hue <= greenHueEnd;
	}
	
	
	/**
	 * Returns whether some luma level is high or not.
	 * @param luma The luma level to check for being high or not.
	 * @return True if the given luma level is high, false otherwise.
	 */
	public boolean hasHighLuma() {
		return luma > HIGH_LUMA_THRESHOLD;
	}
	
	/**
	 * Returns whether some luma level is high or not.
	 * @param luma The luma level to check for being high or not.
	 * @return True if the given luma level is high, false otherwise.
	 */
	public boolean hasLowLuma() {
		return luma < LOW_LUMA_THRESHOLD;
	}
	
	/**
	 * Returns whether some chroma level is high or not.
	 * @param chroma The chroma level to check for being high or not.
	 * @return True if the given chroma level is high, false otherwise.
	 */
	public boolean hasHighChroma() {
		return chroma > HIGH_CHROMA_THRESHOLD;
	}
	
	/**
	 * Returns whether some chroma level is high or not.
	 * @param chroma The chroma level to check for being high or not.
	 * @return True if the given chroma level is high, false otherwise.
	 */
	public boolean hasLowChroma() {
		return chroma < LOW_CHROMA_THRESHOLD;
	}
}
