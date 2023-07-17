package gred.nucleus.plugins;

import gred.nucleus.core.ChromocenterAnalysis;
import gred.nucleus.core.NucleusChromocentersAnalysis;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.plugin.PlugIn;


/**
 * @author Tristan Dubos and Axel Poulet
 * @deprecated Method to analyse the chromocenter
 */
@Deprecated
public class ChromocentersAnalysisPlugin_ implements PlugIn {
	
	/**
	 *
	 */
	public void run(String arg) {
		int    indexCcImage        = 0;
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
		GenericDialog genericDialog = new GenericDialog("Chromocenter Analysis", IJ.getInstance());
		genericDialog.addChoice("Raw image", titles, titles[indexRawImage]);
		genericDialog.addChoice("Nucleus Segmented", titles, titles[indexSegmentedImage]);
		genericDialog.addChoice("Chromocenters image Segmented", titles, titles[indexCcImage]);
		genericDialog.addNumericField("x calibration", xCalibration, 3);
		genericDialog.addNumericField("y calibration", yCalibration, 3);
		genericDialog.addNumericField("z calibration).", zCalibration, 3);
		genericDialog.addStringField("Unit", unit, 10);
		genericDialog.addRadioButtonGroup("Type of RHF ",
		                                  new String[]{"Volume and intensity", "Volume", "Intensity"},
		                                  1,
		                                  3,
		                                  "Volume and intensity");
		
		String nucleusParameters      = "Nucleus parameters";
		String chromocenterParameters = "Chromocenter parameters";
		String ncParameters           = "Nucleus and chromocenter parameters";
		genericDialog.addRadioButtonGroup("Type of results ",
		                                  new String[]{ncParameters,
		                                               chromocenterParameters,
		                                               nucleusParameters},
		                                  3,
		                                  1,
		                                  ncParameters);
		genericDialog.showDialog();
		if (genericDialog.wasCanceled()) {
			return;
		}
		ImagePlus imagePlusInput        = WindowManager.getImage(wList[genericDialog.getNextChoiceIndex()]);
		ImagePlus imagePlusSegmented    = WindowManager.getImage(wList[genericDialog.getNextChoiceIndex()]);
		ImagePlus imagePlusChromocenter = WindowManager.getImage(wList[genericDialog.getNextChoiceIndex()]);
		xCalibration = genericDialog.getNextNumber();
		yCalibration = genericDialog.getNextNumber();
		zCalibration = genericDialog.getNextNumber();
		String rhfChoice      = genericDialog.getNextRadioButton();
		String analysisChoice = genericDialog.getNextRadioButton();
		unit = genericDialog.getNextString();
		Calibration calibration = new Calibration();
		calibration.pixelDepth = zCalibration;
		calibration.pixelWidth = xCalibration;
		calibration.pixelHeight = yCalibration;
		calibration.setUnit(unit);
		imagePlusInput.setCalibration(calibration);
		imagePlusChromocenter.setCalibration(calibration);
		imagePlusSegmented.setCalibration(calibration);
		
		if (analysisChoice.equals(ncParameters)) {
			ChromocenterAnalysis.computeParametersChromocenter(imagePlusSegmented, imagePlusChromocenter);
			NucleusChromocentersAnalysis.computeParameters(rhfChoice,
			                                               imagePlusInput,
			                                               imagePlusSegmented,
			                                               imagePlusChromocenter);
		} else if (analysisChoice.equals(chromocenterParameters)) {
			ChromocenterAnalysis.computeParametersChromocenter(imagePlusSegmented, imagePlusChromocenter);
		} else {
			NucleusChromocentersAnalysis.computeParameters(rhfChoice,
			                                               imagePlusInput,
			                                               imagePlusSegmented,
			                                               imagePlusChromocenter);
		}
	}
	
}