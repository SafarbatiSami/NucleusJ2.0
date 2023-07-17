package gred.nucleus.autocrop;

import gred.nucleus.plugins.PluginParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Properties;


/** This class extend plugin parameters and contain the list of specific parameters available for Autocrop function. */
public class AutocropParameters extends PluginParameters {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	/** Minimal object volume to crop */
	int minVolumeNucleus = 1;
	/** Maximal object volume to crop */
	int maxVolumeNucleus = 2147483647;
	/** Number of pixels take plus object size in x */
	private int     xCropBoxSize                = 40;
	/** Number of pixels take plus object size in y */
	private int     yCropBoxSize                = 40;
	/** Number of slice take plus object in y */
	private int     zCropBoxSize                = 20;
	/** Font size of the box number */
	private int numberFontSize 					= 30;
	/** Minimal default OTSU threshold */
	private int     thresholdOTSUComputing      = 20;
	/** Channel to compute OTSU threshold */
	private int     channelToComputeThreshold   = 0;
	/** Slice start to compute OTSU threshold */
	private int     slicesOTSUComputing         = 0;
	/** Surface percent of boxes to groups them */
	private int     boxesPercentSurfaceToFilter = 50;
	/** Activation of boxes regrouping */
	private boolean boxesRegrouping             = true;
	
	
	public AutocropParameters() {
	}
	
	
	/**
	 * Constructor with default parameter
	 *
	 * @param inputFolder  Path folder containing Images
	 * @param outputFolder Path folder output analyse
	 */
	public AutocropParameters(String inputFolder, String outputFolder) {
		super(inputFolder, outputFolder);
	}
	
	
	/**
	 * Constructor with box size modifications
	 *
	 * @param inputFolder  Path folder containing Images
	 * @param outputFolder Path folder output analyse
	 * @param xCropBoxSize Number of voxels add in x axis around object
	 * @param yCropBoxSize Number of voxels add in z axis around object
	 * @param zCropBoxSize Number of stack add in z axis around object
	 */
	public AutocropParameters(String inputFolder, String outputFolder,
	                          int xCropBoxSize,
	                          int yCropBoxSize,
	                          int zCropBoxSize,
	                          int numberFontSize,
	                          int thresholdOTSUComputing,
	                          int channelToComputeThreshold) {
		super(inputFolder, outputFolder);
		this.xCropBoxSize = xCropBoxSize;
		this.yCropBoxSize = yCropBoxSize;
		this.zCropBoxSize = zCropBoxSize;
		this.numberFontSize = numberFontSize;
		this.thresholdOTSUComputing = thresholdOTSUComputing;
		this.channelToComputeThreshold = channelToComputeThreshold;
	}
	
	
	/**
	 * Constructor with all manual parameters
	 *
	 * @param inputFolder               Path folder containing Images
	 * @param outputFolder              Path folder output analyse
	 * @param xCropBoxSize              Number of voxels add in x axis around object
	 * @param yCropBoxSize              Number of voxels add in z axis around object
	 * @param zCropBoxSize              Number of stack add in z axis around object
	 * @param channelToComputeThreshold Channel number to compute OTSU
	 * @param slicesOTSUComputing       Slice start to compute OTSU
	 * @param thresholdOTSUComputing    Minimum OTSU threshold used
	 * @param maxVolumeNucleus          Volume maximum of objects detected
	 * @param minVolumeNucleus          Volume minimum of objects detected
	 */
	public AutocropParameters(String inputFolder, String outputFolder,
	                          int xCropBoxSize,
	                          int yCropBoxSize,
	                          int zCropBoxSize,
	                          int numberFontSize,
	                          int slicesOTSUComputing,
	                          int thresholdOTSUComputing,
	                          int channelToComputeThreshold,
	                          int minVolumeNucleus,
	                          int maxVolumeNucleus) {
		
		super(inputFolder, outputFolder);
		this.xCropBoxSize = xCropBoxSize;
		this.yCropBoxSize = yCropBoxSize;
		this.zCropBoxSize = zCropBoxSize;
		this.numberFontSize = numberFontSize;
		this.thresholdOTSUComputing = thresholdOTSUComputing;
		this.slicesOTSUComputing = slicesOTSUComputing;
		this.channelToComputeThreshold = channelToComputeThreshold;
		this.maxVolumeNucleus = maxVolumeNucleus;
		this.minVolumeNucleus = minVolumeNucleus;
	}
	
	
	/**
	 * Constructor with all manual parameters 2
	 *
	 * @param inputFolder                 Path folder containing Images
	 * @param outputFolder                Path folder output analyse
	 * @param xCropBoxSize                Number of voxels add in x axis around object
	 * @param yCropBoxSize                Number of voxels add in z axis around object
	 * @param zCropBoxSize                Number of stack add in z axis around object
	 * @param channelToComputeThreshold   Channel number to compute OTSU
	 * @param slicesOTSUComputing         Slice start to compute OTSU
	 * @param thresholdOTSUComputing      Minimum OTSU threshold used
	 * @param maxVolumeNucleus            Volume maximum of objects detected
	 * @param minVolumeNucleus            Volume minimum of objects detected
	 * @param boxesPercentSurfaceToFilter Surface percent of boxes to groups them
	 * @param boxesRegrouping             Activation of boxes regrouping
	 */
	public AutocropParameters(String inputFolder, String outputFolder,
	                          int xCropBoxSize,
	                          int yCropBoxSize,
	                          int zCropBoxSize,
	                          int numberFontSize,
	                          int slicesOTSUComputing,
	                          int thresholdOTSUComputing,
	                          int channelToComputeThreshold,
	                          int minVolumeNucleus,
	                          int maxVolumeNucleus,
	                          int boxesPercentSurfaceToFilter,
	                          boolean boxesRegrouping) {
		
		super(inputFolder, outputFolder);
		this.xCropBoxSize = xCropBoxSize;
		this.yCropBoxSize = yCropBoxSize;
		this.zCropBoxSize = zCropBoxSize;
		this.numberFontSize = numberFontSize;
		this.thresholdOTSUComputing = thresholdOTSUComputing;
		this.slicesOTSUComputing = slicesOTSUComputing;
		this.channelToComputeThreshold = channelToComputeThreshold;
		this.maxVolumeNucleus = maxVolumeNucleus;
		this.minVolumeNucleus = minVolumeNucleus;
		this.boxesRegrouping = boxesRegrouping;
		this.boxesPercentSurfaceToFilter = boxesPercentSurfaceToFilter;
	}
	
	
	/**
	 * Constructor with box size modification and slice number used to start OTSU threshold calculation to last slice
	 *
	 * @param inputFolder               Path folder containing Images
	 * @param outputFolder              Path folder output analyse
	 * @param xCropBoxSize              Number of voxels add in x axis around object
	 * @param yCropBoxSize              Number of voxels add in z axis around object
	 * @param zCropBoxSize              Number of stack add in z axis around object
	 * @param channelToComputeThreshold Channel number to compute OTSU
	 * @param slicesOTSUComputing       Slice start to compute OTSU
	 * @param thresholdOTSUComputing    Minimum OTSU threshold used
	 * @param maxVolumeNucleus          Volume maximum of objects detected
	 * @param minVolumeNucleus          Volume minimum of objects detected
	 */
	public AutocropParameters(String inputFolder, String outputFolder,
	                          double xCal,
	                          double yCal,
	                          double zCal,
	                          int xCropBoxSize,
	                          int yCropBoxSize,
	                          int zCropBoxSize,
	                          int numberFontSize,
	                          int slicesOTSUComputing,
	                          int thresholdOTSUComputing,
	                          int channelToComputeThreshold,
	                          int minVolumeNucleus,
	                          int maxVolumeNucleus) {
		
		super(inputFolder, outputFolder, xCal, yCal, zCal);
		this.xCropBoxSize = xCropBoxSize;
		this.yCropBoxSize = yCropBoxSize;
		this.zCropBoxSize = zCropBoxSize;
		this.numberFontSize = numberFontSize;
		this.thresholdOTSUComputing = thresholdOTSUComputing;
		this.slicesOTSUComputing = slicesOTSUComputing;
		this.channelToComputeThreshold = channelToComputeThreshold;
		this.maxVolumeNucleus = maxVolumeNucleus;
		this.minVolumeNucleus = minVolumeNucleus;
		
	}
	
	
	/**
	 * Constructor with box size modification and slice number used to start OTSU threshold calculation to last slice
	 *
	 * @param inputFolder                 Path folder containing Images
	 * @param outputFolder                Path folder output analyse
	 * @param xCal                        Image calibration X
	 * @param yCal                        Image calibration Y
	 * @param zCal                        Image calibration Z
	 * @param xCropBoxSize                Number of voxels add in x axis around object
	 * @param yCropBoxSize                Number of voxels add in z axis around object
	 * @param zCropBoxSize                Number of stack add in z axis around object
	 * @param channelToComputeThreshold   Channel number to compute OTSU
	 * @param slicesOTSUComputing         Slice start to compute OTSU
	 * @param thresholdOTSUComputing      Minimum OTSU threshold used
	 * @param maxVolumeNucleus            Volume maximum of objects detected
	 * @param minVolumeNucleus            Volume minimum of objects detected
	 * @param boxesPercentSurfaceToFilter Surface percent of boxes to groups them
	 * @param regroupBoxes                Activation of boxes regrouping
	 */
	public AutocropParameters(String inputFolder, String outputFolder,
	                          double xCal,
	                          double yCal,
	                          double zCal,
	                          int xCropBoxSize,
	                          int yCropBoxSize,
	                          int zCropBoxSize,
	                          int numberFontSize,
	                          int slicesOTSUComputing,
	                          int thresholdOTSUComputing,
	                          int channelToComputeThreshold,
	                          int minVolumeNucleus,
	                          int maxVolumeNucleus,
	                          int boxesPercentSurfaceToFilter,
	                          boolean regroupBoxes) {
		
		super(inputFolder, outputFolder, xCal, yCal, zCal);
		this.xCropBoxSize = xCropBoxSize;
		this.yCropBoxSize = yCropBoxSize;
		this.zCropBoxSize = zCropBoxSize;
		this.numberFontSize = numberFontSize;
		this.thresholdOTSUComputing = thresholdOTSUComputing;
		this.slicesOTSUComputing = slicesOTSUComputing;
		this.channelToComputeThreshold = channelToComputeThreshold;
		this.maxVolumeNucleus = maxVolumeNucleus;
		this.minVolumeNucleus = minVolumeNucleus;
		this.boxesPercentSurfaceToFilter = boxesPercentSurfaceToFilter;
		this.boxesRegrouping = regroupBoxes;
		
	}
	
	
	/**
	 * Constructor using input , output folders and config file (for command line execution)
	 *
	 * @param inputFolder      Path folder containing Images
	 * @param outputFolder     Path folder output analyse
	 * @param pathToConfigFile Path to the config file
	 */
	public AutocropParameters(String inputFolder, String outputFolder,
	                          String pathToConfigFile) {
		super(inputFolder, outputFolder, pathToConfigFile);
		addProperties(pathToConfigFile);
		
	}
	
	
	public void addProperties(String pathToConfigFile) {
		
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
				case "xCropBoxSize":
					this.xCropBoxSize = Integer.parseInt(prop.getProperty("xCropBoxSize"));
					break;
				case "yCropBoxSize":
					this.yCropBoxSize = Integer.parseInt(prop.getProperty("yCropBoxSize"));
					break;
				case "zCropBoxSize":
					this.zCropBoxSize = Integer.parseInt(prop.getProperty("zCropBoxSize"));
					break;
				case "boxNumberFontSize":
					this.numberFontSize = Integer.parseInt(prop.getProperty("boxNumberFontSize"));
					break;
				case "thresholdOTSUComputing":
					this.thresholdOTSUComputing = Integer.parseInt(prop.getProperty("thresholdOTSUComputing"));
					break;
				case "slicesOTSUComputing":
					this.slicesOTSUComputing = Integer.parseInt(prop.getProperty("slicesOTSUComputing"));
					break;
				case "channelToComputeThreshold":
					this.channelToComputeThreshold = Integer.parseInt(prop.getProperty("channelToComputeThreshold"));
					break;
				case "maxVolumeNucleus":
					this.maxVolumeNucleus = Integer.parseInt(prop.getProperty("maxVolumeNucleus"));
					break;
				case "minVolumeNucleus":
					this.minVolumeNucleus = Integer.parseInt(prop.getProperty("minVolumeNucleus"));
					break;
				case "boxesPercentSurfaceToFilter":
					this.boxesPercentSurfaceToFilter =
							Integer.parseInt(prop.getProperty("boxesPercentSurfaceToFilter"));
					break;
				case "boxesRegrouping":
					this.boxesRegrouping = Boolean.parseBoolean(prop.getProperty("boxesRegrouping"));
					break;
			}
		}
	}
	
	
	/**
	 * Method to get parameters of the analyse
	 *
	 * @return : list of the parameters used for the analyse
	 */
	@Override
	public String getAnalysisParameters() {
		super.getAnalysisParameters();
		this.headerInfo += "#X box size: " + xCropBoxSize + "\n"
		                   + "#Y box size: " + yCropBoxSize + "\n"
		                   + "#Z box size: " + zCropBoxSize + "\n"
		                   + "#thresholdOTSUComputing: " + thresholdOTSUComputing + "\n"
		                   + "#slicesOTSUComputing: " + slicesOTSUComputing + "\n"
		                   + "#channelToComputeThreshold: " + channelToComputeThreshold + "\n"
		                   + "#maxVolumeNucleus:" + maxVolumeNucleus + "\n"
		                   + "#minVolumeNucleus: " + minVolumeNucleus + "\n";
		return this.headerInfo;
	}
	
	
	/**
	 * Getter for x box size in pixel
	 *
	 * @return x box size in pixel
	 */
	public int getXCropBoxSize() {
		return this.xCropBoxSize;
	}
	
	
	/**
	 * Getter for y box size in pixel
	 *
	 * @return y box size in pixel
	 */
	public int getYCropBoxSize() {
		return this.yCropBoxSize;
	}
	
	
	/**
	 * Getter for z box size in pixel
	 *
	 * @return z box size in pixel
	 */
	public int getZCropBoxSize() {
		return this.zCropBoxSize;
	}

	/**
	 * Getter for the font size of the box number
	 *
	 * @return font size
	 */
	public int getNumberFontSize(){
		return this.numberFontSize;
	}
	
	/**
	 * Getter for OTSU threshold used to compute segmented image
	 *
	 * @return OTSU threshold used
	 */
	public int getThresholdOTSUComputing() {
		return this.thresholdOTSUComputing;
	}
	
	
	/**
	 * Getter for channel number used to segmented image (OTSU computing)
	 *
	 * @return channel number
	 */
	public int getChannelToComputeThreshold() {
		return this.channelToComputeThreshold;
	}
	
	
	/**
	 * Getter for minimum volume object segmented
	 *
	 * @return minimum volume
	 */
	public int getMinVolumeNucleus() {
		return this.minVolumeNucleus;
	}
	
	
	/**
	 * Getter for maximum volume object segmented
	 *
	 * @return maximum volume
	 */
	public int getMaxVolumeNucleus() {
		return this.maxVolumeNucleus;
	}
	
	
	/**
	 * Getter for start slice used to compute OTSU
	 *
	 * @return start slice
	 */
	public int getSlicesOTSUComputing() {
		return this.slicesOTSUComputing;
	}
	
	
	/**
	 * Getter boxes merging activation
	 *
	 * @return status
	 */
	public boolean getBoxesRegrouping() {
		return this.boxesRegrouping;
	}
	
	
	/**
	 * Getter percent of surface intersection to merge 2 rectangles.
	 *
	 * @return percentage surface
	 */
	public int getBoxesPercentSurfaceToFilter() {
		return this.boxesPercentSurfaceToFilter;
	}
	
}
