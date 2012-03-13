package group2.sdp.pc.vision;

import group2.sdp.pc.vision.LCHColour.ColourClass;
import group2.sdp.pc.vision.skeleton.ImageConsumer;
import group2.sdp.pc.vision.skeleton.ImageProcessorSkeleton;
import group2.sdp.pc.vision.skeleton.StaticInfoConsumer;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.print.attribute.standard.Finishings;

/**
 * This is how I process what I see on the pitch. I first remove the background to 
 * determine what pixels have changed, and therefore what pixels I need to consider when 
 * looking for myself, the other robot and the ball.
 * To find the robots I look for the largest connected area of that colour. I then 
 * use repeated regression to ensure the angle is correct.
 * @author Alfie
 *
 */
public class ImageProcessor extends ImageProcessorSkeleton {

	/**
	 * The mode of the output from the processor: MATCH is the default, 
	 * CHROMA and LUMA are used during setup. 
	 */
	public enum OutputMode {
		MATCH,
		CHROMA,
		LUMA
	}

	private OutputMode currentMode = OutputMode.MATCH;

	/**
	 * Shows whether the background has to be updated or not.
	 */
	private boolean extractBackground;

	/**
	 * An image of the background compare each frame with.
	 */
	private BufferedImage backgroundImage;

	private static final boolean pitchOne = false;
	private static final boolean VERBOSE = false;
	/**
	 * The boundaries of the pitch rectangle in the real world. In cm.
	 */
	private final Rectangle2D pitchPhysicalRectangle = new Rectangle2D.Float(-122, -60.5f, 244, 121);
	private String backgroundFileName = "background.png";
	private boolean saveBackground = false;

	/**
	 * New pixels in the last frame that was processed.
	 */
	private ArrayList<Point> newPixels;
	
	private ArrayList<Point> pointsToDraw;

	//values to return
	private Point blueCentroid, yellowCentroid, ballCentroid, plateCentroidYellowRobot;
	private double blueDir, yellowDir;


	/**
	 * The rectangle that contains the whole pitch.
	 */
	private final Rectangle pitchCrop1 = new Rectangle(10, 58, 630-10, 421-58);
	private final Rectangle pitchCrop2 = new Rectangle(53, 100, 592 - 53, 383 - 100);

	// TODO: think of a better name/way of doing this
	private boolean isYellowRobotRightGoal = true;

	/**
	 * The goal post positions for the goal on the right
	 */
	// TODO: SET ACTUAL POSITIONS
	// TODO: ALSO NOT REALLY MESSY
	Point2D leftTop = convertPixelsToCm(new Point(61,175));
	Point2D leftBottom = convertPixelsToCm(new Point(65,305));
	Point2D rightTop = convertPixelsToCm(new Point(570,182));
	Point2D rightBottom = convertPixelsToCm(new Point(568,312));
	private final ArrayList<Point2D> rightGoalPostInfo1 = new ArrayList<Point2D>(Arrays.asList(new Point(0,0),new Point(0,0)));
	private final ArrayList<Point2D> rightGoalPostInfo2 = new ArrayList<Point2D>(Arrays.asList(rightTop,rightBottom));

	/**
	 * The goal post positions for the goal on the left
	 */
	// TODO: SET ACTUAL POSITIONS
	// TODO: ALSO NOT REALLY MESSY
	private final ArrayList<Point2D> leftGoalPostInfo1 = new ArrayList<Point2D>(Arrays.asList(new Point(0,0),new Point(0,0)));
	private final ArrayList<Point2D> leftGoalPostInfo2 = new ArrayList<Point2D>(Arrays.asList(leftTop,leftBottom));

	/**
	 * See parent's comment.
	 */
	public ImageProcessor(StaticInfoConsumer consumer, boolean yellowAlfie) {
		super(consumer, yellowAlfie);
		extractBackground = true;
		newPixels = new ArrayList<Point> ();
	}
	/**
	 * See parent's comment.
	 */
	public ImageProcessor(StaticInfoConsumer consumer, boolean yellowAlfie, ImageConsumer imageConsumer) {
		super(consumer, yellowAlfie, imageConsumer);
		extractBackground = true;
		newPixels = new ArrayList<Point> ();
	}

	/**
	 * In addition to processing the frame extracts the background if needed.
	 */
	@Override
	public void process(BufferedImage image) {
		if (extractBackground) {
			if (!saveBackground) {
				backgroundImage = loadBackgroundImage();
				if (backgroundImage == null) {
					backgroundImage = image;
				}
			} else {
				backgroundImage = image;
				saveBackgroundImage(backgroundImage);
			}
			extractBackground = false;
			// Note that the super.process(image) is not called.
			// This is the case, since we expect that the background
			// contains no objects worth of detection.
		} else {
			newPixels = getDifferentPixels(image);
			internalImage = drawPixels(image, new ArrayList<Point>());
			drawStuff(internalImage);
			super.process(image);
		}
		pointsToDraw = new ArrayList<Point>();
		detectRobotsAndBall(image, newPixels);

		

	}


	/**
	 * Grabs a new background image and saves it.
	 */
	public void grabNewBackgroundImage() {
		extractBackground = true;
		saveBackground = true;
	}

	private BufferedImage loadBackgroundImage() {
		File inputfile = new File(backgroundFileName);
		BufferedImage image = null;
		try {
			image = ImageIO.read(inputfile);
			System.out.println("Background image loaded.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * 
	 * @param image
	 */
	private void saveBackgroundImage(BufferedImage image) {
		File outputfile = new File(backgroundFileName);
		try {
			ImageIO.write(image, "png", outputfile);
			System.out.println("Background image saved.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The pixels that are sufficiently different from the background are 
	 * added to a list that is returned.
	 * @param image The image to subtract from the background image.
	 * @param pitchOne
	 * @return A list of the different points.
	 * @see isDifferent
	 */
	private ArrayList<Point> getDifferentPixels(BufferedImage image) {
		Rectangle pitchCrop = new Rectangle();
		if (pitchOne){
			pitchCrop = pitchCrop1;
		}
		else{
			pitchCrop = pitchCrop2;
		}
		int minX = Math.max(pitchCrop.x, image.getMinX());
		int minY = Math.max(pitchCrop.y, image.getMinY());
		int w = Math.min(pitchCrop.width, image.getWidth());
		int h = Math.min(pitchCrop.height, image.getHeight());

		int maxX = minX + w;
		int maxY = minY + h;

		ArrayList<Point> result = new ArrayList<Point>(); 
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
		int threshold;
		if (pitchOne) {
			threshold = 60;
		} else {
			threshold = 90;
		}

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
	 * This function is where everything except background removal is done. The robot 'T's 
	 * are detected, processed and have their centroids and orientations calculated. These 
	 * values are stored in the respective global variables.
	 * @param image
	 * @param newPixels The pixels which are "different" (calculated by background removal).
	 * @see #getDifferentPixels(BufferedImage)
	 * @see #getGreatestArea(ArrayList)
	 * @see #regressionAndDirection(BufferedImage, ArrayList, boolean)
	 */

	public void detectRobotsAndBall(BufferedImage image, ArrayList<Point> newPixels) {

		ArrayList<Point> yellowPoints = new ArrayList<Point>();
		ArrayList<Point> bluePoints = new ArrayList<Point>();
		ArrayList<Point> ballPoints = new ArrayList<Point>();
		ArrayList<Point> platePoints = new ArrayList<Point>();
		for (Point p : newPixels) {
			Color c = new Color(image.getRGB(p.x, p.y));
			LCHColour lch = new LCHColour(c);
			ColourClass cc = lch.getColourClass();

			switch (cc) {
			case RED:
				ballPoints.add(p);
				break;
			case GREEN_PLATE:
				platePoints.add(p);
				break;
			case BLUE:
				bluePoints.add(p);
				break;
			case YELLOW:
				yellowPoints.add(p);
				break;
			}
		}
		ArrayList<Point> yellowPointsClean = new ArrayList<Point>();
		if (yellowPoints.size() > 3000) {
			System.out.println("Something non-robot! :o");
		} else {
			yellowPointsClean = getGreatestArea(yellowPoints);
		}
		ArrayList<Point> bluePointsClean = getGreatestArea(bluePoints);
		ArrayList<Point> ballPointsClean = getGreatestArea(ballPoints);


		this.ballCentroid = calcCentroid(ballPointsClean);
		this.blueCentroid = calcCentroid(bluePointsClean);
		this.yellowCentroid = calcCentroid(yellowPointsClean);

		this.blueDir = regressionAndDirection(image, bluePointsClean, false) % 360;
		this.yellowDir = regressionAndDirection(image, yellowPointsClean, true) % 360;

	}

	/**
	 * Loops through allPoints calling {@link #mindFlower(ArrayList,ArrayList,Point)} on pixels in it. mindFlower 
	 * removes pixels from allPoints if it determines they are connected to another 
	 * pixel so this will almost never cycle through the *whole* ArrayList.
	 * @param allPoints the list of points of the same colour (yellowPoints/bluePoints)
	 * @return the ArrayList of connected pixels in allPoints which form the largest 
	 * area
	 * 
	 */

	public ArrayList<Point> getGreatestArea(ArrayList<Point> allPoints) {
		ArrayList<Point> bestArea = new ArrayList<Point>();
		// this while loop should end because mindFlower
		// removes points from allPoints
		while (allPoints.size() != 0) {
			ArrayList<Point> newArea = new ArrayList<Point>();

			newArea.add(allPoints.get(0));
			allPoints.remove(0);
			newArea = mindFlower(newArea,allPoints,newArea.get(0));
			if (newArea.size() > bestArea.size()) {
				bestArea = newArea;
			}
		}
		return bestArea;
	}

	/**
	 * Uses {@link #findFacingDirection(BufferedImage, Point, boolean)} to get a starting 
	 * direction and then refines it using {@link #regression(ArrayList, double, boolean)}.
	 * @param image
	 * @param fixels
	 * @param isYellow
	 * @return the direction 0 < x < 360 degrees.
	 */

	public double regressionAndDirection(BufferedImage image,
			ArrayList<Point> fixels, boolean isYellow) {


		// for regression
		double end_angle = 0;

		Point fixelsCentroid = calcCentroid(fixels);

		double actualDir;
		if (isYellow) {
			actualDir = (findFacingDirection(image, fixelsCentroid, true));
		} else {
			actualDir = (findFacingDirection(image, fixelsCentroid, false));
		}

		double m = 0;
		double newangle = actualDir;
		for (int i = 0; i < 5; i++) {
			m = regression(fixels, newangle, isYellow);
			newangle = (newangle) - Math.toDegrees(Math.atan(m));

		}
		end_angle = newangle;
		return end_angle;
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
			LCHColour lch = new LCHColour(c);
			ColourClass cc = lch.getColourClass();

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
			int v;
			switch (currentMode) {
			case MATCH:
				result.setRGB(p.x, p.y, dc.getRGB());
				break;
			case CHROMA:
				v = lch.getChroma();
				result.setRGB(p.x, p.y, new Color(v, v, v).getRGB());
				break;
			case LUMA:
				v = lch.getLuma();
				result.setRGB(p.x, p.y, new Color(v, v, v).getRGB());
				break;
			}
		}
		return result;
	}

	/**
	 * Get the colour of a certain pixel on the image
	 * @param image
	 * @param fixel
	 * @return
	 */
	public int[] getColour(BufferedImage image, Point fixel){

		//TODO: it seems like this is never called?

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
	 * Called by {@link #getGreatestArea(ArrayList)}. Looks for adjacent points to determine connected 
	 * areas
	 * @param newArea connected pixels are stored in here
	 * @param allPoints the list of all possible points (e.g. all yellow/blue points)
	 * @param pixel the pixel to which all other points should be connected
	 * @return all points connected to pixel which 
	 * are present in allPoints.
	 */

	public ArrayList<Point> mindFlower(ArrayList<Point> newArea, ArrayList<Point> allPoints,  Point pixel){
		Point north = new Point(pixel.x,pixel.y-1);
		Point east = new Point(pixel.x+1,pixel.y);
		Point south = new Point(pixel.x,pixel.y+1);
		Point west = new Point(pixel.x-1,pixel.y);

		if (allPoints.remove(north)) {
			newArea.add(north);
			mindFlower(newArea,allPoints,north);
		}
		if (allPoints.remove(east)) {
			newArea.add(east);
			mindFlower(newArea,allPoints,east);
		}
		if (allPoints.remove(south)) {
			newArea.add(south);
			mindFlower(newArea,allPoints,south);
		}
		if (allPoints.remove(west)) {
			newArea.add(west);
			mindFlower(newArea,allPoints,west);
		}

		return newArea;
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
	public int findFacingDirection(BufferedImage image, Point centroid,
			boolean isYellow) {
		int cur_score = 0;
		int cur_score2 = 0;
		int best_score = 0;
		int best_angle = 0;
		Point nextPixel,rot_pixel;
		nextPixel = new Point();
		Point bestPointDir = new Point();
		Point bestPointDirBack = new Point();
		Point lastPointDir = new Point();
		Point lastPointDirBack = new Point();
		bestPointDir = new Point();
		bestPointDirBack = new Point();
		int lineScore, lineScoreBack;
		if (centroid.x != 0) {

			for (int i = 0; i < 180; i++) {
				cur_score = 0;
				cur_score2 = 0;
				nextPixel = new Point();
				nextPixel.x = centroid.x;
				nextPixel.y = centroid.y;
				rot_pixel = rotatePoint(centroid, new Point(nextPixel.x,
						nextPixel.y), i);
				/**
				 * Do not stop until the next pixel colour is not the colour we
				 * are looking for. The next pixel is determined by travelling
				 * in the negative x direction and then rotating the point i
				 * degrees around the centroid.
				 */

				while (isBlueYellow(image, rot_pixel, isYellow)) {

					cur_score++; // Since we sort in ascending order, lower
					// score is longer segments

					nextPixel = new Point(centroid.x + cur_score, centroid.y);
					rot_pixel = rotatePoint(centroid, new Point(nextPixel.x,
							nextPixel.y), i);
					lastPointDir = rot_pixel;
				}
				rot_pixel = centroid;
				while (isBlueYellow(image, rot_pixel, isYellow)) {
					cur_score2++; // Since we sort in ascending order, lower
					// score is longer segments

					nextPixel = new Point(centroid.x + cur_score2, centroid.y);
					rot_pixel = rotatePoint(centroid, new Point(nextPixel.x,
							nextPixel.y), i + 180);
					lastPointDirBack = rot_pixel;
				}

				if (cur_score +cur_score2 > best_score) {

					//					if(cur_score>cur_score2){
					//						best_angle = i;
					//					}else{
					//						best_angle = (i+180) % 360;
					//					}
					best_angle = i;
					bestPointDir = lastPointDir;
					bestPointDirBack = lastPointDirBack;
					best_score = cur_score+cur_score2;
				}
			}
			lineScore = 0;
			lineScoreBack=0;
			rot_pixel = new Point();
			rot_pixel.x = (bestPointDir.x + centroid.x)/2;
			rot_pixel.y = (bestPointDir.y + centroid.y)/2;
			nextPixel.x = (bestPointDir.x + centroid.x)/2;
			nextPixel.y = (bestPointDir.y + centroid.y)/2;
			Point middlePoint = (Point) nextPixel.clone();
			rot_pixel.x = nextPixel.x;
			rot_pixel.y = nextPixel.y;
			while (isBlueYellow(image, rot_pixel , isYellow)) {

				lineScore++; // Since we sort in ascending order, lower
				// score is longer segments

				nextPixel = new Point(middlePoint.x + lineScore, middlePoint.y);
				pointsToDraw.add((Point)rot_pixel.clone());
				rot_pixel = rotatePoint(middlePoint, new Point(nextPixel.x,
						nextPixel.y), best_angle + 90);
				
			}
			nextPixel.x = (bestPointDirBack.x + centroid.x)/2;
			nextPixel.y = (bestPointDirBack.y + centroid.y)/2;
			Point middlePointBack = (Point) nextPixel.clone();
			rot_pixel.x = nextPixel.x;
			rot_pixel.y = nextPixel.y;
			while (isBlueYellow(image, rot_pixel , isYellow)) {

				lineScoreBack++; // Since we sort in ascending order, lower
				// score is longer segments
				pointsToDraw.add((Point)rot_pixel.clone());
				nextPixel = new Point(middlePointBack.x + lineScoreBack, middlePointBack.y);
				rot_pixel = rotatePoint(middlePointBack, new Point(nextPixel.x,
						nextPixel.y), best_angle + 90);
				
			}
			if(lineScore > lineScoreBack){

				best_angle = (best_angle+180) % 360;
			} else {
				best_angle = best_angle;
			}
		}

		return 360 - best_angle;
	}

	/**
	 * regression function
	 * @param fixels
	 * @param angle
	 * @return
	 */

	protected double regression(ArrayList<Point> fixels, double angle, boolean isYellow) {

		Point actualCentroid = blueCentroid;
		if (isYellow) {
			actualCentroid = yellowCentroid;
		}

		double allx = 0;
		double ally = 0;
		double allxy = 0;
		double allx_sqr = 0;
		double ally_sqr = 0;
		int n = fixels.size();

		for (int i = 0; i < fixels.size(); i++) {

			int x_for_rotate = fixels.get(i).x - actualCentroid.x;
			int y_for_rotate = fixels.get(i).y - actualCentroid.y;

			double x_rotated = x_for_rotate * Math.cos(Math.toRadians(angle))
			- y_for_rotate * Math.sin(Math.toRadians(angle));
			double y_rotated = x_for_rotate * Math.sin(Math.toRadians(angle))
			+ y_for_rotate * Math.cos(Math.toRadians(angle));

			x_rotated = x_rotated + actualCentroid.x;
			y_rotated = y_rotated + actualCentroid.y;

			allx += x_rotated;
			ally += y_rotated;

			allxy += x_rotated * y_rotated;

			allx_sqr += x_rotated * x_rotated;
			ally_sqr += y_rotated * y_rotated;

		}

		return (n * allxy - allx * ally) / (n * allx_sqr - allx * allx);
	}


	/**
	 * 
	 * @param colour The colour you are checking
	 * @param isYellow If you are looking for yellow (the other option is blue)
	 * @return
	 */
	private boolean isBlueYellow(BufferedImage image, Point pixel, boolean isYellow) {
		boolean returnValue = false;
		//TODO check bounds
		int width = 640;
		int height = 480;
		if (!(pixel.x >= 0 && pixel.x < width && pixel.y >= 0 && pixel.y < height)) {
			return false;
		}
		Color c = new Color(image.getRGB(pixel.x, pixel.y));
		LCHColour lch = new LCHColour(c);
		ColourClass cc = lch.getColourClass();
		switch (cc) {
		case BLUE:
			if (!isYellow) {
				returnValue = true;
			}
			break;
		case YELLOW:
			if (isYellow) {
				returnValue = true;
			}
			break;
		}
		return returnValue;
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
		else {
			if (VERBOSE) {
				System.out.println("Robot's missing.");
			}
		}
		return centroid;
	}


	/**
	 * WARNING: DO NOT USE p2 AFTER THIS FUNCTION HAS BEEN CALLED.
	 * This function will change the values of p2. Use the returned point 
	 * and create a copy of p2 if you want to use it. This will also perform 
	 * very badly if continually used to rotate by 1 degrees.
	 */

	public Point rotatePoint(Point pivot, Point rotate, int deg) {
		Point point = new Point(rotate.x,rotate.y);
		point.x -= pivot.x;
		point.y -= pivot.y;
		double rad = (double) Math.toRadians(deg);
		int xtemp;
		xtemp = (int) Math.round((point.x * (double)Math.cos(rad)) - (point.y * (double)Math.sin(rad)));
		point.y = (int) Math.round((point.x * (double)Math.sin(rad)) + (point.y * (double)Math.cos(rad)));
		point.x = xtemp;
		return new Point (point.x+pivot.x, point.y+pivot.y);
	}

	/**
	 * Converts from the image coordinate system (in pixels) to a coordinate system
	 * centred at the physical centre of the pitch (in cm), where y grows upwards and 
	 * x to the right.
	 * @param point The point to convert.
	 * @return The point, converted.
	 */
	private Point2D convertPixelsToCm(Point2D point) {
		Point2D p = new Point2D.Float();
		Rectangle pitchImageRectangle;
		if (pitchOne) {
			pitchImageRectangle = pitchCrop1;
		} else {
			pitchImageRectangle = pitchCrop2;
		}
		double x = linearRemap(point.getX(), 
				pitchImageRectangle.getMinX(), pitchImageRectangle.getWidth(), 
				pitchPhysicalRectangle.getMinX(), pitchPhysicalRectangle.getWidth());
		double y = linearRemap(point.getY(), 
				pitchImageRectangle.getMinY(), pitchImageRectangle.getHeight(), 
				pitchPhysicalRectangle.getMinY(), pitchPhysicalRectangle.getHeight());
		p.setLocation(x, -y);
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


	/**
	 * A safe way to draw a pixel
	 *
	 * @param raster
	 * draw on writable raster
	 * @param p1
	 * point coordinates
	 * @param colour
	 * colour
	 */
	private void drawPixel(WritableRaster raster, Point p1, int[] colour) {
		int width = 640;
		int height = 480;
		if (p1.x >= 0 && p1.x < width && p1.y >= 0 && p1.y < height)
			raster.setPixel(p1.x, p1.y, colour);
	}

	/**
	 * This function is used to keep all drawing/printing in one place.
	 * @param internalImage
	 */
	private void drawStuff(BufferedImage internalImage) {
		WritableRaster raster = internalImage.getRaster();
		drawLine_Robot_Facing(raster, this.blueCentroid , this.blueDir );
		drawLine_Robot_Facing(raster, this.yellowCentroid , this.yellowDir );
		drawCentroidCircle(raster,blueCentroid,new int[]{0,0,255},50);
		drawCentroidCircle(raster,yellowCentroid,new int[]{255,255,0},50);
		drawCentroidCircle(raster,ballCentroid,new int[]{255,0,0},25);
		for (int i = 0; i < pointsToDraw.size();i++) {
			drawPixel(raster, pointsToDraw.get(i), new int[]{255,0,0});
		}
	}

	/**
	 * Simply used to draw a circle around the point centroid.
	 * @param raster
	 * @param centroid
	 * @param colour
	 * @param radius
	 * @see #rotatePoint(Point, Point, int)
	 */
	private void drawCentroidCircle(WritableRaster raster, Point centroid, int[] colour, int radius) {
		Point rotPoint = new Point(centroid.x + radius,centroid.y);
		for (int i=0; i < 360;i++) {
			Point tempPoint = new Point(centroid.x + radius,centroid.y);
			rotPoint = rotatePoint(centroid,tempPoint,i);
			drawPixel(raster,rotPoint,colour);
		}
	}

	/**
	 * Used to draw the facing direction of robots.
	 * @param raster
	 * @param c
	 * @param angle
	 */
	private void drawLine_Robot_Facing(WritableRaster raster, Point c,
			double angle) {
		angle = 360 - angle;
		int[] colour = { 255, 255, 255 };
		if (angle < 270 && angle > 90) {

			int xh = c.x - 100;
			int x = c.x;
			int y = c.y;

			double b = c.y - Math.tan(Math.toRadians(angle)) * c.x;
			while (x > xh) {
				drawPixel(
						raster,
						new Point(x,
								(int) (Math.tan(Math.toRadians(angle)) * x + b)),
								colour);
				x--;
			}
		} else {

			int xh = c.x + 100;
			int x = c.x;
			int y = c.y;

			double b = c.y - Math.tan(Math.toRadians(angle)) * c.x;
			while (x < xh) {
				drawPixel(
						raster,
						new Point(x,
								(int) (Math.tan(Math.toRadians(angle)) * x + b)),
								colour);
				x++;
			}
		}

	}
	/**
	 * Used by the GUI
	 * @param isYellowRobotRight
	 */

	public void setYellowRobotRight(boolean isYellowRobotRight) {
		isYellowRobotRightGoal = isYellowRobotRight;
	}

	/**
	 * Set the mode of output.
	 * @param currentMode The mode of output.
	 */
	public void setCurrentMode(OutputMode currentMode) {
		this.currentMode = currentMode;
	}

	/**
	 * Get the mode of output.
	 * @return The mode of output.
	 */
	public OutputMode getCurrentMode() {
		return currentMode;
	}

	@Override
	protected Point2D extractBallPosition(BufferedImage image) {
		if (ballCentroid == null) {
			return null;
		}
		return convertPixelsToCm(ballCentroid);
	}

	@Override
	protected Point2D extractRobotPosition(BufferedImage image, boolean yellow) {
		if (yellow) {
			if (yellowCentroid == null) {
				return null;
			}
			return convertPixelsToCm(yellowCentroid);
		} else {
			if (blueCentroid == null) {
				return null;
			}
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

	@Override
	protected ArrayList<Point2D> extractRobotGoalPostInfo(BufferedImage image,
			boolean yellow) {
		ArrayList<Point2D> rightGoalPostInfo;
		ArrayList<Point2D> leftGoalPostInfo;
		if (pitchOne) {
			rightGoalPostInfo = rightGoalPostInfo1;
			leftGoalPostInfo = leftGoalPostInfo1;
		} else {
			rightGoalPostInfo = rightGoalPostInfo2;
			leftGoalPostInfo = leftGoalPostInfo2;
		}
		if (yellow) {
			if (isYellowRobotRightGoal) {
				return rightGoalPostInfo;
			} else {
				return leftGoalPostInfo;
			}
		} else {
			if (!isYellowRobotRightGoal) {
				return rightGoalPostInfo;
			} else {
				return leftGoalPostInfo;
			}
		}
	}
}
