package gred.nucleus.utils;


public class ObjectCharacteristics {
	
	private double _circularity;
	private int _nbPixel;
	private double _averageIntesnity;
	private double _stdDevIntesnity;
	private double _aspectRatio;
	private double _perimeter;
	private double _area;
	private double _solidity;
	private double _round;
	private String _name;
	
	
	public ObjectCharacteristics(double circularity, int nbPixel, double averageIntesnity,
	                             double stdDevIntesnity, double aspectRatio,  double perimeter,  double area, double solidity, double round, String name){
		
		this._circularity = circularity;
		this._nbPixel = nbPixel;
		this._averageIntesnity = averageIntesnity  ;
		this._stdDevIntesnity = stdDevIntesnity ;
		this._aspectRatio = aspectRatio;
		this._perimeter = perimeter;
		this._area = area;
		this._solidity = solidity;
		this._round = round;
		this._name = name;
	}
	
	
	public ObjectCharacteristics(double circularity,  double aspectRatio,  double perimeter,  double area, double solidity, double round, String name){
		
		this._circularity = circularity;
		this._aspectRatio = aspectRatio;
		this._perimeter = perimeter;
		this._area = area;
		this._solidity = solidity;
		this._round = round;
		this._name = name;
	}
	
	
	
	
	public double getArea(){return  this._area;}
	public double getAspectRatio(){return  this._aspectRatio;}
	public double getPerimeter(){return  this._perimeter;}
	public double getCircularity(){return  this._circularity;}
	public double getAverageIntesnity(){return  this._averageIntesnity;}
	public double getStdDevIntesnity(){return  this._stdDevIntesnity;}
	public double getSolidity(){return  this._solidity;}
	public double getRound(){return  this._round;}
	public int getNbPixel(){return  this._nbPixel;}
	public String getName(){return  this._name;}
	
	
	
	public void setArea(double area){ this._area = area;}
	public void setAspectRatio(double aspectRatio){ this._aspectRatio = aspectRatio;}
	public void setPerimeter(double a){this._perimeter = a;}
	public void setCircularity(double a){this._circularity = a;}
	public void setAverageIntesnity(double a){ this._averageIntesnity = a;}
	public void setStdDevIntesnity(double a){this._stdDevIntesnity = a;}
	public void setSolidity(double a){this._solidity = a;}
	public void setRound(double a){this._round = a;}
	public void setNbPixel(int a){this._nbPixel = a;}
	
	
	
}
