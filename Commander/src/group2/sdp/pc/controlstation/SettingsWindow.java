package group2.sdp.pc.controlstation;

import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.vision.VisualCortex;
import group2.sdp.pc.vision.VisualCortex.OutputMode;

import java.awt.Button;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// We do not use serialisation.
@SuppressWarnings("serial")
public class SettingsWindow extends JFrame {
	
	/**
	 * Used for the sliders.
	 */
	private final int MIN_BR_HUE = -120, MAX_BR_HUE = 0;
	private int blueToRedHue;
	
	private final int MIN_RY_HUE = 0, MAX_RY_HUE = 60;
	private int redToYellowHue;
	
	private final int MIN_YG_HUE = 60, MAX_YG_HUE = 120;
	private int yellowToGreenHue;
	
	private final int MIN_GB_HUE = 120, MAX_GB_HUE = 240;
	private int greenToBlueHue;
	
	private JLabel blueToRedHueLabel;
	private JLabel redToYellowHueLabel;
	private JLabel yellowToGreenHueLabel;
	private JLabel greenToBlueHueLabel;	
	
	private JSlider blueToRedHueSlider;
	private JSlider redToYellowHueSlider;
	private JSlider yellowToGreenHueSlider;
	private JSlider greenToBlueHueSlider;
	
	private Button matchVisionModeButton;
	private Button chromaVisionModeButton;
	private Button lumaVisionModeButton;
	private Button grabImageButton;
	
	private GlobalInfo globalInfo;
	private VisualCortex processor;
	
	public SettingsWindow(GlobalInfo globalInfo, VisualCortex processor) {
		this.globalInfo = globalInfo;
		this.processor = processor;
		setBounds(0, 0, 640, 480);
		initializeFrame();
		setVisible(true);
	}
	
	
	private void initializeFrame() {
		blueToRedHue = globalInfo.getColourSettings().getBlueToRedHue() - 360;
		redToYellowHue = globalInfo.getColourSettings().getRedToYellowHue();
		yellowToGreenHue = globalInfo.getColourSettings().getYellowToGreenHue();
		greenToBlueHue = globalInfo.getColourSettings().getGreenToBlueHue();
		
		// Image filtering controls
		
		blueToRedHueLabel = new JLabel();
		blueToRedHueLabel.setText("B/R");
		blueToRedHueLabel.setBounds(12, 12, 30, 25);
		
		blueToRedHueSlider = new JSlider(JSlider.HORIZONTAL, MIN_BR_HUE, MAX_BR_HUE, blueToRedHue);
		blueToRedHueSlider.setBounds(42, 12, 200, 25);
		blueToRedHueSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				blueToRedHue = blueToRedHueSlider.getValue();
				globalInfo.getPitch().getCamera().getColourSettings()
				.setBlueToRedHue(blueToRedHue + 360);
				System.out.println(blueToRedHue + 360);
			}
		});
		blueToRedHueSlider.setMajorTickSpacing(20);
		blueToRedHueSlider.setMinorTickSpacing(5);
		blueToRedHueSlider.setPaintTicks(true);
		
		redToYellowHueLabel = new JLabel();
		redToYellowHueLabel.setText("R/Y");
		redToYellowHueLabel.setBounds(12, 61, 30, 25);
		
		redToYellowHueSlider = new JSlider(JSlider.HORIZONTAL, MIN_RY_HUE, MAX_RY_HUE, redToYellowHue);
		redToYellowHueSlider.setBounds(42, 61, 200, 25);
		redToYellowHueSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				redToYellowHue = redToYellowHueSlider.getValue();
				globalInfo.getColourSettings().setRedToYellowHue(redToYellowHue);
			}
		});
		redToYellowHueSlider.setMajorTickSpacing(20);
		redToYellowHueSlider.setMinorTickSpacing(5);
		redToYellowHueSlider.setPaintTicks(true);
		
		
		yellowToGreenHueLabel = new JLabel();
		yellowToGreenHueLabel.setText("Y/G");
		yellowToGreenHueLabel.setBounds(12, 110, 30, 25);
		
		yellowToGreenHueSlider = new JSlider(JSlider.HORIZONTAL, MIN_YG_HUE, MAX_YG_HUE, yellowToGreenHue);
		yellowToGreenHueSlider.setBounds(42, 110, 200, 25);
		yellowToGreenHueSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				yellowToGreenHue = yellowToGreenHueSlider.getValue();
				globalInfo.getColourSettings().setYellowToGreenHue(yellowToGreenHue);
			}
		});
		yellowToGreenHueSlider.setMajorTickSpacing(20);
		yellowToGreenHueSlider.setMinorTickSpacing(5);
		yellowToGreenHueSlider.setPaintTicks(true);
		
		
		greenToBlueHueLabel = new JLabel();
		greenToBlueHueLabel.setText("G/B");
		greenToBlueHueLabel.setBounds(12, 159, 30, 25);
		
		greenToBlueHueSlider = new JSlider(JSlider.HORIZONTAL, MIN_GB_HUE, MAX_GB_HUE, greenToBlueHue);
		greenToBlueHueSlider.setBounds(42, 159, 200, 25);
		greenToBlueHueSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				greenToBlueHue = greenToBlueHueSlider.getValue();
				globalInfo.getColourSettings().setGreenToBlueHue(greenToBlueHue);
			}
		});
		greenToBlueHueSlider.setMajorTickSpacing(20);
		greenToBlueHueSlider.setMinorTickSpacing(5);
		greenToBlueHueSlider.setPaintTicks(true);
		 
		
		matchVisionModeButton = new Button();
		matchVisionModeButton.setLabel("Match");
		matchVisionModeButton.setBounds(12, 208, 65, 25);
		matchVisionModeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				processor.setCurrentMode(OutputMode.MATCH);
			}
		});
	    
		chromaVisionModeButton = new Button();
		chromaVisionModeButton.setLabel("Chroma");
		chromaVisionModeButton.setBounds(97, 208, 65, 25);
		chromaVisionModeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				processor.setCurrentMode(OutputMode.CHROMA);
			}
		});
	    
		
		lumaVisionModeButton = new Button();
		lumaVisionModeButton.setLabel("Luma");
		lumaVisionModeButton.setBounds(182, 208, 65, 25);
		lumaVisionModeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				processor.setCurrentMode(OutputMode.LUMA);
			}
		});
		
	    
		grabImageButton = new Button();
		grabImageButton.setLabel("Grab background");
		grabImageButton.setBounds(55, 257, 150, 25);
		grabImageButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				processor.grabNewBackgroundImage();
			}
		});

		
		getContentPane().add(blueToRedHueLabel);
		getContentPane().add(redToYellowHueLabel);
		getContentPane().add(yellowToGreenHueLabel);
		getContentPane().add(greenToBlueHueLabel);
		
		getContentPane().add(blueToRedHueSlider);
		getContentPane().add(redToYellowHueSlider);
		getContentPane().add(yellowToGreenHueSlider);
		getContentPane().add(greenToBlueHueSlider);
		
		getContentPane().add(matchVisionModeButton);
		getContentPane().add(chromaVisionModeButton);
		getContentPane().add(lumaVisionModeButton);
		getContentPane().add(grabImageButton);

		// Funny...
		getContentPane().add(new Panel());
	}
}
