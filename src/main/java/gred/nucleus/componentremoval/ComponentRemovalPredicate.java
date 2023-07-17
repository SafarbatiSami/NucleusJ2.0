/*
 * Copyright (C) 2016 by Rémy Malgouyres
 * http://malgouyres.org
 * File: ComponentRemovalPredicate.java
 *
 * The program is distributed under the terms of the GNU General Public License
 *
 */

package gred.nucleus.componentremoval;

import gred.nucleus.connectedcomponent.ComponentInfo;
import gred.nucleus.utils.Voxel;


/**
 * This interface is intended for the purpose of selectively removing connected components in a binary image, according
 * to a predicate satisfied by some voxels of the component.
 * <p>
 * The options are:
 * <ul>
 * 	<li>To remove the components such as one voxel satisfies the predicate</li>
 * 	<li>To remove the components such none of the voxel satisfies the predicate</li>
 * </ul>
 *
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 */
public interface ComponentRemovalPredicate {
	
	/**
	 * @param voxel         the voxel at which to test the predicate
	 * @param componentInfo The information concerning the connected component of the voxel
	 *
	 * @return the predicate's value
	 */
	boolean keepVoxelComponent(Voxel voxel, ComponentInfo componentInfo);
	
}
