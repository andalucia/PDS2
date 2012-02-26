package group2.sdp.pc.planner;

import group2.sdp.pc.breadbin.DynamicPitchInfo;
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
	
	/**
	 * The FieldMarshal that will be executing the strategies that the overlord
	 * comes up with.
	 */
	protected FieldMarshal fieldMarshal;
	/**
	 * The current strategy that is being executed.
	 */
	protected Strategy currentStrategy;
	
	public Overlord(FieldMarshal fieldMarshal) {
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
		running = false;
	}

	/**
	 * When running, computes the strategy that should be employed depending 
	 * on the current pitch status and passes the information to the 
	 * FieldMarshal.
	 */
	@Override
	public void consumeInfo(DynamicPitchInfo dpi) {
		if (running) {
			Strategy strategy = computeStrategy(dpi);
			if (strategy != currentStrategy) {
				fieldMarshal.setStrategy(strategy);
			}
		} else {
			//TODO using null isn't very nice..?
			Strategy strategy = Strategy.STOP;
			fieldMarshal.setStrategy(strategy);
		}
		fieldMarshal.consumeInfo(dpi);
	}

	/**
	 * Most important method of the class. Computes the high-level strategy 
	 * that should be employed, depending on the current dynamic pitch 
	 * information.
	 * @param dpi The DynamicPitchInfo to use when deciding what strategy 
	 * should be employed.
	 * @return The strategy that should be currently employed.
	 */
	protected Strategy computeStrategy(DynamicPitchInfo dpi) {
		// TODO: implement
		if (!running) {
			return Strategy.STOP;
		}
		return Strategy.DEFENSIVE;
	}
}
