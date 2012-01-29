package group2.sdp.pc.commander;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextPane;

/**
 * The terminal to oversee Alfie.
 */
public class CommanderTerminal {

	private JFrame frmAlfieCommandCentre;
	private JTextPane txtLog;

	private Server alfieServer;
	public boolean connected = false;
	
	// No of connection attempts before giving up
	private static final int CONNECTION_ATTEMPTS = 10;
	private static final int RETRY_TIMEOUT = 3000;

	private Thread init_thread, cleanup_thread;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CommanderTerminal window = new CommanderTerminal();
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
	public CommanderTerminal() {
		initializeConnectionThreads();
		initializeFrame();
	}
	
	/**
	 * Initialise the threads for connecting to and disconnecting from Alfie.
	 */
	private void initializeConnectionThreads() {
		init_thread = new Thread() {		
			public void run() {
				for(int i = 1; i <= CONNECTION_ATTEMPTS; ++i) {	
					log("Connection attempt: " + i);
					
					try {
						alfieServer = new Server();
						connected = true;
						log("Connected to Alfie");
						break;
					} catch(Exception e) {
						log("Failed to connect... Retrying in " + (RETRY_TIMEOUT / 1000) + " seconds");
						try {
							Thread.sleep(RETRY_TIMEOUT);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
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
	 * Initialise the contents of the frame.
	 */
	private void initializeFrame() {
		frmAlfieCommandCentre = new JFrame();
		frmAlfieCommandCentre.setTitle("Alfie Command Centre");
		frmAlfieCommandCentre.setBounds(100, 100, 443, 391);
		frmAlfieCommandCentre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAlfieCommandCentre.getContentPane().setLayout(null);
		
		txtLog = new JTextPane();
		txtLog.setBounds(12, 12, 415, 305);
		frmAlfieCommandCentre.getContentPane().add(txtLog);
		
		JButton btnTakePenalty = new JButton("Take Penalty");
		btnTakePenalty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(connected == true) {
					alfieServer.sendKick(1000);
					log("Sending Penalty Code");
				} else {
					log("Unable to send Penalty Code: Alfie not connected");
				}
			}
		});
		btnTakePenalty.setBounds(12, 329, 142, 25);
		frmAlfieCommandCentre.getContentPane().add(btnTakePenalty);
		
		JButton btnGoForward = new JButton("Go Forward");
		btnGoForward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent argo) {
				if(connected == true) {
					alfieServer.sendGoForward(25, 0);
					log("Sending Go Forward code");
				} else {
					log("Unable to send Go Forward code: Alfie not connected");
				}
			}
		});
		btnGoForward.setBounds(166, 329, 118, 25);
		frmAlfieCommandCentre.getContentPane().add(btnGoForward);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent argo) {
				if(connected == true) {
					alfieServer.sendStop();
					log("Sending Stop code");
				} else {
					log("Unable to send Stop code: Alfie not connected");
				}
			}
		});
		btnStop.setBounds(296, 329, 75, 25);
		frmAlfieCommandCentre.getContentPane().add(btnStop);
		
		frmAlfieCommandCentre.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmAlfieCommandCentre.setVisible(true);
		frmAlfieCommandCentre.addWindowListener(new WindowListener() {
            public void windowClosed(WindowEvent arg0) {
            	System.exit(0);
            }
            public void windowActivated(WindowEvent arg0) {
            }
            public void windowClosing(WindowEvent arg0) {
            	cleanup_thread.start();
            }
            public void windowDeactivated(WindowEvent arg0) {
            }
            public void windowDeiconified(WindowEvent arg0) {
            }
            public void windowIconified(WindowEvent arg0) {
            }
            public void windowOpened(WindowEvent arg0) {
            	init_thread.start();
            }
        });
	}	
	
	/**
	 * Output a string to the log box.
	 * @param logString The string to show.
	 */
	private void log(String logString) {
		txtLog.setText(txtLog.getText() + logString + "\n");
		txtLog.repaint();
	}
}
