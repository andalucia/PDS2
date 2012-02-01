package group2.sdp.pc.vision;

import java.awt.Color;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import au.edu.jcu.v4l4j.Control;
import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.StateException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

public class SimpleViewer extends WindowAdapter implements CaptureCallback{
	private static int      width = 640, height = 480, std = V4L4JConstants.STANDARD_WEBCAM, channel = 0,
	width_margin = 20, height_margin = 60, ball_box_radius = 50;
	private static String   device = "/dev/video0";

	private VideoDevice     videoDevice;
	private FrameGrabber    frameGrabber;

	private JLabel          label;
	private JFrame          frame;
	private int[] red = new int[] { 255, 0, 0 };
	Point ball_pos = new Point(-1,-1);
	private static final int SATURATION = 100;
	private static final int BRIGHTNESS = 128;
	private static final int CONTRAST = 64;
	private static final int HUE = 0;

	// int values for the pure colours 
	private static final int[] pureRed = new int[] { 255, 0, 0 };
	private static final int[] pureYellow = new int[] {255, 255, 0};
	private static final int[] pureBlue = new int[] {0, 0, 255};
	private static final int[] pureBlack = new int[] {0, 0, 0};
	private static final int[] firebrick = new int[] {178, 34, 34};

	// THRESHOLDS - CHANGE THEM!!!
	private static final int redThreshForYellow = 140;
	private static final int greenThreshForYellow = 140;
	private static final int blueThreshForYellow = 0; //not actually used but could be needed at some point
	private static final int redThreshForBlue = 140;
	private static final int greenThreshForBlue = 140;
	private static final int blueThreshForBlue = 160;
	private static final int redThreshForBlack = 2;
	private static final int greenThreshForBlack = 2;
	private static final int blueThreshForBlack = 2;
	
	//Previous centroids
	private static Point prevYellowCentroid = new Point(-1,-1);
	private static Point prevBlueCentroid = new Point(-1,-1);



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
			controls = videoDevice.getControlList().getList();
			for(Control c2: controls)
				System.out.println("control name: "+c2.getName()+" - min: "+c2.getMinValue()+" - max: "+c2.getMaxValue()+" - step: "+c2.getStepValue()+" - value: "+c2.getValue());
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
		BufferedImage ball_cross_drawn = null;
		ball_cross_drawn = findBallPosition(frame.getBufferedImage());
		ball_cross_drawn = detectRobots(ball_cross_drawn);
		label.getGraphics().drawImage(ball_cross_drawn, 0, 0, width, height, null);

		// recycle the frame
		frame.recycle();
	}

	/**
	 * Uses drawCross() to draw a cross on the position of the ball
	 * 
	 * @param thisFrame
	 *            current untouched frame
	 *            
	 * @see #drawCross(WritableRaster, Point, int[])
	 */

	public BufferedImage findBallPosition(BufferedImage thisFrame) {

		WritableRaster cross = thisFrame.getRaster();
		int redness = -1;
		//must create point this way or it does not create new object (which creates errors in for loops)
		Point prev_pos = new Point(ball_pos.x,ball_pos.y);
		//checks if we have previous ball position or if previous 
		//position would create out of bounds exception
		if (prev_pos.x == -1 || 
				prev_pos.x <= ball_box_radius || 
				prev_pos.x > thisFrame.getWidth() - ball_box_radius ||
				prev_pos.y <= ball_box_radius || 
				prev_pos.y > thisFrame.getHeight() - ball_box_radius) {
			for (int x=width_margin; x < thisFrame.getWidth() - width_margin; x++)
			{          
				for (int y = height_margin; y < thisFrame.getHeight() - height_margin; y++)
				{   
					if (((thisFrame.getRGB(x,y) >> 16) & 0xFF) > redness) {
						ball_pos.x = x;
						ball_pos.y = y;
						redness = (thisFrame.getRGB(x,y) >> 16) & 0xFF;
					}
				}
			}
			//only checks in square box around previous position of ball
		} else {
			for (int x = prev_pos.x - ball_box_radius;x < prev_pos.x + ball_box_radius;x++) {
				for (int y = prev_pos.y - ball_box_radius;y < prev_pos.y + ball_box_radius;y++) {
					//System.out.println("x = " + x + ", y = " + y + "prev_pos = " + prev_pos.x + "," + prev_pos.y);
					if (((thisFrame.getRGB(x,y) >> 16) & 0xFF) > redness) {
						ball_pos.x = x;
						ball_pos.y = y;
						redness = (thisFrame.getRGB(x,y) >> 16) & 0xFF;
					}
				} 
			}
		}
		if (redness < 100) {
			//not red enough
			drawCross(cross, prev_pos,red);
		} else {
			drawCross(cross, ball_pos, red);
		}
		return thisFrame;

	}
	
	/**
	 * Finds position of robots
	 * 
	 * @param image
	 *            current image from camera
	 *            
	 */

	public BufferedImage detectRobots(BufferedImage image) {
		// create a new blank raster of the same size
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, false, false,
				Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

		// create raster from given image
		WritableRaster raster = image.getRaster();

		//centroid connection 
		int howManyBlacks = 0;
		int howManyYellows = 0;
		int howManyBlues = 0;

		Point yellowCentroid = new Point(0,0);
		Point blueCentroid = new Point(0,0);
		Point blackCentroid = new Point(0,0);

		Point actualCentroidPosYellow = new Point(0,0);
		Point actualCentroidPosBlue = new Point(0,0);
		Point actualCentroidPosBlack = new Point(0,0);


		// for every point on the image

		for (int x=width_margin; x < image.getWidth() - width_margin; x++)
		{          
			for (int y = height_margin; y < image.getHeight() - height_margin; y++)
			{   
				// get RGB values for a pixel
				int[] colour = new int[3];
				int col = image.getRGB(x, y);
				Color c = new Color(col);
				colour[0] = c.getRed();
				colour[1] = c.getGreen();
				colour[2] = c.getBlue();

				Point currentPoint = new Point(x,y);

				if (isSthYellow(colour) && (calcDistanceBetweenPoints(currentPoint, prevYellowCentroid) < 100 || prevYellowCentroid.x == -1))	{
					drawPixel(raster, currentPoint, pureYellow);
					howManyYellows++;
					yellowCentroid.x += currentPoint.x;
					yellowCentroid.y += currentPoint.y;
				}

				if (howManyYellows > 25){
					actualCentroidPosYellow.x = yellowCentroid.x / howManyYellows;
					actualCentroidPosYellow.y = yellowCentroid.y / howManyYellows;
				}

				if (isSthBlue(colour))	{
					drawPixel(raster, currentPoint, pureBlue);
					howManyBlues++;
					blueCentroid.x += currentPoint.x;
					blueCentroid.y += currentPoint.y;
				}

				if (howManyBlues > 25){
					actualCentroidPosBlue.x = blueCentroid.x / howManyBlues;
					actualCentroidPosBlue.y = blueCentroid.y / howManyBlues;
				}

				if (isSthBlack(colour))	{
					drawPixel(raster, currentPoint, pureBlack);
					howManyBlacks++;
					blackCentroid.x += currentPoint.x;
					blackCentroid.y += currentPoint.y;
				}

				if (howManyBlacks > 25){
					actualCentroidPosBlack.x = blackCentroid.x / howManyBlacks;
					actualCentroidPosBlack.y = blackCentroid.y / howManyBlacks;}
			}
		}
		prevYellowCentroid = actualCentroidPosYellow;
		prevBlueCentroid = actualCentroidPosBlue;
		
		drawCross(raster,actualCentroidPosYellow,pureYellow);

		double distanceBetweenCentroidsYandK = calcDistanceBetweenPoints(actualCentroidPosYellow,actualCentroidPosBlack);
		double distanceBetweenCentroidsBandK = calcDistanceBetweenPoints(actualCentroidPosBlue,actualCentroidPosBlack);

		//if (distanceBetweenCentroidsYandK < 5000) drawLine(raster, actualCentroidPosYellow, actualCentroidPosBlack, firebrick);
		//else if (distanceBetweenCentroidsBandK < 5000) drawLine(raster, actualCentroidPosBlue, actualCentroidPosBlack, firebrick);
		drawCross(raster,actualCentroidPosBlue,pureBlue);
		//System.out.println(actualCentroidPosYellow);

		BufferedImage img = new BufferedImage(cm, raster, false, null);
		return img;

	}

	/**
	 * The function calculates the difference in colour values of two pixels
	 * @param colour1 - RGB values
	 * @param colour2 - RGB values
	 * @return
	 */

	public int[] calcColourDifferences(int[] colour1, int[] colour2){

		int channel1R = colour1[0];
		int channel1G = colour1[1];
		int channel1B = colour1[2];


		int channel2R = colour2[0];
		int channel2G = colour2[1];
		int channel2B = colour2[2];

		int[] result = new int[3];

		result[0] = Math.abs(channel1R - channel2R);
		result[1] = Math.abs(channel1G - channel2G);
		result[2] = Math.abs(channel1B - channel2B);

		return result;
	}

	/**
	 * Says "yes" if what it sees is yellow
	 * @param colour
	 * @return
	 */

	public boolean isSthYellow(int[] colour){
		int[] yellowSim = calcColourDifferences(pureYellow, colour);
		return (yellowSim[0] < redThreshForYellow && yellowSim[1] < greenThreshForYellow);
	}


	/**
	 * Says "yes" if what it sees is blue
	 * @param colour
	 * @return
	 */

	public boolean isSthBlue(int[] colour){
		int[] blueSim = calcColourDifferences(pureBlue, colour);
		return (blueSim[0] < redThreshForBlue && blueSim[1] < greenThreshForBlue && blueSim[2] < blueThreshForBlue);
	}

	/**
	 * @param colour
	 * @return
	 */

	public boolean isSthBlack(int[] colour){
		int[] blackSim = calcColourDifferences(pureBlack, colour);
		return (blackSim[0] < redThreshForBlack && blackSim[1] < greenThreshForBlack && blackSim[2] < blueThreshForBlack);
	}


	/**
	 * Calculates distance between two points.
	 * @param p1 start point
	 * @param p2 end point
	 * @return sqrt((x1-x2)^2+(y1-y2)^2)
	 */
	public static double calcDistanceBetweenPoints(Point p1, Point p2)
	{
		return Math.sqrt((double)(Math.pow(p1.x-p2.x,2)+(Math.pow(p1.y-p2.y,2))));
	}

	/**
	 * Draw line from p1 to p2 of specified colour on given raster.
	 * 
	 * @param raster
	 *            draw on writable raster
	 * @param p1
	 *            start point
	 * @param p2
	 *            end point
	 * @param colour
	 *            - colour as three integers
	 */
	public void drawLine(WritableRaster raster, Point p1, Point p2, int[] colour) {
		int dx = Math.abs(p2.x - p1.x);
		int dy = Math.abs(p2.y - p1.y);

		drawPixel(raster, new Point(p1.x, p1.y), colour);

		if (dx != 0) {
			float m = (float) dy / (float) dx;
			float b = p1.y - m * p1.x;
			dx = (p2.x > p1.x) ? 1 : -1;
			while (p1.x != p2.x) {
				p1.x += dx;
				p1.y = Math.round(m * p1.x + b);
				drawPixel(raster, new Point(p1.x, p1.y), colour);
			}
		}
	}

	/**
	 * A safe way to draw a pixel
	 * 
	 * @param raster
	 *            draw on writable raster
	 * @param p1
	 *            point coordinates
	 * @param colour
	 *            colour
	 */
	private void drawPixel(WritableRaster raster, Point p1, int[] colour) {
		if (p1.x >= 0 && p1.x < width && p1.y >= 0 && p1.y < height)
			raster.setPixel(p1.x, p1.y, colour);
	}

	/**
	 * Draws 2 perpendicular lines intersecting at point p1
	 * 
	 * @param raster
	 *            draw on writable raster
	 * @param p1
	 *            point coordinates
	 * @param colour
	 *            colour
	 */
	private void drawCross(WritableRaster raster, Point p1, int[] colour) {
		for (int i = 0; i < width; i++) {
			drawPixel(raster, new Point(i, p1.y), colour);
		}
		for (int i = 0; i < height; i++) {
			drawPixel(raster, new Point(p1.x, i), colour);
		}
	}
	
	private ArrayList<Point> performClustering(ArrayList<Point> pixels, int distance) {
		int centroidx,centroidy = 0;
		Point centroid = new Point(0,0);
		while (calcDistanceBetweenPoints(pixels.get(0), pixels.get(pixels.size())) > distance) {
			pixels.trimToSize();
			centroidx = 0;
			centroidy = 0;
			for (Point p : pixels) {
				centroidx += p.x;
				centroidy += p.y;
			}
			centroid.x = centroidx/pixels.size();
			centroid.y = centroidy/pixels.size();
			if (pixels.indexOf(centroid) == -1) {
				System.out.println("Centroid not found in list of pixels");
			} else if (pixels.indexOf(centroid) < pixels.size()/2) {
				//pixels.subList(0, 4).clear();
				pixels.remove(0);
			} else {
				//pixels.subList(pixels.size()-5,pixels.size()-1);
				pixels.remove(pixels.size()-1);
			}
		}
		
		return pixels;
	}
}