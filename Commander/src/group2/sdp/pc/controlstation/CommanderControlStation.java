package group2.sdp.pc.controlstation;

import group2.sdp.pc.planner.Milestone2Planner;
import group2.sdp.pc.planner.Milestone2Planner.Mode;
import group2.sdp.pc.planner.PlanExecutor;
import group2.sdp.pc.server.Server;
import group2.sdp.pc.vision.Bakery;
import group2.sdp.pc.vision.ImageGrabber;
import group2.sdp.pc.vision.ImagePreviewer;
import group2.sdp.pc.vision.ImageProcessor;
import group2.sdp.pc.vision.ImageProcessor2;
import group2.sdp.pc.vision.LCHColour;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
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
	private int blueToRedHue = -60;
	
	private final int MIN_RY_HUE = 0, MAX_RY_HUE = 60;
	private int redToYellowHue = 30;
	
	private final int MIN_YG_HUE = 60, MAX_YG_HUE = 120;
	private int yellowToGreenHue = 80;
	
	private final int MIN_GB_HUE = 120, MAX_GB_HUE = 240;
	private int greenToBlueHue = 150;
	
	// GUI elements
	private JFrame frmAlfieCommandCentre;
	
	private CheckboxGroup yellowBlueAlfieGroup;
	private Checkbox yellowAlfieCheckbox;
	private Checkbox blueAlfieCheckbox;
	
	private Label[] colorLabels;
	
	private Label lumaLabel;
	private Checkbox [][] lumaCheckboxes;
	
	private Label chromaLabel;
	private Checkbox [][] chromaCheckboxes;
	
	private Checkbox connectToAlfieCheckbox;
	private Checkbox grabImageCheckbox;
	private Checkbox processImageCheckbox;
	private Checkbox bakeInfoCheckbox;
	private Checkbox previewImageCheckbox;
	private Checkbox planCheckbox;
	private Checkbox planDribble;
	private Checkbox executePlanCheckbox;

	private Button runButton;
	private Button updateButton;
	
	private JLabel blueToRedHueLabel;
	private JLabel redToYellowHueLabel;
	private JLabel yellowToGreenHueLabel;
	private JLabel greenToBlueHueLabel;	
	
	private JSlider blueToRedHueSlider;
	private JSlider redToYellowHueSlider;
	private JSlider yellowToGreenHueSlider;
	private JSlider greenToBlueHueSlider;
	
	private Button grabImageButton;
	
	private JTextPane txtLog;
	private JEditorPane Alfie_Speed;
	private JEditorPane Alfie_Angle;

	private JLabel Speed;
	private JLabel Angle;

	private JLabel Info;
	private JLabel Info2;
	
	/**
	 * The server that sends commands to Alfie.
	 */
	private Server alfieServer;
	private ImageProcessor2 processor2;

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
	 */
	public CommanderControlStation() {
		initializeFrame();
	}

	/**
	 * Initialise the threads for connecting to and disconnecting from Alfie.
	 */
	private void initializeConnectionThreads() {
		init_thread = new Thread() {		

			public void run() {
				if (connectToAlfieCheckbox.getState()) {
					for(int i = 1; i <= CONNECTION_ATTEMPTS && !exiting; ++i) {	
						log("Connection attempt: " + i);
						
						try {
							alfieServer = new Server();
							log("Connected to Alfie");
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
				PlanExecutor executor = new PlanExecutor(alfieServer);
				Milestone2Planner planner = new Milestone2Planner(executor);
				Bakery bakery = new Bakery(planner);
				ImagePreviewer previewer = new ImagePreviewer();
				if (processImageCheckbox.getState()) {
					ImageProcessor processor = new ImageProcessor(bakery, yellowAlfieCheckbox.getState(), previewer);
					processor2 = new ImageProcessor2(null, yellowAlfieCheckbox.getState(), processor);
					new ImageGrabber(processor2);
				} else {
					new ImageGrabber(previewer);
				}
				if (planCheckbox.getState()) {
					planner.setCurrentMode(Mode.GET_TO_BALL);
					planner.start();
				}
				if (planDribble.getState()){
					planner.setCurrentMode(Mode.DRIBBLE);
					planner.start();
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
	 * Initialize the contents of the frame.
	 */
	private void initializeFrame() {
		frmAlfieCommandCentre = new JFrame();
		frmAlfieCommandCentre.setTitle("Alfie Command Centre");
		frmAlfieCommandCentre.setBounds(100, 100, 1000, 440);
		frmAlfieCommandCentre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAlfieCommandCentre.getContentPane().setLayout(null);
		
		yellowBlueAlfieGroup = new CheckboxGroup();
		
	    yellowAlfieCheckbox = new Checkbox("Yellow Alfie", yellowBlueAlfieGroup, true);
	    yellowAlfieCheckbox.setBounds(12, 12, 160, 25);
	    
	    blueAlfieCheckbox = new Checkbox("Blue Alfie", yellowBlueAlfieGroup, false);
	    blueAlfieCheckbox.setBounds(12, 40, 160, 25);
	    		
		connectToAlfieCheckbox = new Checkbox();
		connectToAlfieCheckbox.setLabel("Connect to Alfie");
		connectToAlfieCheckbox.setBounds(12, 68, 160, 25);
		connectToAlfieCheckbox.setState(false);
		
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
		
		planDribble = new Checkbox();
		planDribble.setLabel("Plan for dribble");
		planDribble.setBounds(12, 236, 160, 25);
		planDribble.setState(false);
		
		executePlanCheckbox = new Checkbox();
		executePlanCheckbox.setLabel("Execute plan");
		executePlanCheckbox.setBounds(12, 264, 160, 25);
		executePlanCheckbox.setState(true);
		
		runButton = new Button();
		runButton.setLabel("RUN!");
		runButton.setBounds(42, 292, 100, 25);
		runButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				initializeConnectionThreads();
				init_thread.start();
				
			    yellowAlfieCheckbox.setEnabled(false);
			    blueAlfieCheckbox.setEnabled(false);
				connectToAlfieCheckbox.setEnabled(false);
				grabImageCheckbox.setEnabled(false);
				processImageCheckbox.setEnabled(false);
				previewImageCheckbox.setEnabled(false);
				bakeInfoCheckbox.setEnabled(false);
				planCheckbox.setEnabled(false);
				planDribble.setEnabled(false);
				executePlanCheckbox.setEnabled(false);
				runButton.setEnabled(false);
				
				blueToRedHueSlider.setEnabled(true);
				redToYellowHueSlider.setEnabled(true);
				yellowToGreenHueSlider.setEnabled(true);
				greenToBlueHueSlider.setEnabled(true);
				
				grabImageButton.setEnabled(true);
			}
		});
		
		final int COLOUR_NUM = 6;
		final int LEVELS_NUM = 3;
		
		String [] colours = {"Yellow", "Blue", "Plate", "Pitch", "Red", "Gray"}; 
		colorLabels = new Label[6];
		for (int i = 0; i < COLOUR_NUM; ++i) {
			colorLabels[i] = new Label();
			colorLabels[i].setBounds(212, 40 + i * 28, 45, 25);
			colorLabels[i].setText(colours[i]);
			frmAlfieCommandCentre.getContentPane().add(colorLabels[i]);
		}
		
		chromaLabel = new Label();
		chromaLabel.setBounds(356, 12, 81, 25);
		chromaLabel.setText("Chroma");
		
		chromaCheckboxes = new Checkbox [COLOUR_NUM][LEVELS_NUM];
		for (int i = 0; i < COLOUR_NUM; ++i) {
			for (int j = 0; j < LEVELS_NUM; ++j) {
				chromaCheckboxes[i][j] = new Checkbox();
				chromaCheckboxes[i][j].setBounds(356 + j * 28, 40 + i * 28, 25, 25);
				chromaCheckboxes[i][j].setState(LCHColour.getChromaCheck(i, j));
				frmAlfieCommandCentre.getContentPane().add(chromaCheckboxes[i][j]);
			}
		}
		
		lumaLabel = new Label();
		lumaLabel.setBounds(260, 12, 81, 25);
		lumaLabel.setText("Luminosity");
		
		lumaCheckboxes = new Checkbox [COLOUR_NUM][LEVELS_NUM];
		for (int i = 0; i < COLOUR_NUM; ++i) {
			for (int j = 0; j < LEVELS_NUM; ++j) {
				lumaCheckboxes[i][j] = new Checkbox();
				lumaCheckboxes[i][j].setBounds(260 + j * 28, 40 + i * 28, 25, 25);
				lumaCheckboxes[i][j].setState(LCHColour.getLumaCheck(i, j));
				frmAlfieCommandCentre.getContentPane().add(lumaCheckboxes[i][j]);
			}
		}
		
		updateButton = new Button();
		updateButton.setLabel("Update");
		updateButton.setBounds(260, 292, 100, 25);
		updateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < COLOUR_NUM; ++i) {
					for (int j = 0; j < LEVELS_NUM; ++j) {
						LCHColour.setLumaCheck(i, j, lumaCheckboxes[i][j].getState());
						LCHColour.setChromaCheck(i, j, chromaCheckboxes[i][j].getState());
					}
				}
			}
		});
		
		blueToRedHueLabel = new JLabel();
		blueToRedHueLabel.setText("B/R");
		blueToRedHueLabel.setBounds(696, 12, 30, 25);
		
		blueToRedHueSlider = new JSlider(JSlider.HORIZONTAL, MIN_BR_HUE, MAX_BR_HUE, blueToRedHue);
		blueToRedHueSlider.setBounds(726, 12, 200, 25);
		blueToRedHueSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				blueToRedHue = blueToRedHueSlider.getValue();
				LCHColour.setBlueToRedHue(blueToRedHue + 360);
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
				LCHColour.setRedToYellowHue(redToYellowHue);
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
				LCHColour.setYellowToGreenHue(yellowToGreenHue);
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
				LCHColour.setGreenToBlueHue(greenToBlueHue);
			}
		});
		greenToBlueHueSlider.setMajorTickSpacing(20);
		greenToBlueHueSlider.setMinorTickSpacing(5);
		greenToBlueHueSlider.setPaintTicks(true);
		greenToBlueHueSlider.setEnabled(false);
		 
		grabImageButton = new Button();
		grabImageButton.setLabel("Grab background");
		grabImageButton.setBounds(726, 292, 200, 25);
		grabImageButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				processor2.grabNewBackgroundImage();
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
		
		frmAlfieCommandCentre.getContentPane().add(connectToAlfieCheckbox);
		frmAlfieCommandCentre.getContentPane().add(grabImageCheckbox);
		frmAlfieCommandCentre.getContentPane().add(processImageCheckbox);
		frmAlfieCommandCentre.getContentPane().add(previewImageCheckbox);
		frmAlfieCommandCentre.getContentPane().add(bakeInfoCheckbox);
		frmAlfieCommandCentre.getContentPane().add(planCheckbox);
		frmAlfieCommandCentre.getContentPane().add(planDribble);
		frmAlfieCommandCentre.getContentPane().add(executePlanCheckbox);
		frmAlfieCommandCentre.getContentPane().add(runButton);
		
		frmAlfieCommandCentre.getContentPane().add(lumaLabel);
		frmAlfieCommandCentre.getContentPane().add(chromaLabel);

		frmAlfieCommandCentre.getContentPane().add(updateButton);
		
//		frmAlfieCommandCentre.getContentPane().add(txtLog);
//		frmAlfieCommandCentre.getContentPane().add(Info);
//		frmAlfieCommandCentre.getContentPane().add(Info2);
//		frmAlfieCommandCentre.getContentPane().add(Alfie_Angle);
//		frmAlfieCommandCentre.getContentPane().add(Alfie_Speed);
//		frmAlfieCommandCentre.getContentPane().add(Angle);
//		frmAlfieCommandCentre.getContentPane().add(Speed);
		
		frmAlfieCommandCentre.getContentPane().add(blueToRedHueLabel);
		frmAlfieCommandCentre.getContentPane().add(redToYellowHueLabel);
		frmAlfieCommandCentre.getContentPane().add(yellowToGreenHueLabel);
		frmAlfieCommandCentre.getContentPane().add(greenToBlueHueLabel);
		
		frmAlfieCommandCentre.getContentPane().add(blueToRedHueSlider);
		frmAlfieCommandCentre.getContentPane().add(redToYellowHueSlider);
		frmAlfieCommandCentre.getContentPane().add(yellowToGreenHueSlider);
		frmAlfieCommandCentre.getContentPane().add(greenToBlueHueSlider);
		
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
	 * Adds a string and a new line to the log text box.
	 * @param logString The string to add.
	 */
	private void log(String logString) {
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
