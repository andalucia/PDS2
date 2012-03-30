package group2.sdp.common.util;

import java.awt.geom.Point2D;

public class Geometry {

	private static final double BIG_RANDOM_NUMBER = 10.0;
	/**
	 * Set to true to output debug data.
	 */
	@SuppressWarnings("unused")
	private static boolean verbose = true;
	
	/**
	 * Gets the number of intersections between a semi-infinite line segment 
	 * (a ray) and a circle.
	 * @param rayStart The start of the ray.
	 * @param rayVector The direction of the ray.
	 * @param circleCentre The centre of the circle.
	 * @param circleRadius The radius of the circle.
	 * @return The number of intersections between the given ray and the given circle.
	 */
	public static int getNumberOfRayCircleIntersections(Point2D rayStart,
			Point2D rayVector, Point2D circleCentre, double circleRadius) {
		double a = circleCentre.getX();
		double b = circleCentre.getY();
		double c = rayStart.getX();
		double d = rayStart.getY(); 
		double e = rayVector.getX();
		double f = rayVector.getY();
		double r = circleRadius;

		double A = e * e + f * f;
		double B = 2 * (e * (c - a) + f * (d - b));
		double C = (c - a) * (c - a) + (d - b) * (d - b) - r * r;
		
		int n = Tools.getNumberOfQuadraticSolutions(A, B, C);
		return n;
	}
	
	/**
	 * Infers the parameters (for point parametrisation) for the intersection points
	 * (with the circle), that positions it (the point) on the line.
	 * @param segmentStart
	 * @param segmentEnd
	 * @param circleCentre
	 * @param circleRadius
	 * @return
	 */
	public static Pair<Double, Double> getLineCircleIntersectionsParameters(
			Point2D segmentStart, Point2D segmentEnd, Point2D circleCentre, 
			double circleRadius) {
		double a = segmentStart.getX() - circleCentre.getX();
		double b = segmentEnd.getX() - segmentStart.getX();
		double c = segmentStart.getY() - circleCentre.getY();
		double d = segmentEnd.getY() - segmentStart.getY();
		double r = circleRadius;
		
		double A = b * b + d * d;
		double B = 2 * (a * b + c * d);
		double C = a * a + c * c - r * r;
		
		// a point on the given segment is
		// segmentStart + t * (segmentEnd - segmentStart)
		Pair <Double, Double> t = Tools.getQuadraticSolutions(A, B, C);
		return t;
	}
	
	/**
	 * Gets the points of intersection between the given line and arc.
	 */
	public static Pair<Point2D, Point2D> getLineCircleIntersections(
			Point2D linePoint1, Point2D linePoint2, Point2D circleCentre, 
			double circleRadius) {
		
		Pair<Double, Double> t = 
			getLineCircleIntersectionsParameters(
					linePoint1, 
					linePoint2, 
					circleCentre, 
					circleRadius
			);
		Pair<Point2D, Point2D> result = 
			new Pair<Point2D, Point2D>(
					translate(
							linePoint1,
							t.first,
							getVectorDifference(linePoint2, linePoint1)
					),
					translate(
							linePoint1,
							t.second,
							getVectorDifference(linePoint2, linePoint1)
					)
			);
		return result;
	}
	
	/**
	 * Transposes v in the given direction with the given scale.
	 * @param v The vector to transpose.
	 * @param scale The scale to apply.
	 * @param direction The direction of the transposition.
	 * @return The transposed vector.
	 */
	public static Point2D translate(Point2D v, double scale, Point2D direction) {
		double x = v.getX() + scale * direction.getX();
		double y = v.getY() + scale * direction.getY();
		return new Point2D.Double(x, y);
	}
	
	public static Point2D translate(Point2D v, double scale, double angle) {
		return translate(v, scale, getDirectionVector(angle));
	}
	
	/**
	 * Gets the offset from start to end.
	 */
	public static Point2D getVectorDifference(Point2D subtractor, Point2D subtractee) {
		return 
			new Point2D.Double(
					subtractor.getX() - subtractee.getX(), 
					subtractor.getY() - subtractee.getY()
			);
	}
	
	/**
	 * Gets the number of intersections between a line segment and a circular 
	 * arc.
	 * @param segmentStart The starting point of the line segment.
	 * @param segmentEnd The ending point of the line segment.
	 * @param circleCentre The centre of the circle containing the arc.
	 * @param circleRadius The radius of the circle containing the arc.
	 * @param arcStartAngle The starting angle of the arc.
	 * @param arcEndAngle The end angle of the arc; when moving on the arc from
	 * the start angle to the end angle, you should be moving anti-clock wise 
	 * (otherwise there is ambiguity of order 2).
	 * @return The number of intersections between a line segment and a 
	 * circular arc.
	 */
	public static int getNumberOfLineSegmentArcIntersections(
			Point2D segmentStart, Point2D segmentEnd, Point2D circleCentre, 
			double circleRadius, double arcStartAngle, double arcEndAngle) {
		
		double a = segmentStart.getX() - circleCentre.getX();
		double b = segmentEnd.getX() - segmentStart.getX();
		double c = segmentStart.getY() - circleCentre.getY();
		double d = segmentEnd.getY() - segmentStart.getY();
		
		Pair<Double, Double> t = 
			getLineCircleIntersectionsParameters(
					segmentStart, 
					segmentEnd, 
					circleCentre, 
					circleRadius
			);
		
		// theta is the angle on the arc at which the segment crosses it
		Pair<Double, Double> theta = new Pair<Double, Double> (
				Math.toDegrees(Math.atan2(c + t.first * d, a + t.first * b)),
				Math.toDegrees(Math.atan2(c + t.second * d, a + t.second * b))
		);
		
		int n = 0;
		// because of the particular parameterisation of the line segment
		// t should be between 0 and 1; theta should be bounded by the 
		// arc angles
		if (0.0 <= t.first && t.first <= 1.0 && 
				angleWithinBounds(theta.first, arcStartAngle, arcEndAngle)) {
			++n;
		}
		
		// Comparison for doubles is special, so do not use t.first == t.second
		if (t.first.compareTo(t.second) != 0 && 
				0.0 <= t.second && t.second <= 1.0 && 
				angleWithinBounds(theta.second, arcStartAngle, arcEndAngle)) {
			++n;
		}
		return n;
	}

	/**
	 * Checks if an angle is between two given angles. The region should be
	 * anti-clock-wise oriented, that is when you move from arcStartAngle to
	 * arcEndAngle, you should be moving in anti-clock-wise direction. Copes
	 * with 360 - 0 jump.
	 * @param theta The angle to check.
	 * @param arcStartAngle Lower bound with respect to anti-clock-wise 
	 * orientation.
	 * @param arcEndAngle Upper bound with respect to anti-clock-wise 
	 * orientation.
	 * @return True if an angle is between two given angles, false otherwise.
	 */
	public static boolean angleWithinBounds(double theta,
			double arcStartAngle, double arcEndAngle) {
		
		return 
			getAntiClockWiseAngleDistance(arcStartAngle, theta) <=
			getAntiClockWiseAngleDistance(arcStartAngle, arcEndAngle);
	}
	
	/**
	 * Returns the amount you need to turn anti-clock-wise direction in degrees  
	 * to get from angle alpha to angle beta. The result is from
	 * [0 to 360) and the units are degrees.  
	 * @param alpha The starting angle.
	 * @param beta The end angle.
	 * @return The amount you need to turn anti-clock-wise in degrees  
	 * to get from angle alpha to angle beta.
	 */
	public static double getAntiClockWiseAngleDistance(double alpha, 
			double beta) {
		double delta = beta - alpha;
		if (delta < 0) 
			delta += 360;

		return delta;
	}

	/**
	 * Gets the angle of the vector starting from the first point and ending in the
	 * second point.
	 * @param first The start of the vector.
	 * @param second The end of the vector.
	 * @return The angle of the vector in degrees,
	 */
	public static double getVectorDirection(Point2D first,
			Point2D second) {
		double dx = second.getX() - first.getX();
		double dy = second.getY() - first.getY();
		return Math.toDegrees(Math.atan2(dy, dx));
	}
	
	/**
	 * Gets the unit vector representing the given direction.
	 * @param angle ... in degrees ...
	 * @return
	 */
	public static Point2D getDirectionVector(double angle) {
		double radians = Math.toRadians(angle);
		return 
			new Point2D.Double(
					Math.cos(radians),
					Math.sin(radians) 
			);
	}

	/**
	 * Given the start position, start direction, radius and angle of the
	 * arc, return the end position. The sign of the angle indicates whether
	 * it is a clock-wise (negative angle) or anti-clock-wise (positive angle) 
	 * arc; the sign of the radius indicates if the arc movement is forward or
	 * backwards.
	 * @param arcStart The starting point of the arc.
	 * @param arcStartDirection The slope of the tangent line to the arc in the 
	 * starting point.
	 * @param radius The radius of the circle containing the arc. Can be negative. 
	 * @param angle The central angle of the arc. Can be negative.
	 * @return The end point of the arc.
	 */
	public static Point2D getArcEnd(Point2D arcStart, double arcStartDirection,
			double radius, double angle) {
		// Cosine theorem
		double c = radius * Math.sqrt(2 * (1 - Math.cos(Math.toRadians(angle))));
		
		double beta;
		beta = arcStartDirection + angle / 2;
		
		double x = arcStart.getX() + c * Math.cos(Math.toRadians(beta));
		double y = arcStart.getY() + c * Math.sin(Math.toRadians(beta));
		
		return new Point2D.Double(x, y);
	}
	
	/**
	 * Gives the angle to travel on an arc from start to end position given 
	 * the direction of the robot.
	 * @param arcStart
	 * @param startDirection
	 * @param arcEnd
	 * @param circleCentre
	 * @param radius
	 * @return ... from 0 to 360 degrees ...
	 */
	public static double getArcOrientedAngle(
			Point2D arcStart, 
			double startDirection,
			Point2D arcEnd,
			Point2D circleCentre
	) {
		double radius = circleCentre.distance(arcStart);
		double c = arcStart.distance(arcEnd);
		double cosOfAngle = (2 * radius * radius - c * c) / (2 * radius * radius);
		double angle = Math.toDegrees(Math.acos(cosOfAngle));
		
		return 
			!isPointBehind(arcStart, startDirection, arcEnd)
			? angle
			: 360 - angle;
	}

	/**
	 * Gets the point of intersection between two lines. Returns null if they are parallel.
	 */
	public static Point2D getLinesIntersection(Point2D a, Point2D b, Point2D c, Point2D d) {
		double x1 = a.getX();
		double y1 = a.getY();
		
		double x2 = b.getX();
		double y2 = b.getY();
		
		double x3 = c.getX();
		double y3 = c.getY();
		
		double x4 = d.getX();
		double y4 = d.getY();
		
		double dx1 = x2 - x1;
		double dy1 = y2 - y1;
		
		double dx2 = x4 - x3;
		double dy2 = y4 - y3; 
		
		double numerator = dx2 * (y1 - y3) - dy2 * (x1 - x3); 
		double denominator = dx1 * dy2 - dy1 * dx2;
		
		if (new Double(denominator).equals(0.0)) {
			// The lines are parallel. 
			return null;
		}
		
		double t = numerator / denominator;
		
		return new Point2D.Double(x1 + t * dx1, y1 + t * dy1);
	}
	
	/**
	 * Is the centre on the left of the start point, with respect to the given direction.
	 */
	public static boolean isArcLeft(Point2D start, double direction, Point2D centre) {
		Point2D temp = generateRandomPoint(centre, direction);
		
		return 
		crossProduct(
				getVectorDifference(start, centre),
				getVectorDifference(temp, centre)
		) > 0.0;
	}
	
	public static double reverse(double direction) {
		return (direction + 180.0) % 360.0;
	}
	
	/**
	 * No French people allowed.
	 * @param direction
	 * @return
	 */
	public static double perpendicularisePaul(double direction) {
		return (direction + 90.0) % 360.0;
	}
	
	public static boolean isPointBehind(Point2D referencePoint, double direction, Point2D testPoint) {
		double dt = perpendicularisePaul(direction);
		boolean result = isArcLeft(referencePoint, dt, testPoint);
		System.out.println("[isPointBehind]: " + result);
		return result;
	}
	
	/**
	 * Normalises the given angle to be from 0 to 360 degrees. 
	 */
	public static double normalizeToPositive(double angle) {
		angle = (angle + 360.0) % 360.0;
		return angle;
	}
	
	public static double crossProduct(Point2D v1, Point2D v2) {
		return v1.getX() * v2.getY() - v2.getX() * v1.getY(); 
	}

	public static boolean isCircleCentreOnTheRight(Point2D startPosition,
			double startDirection, Point2D circleCentre){
		Point2D positiveLeft = 
			getArcEnd(
					startPosition, 
					startDirection, 
					circleCentre.distance(startPosition), 
					90
			);
		double threshold = 0.0001;
		if (circleCentre.distance(positiveLeft) - circleCentre.distance(startPosition) < threshold) {
			return false;
		}
		return true;
	}

	public static Point2D generateRandomPoint(Point2D p1, double direction) {
		double scale = BIG_RANDOM_NUMBER;
		return translate(p1, scale, getDirectionVector(direction));
	}
	
	public static Point2D generatePointOnLine(Point2D p1, double direction, double scale) {
		return translate(p1, scale, getDirectionVector(direction));
	}
	
	public static boolean isPointOnArc(Point2D startPoint, Point2D endPoint,
										Point2D pointToCheck, double angle){
		if (angle < 180){
			if (startPoint.distance(pointToCheck) < startPoint.distance(endPoint))
				return true;
			else
				return false;
		}
		else {
			if (endPoint.distance(pointToCheck) < endPoint.distance(startPoint))
				return true;
			else
				return false;  
		}
		
	}

	/**
	 * Gets the point that lies on the middle of the segment connecting the 
	 * given points.
	 */
	public static Point2D getMidPoint(Point2D start, Point2D end) {
		return new Point2D.Double(
				(start.getX() + end.getX()) / 2,
				(start.getY() + end.getY()) / 2
		);
	}
}
