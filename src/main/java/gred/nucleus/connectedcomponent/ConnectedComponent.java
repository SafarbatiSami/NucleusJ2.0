/*
 * Copyright (C) 2016 by Rémy Malgouyres
 * http://malgouyres.org
 * File: ConnectedComponent.java
 *
 * The program is distributed under the terms of the GNU General Public License *
 *
 */

package gred.nucleus.connectedcomponent;

import gred.nucleus.componentremoval.ComponentRemovalNone;
import gred.nucleus.componentremoval.ComponentRemovalPredicate;
import gred.nucleus.utils.Voxel;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;


/**
 * Allows connected components labeling in a binary image. This abstract class has derived classes to label components
 * in 2D or 3D images. Some filtering criteria for filtering the connected components are supported :
 * <ul>
 * 	<li>Minimal volume for the components (taking into account the calibration)</li>
 * 	<li>Remove connected components that touch the boundary of the image.</li>
 * </ul>
 * An option in the filtering method allows to modify the input image to set random grey levels on each component.
 *
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 */
public abstract class ConnectedComponent {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	/**
	 * Input Image. The image can be modified by the filtering in the filterComponents() method
	 */
	protected ImagePlus inputImage;
	
	/** Color of the foreground in the input binary image */
	protected int foregroundColor;
	
	/** Components Information (cardinality, label, voxel representative, etc.) */
	protected List<ComponentInfo> compInfo;
	
	/** Array containing the label of each voxel */
	protected int[][][] labels;
	
	/** Volume of a voxel (used for component size thresholding) */
	protected double voxelVolume;
	
	
	/**
	 * Initializes the fields of this instance (to be called in derived classes constructors)
	 *
	 * @param inputImage      Input (probably binary) image, the components of which to compute.
	 * @param foregroundColor label of the 1's in the input image ip
	 */
	protected ConnectedComponent(ImagePlus inputImage, int foregroundColor) {
		this.inputImage = inputImage;
		Calibration cal = this.inputImage.getCalibration();
		this.voxelVolume = cal.pixelDepth * cal.pixelWidth * cal.pixelHeight;
		
		//LOGGER.debug("vol vx{}", voxelVolume);
		this.foregroundColor = foregroundColor;
		this.labels =
				new int[this.inputImage.getWidth()][this.inputImage.getHeight()][this.inputImage.getNSlices()];
		for (int i = 0; i < this.inputImage.getWidth(); i++) {
			for (int j = 0; j < this.inputImage.getHeight(); j++) {
				for (int k = 0; k < this.inputImage.getNSlices(); k++) {
					this.labels[i][j][k] = 0;
				}
			}
		}
		this.compInfo = new ArrayList<>();
	}
	
	
	/**
	 * Constructs a ConnectedComponent derived class instance with relevant dimension (2D or 3D). the connected
	 * components are not labeled. Please call doConnectedComponent().
	 *
	 * @param inputImage      Input (probably binary) image, the components of which to compute.
	 * @param foregroundColor label of the 1's in the input image inputImage
	 *
	 * @return an instance of a concrete derived class for ConnectedComponent
	 */
	public static ConnectedComponent getConnectedComponent(ImagePlus inputImage, int foregroundColor) {
		if (inputImage.getNSlices() <= 1) {
			return new ConnectedComponent2D(inputImage, foregroundColor);
		}
		
		return new ConnectedComponent3D(inputImage, foregroundColor);
	}
	
	
	/**
	 * Constructs a ConnectedComponent derived class instance with relevant dimension (2D or 3D) and labels the
	 * components. Filters the image components according to two criteria:
	 * <ul>
	 *    <li>Possibly remove the components which are on the edge of the image</li>
	 *    <li>Possibly remove the components with size bellow some threshold</li>
	 * </ul>
	 *
	 * @param inputImage            Input (probably binary) image, the components of which to compute.
	 * @param foregroundColor       label of the 1's in the input image inputImage
	 * @param removeBorderComponent true if the components which are on the edge of the image should be removed by
	 *                              filtering
	 * @param thresholdVoxelVolume  minimal volume for filtering (taking into account the calibration) for the
	 *                              components. 0 if no minimal volume is required
	 * @param setRandomColors       true if the colors of the original image should be set according to the components
	 *                              labels.
	 *
	 * @return an instance of a concrete derived class for ConnectedComponent
	 *
	 * @throws Exception in case the number of connected components exceeds the Short.MAX_VALUE (32767)
	 */
	public static ConnectedComponent getLabelledConnectedComponent(ImagePlus inputImage,
	                                                               int foregroundColor,
	                                                               boolean removeBorderComponent,
	                                                               double thresholdVoxelVolume,
	                                                               boolean setRandomColors) throws Exception {
		ConnectedComponent cc;
		if (inputImage.getNSlices() <= 1) {
			cc = new ConnectedComponent2D(inputImage, foregroundColor);
		} else {
			cc = new ConnectedComponent3D(inputImage, foregroundColor);
		}
		cc.doLabelConnectedComponent();
		cc.filterComponents(removeBorderComponent, thresholdVoxelVolume, setRandomColors);
		return cc;
	}
	
	
	/**
	 * Constructs a ConnectedComponent derived class instance with relevant dimension (2D or 3D) and labels the
	 * components. Filters the image components according to two criteria:
	 * <ul>
	 *    <li>Possibly remove the components which are on the edge of the image</li>
	 *    <li>Possibly remove the components with size bellow some threshold</li>
	 * </ul>
	 *
	 * @param inputImage            Input (probably binary) image, the components of which to compute.
	 * @param foregroundColor       label of the 1's in the input image inputImage
	 * @param removeBorderComponent true if the components which are on the edge of the image should be removed by
	 *                              filtering
	 * @param thresholdVoxelVolume  minimal volume for filtering (taking into account the calibration) for the
	 *                              components. 0 if no minimal volume is required
	 * @param removalPredicate      a predicate according to which components should be filtered out
	 * @param keepPredicate         true if we should keep the components with a voxel satisfying removalPredicate, and
	 *                              false if we should remove the components with a voxel satisfying removalPredicate
	 * @param setRandomColors       true if the colors of the original image should be set according to the components
	 *                              labels.
	 *
	 * @return an instance of a concrete derived class for ConnectedComponent
	 *
	 * @throws Exception in case the number of connected components exceeds the Short.MAX_VALUE (32767)
	 */
	public static ConnectedComponent getLabelledConnectedComponent(ImagePlus inputImage,
	                                                               int foregroundColor,
	                                                               boolean removeBorderComponent,
	                                                               double thresholdVoxelVolume,
	                                                               ComponentRemovalPredicate removalPredicate,
	                                                               boolean keepPredicate,
	                                                               boolean setRandomColors) throws Exception {
		ConnectedComponent cc;
		if (inputImage.getNSlices() <= 1) {
			cc = new ConnectedComponent2D(inputImage, foregroundColor);
		} else {
			cc = new ConnectedComponent3D(inputImage, foregroundColor);
		}
		cc.doLabelConnectedComponent();
		cc.filterComponents(removeBorderComponent,
		                    thresholdVoxelVolume,
		                    removalPredicate,
		                    keepPredicate,
		                    setRandomColors);
		return cc;
	}
	
	
	/**
	 * retrieves the label of a voxel (after calling doComponents)
	 *
	 * @param x first coordinate of the pixel
	 * @param y second coordinate of the pixel
	 *
	 * @return the label of the input voxel (0 if not in any connected component)
	 */
	public int getLabel(int x, int y) {
		return this.labels[x][y][0];
	}
	
	
	/**
	 * retrieves the label of a voxel (after calling doComponents)
	 *
	 * @param x first coordinate of the voxel
	 * @param y second coordinate of the voxel
	 * @param z third coordinate of the voxel
	 *
	 * @return the label of the input voxel (0 if not in any connected component)
	 */
	public int getLabel(int x, int y, int z) {
		return this.labels[x][y][z];
	}
	
	
	/**
	 * retrieves the label of a voxel (after calling doComponents)
	 *
	 * @param x     first coordinate of the voxel
	 * @param y     second coordinate of the voxel
	 * @param z     third coordinate of the voxel
	 * @param label the label of the input voxel (0 if not in any connected component)
	 */
	protected void setLabel(int x, int y, int z, int label) {
		this.labels[x][y][z] = label;
	}
	
	
	/**
	 * Retrieves the information about a component for its label may return null if the component does not exist or has
	 * been erased because below volume threshold or on the border.
	 *
	 * @param label the component label
	 *
	 * @return the ComponentInfo instance of the component with the considered label. returns null if the component info
	 * is undefined
	 */
	public ComponentInfo getComponentInfo(short label) {
		try {
			ComponentInfo ci = this.compInfo.get(label - 1);
			if (ci.getNumberOfPoints() == 0) {
				return null;
			}
			return ci;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * Retrieve a collection of voxels, with one voxel in each (possibly filtered) connected component.
	 *
	 * @return the array of voxel representatives of components
	 */
	public List<Voxel> getVoxelRepresentants() {
		List<Voxel> tabVoxels = new ArrayList<>();
		for (ComponentInfo ci : this.compInfo) {
			if (ci.getNumberOfPoints() > 0) {
				tabVoxels.add(ci.getRepresentant());
			}
		}
		return tabVoxels;
	}
	
	
	/**
	 * labels the connected components of the input image (attribute this.ip)
	 *
	 * @throws Exception in case the number of connected components exceeds the Short.MAX_VALUE (32767)
	 */
	abstract void doLabelConnectedComponent() throws Exception;
	
	
	/**
	 * Filters the image components according to two criteria:
	 * <ul>
	 *    <li>Possibly remove the components which are on the edge of the image</li>
	 *    <li>Possibly remove the components with size bellow some threshold</li>
	 *    <li>Possibly keep (or remove) the components with a voxel satisfying a predicate</li>
	 * </ul>
	 *
	 * @param removeBorderComponent    true if the components which are on the edge of the image should be removed by
	 *                                 filtering
	 * @param thresholdComponentVolume minimal volume for filtering (taking into account the calibration) for the
	 *                                 components. 0 if no minimal volume is required
	 * @param setRandomColors          true if the colors of the original image should be set according to the
	 *                                 components labels.
	 */
	protected void filterComponents(boolean removeBorderComponent,
	                                double thresholdComponentVolume,
	                                boolean setRandomColors) {
		filterComponents(removeBorderComponent,
		                 thresholdComponentVolume,
		                 new ComponentRemovalNone(),
		                 true,
		                 setRandomColors);
	}
	
	
	/**
	 * Filters the image components according to two criteria:
	 * <ul>
	 *    <li>Possibly remove the components which are on the edge of the image</li>
	 *    <li>Possibly remove the components with size bellow some threshold</li>
	 *    <li>Possibly keep (or remove) the components with a voxel satisfying a predicate</li>
	 * </ul>
	 *
	 * @param removeBorderComponent    true if the components which are on the edge of the image should be removed by
	 *                                 filtering
	 * @param thresholdComponentVolume minimal volume for filtering (taking into account the calibration) for the
	 *                                 components. 0 if no minimal volume is required
	 * @param removalPredicate         a predicate according to which components should be filtered out
	 * @param keepPredicate            true if we should keep only the components with at least one voxel satisfying
	 *                                 removalPredicate, and false if we should remove the components with at least one
	 *                                 voxel satisfying removalPredicate
	 * @param setRandomColors          true if the colors of the original image should be set according to the
	 *                                 components labels.
	 */
	protected void filterComponents(boolean removeBorderComponent,
	                                double thresholdComponentVolume,
	                                ComponentRemovalPredicate removalPredicate,
	                                boolean keepPredicate,
	                                boolean setRandomColors) {
		LOGGER.debug("Là, on des compO : {}", this.voxelVolume);
		
		List<Boolean> existsVoxelSatisfyingPredicate = new ArrayList<>();
		for (int i = 0; i < this.compInfo.size(); ++i) {
			existsVoxelSatisfyingPredicate.add(Boolean.FALSE);
		}
		
		// Check the predicate
		Voxel voxelToTest = new Voxel();
		for (voxelToTest.setX((short) 0);
		     voxelToTest.getX() < this.inputImage.getWidth();
		     voxelToTest.incrementCoordinate(0)) {
			for (voxelToTest.setY((short) 0);
			     voxelToTest.getY() < this.inputImage.getHeight();
			     voxelToTest.incrementCoordinate(1)) {
				for (voxelToTest.setZ((short) 0);
				     voxelToTest.getZ() < this.inputImage.getNSlices();
				     voxelToTest.incrementCoordinate(2)) {
					// get the voxel's label
					int label = getLabel(voxelToTest.getX(), voxelToTest.getY(), voxelToTest.getZ());
					if (label > 0) { // if not a background voxel
						ComponentInfo ci = this.compInfo.get(label - 1);
						// test the predicate
						if (removalPredicate.keepVoxelComponent(voxelToTest, ci)) {
							existsVoxelSatisfyingPredicate.set(label - 1, Boolean.TRUE);
						}
					}
				}
			}
		}
		
		// if the keep predicate is true for at least one voxel
		// and we should remove
		// the components with a voxel satisfying removalPredicate 
		// or
		// if the keep predicate is false for all the voxels
		// and we should keep only
		// the components with a voxel satisfying removalPredicate 
		for (int i = 0; i < this.compInfo.size(); ++i) {
			if (((!existsVoxelSatisfyingPredicate.get(i)) && keepPredicate) ||
			    (existsVoxelSatisfyingPredicate.get(i) && !keepPredicate))
			// remove the component
			{
				this.compInfo.get(i).setNumberOfPoints(0);
			}
		}
		
		int                 thresholdNVoxel  = (int) (thresholdComponentVolume / this.voxelVolume);
		List<Integer>       newLabels        = new ArrayList<>(this.compInfo.size());
		List<ComponentInfo> newTabComponents = new ArrayList<>();
		short               componentsCount  = 0;
		// For each label
		for (int label = 1; label <= this.compInfo.size(); label++) {
			ComponentInfo ci = this.compInfo.get(label - 1);
			// If the component survives the filtering criteria
			if (ci != null &&
			    ci.getNumberOfPoints() > 0 &&
			    ci.getNumberOfPoints() >= thresholdNVoxel &&
			    ((!removeBorderComponent) || !ci.isOnTheBorder())) {
				componentsCount++;
				// old label/new label correspondence
				newLabels.add((int) componentsCount);
				// register the component in the final array
				newTabComponents.add(ci);
			} else if (ci != null) {
				ci.setNumberOfPoints(0);
				newLabels.add(0);
			}
		}
		List<Double> componentsColors = new ArrayList<>(newTabComponents.size());
		for (int i = 0; i < newTabComponents.size(); i++) {
			componentsColors.add(100 + Math.random() * (255 - 100));
		}
		ImageStack imgP = inputImage.getStack();
		for (int i = 0; i < this.inputImage.getWidth(); ++i) {
			for (int j = 0; j < this.inputImage.getHeight(); ++j) {
				for (int k = 0; k < this.inputImage.getNSlices(); ++k) {
					int label = getLabel(i, j, k);
					// if not a background voxel and component not removed
					if (label > 0 && newLabels.get(label - 1) > 0) {
						int           newLabel = newLabels.get(label - 1); // get new label from old label
						ComponentInfo ci       = newTabComponents.get(newLabel - 1);
						ci.setLabel(newLabel); // Set new label for the component
						setLabel(i, j, k, newLabel); // Set new label for the voxel
						// Possibly change the color on the whole component
						if (setRandomColors) {
							imgP.setVoxel(i, j, k, componentsColors.get(newLabel - 1).intValue());
						}
					} else {
						setLabel(i, j, k, 0); // Set label to 0 (remove the voxel)
						imgP.setVoxel(i, j, k, 0);
					}
				}
			}
		}
		this.compInfo = newTabComponents;
	}
	
	
	/**
	 * @return a human readable string representation of this instance
	 *
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Connected components of the image ").append(this.inputImage.getTitle()).append("\n");
		for (ComponentInfo compInfo : this.compInfo) {
			builder.append(compInfo).append("\n");
		}
		return builder.toString();
	}
	
	
	/**
	 * retrieves the number of components (after calling doComponents)
	 *
	 * @return the number of components detected.
	 */
	public int getNumberOfComponents() {
		return this.compInfo.size();
	}
	
} // end of class