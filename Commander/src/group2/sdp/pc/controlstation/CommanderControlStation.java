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
public class CommanderControlStation implements KeyListener {

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
	
	// GUI elements
	private JFrame frmAlfieCommandCentre;
	
	private CheckboxGroup yellowBlueAlfieGroup;
	private Checkbox yellowAlfieCheckbox;
	private Checkbox blueAlfieCheckbox;
	
	private Checkbox grabImageCheckbox;
	private Checkbox processImageCheckbox;
	private Checkbox bakeInfoCheckbox;
	private Checkbox previewImageCheckbox;
	private Checkbox planCheckbox;
	private Checkbox executePlanCheckbox;

	private Button connectButton;
	private Button runButton;
	private Button startPlanningButton;
	private Button stopPlanningButton;
	private Button penaltyButton;
	private Button robotPositionButtonLeft;
	private Button robotPositionButtonRight;
	private Button goalieButton;
	
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
	private JEditorPane Alfie_Speed;
	private JEditorPane Alfie_Angle;

	private JLabel Speed;
	private JLabel Angle;

	private JLabel Info;
	private JLabel Info2;
	
	private GlobalInfo globalInfo;
	private boolean attackingRight = false;
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
		globalInfo = new GlobalInfo(attackingRight, yellowAlfieCheckbox.getState(), Pitch.TWO);
		
		PathFinder finder = new PathFinder(globalInfo,alfieServer);
		
		FieldMarshal marshal = new FieldMarshal(globalInfo, finder);
		
		lord = new Overlord(globalInfo, marshal);
		
		Bakery bakery = new Bakery(lord);
		
		//TODO initalise penalty
		ImagePreviewer previewer = new ImagePreviewer();
		
		if (processImageCheckbox.getState()) {
			processor = new ImageProcessor(globalInfo, bakery, previewer);
			new ImageGrabber(processor);
		} else {
			new ImageGrabber(previewer);
		}
		
		if (planCheckbox.getState()) {
			lord.start();
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
		frmAlfieCommandCentre.setBounds(100, 100, 1000, 440);
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
		
		yellowBlueAlfieGroup = new CheckboxGroup();
		
	    yellowAlfieCheckbox = new Checkbox("Yellow Alfie", yellowBlueAlfieGroup, true);
	    yellowAlfieCheckbox.setBounds(12, 40, 160, 25);
	    
	    blueAlfieCheckbox = new Checkbox("Blue Alfie", yellowBlueAlfieGroup, false);
	    blueAlfieCheckbox.setBounds(12, 68, 160, 25);
		
		grabImageCheckbox = new Checkbox();
		grabImageCheckbox.setLabel("Grab image");
		grabImageCheckbox.setBounds(12, 96, 160, 25);
		grabImageCheckbox.setState(true);
		
		processImageCheckbox = new Checkbox();
		processImageCheckbox.setLabel("Process image");
		processImageCheckbox.setBounds(12, 124, 160, 25);
		processImageCheckbox.setState(true);
		
		previewImageCheckbox = new Checkbox();
		previewImageCheckbox.setLabel("Preview image");
		previewImageCheckbox.setBounds(12, 152, 160, 25);
		previewImageCheckbox.setState(true);
		
		bakeInfoCheckbox = new Checkbox();
		bakeInfoCheckbox.setLabel("Bake dynamic info");
		bakeInfoCheckbox.setBounds(12, 180, 160, 25);
		bakeInfoCheckbox.setState(true);
		
		planCheckbox = new Checkbox();
		planCheckbox.setLabel("Plan");
		planCheckbox.setBounds(12, 208, 160, 25);
		planCheckbox.setState(false);
		
		
		executePlanCheckbox = new Checkbox();
		executePlanCheckbox.setLabel("Execute plan");
		executePlanCheckbox.setBounds(12, 264, 160, 25);
		executePlanCheckbox.setState(true);
		
		runButton = new Button();
		runButton.setLabel("RUN!");
		runButton.setBounds(42, 292, 100, 25);
		runButton.setBackground(Color.green);
		runButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				startPipeline();
				
			    yellowAlfieCheckbox.setEnabled(false);
			    blueAlfieCheckbox.setEnabled(false);
				connectButton.setEnabled(false);
				grabImageCheckbox.setEnabled(false);
				processImageCheckbox.setEnabled(false);
				previewImageCheckbox.setEnabled(false);
				bakeInfoCheckbox.setEnabled(false);
				planCheckbox.setEnabled(false);
				executePlanCheckbox.setEnabled(false);
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

		Label notes= new Label();
		notes.setBounds(600, 380, 500, 25);
		notes.setText("*will start Overlord after shot");
		
		startPlanningButton = new Button();
		startPlanningButton.setLabel("Start Planning");
		startPlanningButton.setBounds(380, 292, 100, 25);
		startPlanningButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lord != null) {
					lord.start();
				}
			}
		});
		
		stopPlanningButton = new Button();
		stopPlanningButton.setLabel("Stop!");
		stopPlanningButton.setBounds(600, 292, 100, 25);
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
		penaltyButton.setBounds(490, 292, 100, 25);
		penaltyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lord != null) {
					lord.stop();
					Penalty pen=new Penalty(alfieServer,lord);
					pen.go();
					System.out.println("PENALTY!");
				}else{
					System.out.println("OVERLORD IS NULL WHEN PENALTY CALLED");
				}
			}
			
			
		});
		goalieButton = new Button();
		goalieButton.setLabel("Goalkeeper!*");
		goalieButton.setBounds(490, 250, 100, 25);
		goalieButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lord != null) {
					lord.stop();
					Penalty pen=new Penalty(alfieServer,lord);
					pen.defend();
					System.out.println("PENALTY!");
				}else{
					System.out.println("OVERLORD IS NULL WHEN PENALTY CALLED");
				}
			}
			
			
		});
		
		robotPositionButtonLeft = new Button();
		robotPositionButtonLeft.setLabel("Yellow robot defends left");
		robotPositionButtonLeft.setBounds(726, 350, 200, 25);
		robotPositionButtonLeft.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (yellowAlfieCheckbox.getState()) {
					attackingRight = true;
				}
			}
			
			
		});
		
		robotPositionButtonRight = new Button();
		robotPositionButtonRight.setLabel("Yellow robot defends right");
		robotPositionButtonRight.setBounds(726, 320, 200, 25);
		robotPositionButtonRight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {				
				if (yellowAlfieCheckbox.getState()) {
					attackingRight = false;
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
		grabImageButton.setBounds(726, 292, 200, 25);
		grabImageButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				processor.grabNewBackgroundImage();
			}
		});
		grabImageButton.setEnabled(false);
		
		
		
		txtLog = new JTextPane();
		txtLog.setEditable(false);
		txtLog.setBounds(212, 12, 472, 305);

		txtLog.addKeyListener(this);
		Info = new JLabel();
		Info2 = new JLabel();
		Info.setBounds(212, 329, 500, 25);
		Info2.setBounds(212, 345, 500, 25);
		Info.setText("UP: forward; DOWN: backward; LEFT: trun left; RIGHT: trun right");

		Info2.setText("1: +speed; 2: -speed; 3: +angle; 4: -angle");

		Alfie_Speed = new JEditorPane();
		Alfie_Speed.setBounds(380, 373, 40, 25);
		Alfie_Speed.setText("0");

		Alfie_Angle = new JEditorPane();
		Alfie_Angle.setBounds(260, 373, 40, 25);
		Alfie_Angle.setText("0");

		Speed = new JLabel();
		Speed.setText("Speed");
		Speed.setBounds(320, 373, 60, 25);

		Angle = new JLabel();
		Angle.setText("Angle");
		Angle.setBounds(210, 373, 40, 25);
		
		
		frmAlfieCommandCentre.getContentPane().add(yellowAlfieCheckbox);
		frmAlfieCommandCentre.getContentPane().add(blueAlfieCheckbox);
		
		frmAlfieCommandCentre.getContentPane().add(connectButton);
		frmAlfieCommandCentre.getContentPane().add(grabImageCheckbox);
		frmAlfieCommandCentre.getContentPane().add(processImageCheckbox);
		frmAlfieCommandCentre.getContentPane().add(previewImageCheckbox);
		frmAlfieCommandCentre.getContentPane().add(bakeInfoCheckbox);
		frmAlfieCommandCentre.getContentPane().add(planCheckbox);
		frmAlfieCommandCentre.getContentPane().add(executePlanCheckbox);
		frmAlfieCommandCentre.getContentPane().add(runButton);
		
		frmAlfieCommandCentre.getContentPane().add(notes);

		frmAlfieCommandCentre.getContentPane().add(startPlanningButton);
		frmAlfieCommandCentre.getContentPane().add(penaltyButton);
		frmAlfieCommandCentre.getContentPane().add(goalieButton);
		frmAlfieCommandCentre.getContentPane().add(stopPlanningButton);
		frmAlfieCommandCentre.getContentPane().add(robotPositionButtonLeft);
		frmAlfieCommandCentre.getContentPane().add(robotPositionButtonRight);
		
		frmAlfieCommandCentre.getContentPane().add(txtLog);
		frmAlfieCommandCentre.getContentPane().add(Info);
		frmAlfieCommandCentre.getContentPane().add(Info2);
		frmAlfieCommandCentre.getContentPane().add(Alfie_Angle);
		frmAlfieCommandCentre.getContentPane().add(Alfie_Speed);
		frmAlfieCommandCentre.getContentPane().add(Angle);
		frmAlfieCommandCentre.getContentPane().add(Speed);
		
		frmAlfieCommandCentre.getContentPane().add(blueToRedHueLabel);
		frmAlfieCommandCentre.getContentPane().add(redToYellowHueLabel);
		frmAlfieCommandCentre.getContentPane().add(yellowToGreenHueLabel);
		frmAlfieCommandCentre.getContentPane().add(greenToBlueHueLabel);
		
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

	@Override
	public void keyPressed(KeyEvent key) {
		
		switch (key.getKeyCode()) {

		// when pressed "up" key the robot move forward
		case KeyEvent.VK_UP:

			alfieServer.sendGoForward(Integer.parseInt(Alfie_Speed.getText()), Integer.parseInt(Alfie_Angle.getText()));
			key_pressed = KeyEvent.VK_UP;
			break;

		// when pressed "left" key the robot turn left
		case KeyEvent.VK_LEFT:
			
			alfieServer.sendSpinLeft(Integer.parseInt(Alfie_Speed.getText()), Integer.parseInt(Alfie_Angle.getText()));
			key_pressed = KeyEvent.VK_LEFT;
			break;

		// when pressed "right" key the robot turn right
		case KeyEvent.VK_RIGHT:

			alfieServer.sendSpinRight(Integer.parseInt(Alfie_Speed.getText()), Integer.parseInt(Alfie_Angle.getText()));
			key_pressed = KeyEvent.VK_RIGHT;
			break;

		// when pressed "up" key the robot move backward
		case KeyEvent.VK_DOWN:

			alfieServer.sendGoBackwards(Integer.parseInt(Alfie_Speed.getText()), Integer.parseInt(Alfie_Angle.getText()));
			key_pressed = KeyEvent.VK_DOWN;
			break;
		}

	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent key) {
		System.out.println(key.getKeyChar());
		if (key.getKeyChar() == 's') {
			alfieServer.sendKick(1000);

		}
		if (key.getKeyChar() == 'd') {
			alfieServer.sendStop();
			System.out.println("ok");
		}

		if (key.getKeyChar() == '1') {
			Alfie_Speed.setText(Integer.parseInt(Alfie_Speed.getText()) + 1
					+ "");
		}
		if (key.getKeyChar() == '2') {
			Alfie_Speed.setText(Integer.parseInt(Alfie_Speed.getText()) - 1
					+ "");
		}

		if (key.getKeyChar() == '3') {
			Alfie_Angle.setText(Integer.parseInt(Alfie_Angle.getText()) + 1
					+ "");
		}
		if (key.getKeyChar() == '4') {
			Alfie_Angle.setText(Integer.parseInt(Alfie_Angle.getText()) - 1
					+ "");
		}
		if (key.getKeyChar() == ',') {
			alfieServer.sendSpinLeft(Integer.parseInt(Alfie_Speed.getText()), Integer.parseInt(Alfie_Angle.getText()));
		}
		if (key.getKeyChar() == '.') {
			alfieServer.sendSpinRight(Integer.parseInt(Alfie_Speed.getText()), Integer.parseInt(Alfie_Angle.getText()));
			
		}

	}

}
