package group2.sdp.pc.test;

import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.globalinfo.DynamicInfoChecker;
import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.globalinfo.Pitch;

import java.awt.geom.Point2D;

import org.junit.Assert;
import org.junit.Test;

// INCOMPLETE TESTS, just started working on them
public class DynamicInfoCheckerTest {

	DynamicInfo dynamicInfo;
	DynamicInfoChecker info = new DynamicInfoChecker(dynamicInfo);

	
	@Test
	public void isSimilarAngleTest(){
		double angle1A = 40.5;
		double angle2A = 40;
		double threshold1A = 40;
		Assert.assertTrue(DynamicInfoChecker.isSimilarAngle(angle1A, angle2A, threshold1A));
		
		//this should be change; more check-condition for threshold
		double angle1B = 40.5;
		double angle2B = 40;
		double threshold1B = -0.5;
		Assert.assertFalse(DynamicInfoChecker.isSimilarAngle(angle1B, angle2B, threshold1B));
		
		double angle1C = 370;
		double angle2C = 390;
		double threshold1C = 30;
		Assert.assertTrue(DynamicInfoChecker.isSimilarAngle(angle1C, angle2C, threshold1C));
	}
	
	@Test
	public void getAngleToBallTest(){
		Point2D.Double positionTarget1= new Point2D.Double(23.0,23.0);
		Point2D.Double positionRobot1= new Point2D.Double(12.0,12.0);
		double facingDirection1= 90;
		
		Assert.assertEquals(-45,info.getAngleToBall(positionTarget1, positionRobot1, facingDirection1));

		
		Point2D.Double positionTarget2= new Point2D.Double(97.0,45.0);
		Point2D.Double positionRobot2= new Point2D.Double(56.0,67.0);
		double facingDirection2= 270;
	
		Assert.assertEquals(61,info.getAngleToBall(positionTarget2, positionRobot2, facingDirection2));
		
		Point2D.Double positionTarget3= new Point2D.Double(-120,-45.0);
		Point2D.Double positionRobot3= new Point2D.Double(-56.0,-64.0);
		double facingDirection3= 0;
		
		Assert.assertEquals(163,info.getAngleToBall(positionTarget3, positionRobot3, facingDirection3));
		
	}

	public void testHasBallCase(Point2D robotPosition,
			Point2D ballPosition, double direction, boolean expected){
		DynamicRobotInfo dri = new DynamicRobotInfo(robotPosition, direction, true, false, 0, direction, 0, false, 0);
		boolean actual = DynamicInfoChecker.hasBall(dri, ballPosition);
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void testHasBall(){
		testHasBallCase(new Point2D.Double(20,20), new Point2D.Double(35,20), 0, true);
		testHasBallCase(new Point2D.Double(20,20), new Point2D.Double(40,20), 0, false);
		testHasBallCase(new Point2D.Double(20,20), new Point2D.Double(20,30), 0, false);
		testHasBallCase(new Point2D.Double(20,20), new Point2D.Double(27,27), 45, true);
		testHasBallCase(new Point2D.Double(20,20), new Point2D.Double(13,13), 135, false);
		}
	
	public void testCorrectSideCase(Point2D robotPosition,
			boolean isAlfie, boolean isAttackingRight,
			Point2D ballPosition, boolean expected){

		GlobalInfo.setPitch(Pitch.ONE);
		GlobalInfo.setPitchOne(true);
		
		GlobalInfo.setAttackingRight(isAttackingRight);
		DynamicRobotInfo dri = new DynamicRobotInfo(robotPosition, 0, isAlfie, false, 0, 0, 0, false, 0);
		
		boolean actual = DynamicInfoChecker.defensiveSide(dri, ballPosition);
		Assert.assertEquals(actual,expected);
	}
	
	@Test
	public void testCorrectSide(){
		testCorrectSideCase(new Point2D.Double(50,20), true, false, new Point2D.Double(20,20), true);
		testCorrectSideCase(new Point2D.Double(50,20), false, true,	new Point2D.Double(20,20), true);
		testCorrectSideCase(new Point2D.Double(20,20), true, false, new Point2D.Double(50,20), false);
		testCorrectSideCase(new Point2D.Double(30,20), false, false, new Point2D.Double(20,20), false);
		testCorrectSideCase(new Point2D.Double(10,20), false, true, new Point2D.Double(20,20), false);
		testCorrectSideCase(new Point2D.Double(100,0), true, true, new Point2D.Double(5,0), false);
	}
	
}