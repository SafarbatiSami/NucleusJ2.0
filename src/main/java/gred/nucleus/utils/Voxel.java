/*
 * Copyright (C) 2016 by Rémy Malgouyres
 * http://malgouyres.org
 * File: VoxelShort.java
 *
 * The program is distributed under the terms of the GNU General Public License *
 *
 */

package gred.nucleus.utils;

/**
 * Represents a voxel with its integer coordinates in the three dimensions stored as shorts on 2 Bytes. This class can
 * be used for memory intensive storage of voxel's coordinates since these coordinates generally don't take high values
 * so that they fit in a short.
 *
 * @author Rémy Malgouyres, Tristan Dubos and Axel Poulet
 */
public class Voxel {
	
	/** Integer valued coordinates of the voxel */
	private final short[] coordinates;
	private       short   value;
	
	
	/** Creates an uninitialized voxel */
	public Voxel() {
		this.coordinates = new short[3];
	}
	
	
	/**
	 * Constructor from given coordinates
	 *
	 * @param x first coordinate of the voxel
	 * @param y second coordinate of the voxel
	 * @param z third coordinate of the voxel
	 */
	public Voxel(short x, short y, short z) {
		this.coordinates = new short[3];
		this.coordinates[0] = x;
		this.coordinates[1] = y;
		this.coordinates[2] = z;
	}
	
	
	/**
	 * Constructor from given coordinates
	 *
	 * @param x first coordinate of the voxel
	 * @param y second coordinate of the voxel
	 * @param z third coordinate of the voxel
	 */
	public Voxel(short x, short y, short z, short value) {
		this.coordinates = new short[3];
		this.coordinates[0] = x;
		this.coordinates[1] = y;
		this.coordinates[2] = z;
		this.value = value;
	}
	
	
	/**
	 * Constructor
	 *
	 * @param x first coordinate of the voxel
	 * @param y second coordinate of the voxel
	 * @param z third coordinate of the voxel
	 */
	public void setCoordinates(short x, short y, short z) {
		this.coordinates[0] = x;
		this.coordinates[1] = y;
		this.coordinates[2] = z;
	}
	
	
	/**
	 * Override of the clone operation
	 *
	 * @return a reference to a clone copy of this instance.
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	
	/**
	 * Increments the i^th coordinates of a voxel, with i = 0, 1 or 2
	 *
	 * @param i the coordinate's index (0, 1 or 2)
	 */
	public void incrementCoordinate(int i) {
		this.coordinates[i]++;
	}
	
	
	/**
	 * sets the i^th coordinates of a voxel, with i = 0, 1 or 2
	 *
	 * @param i     the coordinate's index (0, 1 or 2)
	 * @param value the new value of the i^th coordinate of the voxel
	 */
	public void setCoordinates(int i, short value) {
		this.coordinates[i] = value;
	}
	
	
	/**
	 * Returns the i^th coordinates of a voxel, with i = 0, 1 or 2
	 *
	 * @param i the coordinate's index (0, 1 or 2)
	 *
	 * @return the i^th coordinate of the voxel
	 */
	public short getCoordinate(int i) {
		return this.coordinates[i];
	}
	
	
	/**
	 * Returns the x coordinates of a voxel
	 *
	 * @return the first coordinate of the voxel
	 */
	public short getX() {
		return this.coordinates[0];
	}
	
	
	/**
	 * sets the first coordinate of a voxel
	 *
	 * @param value the new value of the first coordinate of the voxel
	 */
	public void setX(short value) {
		this.coordinates[0] = value;
	}
	
	
	/**
	 * Returns the y coordinate of a voxel
	 *
	 * @return the second coordinate of the voxel
	 */
	public short getY() {
		return this.coordinates[1];
	}
	
	
	/**
	 * sets the second coordinate of a voxel
	 *
	 * @param value the new value of the first coordinate of the voxel
	 */
	public void setY(short value) {
		this.coordinates[1] = value;
	}
	
	
	/**
	 * Returns the z coordinate of a voxel
	 *
	 * @return the third coordinate of a voxel
	 */
	public short getZ() {
		return this.coordinates[2];
	}
	
	
	/**
	 * sets the third coordinate of a voxel
	 *
	 * @param value the new value of the first coordinate of the voxel
	 */
	public void setZ(short value) {
		this.coordinates[2] = value;
	}
	
	
	/**
	 * Computes the coordinate by coordinate addition between two voxels
	 *
	 * @param voxel another voxel
	 */
	public void shiftCoordinates(Voxel voxel) {
		this.coordinates[0] += voxel.coordinates[0];
		this.coordinates[1] += voxel.coordinates[1];
		this.coordinates[2] += voxel.coordinates[2];
	}
	
	
	/**
	 * Multiplies the coordinates of voxel with a different factor on each coordinates
	 *
	 * @param a factor on the first coordinate
	 * @param b factor on the second coordinate
	 * @param c factor on the third coordinate
	 */
	public void multiply(short a, short b, short c) {
		this.coordinates[0] *= a;
		this.coordinates[1] *= b;
		this.coordinates[2] *= c;
	}
	
	
	/**
	 * Multiplied the coordinates of voxel with a same factor for each coordinates
	 *
	 * @param a factor for all the coordinates
	 */
	public void multiply(short a) {
		this.coordinates[0] *= a;
		this.coordinates[1] *= a;
		this.coordinates[2] *= a;
	}
	
	
	/**
	 * Returns a human readable representation of a voxel as a string
	 *
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.coordinates[0] + ", " + this.coordinates[1] + ", " + this.coordinates[2] + ")";
	}
	
	
	public short getValue() {
		return value;
	}
	
	
	public void setValue(short value) {
		this.value = value;
	}
	
} // End of class