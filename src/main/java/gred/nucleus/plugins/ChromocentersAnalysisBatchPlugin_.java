package gred.nucleus.plugins;

import gred.nucleus.core.ChromocenterAnalysis;
import gred.nucleus.core.NucleusChromocentersAnalysis;
import gred.nucleus.dialogs.ChromocentersAnalysisPipelineBatchDialog;
import gred.nucleus.utils.FileList;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.plugin.PlugIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;


/**
 * @author Tristan Dubos and Axel Poulet
 */
public class ChromocentersAnalysisBatchPlugin_ implements PlugIn {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	/** Run the the analyse, call the graphical windows */
	public void run(String arg) {
		ChromocentersAnalysisPipelineBatchDialog chromocentersPipelineBatchDialog =
				new ChromocentersAnalysisPipelineBatchDialog();
		while (chromocentersPipelineBatchDialog.isShowing()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				LOGGER.error("An error occurred.", e);
			}
		}
		if (chromocentersPipelineBatchDialog.isStart()) {
			FileList fileList      = new FileList();
			File[]   tFileRawImage = fileList.run(chromocentersPipelineBatchDialog.getRawDataDirectory());
			
			if (fileList.isDirectoryOrFileExist(".+RawDataNucleus.+", tFileRawImage) &&
			    fileList.isDirectoryOrFileExist(".+SegmentedDataNucleus.+", tFileRawImage) &&
			    fileList.isDirectoryOrFileExist(".+SegmentedDataCc.+", tFileRawImage)) {
				
				String rhfChoice;
				if (chromocentersPipelineBatchDialog.isRHFVolumeAndIntensity()) {
					rhfChoice = "Volume and intensity";
				} else if (chromocentersPipelineBatchDialog.isRhfVolume()) {
					rhfChoice = "Volume";
				} else {
					rhfChoice = "Intensity";
				}
				
				List<String> listImageChromocenter = fileList.fileSearchList(".+SegmentedDataCc.+", tFileRawImage);
				
				String workDirectory = chromocentersPipelineBatchDialog.getWorkDirectory();
				
				String nameFileChromocenterAndNucleus = workDirectory + File.separator + "NucAndCcParameters.tab";
				String nameFileChromocenter           = workDirectory + File.separator + "CcParameters.tab";
				
				for (int i = 0; i < listImageChromocenter.size(); ++i) {
					LOGGER.info("image {}/{}", (i + 1), listImageChromocenter.size());
					String pathImageChromocenter = listImageChromocenter.get(i);
					String pathNucleusRaw =
							pathImageChromocenter.replace("SegmentedDataCc", "RawDataNucleus");
					String pathNucleusSegmented =
							pathImageChromocenter.replace("SegmentedDataCc", "SegmentedDataNucleus");
					LOGGER.info(pathNucleusRaw);
					LOGGER.info(pathNucleusSegmented);
					if (fileList.isDirectoryOrFileExist(pathNucleusRaw, tFileRawImage) &&
					    fileList.isDirectoryOrFileExist(pathNucleusSegmented, tFileRawImage)) {
						ImagePlus imagePlusInput = IJ.openImage(pathNucleusRaw);
						if (imagePlusInput.getType() == ImagePlus.GRAY32) {
							IJ.error("image format", "No images in gray scale 8bits in 3D");
							return;
						}
						ImagePlus   imagePlusChromocenter = IJ.openImage(listImageChromocenter.get(i));
						ImagePlus   imagePlusSegmented    = IJ.openImage(pathNucleusSegmented);
						Calibration calibration           = new Calibration();
						if (chromocentersPipelineBatchDialog.getCalibrationStatus()) {
							calibration.pixelWidth = chromocentersPipelineBatchDialog.getXCalibration();
							calibration.pixelHeight = chromocentersPipelineBatchDialog.getYCalibration();
							calibration.pixelDepth = chromocentersPipelineBatchDialog.getZCalibration();
							calibration.setUnit(chromocentersPipelineBatchDialog.getUnit());
						} else {
							calibration = imagePlusInput.getCalibration();
						}
						imagePlusChromocenter.setCalibration(calibration);
						imagePlusSegmented.setCalibration(calibration);
						imagePlusInput.setCalibration(calibration);
						try {
							if (chromocentersPipelineBatchDialog.isNucAndCcAnalysis()) {
								ChromocenterAnalysis.computeParametersChromocenter(nameFileChromocenter,
								                                                   imagePlusSegmented,
								                                                   imagePlusChromocenter);
								LOGGER.info("chromocenterAnalysis is computing...");
								LOGGER.info("nucleusChromocenterAnalysis is computing...");
								NucleusChromocentersAnalysis.computeParameters(nameFileChromocenterAndNucleus,
								                                               rhfChoice,
								                                               imagePlusInput,
								                                               imagePlusSegmented,
								                                               imagePlusChromocenter);
							} else if (chromocentersPipelineBatchDialog.isCcAnalysis()) {
								ChromocenterAnalysis.computeParametersChromocenter(nameFileChromocenter,
								                                                   imagePlusSegmented,
								                                                   imagePlusChromocenter);
							} else {
								NucleusChromocentersAnalysis.computeParameters(nameFileChromocenterAndNucleus,
								                                               rhfChoice,
								                                               imagePlusInput,
								                                               imagePlusSegmented,
								                                               imagePlusChromocenter);
							}
						} catch (IOException e) {
							LOGGER.error("An error occurred.", e);
						}
					} else {
						LOGGER.info("Image name problem: the image {} is not found " +
						            "in the directory SegmentedDataNucleus or RawDataNucleus, " +
						            "see nameProblem.txt in {}",
						            pathImageChromocenter,
						            workDirectory);
						try (BufferedWriter bufferedWriterLogFile = new BufferedWriter(new FileWriter(workDirectory +
						                                                                              File.separator +
						                                                                              "logNameProblem.log",
						                                                                              true))) {
							bufferedWriterLogFile.write(pathImageChromocenter + "\n");
							bufferedWriterLogFile.flush();
						} catch (IOException e) {
							LOGGER.error("An error occurred.", e);
						}
					}
				}
				LOGGER.info("End of the chromocenter analysis , the results are in {}",
				            chromocentersPipelineBatchDialog.getWorkDirectory());
			} else {
				IJ.showMessage(
						"There are no the three subdirectories  (See the directory name) or subDirectories are empty");
			}
		}
	}
	
}