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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JTextArea;
import javax.swing.JLabel;

public class CommanderGUIUnstable implements KeyListener {

	private JFrame frmAlfieCommandCentre;
	private JTextPane txtLog;
	private JEditorPane  Alfie_Speed;
	private JEditorPane  Alfie_Angle;

	private Server alfie;

	// No of connection attempts before giving up
	private static final int CONNECTION_ATTEMPTS = 5;
	private static final int RETRY_TIMEOUT = 3000;

	private int key_pressed = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CommanderGUIUnstable window = new CommanderGUIUnstable();
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
	public CommanderGUIUnstable() {
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
		txtLog.setEditable(false);
		txtLog.setBounds(12, 12, 415, 305);
		frmAlfieCommandCentre.getContentPane().add(txtLog);
		txtLog.addKeyListener(this);
		JButton btnTakePenalty = new JButton("Take Penalty");
		btnTakePenalty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				alfie.sendKick(1000);
				log("Sending Penalty Code");
			}
		});
		btnTakePenalty.setBounds(12, 329, 142, 25);
		frmAlfieCommandCentre.getContentPane().add(btnTakePenalty);

		Alfie_Speed = new JEditorPane();
		Alfie_Speed.setBounds(166, 329, 118, 25);
		Alfie_Speed.setText("Speed");

		Alfie_Angle = new JEditorPane();
		Alfie_Angle.setBounds(12, 373, 63, 25);
		Alfie_Angle.setText("Angle");
		
		frmAlfieCommandCentre.getContentPane().add(Alfie_Angle);
		frmAlfieCommandCentre.getContentPane().add(Alfie_Speed);
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
		} catch (Exception e) {
			return false;
		}
	}

	Thread init_thread = new Thread() {

		public void run() {

			for (int i = 1; i < CONNECTION_ATTEMPTS; ++i) {

				log("Connection attempt: " + i);

				try {
					alfie = new Server();
					log("Connected to Alfie");
					this.stop();
				} catch (Exception e) {
					log("Failed to connect... Retrying in "
							+ (RETRY_TIMEOUT / 5) + " seconds");
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

	@Override
	public void keyPressed(KeyEvent key) {
		switch (key.getKeyCode()) {

		// when pressed "up" key the robot move forward
		case KeyEvent.VK_UP:

			if (key_pressed != KeyEvent.VK_UP)
				
				alfie.sendGoForward(Integer.parseInt(Alfie_Speed.getText()));
			key_pressed = KeyEvent.VK_UP;
			break;

		// when pressed "left" key the robot turn left
		case KeyEvent.VK_LEFT:

			alfie.sendSpin(Integer.parseInt(Alfie_Speed.getText()), Integer.parseInt(Alfie_Angle.getText()));
			key_pressed = KeyEvent.VK_LEFT;
			break;

		// when pressed "right" key the robot turn right
		case KeyEvent.VK_RIGHT:

			alfie.sendSpin(Integer.parseInt(Alfie_Speed.getText()), -Integer.parseInt(Alfie_Angle.getText()));
			key_pressed = KeyEvent.VK_RIGHT;
			break;

		// when pressed "up" key the robot move backward
		case KeyEvent.VK_DOWN:
			if (key_pressed != KeyEvent.VK_DOWN)
				alfie.sendGoBackwards(Integer.parseInt(Alfie_Speed.getText()));
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
		if(key.getKeyChar()=='s'){
			alfie.sendKick(10);
			
		}
		if(key.getKeyChar()=='d'){
			alfie.sendStop();
			System.out.println("ok");
		}	
	
		
	}
}
