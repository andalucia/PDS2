package group2.sdp.pc.vision;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import group2.sdp.pc.breadbin.StaticBallInfo;
import group2.sdp.pc.breadbin.StaticRobotInfo;
import group2.sdp.pc.vision.skeleton.BakerySkeleton;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

/**
 * Description: The Bakery bakes series of Static Info objects into Dynamic Info
 *               objects.  
 * Main client: Overlord (see Planning Pipeline section below).
 * Actions:     Stores a history of Static Info objects, uses that history to 
 *               estimate derivative properties of objects: speed and direction 
 *               are estimated from sequence of positions, rotation speed and 
 *               direction are estimated from sequence of directions. Passes that
 *               information to a Dynamic Info Consumer, supplied on construction
 *               of the Bakery.
 */
public class Bakery extends BakerySkeleton {

	
	private final boolean verbose = false;
	
	/**
	 * Variables used to prevent too much correction in correct facing direction
	 */
	private int stopAflie;
	private int correctionsAlfie;
	private int counterAlfie;
	private int stopOp;
	private int correctionsOp;
	private int counterOp;
	/**
	 * moving is minimum distance an object has moved (between average points) to be considered moving
	 */
	private static final double MOVING_THRESHOLD=0.1;
	
	/**
	 * NOTROTATING is minimum angle a robot has turned to be considered turning 
	 */
	private static final double ROTATING_THRESHOLD = 0.1;

	public Bakery(DynamicInfoConsumer consumer) {
		super(consumer);
	}

	@Override
	protected double computeBallRollingSpeed(
			LinkedList<StaticBallInfo> ballHistoryInfos) {
		// algorithm finds average position from last 3 points then average of 3
		// before that
		// and uses these two average positions and times to calculate speed
		int historySize=ballHistoryInfos.size();
		if (historySize < 6) {
			if (verbose) {
				System.out.println("Not enough history ball speed 0");
			}
			return 0;
		}
		// find average position of last 3 points
		Point2D recentAvg = findAverageBallPoint(ballHistoryInfos, ballHistoryInfos.size()-1 , ballHistoryInfos.size()-3);
		long recentTime = findAverageTime(ballHistoryInfos, ballHistoryInfos.size()-1 , ballHistoryInfos.size()-3);
		//find average of previous 3 points
		Point2D oldAvg = findAverageBallPoint(ballHistoryInfos, ballHistoryInfos.size()-4 , ballHistoryInfos.size()-6);
		long oldTime = findAverageTime(ballHistoryInfos, ballHistoryInfos.size()-4 , ballHistoryInfos.size()-6);

		// test if ball is moving
		if (oldAvg.distance(recentAvg) < MOVING_THRESHOLD) {
			return 0;
		}

		double timeDif = (recentTime - oldTime);
		timeDif = timeDif / 1000;
		double speed = (recentAvg.distance(oldAvg)) / (timeDif);
		return speed;

	}

	@Override
	protected double computeBallRollingDirection(
			LinkedList<StaticBallInfo> ballHistoryInfos) {
		// algorithm finds average position from last 3 points then average of 3
		// before that
		// and uses these two average positions and times to calculate angle
		int numPoints=ballHistoryInfos.size();
		double angle;
		if (numPoints < 6) {
			if (verbose) {
				System.out.println("Not enough history ball direction 0");
			}
			return 0;
		}
		
		// find average of last 3 points
		Point2D recentAvg = findAverageBallPoint(ballHistoryInfos, numPoints-1 , numPoints-3);
		Point2D oldAvg = findAverageBallPoint(ballHistoryInfos, numPoints-4 , numPoints-6);		
		// test if ball is moving
		if (oldAvg.distance(recentAvg) < MOVING_THRESHOLD) {
			return 0;
		}
		// now calculate angle
		double xDiff = recentAvg.getX() - oldAvg.getX();
		double yDiff = recentAvg.getY() - oldAvg.getY();

		if (yDiff >= 0) {
			angle = Math.toDegrees(Math.atan2(yDiff, xDiff));
		} else {
			angle = 360 + Math.toDegrees(Math.atan2(yDiff, xDiff));
		}

		return angle;
	}

	@Override
	protected double computeRobotTravelSpeed(
			LinkedList<StaticRobotInfo> historyInfos) {
		// algorithm finds average position from last 3 points then average of 3
		// before that
		// and uses these two average positions and times to calculate speed
		int numPoints=historyInfos.size();
		// at beginning when no history speed cannot be calculated
		if (numPoints < 6) {
			if (verbose) {
				System.out.println("Not enough history robot speed 0");
			}
			return 0;
			
		}
		
		
		// find average position of last 3 points and then average of points before
		
		Point2D recentAvg = findAverageRobotPoint(historyInfos, numPoints-1 , numPoints-3);
		Point2D oldAvg = findAverageRobotPoint(historyInfos, numPoints-4 , numPoints-6);
		long recentTime = findAverageTimeR(historyInfos, numPoints-1 , numPoints-3);
		long oldTime = findAverageTimeR(historyInfos, numPoints-4 , numPoints-6);
		// test if robot is moving
		if (oldAvg.distance(recentAvg) < MOVING_THRESHOLD) {
			return 0;
		}
		double timeDif = (recentTime - oldTime);
		timeDif = timeDif / 1000;
		double speed = (recentAvg.distance(oldAvg)) / (timeDif);
		return speed;
	}

	protected double correctRobotFacingDirection(
			LinkedList<StaticRobotInfo> historyInfos) {
		// if we are correcting more than half the frames in the last 10 frames
		// then
		// set method to stop correcting for next 10 frames to avoid loop. use
		// vision values instead
		// use seperate counters for different robots (i.e Alfie and not Alfie).
		
		double currentFacing = historyInfos.getLast().getFacingDirection();
		//check for negative values (i.e robot is missing)
		if (currentFacing < 0) {
			return currentFacing;
		}
		if (historyInfos.getLast().isAlfie()) {
			if (stopAflie > 0) {
				stopAflie--;
				return currentFacing;
			}
			if (correctionsAlfie >= 5) {
				correctionsAlfie = 0;
				counterAlfie = 0;
				stopAflie = 10;
				return currentFacing;
			}
			if (counterAlfie == 10) {
				counterAlfie = 0;
				correctionsAlfie = 0;
			}

			if (historyInfos.isEmpty()) {
				return 0;
			}
			counterAlfie++;
		} else {
			if (stopOp > 0) {
				stopOp--;
				return currentFacing;
			}
			if (correctionsOp >= 5) {
				correctionsOp = 0;
				counterOp = 0;
				stopOp = 10;
				return currentFacing;
			}
			if (counterOp == 10) {
				counterOp = 0;
				correctionsOp = 0;
			}

			if (historyInfos.isEmpty()) {
				return 0;
			}
			counterOp++;
		}

		if (historyInfos.size() < 6) {
			return currentFacing;
		}
		int numSim = 0;
		// alternative used to pass previous angle if current is extreme
		double alternative = 0.0;
		double angle = currentFacing;
		// loop through previous points and count how many are similar
		for (int i = historyInfos.size() - 2; i >= historyInfos.size() - 6
				&& i >= 1; i--) {
			double angleOld = historyInfos.get(i).getFacingDirection();
			// current threshold=90
			if (isSimilarAngle(angleOld , angle, 90)) {
				numSim++;
			} else {
				alternative = angleOld;
			}
		}
		// if more than half are similar then angle ok
		if (numSim >= 3) {
			return angle;
		} else {
			// correct angle because of extreme value
			if (historyInfos.getLast().isAlfie()) {
				correctionsAlfie++;
			} else {
				correctionsOp++;
			}
			return alternative;
		}
	}

	@Override
	protected double computeRobotTravelDirection(
			LinkedList<StaticRobotInfo> historyInfos) {
		// algorithm finds average position from last 3 points then average of 3
		// before that
		// and uses these two average positions and times to calculate angle
		double angle;
		int numPoints=historyInfos.size();
		if (numPoints < 6) {
			if (verbose) {
				System.out.println("Not enough history robot direction set to facing direction");
			}
			return historyInfos.getLast().getFacingDirection();
		}
		
		
		Point2D recentAvg = findAverageRobotPoint(historyInfos, numPoints-1 , numPoints-3);
		Point2D oldAvg = findAverageRobotPoint(historyInfos, numPoints-4 , numPoints-6);
		
		// test if robot is moving
		if (oldAvg.distance(recentAvg) < MOVING_THRESHOLD) {
			return historyInfos.getLast().getFacingDirection();
		}
		// now calculate angle
		double xDiff = recentAvg.getX() - oldAvg.getX();
		double yDiff = recentAvg.getY() - oldAvg.getY();

		if (yDiff >= 0) {
			angle = Math.toDegrees(Math.atan2(yDiff, xDiff));
		} else {
			angle = 360 + Math.toDegrees(Math.atan2(yDiff, xDiff));
		}
		return angle;

	}

	@Override
	protected double computeRobotRotatingSpeed(
			LinkedList<StaticRobotInfo> historyInfos) {
		
		StaticRobotInfo recentRobot = historyInfos.get(historyInfos.size()-1);
		StaticRobotInfo oldRobot = historyInfos.get(historyInfos.size()-3);
		double time2 = recentRobot.getTimeStamp();
		double time1 = oldRobot.getTimeStamp();
		double angle2 = recentRobot.getFacingDirection();
		double angle1 = oldRobot.getFacingDirection();
		double bigAngle;
		double smallAngle;
		double angleDifference;
		
		// get time difference (seconds)
		double timeDifference = time2 - time1;
		timeDifference = timeDifference/1000.0;
		if (angle2==angle1){
			return 0;
		}
		if (angle1 >= angle2) {
			bigAngle = angle1;
			smallAngle = angle2;
		} else {
			bigAngle = angle2;
			smallAngle = angle1;
		}
		
		angleDifference=bigAngle-smallAngle;
		if((angleDifference) > 180){
			angleDifference = 360 - angleDifference;
		}
		if(verbose){
			System.out.println("Difference in angle between frames = " +angleDifference);
		}
		double rotSpeed = angleDifference / timeDifference;
		return rotSpeed;
		
	}

	@Override
	protected boolean isRobotRotatingCCW(
			LinkedList<StaticRobotInfo> historyInfos) {
		
		StaticRobotInfo recentRobot = historyInfos.get(historyInfos.size()-1);
		StaticRobotInfo oldRobot = historyInfos.get(historyInfos.size()-3);
		double angle2 = recentRobot.getFacingDirection();
		double angle1 = oldRobot.getFacingDirection();
		//not rotating case
		if(isSimilarAngle(angle2, angle1 , ROTATING_THRESHOLD)){
			return false;
		}
		if (angle2 > angle1) {
			if (angle2 <= (angle1 +180)){
				return true;
			}else {
				return false;
			}
		}
		//Else solves 0-360 jump problem
		//Works out difference between angles in the counter clock wise direction 
		//and checks difference <180
		else{
			double ccwDifference = (360-angle1) + angle2;
			if (ccwDifference <= 180){
				return true;
			}else{
				return false;
			}
					
		}		
		
	}


	/**
	 * Works out the average time of a set of ballInfos. Bounds used to set how much of the info is used for average.
	 * @param ballHistoryInfos list of ballInfos
	 * @param upperBound	index of first ball used in average
	 * @param lowerBound	index of last ball used in average
	 * @return average time for given set balls within bounds
	 */
	
	protected long findAverageTime(LinkedList<StaticBallInfo> ballHistoryInfos, int upperBound, int lowerBound){
		long sum = 0;
		int numPoints=(upperBound-lowerBound)+1;
		for(int i=lowerBound ; i <= upperBound ; i++){
			sum+=ballHistoryInfos.get(i).getTimeStamp();
			
		}
		long average =sum/numPoints;
		return average;
	}
	
	
	/**
	 * Works out the average point of a set of ballInfos. Bounds used to set how much of the info is used for average.
	 * @param ballHistoryInfos list of ballInfos
	 * @param upperBound	index of first ball used in average
	 * @param lowerBound	index of last ball used in average
	 * @return average point for given set balls within bounds
	 */
	
	protected Point2D findAverageBallPoint(LinkedList<StaticBallInfo> ballHistoryInfos, int upperBound, int lowerBound){
		double sumx = 0.0;
		double sumy = 0.0;
		int numPoints=(upperBound-lowerBound)+1;
		for(int i=lowerBound ; i <= upperBound ; i++){
			sumx+=ballHistoryInfos.get(i).getPosition().getX();
			sumy+=ballHistoryInfos.get(i).getPosition().getY();
		}
		Point2D.Double average= new Point2D.Double(sumx/numPoints , sumy/numPoints);
		return average;
	}
	
	/**
	 * Works out the average time of a set of robotInfos. Bounds used to set how much of the info is used for average.
	 * @param ballHistoryInfos list of robotInfos
	 * @param upperBound	index of first robot used in average
	 * @param lowerBound	index of last robot used in average
	 * @return average time for given set robotInfos within bounds
	 */
	
	protected long findAverageTimeR(LinkedList<StaticRobotInfo> robotHistoryInfos, int upperBound, int lowerBound){
		long sum = 0;
		int numPoints=(upperBound-lowerBound)+1;
		for(int i=lowerBound ; i <= upperBound ; i++){
			sum+=robotHistoryInfos.get(i).getTimeStamp();
			
		}
		long average =sum/numPoints;
		return average;
	}
	
	
	/**
	 * Works out the average point of a set of robotInfos. Bounds used to set how much of the info is used for average.
	 * @param ballHistoryInfos list of ballInfos
	 * @param upperBound	index of first robot used in average
	 * @param lowerBound	index of last robot used in average
	 * @return average point for given set robotInfos within bounds
	 */
	
	protected Point2D findAverageRobotPoint(LinkedList<StaticRobotInfo> robotHistoryInfos, int upperBound, int lowerBound){
		double sumx = 0.0;
		double sumy = 0.0;
		int numPoints=(upperBound-lowerBound)+1;
		for(int i=lowerBound ; i <= upperBound ; i++){
			sumx+=robotHistoryInfos.get(i).getPosition().getX();
			sumy+=robotHistoryInfos.get(i).getPosition().getY();
		}
		Point2D.Double average= new Point2D.Double(sumx/numPoints , sumy/numPoints);
		return average;
	}
	
	/**
	 * Method for comparison of angles. If angle within certain threshold of
	 * each other then return true else false
	 * 
	 * @param angle1
	 *            first angle for comparison
	 * @param angle2
	 *            second angle for comparison
	 * @param threshold
	 *            max difference for angles to be similar
	 * @return are angles within threshold of each other
	 */
	protected boolean isSimilarAngle(double angle1, double angle2,
			double threshold) {
		double bigAngle;
		double smallAngle;
		if (angle1 == angle2) {
			return true;
		}
		if (angle1 >= angle2) {
			bigAngle = angle1;
			smallAngle = angle2;
		} else {
			bigAngle = angle2;
			smallAngle = angle1;
		}
		if (bigAngle - smallAngle <= threshold) {
			return true;
		}
		// check to solve 360-0 problem
		if (bigAngle >= (360 - threshold)
				&& smallAngle <= 0 + (threshold - (360 - bigAngle))) {
			return true;
		}
		return false;
	}
}
