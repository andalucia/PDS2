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
//				new Rectangle(17, 123, 597-17, 424-123),
//				LCHColourSettings.ONE,
//				248,
//				60
				new Rectangle(8, 87, 624 - 10, 441 - 87),
				LCHColourSettings.ONE, 
				248.0,
				60
		);
	// Package visibility - should be used only in Pitch
	/**
	 * Predefined information about the second camera.
	 */
	final static Camera TWO = 
		new Camera(
				new Rectangle(53, 100, 592 - 53, 383 - 100),
				LCHColourSettings.TWO, 
				248.0,
				90
		);
	
	/**
	 * The distance, in centimetres, of the camera from the pitch.
	 */
	private double distanceFromPitch;
	
	/**
	 * The minimum visual crop that encloses the pitch below the camera.
	 */
	private Rectangle pitchCrop;
	
	/**
	 * The colour settings of the camera.
	 */
	private LCHColourSettings colourSettings;

	/**
	 * The threshold after which the difference between two pixels is 
	 * considered significant.
	 */
	private int pixelDifferenceThreshold;
	
	/**
	 * Fully initialising constructor.
	 * @param pitchCrop The minimum visual crop that encloses the pitch below the camera.
	 * @param colourSettings The colour settings of the camera.
	 * @param distanceFromPitch The distance of the camera from the pitch.
	 */
	public Camera(Rectangle pitchCrop, LCHColourSettings colourSettings, double distanceFromPitch, int pixelDifferenceThreshold) {
		super();
		this.pitchCrop = pitchCrop;
		this.colourSettings = colourSettings;
		this.setDistanceFromPitch(distanceFromPitch);
		this.setPixelDifferenceThreshold(pixelDifferenceThreshold);
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

	/**
	 * Set the distance, in centimetres, of the camera from the pitch.
	 * @param distanceFromPitch The distance, in centimetres, of the camera from the pitch.
	 */
	public void setDistanceFromPitch(double distanceFromPitch) {
		this.distanceFromPitch = distanceFromPitch;
	}

	/**
	 * Get the distance, in centimetres, of the camera from the pitch.
	 * @return The distance, in centimetres, of the camera from the pitch.
	 */
	public double getDistanceFromPitch() {
		return distanceFromPitch;
	}

	/**
	 * Set the threshold after which the difference between two pixels is 
	 * considered significant.
	 * @param pixelDifferenceThreshold The threshold after which the difference
	 * between two pixels is considered significant.
	 */
	public void setPixelDifferenceThreshold(int pixelDifferenceThreshold) {
		this.pixelDifferenceThreshold = pixelDifferenceThreshold;
	}

	/**
	 * Get the threshold after which the difference between two pixels is 
	 * considered significant. 
	 * @return The threshold after which the difference between two pixels is 
	 * considered significant.
	 */
	public int getPixelDifferenceThreshold() {
		return pixelDifferenceThreshold;
	}
}
