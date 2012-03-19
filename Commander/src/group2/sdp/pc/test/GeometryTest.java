package group2.sdp.pc.test;

import java.awt.geom.Point2D;


import org.junit.Test;

import junit.framework.Assert;

import group2.sdp.common.util.Geometry;

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
}
