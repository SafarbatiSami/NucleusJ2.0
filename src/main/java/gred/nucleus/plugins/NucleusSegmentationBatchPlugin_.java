package gred.nucleus.plugins;

import gred.nucleus.dialogs.NucleusSegmentationBatchDialog;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.utils.FileList;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;


/**
 * @author Tristan Dubos and Axel Poulet
 * @deprecated Method to segment the nucleus on batch
 */
public class NucleusSegmentationBatchPlugin_ implements PlugIn {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private final NucleusSegmentationBatchDialog nucleusSegmentationBatchDialog = new NucleusSegmentationBatchDialog();
	
	
	/**
	 *
	 */
	public void run(String arg) {
		while (nucleusSegmentationBatchDialog.isShowing()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				LOGGER.error("An error occurred.", e);
			}
		}
		if (nucleusSegmentationBatchDialog.isStart()) {
			FileList fileList      = new FileList();
			File[]   tRawImageFile = fileList.run(nucleusSegmentationBatchDialog.getRawDataDirectory());
			if (tRawImageFile.length == 0) {
				IJ.showMessage("There are no image in " + nucleusSegmentationBatchDialog.getRawDataDirectory());
			} else {
				if (IJ.openImage(tRawImageFile[0].toString()).getType() == ImagePlus.GRAY32) {
					IJ.error("image format", "No images in gray scale 8bits or 16 bits in 3D");
					return;
				}
				try {
					SegmentationCalling otsuModified =
							new SegmentationCalling(nucleusSegmentationBatchDialog.getRawDataDirectory(),
							                        nucleusSegmentationBatchDialog.getWorkDirectory(),
							                        (short) nucleusSegmentationBatchDialog.getMinVolume(),
							                        (short) nucleusSegmentationBatchDialog.getMaxVolume());
					otsuModified.runOneImage();
				} catch (Exception e) {
					LOGGER.error("An error occurred.", e);
				}
			}
		}
	}
	
	
	/** @return  */
	public int getNbCpu() {
		return nucleusSegmentationBatchDialog.getNbCpu();
	}
	
	
	/** @return  */
	public double getZCalibration() {
		return nucleusSegmentationBatchDialog.getZCalibration();
	}
	
	
	/** @return  */
	public double getXCalibration() {
		return nucleusSegmentationBatchDialog.getXCalibration();
	}
	
	
	/** @return  */
	public double getYCalibration() {
		return nucleusSegmentationBatchDialog.getYCalibration();
	}
	
	
	/** @return  */
	public String getUnit() {
		return nucleusSegmentationBatchDialog.getUnit();
	}
	
	
	/** @return  */
	public double getMinVolume() {
		return nucleusSegmentationBatchDialog.getMinVolume();
	}
	
	
	/** @return  */
	public double getMaxVolume() {
		return nucleusSegmentationBatchDialog.getMaxVolume();
	}
	
	
	/** @return  */
	public String getWorkDirectory() {
		return nucleusSegmentationBatchDialog.getWorkDirectory();
	}
	
}