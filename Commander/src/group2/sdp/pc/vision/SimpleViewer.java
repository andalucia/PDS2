package group2.sdp.pc.vision;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.StateException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

public class SimpleViewer extends WindowAdapter implements CaptureCallback{
	private static int      width = 640, height = 480, std = V4L4JConstants.STANDARD_WEBCAM, channel = 0;
	private static String   device = "/dev/video0";

	private VideoDevice     videoDevice;
	private FrameGrabber    frameGrabber;

	private JLabel          label;
	private JFrame          frame;



	public static void main(String args[]){

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new SimpleViewer();
			}
		});
	}

	/**
	 * Builds a WebcamViewer object
	 * @throws V4L4JException if any parameter if invalid
	 */
	 public SimpleViewer(){
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

		 // create and initialise UI
		 initGUI();

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
		 frameGrabber = videoDevice.getJPEGFrameGrabber(width, height, channel, std, 80);
		 frameGrabber.setCaptureCallback(this);
		 width = frameGrabber.getWidth();
		 height = frameGrabber.getHeight();
		 System.out.println("Starting capture at "+width+"x"+height);
	 }

	 /** 
	  * Creates the UI components and initialises them
	  */
	 private void initGUI(){
		 frame = new JFrame();
		 label = new JLabel();
		 frame.getContentPane().add(label);
		 frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		 frame.addWindowListener(this);
		 frame.setVisible(true);
		 frame.setSize(width, height);       
	 }

	 /**
	  * this method stops the capture and releases the frame grabber and video device
	  */
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

	 /**
	  * Catch window closing event so we can free up resources before exiting
	  * @param e
	  */
	 public void windowClosing(WindowEvent e) {
		 cleanupCapture();

		 // close window
		 frame.dispose();            
	 }


	 @Override
	 public void exceptionReceived(V4L4JException e) {
		 // This method is called by v4l4j if an exception
		 // occurs while waiting for a new frame to be ready.
		 // The exception is available through e.getCause()
		 e.printStackTrace();
	 }

	 @Override
	 public void nextFrame(VideoFrame frame) {
		 // This method is called when a new frame is ready.
		 // Don't forget to recycle it when done dealing with the frame.

		 // draw the new frame onto the JLabel
		 BufferedImage normalizedFrame = normalizeFrame(frame.getBufferedImage());
		 label.getGraphics().drawImage(normalizedFrame, 0, 0, width, height, null);

		 // recycle the frame
		 frame.recycle();
	 }

	 public BufferedImage normalizeFrame(BufferedImage thisFrame) {
		 // does not normalize at the moment (thresholds for red)
//		 for (int x=0; x < thisFrame.getWidth(); x++)
//		 {          
//			 for (int y = 0; y < thisFrame.getHeight(); y++)
//			 {   
//				 //System.out.println((thisFrame.getRGB(x,y) >> 16) & 0xFF);
//				 if (( (thisFrame.getRGB(x,y) >> 16) & 0xFF) > 210) {
//					 thisFrame.setRGB(x, y, 0xFFFFFF);
//				 } else {
//					 thisFrame.setRGB(x, y, 0x000000); }
//			 }
//		 }
		 return thisFrame;

	 }
}