package gred.nucleus.utils;

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.List;


/** @author Philippe Andrey, Tristan Dubos and Axel Poulet */
public class RegionalExtremaFilter implements PlugInFilter {
	/** image to process */
	private ImagePlus    imagePlusInput;
	/** table to stock the values of the local minima their coordinates x, y, z */
	private double[][][] localMinValues = null;
	/** table to stock the binary mask if is necessary */
	private double[][][] tabMask        = null;
	
	
	/**
	 * @param arg
	 * @param imagePlusInput
	 *
	 * @return
	 */
	public int setup(String arg, ImagePlus imagePlusInput) {
		this.imagePlusInput = imagePlusInput;
		return 0;
	}
	
	
	/**
	 * To run the plugin with imageJ
	 *
	 * @param imageProcessor
	 */
	public void run(ImageProcessor imageProcessor) {
		applyWithMask(imagePlusInput);
	}
	
	
	/**
	 * Method used to scan the deconvolved image, and the image having undergone the filter min in the binary mask of
	 * nucleus. For each voxel, the user can determine if the voxel value is a minima or belong to a minima region. All
	 * the minima regions retain a specific voxel value while the others => value = 0
	 *
	 * @param imagePlusInput
	 *
	 * @return
	 */
	public ImagePlus applyWithMask(ImagePlus imagePlusInput) {
		this.imagePlusInput = imagePlusInput;
		double            width          = this.imagePlusInput.getWidth();
		double            height         = this.imagePlusInput.getHeight();
		double            depth          = this.imagePlusInput.getStackSize();
		int               kCurrent;
		int               iCurrent;
		int               jCurrent;
		int               ii;
		int               jj;
		int               kk;
		VoxelRecord       voxelRecord    = new VoxelRecord();
		List<VoxelRecord> arrayListVoxel = new ArrayList<>();
		computeImageMoreOne();
		ImagePlus  imageOutput      = this.imagePlusInput.duplicate();
		ImageStack imageStackOutput = imageOutput.getStack();
		filterMin3DWithMask();
		for (int k = 0; k < depth; ++k) {
			for (int i = 0; i < width; ++i) {
				for (int j = 0; j < height; ++j) {
					double currentValue    = imageStackOutput.getVoxel(i, j, k);
					double currentValueMin = localMinValues[i][j][k];
					if (currentValue > 0 && currentValue != currentValueMin && tabMask[i][j][k] > 0) {
						imageStackOutput.setVoxel(i, j, k, 0);
						voxelRecord.setLocation(i, j, k);
						arrayListVoxel.add(voxelRecord);
						while (!arrayListVoxel.isEmpty()) {
							voxelRecord = arrayListVoxel.remove(0);
							iCurrent = (int) voxelRecord.getI();
							jCurrent = (int) voxelRecord.getJ();
							kCurrent = (int) voxelRecord.getK();
							for (kk = kCurrent - 1; kk <= kCurrent + 1; ++kk) {
								for (ii = iCurrent - 1; ii <= iCurrent + 1; ++ii) {
									for (jj = jCurrent - 1; jj <= jCurrent + 1; ++jj) {
										if (kk >= 0 &&
										    kk < depth &&
										    ii >= 0 &&
										    ii < width &&
										    jj >= 0 &&
										    jj < height &&
										    tabMask[ii][jj][kk] > 0) {
											if (imageStackOutput.getVoxel(ii, jj, kk) == currentValue) {
												imageStackOutput.setVoxel(ii, jj, kk, 0);
												voxelRecord.setLocation(ii, jj, kk);
												arrayListVoxel.add(voxelRecord);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		imageOutput.setTitle("minima_" + imagePlusInput.getTitle());
		return imageOutput;
	}
	
	
	/** Adds one at all the voxel values of a given image */
	public void computeImageMoreOne() {
		double     width           = imagePlusInput.getWidth();
		double     height          = imagePlusInput.getHeight();
		double     depth           = imagePlusInput.getStackSize();
		ImageStack imageStackInput = imagePlusInput.getStack();
		for (int k = 0; k < depth; ++k) {
			for (int i = 0; i < width; ++i) {
				for (int j = 0; j < height; ++j) {
					imageStackInput.setVoxel(i, j, k, imageStackInput.getVoxel(i, j, k) + 1);
				}
			}
		}
	}
	
	
	/** Filter minimum in 3D with a neighboring 3 */
	
	void filterMin3DWithMask() {
		int        size1           = imagePlusInput.getWidth();
		int        size2           = imagePlusInput.getHeight();
		int        size3           = imagePlusInput.getStackSize();
		ImageStack imageStackInput = imagePlusInput.getStack();
		int        ii;
		int        jj;
		int        kk;
		double     minValue;
		localMinValues = new double[size1][size2][size3];
		for (int k = 0; k < size3; ++k) {
			for (int i = 0; i < size1; ++i) {
				for (int j = 0; j < size2; ++j) {
					minValue = imageStackInput.getVoxel(i, j, k);
					for (ii = i - 1; ii <= i + 1; ++ii) {
						if (ii >= 0 && ii < size1) {
							for (jj = j - 1; jj <= j + 1; ++jj) {
								if (jj >= 0 && jj < size2) {
									for (kk = k - 1; kk <= k + 1; ++kk) {
										if (kk >= 0 && kk < size3) {
											if (imageStackInput.getVoxel(ii, jj, kk) < minValue &&
											    tabMask[i][j][k] > 0 &&
											    tabMask[ii][jj][kk] > 0) {
												minValue = imageStackInput.getVoxel(ii, jj, kk);
											}
										}
									}
								}
							}
						}
					}
					localMinValues[i][j][k] = minValue;
				}
			}
		}
	}
	
	
	/**
	 * Initialise a matrix of a binary mask to search the minima regions in the mask
	 *
	 * @param tab binary mask
	 */
	public void setMask(double[][][] tab) {
		tabMask = tab;
	}
	
	
	/**
	 * Initialise a matrix of a binary mask to search the minima regions in the mask
	 *
	 * @param imagePlusEntree Binary image
	 */
	public void setMask(ImagePlus imagePlusEntree) {
		ImageStack imagePlusLabel = imagePlusEntree.getStack();
		final int  size1          = imagePlusLabel.getWidth();
		final int  size2          = imagePlusLabel.getHeight();
		final int  size3          = imagePlusLabel.getSize();
		tabMask = new double[size1][size2][size3];
		for (int i = 0; i < size1; ++i) {
			for (int j = 0; j < size2; ++j) {
				for (int k = 0; k < size3; ++k) {
					tabMask[i][j][k] = imagePlusLabel.getVoxel(i, j, k);
				}
			}
		}
	}
	
}