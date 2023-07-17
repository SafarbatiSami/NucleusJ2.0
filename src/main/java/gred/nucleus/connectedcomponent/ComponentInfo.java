/*
 * Copyright (C) 2016 by Rémy Malgouyres
 * http://malgouyres.org
 * File: ComponentInfo.java
 *
 * The program is distributed under the terms of the GNU General Public License
 *
 */

package gred.nucleus.connectedcomponent;

import gred.nucleus.utils.Voxel;


/**
 * Represents the information relative to a connected component in a binary image.
 *
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 */
public class ComponentInfo {
	
	/** Label (ID) of the connected component (i.e. color in the labels image array) */
	private int label;
	
	/**
	 * Cardinality of the connected component. Can be zero if the connected component has been filtered out (e.g.
	 * threshold size or border components exclusion)
	 */
	private int numberOfPoints;
	
	/**
	 * Voxel representative of the component (one voxel in the component) Currently, this representative has minimal
	 * depth Z
	 * <p> TODO extend usage using a comparison predicate possibly other that comparing depth.
	 */
	private final Voxel voxelRepresentant;
	
	/**
	 * Flag indicating whether the connected component touches the edge of the image. (allows filtering out connected
	 * component which touch the edge of the image.)
	 */
	private boolean componentOnTheBorder;
	
	
	/**
	 * Constructor
	 *
	 * @param label                label of the connected component (i.e. color in the labels image array)
	 * @param numberOfPoints       (initial) cardinality of the connected component.
	 * @param voxelRepresentant    (initial) voxel representative of the component (one voxel in the component)
	 * @param componentOnTheBorder Flag (initial) indicating whether the connected component touches the edge of the
	 *                             image.
	 */
	public ComponentInfo(int label, int numberOfPoints, Voxel voxelRepresentant, boolean componentOnTheBorder) {
		this.label = label;
		this.numberOfPoints = numberOfPoints;
		this.voxelRepresentant = voxelRepresentant;
		this.componentOnTheBorder = componentOnTheBorder;
	}
	
	
	/**
	 * Getter.
	 *
	 * @return the label of the component
	 */
	public int getLabel() {
		return this.label;
	}
	
	
	/**
	 * Setter  for the label of the component
	 *
	 * @param label the label to use
	 */
	public void setLabel(int label) {
		this.label = label;
	}
	
	
	/**
	 * Getter
	 *
	 * @return the cardinality of the component
	 */
	public int getNumberOfPoints() {
		return this.numberOfPoints;
	}
	
	
	/**
	 * Setter
	 *
	 * @param numberOfPoints the cardinality to set
	 */
	public void setNumberOfPoints(int numberOfPoints) {
		this.numberOfPoints = numberOfPoints;
	}
	
	
	/** Increments the cardinality */
	public void incrementNumberOfPoints() {
		this.numberOfPoints++;
	}
	
	
	/**
	 * Getter
	 *
	 * @return returns the component's flag indicating whether the component is on the border.
	 */
	public boolean isOnTheBorder() {
		return this.componentOnTheBorder;
	}
	
	
	/**
	 * Getter
	 *
	 * @return the voxel representative of the component (one voxel in the component)
	 */
	public Voxel getRepresentant() {
		return this.voxelRepresentant;
	}
	
	
	/** Sets to true the flag indicating whether the component is on the border. */
	public void setOnTheeBorder() {
		this.componentOnTheBorder = true;
	}
	
	
	/**
	 * @return a human readable string representation of this instance
	 *
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Component label : " + this.label + ", Number of points : " + this.numberOfPoints;
	}
	
} // end of class ComponentInfo

