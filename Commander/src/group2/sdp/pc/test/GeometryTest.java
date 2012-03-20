package group2.sdp.pc.test;

import group2.sdp.common.util.Geometry;
import group2.sdp.pc.breadbin.DynamicInfo;
import group2.sdp.pc.planner.PathFinder;
import group2.sdp.pc.planner.operation.OperationReallocation;
import group2.sdp.pc.planner.pathstep.PathStep;

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
		LinkedList<PathStep> actual = PathFinder.getDoubleArcPath(dpi, op);
		Assert.assertTrue(actual.size() == expected.size());
		for (int i = 0; i < actual.size(); ++i) {
			Assert.assertEquals(expected.get(i), actual.get(i));
		}
	}
}
