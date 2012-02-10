package group2.sdp.pc.vision;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.management.ImmutableDescriptor;

import group2.sdp.pc.vision.skeleton.ImageProcessorSkeleton;
import group2.sdp.pc.vision.skeleton.StaticInfoConsumer;

public class ImageProcessor2 extends ImageProcessorSkeleton {

	/**
	 * Shows whether the background has to be updated or not.
	 */
	private boolean extractBackground;
	/**
	 * An image of the background to subtract from each frame.
	 */
	private BufferedImage backgroundImage;
	
	
	/**
	 * See parent's comment.
	 */
	public ImageProcessor2(StaticInfoConsumer consumer) {
		super(consumer);
		extractBackground = true;
	}
	
	/**
	 * Accepts a background image in addition to a consumer. 
	 * @param backgroundImage An image of the background.
	 */
	public ImageProcessor2(StaticInfoConsumer consumer, BufferedImage backgroundImage) {
		super(consumer);
		this.backgroundImage = backgroundImage;
	}

	
	@Override
	/**
	 * In addition to processing the frame extracts the background if needed.
	 */
	public void process(BufferedImage image) {
		if (extractBackground) {
			backgroundImage = image;
			extractBackground = false;
			// Note that the super.process(image) is not called.
			// This is the case, since we expect that the background
			// contains no objects worth of detection.
		} else {
			super.process(image);			
		}
	}

	@Override
	protected Point2D extractBallPosition(BufferedImage image) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Point2D extractRobotPosition(BufferedImage image, boolean yellow) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected double extractRobotFacingDirection(BufferedImage image,
			boolean yellow) {
		// TODO Auto-generated method stub
		return 0;
	}

}
