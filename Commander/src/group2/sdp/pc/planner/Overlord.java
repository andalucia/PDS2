package group2.sdp.pc.planner;

import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.globalinfo.DynamicInfoChecker;
import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.planner.strategy.Strategy;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

/**
 * The skeleton of an Overlord class. Depending on the DynamicPitchInfo, 
 * decides on the high-level strategy to employ: Offensive, Defensive, Penalty
 * Defend, or Penalty Take. 
 */
public class Overlord implements DynamicInfoConsumer {
	 
	/**
	 * Indicates if the overlord is running or not.
	 */
	protected boolean running = false;
	
	protected DynamicInfoChecker dynamicInfoChecker;
	
	// FIXME: restructure
	public static final int RweClose = 20;
	
	/**
	 * The FieldMarshal that will be executing the strategies that the overlord
	 * comes up with.
	 */
	protected FieldMarshal fieldMarshal;
	/**
	 * The current strategy that is being executed.
	 */
	protected Strategy currentStrategy;

	/**
	 * Stopping the Overlord. Sending a STOP strategy.
	 */
	private boolean stopping;
	
	private GlobalInfo globalInfo;
	
	public Overlord(GlobalInfo globalInfo, FieldMarshal fieldMarshal) {
		this.globalInfo = globalInfo;
		this.fieldMarshal = fieldMarshal;
	}
	
	/**
	 * When this method is invoked the Overlord starts computing the strategy
	 * and poking the FieldMarshal with new DynamicPitchInfos.
	 */
	public void start() {
		running = true;
	}
	
	/**
	 * When this method is invoked the Overlord stops computing the strategy
	 * and poking the FieldMarshal with new DynamicPitchInfos.
	 */
	public void stop() {
		stopping = true;
		// Running is set to false once a stop command is sent to Alfie
	}

	/**
	 * When running, computes the strategy that should be employed depending 
	 * on the current pitch status and passes the information to the 
	 * FieldMarshal.
	 */
	@Override
	public void consumeInfo(DynamicInfo dpi) {
		dynamicInfoChecker = new DynamicInfoChecker(globalInfo,dpi);
		if (running) {
			Strategy strategy = computeStrategy(dpi);
			if (strategy != currentStrategy) {
				fieldMarshal.setStrategy(strategy);
			}
			fieldMarshal.consumeInfo(dpi);
		}
	}

	/**
	 * Most important method of the class. Computes the high-level strategy 
	 * that should be employed, depending on the current dynamic pitch 
	 * information.
	 * @param dpi The DynamicPitchInfo to use when deciding what strategy 
	 * should be employed.
	 * @return The strategy that should be currently employed.
	 */
	protected Strategy computeStrategy(DynamicInfo dpi) {
		if (stopping) {
			stopping = false;
			running = false;
			return Strategy.STOP;
		}
		DynamicRobotInfo alfieInfo = dpi.getAlfieInfo();
		DynamicRobotInfo opponentInfo = dpi.getOpponentInfo();
		DynamicBallInfo ballInfo = dpi.getBallInfo();
		
		
		// FIXME: restructure
		Point2D ball = ballInfo.getPosition();  
		
		if((dynamicInfoChecker.hasBall(opponentInfo, ball) && dynamicInfoChecker.correctSide(opponentInfo, ball)) || !dynamicInfoChecker.correctSide(alfieInfo,ball)){
			return Strategy.DEFENSIVE;
			
		} else {
			return Strategy.OFFENSIVE;
		}
	}
	
}
