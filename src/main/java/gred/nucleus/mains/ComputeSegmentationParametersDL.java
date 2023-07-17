package gred.nucleus.mains;

import gred.nucleus.core.Measure3D;
import gred.nucleus.files.Directory;
import gred.nucleus.files.OutputTextFile;
import gred.nucleus.plugins.PluginParameters;
import gred.nucleus.utils.Histogram;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import inra.ijpb.binary.BinaryImages;
import inra.ijpb.label.LabelImages;
import loci.formats.FormatException;
import loci.plugins.BF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;


public class ComputeSegmentationParametersDL {
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
			ImagePlus raw = new ImagePlus(currentFile.getAbsolutePath());
			LOGGER.info("current File: {}", currentFile.getName());
			
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
	
	
	public static void computeNucleusParametersDL(String rawImageSourceFile, String segmentedImagesSourceFile)
	throws IOException, FormatException {
		
		PluginParameters pluginParameters = new PluginParameters(rawImageSourceFile, segmentedImagesSourceFile);
		Directory        directoryInput   = new Directory(pluginParameters.getOutputFolder());
		directoryInput.listImageFiles(pluginParameters.getOutputFolder());
		directoryInput.checkIfEmpty();
		List<File> segImages = directoryInput.getFileList();
		StringBuilder outputCropGeneralInfoOTSU =
				new StringBuilder(pluginParameters.getAnalysisParameters() + getResultsColumnNames());
		for (File currentFile : segImages) {
			LOGGER.info("current File: {}", currentFile.getName());
			ImagePlus raw = new ImagePlus(pluginParameters.getInputFolder() +
			                              directoryInput.getSeparator() +
			                              currentFile.getName());
			ImagePlus[] segmented = BF.openImagePlus(pluginParameters.getOutputFolder() + currentFile.getName());
			// TODO TRANSFORMATION FACTORISABLE AVEC METHODE DU DESSUS !!!!!
			segmented[0] = generateSegmentedImage(segmented[0], 1);
			segmented[0] = BinaryImages.componentsLabeling(segmented[0], 26, 32);
			LabelImages.removeBorderLabels(segmented[0]);
			segmented[0] = generateSegmentedImage(segmented[0], 1);
			Histogram histogram = new Histogram();
			histogram.run(segmented[0]);
			if (histogram.getNbLabels() > 0) {
				Measure3D measure3D = new Measure3D(segmented,
				                                    raw,
				                                    pluginParameters.getXCalibration(raw),
				                                    pluginParameters.getYCalibration(raw),
				                                    pluginParameters.getZCalibration(raw));
				outputCropGeneralInfoOTSU.append(measure3D.nucleusParameter3D()).append("\tNA").append("\n");
			}
		}
		
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(pluginParameters.getOutputFolder()
		                                                         + directoryInput.getSeparator()
		                                                         + "result_Segmentation_Analyse.csv");
		resultFileOutputOTSU.saveTextFile(outputCropGeneralInfoOTSU.toString(), true);
	}
	
	
	public static void main(String[] args) throws Exception {
		computeNucleusParametersDL(
				"/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/RAW",
				"/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/129_TRIER");
	}
	
	
	public static ImagePlus imageSEGTransform(ImagePlus segmentedTMP) {
		LabelImages.removeBorderLabels(segmentedTMP);
		return segmentedTMP;
	}
	
	
	public static ImagePlus generateSegmentedImage(ImagePlus imagePlusInput,
	                                               int threshold) {
		ImageStack imageStackInput    = imagePlusInput.getStack();
		ImagePlus  imagePlusSegmented = imagePlusInput.duplicate();
		
		imagePlusSegmented.setTitle(imagePlusInput.getTitle());
		ImageStack imageStackSegmented = imagePlusSegmented.getStack();
		for (int k = 0; k < imagePlusInput.getStackSize(); ++k) {
			for (int i = 0; i < imagePlusInput.getWidth(); ++i) {
				for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
					double voxelValue = imageStackInput.getVoxel(i, j, k);
					if (voxelValue > 1) {
						imageStackSegmented.setVoxel(i, j, k, 255);
					} else {
						imageStackSegmented.setVoxel(i, j, k, 0);
					}
				}
			}
		}
		return imagePlusSegmented;
		
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
	
	
	private static void saveFile(ImagePlus imagePlusInput, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlusInput);
		fileSaver.saveAsTiffStack(pathFile);
	}
	
}
