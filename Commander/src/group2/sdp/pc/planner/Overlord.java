package group2.sdp.pc.planner;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.controlstation.ControlStation;
import group2.sdp.pc.globalinfo.DynamicInfoChecker;
import group2.sdp.pc.planner.skeleton.StrategyConsumer;
import group2.sdp.pc.planner.strategy.Strategy;
import group2.sdp.pc.vision.Bakery;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

import java.awt.geom.Point2D;


/**
 *<p><b>Description</b>: "If they have no bread, let them eat cake!" The popularity of 
*               this misquotation and the subjects of its metaphor suggest that
*              the products of a bakery would be of interest to a majesty. Thus
*               the name of the class that consumes the products of the {@link Bakery} 
*               and produces decisions on what {@link Strategy} to use is Overlord. It 
*               passes the Strategy to a Strategy Consumer, supplied on 
*               construction of the Overlord. Also passes the {@link DynamicInfo} that
*               was received down to another {@link DynamicInfoConsumer}, supplied on
*               construction of the Overlord. Note that the two Consumers can 
*               be the same object, but the Overlord does not need to know.</p>
* <p><b>Main client</b>:	{@link FieldMarshal}</p>
* <p><b>Produces</b>:	{@link Strategy}</p>
* <p><b>Responsibilities</b>:</br>
*              Producing a Strategy and monitoring if it is successful or if a 
*               problem occurs.</p>
* <p><b>Policy</b>:</br>      
*  Planning:</br>   Analysing the DynamicInfo (*how*), the Overlord comes up with a Strategy.
*               After that, it checks the success of the strategy or if a 
*               problem occurred on each DynamicInfo it receives. If there is either,
*               the Overlord comes up with a new Strategy. Otherwise, just 
*               passes the DynamicInfo to its DynamicInfoConsumer.</p>
 */
public class Overlord implements DynamicInfoConsumer {
	 
	private static final boolean VERBOSE = true;

	/**
	 * Indicates if the overlord is running or not.
	 */
	protected boolean running = false;
	
	/**
	 * The object to which we pass the Strategy
	 */
	protected StrategyConsumer strategyConsumer;
	
	/**
	 * The object to which we pass on the DynamicInfo
	 */
	protected DynamicInfoConsumer dynamicInfoConsumer;
	
	/**
	 * The current strategy that is being executed.
	 */
	protected Strategy currentStrategy;
	
	/**
	 * Tells the Overlord if we are defending a penalty
	 */
	protected boolean defendPenalty = false;
	
	/**
	 * Time at which we started defending a penalty
	 */
	protected long penaltyStart;

	/**
	 * Stopping the Overlord. Sending a STOP strategy.
	 */
	private boolean stopping;
	
	public Overlord(StrategyConsumer strategyConsumer, DynamicInfoConsumer dynamicInfoConsumer) {
		this.strategyConsumer = strategyConsumer;
		this.dynamicInfoConsumer = dynamicInfoConsumer;
	}
	
	/**
	 * When this method is invoked the Overlord starts computing the strategy
	 * and poking the FieldMarshal with new DynamicPitchInfos.
	 */
	public void start() {
		running = true;
		strategyConsumer.start();
	}
	
	/**
	 * When this method is invoked the Overlord stops computing the strategy
	 * and poking the FieldMarshal with new DynamicPitchInfos.
	 */
	public void stop() {
		if (running) {
			stopping = true;
			strategyConsumer.stop();
		} else {
			ControlStation.log("Overlord is busy conquering elsewhere.");
		}
		// Running is set to false once a stop command is sent to Alfie
	}
	
	/**
	 * Start defending a penalty
	 */
	public void defendPenalty() {
		defendPenalty = true;
		penaltyStart = System.currentTimeMillis();
		start();
	}

	
	private long lastStrategyIssueTime = 0;
	private final long REPLAN_PERIOD = 3000;
	
	/**
	 * When running, computes the strategy that should be employed depending 
	 * on the current pitch status and passes the information to the 
	 * FieldMarshal.
	 */
	@Override
	public void consumeInfo(DynamicInfo dpi) {
		if (running) {
			long now = System.currentTimeMillis();
			if (now - lastStrategyIssueTime > REPLAN_PERIOD) {
				lastStrategyIssueTime = now;
				Strategy strategy = computeStrategy(dpi);
				if (strategy != currentStrategy) {
					strategyConsumer.setStrategy(strategy);
					currentStrategy = strategy;
				}
			}
			dynamicInfoConsumer.consumeInfo(dpi);
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
			defendPenalty = false;
			return Strategy.STOP;
		}
		if (defendPenalty) {
			if (System.currentTimeMillis() - penaltyStart > 30000 ||
					dpi.getBallInfo().getRollingSpeed() > 5) {
				// 30 seconds has passed or ball has moved
				if (VERBOSE) {
					System.out.println("Exiting penalty mode");
				}
				
				defendPenalty = false;
				computeStrategy(dpi);
				//TODO check if 5 is a good threshold
			} else {
				return Strategy.PENALTY_DEFEND;
			}
		}
//		return Strategy.TEST;
		
		DynamicRobotInfo alfieInfo = dpi.getAlfieInfo();
		DynamicRobotInfo opponentInfo = dpi.getOpponentInfo();
		DynamicBallInfo ballInfo = dpi.getBallInfo();
		
		Point2D ballPosition = ballInfo.getPosition();  
		
		if (DynamicInfoChecker.isInAttackingPosition(opponentInfo, ballPosition)
				|| !DynamicInfoChecker.defensiveSide(alfieInfo,ballPosition)) {
			ControlStation.log("Defending.");
			return Strategy.DEFENSIVE;
		} else {
			ControlStation.log("Attacking.");
			return Strategy.OFFENSIVE;
		}
	}
	
	/**
	 * 1. Strategy is Offensive - successful if we score a goal;
       2. Strategy is Defensive - successful if there is no threat of the 
           other team scoring a goal; 
       3. Strategy is Take a Penalty - successful if we score a goal;
       4. Strategy is Defend a Penalty - successful if Alfie prevents the 
           opponent from scoring a penalty; and 
       5. Strategy is Stealth - successful if Alfie stops.
       TODO: implement the above
	 * @param di
	 * @return
	 */
	protected boolean strategySuccessful(DynamicInfo di) {
		return true;
	}
	
	/**
	 * 1. Strategy is Offensive - problem exists if the other robot gets the
           ball;
       2. Strategy is Defensive - problem exists if the other team scores 
           a goal; 
       3. Strategy is Take a Penalty - problem exists if Alfie misses after 
           the shot;
       4. Strategy is Defend a Penalty - problem exists if the opponent 
           scores a goal; and 
       5. Strategy is Stealth - problem exists if Alfie is being moved by 
          the other robot.
          TODO: implement the above
	 * @param di
	 * @return
	 */
	protected boolean problemExists(DynamicInfo di) {
		return true;
	}
}
