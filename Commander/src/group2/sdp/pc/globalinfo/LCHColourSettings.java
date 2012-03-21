package group2.sdp.pc.globalinfo;

import group2.sdp.pc.vision.LCHColour;

/**
 * Description: Information about the colour characteristics of camera output.
 *               Also contains two predefined static objects of itself, 
 *               representing the settings for the two pitches.
 * Contains:    Settings about six colour classes: Yellow T, Blue T, Ball, 
 *               Plate Green, Pitch Green, and Gray (walls and circle on plate).
 *               The settings are: Hue boundaries, Chroma boundaries, and Luma 
 *               boundaries.
 *
 *               Hue is the actual 'colour' of the colour: green, red, purple, 
 *               and so on. 
 *
 *               Chroma is the 'vividness' of the colour: e.g. grayish, or 
 *               alarming red.
 *
 *               Luma is the 'brightness' of the colour: white light is the 
 *               brightest, white sheet of paper is less bright, yellow is 
 *               bright, and blue is not.
 */
public class LCHColourSettings {
	// Package visibility - should be used only in Camera.
	/**
	 * Predefined colour settings for the camera on the first pitch. 
	 */
	static final LCHColourSettings ONE =
		new LCHColourSettings (
				/* plateHueStart = */ 80,
				/* plateHueEnd = */ 150,
                /* plateLumaStart = */ 90,
				/* plateLumaEnd = */ 255,
				/* plateChromaStart = */ 80,
				/* plateChromaEnd = */ 255,
				
				/* pitchHueStart = */ 80,
				/* pitchHueEnd = */ 150,
				/* pitchLumaStart = */ 90,
				/* pitchLumaEnd = */ 255,
				/* pitchChromaStart = */ 20,
				/* pitchChromaEnd = */ 80,
				
				/* blueHueStart = */ 150,
				/* blueHueEnd = */ 300,
				/* blueLumaStart = */ 60,
				/* blueLumaEnd = */ 255,
				/* blueChromaStart = */ 0,
				/* blueChromaEnd = */ 255,
				
// 				Note that the lower boundary is higher than the upper boundary. This is
// 				because the check is special here, since the region includes 300 - 360, 
// 				and 0 - 30.
				/* redHueStart = */ 300,
				/* redHueEnd = */ 30,
				/* redLumaStart = */ 0,
				/* redLumaEnd = */ 255,
				/* redChromaStart = */ 80,
				/* redChromaEnd = */ 255,

				/* yellowHueStart = */ 30,
				/* yellowHueEnd = */ 80,
				/* yellowLumaStart = */ 90,
				/* yellowLumaEnd = */ 255,
				/* yellowChromaStart = */ 0,
				/* yellowChromaEnd = */ 20,
				
				/* grayHueStart = */ 30,
				/* grayHueEnd = */ 80,
				/* grayLumaStart = */ 0,
				/* grayLumaEnd = */ 60,
				/* grayChromaStart = */ 0,
				/* grayChromaEnd = */ 20
		);
	/**
	 * Predefined colour settings for the camera on the second pitch. 
	 */
	// Package visibility - should be used only in Camera.	
	static final LCHColourSettings TWO =
		new LCHColourSettings (
				/* plateHueStart = */ 80,
				/* plateHueEnd = */ 150,
                /* plateLumaStart = */ 110,
				/* plateLumaEnd = */ 220,
				/* plateChromaStart = */ 70,
				/* plateChromaEnd = */ 150,
				
				/* pitchHueStart = */ 80,
				/* pitchHueEnd = */ 150,
				/* pitchLumaStart = */ 110,
				/* pitchLumaEnd = */ 220,
				/* pitchChromaStart = */ 0,
				/* pitchChromaEnd = */ 70,
				
				/* blueHueStart = */ 150,
				/* blueHueEnd = */ 300,
				/* blueLumaStart = */ 110,
				/* blueLumaEnd = */ 220,
				/* blueChromaStart = */ 0,
				/* blueChromaEnd = */ 150,
				
// 				Note that the lower boundary is higher than the upper boundary. This is
// 				because the check is special here, since the region includes 300 - 360, 
// 				and 0 - 30.
				/* redHueStart = */ 300,
				/* redHueEnd = */ 30,
				/* redLumaStart = */ 0,
				/* redLumaEnd = */ 220,
				/* redChromaStart = */ 70,
				/* redChromaEnd = */ 255,

				/* yellowHueStart = */ 30,
				/* yellowHueEnd = */ 80,
				/* yellowLumaStart = */ 220,
				/* yellowLumaEnd = */ 255,
				/* yellowChromaStart = */ 70,
				/* yellowChromaEnd = */ 255,
				
				/* grayHueStart = */ 30,
				/* grayHueEnd = */ 80,
				/* grayLumaStart = */ 0,
				/* grayLumaEnd = */ 110,
				/* grayChromaStart = */ 0,
				/* grayChromaEnd = */ 70
		);
	
	private int plateHueStart;
	private int plateHueEnd;
	private int plateLumaStart;
	private int plateLumaEnd;
	private int plateChromaStart;
	private int plateChromaEnd;
	
	private int pitchHueStart;
	private int pitchHueEnd;
	private int pitchLumaStart;
	private int pitchLumaEnd;
	private int pitchChromaStart;
	private int pitchChromaEnd;
	
	private int blueHueStart;
	private int blueHueEnd;
	private int blueLumaStart;
	private int blueLumaEnd;
	private int blueChromaStart;
	private int blueChromaEnd;
	
	private int redHueStart;
	private int redHueEnd;
	private int redLumaStart;
	private int redLumaEnd;
	private int redChromaStart;
	private int redChromaEnd;

	private int yellowHueStart;
	private int yellowHueEnd;
	private int yellowLumaStart;
	private int yellowLumaEnd;
	private int yellowChromaStart;
	private int yellowChromaEnd;
	
	private int grayHueStart;
	private int grayHueEnd;
	private int grayLumaStart;
	private int grayLumaEnd;
	private int grayChromaStart;
	private int grayChromaEnd;
	
	
	/** 
	 * Fully initialising constructor.
	 */
	public LCHColourSettings(int plateHueStart, int plateHueEnd,
			int plateLumaStart, int plateLumaEnd, int plateChromaStart,
			int plateChromaEnd, int pitchHueStart, int pitchHueEnd,
			int pitchLumaStart, int pitchLumaEnd, int pitchChromaStart,
			int pitchChromaEnd, int blueHueStart, int blueHueEnd,
			int blueLumaStart, int blueLumaEnd, int blueChromaStart,
			int blueChromaEnd, int redHueStart, int redHueEnd,
			int redLumaStart, int redLumaEnd, int redChromaStart,
			int redChromaEnd, int yellowHueStart, int yellowHueEnd,
			int yellowLumaStart, int yellowLumaEnd, int yellowChromaStart,
			int yellowChromaEnd, int grayHueStart, int grayHueEnd,
			int grayLumaStart, int grayLumaEnd, int grayChromaStart,
			int grayChromaEnd) {
		super();
		this.plateHueStart = plateHueStart;
		this.plateHueEnd = plateHueEnd;
		this.plateLumaStart = plateLumaStart;
		this.plateLumaEnd = plateLumaEnd;
		this.plateChromaStart = plateChromaStart;
		this.plateChromaEnd = plateChromaEnd;
		this.pitchHueStart = pitchHueStart;
		this.pitchHueEnd = pitchHueEnd;
		this.pitchLumaStart = pitchLumaStart;
		this.pitchLumaEnd = pitchLumaEnd;
		this.pitchChromaStart = pitchChromaStart;
		this.pitchChromaEnd = pitchChromaEnd;
		this.blueHueStart = blueHueStart;
		this.blueHueEnd = blueHueEnd;
		this.blueLumaStart = blueLumaStart;
		this.blueLumaEnd = blueLumaEnd;
		this.blueChromaStart = blueChromaStart;
		this.blueChromaEnd = blueChromaEnd;
		this.redHueStart = redHueStart;
		this.redHueEnd = redHueEnd;
		this.redLumaStart = redLumaStart;
		this.redLumaEnd = redLumaEnd;
		this.redChromaStart = redChromaStart;
		this.redChromaEnd = redChromaEnd;
		this.yellowHueStart = yellowHueStart;
		this.yellowHueEnd = yellowHueEnd;
		this.yellowLumaStart = yellowLumaStart;
		this.yellowLumaEnd = yellowLumaEnd;
		this.yellowChromaStart = yellowChromaStart;
		this.yellowChromaEnd = yellowChromaEnd;
		this.grayHueStart = grayHueStart;
		this.grayHueEnd = grayHueEnd;
		this.grayLumaStart = grayLumaStart;
		this.grayLumaEnd = grayLumaEnd;
		this.grayChromaStart = grayChromaStart;
		this.grayChromaEnd = grayChromaEnd;
	}
	
	

	
	/**
	 * Sets the hue border between blue and red. Should be between 0 and 360.
	 * @param value The new value of the border. Default is 300.
	 */
	public void setBlueToRedHue(int value) {
		redHueStart = blueHueEnd = value;
	}
	/**
	 * Sets the hue border between red and yellow. Should be between 0 and 360.
	 * @param value The new value of the border. Default is 30.
	 */
	public void setRedToYellowHue(int value) {
		yellowHueStart = redHueEnd = value;
	}
	/**
	 * Sets the hue border between yellow and green. Should be between 0 and 360.
	 * @param value The new value of the border. Default is 80.
	 */
	public void setYellowToGreenHue(int value) {
		plateHueStart = pitchHueStart = yellowHueEnd = value;
	}
	/**
	 * Sets the hue border between green and blue. Should be between 0 and 360.
	 * @param value The new value of the border. Default is 150.
	 */
	public void setGreenToBlueHue(int value) {
		blueHueStart = plateHueStart = pitchHueStart = value;
	}
	
	/**
	 * Gets the hue border between blue and red. Should be between 0 and 360.
	 * @return The value of the border.
	 */
	public int getBlueToRedHue() {
		return (redHueStart + blueHueEnd) / 2;
	}
	/**
	 * Gets the hue border between red and yellow. Should be between 0 and 360.
	 * @return The value of the border.
	 */
	public int getRedToYellowHue() {
		return (yellowHueStart + redHueEnd) / 2;
	}
	/**
	 * Gets the hue border between yellow and green. Should be between 0 and 360.
	 * @return The value of the border.
	 */
	public int getYellowToGreenHue() {
		return (plateHueStart + pitchHueStart + yellowHueEnd) / 3;
	}
	/**
	 * Gets the hue border between green and blue. Should be between 0 and 360.
	 * @return The value of the border.
	 */
	public int getGreenToBlueHue() {
		return (blueHueStart + plateHueEnd + pitchHueEnd) / 3;
	}
	
	
	/**
	 * Defines a list of colour classes, modelled after the colours on the pitch.
	 */
	public enum ColourClass {
		YELLOW,
		BLUE,
		GREEN_PLATE,
		GREEN_PITCH,
		RED,
		GRAY,
		UNKNOWN
	}
	
	
	/**
	 * Gets the class of a colour, depending on its (non-RGB) properties.
	 * @param c The colour to get the class of. 
	 * @return The class of the colour.
	 * @see ColourClass
	 */
	public ColourClass getColourClass(LCHColour c) {
		int ys = yellowScore(c);
		int bs = blueScore(c);
		int gpls = plateScore(c);
		int gpis = pitchScore(c);
		int rs = redScore(c);
		int gs = grayScore(c);
		
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
		}
		return ColourClass.UNKNOWN;
	}
	
	/**
	 * Get the yellow score of the current colour. It is the number of 
	 * 'yellow' properties that the colour satisfies: being yellow in hue,
	 * having appropriate luma and chroma. 
	 * @return The yellow score of the current colour.
	 */
	private int yellowScore(LCHColour c) {
		int score = 0;
		score += hasYellowHue(c) ? 1 : 0;
		score += hasYellowChroma(c) ? 1 : 0;
		score += hasYellowLuma(c) ? 1 : 0;
		return score;
	}

	/**
	 * Get the blue score of the current colour. It is the number of 
	 * 'blue' properties that the colour satisfies: being blue in hue
	 * and having appropriate luma and chroma.
	 * @return The blue score of the current colour.
	 */
	private int blueScore(LCHColour c) {
		int score = 0;
		score += hasBlueHue(c) ? 1 : 0;
		score += hasBlueLuma(c) ? 1 : 0;
		score += hasBlueChroma(c) ? 1 : 0;
		return score;
	}

	/**
	 * Get the green-plate score of the current colour. It is the number of 
	 * 'green-plate' properties that the colour satisfies: being green in hue,
	 * having appropriate luma and chroma.
	 * @return The green-pitch score of the current colour.
	 */
	private int plateScore(LCHColour c) {
		int score = 0;
		score += hasPlateHue(c) ? 1 : 0;
		score += hasPlateChroma(c) ? 1 : 0;
		score += hasPlateLuma(c) ? 1 : 0;
		return score;
	}

	/**
	 * Get the green-pitch score of the current colour. It is the number of 
	 * 'green-pitch' properties that the colour satisfies: being green in hue,
	 * having appropriate luma and chroma.
	 * @return The green-pitch score of the current colour.
	 */
	private int pitchScore(LCHColour c) {
		int score = 0;
		score += hasPitchHue(c) ? 1 : 0;
		score += hasPitchChroma(c) ? 1 : 0;
		score += hasPitchLuma(c) ? 1 : 0;
		return score;
	}

	/**
	 * Get the gray score of the current colour. It is the number of 
	 * 'gray' properties that the colour satisfies: having low luma 
	 * and having appropriate luma and chroma.
	 * @return The gray score of the current colour.
	 */
	private int grayScore(LCHColour c) {
		int score = 0;
		score += hasGrayHue(c) ? 1 : 0;
		score += hasGrayChroma(c) ? 1 : 0;
		score += hasGrayLuma(c) ? 1 : 0;
		return score;
	}
	
	/**
	 * Get the red score of the current colour. It is the number of 
	 * 'red' properties that the colour satisfies: being red in hue
	 * and having appropriate luma and chroma.
	 * @return The red score of the current colour.
	 */
	private int redScore(LCHColour c) {
		int score = 0;
		score += hasRedHue(c) ? 1 : 0;
		score += hasRedChroma(c) ? 1 : 0;
		score += hasRedLuma(c) ? 1 : 0;
		return score;
	}
	
	
	private boolean hasYellowHue(LCHColour c) {
		return yellowHueStart <= c.getHue() && c.getHue() <= yellowHueEnd;
	}
	private boolean hasYellowLuma(LCHColour c) {
		return yellowLumaStart <= c.getLuma() && c.getLuma() <= yellowLumaEnd;
	}
	private boolean hasYellowChroma(LCHColour c) {
		return yellowChromaStart <= c.getChroma() && c.getChroma() <= yellowChromaEnd;
	}
	
	private boolean hasRedHue(LCHColour c) {
		// Note the or, instead of and.
		return redHueStart <= c.getHue() || c.getHue() < redHueEnd;
	}	
	private boolean hasRedLuma(LCHColour c) {
		return redLumaStart <= c.getLuma() && c.getLuma() <= redLumaEnd;
	}
	private boolean hasRedChroma(LCHColour c) {
		return redChromaStart <= c.getChroma() && c.getChroma() <= redChromaEnd;
	}
	
	private boolean hasBlueHue(LCHColour c) {
		return blueHueStart < c.getHue() && c.getHue() < blueHueEnd;
	}
	private boolean hasBlueLuma(LCHColour c) {
		return blueLumaStart <= c.getLuma() && c.getLuma() <= blueLumaEnd;
	}
	private boolean hasBlueChroma(LCHColour c) {
		return blueChromaStart <= c.getChroma() && c.getChroma() <= blueChromaEnd;
	}
	
	private boolean hasPlateHue(LCHColour c) {
		return plateHueStart < c.getHue() && c.getHue() <= plateHueEnd;
	}
	private boolean hasPlateLuma(LCHColour c) {
		return plateLumaStart <= c.getLuma() && c.getLuma() <= plateLumaEnd;
	}
	private boolean hasPlateChroma(LCHColour c) {
		return plateChromaStart <= c.getChroma() && c.getChroma() <= plateChromaEnd;
	}
	
	private boolean hasPitchHue(LCHColour c) {
		return pitchHueStart < c.getHue() && c.getHue() <= pitchHueEnd;
	}
	private boolean hasPitchLuma(LCHColour c) {
		return pitchLumaStart <= c.getLuma() && c.getLuma() <= pitchLumaEnd;
	}
	private boolean hasPitchChroma(LCHColour c) {
		return pitchChromaStart <= c.getChroma() && c.getChroma() <= pitchChromaEnd;
	}
	
	private boolean hasGrayHue(LCHColour c) {
		return grayHueStart < c.getHue() && c.getHue() <= grayHueEnd;
	}
	private boolean hasGrayLuma(LCHColour c) {
		return grayLumaStart <= c.getLuma() && c.getLuma() <= grayLumaEnd;
	}
	private boolean hasGrayChroma(LCHColour c) {
		return grayChromaStart <= c.getChroma() && c.getChroma() <= grayChromaEnd;
	}
	
	
	public int getPlateHueStart() {
		return plateHueStart;
	}
	public int getPlateHueEnd() {
		return plateHueEnd;
	}
	public int getPlateLumaStart() {
		return plateLumaStart;
	}
	public int getPlateLumaEnd() {
		return plateLumaEnd;
	}
	public int getPlateChromaStart() {
		return plateChromaStart;
	}
	public int getPlateChromaEnd() {
		return plateChromaEnd;
	}

	public int getPitchHueStart() {
		return pitchHueStart;
	}
	public int getPitchHueEnd() {
		return pitchHueEnd;
	}
	public int getPitchLumaStart() {
		return pitchLumaStart;
	}
	public int getPitchLumaEnd() {
		return pitchLumaEnd;
	}
	public int getPitchChromaStart() {
		return pitchChromaStart;
	}
	public int getPitchChromaEnd() {
		return pitchChromaEnd;
	}
	
	public int getBlueHueStart() {
		return blueHueStart;
	}
	public int getBlueHueEnd() {
		return blueHueEnd;
	}
	public int getBlueLumaStart() {
		return blueLumaStart;
	}
	public int getBlueLumaEnd() {
		return blueLumaEnd;
	}
	public int getBlueChromaStart() {
		return blueChromaStart;
	}
	public int getBlueChromaEnd() {
		return blueChromaEnd;
	}

	public int getRedHueStart() {
		return redHueStart;
	}
	public int getRedHueEnd() {
		return redHueEnd;
	}
	public int getRedLumaStart() {
		return redLumaStart;
	}
	public int getRedLumaEnd() {
		return redLumaEnd;
	}
	public int getRedChromaStart() {
		return redChromaStart;
	}
	public int getRedChromaEnd() {
		return redChromaEnd;
	}
	
	public int getYellowHueStart() {
		return yellowHueStart;
	}
	public int getYellowHueEnd() {
		return yellowHueEnd;
	}
	public int getYellowLumaStart() {
		return yellowLumaStart;
	}
	public int getYellowLumaEnd() {
		return yellowLumaEnd;
	}
	public int getYellowChromaStart() {
		return yellowChromaStart;
	}
	public int getYellowChromaEnd() {
		return yellowChromaEnd;
	}

	public int getGrayHueStart() {
		return grayHueStart;
	}
	public int getGrayHueEnd() {
		return grayHueEnd;
	}
	public int getGrayLumaStart() {
		return grayLumaStart;
	}
	public int getGrayLumaEnd() {
		return grayLumaEnd;
	}
	public int getGrayChromaStart() {
		return grayChromaStart;
	}
	public int getGrayChromaEnd() {
		return grayChromaEnd;
	}


	
	
	public void setPlateHueStart(int plateHueStart) {
		this.plateHueStart = plateHueStart;
	}


	public void setPlateHueEnd(int plateHueEnd) {
		this.plateHueEnd = plateHueEnd;
	}


	public void setPlateLumaStart(int plateLumaStart) {
		this.plateLumaStart = plateLumaStart;
	}


	public void setPlateLumaEnd(int plateLumaEnd) {
		this.plateLumaEnd = plateLumaEnd;
	}


	public void setPlateChromaStart(int plateChromaStart) {
		this.plateChromaStart = plateChromaStart;
	}


	public void setPlateChromaEnd(int plateChromaEnd) {
		this.plateChromaEnd = plateChromaEnd;
	}


	
	public void setPitchHueStart(int pitchHueStart) {
		this.pitchHueStart = pitchHueStart;
	}


	public void setPitchHueEnd(int pitchHueEnd) {
		this.pitchHueEnd = pitchHueEnd;
	}


	public void setPitchLumaStart(int pitchLumaStart) {
		this.pitchLumaStart = pitchLumaStart;
	}


	public void setPitchLumaEnd(int pitchLumaEnd) {
		this.pitchLumaEnd = pitchLumaEnd;
	}


	public void setPitchChromaStart(int pitchChromaStart) {
		this.pitchChromaStart = pitchChromaStart;
	}


	public void setPitchChromaEnd(int pitchChromaEnd) {
		this.pitchChromaEnd = pitchChromaEnd;
	}


	
	public void setBlueHueStart(int blueHueStart) {
		this.blueHueStart = blueHueStart;
	}


	public void setBlueHueEnd(int blueHueEnd) {
		this.blueHueEnd = blueHueEnd;
	}


	public void setBlueLumaStart(int blueLumaStart) {
		this.blueLumaStart = blueLumaStart;
	}


	public void setBlueLumaEnd(int blueLumaEnd) {
		this.blueLumaEnd = blueLumaEnd;
	}


	public void setBlueChromaStart(int blueChromaStart) {
		this.blueChromaStart = blueChromaStart;
	}


	public void setBlueChromaEnd(int blueChromaEnd) {
		this.blueChromaEnd = blueChromaEnd;
	}


	
	public void setRedHueStart(int redHueStart) {
		this.redHueStart = redHueStart;
	}


	public void setRedHueEnd(int redHueEnd) {
		this.redHueEnd = redHueEnd;
	}


	public void setRedLumaStart(int redLumaStart) {
		this.redLumaStart = redLumaStart;
	}


	public void setRedLumaEnd(int redLumaEnd) {
		this.redLumaEnd = redLumaEnd;
	}


	public void setRedChromaStart(int redChromaStart) {
		this.redChromaStart = redChromaStart;
	}


	public void setRedChromaEnd(int redChromaEnd) {
		this.redChromaEnd = redChromaEnd;
	}


	
	public void setYellowHueStart(int yellowHueStart) {
		this.yellowHueStart = yellowHueStart;
	}


	public void setYellowHueEnd(int yellowHueEnd) {
		this.yellowHueEnd = yellowHueEnd;
	}


	public void setYellowLumaStart(int yellowLumaStart) {
		this.yellowLumaStart = yellowLumaStart;
	}


	public void setYellowLumaEnd(int yellowLumaEnd) {
		this.yellowLumaEnd = yellowLumaEnd;
	}


	public void setYellowChromaStart(int yellowChromaStart) {
		this.yellowChromaStart = yellowChromaStart;
	}


	public void setYellowChromaEnd(int yellowChromaEnd) {
		this.yellowChromaEnd = yellowChromaEnd;
	}


	
	public void setGrayHueStart(int grayHueStart) {
		this.grayHueStart = grayHueStart;
	}


	public void setGrayHueEnd(int grayHueEnd) {
		this.grayHueEnd = grayHueEnd;
	}


	public void setGrayLumaStart(int grayLumaStart) {
		this.grayLumaStart = grayLumaStart;
	}


	public void setGrayLumaEnd(int grayLumaEnd) {
		this.grayLumaEnd = grayLumaEnd;
	}


	public void setGrayChromaStart(int grayChromaStart) {
		this.grayChromaStart = grayChromaStart;
	}


	public void setGrayChromaEnd(int grayChromaEnd) {
		this.grayChromaEnd = grayChromaEnd;
	}
}