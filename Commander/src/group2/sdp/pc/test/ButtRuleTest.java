package group2.sdp.pc.test;

import java.awt.geom.Point2D;

import group2.sdp.pc.breadbin.StaticBallInfo;
import group2.sdp.pc.breadbin.StaticRobotInfo;
import group2.sdp.pc.planner.FieldMarshal;

import org.junit.Assert;
import org.junit.Test;

public class ButtRuleTest {
	
	public void testButtRuleCase(double ballX, double ballY, double endX, double endY, double endDirection, boolean expected) {
		
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
		if (expected)
			Assert.assertTrue(result);
		else 
			Assert.assertFalse(result);
	}
	
	@Test 
	public void testButtRule() {
		// Should return true
		testButtRuleCase(-30, -20, -45, 10, 30, true);
		testButtRuleCase(-30, -20, -30, 10, 45, false);
		testButtRuleCase(17, 17, 0, 0, 270, true);
		testButtRuleCase(17, 17, 0, 0, 90, true);
		
	}
}
