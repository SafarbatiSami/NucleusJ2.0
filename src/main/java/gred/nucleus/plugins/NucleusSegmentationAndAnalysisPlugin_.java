package gred.nucleus.plugins;

import gred.nucleus.dialogs.NucleusSegmentationAndAnalysisDialog;
import gred.nucleus.nucleuscaracterisations.NucleusAnalysis;
import gred.nucleus.segmentation.SegmentationCalling;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;


/**
 * @author Tristan Dubos and Axel Poulet
 * @deprecated Method to segment and analyse the nucleus on one image //TODO add a parameter in GUI to chose true or
 * false for the 3D convex hull algorithm
 */
@Deprecated
public class NucleusSegmentationAndAnalysisPlugin_ implements PlugIn {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	/**
	 * Run method for imageJ plugin for the nuclear segmentation
	 *
	 * @param arg use by imageJ
	 */
	public void run(String arg) {
		ImagePlus img = WindowManager.getCurrentImage();
		if (null == img) {
			IJ.noImage();
			return;
		} else if (img.getStackSize() == 1 || (img.getType() != ImagePlus.GRAY8 && img.getType() != ImagePlus.GRAY16)) {
			IJ.error("image format", "No images in 8 or 16 bits gray scale  in 3D");
			return;
		}
		if (IJ.versionLessThan("1.32c")) {
			return;
		}
		NucleusSegmentationAndAnalysisDialog nucleusSegmentationAndAnalysisDialog =
				new NucleusSegmentationAndAnalysisDialog(img.getCalibration());
		while (nucleusSegmentationAndAnalysisDialog.isShowing()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				LOGGER.error("An error occurred.", e);
			}
		}
		if (nucleusSegmentationAndAnalysisDialog.isStart()) {
			short volumeMin = (short) nucleusSegmentationAndAnalysisDialog.getMinVolume();
			short volumeMax = (short) nucleusSegmentationAndAnalysisDialog.getMaxVolume();
			
			LOGGER.info("Starting image processing on: {}", img.getTitle());
			SegmentationCalling segMethod = new SegmentationCalling(img, volumeMin, volumeMax);
			try {
				int thresh = segMethod.runOneImage();
				if (thresh != -1) {
					NucleusAnalysis nucleusAnalysis = new NucleusAnalysis(img, segMethod.getImageSegmented());
					//LOGGER.info(nucleusAnalysis.nucleusParameter3D());
					segMethod.getImageSegmented().show();
				}
			} catch (Exception e) {
				LOGGER.error("An error occurred.", e);
			}
		}
	}
	
}