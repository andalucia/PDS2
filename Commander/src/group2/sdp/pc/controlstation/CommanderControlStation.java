package group2.sdp.pc.controlstation;

import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.globalinfo.Pitch;
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
public class CommanderControlStation {
	
	/**
	 * used to check if image is processing
	 */
	private boolean isProcessing;
	// GUI elements
	private JFrame frmAlfieCommandCentre;
	private SettingsWindow settingsWindow;
	
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
//	private Button robotPositionButtonLeft;
//	private Button robotPositionButtonRight;
	private Button goalieButton;

	
	private JTextPane txtLog;

	private GlobalInfo globalInfo;
	private boolean attackingRight = true;
	/**
	 * The server that sends commands to Alfie.
	 */
	private Mouth alfieServer;
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
						alfieServer = new Mouth();
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
		
		//FIXME change null to finder when it implements correctly
		FieldMarshal marshal = new FieldMarshal(globalInfo, finder, finder);
		
		lord = new Overlord(globalInfo, marshal, marshal);
		
		Bakery bakery = new Bakery(lord);
		
		Artist previewer = new Artist();
		
		if (processImageCheckbox.getState()) {
			processor = new VisualCortex(globalInfo, bakery, previewer);
			new Eye(processor);
		} else {
			new Eye(previewer);
		}
		
	}
	
	/**
	 * Initialise the contents of the frame.
	 */
	private void initializeFrame() {
		globalInfo = new GlobalInfo(true, true, Pitch.ONE);
		
		frmAlfieCommandCentre = new JFrame();
		frmAlfieCommandCentre.setTitle("Alfie Command Centre");
		frmAlfieCommandCentre.setBounds(100, 100, 672, 469);
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
	    yellowAlfieCheckbox.setBounds(380, 75, 160, 25);
	    
	    blueAlfieCheckbox = new Checkbox("Blue Alfie", yellowBlueAlfieGroup, false);
	    blueAlfieCheckbox.setBounds(380, 105, 160, 25);
		
		
		processImageCheckbox = new Checkbox();
		processImageCheckbox.setLabel("Process image");
		processImageCheckbox.setBounds(380, 150, 160, 25);
		processImageCheckbox.setState(true);
		
		runButton = new Button();
		runButton.setLabel("Initialise");
		runButton.setBounds(380, 208, 100, 25);
		runButton.setBackground(Color.yellow);
		runButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (processImageCheckbox.getState()) {
					isProcessing = true;
				}
				startPipeline();
				settingsWindow = new SettingsWindow(globalInfo, processor);
				
			    yellowAlfieCheckbox.setEnabled(false);
			    blueAlfieCheckbox.setEnabled(false);
				connectButton.setEnabled(false);
				processImageCheckbox.setEnabled(false);
				runButton.setEnabled(false);
			}
		});
		
		shootingDirectionGroup = new CheckboxGroup();
		
	    leftAlfieCheckbox = new Checkbox("Alfie shooting right", shootingDirectionGroup, true);
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
	    
	    rightAlfieCheckbox = new Checkbox("Alfie shooting left", shootingDirectionGroup, false);
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
		startPlanningButton.setBounds(380, 400, 100, 25);
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
		goalieButton.setLabel("Goalkeeper!");
		goalieButton.setBounds(550, 310, 100, 25);
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
		
		penaltyButton = new Button();
		penaltyButton.setLabel("Take Penalty!");
		penaltyButton.setBounds(550, 345, 100, 25);
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
		
		stopPlanningButton = new Button();
		stopPlanningButton.setLabel("Stop!");
		stopPlanningButton.setBounds(550, 400, 100, 25);
		stopPlanningButton.setBackground(Color.red);
		stopPlanningButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lord != null) {
					lord.stop();
					alfieServer.sendStop();
				}
			}
			
			
		});
		
		txtLog = new JTextPane();
		txtLog.setEditable(false);
		txtLog.setBounds(20, 50, 300, 375);
		
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
