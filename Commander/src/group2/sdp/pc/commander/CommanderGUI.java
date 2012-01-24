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
import javax.swing.JTextArea;

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
		frmAlfieCommandCentre.setBounds(100, 100, 443, 396);
		frmAlfieCommandCentre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAlfieCommandCentre.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.setBounds(12, 329, 118, 25);
		frmAlfieCommandCentre.getContentPane().add(btnNewButton);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(12, 12, 415, 305);
		frmAlfieCommandCentre.getContentPane().add(textPane);
	}
}
