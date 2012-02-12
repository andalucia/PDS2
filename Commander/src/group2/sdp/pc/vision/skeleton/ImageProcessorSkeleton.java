package group2.sdp.pc.vision.skeleton;

import group2.sdp.pc.breadbin.StaticBallInfo;
import group2.sdp.pc.breadbin.StaticPitchInfo;
import group2.sdp.pc.breadbin.StaticRobotInfo;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * An abstract implementation of the class processing image data and producing static pitch info.
 */
public abstract class ImageProcessorSkeleton implements ImageConsumer {
	
	/**
	 * The object that is going to consume the output of the Vision class.
	 */
	private StaticInfoConsumer staticInfoConsumer;
	
	private ImageConsumer imageConsumer;
	protected BufferedImage internalImage;
	
	
	/**
	 * Is Alfie's T yellow?
	 */
	private boolean yellowAlfie;

	/**
	 * A constructor that takes the object that is going to consume the output.
	 * @param consumer The object that is going to consume the output.
	 */
	public ImageProcessorSkeleton (StaticInfoConsumer consumer) {
		this.staticInfoConsumer = consumer;
	}
	
	public ImageProcessorSkeleton (StaticInfoConsumer consumer, ImageConsumer imageConsumer) {
		this.staticInfoConsumer = consumer;
		this.imageConsumer = imageConsumer;
	}
	
	/**
	 * See parent's comment.
	 * Note: If you want to override this, then you want to override process.
	 */
	@Override
	public final void consume (BufferedImage image) {
		internalImage = null;
		process(image);
	}
	
	/**
	 * Call this method to process a frame of video. It calls the consumer's 
	 * consumeInfo() method.
	 * @param image The image to process.
	 */
	public void process (BufferedImage image) {
		// Do processing here
		
		long time = System.currentTimeMillis();
		Point2D ballPosition = extractBallPosition(image);
		StaticBallInfo ballInfo = new StaticBallInfo(ballPosition,time);
		
		Point2D alfiePosition = extractAlfiePosition(image);
		double alfieFacingDirection = extractAlfieFacingDirection(image);
		StaticRobotInfo alfieInfo = new StaticRobotInfo(alfiePosition, alfieFacingDirection, true,time);
		
		Point2D opponentPosition = extractOpponentPosition(image);
		double opponentFacingDirection = extractOpponentFacingDirection(image);
		StaticRobotInfo opponentInfo = new StaticRobotInfo(opponentPosition, opponentFacingDirection, false,time);
		
		StaticPitchInfo spi = new StaticPitchInfo(ballInfo, alfieInfo, opponentInfo);
		//spi.printAllStaticInfo();
		
		if (internalImage == null) {
			internalImage = image;
		}
		if (imageConsumer != null) {
			imageConsumer.consume(internalImage);
		}
		if (staticInfoConsumer != null) {
			staticInfoConsumer.consumeInfo(spi);
		}
	}

	
	/**
	 * Extracts the ball position from a given image.
	 * @param image The image to use to extract the ball position.
	 * @return The position of the ball in cm, w.r.t. the centre of the pitch.
	 */
	protected abstract  Point2D extractBallPosition(BufferedImage image);
	
	/**
	 * Extracts the position of Alfie.
	 * @param image The image to use to extract the position of Alfie.
	 * @return The position of Alfie in cm w.r.t. the centre of the pitch.
	 */
	private Point2D extractAlfiePosition(BufferedImage image) {
		return extractRobotPosition(image, yellowAlfie);
	}
	
	/**
	 * Extracts the direction in which Alfie is facing.
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @param image The image to use to extract the direction in which Alfie is facing.
	 * @return The the direction in which Alfie is facing.
	 */
	private double extractAlfieFacingDirection(BufferedImage image) {
		return extractRobotFacingDirection(image, yellowAlfie);
	}

	/**
	 * Extracts the position of Alfie's opponent.
	 * @param image The image to use to extract the position of Alfie's opponent.
	 * @return The position of Alfie's opponent in cm w.r.t. the centre of the pitch.
	 */
	private Point2D extractOpponentPosition(BufferedImage image) {
		return extractRobotPosition(image, !yellowAlfie);
	}
	
	/**
	 * Extracts the direction in which Alfie's opponent is facing.
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @param image The image to use to extract the direction in which Alfie's opponent is facing.
	 * @return The the direction in which Alfie's opponent is facing.
	 */
	private double extractOpponentFacingDirection(BufferedImage image) {
		return extractRobotFacingDirection(image, !yellowAlfie);
	}

	
	/**
	 * Extracts the position of the specified robot.
	 * @param image The image to use to extract the position of the specified robot.
	 * @param yellow Should the we look for the yellow robot or not (false means blue robot).
	 * @return The position of the specified robot.
	 */
	protected abstract Point2D extractRobotPosition(BufferedImage image, boolean yellow);

	/**
	 * Extracts the direction in which the specified robot is facing.
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @param image The image to use to extract the direction in which the specified robot is facing.
	 * @param yellow Should the we look for the yellow robot or not (false means blue robot).
	 * @return The the direction in which the specified robot is facing.
	 */
	protected abstract double extractRobotFacingDirection(BufferedImage image, boolean yellow);
}
