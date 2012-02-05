package group2.sdp.pc.vision.skeleton;

import java.awt.image.BufferedImage;

/**
 * Needed to abstract away the difference between ImageProcessor and ImagePreviewer,
 * as now the ImageGrabber does not care which one you will give to it.
 */
public interface ImageConsumer {

	/**
	 * Consumes the given image, i.e. does stuff with it!
	 * @param image The image to consume.
	 */
	public void consume(BufferedImage image);
}
