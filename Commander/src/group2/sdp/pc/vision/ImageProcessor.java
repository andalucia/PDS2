package group2.sdp.pc.vision;

import group2.sdp.pc.vision.skeleton.ImageConsumer;
import group2.sdp.pc.vision.skeleton.ImageProcessorSkeleton;
import group2.sdp.pc.vision.skeleton.StaticInfoConsumer;

import java.awt.Color;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class ImageProcessor extends ImageProcessorSkeleton {
	
	private static int width = 640, height = 480,
	width_margin = 20, height_margin = 60;
	
	private int[] red = new int[] { 255, 0, 0 };
	private static int ball_box_radius = 50;
	private static Point BALL_POS = new Point(-1,-1);
	
	private static final int[] firebrick = new int[] { 178, 34, 34 };
	private static final int[] Aqua = new int[] {0, 255, 255};
	private static final int[] Coral = new int[] {255, 127, 80};
	
	//values to return
	private Point blueCentroid, yellowCentroid, ballCentroid;
	private int blueDir, yellowDir;

	
	// BLUE thresholds
	private static final int RThreshBlueLow = 1;
	private static final int RThreshBlueHigh = 50;
	
	private static final int GThreshBlueLow = 70;
	private static final int GThreshBlueHigh = 150;
	
	private static final int BThreshBlueLow = 70;
	private static final int BThreshBlueHigh = 255;
	
	
	// YELLOW thresholds
	private static final int RThreshYellowLow = 110;
	private static final int RThreshYellowHigh = 160;
	
	private static final int GThreshYellowLow = 110;
	private static final int GThreshYellowHigh = 170;
	
	private static final int BThreshYellowLow = 20;
	private static final int BThreshYellowHigh = 100;
	
	
	// Ball thresholds
	private static final int RThreshRedLow = 140;
	private static final int RThreshRedHigh = 255;
	
	private static final int GThreshRedLow = 15;
	private static final int GThreshRedHigh = 35;
	
	private static final int BThreshRedLow = 0;
	private static final int BThreshRedHigh = 30;
	
	// pure colours - used for drawing pixels
	private static final int[] pureRed = new int[] { 255, 0, 0 };
	private static final int[] pureYellow = {255, 255, 0};
	private static final int[] pureBlue = new int[] {0, 0, 255};
	
	public ImageProcessor(StaticInfoConsumer consumer) {
		super(consumer);
	}
	
	public ImageProcessor(StaticInfoConsumer consumer,
			ImageConsumer imageConsumer) {
		super(consumer, imageConsumer);
	}
	
	
	//Please call super.process after done processing
	@Override
	public void process(BufferedImage image) {
		
		image = detectRobotsAndBall(image);
		yellowDir = (findFacingDirection(image, yellowCentroid, true));
		blueDir = (findFacingDirection(image, blueCentroid, false));
		
		super.process(image);
	}
	
	
	
	public BufferedImage detectRobotsAndBall(BufferedImage image) {
		
		// create a new blank raster of the same size
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, false, false,
				Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

		// create raster from given image
		WritableRaster raster = image.getRaster();

		Point yellowCentroid = new Point(0, 0);
		Point blueCentroid = new Point(0, 0);
		Point ballCentroid = new Point(0,0);
	
		ArrayList<Point> yellowpoints = new ArrayList<Point>();
		ArrayList<Point> fakeyellows = new ArrayList<Point>();
		ArrayList<Point> bluepoints = new ArrayList<Point>();
		ArrayList<Point> fakeblues = new ArrayList<Point>();
		ArrayList<Point> ball = new ArrayList<Point>();
		ArrayList<Point> nope_not_ball = new ArrayList<Point>();
		
		
		// for every point on the image
		for (int x = width_margin; x < image.getWidth() - width_margin; x++) {
			for (int y = height_margin; y < image.getHeight() - height_margin; y++) {
				// get RGB values for a pixel
				int[] colour = new int[3];
				int col = image.getRGB(x, y);
				Color c = new Color(col);
				colour[0] = c.getRed();
				colour[1] = c.getGreen();
				colour[2] = c.getBlue();

				// get current pixel
				Point currentPoint = new Point(x, y);

				// test if the pixel belongs to either of the robots or the ball
				if (isSthYellow(colour)) {
					yellowpoints.add(currentPoint);
				}

				if (isSthBlue(colour)) {
					bluepoints.add(currentPoint);
				}
				if (isBall(colour)){
					ball.add(currentPoint);
				}
			}
		}
	
		/** 
		 * Find the yellow and the blue robots; draw a cross through their centroids
		 */
		
		/**
		 * Noise removal: if a pixel is a a certain distance away from the last
		 * pixel known to belong to the yellow robot, then remove that pixel from
		 * the collection of robot pixels and say it's a "fake" robot, i.e. noise
		 */
		
		// yellow robot
		if (yellowpoints.size() != 0){
			Point lastyellow = yellowpoints.get(0);
			for (int i = 0; i < yellowpoints.size(); i++){
				
				Point current = yellowpoints.get(i);
				double dist1 = calcDistanceBetweenPoints(current, lastyellow);
	
				if (dist1 > 50){
					/**
					 * if the current pixel is unusually further away from previous ones
					 * then it's fake, therefore delete it from the robot pixels and add
					 * to fakes
					 * it is necessary to decrement, however, to make sure no 
					 * pixel is omitted from checking
					 */
					yellowpoints.remove(i);
					fakeyellows.add(current);
					i--;
					
				}
				// if pixel distance is reasonable, chances are the pixel is in its right place
				// and it is now the last one of the proper robot pixels
				else lastyellow = current;
			}
			
			/**
			 * we cannot be sure which list consist the proper robots but chances
			 * are the pixels in the larger list form the yellow robot
			 * therefore, the centroid of the bigger list is the
			 * centroid of the yellow robot
			 */
			// DRAW cross through the proper yellow centroid 
			if (yellowpoints.size() > fakeyellows.size()){
				yellowCentroid = calcCentroid(yellowpoints);
				drawCross(raster, yellowCentroid, Aqua);
				
			}
			else {
				yellowCentroid = calcCentroid(fakeyellows);
				drawCross(raster, yellowCentroid, Aqua);
			}
			
			// DRAW what the vision system thinks the yellow robot is
			if (yellowpoints.size() > fakeyellows.size()){
				for (int i = 0; i < yellowpoints.size(); i++){
					Point current = yellowpoints.get(i);
					drawPixel(raster, current, pureYellow);}
			}
			// it might be the case that the "fake" robot is the proper one
			else {
				for (int i = 0; i < fakeyellows.size(); i++){
					Point current = fakeyellows.get(i);
					drawPixel(raster, current, Aqua);}
			}
		}
		else {System.out.println("No yellow robot on pitch");}
		
		// Same for blue robot and ball
		// Blue robot
		if (bluepoints.size() != 0){
			Point lastblue = bluepoints.get(0);
			for (int i = 0; i < bluepoints.size(); i++){
				
				Point current = bluepoints.get(i);
				double dist = calcDistanceBetweenPoints(current, lastblue);
	
				if (dist > 50){
					bluepoints.remove(i);
					fakeblues.add(current);
					i--;
					
				}
				else lastblue = current;
			}
			if (bluepoints.size() > fakeblues.size()){
				blueCentroid = calcCentroid(bluepoints);
				drawCross(raster, blueCentroid, Aqua);
				
			}
			else {
				blueCentroid = calcCentroid(fakeblues);
				drawCross(raster, blueCentroid, Aqua);
			}
			
			if (bluepoints.size() > fakeblues.size()){
				for (int i = 0; i < bluepoints.size(); i++){
					Point current = bluepoints.get(i);
					drawPixel(raster, current, pureBlue);}
			}
			else {
				for (int i = 0; i < fakeblues.size(); i++){
					Point current = fakeblues.get(i);
					drawPixel(raster, current, pureBlue);}
			}
		}
		else {System.out.println("No blue robot on pitch");}
		
		// ball detection
		if (ball.size() != 0){
			
			Point lastred = ball.get(0);
			for (int i = 0; i < ball.size(); i++){
				
				Point current = ball.get(i);
				double dist = calcDistanceBetweenPoints(current, lastred);
	
				if (dist > 15){
					ball.remove(i);
					nope_not_ball.add(current);
					i--;
					
				}
				else lastred = current;
			}
	
			
			if (ball.size() > nope_not_ball.size()){
				ballCentroid = calcCentroid(ball);
				drawCross(raster, ballCentroid, pureRed);
				
			}
			else {
				ballCentroid = calcCentroid(nope_not_ball);
				drawCross(raster, ballCentroid, pureRed);
			}
			
			if (ball.size() > nope_not_ball.size()){
				for (int i = 0; i < ball.size(); i++){
					Point current = ball.get(i);
					drawPixel(raster, current, pureRed);}
			}
			else {
				for (int i = 0; i < nope_not_ball.size(); i++){
					Point current = nope_not_ball.get(i);
					drawPixel(raster, current, pureRed);}
			}
		}
		else {System.out.println("No ball on pitch");}
		
		this.ballCentroid = ballCentroid;
		this.blueCentroid = blueCentroid;
		this.yellowCentroid = yellowCentroid;

		
		BufferedImage img = new BufferedImage(cm, raster, false, null);
		return img;

	}
	
	/**
	 * Cycles through all (360) possible angles and finds the longest unbroken line
	 * from the centroid. The angle at which this line was found is the angle which 
	 * is returned.
	 * 
	 * @param image The image to draw on
	 * @param centroid The centroid of the robot
	 * @param isYellow If the robot is yellow
	 * @return Angle of robot in degrees w.r.t x-axis. Increases CCW.
	 */
	
	public int findFacingDirection(BufferedImage image, Point centroid, boolean isYellow) {
		Color c = null;
		int cur_score = 0;
		int best_score = 0;
		int best_angle = 0;
		if (centroid.x != 0) {
			for (int i = 0; i < 360; i++) {
				cur_score = 0;
				Point nextPixel = new Point();
				nextPixel.x = centroid.x;
				nextPixel.y = centroid.y;
				Point rot_pixel = rotatePoint(centroid, new Point(nextPixel.x,nextPixel.y), i);
				c = new Color(image.getRGB(rot_pixel.x,rot_pixel.y));
				int[] colour = new int[] {c.getRed(),c.getGreen(),c.getBlue()};
				// Do not stop until the next pixel colour is not the colour we are 
				// looking for. The next pixel is determined by travelling in the 
				// negative x direction and then rotating the point i degrees around 
				// the centroid.
				while (isBlueYellow(colour, isYellow)) {
					//System.out.println("rot_pixel = " + rot_pixel + " and isYellow = " + isYellow);
					cur_score++;
					nextPixel = new Point(centroid.x + cur_score,centroid.y);
					rot_pixel = rotatePoint(centroid, new Point(nextPixel.x,nextPixel.y), i);
					try {
						c = new Color(image.getRGB(rot_pixel.x,rot_pixel.y));
					} catch (Exception e) {
						System.out.println("rot_pixel = " + rot_pixel);
						System.out.println("centroid = " + centroid);
						break;
					}
					colour[0] = c.getRed();
					colour[1] = c.getGreen();
					colour[2] = c.getBlue();
	
				}
				if (cur_score > best_score) {
					best_angle = i;
					best_score = cur_score;
				}
			}
		}
		return 360 - best_angle;
	}

	/**
	 * 
	 * @param colour The colour you are checking
	 * @param isYellow If you are looking for yellow (the other option is blue)
	 * @return
	 */
	
	private boolean isBlueYellow(int[] colour, boolean isYellow) {
		if (isYellow) {
			return isSthYellow(colour);
		} else {
			return isSthBlue(colour);
		}
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
	 * 
	 * @param fixels - for every ArrayList<Point> robot/ball,
	 * 					calculate its centroid and return it
	 * @return
	 */
	public Point calcCentroid(ArrayList<Point> fixels){
		Point centroid = new Point(0,0);
		Point fixelsInArrayList = new Point(0,0);
		for (int i = 0; i < fixels.size(); i++){
			Point current = fixels.get(i);
			fixelsInArrayList.x += current.x;
			fixelsInArrayList.y += current.y;
		}
		if (!fixels.isEmpty()){
			centroid.x = fixelsInArrayList.x / fixels.size();
			centroid.y = fixelsInArrayList.y / fixels.size();}
		else System.out.println("Robot's missing");
		return centroid;
	}

	/**
	 * 		Every colour has a pattern: the value for each channel is bounded
	 * 		In order to be classified as yellow, e.g. a pixel should have
	 * 		values strictly between 120 and 160 for Red; 110 and 170 for Green
	 * 		and 20 and 85 for Blue
	 * 		Those values are however highly dependent on lighting conditions and
	 * 		have to be reconsidered every day, every hour and of course, on every
	 * 		match...
	 * @param colour
	 * @return
	 */

	// Yellow robot
	public boolean isSthYellow(int[] colour) {
		int R = colour[0];
		int G = colour[1];
		int B = colour[2];
		return (R > RThreshYellowLow && R < RThreshYellowHigh && G > GThreshYellowLow && G < GThreshYellowHigh
				&& B > BThreshYellowLow && B < BThreshYellowHigh || (R == 255 && G == 255 && B == 0));
	}

	//Blue robot
	public boolean isSthBlue(int[] colour) {
		int R = colour[0];
		int G = colour[1];
		int B = colour[2];
		return (R > RThreshBlueLow && R < RThreshBlueHigh && G > GThreshBlueLow && G < GThreshBlueHigh
				&& B > BThreshBlueLow && B < BThreshBlueHigh || (R == 0 && G == 0 && B == 255));
	}

	// Ball
	public boolean isBall(int[] colour) {
		int R = colour[0];
		int G = colour[1];
		int B = colour[2];
		return (R > RThreshRedLow && R < RThreshRedHigh && G > GThreshRedLow && G < GThreshRedHigh
				&& B > BThreshRedLow && B < BThreshRedHigh);
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
	
	/**
	 * WARNING: DO NOT USE p2 AFTER THIS FUNCTION HAS BEEN CALLED.
	 * This function will change the values of p2. Use the returned point 
	 * and create a copy of p2 if you want to use it.
	 */
	
	public Point rotatePoint(Point pivot, Point p2, int deg)
	{
		p2.x -= pivot.x;
		p2.y -= pivot.y;
		double rad = (double) Math.toRadians(deg);
		int xtemp;
		xtemp = (int) Math.round((p2.x * (double)Math.cos(rad)) - (p2.y * (double)Math.sin(rad)));
		p2.y = (int) Math.round((p2.x * (double)Math.sin(rad)) + (p2.y * (double)Math.cos(rad)));
		p2.x = xtemp;
		return new Point (p2.x+pivot.x, p2.y+pivot.y);
	}

	@Override
	protected Point2D extractBallPosition(BufferedImage image) {
		return convertPixelsToCm(ballCentroid);
	}

	@Override
	protected Point2D extractRobotPosition(BufferedImage image, boolean yellow) {
		if (yellow) {
			return convertPixelsToCm(yellowCentroid);
		} else {
			return convertPixelsToCm(blueCentroid);
		}
	}

	@Override
	protected double extractRobotFacingDirection(BufferedImage image,
			boolean yellow) {
		if (yellow) {
			return yellowDir;
		} else {
			return blueDir;
		}
	}
	
	private Point2D convertPixelsToCm(Point2D points) {
		//TODO: Should convert pixel coordinates into centimetre coordinates
		// w.r.t centre of the pitch
		return null;
	}
}
