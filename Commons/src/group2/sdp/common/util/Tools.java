package group2.sdp.common.util;


/**
 * A class containing useful methods.
 * TODO: rename to LanguageTools.
 */
public class Tools {
	/**
	 * Puts the given value in reasonable limits.
	 * @param value The value to restrict.
	 * @param min Lower bound.
	 * @param max Upper bound.
	 * @return min if value < min, max if value > max, just value otherwise.
	 */
	public static int sanitizeInput(int value, int min, int max) {
		if (value > max)
			value = max;
		if (value < min)
			value = min;
		return value;
	}
	
	/**
	 * Puts the given value in reasonable limits.
	 * @param value The value to restrict.
	 * @param min Lower bound.
	 * @param max Upper bound.
	 * @return min if value < min, max if value > max, just value otherwise.
	 */
	public static float sanitizeInput(float value, float min, float max) {
		if (value > max)
			value = max;
		if (value < min)
			value = min;
		return value;
	}
	
	/**
	 * Gets the number of solutions of a quadratic equation of the given form:
	 * 
	 * a * x^2 + b * x + c = 0
	 * 
	 * @return The number of solutions of the quadratic equation.
	 */
	public static int getNumberOfQuadraticSolutions(double a, double b, double c) {
		double discriminant = b * b - 4 * a * c;
		if (discriminant < 0.0)
			return 0;
		else if (discriminant == 0.0)
			return 1;
		else 
			return 2;
	}
	
	/**
	 * Returns the solutions of the quadratic equation:
	 * 
	 * a * x^2 + b * x + c = 0
	 * 
	 * if there is no solution, null is returned, so beware.
	 * 
	 * @return the solutions of the quadratic equation.
	 */
	public static Pair<Double, Double> getQuadraticSolutions(double a, double b, double c) {
		double discriminant = b * b - 4 * a * c;
		if (discriminant < 0.0)
			return null;
		Double x1 = (-b - Math.sqrt(discriminant)) / (2 * a);
		Double x2 = (-b + Math.sqrt(discriminant)) / (2 * a);
		return new Pair<Double, Double>(x1, x2);
	}

}
