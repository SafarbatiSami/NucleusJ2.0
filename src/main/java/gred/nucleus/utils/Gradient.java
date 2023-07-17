package gred.nucleus.utils;

import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;

import java.util.ArrayList;
import java.util.List;


/** @author Tristan Dubos and Axel Poulet */
public class Gradient {
	/**
	 *
	 */
	private final List<Double>[][][] tableGradient;
	/**
	 *
	 */
	private final ArrayList<Double>[][][] tableUnitNormals;
	private ArrayList <Double> _tableUnitaire [][][] = null;
	
	
	@SuppressWarnings("unchecked")
	public Gradient(ImagePlus imagePlusInput) {
		int w = imagePlusInput.getWidth();
		int h = imagePlusInput.getHeight();
		int d = imagePlusInput.getStackSize();
		tableGradient = new ArrayList[w][h][d];
		tableUnitNormals = new ArrayList[w][h][d];
		computeGradient(imagePlusInput);
	}
	
	
	/**
	 * @param imagePlusInput
	 *
	 * @return
	 */
	private void computeGradient(ImagePlus imagePlusInput) {
		Calibration calibration     = imagePlusInput.getCalibration();
		ImageStack  imageStackInput = imagePlusInput.getStack();
		double      xCalibration    = calibration.pixelWidth;
		double      yCalibration    = calibration.pixelHeight;
		double      zCalibration    = calibration.pixelDepth;
		for (int k = 1; k < imagePlusInput.getStackSize() - 1; ++k) {
			for (int i = 1; i < imagePlusInput.getWidth() - 1; ++i) {
				for (int j = 1; j < imagePlusInput.getHeight() - 1; ++j) {
					List<Double> list = new ArrayList<>();
					double       dx   = 0;
					double       dy   = 0;
					double       dz   = 0;
					if (k - 1 > 1 || j - 1 > 1 || i - 1 > 1 || k + 1 < imagePlusInput.getStackSize() - 1 ||
					    j + 1 < imagePlusInput.getHeight() - 1 || i + 1 < imagePlusInput.getWidth() - 1) {
						dx = (1 / xCalibration) *
						     ((imageStackInput.getVoxel(i + 1, j, k) - imageStackInput.getVoxel(i - 1, j, k)) / 2);
						dy = (1 / yCalibration) *
						     ((imageStackInput.getVoxel(i, j + 1, k) - imageStackInput.getVoxel(i, j - 1, k)) / 2);
						dz = (1 / zCalibration) *
						     ((imageStackInput.getVoxel(i, j, k + 1) - imageStackInput.getVoxel(i, j, k - 1)) / 2);
					}
					list.add(dx);
					list.add(dy);
					list.add(dz);
					tableGradient[i][j][k] = list;
					/* temp variable */
					double norm = Math.sqrt(dx * dx + dy * dy + dz * dz);
					double nx   = 0;
					double ny   = 0;
					double nz   = 0;
					if (norm > 1e-15) {
						nx = dx / norm;
						ny = dy / norm;
						nz = dz / norm;
					}
					List<Double> listN = new ArrayList<>();
					listN.add(nx);
					listN.add(ny);
					listN.add(nz);
					tableUnitNormals[i][j][k] = (ArrayList<Double>) listN;
				}
			}
		}
		// IJ.log("fin Gradient");
	}
	
	
	/** @return  */
	public List<Double>[][][] getUnitNormals() {
		return tableUnitNormals;
	}
	
	
	/** @return  */
	public List<Double>[][][] getGradient() {
		return tableGradient;
	}
	
	
	public ArrayList <Double> [][][] getUnitaire () {
		return tableUnitNormals;
	}
}