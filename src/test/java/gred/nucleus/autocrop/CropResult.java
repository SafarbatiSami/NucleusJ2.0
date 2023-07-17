package gred.nucleus.autocrop;


public class CropResult {
	private final int cropNumber;
	private final Box box;
	private final int channel;
	
	
	public CropResult(int cropNumber,
	                  int channel,
	                  int xStart,
	                  int yStart,
	                  int zStart,
	                  int width,
	                  int height,
	                  int depth) {
		this.cropNumber = cropNumber;
		this.channel = channel;
		
		box = new Box((short) xStart,
		              (short) (xStart + width),
		              (short) yStart,
		              (short) (yStart + height),
		              (short) zStart,
		              (short) (zStart + depth)
		);
	}
	
	
	public Box getBox() {
		return box;
	}
	
	
	public int getCropNumber() {
		return cropNumber;
	}
	
}
