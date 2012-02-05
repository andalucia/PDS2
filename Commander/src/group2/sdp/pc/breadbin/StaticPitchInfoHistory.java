package group2.sdp.pc.breadbin;

import java.util.LinkedList;

/**
 * Adds functionality for separately extracting the lists of ball infos,
 * opponent robot infos and alfie infos. 
 */
// We don't use serialization.
@SuppressWarnings("serial")
public class StaticPitchInfoHistory extends LinkedList<StaticPitchInfo> {
	
		public LinkedList<StaticBallInfo> getBallInfos() {
			LinkedList<StaticBallInfo> result = new LinkedList<StaticBallInfo>();
			for (StaticPitchInfo spi : this) {
				result.add(spi.getBallInfo());
			}
			return result;
		}
		
		public LinkedList<StaticRobotInfo> getOpponentInfos() {
			LinkedList<StaticRobotInfo> result = new LinkedList<StaticRobotInfo>();
			for (StaticPitchInfo spi : this) {
				result.add(spi.getOpponentInfo());
			}
			return result;
		}
		
		public LinkedList<StaticRobotInfo> getAlfieInfos() {
			LinkedList<StaticRobotInfo> result = new LinkedList<StaticRobotInfo>();
			for (StaticPitchInfo spi : this) {
				result.add(spi.getAlfieInfo());
			}
			return result;
		}
}
