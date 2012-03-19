package group2.sdp.pc.vision.skeleton;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.breadbin.StaticBallInfo;
import group2.sdp.pc.breadbin.StaticInfo;
import group2.sdp.pc.breadbin.StaticInfoHistory;
import group2.sdp.pc.breadbin.StaticRobotInfo;

import java.util.LinkedList;

/**
 * This class consumes static information about the pitch from an image 
 * processor and produces dynamic information about the pitch.
 */
public abstract class BakerySkeleton implements StaticInfoConsumer {

	/**
	 * The maximum number of previous static infos to store.
	 */
	private static final int MAX_HISTORY_LENGTH = 64;

	/**
	 * The queue of past static infos. The front of the queue is the oldest SPI.
	 */
	private StaticInfoHistory staticInfoHistory;

	/**
	 * The consumer that is going to consume the output of this class.
	 */
	private DynamicInfoConsumer dynamicConsumer;
	private int counter;

	/**
	 * A constructor that takes the object that is going to consume the output 
	 * of this class as an argument. 
	 * @param consumer The object that is going to consume the output of this 
	 * class as an argument.
	 */
	public BakerySkeleton (DynamicInfoConsumer consumer) {
		this.dynamicConsumer = consumer;
		staticInfoHistory = new StaticInfoHistory();
	}


	@Override
	public void consumeInfo(StaticInfo spi) {
//		System.out.println(spi);
		if (counter < 10) {
			counter++;
		} else {
			addInfoToHistory(spi);
			DynamicInfo dpi = produceDynamicInfo(spi);
			dynamicConsumer.consumeInfo(dpi);
		}
	}

	/**
	 * Adds the given static info to the internal history queue.
	 * @param spi The given static info to the internal history queue.
	 */
	private void addInfoToHistory(StaticInfo spi) {
		if (staticInfoHistory.size() == MAX_HISTORY_LENGTH) {
			staticInfoHistory.poll();
		}
		if (spi.getBallInfo().getPosition() == null) {
			StaticBallInfo lastKnownBallInfo = staticInfoHistory.getBallInfos().get(staticInfoHistory.size()-1);
			spi.setBallInfo(lastKnownBallInfo);

		}
		if (spi.getAlfieInfo().getPosition() == null) {
			StaticRobotInfo lastKnownAlfieInfo = staticInfoHistory.getAlfieInfos().get(staticInfoHistory.size()-1);
			spi.setAlfieInfo(lastKnownAlfieInfo);

		}
		if (spi.getOpponentInfo().getPosition() == null) {
			StaticRobotInfo lastKnownOpponentInfo = staticInfoHistory.getOpponentInfos().get(staticInfoHistory.size()-1);
			spi.setOpponentInfo(lastKnownOpponentInfo);
		}
		staticInfoHistory.add(spi);
	}

	/**
	 * Builds on top of the given static information to produce dynamic information
	 * about the pitch. Corrects errors in facing direction for robots.
	 * @param spi The given static information.
	 * @return Dynamic information about the pitch based on a history of previous
	 * informations.
	 */
	private DynamicInfo produceDynamicInfo(StaticInfo spi) {
		
		// Dynamic ball information
		double rollingSpeed = computeBallRollingSpeed(staticInfoHistory.getBallInfos());
		double rollingDirection = computeBallRollingDirection(staticInfoHistory.getBallInfos());
		DynamicBallInfo ballInfo = new DynamicBallInfo(
				spi.getBallInfo().getPosition(), 
				rollingSpeed, 
				rollingDirection, 
				spi.getBallInfo().getTimeStamp());
		
		// Dynamic Alfie information
		double alfieTravelSpeed = computeAlfieTravelSpeed();
		double alfieTravelDirection = computeAlfieTravelDirection();
		double alfieRotatingSpeed = computeAlfieRotatingSpeed();
		boolean alfieRotatingCCW = isAlfieRotatingCCW();
		DynamicRobotInfo alfieInfo = new DynamicRobotInfo(
				spi.getAlfieInfo(), 
				alfieTravelSpeed, 
				alfieTravelDirection,
				alfieRotatingSpeed,
				alfieRotatingCCW);
		
		alfieInfo.setFacingDirection(correctRobotFacingDirection(staticInfoHistory.getAlfieInfos()));
	
		
		// Dynamic opponent information
		double opponentTravelSpeed = computeOpponentTravelSpeed();
		double opponentTravelDirection = computeOpponentTravelDirection();
		double opponentRotatingSpeed = computeOpponentRotatingSpeed();
		boolean opponentRotatingCCW = isOpponentRotatingCCW();
		DynamicRobotInfo opponentInfo = new DynamicRobotInfo(
				spi.getOpponentInfo(), 
				opponentTravelSpeed, 
				opponentTravelDirection,
				opponentRotatingSpeed,
				opponentRotatingCCW);
		
		opponentInfo.setFacingDirection(correctRobotFacingDirection(staticInfoHistory.getOpponentInfos()));
		
		// Dynamic pitch information
		DynamicInfo result = new DynamicInfo(ballInfo, alfieInfo, opponentInfo);
		return result;
	}

	/**
	 * Computes the rolling speed of the ball. Units are cm/s.
	 * @param ballHistoryInfos The history of ball infos to use when computing the rolling speed.
	 * @return The rolling speed of the ball.
	 */
	protected abstract double computeBallRollingSpeed(LinkedList<StaticBallInfo> ballHistoryInfos);

	/**
	 * Computes the rolling direction of the ball.
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @param ballHistoryInfos The history of ball infos to use when computing the rolling direction. 
	 * @return The rolling direction of the ball.
	 */
	protected abstract double computeBallRollingDirection(LinkedList<StaticBallInfo> ballHistoryInfos);

	
	/**
	 * Computes the travel speed of Alfie. Units are cm/s.
	 * @return The travel speed of Alfie.
	 */
	private double computeAlfieTravelSpeed() {
		return computeRobotTravelSpeed(staticInfoHistory.getAlfieInfos());
	}

	/**
	 * Computes the travel direction of Alfie.
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.  
	 * @return The travel direction of Alfie.
	 */
	private double computeAlfieTravelDirection() {
		return computeRobotTravelDirection(staticInfoHistory.getAlfieInfos());
	}

	/**
	 * Computes the rotating speed of Alfie in degrees per second.
	 * @return The rotating speed of Alfie in degrees per second.
	 */
	private double computeAlfieRotatingSpeed() {
		return computeRobotRotatingSpeed(staticInfoHistory.getAlfieInfos());
	}
	
	/**
	 * Finds out if Alfie is rotating counter-clock-wise or not.
	 * @return True if Alfie is rotating counter-clock-wise, false otherwise.
	 */
	private boolean isAlfieRotatingCCW() {
		return isRobotRotatingCCW(staticInfoHistory.getAlfieInfos());
	}

	
	/**
	 * Computes the travel speed of Alfie's opponent. Units are cm/s.
	 * @return The travel speed of Alfie's opponent
	 */
	private double computeOpponentTravelSpeed() {
		return computeRobotTravelSpeed(staticInfoHistory.getOpponentInfos());
	}

	/**
	 * Computes the travel direction of Alfie's opponent.
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.  
	 * @return The travel direction of Alfie's opponent.
	 */
	private double computeOpponentTravelDirection() {
		return computeRobotTravelDirection(staticInfoHistory.getOpponentInfos());
	}

	/**
	 * Computes the rotating speed of Alfie's opponent in degrees per second.
	 * @return The rotating speed of Alfie's opponent in degrees per second.
	 */
	private double computeOpponentRotatingSpeed() {
		return computeRobotRotatingSpeed(staticInfoHistory.getAlfieInfos());
	}
	
	/**
	 * Finds out if Alfie's opponent is rotating counter-clock-wise or not.
	 * @return True if Alfie's opponent is rotating counter-clock-wise, false 
	 * otherwise.
	 */
	private boolean isOpponentRotatingCCW() {
		return isRobotRotatingCCW(staticInfoHistory.getAlfieInfos());
	}


	/**
	 * Computes the travel speed of a robot. Units are cm/s.
	 * @param historyInfos The list of infos to use to compute the travel speed of the robot.
	 * @return The travel speed of the robot.
	 */
	protected abstract double computeRobotTravelSpeed(LinkedList<StaticRobotInfo> historyInfos);

	/**
	 * Computes the travel direction of a robot. Note that this might be different than the
	 * actual pointing direction.
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @param historyInfos The list of infos to use to compute the travel direction of the robot.
	 * @return The travel direction of the robot.
	 */
	protected abstract double computeRobotTravelDirection(LinkedList<StaticRobotInfo> historyInfos);

	/**
	 * Compares against previous frames to remove extreme values from facing direction
	 * @param historyInfos The list of infos (some used for comparison to current angle).
	 * @return A value for facing direction
	 */
	protected abstract double correctRobotFacingDirection(LinkedList<StaticRobotInfo> historyInfos);
	
	/**
	 * Computes rotating speed of the robot, depending on angle on the previous
	 * frames.
	 * @param historyInfos The list of Static Robot Info objects to use.
	 * @return The rotating speed of the robot that has the given history.
	 */
	protected abstract double computeRobotRotatingSpeed(LinkedList<StaticRobotInfo> historyInfos);
	
	/**
	 * Finds out if the robot with the given history is turning 
	 * counter-clock-wise or not.
	 * @param historyInfos The list of Static Robot Info objects to use.
	 * @return True if the robot with the given history is turning 
	 * counter-clock-wise, false otherwise.
	 */
	protected abstract boolean isRobotRotatingCCW(LinkedList<StaticRobotInfo> historyInfos);
}