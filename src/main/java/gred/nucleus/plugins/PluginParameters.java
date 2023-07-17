package gred.nucleus.plugins;

import gred.nucleus.files.Directory;
import ij.IJ;
import ij.ImagePlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;


public class PluginParameters {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	/** Activation of manual calibration parameter */
	public boolean manualParameter = false;
	/** X calibration plugin parameter */
	public double  xCal            = 1;
	/** y calibration plugin parameter */
	public double  yCal            = 1;
	/** z calibration plugin parameter */
	public double  zCal            = 1;
	/** Input folder */
	public String  inputFolder;
	/** Output folder */
	public String  outputFolder;
	/** Autocrop parameters information */
	public String  headerInfo;
	
	
	/** Constructor with default parameter */
	public PluginParameters() {
	}
	
	
	/**
	 * Constructor with default parameter
	 *
	 * @param inputFolder  Path folder containing Images
	 * @param outputFolder Path folder output analyse
	 */
	public PluginParameters(String inputFolder, String outputFolder) {
		checkInputPaths(inputFolder, outputFolder);
		Directory dirOutput = new Directory(outputFolder);
		dirOutput.checkAndCreateDir();
		this.outputFolder = dirOutput.getDirPath();
		
		
	}
	
	
	/**
	 * Constructor with specific calibration in x y and z
	 *
	 * @param inputFolder  Path folder containing Images
	 * @param outputFolder Path folder output analyse
	 * @param xCal         x calibration voxel
	 * @param yCal         Y calibration voxel
	 * @param zCal         Z calibration voxel
	 */
	public PluginParameters(String inputFolder, String outputFolder, double xCal, double yCal, double zCal) {
		checkInputPaths(inputFolder, outputFolder);
		Directory dirOutput = new Directory(outputFolder);
		dirOutput.checkAndCreateDir();
		this.outputFolder = dirOutput.getDirPath();
		this.manualParameter = true;
		this.xCal = xCal;
		this.yCal = yCal;
		this.zCal = zCal;
		
	}
	
	
	/**
	 * Constructor using input , output folders and config file (for command line execution)
	 *
	 * @param inputFolder      Path folder containing Images
	 * @param outputFolder     Path folder output analyse
	 * @param pathToConfigFile Path to the config file
	 */
	public PluginParameters(String inputFolder, String outputFolder, String pathToConfigFile) {
		checkInputPaths(inputFolder, outputFolder);
		Directory dirOutput = new Directory(outputFolder);
		dirOutput.checkAndCreateDir();
		this.outputFolder = dirOutput.getDirPath();
		addGeneralProperties(pathToConfigFile);
		
	}
	
	
	public void addGeneralProperties(String pathToConfigFile) {
		
		Properties prop = new Properties();
		try (InputStream is = new FileInputStream(pathToConfigFile)) {
			prop.load(is);
		} catch (FileNotFoundException ex) {
			LOGGER.error("{}: can't find the config file !", pathToConfigFile);
			System.exit(-1);
		} catch (IOException ex) {
			LOGGER.error("{}: can't load the config file !", pathToConfigFile);
			System.exit(-1);
		}
		for (String idProp : prop.stringPropertyNames()) {
			switch (idProp) {
				case "xCal":
					setXCal(Double.parseDouble(prop.getProperty("xCal")));
					break;
				case "yCal":
					setYCal(Double.parseDouble(prop.getProperty("yCal")));
					break;
				case "zCal":
					setZCal(Double.parseDouble(prop.getProperty("zCal")));
					break;
			}
		}
	}
	
	
	private void checkInputPaths(String inputFolder, String outputFolder) {
		
		File input = new File(inputFolder);
		if (input.isDirectory()) {
			this.inputFolder = inputFolder;
		} else if (input.isFile()) {
			this.inputFolder = input.getParent();
			
		} else {
			LOGGER.error("{}: can't find the input folder/file !", inputFolder);
			IJ.error(inputFolder + " : can't find the input folder/file !");
//            System.exit(-1);
		}
		if (outputFolder == null) {
			IJ.error("Output directory is missing");
			System.exit(-1);
		}
	}
	
	
	/**
	 * Getter : input path
	 *
	 * @return input path folder
	 */
	public String getInputFolder() {
		return this.inputFolder;
	}
	
	
	/**
	 * Getter : output path
	 *
	 * @return output path folder
	 */
	public String getOutputFolder() {
		return this.outputFolder;
	}
	
	
	/**
	 * Getter : HEADER parameter of the analysis containing path input output folder and x y z calibration on parameter
	 * per line
	 *
	 * @return output path folder
	 */
	public String getAnalysisParameters() {
		this.headerInfo = "#Header \n"
		                  + "#Star time analyse: " + getLocalTime() + "\n"
		                  + "#Input folder: " + this.inputFolder + "\n"
		                  + "#Output folder: " + this.outputFolder + "\n"
		                  + "#Calibration:" + getInfoCalibration() + "\n";
		return this.headerInfo;
		
	}
	
	
	/**
	 * Getter : image x y z calibration
	 *
	 * @return output path folder
	 */
	public String getInfoCalibration() {
		String parametersInfo;
		if (this.manualParameter) {
			parametersInfo = "x:" + this.xCal + "-y:" + this.yCal + "-z:" + this.zCal;
		} else {
			parametersInfo = "x:default-y:default-z:default";
		}
		return parametersInfo;
		
	}
	
	
	/**
	 * get local time start analyse information yyyy-MM-dd:HH-mm-ss format
	 *
	 * @return time in yyyy-MM-dd:HH-mm-ss format
	 */
	public String getLocalTime() {
		return new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
	}
	
	
	public double getVoxelVolume() {
		return this.xCal * this.yCal * this.zCal;
		
	}
	
	
	public double getXCal() {
		return this.xCal;
	}
	
	
	public void setXCal(double manualXCal) {
		this.xCal = manualXCal;
		this.manualParameter = true;
	}
	
	
	public double getYCal() {
		return this.yCal;
	}
	
	
	public void setYCal(double manualYCal) {
		this.yCal = manualYCal;
		this.manualParameter = true;
	}
	
	
	public double getZCal() {
		return this.zCal;
	}
	
	
	public void setZCal(double manualZCal) {
		this.zCal = manualZCal;
		this.manualParameter = true;
	}
	
	
	public boolean getManualParameter() {
		return this.manualParameter;
	}
	
	
	public double getXCalibration(ImagePlus raw) {
		double xCalibration;
		if (this.manualParameter) {
			xCalibration = this.xCal;
		} else {
			xCalibration = raw.getCalibration().pixelWidth;
		}
		return xCalibration;
	}
	
	
	public double getYCalibration(ImagePlus raw) {
		double yCalibration;
		if (this.manualParameter) {
			yCalibration = this.yCal;
		} else {
			yCalibration = raw.getCalibration().pixelHeight;
		}
		return yCalibration;
	}
	
	
	public double getZCalibration(ImagePlus raw) {
		double zCalibration;
		if (this.manualParameter) {
			zCalibration = this.zCal;
		} else {
			zCalibration = raw.getCalibration().pixelDepth;
		}
		return zCalibration;
	}
	
}
