package group2.sdp.pc.vision.stan;

import java.util.Queue;

import group2.sdp.common.breadbin.StaticPitchInfo;

/**
 * This class consumes static information about the pitch from an image 
 * processor and produces dynamic information about the pitch.
 */
public class Bakery implements StaticInfoConsumer {

	private static final int HISTORY_LENGTH = 1024;
	
	private Queue<StaticPitchInfo> staticInfoHistory;
	
	
	
	@Override
	public void consumeInfo(StaticPitchInfo spi) {
		
	}
	
}
