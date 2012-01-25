package group2.sdp.pc.commander;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JEditorPane;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JFormattedTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JTextArea;
import javax.swing.JLabel;

public class CommanderGUI {

	private JFrame frmAlfieCommandCentre;
	private JTextPane txtLog;

	private Server alfieServer;
	public boolean connected = false;
	
	// No of connection attempts before giving up
	private static final int CONNECTION_ATTEMPTS = 1;
	private static final int RETRY_TIMEOUT = 3000;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CommanderGUI window = new CommanderGUI();
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
	public CommanderGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
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
					alfieServer.sendGoForward(10);
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
            	init();
            }
            public void windowClosing(WindowEvent arg0) {
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

		
		// Check whether Al
	}
	
	private void init() {
		// Attempt to initialise the bluetooth connection		
		init_thread.start();
	}
		
	Thread init_thread = new Thread() {
		
		public void run() {
			
			for(int i = 1; i <= CONNECTION_ATTEMPTS; ++i) {	
				
				log("Connection attempt: " + i);
				
				try {
					alfieServer = new Server();
					connected = true;
					log("Connected to Alfie");
					Thread.yield();
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
			
			Thread.yield();
		}
	
	};
	
	private void log(String logString) {
		txtLog.setText(txtLog.getText() + logString + "\n");
		txtLog.repaint();
	}
}
