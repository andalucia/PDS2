package group2.sdp.pc.vision;

import group2.sdp.pc.vision.skeleton.ImageConsumer;
import group2.sdp.pc.vision.skeleton.ImageProcessorSkeleton;
import group2.sdp.pc.vision.skeleton.StaticInfoConsumer;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessor2 extends ImageProcessorSkeleton {

	/**
	 * Shows whether the background has to be updated or not.
	 */
	private boolean extractBackground;
	
	/**
	 * An image of the background compare each frame with.
	 */
	private BufferedImage backgroundImage;
	
	private List<Point> newPixels;
	
	/**
	 * See parent's comment.
	 */
	public ImageProcessor2(StaticInfoConsumer consumer) {
		super(consumer);
		extractBackground = true;
		newPixels = new ArrayList<Point> ();
	}
	/**
	 * See parent's comment.
	 */
	public ImageProcessor2(StaticInfoConsumer consumer, ImageConsumer imageConsumer) {
		super(consumer, imageConsumer);
		extractBackground = true;
		newPixels = new ArrayList<Point> ();
	}	
	/**
	 * Accepts a background image in addition to a consumer. 
	 * @param backgroundImage An image of the background.
	 */
	public ImageProcessor2(StaticInfoConsumer consumer, BufferedImage backgroundImage) {
		this(consumer);
		this.backgroundImage = backgroundImage;
		extractBackground = false;
	}
	/**
	 * Accepts a background image in addition to consumers.
	 * @param backgroundImage An image of the background.
	 */
	public ImageProcessor2(StaticInfoConsumer consumer, ImageConsumer imageConsumer, 
			BufferedImage backgroundImage) {
		this(consumer, imageConsumer);
		this.backgroundImage = backgroundImage;
		extractBackground = false;
	}

	
	/**
	 * In addition to processing the frame extracts the background if needed.
	 */
	@Override
	public void process(BufferedImage image) {
		if (extractBackground) {
			backgroundImage = image;
			System.out.println(backgroundImage != null);
			extractBackground = false;
			// Note that the super.process(image) is not called.
			// This is the case, since we expect that the background
			// contains no objects worth of detection.
		} else {
			newPixels = getDifferentPixels(image);
			internalImage = drawPixels(newPixels);
			super.process(image);			
		}
	}

	/**
	 * The pixels that are sufficiently different from the background are 
	 * added to a list that is returned.
	 * @param image The image to subtract from the background image.
	 * @return A list of the different points.
	 * @see isDifferent
	 */
	private List<Point> getDifferentPixels(BufferedImage image) {
		int maxX = image.getMinX() - image.getWidth();
		int maxY = image.getMinY() - image.getWidth();
		
		List<Point> result = new ArrayList<Point>(); 
		for (int x = image.getMinX(); x < maxX; ++x) {
			for (int y = image.getMinY(); y < maxY; ++y) {
				if (isDifferent(image, x, y)) {
					result.add(new Point(x, y));
				}
			}
		}
		return result;
	}

	/**
	 * Compares if the given pixel in the given image is different from the 
	 * corresponding pixel in the background image. The current implementation
	 * subtracts the background from the given image and checks if the result 
	 * passes a certain threshold.
	 * @param image The pixel to compare is taken from this picture.
	 * @param x The x coordinate of the pixel.
	 * @param y The y coordinate of the pixel.
	 * @return True if the specified pixel is different, false otherwise.
	 */
	private boolean isDifferent(BufferedImage image, int x, int y) {
		int threshold = 50;
//		image.getRGB(x, y) - backgroundImage.getRGB(x, y);
		Color imagePixel = new Color(image.getRGB(x, y));
		Color backPixel = new Color(backgroundImage.getRGB(x, y));
		
		int [] delta = new int [] {
				imagePixel.getRed() - backPixel.getRed(),
				imagePixel.getGreen() - backPixel.getGreen(),
				imagePixel.getBlue() - backPixel.getBlue()
		};
		
		return delta[0] + delta[1] + delta[2] > threshold;
	}

	/**
	 * This function is supposed to be *slow*. Do not use apart from testing.
	 * @param newPixels2
	 * @return
	 */
	private BufferedImage drawPixels(List<Point> newPixels) {
		int w = backgroundImage.getWidth();
		int h = backgroundImage.getHeight();
		BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for (Point p : newPixels) {
			result.setRGB(p.x, p.y, Color.WHITE.getRGB());
		}
		return result;
	}
	
	private int[] convertRGBtoLCh(int red, int green, int blue) {
		return null;
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
