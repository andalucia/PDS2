package group2.sdp.pc.vision.skeleton;

import group2.sdp.pc.breadbin.DynamicInfo;

/**
 * Classes implementing this interface can consume dynamic information about the pitch.
 */
public interface DynamicInfoConsumer {
	/**
	 * Consumes the given DynamicPitchInfo object.
	 * @param dpi The DynamicPitchInfo object to consume.
	 */
	public void consumeInfo(DynamicInfo dpi);
}
