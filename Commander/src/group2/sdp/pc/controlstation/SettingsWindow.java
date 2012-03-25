package group2.sdp.pc.controlstation;

import group2.sdp.pc.globalinfo.GlobalInfo;
import group2.sdp.pc.vision.VisualCortex;
import group2.sdp.pc.vision.VisualCortex.OutputMode;

import java.awt.Button;
import java.awt.Color;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// We do not use serialisation.
@SuppressWarnings("serial")
public class SettingsWindow extends JFrame {

	/**
	 * Coordinates for sliders.
	 */
	private int row1Y = 50;
	private int row2Y = 105;
	private int row3Y = 160;
	private int row4Y = 215;
	private int row5Y = 280;
	private int row6Y = 335;

	private int column1X = 200;
	private int column2X = 500;
	private int column3X = 800;

	int value = 0;// value for holding color values
	int tresholdValue = 0;

	private final int MIN_HUE_VALUE = 0, MAX_HUE_VALUE = 360;
	private final int MIN_OTHER_VALUE = 0, MAX_OTHER_VALUE = 255;

	// Adding side labels

	private JLabel yellowTLabel;
	private JLabel blueTLabel;
	private JLabel ballLabel;
	private JLabel plateGreenLabel;
	private JLabel pitchGreenLabel;
	private JLabel greyLabel;

	private JSlider plateHueStartSlider;
	private JSlider plateHueEndSlider;
	private JSlider plateLumaEndSlider;
	private JSlider plateLumaStartSlider;
	private JSlider plateChromaStartSlider;
	private JSlider plateChromaEndSlider;

	private JSlider pitchHueStartSlider;
	private JSlider pitchHueEndSlider;
	private JSlider pitchLumaStartSlider;
	private JSlider pitchLumaEndSlider;
	private JSlider pitchChromaStartSlider;
	private JSlider pitchChromaEndSlider;

	private JSlider blueHueStartSlider;
	private JSlider blueHueEndSlider;
	private JSlider blueLumaStartSlider;
	private JSlider blueLumaEndSlider;
	private JSlider blueChromaStartSlider;
	private JSlider blueChromaEndSlider;

	private JSlider redHueStartSlider;
	private JSlider redHueEndSlider;
	private JSlider redLumaStartSlider;
	private JSlider redLumaEndSlider;
	private JSlider redChromaStartSlider;
	private JSlider redChromaEndSlider;

	private JSlider yellowHueStartSlider;
	private JSlider yellowHueEndSlider;
	private JSlider yellowLumaStartSlider;
	private JSlider yellowLumaEndSlider;
	private JSlider yellowChromaStartSlider;
	private JSlider yellowChromaEndSlider;

	private JSlider grayHueStartSlider;
	private JSlider grayHueEndSlider;
	private JSlider grayLumaStartSlider;
	private JSlider grayLumaEndSlider;
	private JSlider grayChromaStartSlider;
	private JSlider grayChromaEndSlider;

	// adding labels

	private JLabel plateHueStartLabel;
	private JLabel plateHueEndLabel;
	private JLabel plateLumaEndLabel;
	private JLabel plateLumaStartLabel;
	private JLabel plateChromaStartLabel;
	private JLabel plateChromaEndLabel;

	private JLabel pitchHueStartLabel;
	private JLabel pitchHueEndLabel;
	private JLabel pitchLumaStartLabel;
	private JLabel pitchLumaEndLabel;
	private JLabel pitchChromaStartLabel;
	private JLabel pitchChromaEndLabel;

	private JLabel blueHueStartLabel;
	private JLabel blueHueEndLabel;
	private JLabel blueLumaStartLabel;
	private JLabel blueLumaEndLabel;
	private JLabel blueChromaStartLabel;
	private JLabel blueChromaEndLabel;

	private JLabel redHueStartLabel;
	private JLabel redHueEndLabel;
	private JLabel redLumaStartLabel;
	private JLabel redLumaEndLabel;
	private JLabel redChromaStartLabel;
	private JLabel redChromaEndLabel;

	private JLabel yellowHueStartLabel;
	private JLabel yellowHueEndLabel;
	private JLabel yellowLumaStartLabel;
	private JLabel yellowLumaEndLabel;
	private JLabel yellowChromaStartLabel;
	private JLabel yellowChromaEndLabel;

	private JLabel grayHueStartLabel;
	private JLabel grayHueEndLabel;
	private JLabel grayLumaStartLabel;
	private JLabel grayLumaEndLabel;
	private JLabel grayChromaStartLabel;
	private JLabel grayChromaEndLabel;

	// adding labels

	private Button matchVisionModeButton;
	private Button chromaVisionModeButton;
	private Button lumaVisionModeButton;
	private Button grabImageButton;
	private Button hueVisionModeButton;

	private VisualCortex processor;

	public SettingsWindow(VisualCortex processor) {
		this.processor = processor;
		setBounds(0, 532, 1280, 427);
		initializeFrame();
		setVisible(true);
	}

	private void initializeFrame() {
		// Image filtering controls

		// adding Hue Chrome Luma labels
		/*
		 * hueLabel = new JLabel(); hueLabel.setText("Hue");
		 * hueLabel.setBounds(280, 30, 60, 25);
		 * 
		 * chromeLabel = new JLabel(); chromeLabel.setText("Chroma");
		 * chromeLabel.setBounds(880, 30, 60, 25);
		 * 
		 * lumaLabel = new JLabel(); lumaLabel.setText("Luma");
		 * lumaLabel.setBounds(580, 30, 60, 25);
		 */

		// Adding side labels

		yellowTLabel = new JLabel();
		yellowTLabel.setText("Yellow T ");
		yellowTLabel.setBounds(25, row1Y, 80, 25);
		yellowTLabel.setForeground(Color.ORANGE);

		blueTLabel = new JLabel();
		blueTLabel.setText("Blue T ");
		blueTLabel.setBounds(25, row2Y, 80, 25);
		blueTLabel.setForeground(Color.BLUE);

		ballLabel = new JLabel();
		ballLabel.setText("Ball ");
		ballLabel.setBounds(25, row3Y, 80, 25);
		ballLabel.setForeground(Color.RED);

		plateGreenLabel = new JLabel();
		plateGreenLabel.setText("Plate Green");
		plateGreenLabel.setBounds(25, row4Y, 100, 25);
		plateGreenLabel.setForeground(Color.GREEN);

		pitchGreenLabel = new JLabel();
		pitchGreenLabel.setText("Pitch Green");
		pitchGreenLabel.setBounds(25, row5Y, 100, 25);
		pitchGreenLabel.setForeground(Color.GREEN);

		greyLabel = new JLabel();
		greyLabel.setText("Grey");
		greyLabel.setBounds(25, row6Y, 100, 25);
		greyLabel.setForeground(Color.GRAY);

		// ADDING SLIDERS
		// YELLOW T SLIDERS

		yellowHueStartSlider = new JSlider(JSlider.HORIZONTAL, MIN_HUE_VALUE,
				MAX_HUE_VALUE, GlobalInfo.getColourSettings()
						.getYellowHueStart());
		yellowHueStartSlider.setBounds(column1X, row1Y, 200, 25);
		yellowHueStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = yellowHueStartSlider.getValue();
				GlobalInfo.getColourSettings().setYellowHueStart(value);
				System.out.println(value);
				yellowHueStartLabel.setText(Integer
						.toString(yellowHueStartSlider.getValue()));

			}
		});

		yellowHueStartLabel = new JLabel();
		yellowHueStartLabel.setText(Integer.toString(yellowHueStartSlider
				.getValue()));
		yellowHueStartLabel.setBounds(column1X - 50, row1Y, 50, 25);

		yellowHueEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_HUE_VALUE,
				MAX_HUE_VALUE, GlobalInfo.getColourSettings().getYellowHueEnd());
		yellowHueEndSlider.setBounds(column1X, row1Y + 20, 200, 25);
		yellowHueEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = yellowHueEndSlider.getValue();
				GlobalInfo.getColourSettings().setYellowHueEnd(value);
				System.out.println(value);
				yellowHueEndLabel.setText(Integer.toString(yellowHueEndSlider
						.getValue()));
			}
		});
		yellowHueEndLabel = new JLabel();
		yellowHueEndLabel.setText(Integer.toString(yellowHueEndSlider
				.getValue()));
		yellowHueEndLabel.setBounds(column1X - 50, row1Y + 20, 50, 25);

		yellowLumaStartSlider = new JSlider(JSlider.HORIZONTAL,
				MIN_OTHER_VALUE, MAX_OTHER_VALUE, GlobalInfo
						.getColourSettings().getYellowLumaStart());
		yellowLumaStartSlider.setBounds(column2X, row1Y, 200, 25);
		yellowLumaStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = yellowLumaStartSlider.getValue();
				GlobalInfo.getColourSettings().setYellowLumaStart(value);
				System.out.println(value);
				yellowLumaStartLabel.setText(Integer
						.toString(yellowLumaStartSlider.getValue()));
			}
		});
		yellowLumaStartLabel = new JLabel();
		yellowLumaStartLabel.setText(Integer.toString(yellowLumaStartSlider
				.getValue()));
		yellowLumaStartLabel.setBounds(column2X - 50, row1Y, 50, 25);

		yellowLumaEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getYellowLumaEnd());
		yellowLumaEndSlider.setBounds(column2X, row1Y + 20, 200, 25);
		yellowLumaEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = yellowLumaEndSlider.getValue();
				GlobalInfo.getColourSettings().setYellowLumaEnd(value);
				System.out.println(value);
				yellowLumaEndLabel.setText(Integer.toString(yellowLumaEndSlider
						.getValue()));
			}
		});
		yellowLumaEndLabel = new JLabel();
		yellowLumaEndLabel.setText(Integer.toString(yellowLumaEndSlider
				.getValue()));
		yellowLumaEndLabel.setBounds(column2X - 50, row1Y + 20, 50, 25);

		yellowChromaStartSlider = new JSlider(JSlider.HORIZONTAL,
				MIN_OTHER_VALUE, MAX_OTHER_VALUE, GlobalInfo
						.getColourSettings().getYellowChromaStart());
		yellowChromaStartSlider.setBounds(column3X, row1Y, 200, 25);
		yellowChromaStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = yellowChromaStartSlider.getValue();
				GlobalInfo.getColourSettings().setYellowChromaStart(value);
				System.out.println(value);
				yellowChromaStartLabel.setText(Integer
						.toString(yellowChromaStartSlider.getValue()));
			}
		});
		yellowChromaStartLabel = new JLabel();
		yellowChromaStartLabel.setText(Integer.toString(yellowChromaStartSlider
				.getValue()));
		yellowChromaStartLabel.setBounds(column3X - 50, row1Y, 50, 25);

		yellowChromaEndSlider = new JSlider(JSlider.HORIZONTAL,
				MIN_OTHER_VALUE, MAX_OTHER_VALUE, GlobalInfo
						.getColourSettings().getYellowChromaEnd());
		yellowChromaEndSlider.setBounds(column3X, row1Y + 20, 200, 25);
		yellowChromaEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = yellowChromaEndSlider.getValue();
				GlobalInfo.getColourSettings().setYellowChromaEnd(value);
				System.out.println(value);
				yellowChromaEndLabel.setText(Integer
						.toString(yellowChromaEndSlider.getValue()));
			}
		});

		yellowChromaEndLabel = new JLabel();
		yellowChromaEndLabel.setText(Integer.toString(yellowChromaEndSlider
				.getValue()));
		yellowChromaEndLabel.setBounds(column3X - 50, row1Y + 20, 50, 25);

		// BLUE T SLIDERS
		blueHueStartSlider = new JSlider(JSlider.HORIZONTAL, MIN_HUE_VALUE,
				MAX_HUE_VALUE, GlobalInfo.getColourSettings().getBlueHueStart());
		blueHueStartSlider.setBounds(column1X, row2Y, 200, 25);
		blueHueStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = blueHueStartSlider.getValue();
				GlobalInfo.getColourSettings().setBlueHueStart(value);
				System.out.println(value);
				blueHueStartLabel.setText(Integer.toString(blueHueStartSlider
						.getValue()));
			}
		});

		blueHueStartLabel = new JLabel();
		blueHueStartLabel.setText(Integer.toString(blueHueStartSlider
				.getValue()));
		blueHueStartLabel.setBounds(column1X - 50, row2Y, 50, 25);

		blueHueEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_HUE_VALUE,
				MAX_HUE_VALUE, GlobalInfo.getColourSettings().getBlueHueEnd());
		blueHueEndSlider.setBounds(column1X, row2Y + 20, 200, 25);
		blueHueEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = blueHueEndSlider.getValue();
				GlobalInfo.getColourSettings().setBlueHueEnd(value);
				System.out.println(value);
				blueHueEndLabel.setText(Integer.toString(blueHueEndSlider
						.getValue()));
			}
		});

		blueHueEndLabel = new JLabel();
		blueHueEndLabel.setText(Integer.toString(blueHueEndSlider.getValue()));
		blueHueEndLabel.setBounds(column1X - 50, row2Y + 20, 50, 25);

		blueLumaStartSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getBlueLumaStart());
		blueLumaStartSlider.setBounds(column2X, row2Y, 200, 25);
		blueLumaStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = blueLumaStartSlider.getValue();
				GlobalInfo.getColourSettings().setBlueLumaStart(value);
				System.out.println(value);
				blueLumaStartLabel.setText(Integer.toString(blueLumaStartSlider
						.getValue()));
			}
		});

		blueLumaStartLabel = new JLabel();
		blueLumaStartLabel.setText(Integer.toString(blueLumaStartSlider
				.getValue()));
		blueLumaStartLabel.setBounds(column2X - 50, row2Y, 50, 25);

		blueLumaEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getBlueLumaEnd());
		blueLumaEndSlider.setBounds(column2X, row2Y + 20, 200, 25);
		blueLumaEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = blueLumaEndSlider.getValue();
				GlobalInfo.getColourSettings().setBlueLumaEnd(value);
				System.out.println(value);
				blueLumaEndLabel.setText(Integer.toString(blueLumaEndSlider
						.getValue()));
			}
		});

		blueLumaEndLabel = new JLabel();
		blueLumaEndLabel
				.setText(Integer.toString(blueLumaEndSlider.getValue()));
		blueLumaEndLabel.setBounds(column2X - 50, row2Y + 20, 50, 25);

		blueChromaStartSlider = new JSlider(JSlider.HORIZONTAL,
				MIN_OTHER_VALUE, MAX_OTHER_VALUE, GlobalInfo
						.getColourSettings().getBlueChromaStart());
		blueChromaStartSlider.setBounds(column3X, row2Y, 200, 25);
		blueChromaStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = blueChromaStartSlider.getValue();
				GlobalInfo.getColourSettings().setBlueChromaStart(value);
				System.out.println(value);
				blueChromaStartLabel.setText(Integer
						.toString(blueChromaStartSlider.getValue()));
			}
		});

		blueChromaStartLabel = new JLabel();
		blueChromaStartLabel.setText(Integer.toString(blueChromaStartSlider
				.getValue()));
		blueChromaStartLabel.setBounds(column3X - 50, row2Y, 50, 25);

		blueChromaEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getBlueChromaEnd());
		blueChromaEndSlider.setBounds(column3X, row2Y + 20, 200, 25);
		blueChromaEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = blueChromaEndSlider.getValue();
				GlobalInfo.getColourSettings().setBlueChromaEnd(value);
				System.out.println(value);
				blueChromaEndLabel.setText(Integer.toString(blueChromaEndSlider
						.getValue()));
			}
		});

		blueChromaEndLabel = new JLabel();
		blueChromaEndLabel.setText(Integer.toString(blueChromaEndSlider
				.getValue()));
		blueChromaEndLabel.setBounds(column3X - 50, row2Y + 20, 50, 25);

		// BALL RED SLIDERS
		redHueStartSlider = new JSlider(JSlider.HORIZONTAL, MIN_HUE_VALUE,
				MAX_HUE_VALUE, GlobalInfo.getColourSettings().getRedHueStart());
		redHueStartSlider.setBounds(column1X, row3Y, 200, 25);
		redHueStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = redHueStartSlider.getValue();
				GlobalInfo.getColourSettings().setRedHueStart(value);
				System.out.println(value);
				redHueStartLabel.setText(Integer.toString(redHueStartSlider
						.getValue()));
			}
		});

		redHueStartLabel = new JLabel();
		redHueStartLabel
				.setText(Integer.toString(redHueStartSlider.getValue()));
		redHueStartLabel.setBounds(column1X - 50, row3Y, 50, 25);

		redHueEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_HUE_VALUE,
				MAX_HUE_VALUE, GlobalInfo.getColourSettings().getRedHueEnd());
		redHueEndSlider.setBounds(column1X, row3Y + 20, 200, 25);
		redHueEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = redHueEndSlider.getValue();
				GlobalInfo.getColourSettings().setRedHueEnd(value);
				System.out.println(value);
				redHueEndLabel.setText(Integer.toString(redHueEndSlider
						.getValue()));
			}
		});

		redHueEndLabel = new JLabel();
		redHueEndLabel.setText(Integer.toString(redHueEndSlider.getValue()));
		redHueEndLabel.setBounds(column1X - 50, row3Y + 20, 50, 25);

		redLumaStartSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getRedLumaStart());
		redLumaStartSlider.setBounds(column2X, row3Y, 200, 25);
		redLumaStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = redLumaStartSlider.getValue();
				GlobalInfo.getColourSettings().setRedLumaStart(value);
				System.out.println(value);
				redLumaStartLabel.setText(Integer.toString(redLumaStartSlider
						.getValue()));
			}
		});

		redLumaStartLabel = new JLabel();
		redLumaStartLabel.setText(Integer.toString(redLumaStartSlider
				.getValue()));
		redLumaStartLabel.setBounds(column2X - 50, row3Y, 50, 25);

		redLumaEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings().getRedLumaEnd());
		redLumaEndSlider.setBounds(column2X, row3Y + 20, 200, 25);
		redLumaEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = redLumaEndSlider.getValue();
				GlobalInfo.getColourSettings().setRedLumaEnd(value);
				System.out.println(value);
				redLumaEndLabel.setText(Integer.toString(redLumaEndSlider
						.getValue()));
			}
		});

		redLumaEndLabel = new JLabel();
		redLumaEndLabel.setText(Integer.toString(redLumaEndSlider.getValue()));
		redLumaEndLabel.setBounds(column2X - 50, row3Y + 20, 50, 25);

		redChromaStartSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getRedChromaStart());
		redChromaStartSlider.setBounds(column3X, row3Y, 200, 25);
		redChromaStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = redChromaStartSlider.getValue();
				GlobalInfo.getColourSettings().setRedChromaStart(value);
				System.out.println(value);
				redChromaStartLabel.setText(Integer
						.toString(redChromaStartSlider.getValue()));
			}
		});

		redChromaStartLabel = new JLabel();
		redChromaStartLabel.setText(Integer.toString(redChromaStartSlider
				.getValue()));
		redChromaStartLabel.setBounds(column3X - 50, row3Y, 50, 25);

		redChromaEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getRedChromaEnd());
		redChromaEndSlider.setBounds(column3X, row3Y + 20, 200, 25);
		redChromaEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = redChromaEndSlider.getValue();
				GlobalInfo.getColourSettings().setRedChromaEnd(value);
				System.out.println(value);
				redChromaEndLabel.setText(Integer.toString(redChromaEndSlider
						.getValue()));
			}
		});

		redChromaEndLabel = new JLabel();
		redChromaEndLabel.setText(Integer.toString(redChromaEndSlider
				.getValue()));
		redChromaEndLabel.setBounds(column3X - 50, row3Y + 20, 50, 25);

		// PLATE GREEN SLIDERS
		plateHueStartSlider = new JSlider(JSlider.HORIZONTAL, MIN_HUE_VALUE,
				MAX_HUE_VALUE, GlobalInfo.getColourSettings()
						.getPlateHueStart());
		plateHueStartSlider.setBounds(column1X, row4Y, 200, 25);
		plateHueStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = plateHueStartSlider.getValue();
				GlobalInfo.getColourSettings().setPlateHueStart(value);
				System.out.println(value);
				plateHueStartLabel.setText(Integer.toString(plateHueStartSlider
						.getValue()));
			}
		});

		plateHueStartLabel = new JLabel();
		plateHueStartLabel.setText(Integer.toString(plateHueStartSlider
				.getValue()));
		plateHueStartLabel.setBounds(column1X - 50, row4Y, 50, 25);

		plateHueEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_HUE_VALUE,
				MAX_HUE_VALUE, GlobalInfo.getColourSettings().getPlateHueEnd());
		plateHueEndSlider.setBounds(column1X, row4Y + 20, 200, 25);
		plateHueEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = plateHueEndSlider.getValue();
				GlobalInfo.getColourSettings().setPlateHueEnd(value);
				System.out.println(value);
				plateHueEndLabel.setText(Integer.toString(plateHueEndSlider
						.getValue()));
			}
		});

		plateHueEndLabel = new JLabel();
		plateHueEndLabel
				.setText(Integer.toString(plateHueEndSlider.getValue()));
		plateHueEndLabel.setBounds(column1X - 50, row4Y + 20, 50, 25);

		plateLumaStartSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getPlateLumaStart());
		plateLumaStartSlider.setBounds(column2X, row4Y, 200, 25);
		plateLumaStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = plateLumaStartSlider.getValue();
				GlobalInfo.getColourSettings().setPlateLumaStart(value);
				System.out.println(value);
				plateLumaStartLabel.setText(Integer
						.toString(plateLumaStartSlider.getValue()));
			}
		});

		plateLumaStartLabel = new JLabel();
		plateLumaStartLabel.setText(Integer.toString(plateLumaStartSlider
				.getValue()));
		plateLumaStartLabel.setBounds(column2X - 50, row4Y, 50, 25);

		plateLumaEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getPlateLumaEnd());
		plateLumaEndSlider.setBounds(column2X, row4Y + 20, 200, 25);
		plateLumaEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = plateLumaEndSlider.getValue();
				GlobalInfo.getColourSettings().setPlateLumaEnd(value);
				System.out.println(value);
				plateLumaEndLabel.setText(Integer.toString(plateLumaEndSlider
						.getValue()));
			}
		});

		plateLumaEndLabel = new JLabel();
		plateLumaEndLabel.setText(Integer.toString(plateLumaEndSlider
				.getValue()));
		plateLumaEndLabel.setBounds(column2X - 50, row4Y + 20, 50, 25);

		plateChromaStartSlider = new JSlider(JSlider.HORIZONTAL,
				MIN_OTHER_VALUE, MAX_OTHER_VALUE, GlobalInfo
						.getColourSettings().getPlateChromaStart());
		plateChromaStartSlider.setBounds(column3X, row4Y, 200, 25);
		plateChromaStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = plateChromaStartSlider.getValue();
				GlobalInfo.getColourSettings().setPlateChromaStart(value);
				System.out.println(value);
				plateChromaStartLabel.setText(Integer
						.toString(plateChromaStartSlider.getValue()));
			}
		});

		plateChromaStartLabel = new JLabel();
		plateChromaStartLabel.setText(Integer.toString(plateChromaStartSlider
				.getValue()));
		plateChromaStartLabel.setBounds(column3X - 50, row4Y, 50, 25);

		plateChromaEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getPlateChromaEnd());
		plateChromaEndSlider.setBounds(column3X, row4Y + 20, 200, 25);
		plateChromaEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = plateChromaEndSlider.getValue();
				GlobalInfo.getColourSettings().setPlateChromaEnd(value);
				System.out.println(value);
				plateChromaEndLabel.setText(Integer
						.toString(plateChromaEndSlider.getValue()));
			}
		});

		plateChromaEndLabel = new JLabel();
		plateChromaEndLabel.setText(Integer.toString(plateChromaEndSlider
				.getValue()));
		plateChromaEndLabel.setBounds(column3X - 50, row4Y + 20, 50, 25);

		// PITCH GREEN SLIDERS
		pitchHueStartSlider = new JSlider(JSlider.HORIZONTAL, MIN_HUE_VALUE,
				MAX_HUE_VALUE, GlobalInfo.getColourSettings()
						.getPitchHueStart());
		pitchHueStartSlider.setBounds(column1X, row5Y, 200, 25);
		pitchHueStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = pitchHueStartSlider.getValue();
				GlobalInfo.getColourSettings().setPitchHueStart(value);
				System.out.println(value);
				pitchHueStartLabel.setText(Integer.toString(pitchHueStartSlider
						.getValue()));
			}
		});

		pitchHueStartLabel = new JLabel();
		pitchHueStartLabel.setText(Integer.toString(pitchHueStartSlider
				.getValue()));
		pitchHueStartLabel.setBounds(column1X - 50, row5Y, 50, 25);

		pitchHueEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_HUE_VALUE,
				MAX_HUE_VALUE, GlobalInfo.getColourSettings().getPitchHueEnd());
		pitchHueEndSlider.setBounds(column1X, row5Y + 20, 200, 25);
		pitchHueEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = pitchHueEndSlider.getValue();
				GlobalInfo.getColourSettings().setPitchHueEnd(value);
				System.out.println(value);
				pitchHueEndLabel.setText(Integer.toString(pitchHueEndSlider
						.getValue()));
			}
		});

		pitchHueEndLabel = new JLabel();
		pitchHueEndLabel
				.setText(Integer.toString(pitchHueEndSlider.getValue()));
		pitchHueEndLabel.setBounds(column1X - 50, row5Y + 20, 50, 25);

		pitchLumaStartSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getPitchLumaStart());
		pitchLumaStartSlider.setBounds(column2X, row5Y, 200, 25);
		pitchLumaStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = pitchLumaStartSlider.getValue();
				GlobalInfo.getColourSettings().setPitchLumaStart(value);
				System.out.println(value);
				pitchLumaStartLabel.setText(Integer
						.toString(pitchLumaStartSlider.getValue()));
			}
		});

		pitchLumaStartLabel = new JLabel();
		pitchLumaStartLabel.setText(Integer.toString(pitchLumaStartSlider
				.getValue()));
		pitchLumaStartLabel.setBounds(column2X - 50, row5Y, 50, 25);

		pitchLumaEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getPitchLumaEnd());
		pitchLumaEndSlider.setBounds(column2X, row5Y + 20, 200, 25);
		pitchLumaEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = pitchLumaEndSlider.getValue();
				GlobalInfo.getColourSettings().setPitchLumaEnd(value);
				System.out.println(value);
				pitchLumaEndLabel.setText(Integer.toString(pitchLumaEndSlider
						.getValue()));
			}
		});

		pitchLumaEndLabel = new JLabel();
		pitchLumaEndLabel.setText(Integer.toString(pitchLumaEndSlider
				.getValue()));
		pitchLumaEndLabel.setBounds(column2X - 50, row5Y + 20, 50, 25);

		pitchChromaStartSlider = new JSlider(JSlider.HORIZONTAL,
				MIN_OTHER_VALUE, MAX_OTHER_VALUE, GlobalInfo
						.getColourSettings().getPitchChromaStart());
		pitchChromaStartSlider.setBounds(column3X, row5Y, 200, 25);
		pitchChromaStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = pitchChromaStartSlider.getValue();
				GlobalInfo.getColourSettings().setPitchChromaStart(value);
				System.out.println(value);
				pitchChromaStartLabel.setText(Integer
						.toString(pitchChromaStartSlider.getValue()));
			}
		});

		pitchChromaStartLabel = new JLabel();
		pitchChromaStartLabel.setText(Integer.toString(pitchChromaStartSlider
				.getValue()));
		pitchChromaStartLabel.setBounds(column3X - 50, row5Y, 50, 25);

		pitchChromaEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getPitchChromaEnd());
		pitchChromaEndSlider.setBounds(column3X, row5Y + 20, 200, 25);
		pitchChromaEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = pitchChromaEndSlider.getValue();
				GlobalInfo.getColourSettings().setPitchChromaEnd(value);
				System.out.println(value);
				pitchChromaEndLabel.setText(Integer
						.toString(pitchChromaEndSlider.getValue()));
			}
		});

		pitchChromaEndLabel = new JLabel();
		pitchChromaEndLabel.setText(Integer.toString(pitchChromaEndSlider
				.getValue()));
		pitchChromaEndLabel.setBounds(column3X - 50, row5Y + 20, 50, 25);

		// Gray (walls etc.) Sliders
		grayHueStartSlider = new JSlider(JSlider.HORIZONTAL, MIN_HUE_VALUE,
				MAX_HUE_VALUE, GlobalInfo.getColourSettings().getGrayHueStart());
		grayHueStartSlider.setBounds(column1X, row6Y, 200, 25);
		grayHueStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = grayHueStartSlider.getValue();
				GlobalInfo.getColourSettings().setGrayHueStart(value);
				System.out.println(value);
				grayHueStartLabel.setText(Integer.toString(grayHueStartSlider
						.getValue()));
			}
		});

		grayHueStartLabel = new JLabel();
		grayHueStartLabel.setText(Integer.toString(grayHueStartSlider
				.getValue()));
		grayHueStartLabel.setBounds(column1X - 50, row6Y, 50, 25);

		grayHueEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_HUE_VALUE,
				MAX_HUE_VALUE, GlobalInfo.getColourSettings().getGrayHueEnd());
		grayHueEndSlider.setBounds(column1X, row6Y + 20, 200, 25);
		grayHueEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = grayHueEndSlider.getValue();
				GlobalInfo.getColourSettings().setGrayHueEnd(value);
				System.out.println(value);
				grayHueEndLabel.setText(Integer.toString(grayHueEndSlider
						.getValue()));
			}
		});

		grayHueEndLabel = new JLabel();
		grayHueEndLabel.setText(Integer.toString(grayHueEndSlider.getValue()));
		grayHueEndLabel.setBounds(column1X - 50, row6Y + 20, 50, 25);

		grayLumaStartSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getGrayLumaStart());
		grayLumaStartSlider.setBounds(column2X, row6Y, 200, 25);
		grayLumaStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = grayLumaStartSlider.getValue();
				GlobalInfo.getColourSettings().setGrayLumaStart(value);
				System.out.println(value);
				grayLumaStartLabel.setText(Integer.toString(grayLumaStartSlider
						.getValue()));
			}
		});

		grayLumaStartLabel = new JLabel();
		grayLumaStartLabel.setText(Integer.toString(grayLumaStartSlider
				.getValue()));
		grayLumaStartLabel.setBounds(column2X - 50, row6Y, 50, 25);

		grayLumaEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getGrayLumaEnd());
		grayLumaEndSlider.setBounds(column2X, row6Y + 20, 200, 25);
		grayLumaEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = grayLumaEndSlider.getValue();
				GlobalInfo.getColourSettings().setGrayLumaEnd(value);
				System.out.println(value);
				grayLumaEndLabel.setText(Integer.toString(grayLumaEndSlider
						.getValue()));
			}
		});

		grayLumaEndLabel = new JLabel();
		grayLumaEndLabel
				.setText(Integer.toString(grayLumaEndSlider.getValue()));
		grayLumaEndLabel.setBounds(column2X - 50, row6Y + 20, 50, 25);

		grayChromaStartSlider = new JSlider(JSlider.HORIZONTAL,
				MIN_OTHER_VALUE, MAX_OTHER_VALUE, GlobalInfo
						.getColourSettings().getGrayChromaStart());
		grayChromaStartSlider.setBounds(column3X, row6Y, 200, 25);
		grayChromaStartSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = grayChromaStartSlider.getValue();
				GlobalInfo.getColourSettings().setGrayChromaStart(value);
				System.out.println(value);
				grayChromaStartLabel.setText(Integer
						.toString(grayChromaStartSlider.getValue()));
			}
		});

		grayChromaStartLabel = new JLabel();
		grayChromaStartLabel.setText(Integer.toString(grayChromaStartSlider
				.getValue()));
		grayChromaStartLabel.setBounds(column3X - 50, row6Y, 50, 25);

		grayChromaEndSlider = new JSlider(JSlider.HORIZONTAL, MIN_OTHER_VALUE,
				MAX_OTHER_VALUE, GlobalInfo.getColourSettings()
						.getGrayChromaEnd());
		grayChromaEndSlider.setBounds(column3X, row6Y + 20, 200, 25);
		grayChromaEndSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				value = grayChromaEndSlider.getValue();
				GlobalInfo.getColourSettings().setGrayChromaEnd(value);
				System.out.println(value);
				grayChromaEndLabel.setText(Integer.toString(grayChromaEndSlider
						.getValue()));
			}
		});

		grayChromaEndLabel = new JLabel();
		grayChromaEndLabel.setText(Integer.toString(grayChromaEndSlider
				.getValue()));
		grayChromaEndLabel.setBounds(column3X - 50, row6Y + 20, 50, 25);

		// END OF SLIDERS
		/*
		 * plateHueStartSlider = new JSlider(JSlider.HORIZONTAL, MIN_HUE_VALUE,
		 * MAX_HUE_VALUE,
		 * globalInfo.getCamera().getColourSettings().getPlateHueStart());
		 * blueToRedHueSlider.setBounds(42, 12, 200, 25);
		 * blueToRedHueSlider.addChangeListener(new ChangeListener() {
		 * 
		 * @Override public void stateChanged(ChangeEvent arg0) { blueToRedHue =
		 * blueToRedHueSlider.getValue(); GlobalInfo.getColourSettings()
		 * .setBlueToRedHue(blueToRedHue + 360); System.out.println(blueToRedHue
		 * + 360); } });
		 */

		// yellowTSliderHue = new JSlider(JSlider.HORIZONTAL, minColourValue,
		// maxColourValue, );

		/*
		 * blueToRedHueLabel = new JLabel(); blueToRedHueLabel.setText("B/R");
		 * blueToRedHueLabel.setBounds(12, 12, 30, 25);
		 * 
		 * blueToRedHueSlider = new JSlider(JSlider.HORIZONTAL, MIN_BR_HUE,
		 * MAX_BR_HUE, blueToRedHue); blueToRedHueSlider.setBounds(42, 12, 200,
		 * 25); blueToRedHueSlider.addChangeListener(new ChangeListener() {
		 * 
		 * @Override public void stateChanged(ChangeEvent arg0) { blueToRedHue =
		 * blueToRedHueSlider.getValue(); GlobalInfo.getColourSettings()
		 * .setBlueToRedHue(blueToRedHue + 360); System.out.println(blueToRedHue
		 * + 360); } }); blueToRedHueSlider.setMajorTickSpacing(20);
		 * blueToRedHueSlider.setMinorTickSpacing(5);
		 * blueToRedHueSlider.setPaintTicks(true);
		 * 
		 * redToYellowHueLabel = new JLabel();
		 * redToYellowHueLabel.setText("R/Y"); redToYellowHueLabel.setBounds(12,
		 * 61, 30, 25);
		 * 
		 * redToYellowHueSlider = new JSlider(JSlider.HORIZONTAL, MIN_RY_HUE,
		 * MAX_RY_HUE, redToYellowHue); redToYellowHueSlider.setBounds(42, 61,
		 * 200, 25); redToYellowHueSlider.addChangeListener(new ChangeListener()
		 * {
		 * 
		 * @Override public void stateChanged(ChangeEvent arg0) { redToYellowHue
		 * = redToYellowHueSlider.getValue();
		 * globalInfo.getColourSettings().setRedToYellowHue(redToYellowHue); }
		 * }); redToYellowHueSlider.setMajorTickSpacing(20);
		 * redToYellowHueSlider.setMinorTickSpacing(5);
		 * redToYellowHueSlider.setPaintTicks(true);
		 * 
		 * 
		 * yellowToGreenHueLabel = new JLabel();
		 * yellowToGreenHueLabel.setText("Y/G");
		 * yellowToGreenHueLabel.setBounds(12, 110, 30, 25);
		 * 
		 * yellowToGreenHueSlider = new JSlider(JSlider.HORIZONTAL, MIN_YG_HUE,
		 * MAX_YG_HUE, yellowToGreenHue); yellowToGreenHueSlider.setBounds(42,
		 * 110, 200, 25); yellowToGreenHueSlider.addChangeListener(new
		 * ChangeListener() {
		 * 
		 * @Override public void stateChanged(ChangeEvent arg0) {
		 * yellowToGreenHue = yellowToGreenHueSlider.getValue();
		 * globalInfo.getColourSettings().setYellowToGreenHue(yellowToGreenHue);
		 * } }); yellowToGreenHueSlider.setMajorTickSpacing(20);
		 * yellowToGreenHueSlider.setMinorTickSpacing(5);
		 * yellowToGreenHueSlider.setPaintTicks(true);
		 * 
		 * 
		 * greenToBlueHueLabel = new JLabel();
		 * greenToBlueHueLabel.setText("G/B"); greenToBlueHueLabel.setBounds(12,
		 * 159, 30, 25);
		 * 
		 * greenToBlueHueSlider = new JSlider(JSlider.HORIZONTAL, MIN_GB_HUE,
		 * MAX_GB_HUE, greenToBlueHue); greenToBlueHueSlider.setBounds(42, 159,
		 * 200, 25); greenToBlueHueSlider.addChangeListener(new ChangeListener()
		 * {
		 * 
		 * @Override public void stateChanged(ChangeEvent arg0) { greenToBlueHue
		 * = greenToBlueHueSlider.getValue();
		 * globalInfo.getColourSettings().setGreenToBlueHue(greenToBlueHue); }
		 * }); greenToBlueHueSlider.setMajorTickSpacing(20);
		 * greenToBlueHueSlider.setMinorTickSpacing(5);
		 * greenToBlueHueSlider.setPaintTicks(true);
		 */

		matchVisionModeButton = new Button();
		matchVisionModeButton.setLabel("Match");
		matchVisionModeButton.setBounds(1090, 160, 150, 25);
		matchVisionModeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				processor.setCurrentMode(OutputMode.MATCH);
			}
		});

		chromaVisionModeButton = new Button();
		chromaVisionModeButton.setLabel("Chroma");
		chromaVisionModeButton.setBounds(880, 15, 65, 25);
		chromaVisionModeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				processor.setCurrentMode(OutputMode.CHROMA);
			}
		});

		lumaVisionModeButton = new Button();
		lumaVisionModeButton.setLabel("Luma");
		lumaVisionModeButton.setBounds(580, 15, 65, 25);

		lumaVisionModeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				processor.setCurrentMode(OutputMode.LUMA);
			}
		});

		grabImageButton = new Button();
		grabImageButton.setLabel("Grab background");
		grabImageButton.setBounds(1090, 200, 150, 25);
		grabImageButton.setBackground(Color.green);
		grabImageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				processor.grabNewBackgroundImage();
			}
		});

		hueVisionModeButton = new Button();
		hueVisionModeButton.setLabel("Hue");
		hueVisionModeButton.setBounds(280, 15, 65, 25);

		// getContentPane().add(blueToRedHueLabel);
		// getContentPane().add(redToYellowHueLabel);
		// getContentPane().add(yellowToGreenHueLabel);
		// getContentPane().add(greenToBlueHueLabel);
		//
		// getContentPane().add(hueLabel);
		// getContentPane().add(chromeLabel);
		// getContentPane().add(lumaLabel);

		getContentPane().add(yellowTLabel);
		getContentPane().add(blueTLabel);
		getContentPane().add(ballLabel);
		getContentPane().add(plateGreenLabel);
		getContentPane().add(greyLabel);
		getContentPane().add(pitchGreenLabel);

		// getContentPane().add(blueToRedHueSlider);
		// getContentPane().add(redToYellowHueSlider);
		// getContentPane().add(yellowToGreenHueSlider);
		// getContentPane().add(greenToBlueHueSlider);

		getContentPane().add(matchVisionModeButton);
		getContentPane().add(chromaVisionModeButton);
		getContentPane().add(lumaVisionModeButton);
		getContentPane().add(hueVisionModeButton);

		// adding the sliders

		getContentPane().add(yellowHueStartSlider);
		getContentPane().add(yellowHueEndSlider);
		getContentPane().add(yellowLumaStartSlider);
		getContentPane().add(yellowLumaEndSlider);
		getContentPane().add(yellowChromaStartSlider);
		getContentPane().add(yellowChromaEndSlider);

		getContentPane().add(blueHueStartSlider);
		getContentPane().add(blueHueEndSlider);
		getContentPane().add(blueLumaStartSlider);
		getContentPane().add(blueLumaEndSlider);
		getContentPane().add(blueChromaStartSlider);
		getContentPane().add(blueChromaEndSlider);

		getContentPane().add(redHueStartSlider);
		getContentPane().add(redHueEndSlider);
		getContentPane().add(redLumaStartSlider);
		getContentPane().add(redLumaEndSlider);
		getContentPane().add(redChromaStartSlider);
		getContentPane().add(redChromaEndSlider);

		getContentPane().add(plateHueStartSlider);
		getContentPane().add(plateHueEndSlider);
		getContentPane().add(plateLumaStartSlider);
		getContentPane().add(plateLumaEndSlider);
		getContentPane().add(plateChromaStartSlider);
		getContentPane().add(plateChromaEndSlider);

		getContentPane().add(pitchHueStartSlider);
		getContentPane().add(pitchHueEndSlider);
		getContentPane().add(pitchLumaStartSlider);
		getContentPane().add(pitchLumaEndSlider);
		getContentPane().add(pitchChromaStartSlider);
		getContentPane().add(pitchChromaEndSlider);

		getContentPane().add(grayHueStartSlider);
		getContentPane().add(grayHueEndSlider);
		getContentPane().add(grayLumaStartSlider);
		getContentPane().add(grayLumaEndSlider);
		getContentPane().add(grayChromaStartSlider);
		getContentPane().add(grayChromaEndSlider);

		getContentPane().add(yellowHueStartLabel);
		getContentPane().add(yellowHueEndLabel);
		getContentPane().add(yellowLumaStartLabel);
		getContentPane().add(yellowLumaEndLabel);
		getContentPane().add(yellowChromaStartLabel);
		getContentPane().add(yellowChromaEndLabel);

		getContentPane().add(blueHueStartLabel);
		getContentPane().add(blueHueEndLabel);
		getContentPane().add(blueLumaStartLabel);
		getContentPane().add(blueLumaEndLabel);
		getContentPane().add(blueChromaStartLabel);
		getContentPane().add(blueChromaEndLabel);

		getContentPane().add(redHueStartLabel);
		getContentPane().add(redHueEndLabel);
		getContentPane().add(redLumaStartLabel);
		getContentPane().add(redLumaEndLabel);
		getContentPane().add(redChromaStartLabel);
		getContentPane().add(redChromaEndLabel);

		getContentPane().add(plateHueStartLabel);
		getContentPane().add(plateHueEndLabel);
		getContentPane().add(plateLumaStartLabel);
		getContentPane().add(plateLumaEndLabel);
		getContentPane().add(plateChromaStartLabel);
		getContentPane().add(plateChromaEndLabel);

		getContentPane().add(pitchHueStartLabel);
		getContentPane().add(pitchHueEndLabel);
		getContentPane().add(pitchLumaStartLabel);
		getContentPane().add(pitchLumaEndLabel);
		getContentPane().add(pitchChromaStartLabel);
		getContentPane().add(pitchChromaEndLabel);

		getContentPane().add(grayHueStartLabel);
		getContentPane().add(grayHueEndLabel);
		getContentPane().add(grayLumaStartLabel);
		getContentPane().add(grayLumaEndLabel);
		getContentPane().add(grayChromaStartLabel);
		getContentPane().add(grayChromaEndLabel);

		getContentPane().add(grabImageButton);
		// Funny...
		getContentPane().add(new Panel());
	}

}
