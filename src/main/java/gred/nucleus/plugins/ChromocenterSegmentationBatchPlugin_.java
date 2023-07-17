package gred.nucleus.plugins;

import gred.nucleus.core.ChromocentersEnhancement;
import gred.nucleus.dialogs.ChromocenterSegmentationPipelineBatchDialog;
import gred.nucleus.utils.FileList;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.plugin.GaussianBlur3D;
import ij.plugin.PlugIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.List;


/**
 * @author Tristan Dubos and Axel Poulet
 */
public class ChromocenterSegmentationBatchPlugin_ implements PlugIn {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	public void run(String arg) {
		
		ChromocenterSegmentationPipelineBatchDialog chromocenterSegmentationPipelineBatchDialog =
				new ChromocenterSegmentationPipelineBatchDialog();
		while (chromocenterSegmentationPipelineBatchDialog.isShowing()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				LOGGER.error("Interruption exception.", e);
				Thread.currentThread().interrupt();
			}
		}
		if (chromocenterSegmentationPipelineBatchDialog.isStart()) {
			FileList fileList     = new FileList();
			File[]   tFileRawData = fileList.run(chromocenterSegmentationPipelineBatchDialog.getRawDataDirectory());
			if (fileList.isDirectoryOrFileExist(".+RawDataNucleus.+", tFileRawData) &&
			    fileList.isDirectoryOrFileExist(".+SegmentedDataNucleus.+", tFileRawData)) {
				
				List<String> listImageSegmentedDataNucleus =
						fileList.fileSearchList(".+SegmentedDataNucleus.+", tFileRawData);
				String workDirectory =
						chromocenterSegmentationPipelineBatchDialog.getWorkDirectory();
				for (int i = 0; i < listImageSegmentedDataNucleus.size(); ++i) {
					LOGGER.info("image {}/{}", (i + 1), listImageSegmentedDataNucleus.size());
					String pathImageSegmentedNucleus = listImageSegmentedDataNucleus.get(i);
					String pathNucleusRaw =
							pathImageSegmentedNucleus.replace("SegmentedDataNucleus", "RawDataNucleus");
					LOGGER.info(pathNucleusRaw);
					if (fileList.isDirectoryOrFileExist(pathNucleusRaw, tFileRawData)) {
						ImagePlus imagePlusSegmented = IJ.openImage(pathImageSegmentedNucleus);
						ImagePlus imagePlusInput     = IJ.openImage(pathNucleusRaw);
						GaussianBlur3D.blur(imagePlusInput, 0.25, 0.25, 1);
						ImageStack imageStack = imagePlusInput.getStack();
						int        max        = 0;
						for (int k = 0; k < imagePlusInput.getStackSize(); ++k) {
							for (int b = 0; b < imagePlusInput.getWidth(); ++b) {
								for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
									if (max < imageStack.getVoxel(b, j, k)) {
										max = (int) imageStack.getVoxel(b, j, k);
									}
								}
							}
						}
						IJ.setMinAndMax(imagePlusInput, 0, max);
						IJ.run(imagePlusInput, "Apply LUT", "stack");
						Calibration calibration = new Calibration();
						if (chromocenterSegmentationPipelineBatchDialog.getCalibrationStatus()) {
							calibration.pixelWidth = chromocenterSegmentationPipelineBatchDialog.getXCalibration();
							calibration.pixelHeight = chromocenterSegmentationPipelineBatchDialog.getYCalibration();
							calibration.pixelDepth = chromocenterSegmentationPipelineBatchDialog.getZCalibration();
							calibration.setUnit(chromocenterSegmentationPipelineBatchDialog.getUnit());
						} else {
							calibration = imagePlusInput.getCalibration();
						}
						ImagePlus imagePlusContrast =
								ChromocentersEnhancement.applyEnhanceChromocenters(imagePlusInput, imagePlusSegmented);
						imagePlusContrast.setTitle(imagePlusInput.getTitle());
						imagePlusContrast.setCalibration(calibration);
						saveFile(imagePlusContrast, workDirectory + File.separator + "ContrastDataNucleus");
					}
				}
				LOGGER.info("End of the chromocenter segmentation , the results are in {}",
				            chromocenterSegmentationPipelineBatchDialog.getWorkDirectory());
			} else {
				IJ.showMessage(
						"There are no the two subdirectories (See the directory name) or subDirectories are empty");
			}
		}
	}
	
	
	/**
	 * saving file method
	 *
	 * @param imagePlus imagePus to save
	 * @param pathFile  the path where save the image
	 */
	public void saveFile(ImagePlus imagePlus, String pathFile) {
		
		FileSaver fileSaver = new FileSaver(imagePlus);
		File      file      = new File(pathFile);
		if (file.exists() || file.mkdirs()) {
			fileSaver.saveAsTiffStack(pathFile + File.separator + imagePlus.getTitle());
		} else {
			LOGGER.error("Directory does not exist and could not be created: {}", pathFile);
		}
	}
	
}