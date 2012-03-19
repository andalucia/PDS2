package group2.sdp.pc.test;

import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.StaticBallInfo;
import group2.sdp.pc.breadbin.StaticRobotInfo;
import group2.sdp.pc.planner.FieldMarshal;

import org.junit.Assert;
import org.junit.Test;

public class ButtRuleTest {
	
	public void testButtRuleCase(double ballX, double ballY, 
			double endX, double endY, double endDirection, boolean expected) {
		
		Point2D ballPosition = new Point2D.Double(ballX, ballY);
		
		long timeStamp = System.currentTimeMillis();
		StaticBallInfo ballInfo = new StaticBallInfo(ballPosition, timeStamp);
		Point2D robotPosition = new Point2D.Double(0.0, 0.0);
		Point2D endPosition = new Point2D.Double(endX, endY);
		
		StaticRobotInfo robotInfo = 
			new StaticRobotInfo(
					robotPosition, 
					0, 
					true,
					false, 
					timeStamp
			);
		
		boolean result = 
			FieldMarshal.checkButtRule(
					ballInfo, 
					robotInfo, 
					endPosition, 
					endDirection
			); 
		System.out.println("result = " + result);
		Assert.assertEquals(expected, result);
	}
		
	@Test 
	public void testButtRule() {
		// Should return true
		testButtRuleCase(-30, -20, -47, 5, 110, false);
		testButtRuleCase(-30, -20, -47, 5, 30, true);
		// 2 points of intersection
		//testButtRuleCase(-30, -20, -30, 10, 45, false);
		// on the tangent
		testButtRuleCase(17, 17, -1, -1, 270, true);
		testButtRuleCase(-25, 40.494, 0, 0, 315, false);
		testButtRuleCase(0, 40.494, 0, 0, 270, false);
	}
}
