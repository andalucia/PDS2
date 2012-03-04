package group2.sdp.common.util;

/**
 * A class containing useful methods.
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
}
