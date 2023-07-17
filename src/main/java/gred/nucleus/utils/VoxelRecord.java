package gred.nucleus.utils;

/**
 * Class to create a voxel with its coordinates in the three dimensions and its value
 *
 * @author Philippe Andrey, Tristan and Axel Poulet
 */
public class VoxelRecord {
	/** Coordinates voxel */
	public double i, j, k;
	/** Voxel value */
	double value;
	
	
	/**
	 * Constructor
	 *
	 * @param i Coordinates x of voxel
	 * @param j Coordinates y of voxel
	 * @param k Coordinates z of voxel
	 */
	public void setLocation(double i, double j, double k) {
		this.i = i;
		this.j = j;
		this.k = k;
	}
	public void Multiplie (double a, double b, double c ) {  this.setLocation(this.i*a,this.j*b, this.k*c); }
	
	/**
	 * Returns the x coordinates of a voxel
	 *
	 * @return
	 */
	public double getI() {
		return i;
	}
	
	
	/**
	 * Returns the y coordinates of a voxel
	 *
	 * @return
	 */
	public double getJ() {
		return j;
	}
	
	
	/**
	 * Returns the z coordinates of a voxel
	 *
	 * @return
	 */
	public double getK() {
		return k;
	}
	
	
	/**
	 * Returns the voxel value
	 *
	 * @return
	 */
	public double getValue() {
		return value;
	}
	
	
	/**
	 * Initializes the voxel value
	 *
	 * @param value
	 */
	public void setValue(double value) {
		this.value = value;
	}
	
	/*
	  Compare the values of two voxel
	  0 if same voxel value
	  -1 if value of voxel input > value voxel
	  1 if value of voxel input < value voxel
	 * @param object a voxel
	 * @return results of comparison
	 */

  /*public int compareTo(Object object) {
    VoxelRecord voxelRecord = (VoxelRecord)object;

    if (value == voxelRecord.value)
    	return 0;
    else if (value < voxelRecord.value)
    	return -1;
    else
    	return 1;
  }*/
	
	
	/**
	 * Compares the values of two voxel 0 if same voxel value -1 if value of voxel input > value voxel 1 if value of
	 * voxel input < value voxel
	 *
	 * @param object a voxel
	 *
	 * @return results of comparison
	 */
	public int compareCoordinatesTo(Object object) {
		VoxelRecord voxelRecord = (VoxelRecord) object;
		
		if (i == voxelRecord.i && j == voxelRecord.j && k == voxelRecord.k) {
			return 0;
		} else {
			return 1;
		}
	}
	
	
	/**
	 * Computes a addition between the coordinates between two voxel
	 *
	 * @param p a VoxelRecord
	 */
	public void shiftCoordinates(VoxelRecord p) {
		this.setLocation(this.i + p.i, this.j + p.j, this.k + p.k);
	}
	
	
	/**
	 * Multiplies the coordinates of voxel with a different factor for each coordinates
	 *
	 * @param a
	 * @param b
	 * @param c
	 */
	public void multiply(double a, double b, double c) {
		this.setLocation(this.i * a, this.j * b, this.k * c);
	}
	
	
	/**
	 * Multiplies the coordinates of voxel with a same factor for each coordinates
	 *
	 * @param a
	 */
	public void multiply(double a) {
		this.setLocation(this.i * a, this.j * a, this.k * a);
	}
	
}