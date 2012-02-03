package group2.sdp.pc.vision;

import java.util.Queue;

import group2.sdp.common.breadbin.StaticBallInfo;
import group2.sdp.common.breadbin.StaticRobotInfo;
import group2.sdp.pc.vision.skeleton.BakerySkeleton;
import group2.sdp.pc.vision.skeleton.DynamicInfoConsumer;

/**
 * Implementation of a Bakery. Check the comments in BakerySkeleton. Should
 * compute dynamic information about a pitch. Just implement the abstract 
 * compute methods in the parent. Give an implementor of the DynamicInfoConsumer
 * interface (a Planner object, most probably) as argument when constructing an object.
 * Pass the constructed object as an argument to the constructor of the 
 * ImageProcessor you want to use.  
 */
public class Bakery extends BakerySkeleton {

	public Bakery(DynamicInfoConsumer consumer) {
		super(consumer);
	}

	@Override
	protected double computeBallRollingSpeed(
			Queue<StaticBallInfo> ballHistoryInfos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected double computeBallRollingDirection(
			Queue<StaticBallInfo> ballHistoryInfos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected double computeRobotTravelSpeed(Queue<StaticRobotInfo> historyInfos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected double computeRobotTravelDirection(
			Queue<StaticRobotInfo> historyInfos) {
		// TODO Auto-generated method stub
		return 0;
	}
}
