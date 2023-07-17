package gred.nucleus.imageprocessing;

import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;
import ij.process.*;


public final class Thresholding {
	
	
	private Thresholding() {
	}
	
	
	/**
	 * Compute the initial threshold value from OTSU method
	 *
	 * @param imagePlusInput raw image
	 *
	 * @return OTSU threshold
	 * <p> TODO STRUCTURES PROBABLY NEEDED
	 */
	public static int computeOTSUThreshold(ImagePlus imagePlusInput) {
		AutoThresholder autoThresholder = new AutoThresholder();
		ImageStatistics imageStatistics = new StackStatistics(imagePlusInput);
		int[]           tHistogram      = imageStatistics.histogram;
		return autoThresholder.getThreshold(AutoThresholder.Method.Otsu, tHistogram);
	}
	
	
	/**
	 * TODO COMMENT !!!! 2D 3D
	 *
	 * @param imagePlusInput
	 *
	 * @return
	 */
	public static ImagePlus contrastAnd8bits(ImagePlus imagePlusInput) {
		ContrastEnhancer enh = new ContrastEnhancer();
		enh.setNormalize(true);
		enh.setUseStackHistogram(true);
		enh.setProcessStack(true);
		enh.stretchHistogram(imagePlusInput, 0.05);
		StackStatistics statistics = new StackStatistics(imagePlusInput);
		imagePlusInput.setDisplayRange(statistics.min, statistics.max);
		
		if (imagePlusInput.getNSlices() > 1) { // 3D
			StackConverter stackConverter = new StackConverter(imagePlusInput);
			stackConverter.convertToGray8();
		} else { // 2D
			ImageConverter imageConverter = new ImageConverter(imagePlusInput);
			imageConverter.convertToGray8();
		}
		return imagePlusInput;
		
	}
	
}

