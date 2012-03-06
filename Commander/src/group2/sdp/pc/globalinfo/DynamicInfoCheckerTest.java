package group2.sdp.pc.globalinfo;

import static org.junit.Assert.*;

import group2.sdp.pc.breadbin.DynamicInfo;

import java.awt.geom.Point2D;

import org.junit.Assert;
import org.junit.Test;

// INCOMPLETE TESTS, just started working on them
public class DynamicInfoCheckerTest {

	GlobalInfo globalInfo;
	DynamicInfo dynamicInfo;
	DynamicInfoChecker info = new DynamicInfoChecker(globalInfo, dynamicInfo);

	
	@Test
	public void isSimilarAngleTest(){
		double angle1A = 40.5;
		double angle2A = 40;
		double threshold1A = 40;
		Assert.assertTrue(info.isSimilarAngle(angle1A, angle2A, threshold1A));
		
		//this should be change; more check-condition for threshold
		double angle1B = 40.5;
		double angle2B = 40;
		double threshold1B = -0.5;
		Assert.assertFalse(info.isSimilarAngle(angle1B, angle2B, threshold1B));
		
		double angle1C = 370;
		double angle2C = 390;
		double threshold1C = 30;
		Assert.assertTrue(info.isSimilarAngle(angle1C, angle2C, threshold1C));
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
	
	@Test
	public void getAngleFromOriginTest(){
		Point2D.Double origin1= new Point2D.Double(33.0,23.0);
		Point2D.Double targetPosition1= new Point2D.Double(22.0,12.0);

		
		Point2D.Double origin2= new Point2D.Double(120.0,130.0);
		Point2D.Double targetPosition2= new Point2D.Double(89.0,20.0);
		
	}	
}
