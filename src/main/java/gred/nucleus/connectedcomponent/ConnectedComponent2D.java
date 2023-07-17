/*
 * Copyright (C) 2016 by Rémy Malgouyres
 * http://malgouyres.org
 * File: ConnectedComponent2D.java
 *
 * The program is distributed under the terms of the GNU General Public License *
 *
 */

package gred.nucleus.connectedcomponent;


import gred.nucleus.utils.Voxel;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;


/**
 * Class dedicated to connected components labeling in 3D images
 *
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 * <p> TODO : This class has not been tested and should probably be worked out before use.
 */
public class ConnectedComponent2D extends ConnectedComponent {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	/**
	 * Constructor from an ImageWrapper representing a binary image and the foreground color in this image
	 *
	 * @param inputImage      input (probably binary) image, the components of which to compute.
	 * @param foregroundColor label of the 1's in the input image inputImage
	 */
	protected ConnectedComponent2D(ImagePlus inputImage, int foregroundColor) {
		super(inputImage, foregroundColor);
	}
	
	
	/**
	 * Performs a breadth first search of the connected component for labeling The method goes over all the voxels in
	 * the connected component of the object of the initial voxel. The method sets the fields of the ComponentInfo
	 * parameter to record the status of the component.
	 *
	 * @param voxelShort   initial voxel of the connected component
	 * @param currentLabel label to set for the voxels of the component
	 */
	protected void breadthFirstSearch(Voxel voxelShort, short currentLabel, ComponentInfo componentInfo) {
		
		// FIFO for the Breadth First Search algorithm
		// LinkedList is more efficient than ArrayList
		// because poll() (alias Remove(0)) is constant time !!!
		LinkedList<Voxel> voxelFifo = new LinkedList<>();
		
		// add initial voxel to the FIFO
		voxelFifo.add(voxelShort);
		while (!voxelFifo.isEmpty()) {
			// Retrieve and remove the head of the FIFO
			Voxel polledVoxelShort = voxelFifo.poll();
			short iV               = polledVoxelShort.getX();
			short jV               = polledVoxelShort.getY();
			
			//			freeVoxel();
			
			// Determine the neighborhood taking into account the image's boundaries
			short iMin;
			short iMax;
			short jMin;
			short jMax;
			if (iV - 1 >= 0) {
				iMin = (short) (iV - 1);
			} else {
				iMin = 0;
				componentInfo.setOnTheeBorder();
			}
			
			if (jV - 1 >= 0) {
				jMin = (short) (jV - 1);
			} else {
				jMin = 0;
				componentInfo.setOnTheeBorder();
			}
			
			if (iV + 1 < this.inputImage.getWidth()) {
				iMax = (short) (iV + 1);
			} else {
				iMax = (short) (this.inputImage.getWidth() - 1);
				componentInfo.setOnTheeBorder();
			}
			
			if (jV + 1 < this.inputImage.getHeight()) {
				jMax = (short) (jV + 1);
			} else {
				jMax = (short) (this.inputImage.getHeight() - 1);
				componentInfo.setOnTheeBorder();
			}
			
			ImageProcessor imgProc = inputImage.getProcessor();
			// For each neighbor :
			for (short ii = iMin; ii <= iMax; ii++) {
				for (short jj = jMin; jj <= jMax; jj++) {
					// If the neighbor (different from VoxelRecordShort) is a 1 and not labeled
					if ((getLabel(ii, jj, 0) == 0) && (imgProc.get(ii, jj) == this.foregroundColor)) {
						// Set the voxel's label
						setLabel(ii, jj, 0, currentLabel);
						// Increment component's cardinality
						componentInfo.incrementNumberOfPoints();
						// Add to FIFO
						voxelFifo.add(new Voxel(ii, jj, (short) 0));
					}
				}
			}
		}
	}
	
	
	/** labels the connected components of the input image (attribute ip) */
	@Override
	public void doLabelConnectedComponent() {
		short          currentLabel = 0;
		ImageProcessor imgProc      = inputImage.getProcessor();
		for (short i = 0; i < this.inputImage.getWidth(); i++) {
			for (short j = 0; j < this.inputImage.getHeight(); j++) {
				if (imgProc.getPixel(i, j) == this.foregroundColor && getLabel(i, j, 0) == 0) {
					currentLabel++;
					this.labels[i][j][0] = currentLabel;
					ComponentInfo componentInfo = new ComponentInfo(currentLabel, 1, new Voxel(i, j, (short) 0), false);
					try {
						breadthFirstSearch(new Voxel(i, j, (short) 0), currentLabel, componentInfo);
					} catch (Exception e) {
						LOGGER.error("An error occurred.", e);
						System.exit(0);
					}
					this.compInfo.add(componentInfo);
				}
			}
		}
	}
	
} // end of class

