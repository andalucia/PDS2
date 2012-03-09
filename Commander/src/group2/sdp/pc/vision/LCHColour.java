package group2.sdp.pc.vision;

import java.awt.Color;

/**
 * A luma-chroma-hue colour. The definitions of the terms can be found here:
 * http://en.wikipedia.org/wiki/Luma_%28video%29#Rec._601_luma_versus_Rec._709_luma_coefficients
 * http://en.wikipedia.org/wiki/HSL_and_HSV#Hue_and_chroma
 */
public class LCHColour {
	
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
}
