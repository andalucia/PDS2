package group2.sdp.pc.globalinfo;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Description: Non-changing information about a pitch.
 * Contains:    Physical dimensions (minimum enclosing rectangle), goal posts,
 *               camera settings for the camera above the pitch. Coordinates are 
 *               in centimetres, the origin is the centre of the pitch, the X 
 *               coordinate grows from left to right and the Y coordinate grows 
 *               from bottom to top.
 */
public class Pitch {
	/**
	 * Predefined settings for pitch one.
	 */
	public static Pitch ONE = 
		new Pitch(
				new Rectangle2D.Float(-122, -60.5f, 244, 121),
				30.25f, /* top goal post */
				-30.25f, /* bottom goal post */
				Camera.ONE
		);
	/**
	 * Predefined settings for pitch two.
	 */
	public static Pitch TWO = 
		new Pitch(
				new Rectangle2D.Float(-122, -60.5f, 244, 121),
				30.25f, /* top goal post */
				-30.25f, /* bottom goal post */
				Camera.TWO
		);
	
	/**
	 * The boundaries of the minimum enclosing rectangle of the pitch. 
	 * Coordinates are in centimetres, the origin is the centre of the pitch, 
	 * the X coordinate grows from left to right and the Y coordinate grows 
	 * from bottom to top.
	 */
	private Rectangle2D minimumEnclosingRectangle;
	
	/**
	 * The Y coordinate of the top goal post. This should be the same for both
	 * goals. Coordinates are in centimetres, the origin is the centre of the 
	 * pitch, and the Y coordinate grows from bottom to top.
	 */
	private float topGoalPostYCoordinate;
	
	/**
	 * The Y coordinate of the bottom goal post. This should be the same for 
	 * both goals. Coordinates are in centimetres, the origin is the centre of 
	 * the pitch, and the Y coordinate grows from bottom to top.
	 */
	private float bottomGoalPostYCoordinate;

	/**
	 * Camera settings for the camera above the pitch.
	 */
	private Camera camera;

	
	/**
	 * Fully initialising constructor. 
	 * @param minimumEnclosingRectangle The boundaries of the minimum enclosing
	 * rectangle of the pitch. Coordinates are in centimetres, the origin is 
	 * the centre of the pitch, the X coordinate grows from left to right and 
	 * the Y coordinate grows from bottom to top.
	 * @param topGoalPostYCoordinate The Y coordinate of the top goal post. 
	 * This should be the same for both goals. Coordinates are in centimetres, 
	 * the origin is the centre of the pitch, and the Y coordinate grows from 
	 * bottom to top.
	 * @param bottomGoalPostYCoordinate The Y coordinate of the bottom goal 
	 * post. This should be the same for both goals. Coordinates are in 
	 * centimetres, the origin is the centre of the pitch, and the Y coordinate
	 * grows from bottom to top.
	 * @param camera Camera settings for the camera above the pitch.
	 */
	public Pitch(Rectangle2D minimumEnclosingRectangle,
			float topGoalPostYCoordinate, float bottomGoalPostYCoordinate,
			Camera camera) {
		super();
		this.minimumEnclosingRectangle = minimumEnclosingRectangle;
		this.topGoalPostYCoordinate = topGoalPostYCoordinate;
		this.bottomGoalPostYCoordinate = bottomGoalPostYCoordinate;
		this.camera = camera;
	}

	
	/**
	 * Get the boundaries of the minimum enclosing rectangle of the pitch. 
	 * Coordinates are in centimetres, the origin is the centre of the pitch, 
	 * the X coordinate grows from left to right and the Y coordinate grows 
	 * from bottom to top. 
	 * @return The boundaries of the minimum enclosing rectangle of the pitch. 
	 * Coordinates are in centimetres, the origin is the centre of the pitch, 
	 * the X coordinate grows from left to right and the Y coordinate grows 
	 * from bottom to top.
	 */
	public Rectangle2D getMinimumEnclosingRectangle() {
		return minimumEnclosingRectangle;
	}

	/**
	 * Set the boundaries of the minimum enclosing rectangle of the pitch. 
	 * Coordinates are in centimetres, the origin is the centre of the pitch, 
	 * the X coordinate grows from left to right and the Y coordinate grows 
	 * from bottom to top.
	 * @param minimumEnclosingRectangle The boundaries of the minimum enclosing
	 * rectangle of the pitch. Coordinates are in centimetres, the origin is 
	 * the centre of the pitch, the X coordinate grows from left to right and 
	 * the Y coordinate grows from bottom to top.
	 */
	public void setMinimumEnclosingRectangle(Rectangle2D minimumEnclosingRectangle) {
		this.minimumEnclosingRectangle = minimumEnclosingRectangle;
	}

	/**
	 * Get the Y coordinate of the top goal post. This should be the same for 
	 * both goals. Coordinates are in centimetres, the origin is the centre of 
	 * the pitch, and the Y coordinate grows from bottom to top. 
	 * @return The Y coordinate of the top goal post. This should be the same 
	 * for both goals. Coordinates are in centimetres, the origin is the centre
	 * of the pitch, and the Y coordinate grows from bottom to top.
	 */
	public float getTopGoalPostYCoordinate() {
		return topGoalPostYCoordinate;
	}

	/**
	 * Set the Y coordinate of the top goal post. This should be the same for 
	 * both goals. Coordinates are in centimetres, the origin is the centre of 
	 * the pitch, and the Y coordinate grows from bottom to top.
	 * @param topGoalPostYCoordinate The Y coordinate of the top goal post. 
	 * This should be the same for both goals. Coordinates are in centimetres, 
	 * the origin is the centre of the pitch, and the Y coordinate grows from 
	 * bottom to top.
	 */
	public void setTopGoalPostYCoordinate(float topGoalPostYCoordinate) {
		this.topGoalPostYCoordinate = topGoalPostYCoordinate;
	}

	/**
	 * Get the Y coordinate of the bottom goal post. This should be the same for 
	 * both goals. Coordinates are in centimetres, the origin is the centre of 
	 * the pitch, and the Y coordinate grows from bottom to bottom. 
	 * @return The Y coordinate of the bottom goal post. This should be the same 
	 * for both goals. Coordinates are in centimetres, the origin is the centre
	 * of the pitch, and the Y coordinate grows from bottom to bottom.
	 */
	public float getBottomGoalPostYCoordinate() {
		return bottomGoalPostYCoordinate;
	}

	/**
	 * Set the Y coordinate of the bottom goal post. This should be the same for 
	 * both goals. Coordinates are in centimetres, the origin is the centre of 
	 * the pitch, and the Y coordinate grows from bottom to bottom.
	 * @param bottomGoalPostYCoordinate The Y coordinate of the bottom goal post. 
	 * This should be the same for both goals. Coordinates are in centimetres, 
	 * the origin is the centre of the pitch, and the Y coordinate grows from 
	 * bottom to bottom.
	 */
	public void setBottomGoalPostYCoordinate(float bottomGoalPostYCoordinate) {
		this.bottomGoalPostYCoordinate = bottomGoalPostYCoordinate;
	}

	/**
	 * Get the camera settings for the camera above the pitch.
	 * @return The camera settings for the camera above the pitch.
	 */
	public Camera getCamera() {
		return camera;
	}
	
	/**
	 * Set the camera settings for the camera above the pitch.
	 * @param camera The camera settings for the camera above the pitch.
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	/**
	 * Get the middle Y coordinate of the goal posts.
	 * @return The middle Y coordinate of the goal posts.
	 */
	public double getGoalCentreY() {
		return (getBottomGoalPostYCoordinate() + getTopGoalPostYCoordinate()) / 2;
	}	
}
