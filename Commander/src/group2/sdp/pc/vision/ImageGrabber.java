package group2.sdp.pc.vision;

import java.util.List;
import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.Control;
import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.StateException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

public class ImageGrabber implements CaptureCallback {
	
	private static int width = 640, height = 480;
	private static int std = V4L4JConstants.STANDARD_WEBCAM, channel = 0;
	private static String   device = "/dev/video0";
	
	private static final int SATURATION = 100;
	private static final int BRIGHTNESS = 128;
	private static final int CONTRAST = 64;
	private static final int HUE = 0;

	private static final boolean VERBOSE = false;
	
	private VideoDevice     videoDevice;
	private FrameGrabber    frameGrabber;
	
	public ImageGrabber(){
		// Initialise video device and frame grabber
		try {
			initFrameGrabber();
		} catch (V4L4JException e1) {
			System.err.println("Error setting up capture");
			e1.printStackTrace();

			// cleanup and exit
			cleanupCapture();
			return;
		}

		// start capture
		try {
			frameGrabber.startCapture();
		} catch (V4L4JException e){
			System.err.println("Error starting the capture");
			e.printStackTrace();
		}
	}

	/**
	 * Initialises the FrameGrabber object
	 * @throws V4L4JException if any parameter if invalid
	 */
	private void initFrameGrabber() throws V4L4JException{
		videoDevice = new VideoDevice(device);
		try {
			List<Control> controls = videoDevice.getControlList().getList();
			System.out.println("Got list");
			for(Control c: controls) {
				if(c.getName().equals("Contrast"))
					c.setValue(CONTRAST);
				if(c.getName().equals("Brightness"))
					c.setValue(BRIGHTNESS);
				if(c.getName().equals("Hue"))
					c.setValue(HUE);
				if(c.getName().equals("Saturation"))
					c.setValue(SATURATION);
			}
			
			if (VERBOSE) {
				for(Control c2: controls)
					System.out.println(
							"control name: " + c2.getName() + 
							" - min: " + c2.getMinValue() + 
							" - max: " + c2.getMaxValue() + 
							" - step: " + c2.getStepValue() + 
							" - value: " + c2.getValue()
					);
			}
			videoDevice.releaseControlList();
		}
		catch(V4L4JException e) {
			System.out.println("Cannot set video device settings!");
			e.printStackTrace();
		}
		frameGrabber = videoDevice.getJPEGFrameGrabber(width, height, channel, std, 80);
		frameGrabber.setCaptureCallback(this);
		width = frameGrabber.getWidth();
		height = frameGrabber.getHeight();
		if (VERBOSE)
			System.out.println("Starting capture at " + width + "x" + height);
	}
	
	private void cleanupCapture() {
		try {
			frameGrabber.stopCapture();
		} catch (StateException ex) {
			// the frame grabber may be already stopped, so we just ignore
			// any exception and simply continue.
		}

		// release the frame grabber and video device
		videoDevice.releaseFrameGrabber();
		videoDevice.release();
	}

	@Override
	public void exceptionReceived(V4L4JException e) {
		e.printStackTrace();
		
	}

	@Override
	public void nextFrame(VideoFrame frame) {
		ImageProcessor.process(frame.getBufferedImage(),null);
		
	}

}
