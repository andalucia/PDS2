package group2.sdp.pc.vision;

import group2.sdp.pc.vision.skeleton.ImageConsumer;

import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;


/**
 * <p><b>Description:</b>	Sets up a window in which to display images passed to it
 * through consume</p>
 */
public class Artist extends WindowAdapter implements ImageConsumer {
	private static int width = 640, height = 480;
	
	private JLabel          label;
	private JFrame          frame;	
	
	/**
	 * The data will be passed to this object, after it is displayed.
	 */
	private ImageConsumer consumer;
	
	@Override
	public void consume(BufferedImage image) {
		label.getGraphics().drawImage(image, 0, 0, width, height, null);
		if (consumer != null)
			consumer.consume(image);
	}

	public Artist() {
		// create and initialise UI
		initGUI();
	}
	
	public Artist(ImageConsumer consumer) {
		this();
		this.consumer = consumer;
	}

	/** 
	 * Creates the UI components and initialises them
	 */
	private void initGUI(){
		frame = new JFrame();
		frame.setLocation(640, 0);
		label = new JLabel();
		frame.getContentPane().add(label);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(this);
		frame.setVisible(true);
		frame.setSize(width, height);       
	}

}
