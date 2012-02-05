package group2.sdp.pc.vision.skeleton;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicPitchInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.breadbin.StaticBallInfo;
import group2.sdp.pc.breadbin.StaticPitchInfo;
import group2.sdp.pc.breadbin.StaticPitchInfoHistory;
import group2.sdp.pc.breadbin.StaticRobotInfo;

import java.util.Queue;

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
	private StaticPitchInfoHistory staticInfoHistory;

	/**
	 * The consumer that is going to consume the output of this class.
	 */
	private DynamicInfoConsumer dynamicConsumer;
	
	/**
	 * A constructor that takes the object that is going to consume the output 
	 * of this class as an argument. 
	 * @param consumer The object that is going to consume the output of this 
	 * class as an argument.
	 */
	public BakerySkeleton (DynamicInfoConsumer consumer) {
		this.dynamicConsumer = consumer;
		staticInfoHistory = new StaticPitchInfoHistory();
	}
	
	@Override
	public void consumeInfo(StaticPitchInfo spi) {
		addInfoToHistory(spi);
		DynamicPitchInfo dpi = produceDynamicInfo(spi);
		dynamicConsumer.consumeInfo(dpi);
	}

	/**
	 * Adds the given static info to the internal history queue.
	 * @param spi The given static info to the internal history queue.
	 */
	private void addInfoToHistory(StaticPitchInfo spi) {
		if (staticInfoHistory.size() == MAX_HISTORY_LENGTH) {
			staticInfoHistory.poll();
		}
		staticInfoHistory.add(spi);
	}

	/**
	 * Builds on top of the given static information to produce dynamic information
	 * about the pitch. 
	 * @param spi The given static information.
	 * @return Dynamic information about the pitch based on a history of previous
	 * informations.
	 */
	private DynamicPitchInfo produceDynamicInfo(StaticPitchInfo spi) {
		double rollingSpeed = computeBallRollingSpeed(staticInfoHistory.getBallInfos());
		double rollingDirection = computeBallRollingDirection(staticInfoHistory.getBallInfos());
		DynamicBallInfo ballInfo = new DynamicBallInfo(spi.getBallInfo().getPosition(), 
				rollingSpeed, rollingDirection);
		
		double alfieTravelSpeed = computeAlfieTravelSpeed();
		double alfieTravelDirection = computeAlfieTravelDirection();
		DynamicRobotInfo alfieInfo = new DynamicRobotInfo(spi.getAlfieInfo(), 
				alfieTravelSpeed, alfieTravelDirection); 
		
		double opponentTravelSpeed = computeOpponentTravelSpeed();
		double opponentTravelDirection = computeOpponentTravelDirection();
		DynamicRobotInfo opponentInfo = new DynamicRobotInfo(spi.getOpponentInfo(), 
				opponentTravelSpeed, opponentTravelDirection);
		
		DynamicPitchInfo result = new DynamicPitchInfo(ballInfo, alfieInfo, opponentInfo);
		return result;
	}

	/**
	 * Computes the rolling speed of the ball. Units are cm/s.
	 * @param ballHistoryInfos The history of ball infos to use when computing the rolling speed.
	 * @return The rolling speed of the ball.
	 */
	protected abstract double computeBallRollingSpeed(Queue<StaticBallInfo> ballHistoryInfos);

	/**
	 * Computes the rolling direction of the ball.
	 * Units are degrees.
	 * The range is [0, 360)
	 * 3 o'clock is 0 degrees, the angle grows counter clock-wise.
	 * Thus 12 o'clock is 90 degrees, 9 o'clock is 180 degrees and 6 o'clock is 270 degrees.
	 * @param ballHistoryInfos The history of ball infos to use when computing the rolling direction. 
	 * @return The rolling direction of the ball.
	 */
	protected abstract double computeBallRollingDirection(Queue<StaticBallInfo> ballHistoryInfos);

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
	 * Computes the travel speed of a robot. Units are cm/s.
	 * @param historyInfos The list of infos to use to compute the travel speed of the robot.
	 * @return The travel speed of the robot.
	 */
	protected abstract double computeRobotTravelSpeed(Queue<StaticRobotInfo> historyInfos);
	
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
	protected abstract double computeRobotTravelDirection(Queue<StaticRobotInfo> historyInfos);
}
