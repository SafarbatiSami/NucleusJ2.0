package gred.nucleus.utils;

import ij.ImagePlus;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

public class Parameters2D {
	private ImagePlus _img;
	private ImagePlus _raw;
	private ResultsTable _resultsTable;
	private int _sumIntensity;
	private int _avgIntensity;
	private int _nbPixel;
	private float _label;
	
	/**
	 *
	 * @param bin
	 */
	public Parameters2D(ImagePlus bin){
		this._img = bin;
	}
	
	/**
	 *
	 * @param bin
	 * @param raw
	 * @param label
	 */
	public Parameters2D(ImagePlus bin, ImagePlus raw, float label){
		this._img = bin;
		_sumIntensity = 0 ;
		_nbPixel = 0;
		_label = label;
		_raw = raw;
		getTotalIntensity();
	}
	
	
	/**
	 * method computing the different parameters
	 *
	 * @return a resultTable containinig the results
	 */
	public void computePrameters(){
		_resultsTable = new ResultsTable();
		ParticleAnalyzer particleAnalyser = new ParticleAnalyzer
				(ParticleAnalyzer.SHOW_NONE, Measurements.AREA+Measurements.CIRCULARITY+Measurements.PERIMETER, _resultsTable,
				 1, Double.MAX_VALUE, 0,1);
		ImageProcessor ip = _img.getProcessor();
		ip.invertLut();
		_img.setProcessor(ip);
		particleAnalyser.analyze(_img);
	}
	
	/**
	 *
	 */
	private void getTotalIntensity (){
		ImageConverter ic = new ImageConverter(_raw);
		ic.convertToGray8();
		ImageProcessor ipBin = _img.getProcessor();
		ImageProcessor ipRaw = _raw.getProcessor();
		
		for(int i = 0; i < _img.getWidth();++i){
			for(int j = 0; j < _img.getHeight();++j) {
				if(ipBin.getPixelValue(i,j) == _label){
					ipBin.setf(i,j,255);
					_sumIntensity += ipRaw.getPixelValue(i,j);
					_nbPixel++;
				}else   ipBin.setf(i,j,0);
			}
			
		}
		ic = new ImageConverter(_img);
		ic.convertToGray8();
		_avgIntensity = _sumIntensity/_nbPixel;
		//_img.show();
	}
	
	/**
	 * Getter of the Aspect ratio vaue
	 * The aspect ratio of the particle’s fitted ellipse, i.e., [M ajor Axis]/Minor Axis] .
	 * If Fit Ellipse is selected the Major and Minor axis are displayed. Uses the heading AR.
	 * (Soucre : ImageJ)
	 * @return double aspect ratio value
	 */
	public double getAspectRatio(){
		return _resultsTable.getValue("AR", 0);
	}
	
	public double getPerim(){
		return _resultsTable.getValue("Perim.", 0);
	}
	
	public double getArea(){
		return _resultsTable.getValue("Area", 0);
	}
	
	public double getRound(){
		return _resultsTable.getValue("Round", 0);
	}
	
	public double getSolidity(){
		return _resultsTable.getValue("Solidity", 0);
	}
	
	public int getAvgIntensity(){
		return this._avgIntensity;
	}
	
	public int getNbPixelObject(){
		return this._nbPixel;
	}
	
	/**
	 * Getter of the circularity
	 *
	 * Circularity Particles with size circularity values outside the range specified in this field are also
	 * ignored. Circularity = (4π × [Area ] / [P erimeter]2 ) , see Set Measurements. . . ) ranges from 0 (infinitely
	 * elongated polygon) to 1 (perfect circle).
	 * (Soucre : ImageJ)
	 *
	 * @return double circularity value
	 */
	
	public double getCirculairty(){
		return _resultsTable.getValue("Circ.", 0);
	}
	
	
	
}

