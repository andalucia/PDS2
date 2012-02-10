package group2.sdp.pc.vision;

import group2.sdp.common.util.KeyValuePair;
import group2.sdp.pc.vision.skeleton.ImageConsumer;
import group2.sdp.pc.vision.skeleton.ImageProcessorSkeleton;
import group2.sdp.pc.vision.skeleton.StaticInfoConsumer;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageProcessor extends ImageProcessorSkeleton {
	
	private static int width = 640, height = 480,
	width_margin = 20, height_margin = 60;

	
	private static final int[] firebrick = new int[] { 178, 34, 34 };
	private static final int[] Aqua = new int[] {0, 255, 255};
	private static final int[] Coral = new int[] {255, 127, 80};
	
	//values to return
	private Point blueCentroid, yellowCentroid, ballCentroid;
	private int blueDir, yellowDir;
	
	// pitch2 colours
	private static final int[] yellow2 = new int[] {230,200,7};
	private static final int[] blue2 = new int[] {92,140,121};
	
	//pitch1 colours
	private static final int[] yellow1 = new int[] {127,125,69};
	private static final int[] blue1 = new int[] {92,140,121};
	
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
	
	private static final int GThreshYellowLow = 170;
	private static final int GThreshYellowHigh = 250;
	
	private static final int BThreshYellowLow = 0;
	private static final int BThreshYellowHigh = 30;
	
	
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
		//yellowDir = (findFacingDirection(image, yellowCentroid, true));
		//blueDir = (findFacingDirection(image, blueCentroid, false));
		
		super.process(image);
	}
	
	
	
	public BufferedImage detectRobotsAndBall(BufferedImage image) {
		
		// create a new blank raster of the same size
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, false, false,
				Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

		// create raster from given image
		WritableRaster raster = image.getRaster();

		ArrayList<Point> yellowpoints = new ArrayList<Point>();
		ArrayList<Point> bluepoints = new ArrayList<Point>();
		ArrayList<Point> ball = new ArrayList<Point>();
		
		// for every point on the image
		for (int x = width_margin; x < image.getWidth() - width_margin; x++) {
			for (int y = height_margin; y < image.getHeight() - height_margin; y++) {
				// get current pixel
				Point currentPoint = new Point(x, y);
				int[] colour = getColour(image, currentPoint);

				// test if the pixel belongs to either of the robots or the ball
				if (isYellow(colour, false)) {
					yellowpoints.add(currentPoint);
					drawPixel(raster, currentPoint, pureYellow);
				}

				if (isBlue(colour, false)) {
					bluepoints.add(currentPoint);
					drawPixel(raster, currentPoint, pureBlue);
				}
				if (isBall(colour, false)){
					ball.add(currentPoint);
				}
			}
		}
	
	
		this.ballCentroid = calcCentroid(ball);
		this.blueCentroid = calcCentroid(regression(image, noiseRemove(bluepoints, false), false));
		this.yellowCentroid = calcCentroid(regression(image, noiseRemove(yellowpoints, true), true));

		BufferedImage img = new BufferedImage(cm, raster, false, null);
		return img;

	}
	
	/**
	 * Collect the colour of a certain pixel on the image
	 * @param image
	 * @param fixel
	 * @return
	 */
	public int[] getColour(BufferedImage image, Point fixel){
		// get RGB values for a pixel
		int[] colour = new int[3];
		int col = image.getRGB(fixel.x, fixel.y);
		Color c = new Color(col);
		colour[0] = c.getRed();
		colour[1] = c.getGreen();
		colour[2] = c.getBlue();
		return colour;
	}
	
	/**
	 * Noise removal: if a pixel is a a certain distance away from the last
	 * pixel known to belong to the yellow robot, then remove that pixel from
	 * the collection of robot pixels and say it's a "fake" robot, i.e. noise
	 */
	
	public ArrayList<Point> noiseRemove(ArrayList<Point> fixels, boolean isYellow){
		ArrayList<Point> fakes = new ArrayList<Point>();
		Point fixelsCentroid = new Point();
		
			if (fixels.size() != 0){
			Point currCentroid = fixels.get(0);
			for (int i = 0; i < fixels.size(); i++){
				
				Point current = fixels.get(i);
				double dist = calcDistanceBetweenPoints(current, currCentroid);
	
				if (dist > 1000){
					/**
					 * if the current pixel is unusually further away from previous ones
					 * then it's fake, therefore delete it from the robot pixels and add
					 * to fakes
					 * it is necessary to decrement, however, to make sure no 
					 * pixel is omitted from checking
					 */
					fixels.remove(i);
					fakes.add(current);
					i--;
					
				}
			}
			
			/**
			 * we cannot be sure which list consist the proper robots but chances
			 * are the pixels in the larger list form the yellow robot
			 * therefore, the centroid of the bigger list is the
			 * centroid of the yellow robot
			 */
			// DRAW cross through the proper yellow centroid 
			if (fixels.size() > fakes.size()){
				fixelsCentroid = calcCentroid(fixels);
				//drawCross(raster, fixelsCentroid, pureYellow);
				
			}
			else {
				fixelsCentroid = calcCentroid(fakes);
				//drawCross(raster, fixelsCentroid, pureYellow);
			}
			}
		else {System.out.println("No robot on pitch");
		}
			return fixels;
	}
	
	public ArrayList<Point> regression(BufferedImage image, ArrayList<Point> fixels, boolean isYellow){
		
		WritableRaster raster = image.getRaster();
	
		// for regression
		double end_angle = 0;
		double allx = 0;
		double ally = 0;
		double allxy = 0;
		double allx_sqr = 0;
		double ally_sqr = 0;
		int n = fixels.size();
		Point fixelsCentroid = calcCentroid(fixels);
	
		for (int i = 0; i < fixels.size(); i++) {
			allx += fixels.get(i).x;
			ally += fixels.get(i).y;
			allxy += fixels.get(i).x * fixels.get(i).y;
			allx_sqr += fixels.get(i).x * fixels.get(i).x;
			ally_sqr += fixels.get(i).y * fixels.get(i).y;
	
		}
		
		double mx = regression(allx, ally, n, allxy, allx_sqr);
		double my = regression(ally, allx, n, allxy, ally_sqr);
		
		double mtheta = Math.abs((mx*my-1)/(mx+my));

		blueDir = (findFacingDirection(image, fixelsCentroid, false));
		yellowDir = (findFacingDirection(image, fixelsCentroid, true));
		
		
		double mx_degrees = Math.toDegrees(Math.atan(mx));
		double my_degrees = Math.toDegrees(Math.atan(my));
	
		// Using the direction found with findFacingDirection we decide
		// which regression value to take
		if (isYellow){
			if (yellowDir > 45 && yellowDir < 135) {
				end_angle = 90 + my_degrees;
			} else if (yellowDir > 135 && yellowDir < 225) {
				end_angle = 180 - mx_degrees;
			} else if (yellowDir > 225 && yellowDir < 315) {
				end_angle = 270 + my_degrees;
			} else {
				end_angle = (360 - mx_degrees) % 360;
			}
			drawLine_X(raster, new Point((int) (allx / n), (int) (ally / n)), mx,
					Coral);
			drawLine_Y(raster, new Point((int) (allx / n), (int) (ally / n)), my,
					Coral);
			
			
			/**
			 *  We check if the difference between both values is less than a certain amount
			 *  and if it is we use the average of both values (this is because regression only 
			 *   works on X OR Y axis: NOT BOTH
			 */

			if(mtheta<Math.tan(Math.toRadians(20))){

				double mxy2 = Math.tan((Math.atan(mx)+Math.atan(1/my))/2);
				drawLine_XY(raster, new Point((int) (allx / n), (int) (ally / n)), mxy2,
						Coral);
				
				// Using the direction again we decide how to use the regression value
				double mxy2_degrees = Math.toDegrees(Math.atan(mxy2));
				if (yellowDir < 90) {
					end_angle = 0 - mxy2_degrees;
				} else if (yellowDir < 180) {
					end_angle = 180 - mxy2_degrees;
				} else if (yellowDir < 270) {
					end_angle = 180 - mxy2_degrees;
				} else {
					end_angle = 360 - mxy2_degrees;
				}
			}
			System.out.println("yellow direction = " + end_angle);
		}
		else {
			if (blueDir > 45 && blueDir < 135) {
				end_angle = 90 + my_degrees;
			} else if (blueDir > 135 && blueDir < 225) {
				end_angle = 180 - mx_degrees;
			} else if (blueDir > 225 && blueDir < 315) {
				end_angle = 270 + my_degrees;
			} else {
				end_angle = (360 - mx_degrees) % 360;
			}
			drawLine_X(raster, new Point((int) (allx / n), (int) (ally / n)), mx,
					Aqua);
			drawLine_Y(raster, new Point((int) (allx / n), (int) (ally / n)), my,
					Aqua);
			
			if(mtheta<Math.tan(Math.toRadians(20))){

				double mxy2 = Math.tan((Math.atan(mx)+Math.atan(1/my))/2);
				drawLine_XY(raster, new Point((int) (allx / n), (int) (ally / n)), mxy2,
						Aqua);
				
				// Using blueDir again we decide how to use the regression value
				double mxy2_degrees = Math.toDegrees(Math.atan(mxy2));
				if (blueDir < 90) {
					end_angle = 0 - mxy2_degrees;
				} else if (blueDir < 180) {
					end_angle = 180 - mxy2_degrees;
				} else if (blueDir < 270) {
					end_angle = 180 - mxy2_degrees;
				} else {
					end_angle = 360 - mxy2_degrees;
				}
				System.out.println("blue direction = " + end_angle);
			}
		}
		return fixels;
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
			// To store all distances
			//List<KeyValuePair<Integer,Integer>> pairs = new ArrayList<KeyValuePair<Integer, Integer>>();
			
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
					
					cur_score++; // Since we sort in ascending order, lower score is longer segments
					
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
			return isYellow(colour, true);
		} else {
			return isBlue(colour, true);
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
	 * @param pitchOne TODO
	 * @return
	 */
	// Yellow robot
	public boolean isYellow(int[] colour, boolean pitchOne) {
		int R = colour[0];
		int G = colour[1];
		int B = colour[2];
		int[] differences = new int[3];
		if (pitchOne){
			differences = calcColourDifferences(yellow1, colour);
		}
		else {
			differences = calcColourDifferences(yellow2, colour);
		}
		return (differences[0] < 40 && differences[1] < 40 && differences[2] < 70)  || (colour[0] == 255 && colour[1] == 255 && colour[2] == 0);
}

	//Blue robot
	public boolean isBlue(int[] colour, boolean pitchOne) {
		int R = colour[0];
		int G = colour[1];
		int B = colour[2];
		int[] differences = new int[3];
		if (pitchOne){
			differences = calcColourDifferences(blue1, colour);
		}
		else {
			differences = calcColourDifferences(blue2, colour);
		}
		return (differences[0] < 40 && differences[1] < 40 && differences[2] < 40) || (colour[0] == 0 && colour[1] == 0 && colour[2] == 255);
//		return (R > RThreshBlueLow && R < RThreshBlueHigh && G > GThreshBlueLow && G < GThreshBlueHigh
//				&& B > BThreshBlueLow && B < BThreshBlueHigh || (R == 0 && G == 0 && B == 255));
	}

	// Ball
	public boolean isBall(int[] colour, boolean pitchOne) {
		int R = colour[0];
		int G = colour[1];
		int B = colour[2];
		return (R > RThreshRedLow && R < RThreshRedHigh && G > GThreshRedLow && G < GThreshRedHigh
				&& B > BThreshRedLow && B < BThreshRedHigh);
	}
	
	public Point[] getBoundaries(ArrayList<Point> fixels){
		Point topleft = new Point();
		Point topright = new Point();
		Point bottomleft = new Point();
		Point bottomright = new Point();
		
		for (int i = 0; i < fixels.size(); i++){
				topleft.x = fixels.get(0).x;
				topleft.y = fixels.get(0).y;
				topright.x = fixels.get(fixels.size()-1).x;
				topright.y = fixels.get(0).y;
				bottomleft.x = fixels.get(0).x;
				bottomleft.y = fixels.get(fixels.size()-1).y;
				bottomright.x = fixels.get(fixels.size()-1).x;
				bottomright.y = fixels.get(fixels.size()-1).y;
		}
		Point[] result = new Point[]{topleft, topright, bottomleft, bottomright};
		return result;
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
		return convertPixelsToCm(ballCentroid, false);
	}

	@Override
	protected Point2D extractRobotPosition(BufferedImage image, boolean yellow) {
		if (yellow) {
			return convertPixelsToCm(yellowCentroid, false);
		} else {
			return convertPixelsToCm(blueCentroid, false);
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

	
	
	/**
	 * The boundaries of the pitch rectangle (yeah, right) on the image. In pixels.
	 */
	private final Rectangle pitchImageRectangle1 = new Rectangle(37, 92, 601 - 37, 386 - 92);
	private final Rectangle pitchImageRectangle2 = new Rectangle(67, 150, 565 - 67, 404 - 150);
	
	
	
	/**
	 * The boundaries of the pitch rectangle in the real world. In cm.
	 */
	private final Rectangle2D pitchPhysicalRectangle1 = new Rectangle2D.Float(-122, -60.5f, 244, 121);
	private final Rectangle2D pitchPhysicalRectangle2 = new Rectangle2D.Float(-122, -60.5f, 244, 121);
	
	/**
	 * Converts from the image coordinate system (in pixels) to a coordinate system
	 * centred at the physical centre of the pitch (in cm), where y grows upwards and 
	 * x to the right.
	 * @param point The point to convert.
	 * @return The point, converted.
	 */
	private Point2D convertPixelsToCm(Point2D point, boolean pitchOne) {
		//TODO: Should convert pixel coordinates into centimetre coordinates
		// w.r.t centre of the pitch
		Point2D p = new Point2D.Float();
		if (pitchOne){
			double x = linearRemap(point.getX(), 
					pitchImageRectangle1.getMinX(), pitchImageRectangle1.getWidth(), 
					pitchPhysicalRectangle1.getMinX(), pitchPhysicalRectangle1.getWidth());
			double y = linearRemap(point.getY(), 
					pitchImageRectangle1.getMinY(), pitchImageRectangle1.getHeight(), 
					pitchPhysicalRectangle1.getMinY(), pitchPhysicalRectangle1.getHeight());
			p.setLocation(x, y);
		}
		else {
			double x = linearRemap(point.getX(), 
					pitchImageRectangle2.getMinX(), pitchImageRectangle2.getWidth(), 
					pitchPhysicalRectangle2.getMinX(), pitchPhysicalRectangle2.getWidth());
			double y = linearRemap(point.getY(), 
					pitchImageRectangle2.getMinY(), pitchImageRectangle2.getHeight(), 
					pitchPhysicalRectangle2.getMinY(), pitchPhysicalRectangle2.getHeight());
			p.setLocation(x, y);
		}
		return p;
	}

	/**
	 * Changes the coordinate system of a 1-dimensional variable.
	 * E.g. if you want to move your starting point from 1 to 3, and to
	 * dilate your range from 3 to 6, this would move 2 to 5:
	 * 
	 *         1 2 3 4 5 6 7 8 9
	 * Input : > .   <
	 * Output:     >   .       <
	 * 
	 * There is a side-effect of 'flipping' the image if you give negative range
	 * to either of the range arguments:
	 * 
	 *         1 2 3 4 5 6 7 8 9
	 * Input : < .   >
	 * Output:     >       .   <
	 * 
	 * @param x
	 * @param x0
	 * @param domainRange
	 * @param y0
	 * @param targetRange
	 * @return
	 */
	
	private double linearRemap(double x, double x0, double domainRange, double y0, double targetRange) {
		return (x - x0) * (targetRange / domainRange) + y0;
	}
	
	/*
	 * draw line in X direction
	 * */
	
	private void drawLine_X(WritableRaster raster, Point c, double m,
			int[] colour) {
		int xh = c.x + 100;
		int x = c.x;
		int y = c.y;

		double b = c.y - m * c.x;
		x = x - 100;
		while (x < xh) {
			drawPixel(raster, new Point(x, (int) (m * x + b)), colour);
			x++;
		}

	}
	
	private void drawLine_XY(WritableRaster raster, Point c, double m,
			int[] colour) {
		int xh = c.x + 100;
		int x = c.x;
		int y = c.y;

		double b = c.y - m * c.x;
		x = x - 100;
		while (x < xh) {
			drawPixel(raster, new Point(x, (int) (m * x + b)), colour);
			x++;
		}

	}

	/*
	 * draw line in Y direction
	 * */
	
	private void drawLine_Y(WritableRaster raster, Point c, double m,
			int[] colour) {
		int xh = c.y + 100;
		int x = c.y;
		int y = c.x;

		double b = c.x - m * c.y;
		x = x - 100;
		while (x < xh) {
			drawPixel(raster, new Point((int) (m * x + b), x), colour);
			x++;
		}

	}
	
	/*
	 * regression function 
	 * */
	
	protected double regression(double allx, double ally, int n, double allxy,
			double allx_sqr) {

		return (n * allxy - allx * ally) / (n * allx_sqr - allx * allx);
	}

}


