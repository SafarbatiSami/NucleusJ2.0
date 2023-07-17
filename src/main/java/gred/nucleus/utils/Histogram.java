package gred.nucleus.utils;

import ij.ImagePlus;
import ij.ImageStack;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;


/**
 * This class permit to obtain values who are on the Input image (8, 16 or 32 bits)
 *
 * @author Tristan Dubos and Axel Poulet
 */
public class Histogram {
	/** HashMap which stock the different values of voxels and the number of voxels for each value present on the image */
	private final TreeMap<Double, Integer> hHistogram = new TreeMap<>();
	/** All the value present on the image */
	private       double[]             label;
	/**
	 *
	 */
	private       double               labelMax   = -1;
	/**
	 *
	 */
	private       int                  nbLabel    = 0;
	
	
	/**
	 *
	 */
	public Histogram() {
	}
	
	
	/** @param imagePlusInput  */
	public void run(ImagePlus imagePlusInput) {
		Object[] tTemp = computeHistogram(imagePlusInput).keySet().toArray();
		label = new double[tTemp.length];
		for (int i = 0; i < tTemp.length; ++i) {
			label[i] = Double.parseDouble(tTemp[i].toString());
		}
		Arrays.sort(label);
		if (nbLabel > 0) {
			labelMax = label[label.length - 1];
		}
	}
	
	
	/**
	 * this method return a Histogram of the image input in hashMap form
	 *
	 * @param imagePlusInput
	 *
	 * @return
	 */
	private Map<Double, Integer> computeHistogram(ImagePlus imagePlusInput) {
		double     voxelValue;
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		for (int k = 0; k < imagePlusInput.getNSlices(); ++k) {
			for (int i = 0; i < imagePlusInput.getWidth(); ++i) {
				for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
					voxelValue = imageStackInput.getVoxel(i, j, k);
					if (voxelValue > 0) {
						if (hHistogram.containsKey(voxelValue)) {
							int nbVoxel = hHistogram.get(voxelValue);
							++nbVoxel;
							hHistogram.put(voxelValue, nbVoxel);
						} else {
							hHistogram.put(voxelValue, 1);
							++nbLabel;
						}
					}
				}
			}
		}
		return hHistogram;
	}
	
	
	/**
	 * this method return a double table which contain the all the value voxel present on the input image
	 *
	 * @return
	 */
	public double[] getLabels() {
		return label;
	}
	
	
	/** @return  */
	public TreeMap<Double, Integer> getHistogram() {
		return (TreeMap<Double, Integer>) hHistogram;
	}
	
	
	/** @return  */
	public double getLabelMax() {
		return labelMax;
	}
	
	
	/** @return  */
	public int getNbLabels() {
		return nbLabel;
	}
	
}
