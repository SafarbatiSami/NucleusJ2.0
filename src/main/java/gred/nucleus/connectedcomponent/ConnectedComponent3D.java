/*
 * Copyright (C) 2016 by Rémy Malgouyres
 * http://malgouyres.org
 * File: ConnectedComponent3D.java
 *
 * The program is distributed under the terms of the GNU General Public License *
 *
 */

package gred.nucleus.connectedcomponent;

import gred.nucleus.utils.Voxel;
import ij.ImagePlus;
import ij.ImageStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;


/**
 * Class dedicated to connected components labeling in 3D images
 *
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 */
public class ConnectedComponent3D extends ConnectedComponent {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	/**
	 * Constructor from an ImageWrapper representing a binary image and the foreground color in this image
	 *
	 * @param inputImage      input (probably binary) image, the components of which to compute.
	 * @param foregroundColor label of the 1's in the input image inputImage
	 */
	protected ConnectedComponent3D(ImagePlus inputImage, int foregroundColor) {
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
			short kV               = polledVoxelShort.getZ();
			// Determine the neighborhood taking into account the image's boundaries
			short iMin;
			short iMax;
			short jMin;
			short jMax;
			short kMin;
			short kMax;
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
			if (kV - 1 >= 0) {
				kMin = (short) (kV - 1);
			} else {
				kMin = 0;
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
			if (kV + 1 < this.inputImage.getNSlices()) {
				kMax = (short) (kV + 1);
			} else {
				kMax = (short) (this.inputImage.getNSlices() - 1);
				componentInfo.setOnTheeBorder();
			}
			ImageStack imageStack = inputImage.getStack();
			// For each neighbor :
			for (short kk = kMin; kk <= kMax; kk++) {
				//this.inputImage.setCurrentSlice(kk);
				for (short ii = iMin; ii <= iMax; ii++) {
					for (short jj = jMin; jj <= jMax; jj++) {
						// If the neighbor (different from VoxelRecordShort) is a 1 and not labeled
						if ((getLabel(ii, jj, kk) == 0) &&
						    (imageStack.getVoxel(ii, jj, kk) == this.foregroundColor)) {
							// Set the voxel's label
							setLabel(ii, jj, kk, currentLabel);
							componentInfo.incrementNumberOfPoints(); // increment component's cardinality					
							voxelFifo.add(new Voxel(ii, jj, kk)); // add to FIFO
							// check for minimal depth representative and update if necessary
							if (kk < componentInfo.getRepresentant().getZ()) {
								componentInfo.getRepresentant().setX(ii);
								componentInfo.getRepresentant().setY(jj);
								componentInfo.getRepresentant().setZ(kk);
							}
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * labels the connected components of the input image (attribute ip)
	 *
	 * @throws Exception
	 */
	@Override
	public void doLabelConnectedComponent() throws Exception {
		int        currentLabel = 0;
		ImageStack imageStack   = inputImage.getStack();
		for (short k = 0; k < this.inputImage.getNSlices(); k++) {
			for (short i = 0; i < this.inputImage.getWidth(); i++) {
				for (short j = 0; j < this.inputImage.getHeight(); j++) {
					if (imageStack.getVoxel(i, j, k) == this.foregroundColor && getLabel(i, j, k) == 0) {
						currentLabel++;
						if (currentLabel == Integer.MAX_VALUE) {
							throw new Exception("Too many connected components.");
						}
						this.labels[i][j][k] = currentLabel;
						//System.out.println("doLabelConnectedComponent "+currentLabel);
						ComponentInfo componentInfo = new ComponentInfo(
								currentLabel,
								1,
								new Voxel(i, j, k),
								false);
						
						try {
							breadthFirstSearch(new Voxel(i, j, k), (short) currentLabel, componentInfo);
						} catch (Exception e) {
							LOGGER.error("An error occurred.", e);
							System.exit(0);
						}
						this.compInfo.add(componentInfo);
						//System.gc();
					}
				}
			}
			System.gc();
		}
	}
	
} // end of class

