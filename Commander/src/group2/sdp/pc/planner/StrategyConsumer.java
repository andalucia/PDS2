package group2.sdp.pc.planner;

import group2.sdp.pc.planner.strategy.Strategy;

/**
 * Classes implementing this interface can consume a Strategy
 * @author: Alfie
 *
 */
public interface StrategyConsumer {
	
	/**
	 * Sets the strategy
	 * @param strategy The strategy to set to
	 */
	public void setStrategy(Strategy strategy);

	public void start();
	
	public void stop();
	
}
