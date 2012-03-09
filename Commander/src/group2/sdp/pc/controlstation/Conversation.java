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
//		s.sendExit();
		
//		s.sendGoForward(35, 10);
//		Thread.sleep(4000);
//		s.sendSpinLeft(0, 90);
//		Thread.sleep(4000);
//		s.sendGoForward(35,10);
//		Thread.sleep(4000);
//		s.sendSpinRight(0,90);
//		Thread.sleep(4000);
//		s.sendGoForward(35, 30);
//		s.sendSpinRight(0, 90);
//		Thread.sleep(4000);
//		s.sendGoForward(35, 30);
//		Thread.sleep(4000);
//		s.sendSpinRight(0,90);
//		
//		Paul's Test ^^^
		
		long prev = 0, now = 0;
		for (int i = 0; i < 2000; ++i) {
			s.sendGoForward(15, 0);
			Thread.sleep(40); // 5 FPS
			now = System.currentTimeMillis();
			System.out.println(getFPS(prev, now));
			prev = now;
		}
		
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
