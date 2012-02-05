package group2.sdp.pc.vision;

import group2.sdp.pc.vision.skeleton.ImageConsumer;
import group2.sdp.pc.vision.skeleton.ImageProcessorSkeleton;
import group2.sdp.pc.vision.skeleton.StaticInfoConsumer;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class ImageProcessor extends ImageProcessorSkeleton {
	
	private static int width = 640, height = 480,
	width_margin = 20, height_margin = 60;
	
	private int[] red = new int[] { 255, 0, 0 };
	private static int ball_box_radius = 50;
	private static Point BALL_POS = new Point(-1,-1);
	
	//Previous centroids
	private static Point prevYellowCentroid = new Point(-1,-1);
	private static Point prevBlueCentroid = new Point(-1,-1);
	
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
	
	/**
	 * Performs image processing methods on image. raster is null if we do not 
	 * want these results to be drawn on the image.
	 * @param colour1 - RGB values
	 * @param colour2 - RGB values
	 * @return
	 */
	
	private static Point findBallPosition(BufferedImage thisFrame) {

		WritableRaster cross = thisFrame.getRaster();
		int redness = -1;
		//must create point this way or it does not create new object (which creates errors in for loops)
		Point prev_pos = new Point(BALL_POS.x,BALL_POS.y);
		Point new_pos = new Point(0,0);
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
						new_pos.x = x;
						new_pos.y = y;
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
						new_pos.x = x;
						new_pos.y = y;
						redness = (thisFrame.getRGB(x,y) >> 16) & 0xFF;
					}
				} 
			}
		}
		if (redness < 100) {
			//not red enough
			return prev_pos;
			//drawCross(cross, prev_pos,red);
		} else {
			return new_pos;
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

	public ImageProcessor(StaticInfoConsumer consumer) {
		super(consumer);
	}
	
	public ImageProcessor(StaticInfoConsumer consumer,
			ImageConsumer imageConsumer) {
		super(consumer, imageConsumer);
	}

	@Override
	protected Point2D extractBallPosition(BufferedImage image) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Point2D extractRobotPosition(BufferedImage image, boolean yellow) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected double extractRobotFacingDirection(BufferedImage image,
			boolean yellow) {
		// TODO Auto-generated method stub
		return 0;
	}
}
