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
import group2.sdp.pc.planner.pathstep.PathStepArcForwardsLeft;
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
	
	
	public void testDoubleArcCurveCase(Point2D start, double startdir, Point2D end, 
			double enddir, LinkedList<PathStep> expected) {
		
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
		LinkedList<PathStep> actual = PathFinder.getDoubleArcPath(pitch, op, false /* variant */);
		
		Assert.assertTrue(actual.size() == expected.size());
		double threshold = 0.0001;
				
				/* variant: type */
				PathStepArc arcExpectedFirst = (PathStepArc) expected.get(0);
				PathStepArc arcActualFirst = (PathStepArc) actual.get(0);
				//System.out.println(arc2);
				
//				System.out.println("Expected: " + arcExpectedFirst);
//				System.out.println("Actual: " + arcActualFirst);
				
				Assert.assertEquals(arcExpectedFirst.getType(), arcActualFirst.getType());
				Assert.assertTrue(Math.abs(arcExpectedFirst.getRadius() - arcActualFirst.getRadius()) < threshold);
				Assert.assertTrue(Math.abs(arcExpectedFirst.getAngle() - arcActualFirst.getAngle()) < threshold);
				Assert.assertTrue(Math.abs(arcExpectedFirst.getTargetOrientation() - arcActualFirst.getTargetOrientation()) < threshold);
				Assert.assertTrue(Math.abs(arcExpectedFirst.getTargetDestination().distance(arcActualFirst.getTargetDestination())) < threshold);

				/* variant: type */
				PathStepArc arcExpectedSecond = (PathStepArc) expected.get(1);
				PathStepArc arcActualSecond = (PathStepArc) actual.get(1);

//				System.out.println("Expected: " + arcExpectedSecond);
//				System.out.println("Actual: " + arcActualSecond);
				
				Assert.assertEquals(arcExpectedSecond.getType(), arcActualSecond.getType());
				Assert.assertTrue(Math.abs(arcExpectedSecond.getRadius() - arcActualSecond.getRadius()) < threshold);
				Assert.assertTrue(Math.abs(arcExpectedSecond.getAngle() - arcActualSecond.getAngle()) < threshold);
				Assert.assertTrue(Math.abs(arcExpectedSecond.getTargetOrientation() - arcActualSecond.getTargetOrientation()) < threshold);
				Assert.assertTrue(Math.abs(arcExpectedSecond.getTargetDestination().distance(arcActualSecond.getTargetDestination())) < threshold);
	}
	
	@Test
	public void testDoubleArcCurve() {
		Point2D start = new Point2D.Double(-57.74393, -38.52368);
		double startDir = 3.3743178428786678;
		double startRadius = 84.61948097092228;
		double startArcAngle = 56.78676462704812;
		
//		Point2D middlePoint = Geometry.getArcEnd(start, startDir, startRadius, startAngle);
		
		Point2D end = new Point2D.Double(30.996742, 13.672317);
		double endDir = -8.544205595857441;
		double radius2 = 0.0;
		double angle2 = 0.0;
		
		PathStepArc arc1 = 
			new PathStepArcForwardsLeft(start, startDir, startRadius, startArcAngle, 20);
		PathStepArc arc2 = 
			new PathStepArcForwardsRight(
					arc1.getTargetDestination(), 
					arc1.getTargetOrientation(), 
					radius2, angle2, 20);
		
		LinkedList<PathStep> expected = new LinkedList<PathStep>();
		expected.add(arc1);
		expected.add(arc2);
		
		//testDoubleArcCurveCase(start, startDir, end, endDir, expected);
		
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
		Assert.assertTrue(
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
	
	public void testGetArcAngleCase(Point2D arcStart, double direction, Point2D arcEnd, Point2D centre, double radius, double expected) {
		double threshold = 0.0001;
		double actual = Geometry.getArcOrientedAngle(arcStart, direction, arcEnd, centre, radius);
//		System.out.println("expected: " + expected);
//		System.out.println("actual: " + actual);
		Assert.assertTrue(Math.abs(expected - actual) < threshold);
	}
	
	@Test
	public void testGetArcAngle() {
		testGetArcAngleCase(
				new Point(0,0), 
				90, 
				new Point(1,1), 
				new Point(1,0), 
				1, 
				90
				);
		
		testGetArcAngleCase(
				new Point(0,0), 
				90, 
				new Point(2,2), 
				new Point(2,0),
				2, 
				90
				);
		
		testGetArcAngleCase(
				new Point(0,0), 
				90, 
				new Point(-1,1), 
				new Point(-1,0), 
				1, 
				90
				);
		
		testGetArcAngleCase(
				new Point(0,0), 
				-90, 
				new Point(-1,-1), 
				new Point(-1, 0),
				1, 
				90
				);
		
		testGetArcAngleCase(
				new Point(0,0), 
				0, 
				new Point(1,-1), 
				null, 
				1, 
				90
				);
		
		// FIXME:
//		testGetArcAngleCase(
//				new Point(0,0), 
//				90, 
//				new Point(0,-60), 
//				new Point(0,-30), 
//				30, 
//				180
//				);
//		
		testGetArcAngleCase(
			new Point(0,0), 
			0, new Point(50,50), 
			null, 50, 
			90
		);
	}

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

	public void testGetLineCircleIntersectionsParametersCase(Point2D segmentStart, Point2D segmentEnd, Point2D circleCentre, 
			double circleRadius, Pair<Double, Double> expected) {
		Assert.assertEquals(
				expected, 
				Geometry.getLineCircleIntersectionsParameters(
						segmentStart, 
						segmentEnd, 
						circleCentre, 
						circleRadius
				)
		);
	}
	
	@Test
	public void testGetLineCircleIntersectionsParameters() {
		testGetLineCircleIntersectionsParametersCase(
				new Point2D.Double(0.0, 0.0),
				new Point2D.Double(40.0, 40.0),
				new Point2D.Double(30.0, 40.0),
				10.0,
				new Pair <Double, Double> (
						0.75,
						1.0
				)
		);
	}
	
	public void testGetLineCircleIntersectionsCase(
			Point2D segmentStart, Point2D segmentEnd, Point2D circleCentre, 
			double circleRadius, Pair<Point2D, Point2D> expected
			) {
		Pair <Point2D, Point2D> n = Geometry.getLineCircleIntersections(
				segmentStart, 
				segmentEnd, 
				circleCentre, 
				circleRadius
		);
		
		Assert.assertEquals(expected, n);		
	}
	
	@Test
	public void testGetLineCircleIntersections() {
		
		testGetLineCircleIntersectionsCase(
				new Point2D.Double(0.0, 0.0), 
				new Point2D.Double(40.0, 40.0), 
				new Point2D.Double(30.0, 40.0), 
				10.0,
				new Pair<Point2D, Point2D> (
						new Point2D.Double(30.0, 30.0),
						new Point2D.Double(40.0, 40.0)
				)
		);
		
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
				Assert.fail("There are no solutions to quadratic equation, so null is returned.");
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
		
		try {
		testGetLineCircleIntersectionsCase(
				new Point2D.Double(2.0, 3.0), 
				new Point2D.Double(-4.0, 6.0), 
				new Point2D.Double(1.0, 1.0), 
				1.0,
				new Pair<Point2D, Point2D>(new Point2D.Double(1.0, 2.0),new Point2D.Double(1.0, 2.0))
				);
				Assert.fail("There are no solutions to quadratic equation, so null is returned.");
		} catch (NullPointerException ex) {
			
		}
	}
	
	public void testCrossProductCase (Point2D p1, Point2D p2, Point2D p3, double expected) {
		double crossProduct = Geometry.crossProduct(
				Geometry.getVectorDifference(p1, p2),
				Geometry.getVectorDifference(p3, p2)
		);
		
		Assert.assertEquals(expected, crossProduct);
	}
	
	@Test
	public void testCrossProduct() {
		testCrossProductCase(new Point(1,1), new Point(0,0), new Point(-1,1), 2.0);
	}
	
	@Test
	public void testGetVectorDifference() {
		Point p1 = new Point(0,0);
		Point p2 = new Point(-1,1);
		Point p3 = new Point(0,2);
		
		Point exp = new Point(1, -1);
		testGetVectorDifferenceCase(p1, p2, exp);

		exp = new Point(1, 1);
		testGetVectorDifferenceCase(p3, p2, exp);
	}

	public void testGetVectorDifferenceCase(Point p1, Point p2, Point exp) {
		Assert.assertEquals(exp, Geometry.getVectorDifference(p1, p2));
	}
	
	public void testgetNumberOfLineSegmentArcIntersectionsCase(Point2D segmentStart,
			Point2D segmentEnd, 
			Point2D circleCentre,
			double circleRadius,
			double arcStartAngle,
			double arcEndAngle,
			int expected){
		int actual = Geometry.getNumberOfLineSegmentArcIntersections(
				segmentStart, segmentEnd, circleCentre, circleRadius, 
				arcStartAngle, arcEndAngle);
		
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testIsPointOnArc(){
		testgetNumberOfLineSegmentArcIntersectionsCase(
				new Point2D.Double(1,0),
				new Point2D.Double(-1,0),
				new Point2D.Double(0,0),
				1,
				0,
				180,
				2);
		testgetNumberOfLineSegmentArcIntersectionsCase(
				new Point2D.Double(-1,-1),
				new Point2D.Double(1,1),
				new Point2D.Double(0,0),
				2,
				0,
				270,
				0);
		testgetNumberOfLineSegmentArcIntersectionsCase(
				new Point2D.Double(-2,-2),
				new Point2D.Double(1,1),
				new Point2D.Double(0,0),
				2,
				0,
				270,
				1);
		testgetNumberOfLineSegmentArcIntersectionsCase(
				new Point2D.Double(-2,-2),
				new Point2D.Double(2,2),
				new Point2D.Double(0,0),
				2,
				0,
				270,
				2);
		testgetNumberOfLineSegmentArcIntersectionsCase(
				new Point2D.Double(-2,-2),
				new Point2D.Double(-2,2),
				new Point2D.Double(0,0),
				2,
				0,
				270,
				1);
		testgetNumberOfLineSegmentArcIntersectionsCase(
				new Point2D.Double(2,-2),
				new Point2D.Double(-1,1),
				new Point2D.Double(0,0),
				2,
				0,
				270,
				0);
		testgetNumberOfLineSegmentArcIntersectionsCase(
				new Point2D.Double(-1,-3),
				new Point2D.Double(1,-1),
				new Point2D.Double(0,0),
				2,
				0,
				270,
				1);
		testgetNumberOfLineSegmentArcIntersectionsCase(
				new Point2D.Double(-1,-3),
				new Point2D.Double(3,1),
				new Point2D.Double(0,0),
				2,
				0,
				270,
				2);
	}
}
