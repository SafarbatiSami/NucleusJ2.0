package gred.nucleus.mains;

import gred.nucleus.core.Measure3D;
import gred.nucleus.files.Directory;
import gred.nucleus.files.OutputTextFile;
import gred.nucleus.plugins.PluginParameters;
import ij.ImagePlus;
import ij.ImageStack;
import loci.formats.FormatException;
import loci.plugins.BF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;


public class ComputeSegmentationParameters {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	public static void computeNucleusParameters(String rawImageSourceFile,
	                                            String segmentedImagesSourceFile,
	                                            String pathToConfig)
	throws IOException, FormatException {
		PluginParameters pluginParameters =
				new PluginParameters(rawImageSourceFile, segmentedImagesSourceFile, pathToConfig);
		Directory directoryInput = new Directory(pluginParameters.getInputFolder());
		directoryInput.listImageFiles(pluginParameters.getInputFolder());
		directoryInput.checkIfEmpty();
		List<File> rawImages = directoryInput.getFileList();
		StringBuilder outputCropGeneralInfoOTSU =
				new StringBuilder(pluginParameters.getAnalysisParameters() + getResultsColumnNames());
		for (File currentFile : rawImages) {
			ImagePlus   raw       = new ImagePlus(currentFile.getAbsolutePath());
			ImagePlus[] segmented = BF.openImagePlus(pluginParameters.getOutputFolder() + currentFile.getName());
			
			
			Measure3D measure3D = new Measure3D(segmented,
			                                    raw,
			                                    pluginParameters.getXCalibration(raw),
			                                    pluginParameters.getYCalibration(raw),
			                                    pluginParameters.getZCalibration(raw));
			outputCropGeneralInfoOTSU.append(measure3D.nucleusParameter3D()).append("\n");
		}
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(pluginParameters.getOutputFolder()
		                                                         + directoryInput.getSeparator()
		                                                         + "result_Segmentation_Analyse.csv");
		resultFileOutputOTSU.saveTextFile(outputCropGeneralInfoOTSU.toString(), true);
		
	}
	
	
	public static void computeNucleusParameters(String rawImageSourceFile, String segmentedImagesSourceFile)
	throws IOException, FormatException {
		
		PluginParameters pluginParameters = new PluginParameters(rawImageSourceFile, segmentedImagesSourceFile);
		Directory        directoryInput   = new Directory(pluginParameters.getInputFolder());
		directoryInput.listImageFiles(pluginParameters.getInputFolder());
		directoryInput.checkIfEmpty();
		List<File> rawImages = directoryInput.getFileList();
		StringBuilder outputCropGeneralInfoOTSU =
				new StringBuilder(pluginParameters.getAnalysisParameters() + getResultsColumnNames());
		for (File currentFile : rawImages) {
			LOGGER.info("current File: {}", currentFile.getName());
			
			ImagePlus   raw       = new ImagePlus(currentFile.getAbsolutePath());
			ImagePlus[] segmented = BF.openImagePlus(pluginParameters.getOutputFolder() + currentFile.getName());
			Measure3D measure3D = new Measure3D(segmented,
			                                    raw,
			                                    pluginParameters.getXCalibration(raw),
			                                    pluginParameters.getYCalibration(raw),
			                                    pluginParameters.getZCalibration(raw));
			
			outputCropGeneralInfoOTSU.append(measure3D.nucleusParameter3D()).append("\tNA").append("\n");
		}
		
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(pluginParameters.getOutputFolder()
		                                                         + directoryInput.getSeparator()
		                                                         + "result_Segmentation_Analyse.csv");
		resultFileOutputOTSU.saveTextFile(outputCropGeneralInfoOTSU.toString(), true);
		
	}
	
	
	public static void main(String[] args) throws Exception {
		computeNucleusParameters(
				"/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/RAW",
				"/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/GIFT");
	}
	
	
	public static String getResultsColumnNames() {
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
		       "ImageSize\t" +
		       "OTSUThreshold\n";
	}
	
	
	public static int recomputeOTSU(ImagePlus raw, ImagePlus segmented) {
		int        otsuThreshold = Integer.MAX_VALUE;
		ImageStack imageStackRaw = raw.getStack();
		ImageStack imageStackSeg = segmented.getStack();
		for (int k = 0; k < raw.getStackSize(); ++k) {
			for (int i = 0; i < raw.getWidth(); ++i) {
				for (int j = 0; j < raw.getHeight(); ++j) {
					if ((imageStackSeg.getVoxel(i, j, k) == 255) &&
					    (otsuThreshold >= imageStackRaw.getVoxel(i, j, k))) {
						otsuThreshold = (int) (imageStackRaw.getVoxel(i, j, k));
					}
				}
			}
		}
		return otsuThreshold;
	}
	
}

