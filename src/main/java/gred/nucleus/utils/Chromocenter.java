package gred.nucleus.utils;



/**
 *
 */
public class Chromocenter extends ObjectCharacteristics{
	
	private double _totalIntensity;
	private double _ccValue;
	
	/**
	 *
	 * @param circularity
	 * @param nbPixel
	 * @param aspectRatio
	 * @param perimeter
	 * @param area
	 * @param solidity
	 * @param round
	 * @param name
	 */
	public Chromocenter(double circularity, int nbPixel, double aspectRatio,  double perimeter,
	                    double area, double solidity, double round, String name){
		super(circularity, nbPixel, 0, 0, aspectRatio, perimeter,  area, solidity, round, name);
	}
	
	
	
	public void setTotalIntensity(double a){this._totalIntensity = a;}
	public double getTotalIntensity(){return this._totalIntensity;}
	
	public void setCCValue(double a, double b){this._ccValue = a/b;}
	private void setCCValue(double a){this._ccValue = a;}
	public double getCCValue(){ return this._ccValue;}
	
	
	public void addChromocenter(double circularity, int nbPixel, double aspectRatio,
	                            double perimeter, double area, double solidity, double round,
	                            double ccValue){
		
		this.setCircularity(this.getCircularity()+circularity);
		this.setNbPixel(this.getNbPixel()+nbPixel);
		this.setAspectRatio(this.getAspectRatio()+aspectRatio);
		this.setPerimeter(this.getPerimeter()+perimeter);
		this.setArea(this.getArea()+area);
		this.setSolidity(this.getSolidity()+solidity);
		this.setRound(this.getRound()+round);
		this.setCCValue(this.getCCValue()+ccValue);
		
	}
	
	/**
	 *
	 * @param nbCc
	 */
	public void avgChromocenters(int nbCc){
		if(nbCc > 0) {
			this.setCircularity(this.getCircularity() / nbCc);
			this.setNbPixel(this.getNbPixel() / nbCc);
			this.setAspectRatio(this.getAspectRatio() / nbCc);
			this.setPerimeter(this.getPerimeter() / nbCc);
			this.setArea(this.getArea() / nbCc);
			this.setSolidity(this.getSolidity() / nbCc);
			this.setRound(this.getRound() / nbCc);
			this.setCCValue(this.getCCValue() / nbCc);
		}else{
			this.setCircularity(0);
			this.setNbPixel(0);
			this.setAspectRatio(0);
			this.setPerimeter(0);
			this.setArea(0);
			this.setSolidity(0);
			this.setRound(0);
			this.setCCValue(0);
		}
		
	}
	
	public String toString(){
		return getName()+"\t"+getArea()+"\t"+getPerimeter()+"\t"+getCircularity()+"\t"+getAspectRatio()+
		       "\t"+getSolidity()+"\t"+getCCValue();
	}
}
