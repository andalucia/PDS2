package group2.sdp.pc.controlstation;

import group2.sdp.pc.globalinfo.Camera;
import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.globalinfo.LCHColourSettings;
import group2.sdp.pc.globalinfo.Pitch;
import group2.sdp.pc.planner.FieldMarshal;
import group2.sdp.pc.planner.Overlord;
import group2.sdp.pc.planner.PathFinder;
import group2.sdp.pc.planner.Penalty;
import group2.sdp.pc.server.Server;
import group2.sdp.pc.vision.Bakery;
import group2.sdp.pc.vision.ImageGrabber;
import group2.sdp.pc.vision.ImagePreviewer;
import group2.sdp.pc.vision.ImageProcessor;
import group2.sdp.pc.vision.ImageProcessor.OutputMode;
import group2.sdp.pc.vision.LCHColour;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The main GUI program to control Alfie during match or testing.
 */
public class CommanderControlStation {

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
	
	
	/**
	 * used to check if image is processing
	 */
	private boolean isProcessing;
	// GUI elements
	private JFrame frmAlfieCommandCentre;
	
	private CheckboxGroup yellowBlueAlfieGroup;
	private CheckboxGroup shootingDirectionGroup;
	private Checkbox yellowAlfieCheckbox;
	private Checkbox blueAlfieCheckbox;
	private Checkbox rightAlfieCheckbox;
	private Checkbox leftAlfieCheckbox;
	
	private Checkbox processImageCheckbox;

	private Button connectButton;
	private Button runButton;
	private Button startPlanningButton;
	private Button stopPlanningButton;
	private Button penaltyButton;
	private Button robotPositionButtonLeft;
	private Button robotPositionButtonRight;
	private Button goalieButton;
	
	private JLabel visionLabel;
	private JLabel planningLabel;
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
	
	private JTextPane txtLog;

	private GlobalInfo globalInfo;
	private boolean attackingRight = true;
	/**
	 * The server that sends commands to Alfie.
	 */
	private Server alfieServer;
	private ImageProcessor processor;
	private Overlord lord;

	/**
	 *  Number of connection attempts before giving up.
	 */
	private static final int CONNECTION_ATTEMPTS = 2;
	/**
	 * Timeout between retries of connecting to Alfie.
	 */
	private static final int RETRY_TIMEOUT = 3000;

	// This is actually used in a child thread, but eclipse is being silly.
	@SuppressWarnings("unused")
	private int key_pressed = 0;
	/**
	 * True if the main window was closed.
	 */
	private boolean exiting = false;
			
	/**
	 * The threads for starting and stopping the communication to Alfie.
	 */
	private Thread init_thread, cleanup_thread;
	/**
	 * Singleton instance of the class.
	 */
	private static CommanderControlStation instance;



	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CommanderControlStation window = new CommanderControlStation();
					window.frmAlfieCommandCentre.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	
	/**
	 * Create the application.
	 * @throws Exception Thrown when another instance of the class already 
	 * exists. This is done as the class is a singleton.
	 */
	public CommanderControlStation() throws Exception {
		if (instance == null) {
			instance = this;
			isProcessing=false;
		} else {
			throw new Exception();
		}
		initializeFrame();
	}

	/**
	 * Initialise the threads for connecting to and disconnecting from Alfie.
	 */
	private void initializeConnectionThreads() {
		init_thread = new Thread() {		

			public void run() {
				for(int i = 1; i <= CONNECTION_ATTEMPTS && !exiting; ++i) {	
					log("Connection attempt: " + i);
					
					try {
						alfieServer = new Server();
						log("Connected to Alfie");
						connectButton.setBackground(Color.WHITE);
						connectButton.setEnabled(false);
						break;
					} catch(Exception e) {
						log("Failed to connect... Retrying in " + (RETRY_TIMEOUT / 1000) + " seconds");
						try {
							Thread.sleep(RETRY_TIMEOUT);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		};
		
		cleanup_thread = new Thread() {		
			public void run() {
				alfieServer.sendReset();
			}
		};
	}
	
	
	/**
	 * Starts the processing pipeline.
	 */
	private void startPipeline() {
		
		if(leftAlfieCheckbox.getState()){
			attackingRight = true;
		}else{
			attackingRight = false;
		}
		
		globalInfo = new GlobalInfo(attackingRight, yellowAlfieCheckbox.getState(), Pitch.ONE);
		
		PathFinder finder = new PathFinder(globalInfo,alfieServer);
		
		FieldMarshal marshal = new FieldMarshal(globalInfo, finder);
		
		lord = new Overlord(globalInfo, marshal);
		
		Bakery bakery = new Bakery(lord);
		
		ImagePreviewer previewer = new ImagePreviewer();
		
		if (processImageCheckbox.getState()) {
			processor = new ImageProcessor(globalInfo, bakery, previewer);
			new ImageGrabber(processor);
			isProcessing=true;
		} else {
			new ImageGrabber(previewer);
		}
		
	}
	
	/**
	 * Initialise the contents of the frame.
	 */
	private void initializeFrame() {
		globalInfo = new GlobalInfo(true, true, Pitch.ONE);
		blueToRedHue = globalInfo.getColourSettings().getBlueToRedHue() - 360;
		redToYellowHue = globalInfo.getColourSettings().getRedToYellowHue();
		yellowToGreenHue = globalInfo.getColourSettings().getYellowToGreenHue();
		greenToBlueHue = globalInfo.getColourSettings().getGreenToBlueHue();
		
		frmAlfieCommandCentre = new JFrame();
		frmAlfieCommandCentre.setTitle("Alfie Command Centre");
		frmAlfieCommandCentre.setBounds(100, 100, 1000, 500);
		frmAlfieCommandCentre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAlfieCommandCentre.getContentPane().setLayout(null);
		
		connectButton = new Button();
		connectButton.setLabel("Connect");
		connectButton.setBounds(42, 12, 100, 25);
		connectButton.setBackground(new Color(0, 128, 255));
		connectButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				initializeConnectionThreads();
				init_thread.start();
			}
		});
		
		visionLabel = new JLabel();
		visionLabel.setText("Vision");
		visionLabel.setBounds(380, 20, 75, 25);
		
		
		yellowBlueAlfieGroup = new CheckboxGroup();
		
	    yellowAlfieCheckbox = new Checkbox("Yellow Alfie", yellowBlueAlfieGroup, true);
	    yellowAlfieCheckbox.setBounds(380, 75, 160, 25);
	    
	    blueAlfieCheckbox = new Checkbox("Blue Alfie", yellowBlueAlfieGroup, false);
	    blueAlfieCheckbox.setBounds(380, 105, 160, 25);
		
		
		processImageCheckbox = new Checkbox();
		processImageCheckbox.setLabel("Process image");
		processImageCheckbox.setBounds(380, 150, 160, 25);
		processImageCheckbox.setState(true);
		
		runButton = new Button();
		runButton.setLabel("Start Vision");
		runButton.setBounds(380, 208, 100, 25);
		runButton.setBackground(Color.yellow);
		runButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				startPipeline();
				
			    yellowAlfieCheckbox.setEnabled(false);
			    blueAlfieCheckbox.setEnabled(false);
				connectButton.setEnabled(false);
				processImageCheckbox.setEnabled(false);
				runButton.setEnabled(false);
				
				blueToRedHueSlider.setEnabled(true);
				redToYellowHueSlider.setEnabled(true);
				yellowToGreenHueSlider.setEnabled(true);
				greenToBlueHueSlider.setEnabled(true);
				
				matchVisionModeButton.setEnabled(true);
				chromaVisionModeButton.setEnabled(true);
				lumaVisionModeButton.setEnabled(true);
				
				grabImageButton.setEnabled(true);
			}
		});

		
		
		planningLabel = new JLabel();
		planningLabel.setText("Planning");
		planningLabel.setBounds(380, 270, 75, 25);
		
		shootingDirectionGroup = new CheckboxGroup();
		
	    leftAlfieCheckbox = new Checkbox("Alfie defending left", shootingDirectionGroup, true);
	    leftAlfieCheckbox.setBounds(380, 310, 160, 25);
	    leftAlfieCheckbox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (globalInfo != null && leftAlfieCheckbox.getState()) {

					globalInfo.setAttackingRight(true);
					log("ATTACKING RIGHT");
				}
			}
	    });
	    
	    rightAlfieCheckbox = new Checkbox("Alfie defending right", shootingDirectionGroup, false);
	    rightAlfieCheckbox.setBounds(380, 340, 160, 25);
	    rightAlfieCheckbox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (globalInfo != null && rightAlfieCheckbox.getState()) {

					globalInfo.setAttackingRight(false);
					log("ATTACKING LEFT");
				}
			}
	    });		
	    
		startPlanningButton = new Button();
		startPlanningButton.setLabel("Start Planning");
		startPlanningButton.setBounds(380, 395, 100, 25);
		startPlanningButton.setBackground(Color.green);
		startPlanningButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lord != null) {
					lord.start();
				}
				if(!isProcessing){
					log("IMAGE IS NOT BEING PROCESSED");
				}
			}
		});
		
		stopPlanningButton = new Button();
		stopPlanningButton.setLabel("Stop!");
		stopPlanningButton.setBounds(750, 395, 100, 25);
		stopPlanningButton.setBackground(Color.red);
		stopPlanningButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lord != null) {
					lord.stop();
				}
			}
			
			
		});
		
		
		penaltyButton = new Button();
		penaltyButton.setLabel("Take Penalty!*");
		penaltyButton.setBounds(750, 345, 100, 25);
		penaltyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lord != null) {
					lord.stop();
					Penalty pen=new Penalty(alfieServer,lord);
					pen.go();
				}else{
					log("OVERLORD IS NULL WHEN PENALTY CALLED");
				}
			}
			
			
		});
		goalieButton = new Button();
		goalieButton.setLabel("Goalkeeper!*");
		goalieButton.setBounds(750, 310, 100, 25);
		goalieButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lord != null) {
					lord.stop();
					Penalty pen=new Penalty(alfieServer,lord);
					pen.defend();
				}else{
					log("OVERLORD IS NULL WHEN PENALTY CALLED");
				}
			}
			
			
		});
		
		
		// Image filtering controls
		
		blueToRedHueLabel = new JLabel();
		blueToRedHueLabel.setText("B/R");
		blueToRedHueLabel.setBounds(696, 12, 30, 25);
		
		blueToRedHueSlider = new JSlider(JSlider.HORIZONTAL, MIN_BR_HUE, MAX_BR_HUE, blueToRedHue);
		blueToRedHueSlider.setBounds(726, 12, 200, 25);
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
		blueToRedHueSlider.setEnabled(false);
		
		redToYellowHueLabel = new JLabel();
		redToYellowHueLabel.setText("R/Y");
		redToYellowHueLabel.setBounds(696, 61, 30, 25);
		
		redToYellowHueSlider = new JSlider(JSlider.HORIZONTAL, MIN_RY_HUE, MAX_RY_HUE, redToYellowHue);
		redToYellowHueSlider.setBounds(726, 61, 200, 25);
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
		redToYellowHueSlider.setEnabled(false);
		
		
		yellowToGreenHueLabel = new JLabel();
		yellowToGreenHueLabel.setText("Y/G");
		yellowToGreenHueLabel.setBounds(696, 110, 30, 25);
		
		yellowToGreenHueSlider = new JSlider(JSlider.HORIZONTAL, MIN_YG_HUE, MAX_YG_HUE, yellowToGreenHue);
		yellowToGreenHueSlider.setBounds(726, 110, 200, 25);
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
		yellowToGreenHueSlider.setEnabled(false);
		
		
		greenToBlueHueLabel = new JLabel();
		greenToBlueHueLabel.setText("G/B");
		greenToBlueHueLabel.setBounds(696, 159, 30, 25);
		
		greenToBlueHueSlider = new JSlider(JSlider.HORIZONTAL, MIN_GB_HUE, MAX_GB_HUE, greenToBlueHue);
		greenToBlueHueSlider.setBounds(726, 159, 200, 25);
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
		greenToBlueHueSlider.setEnabled(false);
		 
		
		matchVisionModeButton = new Button();
		matchVisionModeButton.setLabel("Match");
		matchVisionModeButton.setBounds(696, 208, 65, 25);
		matchVisionModeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				processor.setCurrentMode(OutputMode.MATCH);
			}
		});
		matchVisionModeButton.setEnabled(false);
	    
		chromaVisionModeButton = new Button();
		chromaVisionModeButton.setLabel("Chroma");
		chromaVisionModeButton.setBounds(781, 208, 65, 25);
		chromaVisionModeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				processor.setCurrentMode(OutputMode.CHROMA);
			}
		});
		chromaVisionModeButton.setEnabled(false);
	    
		
		lumaVisionModeButton = new Button();
		lumaVisionModeButton.setLabel("Luma");
		lumaVisionModeButton.setBounds(866, 208, 65, 25);
		lumaVisionModeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				processor.setCurrentMode(OutputMode.LUMA);
			}
		});
		lumaVisionModeButton.setEnabled(false);
		
	    
		grabImageButton = new Button();
		grabImageButton.setLabel("Grab background");
		grabImageButton.setBounds(520, 208, 150, 25);
		grabImageButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				processor.grabNewBackgroundImage();
			}
		});
		grabImageButton.setEnabled(false);
		
		
		
		txtLog = new JTextPane();
		txtLog.setEditable(false);
		txtLog.setBounds(20, 40, 300, 375);
		

		
		
		frmAlfieCommandCentre.getContentPane().add(rightAlfieCheckbox);
		frmAlfieCommandCentre.getContentPane().add(leftAlfieCheckbox);
		
		frmAlfieCommandCentre.getContentPane().add(yellowAlfieCheckbox);
		frmAlfieCommandCentre.getContentPane().add(blueAlfieCheckbox);
		
		frmAlfieCommandCentre.getContentPane().add(connectButton);
		frmAlfieCommandCentre.getContentPane().add(processImageCheckbox);
		frmAlfieCommandCentre.getContentPane().add(runButton);

		frmAlfieCommandCentre.getContentPane().add(startPlanningButton);
		frmAlfieCommandCentre.getContentPane().add(penaltyButton);
		frmAlfieCommandCentre.getContentPane().add(goalieButton);
		frmAlfieCommandCentre.getContentPane().add(stopPlanningButton);
		
		frmAlfieCommandCentre.getContentPane().add(txtLog);
		
		frmAlfieCommandCentre.getContentPane().add(blueToRedHueLabel);
		frmAlfieCommandCentre.getContentPane().add(redToYellowHueLabel);
		frmAlfieCommandCentre.getContentPane().add(yellowToGreenHueLabel);
		frmAlfieCommandCentre.getContentPane().add(greenToBlueHueLabel);
		frmAlfieCommandCentre.getContentPane().add(visionLabel);
		frmAlfieCommandCentre.getContentPane().add(planningLabel);
		
		frmAlfieCommandCentre.getContentPane().add(blueToRedHueSlider);
		frmAlfieCommandCentre.getContentPane().add(redToYellowHueSlider);
		frmAlfieCommandCentre.getContentPane().add(yellowToGreenHueSlider);
		frmAlfieCommandCentre.getContentPane().add(greenToBlueHueSlider);
		
		frmAlfieCommandCentre.getContentPane().add(matchVisionModeButton);
		frmAlfieCommandCentre.getContentPane().add(chromaVisionModeButton);
		frmAlfieCommandCentre.getContentPane().add(lumaVisionModeButton);
		frmAlfieCommandCentre.getContentPane().add(grabImageButton);
		frmAlfieCommandCentre.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmAlfieCommandCentre.setVisible(true);
		frmAlfieCommandCentre.addWindowListener(new WindowListener() {
			public void windowClosed(WindowEvent arg0) {
			}

			public void windowActivated(WindowEvent arg0) {
				
			}

			public void windowClosing(WindowEvent arg0) {
				exiting = true;
				if (cleanup_thread != null)
					cleanup_thread.start();
			}

			public void windowDeactivated(WindowEvent arg0) {
			}

			public void windowDeiconified(WindowEvent arg0) {
			}

			public void windowIconified(WindowEvent arg0) {
			}

			public void windowOpened(WindowEvent arg0) {
				
			}
		});

	}

	
	/**
	 * Gets the singleton instance of the class.
	 * @return The singleton instance of the class.
	 */
	public static CommanderControlStation getInstance() {
		return instance;
	}
	
	
	/**
	 * Adds a string and a new line to the log text box.
	 * @param logString The string to add.
	 */
	public void log(String logString) {
		txtLog.setText(txtLog.getText() + logString + "\n");
		txtLog.repaint();
	}

}
