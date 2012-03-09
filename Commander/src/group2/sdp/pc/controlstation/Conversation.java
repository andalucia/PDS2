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
			
		
	}
	
	/**
	 * @param prevSysTime - previous state System time in Miliseconds
	 * @return FPS - frames per second.
	 */
	public static double getFPS(long prevSysTime){
		return (1000.0 / (System.currentTimeMillis() - prevSysTime));
	}
}
