/*
 * Copyright (C) 2016 by Rémy Malgouyres
 * http://malgouyres.org
 * File: ComponentRemovalLinear.java
 *
 * The program is distributed under the terms of the GNU General Public License
 *
 */

package gred.nucleus.componentremoval;

import gred.nucleus.connectedcomponent.ComponentInfo;
import gred.nucleus.utils.Voxel;


/**
 * This class is intended to implement the predicate on voxels and connected components to keep all components which are
 * within a thick plane. The thick plane's equation is: z >= xCoefficient*x + yCoefficient*y + constantCoefficient > z -
 * thickness
 *
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 */
public class ComponentRemovalLinear implements ComponentRemovalPredicate {
	
	private final double xCoefficient;
	private final double yCoefficient;
	private final double constantCoefficient;
	private final double thickness;
	
	
	/**
	 * @param xCoefficient        first coefficient of the plane's equation
	 * @param yCoefficient        second coefficient of the plane's equation
	 * @param constantCoefficient third coefficient of the plane's equation
	 * @param thickness           thickness of the plane
	 */
	public ComponentRemovalLinear(double xCoefficient,
	                              double yCoefficient,
	                              double constantCoefficient,
	                              double thickness) {
		this.xCoefficient = xCoefficient;
		this.yCoefficient = yCoefficient;
		this.constantCoefficient = constantCoefficient;
		this.thickness = thickness;
	}
	
	
	/**
	 * @return true if the voxel is in the thick plane, false otherwise
	 *
	 * @see gred.nucleus.componentremoval.ComponentRemovalPredicate)
	 */
	@Override
	public boolean keepVoxelComponent(Voxel voxel, ComponentInfo componentInfo) {
		double zValue =
				this.xCoefficient * voxel.getX() + this.yCoefficient * voxel.getY() + this.constantCoefficient;
		return ((voxel.getZ() >= zValue) && (voxel.getZ() < zValue + this.thickness));
	}
	
}
