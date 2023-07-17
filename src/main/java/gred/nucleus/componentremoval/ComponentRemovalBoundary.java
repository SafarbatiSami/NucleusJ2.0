/*
 * Copyright (C) 2016 by Rémy Malgouyres
 * http://malgouyres.org
 * File: ComponentRemovalBoundary.java
 *
 * The program is distributed under the terms of the GNU General Public License
 *
 */

package gred.nucleus.componentremoval;

import gred.nucleus.connectedcomponent.ComponentInfo;
import gred.nucleus.utils.Voxel;


/**
 * This class is intended to implement the predicate on voxels and connected components. to filter out components of a
 * binary image which touch the border.
 *
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 */
public class ComponentRemovalBoundary implements ComponentRemovalPredicate {
	
	/**
	 *
	 */
	@Override
	public boolean keepVoxelComponent(Voxel voxel, ComponentInfo componentInfo) {
		return !componentInfo.isOnTheBorder();
	}
	
}
