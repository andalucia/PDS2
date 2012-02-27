package group2.sdp.common.util;

/**
 * Extends the Pair class to allow for comparison.
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
public class KeyValuePair 
	<K extends Comparable<K>, V extends Comparable<V>> 
	extends Pair<K, V> 
	implements Comparable<KeyValuePair<K, V>> {

	/**
	 * Default constructor.
	 */
	public KeyValuePair() {
		super();
	}
	
	/**
	 * Fully initializing constructor.
	 * @param first The first element.
	 * @param second The second element.
	 */
	public KeyValuePair(K first, V second) {
		super(first, second);
	}

	/**
	 * See parent.
	 */
	@Override
	public int compareTo(KeyValuePair<K, V> other) {
		return getFirst().compareTo(other.getFirst());
	}

	
}
