package gred.nucleus.utils;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;


/**
 * Running a convex hull algorithm for each axis combined
 *
 * @author Tristan Dubos and Axel Poulet
 */
public class ConvexHullImageMaker {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	List<Double> listLabel;
	private String axesName = "";
	
	/**
	 * Run the convex hull algorithm on the image input for a given axe
	 *
	 * @param imagePlusBinary input imagePlus
	 * @return segmented image in axes concerned corrected by a convex hull algorithm
	 * @see gred.nucleus.core.ConvexHullSegmentation
	 */
	public ImagePlus runConvexHullDetection(ImagePlus imagePlusBinary) {
		LOGGER.debug("Computing convex hull algorithm for axes {}.", this.axesName);
		ImagePlus   imagePlusCorrected        = new ImagePlus();
		ImagePlus   imagePlusBlack            = new ImagePlus();
		int         depth;
		int         width;
		int         height;
		// Defining plan
		if (axesName.equals("xy")) {
			width = imagePlusBinary.getWidth();
			height = imagePlusBinary.getHeight();
			depth = imagePlusBinary.getNSlices();
		} else if (axesName.equals("xz")) {
			width = imagePlusBinary.getWidth();
			height = imagePlusBinary.getNSlices();
			depth = imagePlusBinary.getHeight();
		} else {
			width = imagePlusBinary.getHeight();
			height = imagePlusBinary.getNSlices();
			depth = imagePlusBinary.getWidth();
		}
		// Create 2D image used to create each slice (depth) of a plan
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		imagePlusBlack.setImage(bufferedImage);
		ImageStack imageStackOutput = new ImageStack(width, height);
		for (int k = 0; k < depth; ++k) {
			ImagePlus  ip    = imagePlusBlack.duplicate();
			double[][] image = giveTable(imagePlusBinary, width, height, k); // Return image with labelled components (& initialize listLabel)
			LOGGER.trace("Processing slice {}/{} of plan \"{}\"", k, depth, axesName);
			
			// Calculate boundaries
			if (listLabel.size() == 1) {  // If 1 single connected component
				//LOGGER.trace("Processing the only label {} on slice {}/{}", listLabel.get(0), k, depth);
				List<VoxelRecord> lVoxelBoundary = detectVoxelBoundary(image, listLabel.get(0), k); // List the voxels of boundary of the component
				if (lVoxelBoundary.size() > 5) { // When component is big enough
					// Create temporary image of the component using the convex hull detection algorithm
					ip = imageMaker(lVoxelBoundary, width, height);
				} else {
					ip = imagePlusBlack.duplicate();
				}
			}
			else if (listLabel.size() > 1) { // If several connected components
				ImageStack imageStackIp = ip.getImageStack();
				for (Double label : listLabel) {
					//LOGGER.trace("Processing label {} ({}/{}) on slice: {}/{}", label, listLabel.indexOf(label)+1, listLabel.size() , k, depth);
					List<VoxelRecord> lVoxelBoundary = detectVoxelBoundary(image, label, k); // List the voxels of boundary of the component
					if (lVoxelBoundary.size() > 5) { // When the component is big enough make image
						// Create temporary image of the component using the convex hull detection algorithm
						ImageStack imageTempStack = imageMaker(lVoxelBoundary, width, height).getStack();
						
						for (int l = 0; l < width; ++l) { // For each labelled voxels of the component put a corresponding white voxel on the result
							for (int m = 0; m < height; ++m) {
								if (imageTempStack.getVoxel(l, m, 0) > 0) {
									imageStackIp.setVoxel(l, m, 0, 255);
								}
							}
						}
					}
				}
			}
			else { // In case nothing is found return black image
				ip = imagePlusBlack.duplicate();
			}
			imageStackOutput.addSlice(ip.getProcessor()); // Add the image to the result
		}
		imagePlusCorrected.setStack(imageStackOutput);
		return imagePlusCorrected;
	}
		
	/**
	 * Find all the voxels of the boundaries (near black pixels)
	 *
	 * @param image image used
	 * @param label current label
	 * @param indice slice indice
	 *
	 * @return list of the voxels of the boundaries
	 */
	List<VoxelRecord> detectVoxelBoundary(double[][] image, double label, int indice) {
		LOGGER.trace("Detecting voxel boundary.");
		List<VoxelRecord> lVoxelBoundary = new ArrayList<>();
		// Browse through the pixels of the 2D image
		for (int i = 1; i < image.length; ++i) {
			for (int j = 1; j < image[i].length; ++j) {
				if (image[i][j] == label) {
					if (image[i - 1][j] == 0 || image[i + 1][j] == 0 || image[i][j - 1] == 0 || image[i][j + 1] == 0) {
						VoxelRecord voxelTest = new VoxelRecord();
						if (axesName.equals("xy")) {
							voxelTest.setLocation(i, j, indice);
						} else if (axesName.equals("xz")) {
							voxelTest.setLocation(i, indice, j);
						} else {
							voxelTest.setLocation(indice, i, j);
						}
						lVoxelBoundary.add(voxelTest);
						}
					}
				}
			}
		return lVoxelBoundary;
	}
	
	
	/**
	 * Make image plus of the convex hull detection result
	 *
	 * @param lVoxelBoundary voxels of the boundaries
	 * @param width slice width
	 * @param height slice height
	 *
	 * @return ImagePlus result
	 */
	public ImagePlus imageMaker(List<VoxelRecord> lVoxelBoundary,
	                            int width,
	                            int height) {
		LOGGER.trace("Making image.");
		
		List<VoxelRecord> convexHull = ConvexHullDetection.runGrahamScan(axesName, lVoxelBoundary); // For testing
		return makePolygon(convexHull, width, height);
	}
	
	
	/**
	 * Connect all result voxels of the convex hull and create an image plus
	 *
	 * @param convexHull voxel of the convex hull
	 * @param width slice width
	 * @param height slice height
	 *
	 * @return ImagePlus result
	 */
	public ImagePlus makePolygon(List<VoxelRecord> convexHull, int width, int height) {
		ImagePlus     ip            = new ImagePlus();
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		int[]         tableWidth    = new int[convexHull.size() + 1];
		int[]         tableHeight   = new int[convexHull.size() + 1];
		for (int i = 0; i < convexHull.size(); ++i) {
			switch (axesName) {
				case "xy":
					tableWidth[i] = (int) convexHull.get(i).i;
					tableHeight[i] = (int) convexHull.get(i).j;
					break;
				case "xz":
					tableWidth[i] = (int) convexHull.get(i).i;
					tableHeight[i] = (int) convexHull.get(i).k;
					break;
				case "yz":
					tableWidth[i] = (int) convexHull.get(i).j;
					tableHeight[i] = (int) convexHull.get(i).k;
					break;
			}
		}
		
		switch (axesName) {
			case "xy":
				tableWidth[convexHull.size()] = (int) convexHull.get(0).i;
				tableHeight[convexHull.size()] = (int) convexHull.get(0).j;
				break;
			case "xz":
				tableWidth[convexHull.size()] = (int) convexHull.get(0).i;
				tableHeight[convexHull.size()] = (int) convexHull.get(0).k;
				break;
			case "yz":
				tableWidth[convexHull.size()] = (int) convexHull.get(0).j;
				tableHeight[convexHull.size()] = (int) convexHull.get(0).k;
				break;
		}
		
		ip.setImage(bufferedImage);
		ip.getProcessor().setValue(255);
		ip.getProcessor().fill(new PolygonRoi(tableWidth, tableHeight, tableWidth.length, Roi.POLYGON));
		return ip;
	}
	
	
	/**
	 * Label all the connected components (set of voxels) found on the slice
	 *
	 * @param imagePlusInput stack
	 * @param width width of the slice
	 * @param height height of the slice
	 * @param index index of the slice
	 *
	 * @return list of label
	 */
	double[][] giveTable(ImagePlus imagePlusInput, int width, int height, int index) {
		ImageStack imageStackInput = imagePlusInput.getStack();
		double[][] image           = new double[width][height];
		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
				if (axesName.equals("xy")) {
					image[i][j] = imageStackInput.getVoxel(i, j, index);
				} else if (axesName.equals("xz")) {
					image[i][j] = imageStackInput.getVoxel(i, index, j);
				} else {
					image[i][j] = imageStackInput.getVoxel(index, i, j);
				}
			}
		}
		ConnectedComponents connectedComponents = new ConnectedComponents();
		connectedComponents.setImageTable(image);
		listLabel = connectedComponents.getListLabel(255); // One label per connected components
		image = connectedComponents.getImageTable(); // Return the image where all connected components have a different label (value)
		return image;
	}
	
	
	/**
	 * Return current combined axis analysing
	 *
	 * @return current combined axis  analysing
	 */
	public String getAxes() {
		return axesName;
	}
	
	
	/**
	 * Set the current combined axis analysing
	 *
	 * @param axes Current combined axis analysing
	 */
	public void setAxes(String axes) {
		axesName = axes;
	}
	
}