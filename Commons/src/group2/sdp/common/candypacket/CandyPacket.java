package group2.sdp.common.candypacket;

/**
 * A packet of candies for Alfie to munch on. He decodes them as commands, silly him.
 * For the purpose of this class a pretzel is an integer, sweets are bytes and the 'brand'
 * of the CandyPacket is the opcode of the operation to be executed.
 */
public class CandyPacket {
	
	// The brand of candy that makes Alfie stop.
	public static final byte STOP_CANDY = 0;
	// The brand of candy that makes Alfie start moving forward.
	public static final byte GO_FORWARD_CANDY = 1;
	// The brand of candy that makes Alfie start moving backwards.
	public static final byte GO_BACKWARDS_CANDY = 2;
	// The brand of candy that makes Alfie become aggressive and kick.	
	public static final byte KICK_CANDY = 3;
	// The brand of candy that makes Alfie start spinning around.
	public static final byte SPIN_CANDY = 4;
	public static final byte SPIN_TO_LEFT_CANDY = 5;
	public static final byte SPIN_TO_RIGHT_CANDY = 6;
	// The brand of candy that makes Alfie reset the candy exchange.
	public static final byte RESET_CANDY = 126;
	// The brand of candy that makes Alfie go to bed.
	public static final byte SLEEP_CANDY = 127;
	
	// The number of sweets in the packet.
	public static final int PACKET_SIZE = 32;
	// Number of general purpose pretzels (integers) that form a command.
	private static final int PRETZEL_COUNT = 4; 
	// The number of sweets intended for consumption. Alfie spits the other back, which
	// modifies them into feedback.
	private static final int CONSUMPTION_SIZE = 4 + 4 * PRETZEL_COUNT;
	// The collection of sweets.
	private byte [] sweets;
	
	/**
	 * The default candy packet. Since Alfie needs to know at least the brand, this is private.
	 */
	private CandyPacket() {
		sweets = new byte [PACKET_SIZE];
		for (int i = 0; i < PACKET_SIZE; ++i)
			sweets[i] = 0;
	}

	/**
	 * Simplest candy packet design. Has a brand that tells Alfie what to do when he is
	 * consuming the sweets.
	 * @param brand tells Alfie what to do when he is consuming the sweets. Pick one
	 * from the constants that end in CANDY. [TODO: move to an enumeration]
	 */
	public CandyPacket(byte brand) {
		this();
		sweets[0] = brand;
	}
	
	/**
	 * A candy packet design that has a pretzel (4 sweets) in it. Those can be used
	 * as an argument for the action to be taken by Alfie. This pretzel is usually 
	 * used for speed or power.
	 * @param brand See the simplest constructor.
	 * @param pretzel0 A general-purpose pretzel (integer).
	 */
	public CandyPacket(byte brand, int pretzel0) {
		this(brand);
		byte [] fourSweets = pretzelToSweets4(pretzel0);
		sweets[4] = fourSweets[0];
		sweets[5] = fourSweets[1];
		sweets[6] = fourSweets[2];
		sweets[7] = fourSweets[3];
	}
	
	/**
	 * A candy packet design that has two pretzels (4 sweets) in it. Those can be used
	 * as arguments for the action to be taken by Alfie. The zeroth pretzel is usually 
	 * speed or power and the first one is angle. We don't know for the others.
	 * @param brand See the simplest constructor.
	 * @param pretzel0 A general-purpose pretzel (integer).
	 * @param pretzel1 A general-purpose pretzel (integer).
	 */
	public CandyPacket(byte brand, int pretzel0, int pretzel1) {
		this(brand, pretzel0);
		byte [] fourSweets = pretzelToSweets4(pretzel1);
		sweets[8] = fourSweets[0];
		sweets[9] = fourSweets[1];
		sweets[10] = fourSweets[2];
		sweets[11] = fourSweets[3];
	}
	
	/**
	 * A candy packet design that has three pretzels (4 sweets) in it. Those can be used
	 * as arguments for the action to be taken by Alfie. The zeroth pretzel is usually 
	 * speed or power and the first one is angle. We don't know for the others.
	 * @param brand See the simplest constructor.
	 * @param pretzel0 A general-purpose pretzel (integer).
	 * @param pretzel1 Another general-purpose pretzel (integer).
	 * @param pretzel2 A third general-purpose pretzel (integer).
	 */
	public CandyPacket(byte brand, int pretzel0, int pretzel1, int pretzel2) {
		this(brand, pretzel0, pretzel1);
		byte [] fourSweets = pretzelToSweets4(pretzel2);
		sweets[12] = fourSweets[0];
		sweets[13] = fourSweets[1];
		sweets[14] = fourSweets[2];
		sweets[15] = fourSweets[3];
	}
	
	/**
	 * A candy packet design that has four pretzels (4 sweets) in it (quite rare!). Those 
	 * can be used as arguments for the action to be taken by Alfie. The zeroth pretzel is 
	 * usually speed or power and the first one is angle. We don't know for the others.
	 * @param brand See the simplest constructor.
	 * @param pretzel0 A general-purpose pretzel (integer).
	 * @param pretzel1 Another general-purpose pretzel (integer).
	 * @param pretzel2 A third general-purpose pretzel (integer).
	 * @param pretzel3 A fourth general-purpose pretzel (integer). 
	 */
	public CandyPacket(byte brand, int pretzel0, int pretzel1, int pretzel2, int pretzel3) {
		this(brand, pretzel0, pretzel1, pretzel2);
		byte [] fourSweets = pretzelToSweets4(pretzel3);
		sweets[16] = fourSweets[0];
		sweets[17] = fourSweets[1];
		sweets[18] = fourSweets[2];
		sweets[19] = fourSweets[3];
	}
	
	/**
	 * A candy packet design for which you specify all the sweets in it at the same time.
	 * @param sweets All the sweets that are to be put in the candy packet, in order.
	 */
	public CandyPacket(byte [] sweets) {
		this();
		for (int i = 0; i < PACKET_SIZE; ++i) {
			this.sweets[i] = sweets[i];
		}
	}
	
	/**
	 * Clones the sweets and gets that.
	 * @return A clone of the contents of the candy packet, which is sweets.
	 */
	public byte [] getSweets() {
		return sweets.clone();
	}
	
	/**
	 * Gets the brand of the candy (the opcode).
	 * @return The brand of the candy (the opcode).
	 */
	public byte getBrand() {
		return sweets[0];
	}
	
	/**
	 * Gets one of the {@value}PRETZEL_COUNT{@value} general-purpose pretzels (GPP) 
	 * from the candy packet. E.g. if the candy packet is GO_FORWARD_CANDY, the zeroth 
	 * GPP is the speed for which to execute the command, and the others are 0. 
	 * @param index The index of the general-purpose pretzel to get. The pretzels are 0-indexed.
	 * @return The selected pretzel.
	 */
	public int getPretzel(int index) {
		assert(index >= 0 && index < PRETZEL_COUNT);
		return sweets4ToPretzel(sweets, 4 * (1 + index));
	}
	
	/**
	 * Compares the contents of this CandyPacket to the given bunch of sweets. 
	 * @param c2 The bunch of sweets to compare the contents of the packet with.
	 * @return true if the contents are equal, false otherwise.
	 */
	public boolean contentsEqual(byte [] sweets) {
		// Compare only the first [CONSUMPTION_SIZE] 
		for (int i = 0; i < CONSUMPTION_SIZE; ++i) {
			if (this.sweets[i] != sweets[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Prints the contents of the packet.
	 */
	public void printSweets() {
		System.out.print("Sending bytes:");
		for (int i = 0; i < sweets.length; ++i) {
			System.out.print(" " + sweets[i]);
		}
		System.out.println();
	}
	
	/**
	 * Converts a pretzel (integer) to four sweets (bytes).
	 * @param arg The integer to convert.
	 * @return A byte array consisting of four bytes.
	 */
	private byte[] pretzelToSweets4(int pretzel) {
		byte [] result = new byte [4];
		result[0] = (byte)(pretzel >> 24);
		result[1] = (byte)((pretzel >> 16) & 255);
		result[2] = (byte)((pretzel >> 8) & 255);
		result[3] = (byte)(pretzel & 255);
		return result;
	}
	
	/**
	 * Converts four sweets (bytes) to a pretzel (integer).
	 * @param b The array of sweets to convert.
	 * @param offset The offset at which to get the four sweets.
	 * @return The integer.
	 */
	private static int sweets4ToPretzel(byte [] sweets, int offset) {
		int result = 0;
		for (int i = 0; i < 4; ++i) {
			result <<= 8;
			result += sweets[offset + i] < 0    // condition 
				? (int)sweets[offset + i] + 256 // if the condition is met
				: (int) sweets[offset + i];		// otherwise
		}
		return result;
	}
}
