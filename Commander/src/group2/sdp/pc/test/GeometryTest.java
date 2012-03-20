package group2.sdp.pc.test;

import group2.sdp.common.util.Geometry;
import group2.sdp.common.util.Pair;
import group2.sdp.pc.breadbin.DynamicBallInfo;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.breadbin.DynamicRobotInfo;
import group2.sdp.pc.planner.PathFinder;
import group2.sdp.pc.planner.operation.OperationReallocation;
import group2.sdp.pc.planner.pathstep.PathStep;
import group2.sdp.pc.planner.pathstep.PathStepArc;
import group2.sdp.pc.planner.pathstep.PathStepArcForwardsRight;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import junit.framework.Assert;

import org.junit.Test;

public class GeometryTest {
	
	public void testSegmentArcIntersectionCase(
			Point2D segmentStart, Point2D segmentEnd, Point2D circleCentre, 
			double circleRadius, double arcStartAngle, double arcEndAngle,
			int expected) {
		int n = Geometry.getNumberOfLineSegmentArcIntersections(
				segmentStart, 
				segmentEnd, 
				circleCentre, 
				circleRadius, 
				arcStartAngle, 
				arcEndAngle
		);
		
		Assert.assertEquals(expected, n);
	}
	
	@Test
	public void testSegmentArcIntersection() {
		testSegmentArcIntersectionCase(
				new Point2D.Double(1.0, 1.0), 
				new Point2D.Double(2.0, 2.0), 
				new Point2D.Double(1.0, 1.0), 
				1.0, 
				0.0,
				90.0, 
				1
		);
		
		testSegmentArcIntersectionCase(
				new Point2D.Double(3.0, 0.0), 
				new Point2D.Double(0.0, 3.0), 
				new Point2D.Double(1.0, 1.0), 
				1.0, 
				0.0,
				90.0, 
				2
		);
		
		testSegmentArcIntersectionCase(
				new Point2D.Double(3.0, 0.0), 
				new Point2D.Double(-1.0, 3.0), 
				new Point2D.Double(1.0, 1.0), 
				1.0, 
				0.0,
				90.0, 
				0
		);
		
		testSegmentArcIntersectionCase(
				new Point2D.Double(-1.0, 1.0), 
				new Point2D.Double(1.0, -1.0), 
				new Point2D.Double(0.0, 0.0), 
				1.0,
				180.0,
				270.0, 
				0
		);
		
		testSegmentArcIntersectionCase(
				new Point2D.Double(-1.0, 1.0), 
				new Point2D.Double(1.0, -1.0), 
				new Point2D.Double(0.0, 0.0), 
				1.0,
				180.0, 
				270.0, 
				0
		);
		
		testSegmentArcIntersectionCase(
				new Point2D.Double(1.0, 0.0), 
				new Point2D.Double(1.0, 2.0), 
				new Point2D.Double(0.0, 1.0), 
				1.0,
				270.0,
				90.0, 
				1
		);
		
		testSegmentArcIntersectionCase(
				new Point2D.Double(2.5, 0.0), 
				new Point2D.Double(0.0, 2.5), 
				new Point2D.Double(0.0, 0.0), 
				2.0,
				0.0,
				90.0, 
				2
		);
		
		testSegmentArcIntersectionCase(
				new Point2D.Double(0.5, 0.0), 
				new Point2D.Double(-0.5, 2.0), 
				new Point2D.Double(-1.0, 1.0), 
				1.0,
				0.0,
				90.0, 
				2
		);
		
		testSegmentArcIntersectionCase(
				new Point2D.Double(1.0, 0.0), 
				new Point2D.Double(3.0, 0.0), 
				new Point2D.Double(0.0, 0.0), 
				2.0,
				0.0,
				90.0, 
				1
		);
	}
	
	public void testArcEndCase(Point2D arcStart, double arcStartDirection,
			double radius, double angle, Point2D expected) {
		Point2D p = Geometry.getArcEnd(arcStart, arcStartDirection, radius, angle);
		
		double goodEnough = 1e-6;
		Assert.assertTrue(expected.distance(p) < goodEnough);
	}
	
	@Test
	public void testArcEnd() {
		testArcEndCase(
				new Point2D.Double(1.0, 2.0),
				0.0,
				1.0,
				90,
				new Point2D.Double(2.0, 3.0)
		);
		
		testArcEndCase(
				new Point2D.Double(0.0, 0.0),
				0.0,
				1.0,
				-90,
				new Point2D.Double(1.0, -1.0)
		);
		
		testArcEndCase(
				new Point2D.Double(0.0, 0.0),
				0.0,
				-1.0,
				-90,
				new Point2D.Double(-1.0, 1.0)
		);
		
		testArcEndCase(
				new Point2D.Double(0.0, 0.0),
				0.0,
				-1.0,
				90,
				new Point2D.Double(-1.0, -1.0)
		);
	}
	public void testDirectionCase(Point2D start, Point2D end, double expected) {
		double p = Geometry.getVectorDirection(start, end);

		double goodEnough = 1e-2;
		Assert.assertTrue(Math.abs(expected - p) < goodEnough);
	}
	
	@Test
	public void testDirection() {
		testDirectionCase(new Point2D.Double(1,1), new Point2D.Double(2,2), 45);
		testDirectionCase(new Point2D.Double(0,0), new Point2D.Double(-3,-3), -135);
		testDirectionCase(new Point2D.Double(0,0), new Point2D.Double(3,-3), -45);
		testDirectionCase(new Point2D.Double(0,0), new Point2D.Double(-3,3), 135);
		testDirectionCase(new Point2D.Double(2,2), new Point2D.Double(3,-1.5), -74.05);
		testDirectionCase(new Point2D.Double(0,0), new Point2D.Double(0,3), 90.0);
		testDirectionCase(new Point2D.Double(2,2), new Point2D.Double(-3,-1.5), -145);
	}
	
	public void testAntiClockwiseAngleCase(double start, double end, double expected){
		double p = Geometry.getAntiClockWiseAngleDistance(start, end);
		double goodEnough = 1e-2;
		Assert.assertTrue(Math.abs(expected - p) < goodEnough);
	}
	
	@Test
	public void testAntiClockwiseAngle(){
		testAntiClockwiseAngleCase(0, 90, 90);
		testAntiClockwiseAngleCase(90, 0, 270);
		testAntiClockwiseAngleCase(90, 90, 0);
		testAntiClockwiseAngleCase(90, 89, 359);
		testAntiClockwiseAngleCase(44, 184, 140);
		testAntiClockwiseAngleCase(45, -45, 270);
		testAntiClockwiseAngleCase(-45, 45, 90);
		testAntiClockwiseAngleCase(-45, -45, 0);
	}
	
	public void testAngleWithinBoundsCase(double theta,
			double first, double second, boolean expected){
		boolean check = Geometry.angleWithinBounds(theta, first, second);
		Assert.assertEquals(expected, check);
	}
	
	@Test
	public void testAngleWithinBounds(){
		testAngleWithinBoundsCase(90, 0, 180, true);
		testAngleWithinBoundsCase(0, 90, 180, false);
		testAngleWithinBoundsCase(-15, 340, 0, true);
		testAngleWithinBoundsCase(-15, -90, -5, true);
		testAngleWithinBoundsCase(-15, -90, 355, true);
		testAngleWithinBoundsCase(-15, -90, -25, false);
	}
	
	public void testGetNumberOfRayCircleIntersectionsCase
				(Point2D vectorStart,
						Point2D directionVector, Point2D circleCentre,
						double circleRadius, int expected){
		int p = Geometry.getNumberOfRayCircleIntersections(vectorStart, directionVector, circleCentre, circleRadius);
		Assert.assertEquals(expected, p);
	}
	
	@Test
	public void testGetNumberOfRayCircleIntersections(){
		testGetNumberOfRayCircleIntersectionsCase(new Point2D.Double(0,0),
				new Point2D.Double(0,1),
				new Point2D.Double(0,0),
				1,
				2);
		testGetNumberOfRayCircleIntersectionsCase(new Point2D.Double(0,1),
				new Point2D.Double(1,0),
				new Point2D.Double(0,0),
				1,
				1);
		testGetNumberOfRayCircleIntersectionsCase(new Point2D.Double(2,2),
				new Point2D.Double(1,0),
				new Point2D.Double(0,0),
				1,
				0);
		testGetNumberOfRayCircleIntersectionsCase(new Point2D.Double(-1,-1),
				new Point2D.Double(1,1),
				new Point2D.Double(0,0),
				1,
				2);
		testGetNumberOfRayCircleIntersectionsCase(new Point2D.Double(-1,1),
				new Point2D.Double(1,1),
				new Point2D.Double(0,0),
				1,
				0);
	}
	
	
	public void testDoubleArcCurveCase(DynamicInfo dpi, OperationReallocation op, 
			LinkedList<PathStep> expected) {
		
		LinkedList<PathStep> actual = PathFinder.getDoubleArcPath(dpi, op, false /* variant */);
		Assert.assertTrue(actual.size() == expected.size());
		double threshold = 0.0001;
				
				/* variant: type */
				PathStepArcForwardsRight arcExpectedFirst = (PathStepArcForwardsRight) expected.get(0);
				PathStepArcForwardsRight arcActualFirst = (PathStepArcForwardsRight) actual.get(0);
				//System.out.println(arc2);
				
				System.out.println("Expected: " + arcExpectedFirst);
				System.out.println("Actual: " + arcActualFirst);
				
				Assert.assertTrue(Math.abs(arcExpectedFirst.getRadius() - arcActualFirst.getRadius()) < threshold);
				Assert.assertTrue(Math.abs(arcExpectedFirst.getAngle() - arcActualFirst.getAngle()) < threshold);
				Assert.assertTrue(Math.abs(arcExpectedFirst.getTargetOrientation() - arcActualFirst.getTargetOrientation()) < threshold);
				Assert.assertTrue(Math.abs(arcExpectedFirst.getTargetDestination().distance(arcActualFirst.getTargetDestination())) < threshold);

				/* variant: type */
				PathStepArcForwardsRight arcExpectedSecond = (PathStepArcForwardsRight) expected.get(1);
				PathStepArcForwardsRight arcActualSecond = (PathStepArcForwardsRight) actual.get(1);

				System.out.println("Expected: " + arcExpectedSecond);
				System.out.println("Actual: " + arcActualSecond);
				
				Assert.assertTrue(Math.abs(arcExpectedSecond.getRadius() - arcActualSecond.getRadius()) < threshold);
				Assert.assertTrue(Math.abs(arcExpectedSecond.getAngle() - arcActualSecond.getAngle()) < threshold);
				Assert.assertTrue(Math.abs(arcExpectedSecond.getTargetOrientation() - arcActualSecond.getTargetOrientation()) < threshold);
				Assert.assertTrue(Math.abs(arcExpectedSecond.getTargetDestination().distance(arcActualSecond.getTargetDestination())) < threshold);
		
	}
	
	@Test
	public void testDoubleArcCurve() {
		
		DynamicBallInfo dbi;
		DynamicRobotInfo dri;
		DynamicInfo dpi;
		
		PathStepArc arc1;
		PathStepArc arc2;
		
		OperationReallocation op;
		
		LinkedList<PathStep> expected;
		// test1
		dbi = new DynamicBallInfo(new Point(40,20), 0, 0, 0);
		dri = new DynamicRobotInfo(new Point(0,0), 90, true, false, 0, 90, 0, false, 0);
		
		arc1 = new PathStepArcForwardsRight(
				dri.getPosition(), 
				dri.getFacingDirection(), 
				30.0, 
				90, 
				10);
		arc2 = new PathStepArcForwardsRight(new Point(30, 30), 0, 10, 90, 10);
		
		dpi = new DynamicInfo(dbi, dri, null);
		op = new OperationReallocation(dbi.getPosition(), 270.0);
		
		expected = new LinkedList<PathStep>();
		expected.add(arc1);
		expected.add(arc2);
		testDoubleArcCurveCase(dpi, op, expected);
		
		//test 2
		
//		dbi = new DynamicBallInfo(new Point(60,-60), 0, 0, 0);
//		dri = new DynamicRobotInfo(new Point(0,0), 0, true, false, 0, 0, 0, false, 0);
//		
//		arc1 = new PathStepArcForwardsRight(dri.getPosition(), dri.getFacingDirection(), 50, 90, 10);
//		arc2 = new PathStepArcForwardsLeft(arc1.getTargetDestination(), arc1.getTargetOrientation(), 10, 90, 10);
//		
//		dpi = new DynamicInfo(dbi, dri, null);
//		op = new OperationReallocation(dbi.getPosition(), dri.getFacingDirection());
//		
//		expected = new LinkedList<PathStep>();
//		expected.add(arc1);
//		expected.add(arc2);
//		testDoubleArcCurveCase(dpi, op, expected);
//		
//		//test 3
//		
//		dbi = new DynamicBallInfo(new Point(-60,60), 0, 0, 0);
//		dri = new DynamicRobotInfo(new Point(0,0), 180, true, false, 0, 180, 0, false, 0);
//		
//		arc1 = new PathStepArcForwardsRight(dri.getPosition(), dri.getFacingDirection(), 50, 90, 10);
//		arc2 = new PathStepArcForwardsLeft(arc1.getTargetDestination(), arc1.getTargetOrientation(), 10, 90, 10);
//		
//		dpi = new DynamicInfo(dbi, dri, null);
//		op = new OperationReallocation(dbi.getPosition(), dri.getFacingDirection());
//		
//		expected = new LinkedList<PathStep>();
//		expected.add(arc1);
//		expected.add(arc2);
//		testDoubleArcCurveCase(dpi, op, expected);
		
		//test 4
//		dbi = new DynamicBallInfo(new Point(-20,-40), 0, 0, 0);
//		dri = new DynamicRobotInfo(new Point(0,0), 180, true, false, 0, 180, 0, false, 0);
//		
//		arc3 = new PathStepArcForwardsLeft(dri.getPosition(), dri.getFacingDirection(), 50, 90, 10);
//		arc2 = new PathStepArcForwardsLeft(arc3.getTargetDestination(), arc3.getTargetOrientation(), 10, 0, 10);
//		
//		dpi = new DynamicInfo(dbi, dri, null);
//		op = new OperationReallocation(dbi.getPosition(), dri.getFacingDirection());
//		
//		expected = new LinkedList<PathStep>();
//		expected.add(arc3);
//		expected.add(arc2);
//		testDoubleArcCurveCase(dpi, op, expected);
	}
	
	@Test
	public void testIsArcLeft() {
		Assert.assertFalse(
				Geometry.isArcLeft(
						new Point2D.Double(0.0, 0.0), 
						0.0, 
						new Point2D.Double(1.0, 1.0)
				)
		);
	}
	
	@Test
	public void testIsPointBehind() {
		Assert.assertFalse(
				Geometry.isPointBehind(
						new Point2D.Double(0.0, 0.0), 
						90.0, 
						new Point2D.Double(1.0, 1.0)
				)
		);
		
		Assert.assertTrue(
				Geometry.isPointBehind(
						new Point2D.Double(0.0, 0.0), 
						90.0, 
						new Point2D.Double(1.0, -1.0)
				)
		);
		
		Assert.assertTrue(
				Geometry.isPointBehind(
						new Point2D.Double(0.0, 0.0), 
						90.0, 
						new Point2D.Double(-1.0, -1.0)
				)
		);
		
		Assert.assertFalse(
				Geometry.isPointBehind(
						new Point2D.Double(0.0, 0.0), 
						90.0, 
						new Point2D.Double(-1.0, 1.0)
				)
		);
	}
	
	public void testGetArcAngleCase(Point2D arcStart, Point2D arcEnd, double radius, double expected) {
		double threshold = 0.0001;
		double actual = Geometry.getArcAngle(arcStart, arcEnd, radius);
		Assert.assertTrue(Math.abs(expected - actual) < threshold);
	}
	
	@Test
	public void testGetArcAngle() {
		testGetArcAngleCase(
				new Point(0,0), 
				new Point(1,1), 
				1, 
				90
				);
		
		testGetArcAngleCase(
				new Point(0,0), 
				new Point(2,2), 
				2, 
				90
				);
		
		testGetArcAngleCase(
				new Point(0,0), 
				new Point(-1,1), 
				1, 
				90
				);
		
		testGetArcAngleCase(
				new Point(0,0), 
				new Point(-1,-1), 
				1, 
				90
				);
		
		testGetArcAngleCase(
				new Point(0,0), 
				new Point(1,-1), 
				1, 
				90
				);
		
		testGetArcAngleCase(
				new Point(0,0), 
				new Point(0,-60), 
				30, 
				180
				);
		
		testGetArcAngleCase(
			new Point(0,0), 
			new Point(50,50), 
			50, 
			90
		);
	}
	
	public void testIsCircleCentreOnTheRightCase(Point2D startPosition,
			double startDirection, Point2D circleCentre, boolean expected){
		boolean p = Geometry.isCircleCentreOnTheRight(startPosition, startDirection, circleCentre);
		Assert.assertEquals(expected, p);
	}
	
	@Test
	public void testIsCircleCentreOnTheRight(){
		testIsCircleCentreOnTheRightCase(new Point2D.Double(0,0), 90, new Point2D.Double(1,1), true);
		testIsCircleCentreOnTheRightCase(new Point2D.Double(0,0), 90, new Point2D.Double(-1,-1), false);
		testIsCircleCentreOnTheRightCase(new Point2D.Double(5,5), 30, new Point2D.Double(10,5), true);
		testIsCircleCentreOnTheRightCase(new Point2D.Double(0,0), 30, new Point2D.Double(5,10), false);
	}
=======
	public void testGetLinesIntersectionCase(Point2D a, Point2D b, Point2D c, Point2D d, Point2D expected) {

		Point2D actual = Geometry.getLinesIntersection(a, b, c, d);
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void testGetLinesIntersection() {
		testGetLinesIntersectionCase(
				new Point(0,0), 
				new Point(1,1),  
				new Point(1,1), 
				new Point(1,2),
				new Point(1,1)
				);
		
		testGetLinesIntersectionCase(
				new Point(0,0), 
				new Point(2,2), 
				new Point(0,1), 
				new Point(1,2),
				null
				);
		
		testGetLinesIntersectionCase(
				new Point(0,0), 
				new Point(2,4), 
				new Point(2,2), 
				new Point(1,2),
				new Point(1,2)
				);
		
		testGetLinesIntersectionCase(
				new Point(1,0), 
				new Point(-2,-6), 
				new Point(2,2), 
				new Point(3,2),
				new Point(2,2)
				);
		
		testGetLinesIntersectionCase(
				new Point(1,0), 
				new Point(-3,0), 
				new Point(2,6), 
				new Point(-5,6),
				null
				);
		
		testGetLinesIntersectionCase(
				new Point(-2,0), 
				new Point(-2,-6), 
				new Point(-2,-2), 
				new Point(3,-4),
				new Point(-2,-2)
				);
		
		testGetLinesIntersectionCase(
				new Point(-2,0), 
				new Point(-2,-6), 
				new Point(-2,-2), 
				new Point(3,-4),
				new Point(-2,-2)
				);
		
		testGetLinesIntersectionCase(
				new Point(4,0), 
				new Point(-2,-6), 
				new Point(2,-2), 
				new Point(4,-4),
				new Point(2,-2)
				);
	}
	
	public void testGetLineCircleIntersectionsCase(
			Point2D segmentStart, Point2D segmentEnd, Point2D circleCentre, 
			double circleRadius, Pair<Point2D, Point2D> expected
			) {
		Pair<Point2D, Point2D> n = Geometry.getLineCircleIntersections(
				segmentStart, 
				segmentEnd, 
				circleCentre, 
				circleRadius
		);
		
		Assert.assertEquals(expected, n);
		
	}
	
	@Test
	public void testGetLineCircleIntersections(){
		testGetLineCircleIntersectionsCase(
				new Point2D.Double(1.0, 3.0), 
				new Point2D.Double(1.0, -5.0), 
				new Point2D.Double(1.0, 0.0), 
				1.0,
				new Pair<Point2D, Point2D>(new Point2D.Double(1.0, 1.0),new Point2D.Double(1.0, -1.0))
				);
		
		testGetLineCircleIntersectionsCase(
				new Point2D.Double(0.0, 2.0), 
				new Point2D.Double(0.0, -8.0), 
				new Point2D.Double(1.0, 0.0), 
				1.0,
				new Pair<Point2D, Point2D>(new Point2D.Double(0.0, 0.0),new Point2D.Double(0.0, 0.0))
				);
		
		testGetLineCircleIntersectionsCase(
				new Point2D.Double(0.0, 2.0), 
				new Point2D.Double(8.0, 2.0), 
				new Point2D.Double(1.0, 1.0), 
				1.0,
				new Pair<Point2D, Point2D>(new Point2D.Double(1.0, 2.0),new Point2D.Double(1.0, 2.0))
				);
		
		try{
		testGetLineCircleIntersectionsCase(
				new Point2D.Double(0.0, 2.0), 
				new Point2D.Double(12.0, 14.0), 
				new Point2D.Double(1.0, 1.0), 
				1.0,
				new Pair<Point2D, Point2D>(new Point2D.Double(1.0, 2.0),new Point2D.Double(1.0, 2.0))
				);
				Assert.fail("there are no solutions to quadratic equation, so null is returned");
		}catch(NullPointerException ex){
			
		}
		
		testGetLineCircleIntersectionsCase(
				new Point2D.Double(1.0, 2.0), 
				new Point2D.Double(5.0, 2.0), 
				new Point2D.Double(2.0, 2.0), 
				1.0,
				new Pair<Point2D, Point2D>(new Point2D.Double(1.0, 2.0),new Point2D.Double(3.0, 2.0))
				);
		testGetLineCircleIntersectionsCase(
				new Point2D.Double(-1.0, 7.0), 
				new Point2D.Double(-1.0, -10.0), 
				new Point2D.Double(-1.0, -1.0), 
				2.0,
				new Pair<Point2D, Point2D>(new Point2D.Double(-1.0, 1.0),new Point2D.Double(-1.0, -3.0))
				);
		
		try{
		testGetLineCircleIntersectionsCase(
				new Point2D.Double(2.0, 3.0), 
				new Point2D.Double(-4.0, 6.0), 
				new Point2D.Double(1.0, 1.0), 
				1.0,
				new Pair<Point2D, Point2D>(new Point2D.Double(1.0, 2.0),new Point2D.Double(1.0, 2.0))
				);
				Assert.fail("there are no solutions to quadratic equation, so null is returned");
		}catch(NullPointerException ex){
			
		}
	}
		
	
>>>>>>> 93b15104997054436519f6acd9f5036a462fb325
}
