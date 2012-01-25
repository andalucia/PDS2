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

	private Server alfie;
	
	// No of connection attempts before giving up
	private static final int CONNECTION_ATTEMPTS = 5;
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
		frmAlfieCommandCentre.setBounds(100, 100, 443, 440);
		frmAlfieCommandCentre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAlfieCommandCentre.getContentPane().setLayout(null);
		
		txtLog = new JTextPane();
		txtLog.setBounds(12, 12, 415, 305);
		frmAlfieCommandCentre.getContentPane().add(txtLog);
		
		JButton btnTakePenalty = new JButton("Take Penalty");
		btnTakePenalty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				alfie.sendKick(1000);
				log("Sending Penalty Code");
			}
		});
		btnTakePenalty.setBounds(12, 329, 142, 25);
		frmAlfieCommandCentre.getContentPane().add(btnTakePenalty);
		
		JButton btnGoForward = new JButton("Go Forward");
		btnGoForward.setBounds(166, 329, 118, 25);
		frmAlfieCommandCentre.getContentPane().add(btnGoForward);
		
		JButton btnKick = new JButton("Kick");
		btnKick.setBounds(12, 373, 63, 25);
		frmAlfieCommandCentre.getContentPane().add(btnKick);
		
		JButton btnStop = new JButton("Stop");
		btnStop.setBounds(87, 373, 75, 25);
		frmAlfieCommandCentre.getContentPane().add(btnStop);
		
		frmAlfieCommandCentre.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmAlfieCommandCentre.setVisible(true);
		frmAlfieCommandCentre.addWindowListener(new WindowListener() {
            public void windowClosed(WindowEvent arg0) {
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
	
	private boolean initAlfie() {
		try {
			alfie = new Server();
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	Thread init_thread = new Thread() {
		
		public void run() {
			
			for(int i = 1; i < CONNECTION_ATTEMPTS; ++i) {	
				
				log("Connection attempt: " + i);
				
				try {
					alfie = new Server();
					log("Connected to Alfie");
					this.stop();
				} catch(Exception e) {
					log("Failed to connect... Retrying in " + (RETRY_TIMEOUT / 5) + " seconds");
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
	
	private void log(String logString) {
		txtLog.setText(txtLog.getText() + logString + "\n");
		txtLog.repaint();
	}
}