package group2.sdp.pc.vision.stan;

import group2.sdp.common.breadbin.DynamicBallInfo;
import group2.sdp.common.breadbin.DynamicPitchInfo;
import group2.sdp.common.breadbin.DynamicRobotInfo;
import group2.sdp.common.breadbin.StaticPitchInfo;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class consumes static information about the pitch from an image 
 * processor and produces dynamic information about the pitch.
 */
public class Bakery implements StaticInfoConsumer {

	/**
	 * The maximum number of previous static infos to store.
	 */
	private static final int MAX_HISTORY_LENGTH = 64;
	
	/**
	 * The queue of past static infos. The front of the queue is the oldest SPI.
	 */
	private Queue<StaticPitchInfo> staticInfoHistory;

	/**
	 * The consumer that is going to consume the output of this class.
	 */
	private DynamicInfoConsumer consumer;
	
	/**
	 * A constructor that takes the object that is going to consume the output 
	 * of this class as an argument. 
	 * @param consumer The object that is going to consume the output of this 
	 * class as an argument.
	 */
	public Bakery (DynamicInfoConsumer consumer) {
		this.consumer = consumer;
		staticInfoHistory = new LinkedList<StaticPitchInfo>();
	}
	
	@Override
	public void consumeInfo(StaticPitchInfo spi) {
		addInfoToHistory(spi);
		DynamicPitchInfo dpi = produceDynamicInfo(spi);
		consumer.consumeInfo(dpi);
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
		double rollingSpeed = extractBallRollingSpeed();
		double rollingDirection = extractBallRollingDirection();
		DynamicBallInfo ballInfo = new DynamicBallInfo(spi.getBallInfo().getPosition(), 
				rollingSpeed, rollingDirection);
		
		double alfieTravelSpeed = extractAlfieTravelSpeed();
		double alfieTravelDirection = extractAlfieTravelDirection();
		DynamicRobotInfo alfieInfo = new DynamicRobotInfo(spi.getAlfieInfo(), 
				alfieTravelSpeed, alfieTravelDirection); 
		
		double opponentTravelSpeed = extractOpponentTravelSpeed();
		double opponentTravelDirection = extractOpponentTravelDirection();
		DynamicRobotInfo opponentInfo = new DynamicRobotInfo(spi.getOpponentInfo(), 
				opponentTravelSpeed, opponentTravelDirection);
		
		DynamicPitchInfo result = new DynamicPitchInfo(ballInfo, alfieInfo, opponentInfo);
		return result;
	}

	private double extractBallRollingSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	private double extractBallRollingDirection() {
		// TODO Auto-generated method stub
		return 0;
	}

	private double extractAlfieTravelSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	private double extractAlfieTravelDirection() {
		// TODO Auto-generated method stub
		return 0;
	}

	private double extractOpponentTravelSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	private double extractOpponentTravelDirection() {
		// TODO Auto-generated method stub
		return 0;
	}
}
