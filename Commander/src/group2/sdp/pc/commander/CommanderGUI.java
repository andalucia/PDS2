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

public class CommanderGUI {

	private JFrame frmAlfieCommandCentre;
	public Server alfie = new Server();
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
		frmAlfieCommandCentre.setBounds(100, 100, 284, 64);
		frmAlfieCommandCentre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAlfieCommandCentre.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnGoForward = new JButton("Go Forward");
		btnGoForward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				alfie.sendGoForward(54);
			}
		});
		frmAlfieCommandCentre.getContentPane().add(btnGoForward);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				alfie.sendStop();
			}
		});
		frmAlfieCommandCentre.getContentPane().add(btnStop);
		
		JButton btnKick = new JButton("Kick");
		btnKick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				alfie.sendKick(10000);
			}
		});
		frmAlfieCommandCentre.getContentPane().add(btnKick);
	}
}
