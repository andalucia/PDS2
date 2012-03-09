package group2.sdp.pc.globalinfo;

import java.awt.Rectangle;

/**
 * Description: Information about a camera above a pitch. Also contains two 
 *               predefined static objects of itself, representing the two 
 *               cameras we have.
 * Contains:    The minimum visual crop that encloses the pitch, and colour 
 *               characteristics for the camera.
 */
public class Camera {
	
	// Package visibility - should be used only in Pitch.
	/**
	 * Predefined information about the first camera.
	 */
	final static Camera ONE = 
		new Camera(
				new Rectangle(10, 58, 630-10, 421-58),
				LCHColourSettings.ONE
		);
	// Package visibility - should be used only in Pitch
	/**
	 * Predefined information about the second camera.
	 */
	final static Camera TWO = 
		new Camera(
				new Rectangle(53, 100, 592 - 53, 383 - 100),
				LCHColourSettings.TWO
		);
	
	/**
	 * The minimum visual crop that encloses the pitch below the camera.
	 */
	private Rectangle pitchCrop;
	
	/**
	 * The colour settings of the camera.
	 */
	private LCHColourSettings colourSettings;

	
	/**
	 * Fully initialising constructor.
	 * @param pitchCrop The minimum visual crop that encloses the pitch below the camera.
	 * @param colourSettings The colour settings of the camera.
	 */
	public Camera(Rectangle pitchCrop, LCHColourSettings colourSettings) {
		super();
		this.pitchCrop = pitchCrop;
		this.colourSettings = colourSettings;
	}

	
	/**
	 * Get the minimum visual crop that encloses the pitch below the camera.
	 * @return The minimum visual crop that encloses the pitch below the 
	 * camera.
	 */
	public Rectangle getPitchCrop() {
		return pitchCrop;
	}

	/** 
	 * Set the minimum visual crop that encloses the pitch below the camera.
	 * @param pitchCrop The minimum visual crop that encloses the pitch below 
	 * the camera.
	 */
	public void setPitchCrop(Rectangle pitchCrop) {
		this.pitchCrop = pitchCrop;
	}

	/**
	 * Get the colour settings of the camera.
	 * @return The colour settings of the camera.
	 */
	public LCHColourSettings getColourSettings() {
		return colourSettings;
	}

	/**
	 * Set the colour settings of the camera.
	 * @param colourSettings The colour settings of the camera.
	 */
	public void setColourSettings(LCHColourSettings colourSettings) {
		this.colourSettings = colourSettings;
	}
}
