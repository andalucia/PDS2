package group2.sdp.pc.vision;

import group2.sdp.pc.breadbin.StaticRobotInfo;
import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.globalinfo.LCHColourSettings.ColourClass;
import group2.sdp.pc.vision.skeleton.ImageConsumer;
import group2.sdp.pc.vision.skeleton.StaticInfoConsumer;
import group2.sdp.pc.vision.skeleton.VisualCortexSkeleton;

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
import java.util.List;

import javax.imageio.ImageIO;

/**
 * 
 *<p><b>VisualCortex:</b> An Image Consumer</br>
 *<p><b>Description:</b></br>
 *The visual cortex of Alfie. Does main processing on the image. 
 *<p><b>Main client:</b> Bakery
 *<p><b>Responsibilities:</b></br>
 *Extracts image features: position of the ball and the 
 *robots, and orientation of the T-shapes on the top of the 
 *robots. Passes that information to a Static Info Consumer 
 *that is supplied on construction of the VisualCortex.
 *<p><b> According to Alfie:</b></br>
 *"This is how I process what I see on the pitch.
 *I first remove the background to determine what pixels have
 *changed, and therefore what pixels I need to consider when 
 *looking for myself, the other robot and the ball.
 *To find the robots I look for the largest connected area 
 *of that colour. I then use repeated regression to ensure 
 *the angle is correct.
 * @author Alfie
 *
 */
public class VisualCortex extends VisualCortexSkeleton {

	/**
	 * The mode of the output from the processor: MATCH is the default, CHROMA
	 * and LUMA are used during setup.
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
	* An image of the background - image in each frame is 
	* 							compared to the background
	*/
	private BufferedImage backgroundImage;

	private static final boolean VERBOSE = true;

	// true if we want to draw on the raster
	private static final boolean pleaseDraw = true;
	
	private String backgroundFileName = "background.png";
	private boolean saveBackground = false;

	/**
	 * New pixels in the last frame that was processed.
	 */
	private ArrayList<Point> newPixels;

	// values to return
	private Point blueCentroid, yellowCentroid, ballCentroid;
	private double directionBlueRobot, directionYellowRobot;
	
	// used to check if what we think is a robot/ball is 
	// actually a robot/ball or is noise
	private final int meanRobotSize = 400;
	private final int meanBallSize = 120;

	/**
	 * See parent's comment.
	 */
	public VisualCortex(GlobalInfo globalInfo, StaticInfoConsumer consumer) {
		super(globalInfo, consumer);
		extractBackground = true;
		newPixels = new ArrayList<Point>();
	}

	/**
	 * See parent's comment.
	 */
	public VisualCortex(GlobalInfo globalInfo, Bakery bakery,
			ImageConsumer imageConsumer) {
		super(globalInfo, bakery, imageConsumer);
		extractBackground = true;
		newPixels = new ArrayList<Point>();
	}

	/**
	 * In addition to processing the frame extracts the background if needed.
	 */
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
			internalImage = drawPixels(image, newPixels);
			if(pleaseDraw)
				drawStuff(internalImage);
			super.process(image);
		}
		detectRobotsAndBall(image, newPixels);
	}

	/**
	 * Grabs a new background image and saves it.
	 */
	public void grabNewBackgroundImage() {
		extractBackground = true;
		saveBackground = true;
	}

	/**
	 * Loads the background image from the file that is known to contain it and
	 * returns it.
	 * 
	 * @return The background image.
	 */
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
	 * Saves the given image as a background image to the file used for the
	 * purpose.
	 * 
	 * @param image
	 *            The image to save as a background image.
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
	 * The pixels that are sufficiently different from the background are added
	 * to a list that is returned.
	 * 
	 * @param image
	 *            The image to subtract from the background image.
	 * @return A list of the *different* points.
	 * @see isDifferent
	 */
	private ArrayList<Point> getDifferentPixels(BufferedImage image) {
		Rectangle pitchCrop = globalInfo.getPitch().getCamera().getPitchCrop();
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
	 * 
	 * @param image
	 *            The pixel to compare is taken from this picture.
	 * @param x
	 *            The x coordinate of the pixel.
	 * @param y
	 *            The y coordinate of the pixel.
	 * @return True if the specified pixel is different, false otherwise.
	 */
	private boolean isDifferent(BufferedImage image, int x, int y) {
		int threshold = 90;

		Color imagePixel = new Color(image.getRGB(x, y));
		Color backPixel = new Color(backgroundImage.getRGB(x, y));

		int[] delta = new int[] {
				Math.abs(imagePixel.getRed() - backPixel.getRed()),
				Math.abs(imagePixel.getGreen() - backPixel.getGreen()),
				Math.abs(imagePixel.getBlue() - backPixel.getBlue()) };

		return delta[0] + delta[1] + delta[2] > threshold;
	}

	/**
	 * This function is where everything except background removal is done. The
	 * robot 'T's are detected, processed and have their centroids and
	 * orientations calculated. These values are stored in the respective global
	 * variables.
	 * 
	 * @param image
	 * @param newPixels
	 *            The pixels which are "different" (calculated by background
	 *            removal).
	 * @see #getDifferentPixels(BufferedImage)
	 * @see #getGreatestArea(ArrayList)
	 * @see #regressionAndDirection(BufferedImage, ArrayList, boolean)
	 */
	public void detectRobotsAndBall(BufferedImage image,
			ArrayList<Point> newPixels) {
		ArrayList<Point> yellowPoints = new ArrayList<Point>();
		ArrayList<Point> bluePoints = new ArrayList<Point>();
		ArrayList<Point> ballPoints = new ArrayList<Point>();
		ArrayList<Point> platePoints = new ArrayList<Point>();
		for (Point p : newPixels) {
			Color c = new Color(image.getRGB(p.x, p.y));
			LCHColour lch = new LCHColour(c);
			ColourClass cc = globalInfo.getColourSettings().getColourClass(lch);

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
		ArrayList<Point> yellowPointsClean = new ArrayList<Point>(0);
		ArrayList<Point> bluePointsClean = new ArrayList<Point>(0);
		ArrayList<Point> ballPointsClean = new ArrayList<Point>(0);

		if (sizeCheck(bluePoints, meanRobotSize)) {

			bluePointsClean = getGreatestArea(bluePoints);
		}
		if (sizeCheck(yellowPoints, meanRobotSize)) {

			yellowPointsClean = getGreatestArea(yellowPoints);
		}
		if (sizeCheck(ballPoints, meanBallSize)) {
			ballPointsClean = getGreatestArea(ballPoints);
		}

		this.ballCentroid = calcCentroid(ballPointsClean);
		this.blueCentroid = calcCentroid(bluePointsClean);
		this.yellowCentroid = calcCentroid(yellowPointsClean);

		this.directionBlueRobot = regressionAndDirection(image, bluePointsClean, false) % 360;
		this.directionYellowRobot = regressionAndDirection(image, yellowPointsClean, true) % 360;

	}

	/**
	 * Given an ArrayList of points we think are a robot,
	 * perform a check on the size of the ArrayList and
	 * decide if it is actually a robot or a noise.
	 * it is used to prevent the system from crashing when 
	 * there is a lot of noise on the pitch or whether 
	 * we think there's a robot present when in reality it 
	 * is noise from the other robots (wheels, sensors...) 
	 * 
	 * @param points
	 * @param expectedSize ({@link #meanRobotSize}/{@link #meanBallSize})
	 * @return true if the size of the ArrayList is in bounds
	 */
	private boolean sizeCheck(ArrayList<Point> points, int expectedSize) {
		int upperBound = 4;
		double lowerBound = 0.4;
		int size = points.size(); 
		if (size > expectedSize * upperBound
				&& size < expectedSize * lowerBound) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Loops through allPoints calling
	 * {@link #mindFlower(ArrayList,ArrayList,Point)} on pixels in it.
	 * mindFlower removes pixels from allPoints if it determines they are
	 * connected to another pixel so this will almost never cycle through the
	 * *whole* ArrayList.
	 * @param allPoints
	 *            the list of points of the same colour
	 *            (yellowPoints/bluePoints)
	 * @return the ArrayList of connected pixels in allPoints which form the
	 *         largest area
	 */
	public ArrayList<Point> getGreatestArea(ArrayList<Point> allPoints) {
		ArrayList<Point> bestArea = new ArrayList<Point>();
		// this while loop should end because mindFlower
		// removes points from allPoints
		while (allPoints.size() != 0) {
			ArrayList<Point> newArea = new ArrayList<Point>();

			newArea.add(allPoints.get(0));
			allPoints.remove(0);
			newArea = mindFlower(newArea, allPoints, newArea.get(0));
			if (newArea.size() > bestArea.size()) {
				bestArea = newArea;
			}
		}
		return bestArea;
	}

	/**
	 * Uses {@link #findFacingDirection(BufferedImage, Point, boolean)} to get a
	 * starting direction and then refines it using
	 * {@link #regression(ArrayList, double, boolean)}.
	 * 
	 * @param image
	 * @param fixels
	 * @param isYellow
	 * @return the direction 0 < x < 360 degrees.
	 */
	public double regressionAndDirection(BufferedImage image,
			ArrayList<Point> fixels, boolean isYellow) {

		double angleToReturn = 0;
		Point fixelsCentroid = calcCentroid(fixels);

		double direction;
		if (isYellow) {
			// if we want the yellow direction
			// 'true' means we're looking for yellow
			direction = (findFacingDirection(image, fixelsCentroid, true));
		} else {
			// if we want the blue direction
			direction = (findFacingDirection(image, fixelsCentroid, false));
		}

		double angleReturnedByRegression = 0;
		double newAngle = direction;
		// perform regression 5 times on the angle 
		for (int i = 0; i < 5; i++) {
			angleReturnedByRegression = regression(fixels, newAngle, isYellow);
			double subtractAngle = Math.toDegrees(Math.atan(angleReturnedByRegression));
			newAngle -= subtractAngle;

		}
		angleToReturn = newAngle;
		return angleToReturn;
	}

	/**
	 * The function is used to draw pixels on the image we get from
	 * the camera. It is used once only after we detect the robots
	 * and the ball.
	 * 
	 * @param pixels
	 *            The pixels to draw on a new image.
	 * @return image
	 * 				The image with the pixels drawn on it
	 */
	private BufferedImage drawPixels(BufferedImage image, List<Point> pixels) {
		int w = backgroundImage.getWidth();
		int h = backgroundImage.getHeight();
		BufferedImage result = new BufferedImage(w, h,
				BufferedImage.TYPE_3BYTE_BGR);
		for (Point p : newPixels) {
			Color c = new Color(image.getRGB(p.x, p.y));
			LCHColour lch = new LCHColour(c);
			ColourClass cc = globalInfo.getColourSettings().getColourClass(lch);

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
	 * Called by {@link #getGreatestArea(ArrayList)}. Looks for adjacent points
	 * to determine connected areas.
	 * 
	 * @param newArea
	 *            connected pixels are stored in here
	 * @param allPoints
	 *            the list of all possible points (e.g. all yellow/blue points)
	 * @param pixel
	 *            the pixel to which all other points should be connected
	 * @return all points connected to pixel which are present in allPoints.
	 */

	public ArrayList<Point> mindFlower(ArrayList<Point> newArea,
			ArrayList<Point> allPoints, Point pixel) {
		Point north = new Point(pixel.x, pixel.y - 1);
		Point east = new Point(pixel.x + 1, pixel.y);
		Point south = new Point(pixel.x, pixel.y + 1);
		Point west = new Point(pixel.x - 1, pixel.y);

		if (allPoints.remove(north)) {
			newArea.add(north);
			mindFlower(newArea, allPoints, north);
		}
		if (allPoints.remove(east)) {
			newArea.add(east);
			mindFlower(newArea, allPoints, east);
		}
		if (allPoints.remove(south)) {
			newArea.add(south);
			mindFlower(newArea, allPoints, south);
		}
		if (allPoints.remove(west)) {
			newArea.add(west);
			mindFlower(newArea, allPoints, west);
		}

		return newArea;
	}

	/**
	 * Cycles through all (360) possible angles and finds the longest unbroken
	 * line from the centroid. The angle at which this line was found is the
	 * angle which is returned.
	 * 
	 * @param image
	 *            The image to draw on
	 * @param centroid
	 *            The centroid of the robot
	 * @param isYellow
	 *            If the robot is yellow
	 * @return Angle of robot in degrees w.r.t x-axis. Increases CCW.
	 */
	public int findFacingDirection(BufferedImage image, Point centroid,
			boolean isYellow) {
		if (centroid == null) {
			return -1;
		}
		int currentScore = 0;
		int currentScoreOppositeDirection = 0;
		int bestScore = 0;
		int bestAngle = 0;
		if (centroid.x != 0) {

			// for every possible direction
			// rotate by 1 degree at a time
			for (int i = 0; i < 360; i++) {
				currentScore = 0;
				currentScoreOppositeDirection = 0;
				Point nextPixel = new Point();			
				Point rotatedPixel = new Point();
				/**
				 * Do not stop until the next pixel colour is not the colour we
				 * are looking for. The next pixel is determined by travelling
				 * in the negative x direction and then rotating the point i
				 * degrees around the centroid.
				 */
				while (isBlueYellow(image, rotatedPixel, isYellow)) {
					nextPixel = new Point(centroid.x + currentScore, centroid.y);
					rotatedPixel = rotatePoint(centroid, nextPixel, i);
					// Since we sort in ascending order, lower
					// score is longer segments
					currentScore++;
				}
				/**
				 * The second while loop is because the function is checking
				 * if a line is 'unbroken' (all pixels on it are the same colour)
				 * in *both* directions. That is, if the line (centroid, Point(0,1)) 
				 * is unbroken, you have to also check if the line (centroid, Point(0,-1)) 
				 * is unbroken and only then say that is the right direction.
				 */
				while (isBlueYellow(image, rotatedPixel, isYellow)) {
					nextPixel = new Point(centroid.x + currentScore, centroid.y);
					rotatedPixel = rotatePoint(centroid, nextPixel, i + 180);
					currentScoreOppositeDirection++;
				}
				if (currentScore + currentScoreOppositeDirection > bestScore) {
					if (currentScore > currentScoreOppositeDirection) {
						bestAngle = i;
					} else {
						bestAngle = (i + 180) % 360;
					}
					bestScore = currentScore + currentScoreOppositeDirection;
				}
			}
		}
		return 360 - bestAngle;
	}

	/**
	 * Performs regression on the pixels, using the angle that
	 * {@link #findFacingDirection(BufferedImage, Point, boolean)}
	 * returns as the starting point 
	 * @param fixels
	 * @param angle
	 * @return angle
	 */
	protected double regression(ArrayList<Point> fixels, double angle, boolean isYellow) {

		// we want the function to be generic:
		Point actualCentroid = blueCentroid;
		if (isYellow) {
			actualCentroid = yellowCentroid;
		}

		double sumX = 0;
		double sumY = 0;
		double productXY = 0;
		double productXsquared = 0;
		double productYsquared = 0;
		int size = fixels.size();

		for (int i = 0; i < fixels.size(); i++) {

			//translate the point onto the main axes
			int normalisedX = fixels.get(i).x - actualCentroid.x;
			int normalisedY = fixels.get(i).y - actualCentroid.y;

			//rotate the point while it's still on the main axes
			double cosAngle = Math.cos(Math.toRadians(angle));
			double sinAngle = Math.sin(Math.toRadians(angle));
			double rotatedX = normalisedX * cosAngle
			- normalisedY * sinAngle;
			double rotatedY = normalisedX * sinAngle
			+ normalisedY * cosAngle;

			//translate the point back to its original position
			rotatedX += actualCentroid.x;
			rotatedY += actualCentroid.y;

			//sum together the x and y coordinates of every point
			//in the ArrayList of pixels
			sumX += rotatedX;
			sumY += rotatedY;

			productXY += rotatedX * rotatedY;

			productXsquared += Math.pow(rotatedX, 2);
			productYsquared += Math.pow(rotatedY, 2);

		}

		return (size * productXY - sumX * sumY) / (size * productXsquared - sumX * sumX);
	}

	/**
	 * Checks if a given pixel on the image is blue or is yellow  
	 * depending on the parameter isYellow
	 * @param colour The colour you are checking
	 * @param isYellow If you are looking for yellow (the other option is blue)
	 * @return --
	 */
	private boolean isBlueYellow(BufferedImage image, Point pixel, boolean isYellow) {
		boolean returnValue = false;
		if (!(withinBounds(pixel))) {
			return false;
		}
		Color c = new Color(image.getRGB(pixel.x, pixel.y));
		LCHColour lch = new LCHColour(c);
		ColourClass cc = globalInfo.getColourSettings().getColourClass(lch);
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
	 * For every ArrayList<Point> (robot/ball), calculate its centroid
	 * @param pixels robot/ball pixels
	 * @return Point centroid
	 */
	public Point calcCentroid(ArrayList<Point> fixels){
		if (fixels.size() == 0) {
			return null;
		}
		Point centroid = new Point(0,0);
		Point totalFixels = new Point(0,0);
		
		for (int i = 0; i < fixels.size(); i++){
			Point current = fixels.get(i);
			totalFixels.x += current.x;
			totalFixels.y += current.y;
		}
		if (!fixels.isEmpty()){
			centroid.x = totalFixels.x / fixels.size();
			centroid.y = totalFixels.y / fixels.size();}
		else {
			if (VERBOSE) {
				System.out.println("Robot's missing.");
			}
		}
		return centroid;
	}

	/**
	 * Given a point, rotate it by a given angle (in degrees) 
	 * around a pivot point
	 * @return coordinates of the *rounded* and rotated point
	 */
	public Point rotatePoint(Point pivot, Point rotate, int deg) {
		Point point = new Point(rotate.x,rotate.y);
		point.x -= pivot.x;
		point.y -= pivot.y;
		double rad = Math.toRadians(deg);
		double cosAngle = Math.cos(rad);
		double sinAngle = Math.sin(rad); 
		int xtemp = (int) Math.round((point.x * cosAngle) - (point.y * sinAngle));
		point.y = (int) Math.round((point.x * sinAngle) + (point.y * cosAngle));
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
		Rectangle2D pitchPhysicalRectangle = globalInfo.getPitch()
				.getMinimumEnclosingRectangle();
		Rectangle pitchImageRectangle = globalInfo.getCamera().getPitchCrop();

		double x = linearRemap(point.getX(), pitchImageRectangle.getMinX(),
				pitchImageRectangle.getWidth(),
				pitchPhysicalRectangle.getMinX(),
				pitchPhysicalRectangle.getWidth());
		double y = linearRemap(point.getY(), pitchImageRectangle.getMinY(),
				pitchImageRectangle.getHeight(),
				pitchPhysicalRectangle.getMinY(),
				pitchPhysicalRectangle.getHeight());
		p.setLocation(x, -y);

		return p;
	}

	/**
	 * Corrects the position of a robot. The converted position is our initial
	 * estimation of the position, assuming the plate lies on the pitch. There 
	 * is an error proportional to the distance of the robot from the centre
	 * of the pitch. This is corrected in this function. Here is a rough 
	 * sketch:
	 * Camera
	 *   .
	 *   |\
	 *   | \
	 *   |  \
	 * H |   \
	 *   |____\  <- robot centroid.
	 *   |   h|\ 
	 *   |____|_\
	 *       delta  
	 *      d
	 *      
	 * @param robotPosition The pixel position of the robot. 
	 * @param convertedPosition The converted position of the robot, before correction.
	 * @param height The height of the robot.
	 * @return The corrected position of the robot.
	 */
	private Point2D correctRobotPosition(Point robotPosition,
			Point2D convertedPosition, double height) {
		
		Rectangle pitchImageRectangle = 
			globalInfo.getCamera().getPitchCrop();
		
		// Correction
		double H = globalInfo.getCamera().getDistanceFromPitch();
		double h = height;
		double d = 
			robotPosition.distance(
					pitchImageRectangle.getCenterX(), 
					pitchImageRectangle.getCenterY()
			);
		double delta = d * h / H;
		double quotient = (d - delta) / d;
		Point2D result = 
			new Point2D.Float(
					(float) (convertedPosition.getX() * quotient),
					(float) (convertedPosition.getY() * quotient)
			);
		return result;
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
	private double linearRemap(double x, double x0, double domainRange,
			double y0, double targetRange) {
		return (x - x0) * (targetRange / domainRange) + y0;
	}

	/**
	 * Checks if the point is within the bounds of the background image because 
	 * we assume the background image is the same size as the current image.
	 * @param point
	 * @return
	 */
	private boolean withinBounds(Point point) {	
		int width, height;
		if (backgroundImage == null) {
			width = 640;
			height = 480;
		} else {
			width = backgroundImage.getWidth();
			height = backgroundImage.getHeight();
		}
		if (point.x < 0 && point.x >= width && point.y < 0 && point.y >= height){
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Draw a method on a Writable raster derived from the captured image.
	 * 
	 * @param raster
	 *            draw on writable raster
	 * @param p1
	 *            point coordinates
	 * @param colour
	 *            colour
	 */
	private void drawPixel(WritableRaster raster, Point p1, int[] colour) {
		if (withinBounds(p1))
			raster.setPixel(p1.x, p1.y, colour);
	}

	/**
	 * This function puts together two drawing functions:
	 * drawCentroidCircle - draw a circle of a certain radius 
	 * around the centroid of the robot 
	 * drawRobotFacingDirection - draw the orientation of the robot (a line)
	 * 
	 * @param internalImage
	 */
	private void drawStuff(BufferedImage internalImage) {
		WritableRaster raster = internalImage.getRaster();
		int[] blue = new int[] { 0, 0, 255 };
		int[] yellow = new int[] { 255, 255, 0 };
		int[] red = new int[] { 255, 0, 0 };
		// if there is a blue robot on the pitch,
		// draw a blue circle around it (facilitates the human eye...)
		// radius if the circle is 50 for the robots and 25 for the ball
		// draw orientation of the robot
		if (blueCentroid != null) {
			drawCentroidCircle(raster, blueCentroid, blue, 50);
			drawRobotFacingDirection(raster, this.blueCentroid, this.directionBlueRobot);
			
		}
		if (yellowCentroid != null) {
			drawCentroidCircle(raster, yellowCentroid, yellow, 50);
			drawRobotFacingDirection(raster, this.yellowCentroid, this.directionYellowRobot);
		}
		if (ballCentroid != null) {
			drawCentroidCircle(raster, ballCentroid, red, 25);
		}
	}

	/**
	 * Simply used to draw a circle around the point centroid.
	 * 
	 * @param raster
	 * @param centroid
	 * @param colour
	 * @param radius
	 * @see #rotatePoint(Point, Point, int)
	 */
	private void drawCentroidCircle(WritableRaster raster, Point centroid,
			int[] colour, int radius) {
		/**
		 * Think of it that way:
		 * Pick up a point at a distance, say 20 (future radius)
		 * and start rotating it by 1 degree around another point.
		 * What will happen in the end is that you will get a circle 
		 * of radius 20 whose origin is the point around which you rotate. 
		 */
		Point pointToRotate = new Point(centroid.x + radius, centroid.y);
		for (int i = 0; i < 360; i++) {
			Point previousPointToRotate = new Point(centroid.x + radius, centroid.y);
			pointToRotate = rotatePoint(centroid, previousPointToRotate, i);
			// the aim is for us (people) to see where the vision system
			// thinks the robot is, therefore - draw the point while rotating it
			drawPixel(raster, pointToRotate, colour);
		}
	}

	/**
	 * Used to draw the facing direction of robots.
	 * 
	 * @param raster
	 * @param point
	 * @param angle
	 */
	private void drawRobotFacingDirection(WritableRaster raster, Point centroid,
			double angle) {
		
		angle = 360 - angle;
		double tanAngle = Math.tan(Math.toRadians(angle));
		int[] colour = {255, 255, 255};
		
		if (angle < 270 && angle > 90) {
			int counter = centroid.x - 100;
			double b = centroid.y - centroid.x * tanAngle;
			while (centroid.x > counter) {
				Point pointToDraw = new Point(centroid.x, (int)(b + centroid.x * tanAngle));
				drawPixel(raster, pointToDraw, colour);
				counter++;
			}
		} else {
			int counter = centroid.x + 100;
			double b = centroid.y - centroid.x * tanAngle;
			while (centroid.x < counter) {
				Point pointToDraw = new Point(centroid.x,(int)(b + centroid.x * tanAngle));
				drawPixel(raster, pointToDraw, colour);
				counter--;
			}
		}
	}

	/**
	 * Set the mode of output.
	 * 
	 * @param currentMode
	 *            The mode of output.
	 */
	public void setCurrentMode(OutputMode currentMode) {
		this.currentMode = currentMode;
	}

	/**
	 * Get the mode of output.
	 * 
	 * @return The mode of output.
	 */
	public OutputMode getCurrentMode() {
		return currentMode;
	}

	/**
	 * @return ball position
	 */
	protected Point2D extractBallPosition(BufferedImage image) {
		if (ballCentroid == null || ballCentroid.getLocation() == null) {
			return null;
		}
		return convertPixelsToCm(ballCentroid);
	}

	/**
	 * @return robot positions
	 */
	protected Point2D extractRobotPosition(BufferedImage image, boolean yellow) {
		Point robotPosition = yellow 
			? yellowCentroid
			: blueCentroid;
		if (robotPosition == null) {
			return null;
		}
		Point2D convertedPosition = convertPixelsToCm(robotPosition);
		return 
			correctRobotPosition(
					robotPosition, 
					convertedPosition, 
					StaticRobotInfo.getHeight()
			);
	}

	/**
	 * @return angle of the robot facing direction
	 */
	protected double extractRobotFacingDirection(BufferedImage image,
			boolean yellow) {
		if (yellow) {
			return directionYellowRobot;
		} else {
			return directionBlueRobot;
		}
	}
}
