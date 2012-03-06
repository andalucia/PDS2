package group2.sdp.pc.globalinfo;

import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;

import java.awt.Point;

public class DynamicInfoCheckerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		GlobalInfo globalInfo = new GlobalInfo(true, true, Pitch.TWO);
		//findCircleTangentIntersect
//		System.out.println(DynamicInfoChecker.findCircleTangentIntersect(new Point.Double(3,3), new Point.Double(0.5,0.5), 1.5));
//		System.out.println(DynamicInfoChecker.findCircleTangentIntersect(new Point.Double(5,0), new Point.Double(0,0), 3));
//		System.out.println(DynamicInfoChecker.findCircleTangentIntersect(new Point.Double(5,0), new Point.Double(0,-1), 1));
//		System.out.println(DynamicInfoChecker.findCircleTangentIntersect(new Point.Double(-4,4), new Point.Double(3,3), 5));

		
		//findTangentIntersect
//		System.out.println("findTangentIntersect tests");
//		System.out.println(DynamicInfoChecker.findTangentIntersect(new Point.Double(5,0), 
//				new Point.Double(-3,-3), 
//				new Point.Double(0,-1), 
//				1
//				));
//		System.out.println(DynamicInfoChecker.findTangentIntersect(new Point.Double(-4,4), 
//				new Point.Double(10,10), 
//				new Point.Double(3,3), 
//				5
//				));
		
		System.out.println("shotOnGoal tests");
		DynamicRobotInfo alfieInfo = new DynamicRobotInfo(new Point(0,0), 0, true, 10, 0, 0);
		DynamicRobotInfo enemyInfo = new DynamicRobotInfo(new Point(-30,-30), 0, false, 10, 0, 0);
		DynamicBallInfo ballInfo = new DynamicBallInfo(new Point(10,0), 0, 0, 0);
		DynamicInfo di = new DynamicInfo(ballInfo, alfieInfo, enemyInfo);
		DynamicInfoChecker diChecker = new DynamicInfoChecker(globalInfo, di);
		//System.out.println(diChecker.shotOnGoal(alfieInfo, enemyInfo, ballInfo.getPosition()));
		
		alfieInfo = new DynamicRobotInfo(new Point(-29,0), 0, true, 10, 0, 0);
		enemyInfo = new DynamicRobotInfo(new Point(0,15), 0, false, 10, 0, 0);
		ballInfo = new DynamicBallInfo(new Point(10,0), 0, 0, 0);
		di = new DynamicInfo(ballInfo, alfieInfo, enemyInfo);
		diChecker = new DynamicInfoChecker(globalInfo, di);
		System.out.println(diChecker.shotOnGoal(alfieInfo, enemyInfo, ballInfo.getPosition()));
	}

}
