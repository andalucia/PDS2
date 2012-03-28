package group2.sdp.pc.vision;

import group2.sdp.pc.vision.skeleton.ImageConsumer;
import group2.simulator.starter.SimulatorI;

import java.awt.AWTException;
import java.awt.Robot;

import au.edu.jcu.v4l4j.FrameGrabber;

import au.edu.jcu.v4l4j.VideoDevice;

/**
 * Uses v4l4j to grab images from the camera. The connection is initialised on
 * construction of the object and the streaming is started.
 */
public class EyeOfSimulator {

	private ImageConsumer consumer;

	/**
	 * Used for printing FPS.
	 */

	public EyeOfSimulator(ImageConsumer consumer) {
		this.consumer = consumer;

		// start capture

		CaptureImageFromSimulator.start();

	}

	private Thread CaptureImageFromSimulator = new Thread() {
		public void run() {
			try {
				Robot camera = new Robot();

				while (true) {
					consumer.consume(camera
							.createScreenCapture(SimulatorI.frame.getBounds()));
					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} catch (AWTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	};

}