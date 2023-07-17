package gred.nucleus.utils;



public class Nucleus extends ObjectCharacteristics {
	
	private int _nbCc;
	private double _RHF;
	private Chromocenter _chromocenter;
	
	public Nucleus(double circularity, int nbPixel, double averageIntesnity,
	               double stdDevIntesnity, double aspectRatio,  double perimeter,
	               double area, double solidity, double round, String name){
		
		super(circularity, nbPixel, averageIntesnity, stdDevIntesnity, aspectRatio, perimeter,  area, solidity, round, name);
		
	}
	
	public Nucleus(double circularity, double aspectRatio,  double perimeter,
	               double area, double solidity, double round, String name){
		
		super(circularity, aspectRatio, perimeter,  area, solidity, round, name);
		
	}
	
	public void setChromocenter (Chromocenter chromocenter){
		this._chromocenter = chromocenter;
	}
	public void setNbCc(int nbCc){this._nbCc = nbCc;}
	
	public Chromocenter getChromocenter(){return this._chromocenter;}
	public int getNbCc(){return  this._nbCc;}
	public double getRHF(){return  this._RHF;}
	public void setRHF(double a){_RHF = a;}
	
	
	public String ToString(){
		double nucIntesnity = getAverageIntesnity()*getNbPixel();
		
		return getName()+"\t"+getArea()+"\t"+getPerimeter()+
		       "\t"+getCircularity()+"\t"+getAspectRatio()+"\t"+getSolidity()+"\t"+getRound()+
		       "\t"+getNbCc()+"\t"+getRHF()+"\t"+_chromocenter.getArea();
	}
	
	
	
	
}
