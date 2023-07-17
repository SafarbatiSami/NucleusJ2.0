package gred.nucleus.utils;

import java.util.ArrayList;
import java.util.List;


public class ConnectedComponents {
	private final List<Double> listLabel = new ArrayList<>();
	private       double[][]   image;
	private       String       axesName;
	
	
	/**
	 * Iterates over the image pixels and look for these connected components
	 *
	 * @param labelIni
	 */
	void computeLabel(double labelIni) {
		int currentLabel = 2;
		// Iterate over the image pixels
		for (int i = 0; i < image.length; ++i) {
			for (int j = 0; j < image[i].length; ++j) {
				if (image[i][j] == labelIni) {
					image[i][j] = currentLabel;
					VoxelRecord voxelRecord = new VoxelRecord();
					voxelRecord.setLocation(i, j, 0);
					breadthFirstSearch(labelIni, voxelRecord, currentLabel);
					listLabel.add((double) currentLabel);
					currentLabel++;
				}
			}
		}
	}
	
	
	/**
	 * @param labelIni
	 * @param voxelRecord
	 * @param currentLabel
	 */
	private void breadthFirstSearch(double labelIni, VoxelRecord voxelRecord, int currentLabel) {
		List<VoxelRecord> voxelBoundary = detectVoxelBoundary(labelIni);
		voxelBoundary.add(0, voxelRecord);
		image[(int) voxelRecord.i][(int) voxelRecord.j] = currentLabel;
		while (!voxelBoundary.isEmpty()) {
			VoxelRecord voxelRemove = voxelBoundary.remove(0);
			for (int ii = (int) voxelRemove.i - 1; ii <= (int) voxelRemove.i + 1; ii++) {
				for (int jj = (int) voxelRemove.j - 1; jj <= (int) voxelRemove.j + 1; jj++) {
					if (ii >= 0 && ii <= image.length - 1 && jj >= 0 && jj <= image[0].length - 1) {
						if (ii > 0 && ii < image.length - 1 && jj > 0 && jj < image[0].length - 1) {
							if (image[ii][jj] == labelIni &&
							    (image[ii - 1][jj] == currentLabel ||
							     image[ii + 1][jj] == currentLabel ||
							     image[ii][jj - 1] == currentLabel ||
							     image[ii][jj + 1] == currentLabel)) {
								image[ii][jj] = currentLabel;
								VoxelRecord voxel = new VoxelRecord();
								voxel.setLocation(ii, jj, 0);
								voxelBoundary.add(0, voxel);
							}
						} else if (ii == 0) {
							if (jj == 0) {
								if (image[ii][jj] == labelIni &&
								    (image[ii + 1][jj] == currentLabel || image[ii][jj + 1] == currentLabel)) {
									image[ii][jj] = currentLabel;
									VoxelRecord voxel = new VoxelRecord();
									voxel.setLocation(ii, jj, 0);
									voxelBoundary.add(0, voxel);
								}
							} else if (jj == image[0].length - 1) {
								if (image[ii][jj] == labelIni &&
								    (image[ii + 1][jj] == currentLabel || image[ii][jj - 1] == currentLabel)) {
									image[ii][jj] = currentLabel;
									VoxelRecord voxel = new VoxelRecord();
									voxel.setLocation(ii, jj, 0);
									voxelBoundary.add(0, voxel);
								}
							} else {
								if (image[ii][jj] == labelIni &&
								    (image[ii + 1][jj] == currentLabel ||
								     image[ii][jj - 1] == currentLabel ||
								     image[ii][jj + 1] == currentLabel)) {
									image[ii][jj] = currentLabel;
									VoxelRecord voxel = new VoxelRecord();
									voxel.setLocation(ii, jj, 0);
									voxelBoundary.add(0, voxel);
								}
							}
						} else if (ii == image.length - 1) {
							if (jj == 0) {
								if (image[ii][jj] == labelIni &&
								    (image[ii - 1][jj] == currentLabel || image[ii][jj + 1] == currentLabel)) {
									image[ii][jj] = currentLabel;
									VoxelRecord voxel = new VoxelRecord();
									voxel.setLocation(ii, jj, 0);
									voxelBoundary.add(0, voxel);
								}
							} else if (jj == image[0].length - 1) {
								if (image[ii][jj] == labelIni &&
								    (image[ii - 1][jj] == currentLabel || image[ii][jj - 1] == currentLabel)) {
									image[ii][jj] = currentLabel;
									VoxelRecord voxel = new VoxelRecord();
									voxel.setLocation(ii, jj, 0);
									voxelBoundary.add(0, voxel);
								}
							} else {
								if (image[ii][jj] == labelIni &&
								    (image[ii - 1][jj] == currentLabel ||
								     image[ii][jj - 1] == currentLabel ||
								     image[ii][jj + 1] == currentLabel)) {
									image[ii][jj] = currentLabel;
									VoxelRecord voxel = new VoxelRecord();
									voxel.setLocation(ii, jj, 0);
									voxelBoundary.add(0, voxel);
								}
							}
						} else if (jj == 0) {
							if (image[ii][jj] == labelIni &&
							    (image[ii - 1][jj] == currentLabel ||
							     image[ii + 1][jj] == currentLabel ||
							     image[ii][jj + 1] == currentLabel)) {
								image[ii][jj] = currentLabel;
								VoxelRecord voxel = new VoxelRecord();
								voxel.setLocation(ii, jj, 0);
								voxelBoundary.add(0, voxel);
							}
						} else if (jj == image[0].length - 1 &&
						           image[ii][jj] == labelIni &&
						           (image[ii - 1][jj] == currentLabel ||
						            image[ii + 1][jj] == currentLabel ||
						            image[ii][jj - 1] == currentLabel)) {
							image[ii][jj] = currentLabel;
							VoxelRecord voxel = new VoxelRecord();
							voxel.setLocation(ii, jj, 0);
							voxelBoundary.add(0, voxel);
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * @param label
	 *
	 * @return
	 */
	private List<VoxelRecord> detectVoxelBoundary(double label) {
		List<VoxelRecord> lVoxelBoundary = new ArrayList<>();
		for (int i = 0; i < image.length; ++i) {
			for (int j = 0; j < image[i].length; ++j) {
				if (image[i][j] == label) {
					if (i > 0 && i < image.length - 1 && j > 0 && j < image[i].length - 1) {
						if (image[i - 1][j] == 0 ||
						    image[i + 1][j] == 0 ||
						    image[i][j - 1] == 0 ||
						    image[i][j + 1] == 0) {
							VoxelRecord voxelTest = new VoxelRecord();
							voxelTest.setLocation(i, j, 0);
							lVoxelBoundary.add(voxelTest);
						}
					} else if (i == 0) {
						if (j == 0) {
							if (image[i + 1][j] == 0 || image[i][j + 1] == 0) {
								VoxelRecord voxelTest = new VoxelRecord();
								voxelTest.setLocation(i, j, 0);
								lVoxelBoundary.add(voxelTest);
							}
						} else if (j == image[0].length - 1) {
							if (image[i + 1][j] == 0 || image[i][j - 1] == 0) {
								VoxelRecord voxelTest = new VoxelRecord();
								voxelTest.setLocation(i, j, 0);
								lVoxelBoundary.add(voxelTest);
							}
						} else {
							if (image[i + 1][j] == 0 || image[i][j - 1] == 0 || image[i][j + 1] == 0) {
								VoxelRecord voxelTest = new VoxelRecord();
								voxelTest.setLocation(i, j, 0);
								lVoxelBoundary.add(voxelTest);
							}
						}
					} else if (i == image.length - 1) {
						if (j == 0) {
							if (image[i - 1][j] == 0 || image[i][j + 1] == 0) {
								VoxelRecord voxelTest = new VoxelRecord();
								voxelTest.setLocation(i, j, 0);
								lVoxelBoundary.add(voxelTest);
							}
						} else if (j == image[0].length - 1) {
							if (image[i - 1][j] == 0 || image[i][j - 1] == 0) {
								VoxelRecord voxelTest = new VoxelRecord();
								voxelTest.setLocation(i, j, 0);
								lVoxelBoundary.add(voxelTest);
							}
						} else {
							if (image[i - 1][j] == 0 || image[i][j - 1] == 0 || image[i][j + 1] == 0) {
								VoxelRecord voxelTest = new VoxelRecord();
								voxelTest.setLocation(i, j, 0);
								lVoxelBoundary.add(voxelTest);
							}
						}
					} else if (j == 0) {
						if (image[i - 1][j] == 0 || image[i + 1][j] == 0 || image[i][j + 1] == 0) {
							VoxelRecord voxelTest = new VoxelRecord();
							voxelTest.setLocation(i, j, 0);
							lVoxelBoundary.add(voxelTest);
						}
					} else if (j == image[0].length - 1) {
						if (image[i - 1][j] == 0 || image[i + 1][j] == 0 || image[i][j - 1] == 0) {
							VoxelRecord voxelTest = new VoxelRecord();
							voxelTest.setLocation(i, j, 0);
							lVoxelBoundary.add(voxelTest);
						}
					}
				}
			}
		}
		return lVoxelBoundary;
	}
	
	
	/**
	 * @param labelIni
	 *
	 * @return
	 */
	public List<Double> getListLabel(double labelIni) {
		computeLabel(labelIni);
		return listLabel;
	}
	
	
	/** @return  */
	public double[][] getImageTable() {
		return image;
	}
	
	
	/** @param image  */
	public void setImageTable(double[][] image) {
		this.image = image;
	}
	
	
	/**
	 * @param initialLabel
	 * @param voxelRecord
	 *
	 * @return
	 */
	double[][] computeLabelOfOneObject(int initialLabel, VoxelRecord voxelRecord) {
		int currentLabel = 2;
		/*IJ.log("" + getClass().getName() + " L-" + new Exception().getStackTrace()[0].getLineNumber() + " \n" +
		       "start " + initialLabel + "\n" +
		       "j " + voxelRecord.j + "\n" +
		       "i " + voxelRecord.i + " \n "
		       + currentLabel + "\n" +
		       " euu " + image[(int) voxelRecord.i][(int) voxelRecord.j] + " end");*/
		breadthFirstSearch(initialLabel, voxelRecord, currentLabel);
		return image;
	}
	
	
	/**
	 * @param label
	 *
	 * @return
	 */
	public List<VoxelRecord> getBoundaryVoxel(int label) {
		return detectVoxelBoundary(label);
	}
	
}