package group2.sdp.pc.controlstation;

import group2.sdp.pc.mouth.Mouth;

/**
 * Used for quick test of Mouth-to-Ear communication.
 * Do not forget about Thread.sleep() after each command and s.cleanup()
 * after all commands were executed.
 */
public class Conversation {
	
	public static void main (String [] args) throws Exception {
		Mouth s = new Mouth();
		
//		s.sendGoForward(100, 0);
//		Thread.sleep(500);
		
//		s.sendForwardArcLeft(20, 90);
//		Thread.sleep(5000);
//
		
		s.sendForwardArcRight(40, 70);
		Thread.sleep(2000);
		s.sendForwardArcLeft(40, 70);
		Thread.sleep(2000);
		
		s.sendBackwardsArcRight(40, 70);
		Thread.sleep(2000);
		s.sendBackwardsArcLeft(40, 70);
		Thread.sleep(2000);

		s.sendStop();
		Thread.sleep(1000);
		s.sendReset();
		Thread.sleep(1000);
		s.cleanup();
	}
	
	/**
	 * Converts time difference to frames per second.
	 * @param before System time, in milliseconds, of the beginning of the
	 * interval.
	 * @param now System time, in milliseconds, of the end of the interval.
	 * @return FPS rate, based on the given period.
	 */
	public static double getFPS(long before, long now){
		return (1000.0 / (now - before));
	}
}
