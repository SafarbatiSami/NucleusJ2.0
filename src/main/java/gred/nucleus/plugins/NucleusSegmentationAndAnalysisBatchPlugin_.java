package gred.nucleus.plugins;

import gred.nucleus.dialogs.NucleusSegmentationAndAnalysisBatchDialog;
import gred.nucleus.segmentation.SegmentationCalling;
import ij.plugin.PlugIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;


/**
 * @author Tristant Dubos and Axel Poulet
 * @deprecated Method to segment and analyse the nucleus on batch
 */
public class NucleusSegmentationAndAnalysisBatchPlugin_ implements PlugIn {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	NucleusSegmentationAndAnalysisBatchDialog nucleusPipelineBatchDialog =
			new NucleusSegmentationAndAnalysisBatchDialog();
	
	
	/** TODO CHANGER LES METHODES APPELER !!!!!!! */
	public void run(String arg) {
		while (nucleusPipelineBatchDialog.isShowing()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				LOGGER.error("An error occurred.", e);
			}
		}
		if (nucleusPipelineBatchDialog.isStart()) {
			LOGGER.info("Beginning of the segmentation of nuclei, the data are in " +
			            nucleusPipelineBatchDialog.getRawDataDirectory());
			SegmentationCalling otsuModified =
					new SegmentationCalling(nucleusPipelineBatchDialog.getRawDataDirectory(),
					                        nucleusPipelineBatchDialog.getWorkDirectory(),
					                        (short) nucleusPipelineBatchDialog.getMinVolume(),
					                        (short) nucleusPipelineBatchDialog.getMaxVolume());
			try {
				String log = otsuModified.runSeveralImages2();
			} catch (Exception e) {
				LOGGER.error("An error occurred.", e);
			}
			
			LOGGER.info("End of the segmentation the nuclei, the results are in " +
			            nucleusPipelineBatchDialog.getWorkDirectory());
		}
	}
	
	
	/** @return  */
	public int getNbCpu() {
		return nucleusPipelineBatchDialog.getNbCpu();
	}
	
	
	/** @return  */
	public double getZCalibration() {
		return nucleusPipelineBatchDialog.getZCalibration();
	}
	
	
	/** @return  */
	public double getXCalibration() {
		return nucleusPipelineBatchDialog.getXCalibration();
	}
	
	
	/** @return  */
	public double getYCalibration() {
		return nucleusPipelineBatchDialog.getYCalibration();
	}
	
	
	/** @return  */
	public String getUnit() {
		return nucleusPipelineBatchDialog.getUnit();
	}
	
	
	/** @return  */
	public double getMinVolume() {
		return nucleusPipelineBatchDialog.getMinVolume();
	}
	
	
	/** @return  */
	public double getMaxVolume() {
		return nucleusPipelineBatchDialog.getMaxVolume();
	}
	
	
	/** @return  */
	public String getWorkDirectory() {
		return nucleusPipelineBatchDialog.getWorkDirectory();
	}
	
	
	/** @return  */
	public boolean is2D3DAnalysis() {
		return nucleusPipelineBatchDialog.is2D3DAnalysis();
	}
	
	
	/** @return  */
	public boolean is3DAnalysis() {
		return nucleusPipelineBatchDialog.is3D();
	}
	
}
