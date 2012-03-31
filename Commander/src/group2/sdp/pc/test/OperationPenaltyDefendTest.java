package group2.sdp.pc.test;

import java.awt.geom.Point2D;

import org.junit.Test;

import junit.framework.Assert;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.planner.operation.OperationPenaltyDefend;

public class OperationPenaltyDefendTest {

	public void isAngleIncreasingCase(boolean isOpponentRotatingCCW, 
			double rotatingSpeed, 
			double alfieFacingDirection,
			boolean expected) {
		OperationPenaltyDefend op = new OperationPenaltyDefend(alfieFacingDirection);
		boolean actual = op.isAngleIncreasing(isOpponentRotatingCCW, rotatingSpeed);
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void isAngleIncreasingTest() {
		isAngleIncreasingCase(true, 1, 90, true);
		isAngleIncreasingCase(false, 1, 90, false);
		isAngleIncreasingCase(true, 0, 90, false);
		isAngleIncreasingCase(false, 0, 90, false);
		
		isAngleIncreasingCase(true, 1, 270, false);
		isAngleIncreasingCase(false, 1, 270, true);
	}
	
	public void isAngleDecreasingCase(boolean isOpponentRotatingCCW, 
			double rotatingSpeed, 
			double alfieFacingDirection,
			boolean expected) {
		OperationPenaltyDefend op = new OperationPenaltyDefend(alfieFacingDirection);
		boolean actual = op.isAngleDecreasing(isOpponentRotatingCCW, rotatingSpeed);
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void isAngleDecreasingTest() {
		isAngleDecreasingCase(true, 1, 90, false);
		isAngleDecreasingCase(false, 1, 90, true);
		isAngleDecreasingCase(true, 0, 90, false);
		isAngleDecreasingCase(false, 0, 90, false);
		
		isAngleDecreasingCase(true, 1, 270, true);
		isAngleDecreasingCase(false, 1, 270, false);
	}
	
	public void getCurrentSectorCase(Point2D alfiePosition, 
			double alfieFacingDirection, 
			int expected) {
		OperationPenaltyDefend op = new OperationPenaltyDefend(alfieFacingDirection);
		int actual = op.getCurrentSector(alfiePosition);
		Assert.assertEquals(expected,actual);
	}
	
	@Test
	public void getCurrentSectorTest() {
		getCurrentSectorCase(new Point2D.Double(-120,0), 
				90, 
				2
				);
		getCurrentSectorCase(new Point2D.Double(-120,0), 
				120, 
				2
				);
		getCurrentSectorCase(new Point2D.Double(-120,0), 
				270, 
				2
				);
		getCurrentSectorCase(new Point2D.Double(120,0), 
				90, 
				2
				);
		getCurrentSectorCase(new Point2D.Double(120,0), 
				270, 
				2
				);
		getCurrentSectorCase(new Point2D.Double(120,3), 
				90, 
				2
				);
		getCurrentSectorCase(new Point2D.Double(-120,-3), 
				90, 
				2
				);
		
		getCurrentSectorCase(new Point2D.Double(-120,-6.9), 
				90, 
				2
				);
		getCurrentSectorCase(new Point2D.Double(-120,6.9), 
				270, 
				2
				);
		getCurrentSectorCase(new Point2D.Double(-120,7.5), 
				90, 
				1
				);
		getCurrentSectorCase(new Point2D.Double(-120,7), 
				90, 
				1
				);
		getCurrentSectorCase(new Point2D.Double(-120,-7.5), 
				90, 
				3
				);
		getCurrentSectorCase(new Point2D.Double(-120,7.5), 
				270, 
				3
				);
		getCurrentSectorCase(new Point2D.Double(-120,-7.5), 
				270, 
				1
				);
		getCurrentSectorCase(new Point2D.Double(-120,-7.5), 
				181, 
				1
				);
		getCurrentSectorCase(new Point2D.Double(-120,-7.5), 
				179, 
				3
				);
	}
	
	public void getOpponentFacingSectorCase(Point2D alfiePosition, 
			double opponentFacingDirection, 
			boolean isAttackingRight, 
			double alfieFacingDirection,
			int expected) {
		GlobalInfo.setAttackingRight(isAttackingRight);
		OperationPenaltyDefend op = new OperationPenaltyDefend(alfieFacingDirection);
		int actual = op.getOpponentFacingSector(alfiePosition, opponentFacingDirection);
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void getOpponentFacingSectorTest() {
		getOpponentFacingSectorCase(new Point2D.Double(-120,0), 180, true, 90, 2);
		getOpponentFacingSectorCase(new Point2D.Double(-120,0), 135, true, 90, 1);
		getOpponentFacingSectorCase(new Point2D.Double(-120,0), 225, true, 90, 3);
		getOpponentFacingSectorCase(new Point2D.Double(-120,0), 189, true, 90, 2);
		getOpponentFacingSectorCase(new Point2D.Double(-120,0), 171, true, 90, 2);
		getOpponentFacingSectorCase(new Point2D.Double(-120,0), 170, true, 90, 1);
		
		getOpponentFacingSectorCase(new Point2D.Double(120,0), 0, false, 90, 2);
		getOpponentFacingSectorCase(new Point2D.Double(120,0), 9, false, 90, 2);
		getOpponentFacingSectorCase(new Point2D.Double(120,0), 351, false, 90, 2);
		getOpponentFacingSectorCase(new Point2D.Double(120,0), 350, false, 90, 3);
		getOpponentFacingSectorCase(new Point2D.Double(120,0), 10, false, 90, 1);
		
	}
	
	public void getDesiredSectorCase(double rotatingSpeed, 
			boolean isRotatingCounterClockWise, 
			int opponentFacingSector, 
			double alfieFacingDirection,
			int expected) {
		OperationPenaltyDefend op = new OperationPenaltyDefend(alfieFacingDirection);
		DynamicRobotInfo opponentInfo = new DynamicRobotInfo(null, 0, false, true, 0, 0, 
				rotatingSpeed, isRotatingCounterClockWise, 0);
		int actual = op.getDesiredSector(opponentInfo, opponentFacingSector);
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void getDesiredSectorTest() {
		getDesiredSectorCase(1, true, 3, 90, 3);
		getDesiredSectorCase(1, false, 3, 90, 2);
		getDesiredSectorCase(1, true, 3, 270, 2);
		getDesiredSectorCase(1, false, 3, 270, 3);
		getDesiredSectorCase(0, true, 2, 90, 2);
		getDesiredSectorCase(0, true, 2, 270, 2);
		getDesiredSectorCase(1, false, 2, 90, 1);
		getDesiredSectorCase(1, true, 2, 90, 3);
	}

}
