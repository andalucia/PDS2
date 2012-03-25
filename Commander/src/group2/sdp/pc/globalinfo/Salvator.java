package group2.sdp.pc.globalinfo;

import group2.sdp.pc.controlstation.ControlStation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Saves and loads the LCH Colour Settings.
 */
public class Salvator {
	
	/**
	 * The maximum size of the buffer for reading the settings file.
	 */
	private static final int MAX_BUFFER = 1024;
	
	/**
	 * The name of the file containing the settings for pitch one.
	 */
	private static final String pitchOneSettingsFileName = "settings/clrset1.txt";
	
	/**
	 * The name of the file containing the settings for pitch two.
	 */
	private static final String pitchTwoSettingsFileName = "settings/clrset2.txt";
	
	
	/**
	 * Saves the current settings in the GlobalInfo. 
	 */
	public static void saveLCHSettings() {
		String fileName = getCurrentFileName();
		
		String settings = turnSettingsIntoString();
		FileWriter fout;
		try {
			fout = new FileWriter(new File(fileName));
			fout.write(settings);
			fout.close();
		} catch (IOException e) {
			ControlStation.log("ERROR: Could not save settings.");
		}
	}

	/**
	 * Loads the current settings to the GlobalInfo.
	 */
	public static void loadLCHSettings() {
		String fileName = getCurrentFileName();
		
		try {
			char [] buffer = new char[MAX_BUFFER];
			FileReader fin;
			fin = new FileReader(new File(fileName));
			fin.read(buffer);
			fin.close();
			turnStringIntoSettings(new String(buffer));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * If the current pitch in the GlobalInfo is Pitch.ONE, the filename is 
	 * pitchOneSettingsFileName; if it is Pitch.TWO, the filename is 
	 * pitchTwoSettingsFileName; else it is "clrset0.txt".
	 * @return 
	 */
	private static String getCurrentFileName() {
		String fileName = "settings/clrset0.txt";
		if (GlobalInfo.getPitch().equals(Pitch.ONE)) {
			fileName = pitchOneSettingsFileName;
		}
		if (GlobalInfo.getPitch().equals(Pitch.TWO)) {
			fileName = pitchTwoSettingsFileName;
		}
		return fileName;
	}

	/**
	 * Converts the current LCH settings to a string.
	 * @return The string representing the current LCH settings.
	 */
	private static String turnSettingsIntoString() {
		StringBuilder builder = new StringBuilder();
		char separator = ',';
		
		builder.append(GlobalInfo.getColourSettings().getPlateHueStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getPlateHueEnd());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getPlateLumaStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getPlateLumaEnd());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getPlateChromaStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getPlateChromaEnd());
		builder.append(separator);

		builder.append(GlobalInfo.getColourSettings().getPitchHueStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getPitchHueEnd());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getPitchLumaStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getPitchLumaEnd());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getPitchChromaStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getPitchChromaEnd());
		builder.append(separator);
			
		builder.append(GlobalInfo.getColourSettings().getBlueHueStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getBlueHueEnd());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getBlueLumaStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getBlueLumaEnd());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getBlueChromaStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getBlueChromaEnd());
		builder.append(separator);

		builder.append(GlobalInfo.getColourSettings().getRedHueStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getRedHueEnd());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getRedLumaStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getRedLumaEnd());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getRedChromaStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getRedChromaEnd());
		builder.append(separator);
			
		builder.append(GlobalInfo.getColourSettings().getYellowHueStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getYellowHueEnd());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getYellowLumaStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getYellowLumaEnd());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getYellowChromaStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getYellowChromaEnd());
		builder.append(separator);

		builder.append(GlobalInfo.getColourSettings().getGrayHueStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getGrayHueEnd());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getGrayLumaStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getGrayLumaEnd());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getGrayChromaStart());
		builder.append(separator);
		builder.append(GlobalInfo.getColourSettings().getGrayChromaEnd());
		builder.append(separator); // needed, do not remove
		
		return builder.toString();
	}

	/**
	 * Sets the LCH colour settings in the GlobalInfo to the given string.
	 * @param string The string to get the settings from.
	 */
	private static void turnStringIntoSettings(String string) {
		String[] split = string.split(",");
		int i = 0;
		
		GlobalInfo.getColourSettings().setPlateHueStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setPlateHueEnd(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setPlateLumaStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setPlateLumaEnd(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setPlateChromaStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setPlateChromaEnd(Integer.parseInt(split[i++]));

		GlobalInfo.getColourSettings().setPitchHueStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setPitchHueEnd(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setPitchLumaStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setPitchLumaEnd(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setPitchChromaStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setPitchChromaEnd(Integer.parseInt(split[i++]));
			
		GlobalInfo.getColourSettings().setBlueHueStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setBlueHueEnd(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setBlueLumaStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setBlueLumaEnd(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setBlueChromaStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setBlueChromaEnd(Integer.parseInt(split[i++]));
			
		GlobalInfo.getColourSettings().setRedHueStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setRedHueEnd(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setRedLumaStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setRedLumaEnd(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setRedChromaStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setRedChromaEnd(Integer.parseInt(split[i++]));
			
		GlobalInfo.getColourSettings().setYellowHueStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setYellowHueEnd(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setYellowLumaStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setYellowLumaEnd(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setYellowChromaStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setYellowChromaEnd(Integer.parseInt(split[i++]));
			
		GlobalInfo.getColourSettings().setGrayHueStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setGrayHueEnd(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setGrayLumaStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setGrayLumaEnd(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setGrayChromaStart(Integer.parseInt(split[i++]));
		GlobalInfo.getColourSettings().setGrayChromaEnd(Integer.parseInt(split[i++]));
	}
}
