package group2.sdp.pc.controlstation;

import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.globalinfo.Pitch;
import group2.sdp.pc.globalinfo.Salvator;
import group2.sdp.pc.mouth.Mouth;
import group2.sdp.pc.planner.FieldMarshal;
import group2.sdp.pc.planner.Overlord;
import group2.sdp.pc.planner.PathFinder;
import group2.sdp.pc.planner.Penalty;
import group2.sdp.pc.vision.Artist;
import group2.sdp.pc.vision.Bakery;
import group2.sdp.pc.vision.Eye;
import group2.sdp.pc.vision.VisualCortex;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JTextPane;

/**
 * The main GUI program to control Alfie during match or testing.
 */
public class ControlStation {
	
	/**
	 * used to check if image is processing
	 */
	private boolean isProcessing;
	// GUI elements
	private JFrame frmAlfieCommandCentre;
	@SuppressWarnings("unused")
	private SettingsWindow settingsWindow;
	
	private CheckboxGroup pitchGroup;
	private Checkbox pitchOneCheckbox;
	private Checkbox pitchTwoCheckbox;
	
	private CheckboxGroup yellowBlueAlfieGroup;
	private Checkbox yellowAlfieCheckbox;
	private Checkbox blueAlfieCheckbox;
	
	private CheckboxGroup shootingDirectionGroup;
	private Checkbox rightAlfieCheckbox;
	private Checkbox leftAlfieCheckbox;
	
	private Checkbox processImageCheckbox;

	private Button connectButton;
	private Button runButton;
	private Button startPlanningButton;
	private Button stopPlanningButton;
	private Button penaltyButton;
	private Button goalieButton;

	
	private static JTextPane txtLog;

	/**
	 * The server that sends commands to Alfie.
	 */
	private Mouth alfieMouth;
	private VisualCortex processor;
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
	private static ControlStation instance;



	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ControlStation window = new ControlStation();
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
	public ControlStation() throws Exception {
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
						alfieMouth = new Mouth();
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
				alfieMouth.sendReset();
			}
		};
	}
	
	
	/**
	 * Starts the processing pipeline.
	 */
	private void startPipeline() {
		GlobalInfo.setAttackingRight(rightAlfieCheckbox.getState());
		GlobalInfo.setYellowAlfie(yellowAlfieCheckbox.getState());
		GlobalInfo.setPitchOne(
				pitchOneCheckbox.getState()
				? true
				: false
				);
		Salvator.loadLCHSettings();
		
		
		PathFinder finder = new PathFinder(alfieMouth);
		
		//FIXME change null to finder when it implements correctly
		FieldMarshal marshal = new FieldMarshal(finder, finder);
		
		lord = new Overlord(marshal, marshal);
		
		Bakery bakery = new Bakery(lord);
		
		Artist previewer = new Artist();
		
		if (processImageCheckbox.getState()) {
			processor = new VisualCortex(bakery, previewer);
			new Eye(processor);
		} else {
			new Eye(previewer);
		}
		
	}
	
	/**
	 * Initialise the contents of the frame.
	 */
	private void initializeFrame() {
		frmAlfieCommandCentre = new JFrame();
		frmAlfieCommandCentre.setTitle("Alfie Command Centre");
		frmAlfieCommandCentre.setBounds(100, 100, 632, 507);
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
		
		
		pitchGroup = new CheckboxGroup();
		
	    pitchOneCheckbox = new Checkbox("Pitch One", pitchGroup, true);
	    pitchOneCheckbox.setBounds(332, 75, 160, 25);
	    
	    pitchTwoCheckbox = new Checkbox("Pitch Two", pitchGroup, false);
	    pitchTwoCheckbox.setBounds(332, 105, 160, 25);
	    
		
		yellowBlueAlfieGroup = new CheckboxGroup();
		
	    yellowAlfieCheckbox = new Checkbox("Yellow Alfie", yellowBlueAlfieGroup, true);
	    yellowAlfieCheckbox.setBounds(514, 75, 160, 25);
	    
	    blueAlfieCheckbox = new Checkbox("Blue Alfie", yellowBlueAlfieGroup, false);
	    blueAlfieCheckbox.setBounds(514, 105, 160, 25);
		
		
		processImageCheckbox = new Checkbox();
		processImageCheckbox.setLabel("Process image");
		processImageCheckbox.setBounds(332, 150, 160, 25);
		processImageCheckbox.setState(true);
		
		runButton = new Button();
		runButton.setLabel("Initialise");
		runButton.setBounds(332, 208, 100, 25);
		runButton.setBackground(Color.yellow);
		runButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (processImageCheckbox.getState()) {
					isProcessing = true;
				}
				startPipeline();
				settingsWindow = new SettingsWindow(processor);
				
				frmAlfieCommandCentre.setLocation(0, 0);
				
				pitchOneCheckbox.setEnabled(false);
			    pitchTwoCheckbox.setEnabled(false);
				
				yellowAlfieCheckbox.setEnabled(false);
			    blueAlfieCheckbox.setEnabled(false);
			    
				connectButton.setEnabled(false);
				processImageCheckbox.setEnabled(false);
				runButton.setEnabled(false);
			}
		});
		
		shootingDirectionGroup = new CheckboxGroup();
		
	    leftAlfieCheckbox = new Checkbox("Alfie shooting left", shootingDirectionGroup, true);
	    leftAlfieCheckbox.setBounds(332, 310, 160, 25);
	    leftAlfieCheckbox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (leftAlfieCheckbox.getState()) {

					GlobalInfo.setAttackingRight(false);
					log("Attacking left now");
				}
			}
	    });
	    
	    rightAlfieCheckbox = new Checkbox("Alfie shooting right", shootingDirectionGroup, false);
	    rightAlfieCheckbox.setBounds(332, 340, 160, 25);
	    rightAlfieCheckbox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (rightAlfieCheckbox.getState()) {

					GlobalInfo.setAttackingRight(true);
					log("Attacking right now");
				}
			}
	    });		
	    
		startPlanningButton = new Button();
		startPlanningButton.setLabel("Start Planning");
		startPlanningButton.setBounds(332, 435, 100, 25);
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
		

		
		goalieButton = new Button();
		goalieButton.setLabel("Defend penalty!");
		goalieButton.setBounds(502, 310, 100, 25);
		goalieButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lord != null) {
					lord.stop();
					lord.defendPenalty();
				} else {
					log("OVERLORD IS NULL WHEN PENALTY CALLED");
				}
			}
			
			
		});
		
		penaltyButton = new Button();
		penaltyButton.setLabel("Take Penalty!");
		penaltyButton.setBounds(502, 345, 100, 25);
		penaltyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lord != null) {
					lord.stop();
					Penalty pen=new Penalty(alfieMouth,lord);
					pen.go();
				}else{
					log("OVERLORD IS NULL WHEN PENALTY CALLED");
				}
			}
			
			
		});
		
		stopPlanningButton = new Button();
		stopPlanningButton.setLabel("Stop!");
		stopPlanningButton.setBounds(502, 435, 100, 25);
		stopPlanningButton.setBackground(Color.red);
		stopPlanningButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lord != null) {
					lord.stop();
					if (alfieMouth != null) {
						alfieMouth.sendStop();
					} else {
						log("Mouth is already shut.");
					}
				}
			}
			
			
		});
		
		txtLog = new JTextPane();
		txtLog.setEditable(false);
		txtLog.setBounds(20, 50, 300, 410);
		
		frmAlfieCommandCentre.getContentPane().add(rightAlfieCheckbox);
		frmAlfieCommandCentre.getContentPane().add(leftAlfieCheckbox);
		
		frmAlfieCommandCentre.getContentPane().add(pitchOneCheckbox);
		frmAlfieCommandCentre.getContentPane().add(pitchTwoCheckbox);
		
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
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.exit(0);
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
	public static ControlStation getInstance() {
		return instance;
	}
	
	
	/**
	 * Adds a string and a new line to the log text box.
	 * @param logString The string to add.
	 */
	public static void log(String logString) {
		txtLog.setText(txtLog.getText() + logString + "\n");
		txtLog.repaint();
	}

}
