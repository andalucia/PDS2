package group2.sdp.pc.test;

import group2.sdp.common.util.Geometry;
import group2.sdp.pc.breadbin.StaticRobotInfo;
import group2.sdp.pc.planner.PathFinder;
import group2.sdp.pc.planner.pathstep.PathStep;
import group2.sdp.pc.planner.pathstep.PathStepArc;
import group2.sdp.pc.planner.pathstep.PathStepArcForwardsLeft;
import group2.sdp.pc.planner.pathstep.PathStepArcForwardsRight;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import junit.framework.Assert;

import org.junit.Test;

public class PathFinderTest {
	
	public void testDoubleArcCurveCase(Point2D start, double startdir, Point2D end, 
			double enddir, LinkedList<PathStep> expected) {
		
		LinkedList<PathStep> actual = new PathFinder(null).getDoubleArcPath(
				start, 
				startdir,
				end,
				enddir,
				false /* variant */, 
				0.0
		);
		
		Assert.assertTrue(actual.size() == expected.size());
		double threshold = 0.0001;
				
		/* variant: type */
		PathStepArc arcExpectedFirst = (PathStepArc) expected.get(0);
		PathStepArc arcActualFirst = (PathStepArc) actual.get(0);
		//System.out.println(arc2);
		
//				System.out.println("Expected: " + arcExpectedFirst);
//				System.out.println("Actual: " + arcActualFirst);
		
		Assert.assertEquals(arcExpectedFirst.getType(), arcActualFirst.getType());
		Assert.assertTrue(Math.abs(arcExpectedFirst.getTargetOrientation() - arcActualFirst.getTargetOrientation()) < threshold);
		Assert.assertTrue(Math.abs(arcExpectedFirst.getTargetDestination().distance(arcActualFirst.getTargetDestination())) < threshold);

		/* variant: type */
		PathStepArc arcExpectedSecond = (PathStepArc) expected.get(1);
		PathStepArc arcActualSecond = (PathStepArc) actual.get(1);

//				System.out.println("Expected: " + arcExpectedSecond);
//				System.out.println("Actual: " + arcActualSecond);
		
		Assert.assertEquals(arcExpectedSecond.getType(), arcActualSecond.getType());
		Assert.assertTrue(Math.abs(arcExpectedSecond.getTargetOrientation() - arcActualSecond.getTargetOrientation()) < threshold);
		Assert.assertTrue(Math.abs(arcExpectedSecond.getTargetDestination().distance(arcActualSecond.getTargetDestination())) < threshold);
	}
	
	@Test
	public void testDoubleArcCurve() {
//		Point2D start = new Point2D.Double(-80, 0.0);
//		double startDir = 0.0;
//		double startRadius = 140.0;
//		double startArcAngle = 0.0;
//		
////		Point2D middlePoint = Geometry.getArcEnd(start, startDir, startRadius, startAngle);
//		
//		Point2D end = new Point2D.Double(80.0, 1.0);
//		double endDir = 0.0;
//		double radius2 = 0.0;
//		double angle2 = 0.0;
//		
//		PathStepArc arc1 = 
//			new PathStepArcForwardsLeft(start, startDir, startRadius, startArcAngle, 20);
//		PathStepArc arc2 = 
//			new PathStepArcForwardsRight(
//					arc1.getTargetDestination(), 
//					arc1.getTargetOrientation(), 
//					radius2, angle2, 20);
//		
//		LinkedList<PathStep> expected = new LinkedList<PathStep>();
//		expected.add(arc1);
//		expected.add(arc2);
//		
//		testDoubleArcCurveCase(start, startDir, end, endDir, expected);
		
		//test 2
	}
	
	/**
	 * Testing CCW arcs with top pitch line and start or end point at 0, 50 (10 cm from the line).
	 */
	@Test
	public void testIsGoodArc1() {
		// docs/diagrams/ @ diagram 13
		PathFinder pf = new PathFinder(null);
		boolean good = pf.isGoodArc(
				new PathStepArcForwardsLeft(
						new Point2D.Double(-50.0, 0.0),
						0.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(120, 60),
				new Point2D.Double(-120, 60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
		Assert.assertEquals(false, good);
		
		// docs/diagrams/ @ diagram 14
		good = pf.isGoodArc(
				new PathStepArcForwardsLeft(
						new Point2D.Double(50.0, 0.0),
						90.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(120, 60),
				new Point2D.Double(-120, 60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
		Assert.assertEquals(true, good);
		
		good = pf.isGoodArc(
				new PathStepArcForwardsLeft(
						new Point2D.Double(0.0, 50.0),
						180.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(120, 60),
				new Point2D.Double(-120, 60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
		Assert.assertEquals(true, good);
		
		good = pf.isGoodArc(
				new PathStepArcForwardsLeft(
						new Point2D.Double(0.0, 50.0),
						270.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(120, 60),
				new Point2D.Double(-120, 60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
		Assert.assertEquals(true, good);
	}
	
	/**
	 * Testing CW arcs with top pitch line and start or end point at 0, 50 (10 cm from the line).
	 */
	@Test
	public void testIsGoodArc2() {
		PathFinder pf = new PathFinder(null);
		boolean good = pf.isGoodArc(
				new PathStepArcForwardsRight(
						new Point2D.Double(50.0, 0.0),
						180.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(120, 60),
				new Point2D.Double(-120, 60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
		Assert.assertEquals(false, good);
		
		good = pf.isGoodArc(
				new PathStepArcForwardsRight(
						new Point2D.Double(-50.0, 0.0),
						90.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(120, 60),
				new Point2D.Double(-120, 60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
//		// TODO: fix phi-s
		Assert.assertEquals(false, good);
		
		good = pf.isGoodArc(
				new PathStepArcForwardsRight(
						new Point2D.Double(0.0, 50.0),
						0.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(120, 60),
				new Point2D.Double(-120, 60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
//		// TODO: fix phi-s
		Assert.assertEquals(true, good);
		
		good = pf.isGoodArc(
				new PathStepArcForwardsRight(
						new Point2D.Double(0.0, 50.0),
						270.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(120, 60),
				new Point2D.Double(-120, 60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
		Assert.assertEquals(true, good);
	}
	
	/**
	 * Testing CCW arcs with bottom pitch line and start or end point at 0, -50 (10 cm from the line).
	 */
	@Test
	public void testIsGoodArc3() {
		PathFinder pf = new PathFinder(null);
		boolean good = pf.isGoodArc(
				new PathStepArcForwardsLeft(
						new Point2D.Double(50.0, 0.0),
						180.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(-120, -60),
				new Point2D.Double(120, -60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
		Assert.assertEquals(false, good);
		
		good = pf.isGoodArc(
				new PathStepArcForwardsLeft(
						new Point2D.Double(-50.0, 0.0),
						270.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(-120, -60),
				new Point2D.Double(120, -60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
//		// TODO: fix phi-s
		Assert.assertEquals(false, good);
		
		good = pf.isGoodArc(
				new PathStepArcForwardsLeft(
						new Point2D.Double(0.0, -50.0),
						0.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(-120, -60),
				new Point2D.Double(120, -60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
//		// TODO: fix phi-s
		Assert.assertEquals(false, good);
		
		good = pf.isGoodArc(
				new PathStepArcForwardsLeft(
						new Point2D.Double(0.0, -50.0),
						90.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(-120, -60),
				new Point2D.Double(120, -60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
		Assert.assertEquals(true, good);
	}
	
	/**
	 * Testing CW arcs with bottom pitch line and start or end point at 0, -50 (10 cm from the line).
	 */
	@Test
	public void testIsGoodArc4() {
		PathFinder pf = new PathFinder(null);
		boolean good = pf.isGoodArc(
				new PathStepArcForwardsRight(
						new Point2D.Double(-50.0, 0.0),
						0.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(-120, -60),
				new Point2D.Double(120, -60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
		Assert.assertEquals(false, good);
		
		good = pf.isGoodArc(
				new PathStepArcForwardsRight(
						new Point2D.Double(50.0, 0.0),
						270.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(-120, -60),
				new Point2D.Double(120, -60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
		// TODO: fix phi-s
//		Assert.assertEquals(false, good);
		
		good = pf.isGoodArc(
				new PathStepArcForwardsRight(
						new Point2D.Double(0.0, -50.0),
						180.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(-120, -60),
				new Point2D.Double(120, -60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
//		// TODO: fix phi-s
		Assert.assertEquals(true, good);
		
		good = pf.isGoodArc(
				new PathStepArcForwardsRight(
						new Point2D.Double(0.0, -50.0),
						90.0,
						50.0,
						90.0,
						0.0
				), 
				new Point2D.Double(-120, -60),
				new Point2D.Double(120, -60),
				new StaticRobotInfo(
						null,
						0.0,
						true,
						false,
						0
				)
		);
		Assert.assertEquals(true, good);
	}
}
