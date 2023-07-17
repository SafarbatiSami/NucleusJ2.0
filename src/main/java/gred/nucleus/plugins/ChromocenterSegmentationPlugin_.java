package gred.nucleus.plugins;

import gred.nucleus.core.ChromocentersEnhancement;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.plugin.PlugIn;


/**
 * @author Poulet Axel
 * @deprecated Method to detect the chromocenters on one image
 */
@Deprecated
public class ChromocenterSegmentationPlugin_ implements PlugIn {
	
	/**
	 *
	 */
	public void run(String arg) {
		int    indexRawImage       = 0;
		int    indexSegmentedImage = 0;
		double xCalibration        = 1;
		double yCalibration        = 1;
		double zCalibration        = 1;
		String unit                = "pixel";
		int[]  wList               = WindowManager.getIDList();
		if (wList == null) {
			IJ.noImage();
			return;
		}
		String[] titles = new String[wList.length];
		for (int i = 0; i < wList.length; i++) {
			ImagePlus imagePlus = WindowManager.getImage(wList[i]);
			if (imagePlus != null) {
				if (i == 0) {
					Calibration cal = imagePlus.getCalibration();
					xCalibration = cal.pixelWidth;
					yCalibration = cal.pixelHeight;
					zCalibration = cal.pixelDepth;
					unit = cal.getUnit();
				}
				titles[i] = imagePlus.getTitle();
			} else {
				titles[i] = "";
			}
		}
		GenericDialog genericDialog = new GenericDialog("Chromocenter Segmentation", IJ.getInstance());
		genericDialog.addChoice("Raw image", titles, titles[indexRawImage]);
		genericDialog.addChoice("Nucleus segmented image", titles, titles[indexSegmentedImage]);
		genericDialog.addNumericField("x calibration", xCalibration, 3);
		genericDialog.addNumericField("y calibration", yCalibration, 3);
		genericDialog.addNumericField("z calibration", zCalibration, 3);
		genericDialog.addStringField("Unit", unit, 10);
		genericDialog.showDialog();
		if (genericDialog.wasCanceled()) {
			return;
		}
		ImagePlus imagePlusInput     = WindowManager.getImage(wList[genericDialog.getNextChoiceIndex()]);
		ImagePlus imagePlusSegmented = WindowManager.getImage(wList[genericDialog.getNextChoiceIndex()]);
		xCalibration = genericDialog.getNextNumber();
		yCalibration = genericDialog.getNextNumber();
		zCalibration = genericDialog.getNextNumber();
		unit = genericDialog.getNextString();
		Calibration calibration = new Calibration();
		calibration.pixelDepth = zCalibration;
		calibration.pixelWidth = xCalibration;
		calibration.pixelHeight = yCalibration;
		calibration.setUnit(unit);
		imagePlusInput.setCalibration(calibration);
		imagePlusSegmented.setCalibration(calibration);
		ImagePlus imagePlusContrast =
				ChromocentersEnhancement.applyEnhanceChromocenters(imagePlusInput, imagePlusSegmented);
		imagePlusContrast.setTitle("ContrastedImage");
		imagePlusContrast.show();
	}
	
}