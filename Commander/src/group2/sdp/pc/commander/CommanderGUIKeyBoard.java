package group2.sdp.pc.commander;

import java.awt.EventQueue;

import javax.swing.JFrame;

import javax.swing.JTextPane;
import javax.swing.JEditorPane;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JLabel;

public class CommanderGUIKeyBoard implements KeyListener {

	private JFrame frmAlfieCommandCentre;
	private JTextPane txtLog;
	private JEditorPane Alfie_Speed;
	private JEditorPane Alfie_Angle;

	private JLabel Speed;
	private JLabel Angle;

	private JLabel Info;
	private JLabel Info2;

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
					CommanderGUIKeyBoard window = new CommanderGUIKeyBoard();
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
	public CommanderGUIKeyBoard() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAlfieCommandCentre = new JFrame();
		frmAlfieCommandCentre.setTitle("Alfie Command Centre");
		frmAlfieCommandCentre.setBounds(100, 100, 500, 440);
		frmAlfieCommandCentre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAlfieCommandCentre.getContentPane().setLayout(null);

		txtLog = new JTextPane();
		txtLog.setEditable(false);
		txtLog.setBounds(12, 12, 472, 305);
		frmAlfieCommandCentre.getContentPane().add(txtLog);
		txtLog.addKeyListener(this);
		Info = new JLabel();
		Info2 = new JLabel();
		Info.setBounds(12, 329, 500, 25);
		Info2.setBounds(12, 345, 500, 25);
		Info.setText("UP: forward; DOWN: backward; LEFT: trun left; RIGHT: trun right");

		Info2.setText("1: +speed; 2: -speed; 3: +angle; 4: -angle");

		frmAlfieCommandCentre.getContentPane().add(Info);
		frmAlfieCommandCentre.getContentPane().add(Info2);

		Alfie_Speed = new JEditorPane();
		Alfie_Speed.setBounds(180, 373, 40, 25);
		Alfie_Speed.setText("0");

		Alfie_Angle = new JEditorPane();
		Alfie_Angle.setBounds(60, 373, 40, 25);
		Alfie_Angle.setText("0");

		Speed = new JLabel();
		Speed.setText("Speed");
		Speed.setBounds(120, 373, 60, 25);

		Angle = new JLabel();
		Angle.setText("Angle");
		Angle.setBounds(10, 373, 40, 25);

		frmAlfieCommandCentre.getContentPane().add(Alfie_Angle);
		frmAlfieCommandCentre.getContentPane().add(Alfie_Speed);
		frmAlfieCommandCentre.getContentPane().add(Angle);
		frmAlfieCommandCentre.getContentPane().add(Speed);
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

			alfie.sendSpin(Integer.parseInt(Alfie_Speed.getText()),
					Integer.parseInt(Alfie_Angle.getText()));
			key_pressed = KeyEvent.VK_LEFT;
			break;

		// when pressed "right" key the robot turn right
		case KeyEvent.VK_RIGHT:

			alfie.sendSpin(Integer.parseInt(Alfie_Speed.getText()),
					-Integer.parseInt(Alfie_Angle.getText()));
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
		if (key.getKeyChar() == 's') {
			alfie.sendKick(1000);

		}
		if (key.getKeyChar() == 'd') {
			alfie.sendStop();
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

	}
}
