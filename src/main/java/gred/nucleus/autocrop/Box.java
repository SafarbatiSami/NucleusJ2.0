package gred.nucleus.autocrop;

/**
 * Represents the information relative to a Box in the image space (e.g. a bounding box for an object in an image). A
 * box is represented by the minimal values (xMin, yMin, zMin) and the maximal values (xMax, yMax, zMax) for each
 * coordinate.
 *
 * @author Tristan Dubos and Axel Poulet
 */
public class Box {
	/** The coordinate x min of the Box */
	private short xMin;
	/** The coordinate x max of the Box */
	private short xMax;
	/** The coordinate y min of the Box */
	private short yMin;
	/** The coordinate y max of the Box */
	private short yMax;
	/** The coordinate z min of the Box */
	private short zMin;
	/** The coordinate z max of the Box */
	private short zMax;
	
	
	/**
	 * Constructor
	 *
	 * @param xMin: coordinate x min of the Box
	 * @param xMax: coordinate x max of the Box
	 * @param yMin: coordinate y min of the Box
	 * @param yMax: coordinate y max of the Box
	 * @param zMin: coordinate z min of the Box
	 * @param zMax: coordinate z max of the Box
	 */
	public Box(short xMin, short xMax, short yMin, short yMax, short zMin,
	           short zMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;
	}
	
	
	/**
	 * Returns minimal value of the x coordinate in the box
	 *
	 * @return the xMin
	 */
	public short getXMin() {
		return this.xMin;
	}
	
	
	/** @param xMin the xMin to set */
	public void setXMin(short xMin) {
		this.xMin = xMin;
	}
	
	
	/**
	 * Returns maximal value of the x coordinate in the box
	 *
	 * @return the xMax
	 */
	public short getXMax() {
		return this.xMax;
	}
	
	
	/** @param xMax the xMax to set */
	public void setXMax(short xMax) {
		this.xMax = xMax;
	}
	
	
	/**
	 * Returns minimal value of the y coordinate in the box
	 *
	 * @return the yMin
	 */
	public short getYMin() {
		return this.yMin;
	}
	
	
	/** @param yMin the yMin to set */
	public void setYMin(short yMin) {
		this.yMin = yMin;
	}
	
	
	/**
	 * Returns maximal value of the y coordinate in the box
	 *
	 * @return the yMax
	 */
	public short getYMax() {
		return this.yMax;
	}
	
	
	/** @param yMax the yMax to set */
	public void setYMax(short yMax) {
		this.yMax = yMax;
	}
	
	
	/**
	 * returns minimal value of the z coordinate in the box
	 *
	 * @return the zMin
	 */
	public short getZMin() {
		return this.zMin;
	}
	
	
	/** @param zMin the zMin to set */
	public void setZMin(short zMin) {
		this.zMin = zMin;
	}
	
	
	/**
	 * Returns maximal value of the z coordinate in the box
	 *
	 * @return the zMax
	 */
	public short getZMax() {
		return this.zMax;
	}
	
	
	/** @param zMax the zMax to set */
	public void setZMax(short zMax) {
		this.zMax = zMax;
	}
	
}
