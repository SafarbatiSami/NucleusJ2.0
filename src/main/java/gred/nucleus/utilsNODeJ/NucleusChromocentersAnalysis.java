package gred.nucleus.utilsNODeJ;

import gred.nucleus.utils.Histogram;
import gred.nucleus.utils.Measure3D;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import gred.nucleus.plugins.ChromocenterParameters;
import gred.nucleus.utils.Chromocenter;
import gred.nucleus.utils.Parameters2D;
import gred.nucleus.core.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Several method to realise and create the outfile for the nuclear Analysis
 * this class contains the chromocenter parameters
 *
 * @author Tristan Dubos and Axel Poulet
 *
 */
public class NucleusChromocentersAnalysis {
	/**
	 *
	 */
	public NucleusChromocentersAnalysis(){
	}
	
	//TODO INTEGRATION CLASS NEW MEASURE 3D
	
	
	
	/**
	 *
	 * Analysis for several nuclei, the results are stock on output file
	 *
	 * @param rhfChoice
	 * @param imagePlusInput
	 * @param imagePlusSegmented
	 * @param imagePlusChromocenter
	 * @throws IOException
	 */
	public File[] compute3DParameters ( String rhfChoice,
	                                    ImagePlus imagePlusInput, ImagePlus imagePlusSegmented,
	                                    ImagePlus imagePlusChromocenter,
	                                    ChromocenterParameters chromocenterParameters) throws IOException {
		Histogram histogram = new Histogram();
		histogram.run(imagePlusChromocenter);
		Calibration calibration = imagePlusInput.getCalibration();
		double voxelVolume = calibration.pixelDepth*calibration.pixelHeight*calibration.pixelWidth;
		
		imagePlusSegmented.setCalibration(calibration);
		gred.nucleus.utils.Measure3D measure3D   = new Measure3D(imagePlusSegmented, imagePlusInput, imagePlusInput.getCalibration().pixelWidth, imagePlusInput.getCalibration().pixelHeight, imagePlusInput.getCalibration().pixelDepth);
		File                         fileResults = new File(chromocenterParameters.outputFolder+"NucAndCcParameters3D.tab");
		File fileResultsCC = new File(chromocenterParameters.outputFolder+"CcParameters3D.tab");
		boolean exist = fileResults.exists();
		
		
		
		String text = "";
		String textCC = "";
		if (exist == false) {
			text = chromocenterParameters.getAnalysisParameters();
			text += getResultsColumnNames();
			textCC = chromocenterParameters.getAnalysisParameters();
			textCC += getResultsColumnNamesCC();
			
		}
		
		text += measure3D.nucleusParameter3D()+"\t" +
		        measure3D.computeVolumeRHF(imagePlusSegmented, imagePlusChromocenter)+"\t";
		
		if (histogram.getNbLabels() > 0) {
			double [] tVolumesObjects =  measure3D.computeVolumeofAllObjects(imagePlusChromocenter);
			double volumeCcMean = computeMeanOfTable(tVolumesObjects);
			int nbCc = measure3D.getNumberOfObject(imagePlusChromocenter);
			RadialDistance radialDistance = new RadialDistance ();
			double [] tBorderToBorderDistance = radialDistance.computeBorderToBorderDistances(imagePlusSegmented,imagePlusChromocenter);
			double [] tBarycenterToBorderDistance = radialDistance.computeBarycenterToBorderDistances (imagePlusSegmented,imagePlusChromocenter);
			double [] tIntensity = measure3D.computeIntensityofAllObjects(imagePlusChromocenter);
			double [] tBarycenterToBorderDistanceTableNucleus = radialDistance.computeBarycenterToBorderDistances (imagePlusSegmented,imagePlusSegmented);
			text += nbCc+"\t"
			        +volumeCcMean+"\t"
			        +volumeCcMean*nbCc+"\t"
			        +computeMeanOfTable(tIntensity)+"\t"
			        +computeMeanOfTable(tBorderToBorderDistance)+"\t"
			        +computeMeanOfTable(tBarycenterToBorderDistance)+"\t";
			
			
			for (int i = 0; i < tBorderToBorderDistance.length;++i ) {
				textCC += imagePlusInput.getTitle()+"_"+i+"\t"
				          +tVolumesObjects[i]+"\t"
				          +tIntensity[i]+"\t"
				          +tBarycenterToBorderDistance[i]+"\t"
				          +tBorderToBorderDistance[i]+"\t"
				          +tBarycenterToBorderDistanceTableNucleus[0]+"\n";
			}
		}
		else
			text += "0\t0\t0\tNaN\tNaN\t";
		
		text += voxelVolume+"\n";
		
		BufferedWriter bufferedWriterOutput = new BufferedWriter(new FileWriter(fileResults, true));
		
		bufferedWriterOutput.write(text);
		bufferedWriterOutput.flush();
		bufferedWriterOutput.close();
		
		BufferedWriter bufferedWriterOutputCC = new BufferedWriter(new FileWriter(fileResultsCC, true));
		bufferedWriterOutputCC.write(textCC);
		bufferedWriterOutputCC.flush();
		bufferedWriterOutputCC.close();
		return new File[] {fileResults, fileResultsCC};
		
	}
	
	
	
	
	/**
	 * Method wich compute the mean of the value in the table
	 *
	 * @param tInput Table of value
	 * @return Mean of the table
	 */
	public double computeMeanOfTable (double [] tInput) {
		double mean = 0;
		for (int i = 0; i < tInput.length; ++i)
			mean += tInput[i];
		mean = mean / (tInput.length);
		return mean;
	}
	
	
	/**
	 *
	 * @param img
	 * @return
	 */
	private ArrayList<Float> getLabels(ImagePlus img){
		ArrayList<Float> label = new ArrayList<Float>();
		ImageProcessor ip = img.getProcessor();
		
		for(int i = 0; i < img.getWidth(); ++i){
			for(int j = 0; j < img.getHeight();++j){
				float value = ip.getPixelValue(i,j);
				if(value > 0) {
					if (!label.contains(value)) {
						label.add(value);
					}
				}
			}
		}
		
		
		return label;
	}
	
	/**
	 *
	 * @param imgCc
	 * @param raw
	 * @param labels
	 * @return
	 */
	private Chromocenter getCcParam(ImagePlus imgCc, ImagePlus raw, ArrayList<Float> labels,String name, double avgNuc ){
		Chromocenter cc =  new Chromocenter(0,0,0,
		                                    0,0, 0, 0, "plopi");
		for(int j = 0; j < labels.size(); ++j) {
			//System.out.println(labels.get(j));
			Parameters2D param = new Parameters2D(imgCc,raw,labels.get(j));
			param.computePrameters();
			
			Chromocenter tmp = new Chromocenter(param.getCirculairty(), param.getNbPixelObject(), param.getAspectRatio(), param.getPerim(), param.getArea(),
			                                    param.getSolidity(), param.getRound(), name+"_"+j);
			
			tmp.setCCValue(param.getAvgIntensity(),avgNuc);
			cc.addChromocenter(param.getCirculairty(), param.getNbPixelObject(),
			                   param.getAspectRatio(), param.getPerim(), param.getArea(),
			                   param.getSolidity(), param.getRound(), tmp.getCCValue());
			
		}
		cc.avgChromocenters(labels.size()+1);
		return cc;
		
	}
	public String getResultsColumnNames() {
		return "NucleusFileName\t" +
		       "Volume\t" +
		       "Flatness\t" +
		       "Elongation\t" +
		       "Esr\t" +
		       "SurfaceArea\t" +
		       "Sphericity\t" +
		       "MeanIntensityNucleus\t" +
		       "MeanIntensityBackground\t" +
		       "StandardDeviation\t" +
		       "MinIntensity\t" +
		       "MaxIntensity\t" +
		       "MedianIntensityImage\t" +
		       "MedianIntensityNucleus\t" +
		       "MedianIntensityBackground\t" +
		       "ImageSize\t" +
		       "VolumeRHF\t" +
		       "NbCc\t" +
		       "VCcMean\t" +
		       "VCcTotal\t" +
		       "normIntensityMean\t" +
		       "DistanceBorderToBorderMean\t" +
		       "DistanceBarycenterToBorderMean\t" +
		       "VoxelVolume\n";
	}
	
	public String getResultsColumnNamesCC() {
		return "NucleusFileName\t" +
		       "Volume\t" +
		       "NormIntensity\t" +
		       "BorderToBorderDistance\t" +
		       "BarycenterToBorderDistance\t" +
		       "BarycenterToBorderDistanceNucleus\n";
	}
}