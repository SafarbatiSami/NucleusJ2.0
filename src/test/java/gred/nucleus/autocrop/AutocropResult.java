package gred.nucleus.autocrop;

import java.util.Collections;
import java.util.List;


public class AutocropResult {
	private int              cropNb;
	private List<CropResult> coordinates;
	
	
	public int getCropNb() {
		return cropNb;
	}
	
	
	public void setCropNb(int cropNb) {
		this.cropNb = cropNb;
	}
	
	
	public List<CropResult> getCoordinates() {
		return Collections.unmodifiableList(coordinates);
	}
	
	
	public void setCoordinates(List<CropResult> coordinates) {
		this.coordinates = coordinates;
	}
	
}
