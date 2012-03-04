package group2.sdp.pc.breadbin;

import java.util.LinkedList;

/**
 * Description: A wrapper around a Linked List of Static Pitch Info objects. 
 *               Can generate lists of the Static Ball Info objects, Alfie's 
 *               Static Robot Info objects, and opponent's Static Robot Info
 *               objects that are contained in the list of SPIs. Convenient.
 * Contains:    Nothing, just extends LinkedList <StaticPitchInfo> and adds new
 *               methods.
 */
// We don't use serialisation.
@SuppressWarnings("serial")
public class StaticInfoHistory extends LinkedList<StaticInfo> {
	
		public LinkedList<StaticBallInfo> getBallInfos() {
			LinkedList<StaticBallInfo> result = new LinkedList<StaticBallInfo>();
			for (StaticInfo spi : this) {
				result.add(spi.getBallInfo());
			}
			return result;
		}
		
		public LinkedList<StaticRobotInfo> getOpponentInfos() {
			LinkedList<StaticRobotInfo> result = new LinkedList<StaticRobotInfo>();
			for (StaticInfo spi : this) {
				result.add(spi.getOpponentInfo());
			}
			return result;
		}
		
		public LinkedList<StaticRobotInfo> getAlfieInfos() {
			LinkedList<StaticRobotInfo> result = new LinkedList<StaticRobotInfo>();
			for (StaticInfo spi : this) {
				result.add(spi.getAlfieInfo());
			}
			return result;
		}
}
