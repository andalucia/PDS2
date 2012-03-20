package group2.sdp.common.util;

/**
 * A simple pair of elements.
 *
 * @param <F> The type of the first element.
 * @param <S> The type of the second element.
 */
public class Pair <F, S> {
	/**
	 * The first element.
	 */
	protected F first;
	/**
	 * The second element.
	 */
	protected S second;
	
	/**
	 * Default constructor. Does nothing.
	 */
	public Pair() {
		super();
	}
	
	/**
	 * Fully initializing constructor.
	 * @param first The first element.
	 * @param second The second element.
	 */
	public Pair(F first, S second) {
		super();
		this.first = first;
		this.second = second;
	}

	/**
	 * Get the first element.
	 * @return The first element.
	 */
	public F getFirst() {
		return first;
	}
	
	/**
	 * Set the first element.
	 * @param first The first element.
	 */
	public void setFirst(F first) {
		this.first = first;
	}
	
	/**
	 * Get the second element.
	 * @return The second element.
	 */
	public S getSecond() {
		return second;
	}
	
	/**
	 * Set the second element.
	 * @param second The second element.
	 */
	public void setSecond(S second) {
		this.second = second;
	}

	
	@Override
	public String toString() {
		return "(" + getFirst().toString() + ", " + getSecond().toString() + ")"; 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}
	
	
	
}
