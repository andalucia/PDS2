package group2.sdp.pc.vision.stan;

import group2.sdp.common.breadbin.StaticPitchInfo;

/**
 * Classes implementing this interface can consume static information about the pitch.
 */
public interface StaticInfoConsumer {
	/**
	 * Consumes the given StaticPitchInfo object.
	 * @param spi The StaticPitchInfo object to consume.
	 */
	public void consumeInfo(StaticPitchInfo spi);
}
