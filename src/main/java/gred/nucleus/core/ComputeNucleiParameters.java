package gred.nucleus.core;

import fr.igred.omero.Client;
import fr.igred.omero.exception.AccessException;
import fr.igred.omero.exception.ServiceException;
import fr.igred.omero.repository.DatasetWrapper;
import fr.igred.omero.repository.ImageWrapper;
import gred.nucleus.files.Directory;
import gred.nucleus.files.OutputTextFile;
import gred.nucleus.plugins.PluginParameters;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.measure.Calibration;
import loci.plugins.BF;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ComputeNucleiParameters {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private final PluginParameters pluginParameters;

	/**
	 * Constructor with input, output and config files
	 *
	 * @param rawImagesInputDirectory  path to raw images
	 * @param segmentedImagesDirectory path to segmented images associated
	 * @param pathToConfig             path to config file
	 */
	public ComputeNucleiParameters(String rawImagesInputDirectory,
	                               String segmentedImagesDirectory,
	                               String pathToConfig) {
		this.pluginParameters = new PluginParameters(rawImagesInputDirectory, segmentedImagesDirectory, pathToConfig);


	}
	
	
	/**
	 * Constructor with input and output files
	 *
	 * @param rawImagesInputDirectory  path to raw images
	 * @param segmentedImagesDirectory path to segmented images associated
	 */
	public ComputeNucleiParameters(String rawImagesInputDirectory, String segmentedImagesDirectory) {
		this.pluginParameters = new PluginParameters(rawImagesInputDirectory, segmentedImagesDirectory);
	}

	public ComputeNucleiParameters(){
		String rawPath = "." + File.separator + "raw-computeNucleiParameters";
		String segmentedPath = "." + File.separator + "segmented-computeNucleiParameters";

		Directory rawDirectory = new Directory(rawPath);
		rawDirectory.checkAndCreateDir();
		Directory segmentedDirectory = new Directory(segmentedPath);
		segmentedDirectory.checkAndCreateDir();

		this.pluginParameters = new PluginParameters(rawPath, segmentedPath);
	}
	
	
	/**
	 * Constructor with input, output files and calibration from dialog.
	 *
	 * @param rawImagesInputDirectory  path to raw images
	 * @param segmentedImagesDirectory path to segmented images associated
	 * @param cal                      calibration from dialog
	 */
	public ComputeNucleiParameters(String rawImagesInputDirectory, String segmentedImagesDirectory,
	                               Calibration cal) {
		this.pluginParameters = new PluginParameters(rawImagesInputDirectory, segmentedImagesDirectory,
		                                             cal.pixelWidth, cal.pixelHeight, cal.pixelDepth);
	}
	
	
	/**
	 * Compute nuclei parameters generate from segmentation ( OTSU / Convex Hull)
	 * Useful if parallel segmentation was used to get results parameter in the same folder.
	 */
	public void run() {
		Directory directoryRawInput = new Directory(this.pluginParameters.getInputFolder());
		directoryRawInput.listImageFiles(this.pluginParameters.getInputFolder());
		directoryRawInput.checkIfEmpty();
		Directory directorySegmentedInput = new Directory(this.pluginParameters.getOutputFolder());
		directorySegmentedInput.listImageFiles(this.pluginParameters.getOutputFolder());
		directorySegmentedInput.checkIfEmpty();
		List<File>    segmentedImages           = directorySegmentedInput.getFileList();
		StringBuilder outputCropGeneralInfoOTSU = new StringBuilder();
		
		outputCropGeneralInfoOTSU.append(this.pluginParameters.getAnalysisParameters()).append(getColNameResult());
		
		for (File f : segmentedImages) {
			ImagePlus raw = new ImagePlus(this.pluginParameters.getInputFolder() + File.separator + f.getName());
			try {
				ImagePlus[] segmented = BF.openImagePlus(f.getAbsolutePath());
				
				Measure3D measure3D = new Measure3D(segmented,
				                                    raw,
				                                    this.pluginParameters.getXCalibration(raw),
				                                    this.pluginParameters.getYCalibration(raw),
				                                    this.pluginParameters.getZCalibration(raw));
				outputCropGeneralInfoOTSU.append(measure3D.nucleusParameter3D()).append("\n");
			} catch (Exception e) {
				LOGGER.error("An error occurred.", e);
			}
		}
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(
				this.pluginParameters.getOutputFolder()
				+ directoryRawInput.getSeparator()
				+ "result_Segmentation_Analyse.csv");
		
		resultFileOutputOTSU.saveTextFile(outputCropGeneralInfoOTSU.toString(), true);
	}

	public void runFromOMERO(String rawDatasetID, String segmentedDatasetID, Client client) throws AccessException, ServiceException, ExecutionException, InterruptedException, IOException {
		DatasetWrapper rawDataset = client.getDataset(Long.parseLong(rawDatasetID));
		DatasetWrapper segmentedDataset = client.getDataset(Long.parseLong(segmentedDatasetID));

		for (ImageWrapper raw : rawDataset.getImages(client)) {
			saveFile(raw.toImagePlus(client), pluginParameters.getInputFolder() + File.separator + raw.getName());
		}

		for (ImageWrapper segmented : segmentedDataset.getImages(client)) {
			saveFile(segmented.toImagePlus(client), pluginParameters.getOutputFolder() + File.separator + segmented.getName());
		}

		this.run();

		rawDataset.addFile(
				client,
				new File(this.pluginParameters.getOutputFolder() + File.separator + "result_Segmentation_Analyse.csv")
		);

		FileUtils.deleteDirectory(new File(pluginParameters.getInputFolder()));
		FileUtils.deleteDirectory(new File(pluginParameters.getOutputFolder()));
	}

	public static void saveFile(ImagePlus imagePlusInput, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlusInput);
		fileSaver.saveAsTiff(pathFile);
	}

	public void addConfigParameters(String pathToConfig) {
		this.pluginParameters.addGeneralProperties(pathToConfig);
		
	}

	/** @return columns names for results */
	private String getColNameResult() {
		return "NucleusFileName\t" +
		       "Volume\t" +
				"Moment 1\t" +
				"Moment 2\t" +
				"Moment 3 \t" +
		       "Flatness\t" +
		       "Elongation\t" +
		       "Esr\t" +
		       "SurfaceArea\t" +
		       "Sphericity\t" +
		       "MeanIntensityNucleus\t" +
		       "MeanIntensityBackground\t" +
		       "StandardDeviation\t" +
		       "MinIntensity\t" +
		       "MaxIntensity\t" +
		       "MedianIntensityImage\t" +
		       "MedianIntensityNucleus\t" +
		       "MedianIntensityBackground\t" +
		       "ImageSize\n";
	}
	
}
