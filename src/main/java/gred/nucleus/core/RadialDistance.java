package gred.nucleus.core;

import gred.nucleus.utils.Distance_Map;
import gred.nucleus.utils.Histogram;
import gred.nucleus.utils.VoxelRecord;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.plugin.Resizer;


/**
 * this class allows the determination of the radial distance of chromocenters, using the binary nucleus and the image
 * of segmented chromocenters.
 *
 * @author Tristan Dubos and Axel Poulet
 */
public final class RadialDistance {
	public RadialDistance() {
	}
	
	
	/**
	 * Method which compute the distance map of binary nucleus Rescale the voxel to obtain cubic voxel
	 *
	 * @param imagePlusSegmentedRescaled
	 *
	 * @return
	 */
	public static ImagePlus computeDistanceMap(ImagePlus imagePlusSegmentedRescaled) {
		Distance_Map distanceMap = new Distance_Map();
		distanceMap.apply(imagePlusSegmentedRescaled);
		return imagePlusSegmentedRescaled;
	}
	
	
	/**
	 * Compute the shortest distance between the chromocenter periphery and the nuclear envelope
	 *
	 * @param imagePlusSegmented
	 * @param imagePlusChromocenter
	 *
	 * @return
	 */
	public static double[] computeBorderToBorderDistances(ImagePlus imagePlusSegmented,
	                                                      ImagePlus imagePlusChromocenter) {
		Histogram histogram = new Histogram();
		histogram.run(imagePlusChromocenter);
		double[]    tLabel       = histogram.getLabels();
		Calibration calibration  = imagePlusSegmented.getCalibration();
		double      xCalibration = calibration.pixelWidth;
		imagePlusChromocenter = resizeImage(imagePlusChromocenter);
		ImageStack imageStackChromocenter = imagePlusChromocenter.getStack();
		imagePlusSegmented = resizeImage(imagePlusSegmented);
		ImagePlus  imagePlusDistanceMap  = computeDistanceMap(imagePlusSegmented);
		ImageStack imageStackDistanceMap = imagePlusDistanceMap.getStack();
		double     voxelValueMin, voxelValue;
		double[]   tDistanceRadial       = new double[tLabel.length];
		for (int l = 0; l < tLabel.length; ++l) {
			voxelValueMin = Double.MAX_VALUE;
			for (int k = 0; k < imagePlusChromocenter.getNSlices(); ++k) {
				for (int i = 0; i < imagePlusChromocenter.getWidth(); ++i) {
					for (int j = 0; j < imagePlusChromocenter.getHeight(); ++j) {
						voxelValue = imageStackDistanceMap.getVoxel(i, j, k);
						if (voxelValue < voxelValueMin &&
						    tLabel[l] ==
						    imageStackChromocenter.getVoxel(i, j, k)) {
							voxelValueMin = voxelValue;
						}
					}
				}
			}
			tDistanceRadial[l] = voxelValueMin * xCalibration;
		}
		return tDistanceRadial;
	}
	
	
	/**
	 * Determines the radial distance of all chromocenter in the image of nucleus We realise the distance map on the
	 * binary nucleus. This method measure the radial distance between the barycenter of chromocenter and the nuclear
	 * envelope.
	 *
	 * @param imagePlusSegmented
	 * @param imagePlusChromocenter
	 *
	 * @return
	 */
	public static double[] computeBarycenterToBorderDistances(ImagePlus imagePlusSegmented,
	                                                          ImagePlus imagePlusChromocenter) {
		Calibration calibration  = imagePlusSegmented.getCalibration();
		double      xCalibration = calibration.pixelWidth;
		ImagePlus imagePlusChromocenterRescale =
				resizeImage(imagePlusChromocenter);
		imagePlusSegmented = resizeImage(imagePlusSegmented);
		ImagePlus imagePlusDistanceMap =
				computeDistanceMap(imagePlusSegmented);
		ImageStack imageStackDistanceMap = imagePlusDistanceMap.getStack();
		Measure3D  measure3D             = new Measure3D();
		VoxelRecord[] tVoxelRecord = measure3D.computeObjectBarycenter(
				imagePlusChromocenterRescale, false);
		double[] tRadialDistance = new double[tVoxelRecord.length];
		double   distance;
		for (int i = 0; i < tVoxelRecord.length; ++i) {
			VoxelRecord voxelRecord = tVoxelRecord[i];
			distance = imageStackDistanceMap.getVoxel(
					(int) voxelRecord.i,
					(int) voxelRecord.j,
					(int) voxelRecord.k);
			tRadialDistance[i] = xCalibration * distance;
		}
		return tRadialDistance;
	}
	
	
	/**
	 * Resize the input image to obtain isotropic voxel
	 *
	 * @param imagePlus
	 *
	 * @return resized image
	 */
	private static ImagePlus resizeImage(ImagePlus imagePlus) {
		Resizer     resizer      = new Resizer();
		Calibration calibration  = imagePlus.getCalibration();
		double      xCalibration = calibration.pixelWidth;
		double      zCalibration = calibration.pixelDepth;
		double      zFactor      = zCalibration / xCalibration;
		int         newDepth     = (int) (imagePlus.getNSlices() * zFactor);
		return resizer.zScale(imagePlus, newDepth, 0);
	}
	
}