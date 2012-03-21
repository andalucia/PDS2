package group2.sdp.pc.controlstation;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.mouth.Mouth;
import group2.sdp.pc.planner.PathFinder;
import group2.sdp.pc.planner.operation.OperationReallocation;
import group2.sdp.pc.planner.pathstep.PathStep;

import java.awt.geom.Point2D;
import java.util.LinkedList;

/**
 * Used for quick test of Mouth-to-Ear communication.
 * Do not forget about Thread.sleep() after each command and s.cleanup()
 * after all commands were executed.
 */
public class Conversation {
	
	public static void main (String [] args) throws Exception {
		Mouth s = new Mouth();
		
		s.sendGoForward(100, 0);
		Thread.sleep(5000);
		
		Point2D start = new Point2D.Double(0.0, 0.0);
		double startdir = 90;
		
		Point2D end = new Point2D.Double(40.0, 40.0);
		double enddir = 90;
		
		DynamicRobotInfo alfieInfo = new DynamicRobotInfo(
				start, 
				startdir, 
				true, 
				false, 
				0.0, 
				startdir, 
				0, 
				false, 
				0
		);
		
		DynamicBallInfo ballInfo = null;
		DynamicRobotInfo opponentInfo = null;
		
		DynamicInfo pitch = new DynamicInfo(ballInfo, alfieInfo, opponentInfo);
		
		OperationReallocation op = new OperationReallocation(end, enddir);
		
		LinkedList<PathStep> doubleArcPath = PathFinder.getDoubleArcPath(pitch, op, true);
		doubleArcPath.get(0).execute(s);
		Thread.sleep(5000);
		doubleArcPath.get(1).execute(s);
		Thread.sleep(5000);
		
		s.sendStop();
		Thread.sleep(1000);
		s.sendReset();
		Thread.sleep(1000);
		s.cleanup();
	}
	
	/**
	 * Converts time difference to frames per second.
	 * @param before System time, in milliseconds, of the beginning of the
	 * interval.
	 * @param now System time, in milliseconds, of the end of the interval.
	 * @return FPS rate, based on the given period.
	 */
	public static double getFPS(long before, long now){
		return (1000.0 / (now - before));
	}
}
