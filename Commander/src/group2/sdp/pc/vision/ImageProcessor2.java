package group2.sdp.pc.vision;

import group2.sdp.pc.vision.LCHColour.ColourClass;
import group2.sdp.pc.vision.skeleton.ImageConsumer;
import group2.sdp.pc.vision.skeleton.ImageProcessorSkeleton;
import group2.sdp.pc.vision.skeleton.StaticInfoConsumer;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Stan's image processing candy. Background extraction and colour detection.
 * Room for improvement: Pattern match the T-shapes and circles (red and gray)
 * and draw regular ones on top. 
 */
public class ImageProcessor2 extends ImageProcessorSkeleton {

	/**
	 * Shows whether the background has to be updated or not.
	 */
	private boolean extractBackground;
	
	/**
	 * An image of the background compare each frame with.
	 */
	private BufferedImage backgroundImage;
	
	/**
	 * New pixels in the last frame that was processed.
	 */
	private List<Point> newPixels;
	
	/**
	 * The rectangle that contains the whole pitch.
	 */
	private final Rectangle pitchCrop = new Rectangle(5, 80, 635 - 5, 400 - 80);
	
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
			extractBackground = false;
			// Note that the super.process(image) is not called.
			// This is the case, since we expect that the background
			// contains no objects worth of detection.
		} else {
			newPixels = getDifferentPixels(image);
			internalImage = drawPixels(image, newPixels);
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
		int minX = Math.max(pitchCrop.x, image.getMinX());
		int minY = Math.max(pitchCrop.y, image.getMinY());
		int w = Math.min(pitchCrop.width, image.getWidth());
		int h = Math.min(pitchCrop.height, image.getHeight());
		
		int maxX = minX + w;
		int maxY = minY + h;
		
		List<Point> result = new ArrayList<Point>(); 
		for (int y = minY; y < maxY; ++y) {
			for (int x = minX; x < maxX; ++x) {
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
		int threshold = 40;

		Color imagePixel = new Color(image.getRGB(x, y));
		Color backPixel = new Color(backgroundImage.getRGB(x, y));
		
		int [] delta = new int [] {
				Math.abs(imagePixel.getRed() - backPixel.getRed()),
				Math.abs(imagePixel.getGreen() - backPixel.getGreen()),
				Math.abs(imagePixel.getBlue() - backPixel.getBlue())
		};
		
		return delta[0] + delta[1] + delta[2] > threshold;
	}

	/**
	 * This function is supposed to be *slow*. Do not use apart from testing.
	 * @param pixels The pixels to draw on a new image.
	 * @return
	 */
	private BufferedImage drawPixels(BufferedImage image, List<Point> pixels) {
		int w = backgroundImage.getWidth();
		int h = backgroundImage.getHeight();
		BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		for (Point p : newPixels) {
			Color c = new Color(image.getRGB(p.x, p.y));
			ColourClass cc = new LCHColour(c).getColourClass();
			
			Color dc = null;
			switch (cc) {
			case RED:
				dc = Color.RED;
				break;
			case GREEN_PLATE:
				dc = Color.GREEN;
				break;
			case GREEN_PITCH:
				dc = Color.LIGHT_GRAY;
				break;
			case BLUE:
				dc = Color.BLUE;
				break;
			case YELLOW:
				dc = Color.YELLOW;
				break;
			case GRAY:
				dc = Color.DARK_GRAY;
				break;
			default:
				//System.out.println(c);
				dc = Color.CYAN;
				break;
			}
			result.setRGB(p.x, p.y, dc.getRGB());
		}
		return result;
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
