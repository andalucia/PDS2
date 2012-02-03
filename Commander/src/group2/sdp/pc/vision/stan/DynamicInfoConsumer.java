package group2.sdp.pc.vision.stan;

import group2.sdp.common.breadbin.DynamicPitchInfo;

/**
 * Classes implementing this interface can consume dynamic information about the pitch.
 */
public interface DynamicInfoConsumer {
	/**
	 * Consumes the given DynamicPitchInfo object.
	 * @param dpi The DynamicPitchInfo object to consume.
	 */
	public void consumeInfo(DynamicPitchInfo dpi);
}
