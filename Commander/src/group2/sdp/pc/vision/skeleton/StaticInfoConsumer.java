package group2.sdp.pc.vision.skeleton;

import group2.sdp.pc.breadbin.StaticInfo;

/**
 * Classes implementing this interface can consume static information about the pitch.
 */
public interface StaticInfoConsumer {
	/**
	 * Consumes the given StaticPitchInfo object.
	 * @param spi The StaticPitchInfo object to consume.
	 */
	public void consumeInfo(StaticInfo spi);
}
