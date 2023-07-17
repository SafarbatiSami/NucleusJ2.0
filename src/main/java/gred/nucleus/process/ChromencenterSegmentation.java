package gred.nucleus.process;

import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.plugin.GaussianBlur3D;
import ij.plugin.filter.GaussianBlur;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.binary.BinaryImages;
import gred.nucleus.plugins.ChromocenterParameters;
import gred.nucleus.utils.Rd2ToTif;
import gred.nucleus.utils.Histogram;
import java.util.Map;
import java.util.TreeMap;


public class ChromencenterSegmentation {
	/**
	 * Parent class for chromocenter  segmentation
	 */
	private int _nbPixelNuc;
	private double _avgNucIntensity;
	private double _stdDevNucIntensity;
	
	private String _output;
	private double _threshold;
	private ImagePlus[] _raw;
	private ImagePlus[] _segNuc;
	private ChromocenterParameters _chromocenterParameters;
	private int _neigh;
	private double _factor;
	
	/**
	 * Contructor for 2D images
	 *
	 * @param raw    : raw image input
	 * @param segNuc : segmented/Binary nucleus associated image
	 */
	public ChromencenterSegmentation(ImagePlus[] raw,
	                                 ImagePlus[] segNuc,
	                                 String outputFileName,
	                                 ChromocenterParameters chromocenterParameters,
	                                 boolean image2D
	                                ) {
		_chromocenterParameters=chromocenterParameters;
		this._raw = raw;
		this._segNuc = segNuc;
		this._output = outputFileName;
		setNbPixelNuc2D();
		if (this._nbPixelNuc* getPixelSurface2D() > 30) {
			this._neigh = (int) (this._chromocenterParameters._neigh*2.5);
			this._factor = this._chromocenterParameters._factor+1;
		}else{
			this._neigh = this._chromocenterParameters._neigh;
			this._factor = this._chromocenterParameters._factor;
		}
	}
	
	/**
	 * Contructor for 3D images
	 *
	 * @param raw    : raw image input
	 * @param segNuc : segmented/Binary nucleus associated image
	 */
	public ChromencenterSegmentation(ImagePlus[] raw,
	                                 ImagePlus[] segNuc,
	                                 String outputFileName,
	                                 ChromocenterParameters chromocenterParameters
	                                ) {
		_chromocenterParameters=chromocenterParameters;
		this._raw = raw;
		this._segNuc = segNuc;
		this._output = outputFileName;
		setNbPixelNuc3D();
		int initialV = chromocenterParameters._neigh;
		System.out.print("\t"+initialV);
		if (!_chromocenterParameters._noChange && this._nbPixelNuc*getVoxelVolume3D() > 50) {
			this._neigh = (int) (this._chromocenterParameters._neigh*2.5);
			this._factor = this._chromocenterParameters._factor+1;
		}else{
			this._neigh = this._chromocenterParameters._neigh;
			this._factor = this._chromocenterParameters._factor;
		}
		System.out.print("\t"+this._neigh+" "+this._factor+"\n");
	}
	
	
	/**
	 * Method running chromocenters segmentation.
	 * Simple algorithms description :
	 * 1- Gaussian blur
	 * 2- Image gradient : for each voxels computing sum of difference with
	 * X (_neigh parameter) neighborhood.
	 * 3- Thresholding value : keep voxels having value higher mean nucleus
	 * intensity plus X (_factor parameter) standard deviation value
	 * 4- Binarization of threshold image
	 * 5- Connected component computation from binarized image
	 */
	
	public void runCC2D(String pathGradient) {
		GaussianBlur gb = new GaussianBlur();
		if (this._chromocenterParameters._gaussianOnRaw) {
			gb.blurGaussian(this._raw[0].getProcessor(), (int) this._chromocenterParameters._gaussianBlurXsigma, (int) this._chromocenterParameters._gaussianBlurXsigma,1);
		}
		
		ImagePlus imageGradient = this.imgGradient2D();
		Calibration cal = this._raw[0].getCalibration();
		
		gb.blurGaussian(imageGradient.getProcessor(), 1,1,1);
		
		imageGradient.setCalibration(cal);
		
		Rd2ToTif.saveFile(imageGradient, pathGradient);
		
		this.computeAverage(imageGradient);
		this.computeStdDev(imageGradient);
		
		this._threshold = this._avgNucIntensity + this._factor * this._stdDevNucIntensity;
		System.out.println(this._output + " " + this._threshold + " avg " + this._avgNucIntensity + " std " + this._stdDevNucIntensity);
		
		
		// CC sega
		imageGradient = binarize2D(imageGradient);
		imageGradient.setCalibration(cal);
		if(this._chromocenterParameters._sizeFilterConnectedComponent){
			imageGradient=componentSizeFilter2D(imageGradient);
		}
		Rd2ToTif.saveFile(imageGradient, this._output);
	}
	
	/**
	 * Method running chromocenters segmentation.
	 * Simple algorithms description :
	 * 1- Gaussian blur
	 * 2- Image gradient : for each voxels computing sum of difference with
	 * X (_neigh parameter) neighborhood.
	 * 3- Thresholding value : keep voxels having value higher mean nucleus
	 * intensity plus X (_factor parameter) standard deviation value
	 * 4- Binarization of threshold image
	 * 5- Connected component computation from binarized image
	 */
	
	public void runCC3D(String pathGradient) {
		if (this._chromocenterParameters._gaussianOnRaw) {
			GaussianBlur3D.blur(this._raw[0],
			                    this._chromocenterParameters._gaussianBlurXsigma,
			                    this._chromocenterParameters._gaussianBlurYsigma,
			                    this._chromocenterParameters._gaussianBlurZsigma);
		}
		
		ImagePlus imageGradient = imgGradient3D();
		Calibration cal = this._raw[0].getCalibration();
		GaussianBlur3D.blur(imageGradient,
		                    this._chromocenterParameters._gaussianBlurXsigma,
		                    this._chromocenterParameters._gaussianBlurYsigma,
		                    this._chromocenterParameters._gaussianBlurZsigma);
		imageGradient.setCalibration(cal);
		String diff = this._output.replace(".tif", "_diff.tif");
		Rd2ToTif.saveFile(imageGradient, pathGradient);
		computeAverage3D(imageGradient);
		computeStdDev3D(imageGradient);
		this._threshold = this._avgNucIntensity + this._factor * this._stdDevNucIntensity;
		System.out.println(this._output + " " + this._threshold + " avg " + this._avgNucIntensity +
		                   " std " + this._stdDevNucIntensity);
		imageGradient = binarize3D(imageGradient);
		imageGradient.setCalibration(cal);
		if(this._chromocenterParameters._sizeFilterConnectedComponent){
			imageGradient=componentSizeFilter3D(imageGradient);
			imageGradient.setCalibration(cal);
		}
		
		Rd2ToTif.saveFile(imageGradient, this._output);
	}
	
	
	/**
	 * Create and save the diff image.
	 * for each pixel compute the new value computing the average subtraction
	 * between the pixel of interest and all pixel inside the neighbor 3
	 *
	 * @return : gradient image
	 */
	private ImagePlus imgGradient2D() {
		ImageProcessor ip = this._raw[0].getProcessor();
		ImageProcessor ipBin = this._segNuc[0].getProcessor();
		FloatProcessor pDiff = new FloatProcessor(this._raw[0].getWidth(), this._raw[0].getHeight());
		
		
		for (int i = 0; i < this._raw[0].getWidth(); ++i) {
			for (int j = 0; j < this._raw[0].getHeight(); ++j) {
				float sum = 0;
				int nb = 0;
				if (ipBin.getf(i, j) > 0) {
					for (int ii = i - this._neigh; ii < i + this._neigh; ++ii) {
						for (int jj = j - this._neigh; jj < j + this._neigh; ++jj) {
							if ((i != ii || j != jj) && (ipBin.getf(ii, jj) > 1) && ii >= 0 && jj >= 0 &&
							    ii < this._raw[0].getWidth() && jj < this._raw[0].getHeight()) {
								
								float valueA = ip.getf(i, j);
								float valueB = ip.getf(ii, jj);
								if (Double.isNaN(ip.getf(i, j))) valueA = 0;
								if (Double.isNaN(ip.getf(ii, jj))) valueB = 0;
								float plop = valueA - valueB;
								
								sum += plop;
								nb++;
							}
						}
					}
					pDiff.setf(i, j, sum / nb);
				}
			}
		}
		ImagePlus imgDiff = new ImagePlus();
		imgDiff.setProcessor(pDiff);
		return imgDiff;
	}
	
	/**
	 * Create and save the diff image.
	 * for each pixel compute the new value computing the average subtraction
	 * between the pixel of interest and all pixel inside the neighbor 3
	 *
	 * @return : gradient image
	 */
	public ImagePlus imgGradient3D() {
		ImageStack is = this._raw[0].getStack();
		ImageStack isBin = this._segNuc[0].getStack();
		ImageStack isDiff = new ImageStack(this._raw[0].getWidth(), this._raw[0].getHeight(), this._raw[0].getNSlices());
		for (int k = 0; k < this._raw[0].getNSlices(); ++k) {
			FloatProcessor ipDiff = new FloatProcessor(is.getWidth(), is.getHeight());
			for (int i = 0; i < this._raw[0].getWidth(); ++i) {
				for (int j = 0; j < this._raw[0].getHeight(); ++j) {
					float sum = 0;
					int nb = 0;
					double valueA = is.getVoxel(i, j, k);
					if (isBin.getVoxel(i, j, k) > 0) {
						for (int kk = k - this._neigh; kk < k + this._neigh; ++kk) {
							for (int ii = i - this._neigh; ii < i + this._neigh; ++ii) {
								for (int jj = j - this._neigh; jj < j + this._neigh; ++jj) {
									if (ii < this._raw[0].getWidth() && jj < this._raw[0].getHeight() && kk < this._raw[0].getNSlices()) {
										if (isBin.getVoxel(ii, jj, kk) > 0) {
											
											double valueB = is.getVoxel(ii, jj, kk);
											if (Double.isNaN(is.getVoxel(i, j, k))) valueA = 0;
											if (Double.isNaN(is.getVoxel(ii, jj, kk))) valueB = 0;
											double plop = valueA - valueB;
											sum += plop;
											
										}
										nb++;
									}
								}
							}
						}
						ipDiff.setf(i, j, sum/nb);
					}
					
				}
			}
			isDiff.setProcessor(ipDiff, k + 1);
		}
		ImagePlus imgDiff = new ImagePlus();
		imgDiff.setStack(isDiff);
		return imgDiff;
	}
	
	
	/**
	 * Method to compute image mean intensity only on nucleus mask.
	 *
	 * @param : raw image
	 */
	private void computeAverage(ImagePlus imgDiff) {
		ImageProcessor ip2D = imgDiff.getProcessor();
		ImageProcessor ipSeg = this._segNuc[0].getProcessor();
		double sum = 0;
		for (int i = 0; i < this._raw[0].getWidth(); ++i) {
			for (int j = 0; j < this._raw[0].getHeight(); ++j) {
				if (ipSeg.getPixelValue(i, j) > 1) {
					sum += ip2D.getPixelValue(i, j);
				}
			}
		}
		this._avgNucIntensity = sum / this._nbPixelNuc;
	}
	
	/**
	 * Method to compute standard deviation of intensity average from nucleus
	 * mask.
	 *
	 * @param : image gradient
	 */
	
	private void computeStdDev(ImagePlus imgDiff) {
		ImageProcessor ip2D = imgDiff.getProcessor();
		ImageProcessor ipSeg = this._segNuc[0].getProcessor();
		double sum = 0;
		for (int i = 0; i < this._raw[0].getWidth(); ++i) {
			for (int j = 0; j < this._raw[0].getHeight(); ++j) {
				if (ipSeg.getPixelValue(i, j) > 1) {
					sum += (ip2D.getPixelValue(i, j) - this._avgNucIntensity) * (ip2D.getPixelValue(i, j) - this._avgNucIntensity);
				}
			}
		}
		this._stdDevNucIntensity = Math.sqrt(sum / this._nbPixelNuc);
	}
	
	/**
	 * Method to compute image mean intensity only on nucleus mask.
	 *
	 * @param : raw image
	 */
	private void computeAverage3D(ImagePlus imgDiff) {
		ImageStack isRaw = imgDiff.getStack();
		ImageStack isSeg = this._segNuc[0].getStack();
		double sum = 0;
		for (int k = 0; k < this._raw[0].getNSlices(); ++k) {
			for (int i = 0; i < this._raw[0].getWidth(); ++i) {
				for (int j = 0; j < this._raw[0].getHeight(); ++j) {
					if (isSeg.getVoxel(i, j, k) > 1) {
						sum += isRaw.getVoxel(i, j, k);
					}
				}
			}
		}
		this._avgNucIntensity = sum / this._nbPixelNuc;
	}
	
	
	/**
	 * Method to compute standard deviation of intensity average from nucleus
	 * mask.
	 *
	 * @param : image gradient
	 */
	private void computeStdDev3D(ImagePlus imgDiff) {
		ImageStack is = imgDiff.getStack();
		ImageStack isSeg = this._segNuc[0].getStack();
		double sum = 0;
		for (int k = 0; k < this._raw[0].getNSlices(); ++k) {
			for (int i = 0; i < this._raw[0].getWidth(); ++i) {
				for (int j = 0; j < this._raw[0].getHeight(); ++j) {
					if (isSeg.getVoxel(i, j, k) > 1) {
						sum += (is.getVoxel(i, j, k) - _avgNucIntensity) *
						       (is.getVoxel(i, j, k) - _avgNucIntensity);
					}
				}
			}
		}
		this._stdDevNucIntensity = Math.sqrt(sum / this._nbPixelNuc);
	}
	
	/**
	 *
	 */
	private void setNbPixelNuc2D() {
		ImageProcessor ip = this._segNuc[0].getProcessor();
		for (int i = 0; i < this._raw[0].getWidth(); ++i) {
			for (int j = 0; j < this._raw[0].getHeight(); ++j) {
				if (ip.get(i, j) > 1)
					this._nbPixelNuc++;
				
			}
			
		}
	}
	
	/**
	 *
	 */
	private void setNbPixelNuc3D() {
		ImageStack isSeg = this._segNuc[0].getStack();
		for (int k = 0; k < this._raw[0].getNSlices(); ++k) {
			for (int i = 0; i < this._raw[0].getWidth(); ++i) {
				for (int j = 0; j < this._raw[0].getHeight(); ++j) {
					if (isSeg.getVoxel(i, j, k) > 1)
						this._nbPixelNuc++;
					
				}
			}
		}
	}
	
	/**
	 * Method to binarize image with threshold
	 *
	 * @param img : image gradient
	 * @return binarized image
	 */
	public ImagePlus binarize2D(ImagePlus img) {
		ImagePlus imgCc = img.duplicate();
		ImageProcessor ip = imgCc.getProcessor();
		
		for (int i = 0; i < this._raw[0].getWidth(); ++i) {
			for (int j = 0; j < this._raw[0].getHeight(); ++j) {
				if (ip.getf(i, j) > _threshold) {
					ip.setf(i, j, 255);
				} else ip.setf(i, j, 0);
			}
		}
		
		imgCc = BinaryImages.componentsLabeling(imgCc, 26, 32);
		return imgCc;
	}
	
	/**
	 * Method to binarize image with threshold
	 *
	 * @param img : image gradient
	 * @return binarized image
	 */
	private ImagePlus binarize3D(ImagePlus img) {
		ImagePlus imgCc = img.duplicate();
		ImageStack is = imgCc.getStack();
		for (int k = 0; k < this._raw[0].getNSlices(); ++k) {
			for (int i = 0; i < this._raw[0].getWidth(); ++i) {
				for (int j = 0; j < this._raw[0].getHeight(); ++j) {
					if (is.getVoxel(i, j, k) > this._threshold) {
						is.setVoxel(i, j, k, 255);
					} else is.setVoxel(i, j, k, 0);
				}
			}
		}
		imgCc = BinaryImages.componentsLabeling(imgCc, 26, 16);
		return imgCc;
		
	}
	
	/**
	 *
	 * @param imageGradient
	 * @return
	 */
	private ImagePlus componentSizeFilter3D(ImagePlus imageGradient) {
		Histogram histogram = new Histogram();
		histogram.run(imageGradient);
		histogram.getHistogram();
		TreeMap<Double, Integer> parcour = histogram.getHistogram();
		ImagePlus                imgCc   = imageGradient.duplicate();
		ImageStack is = imgCc.getStack();
		for (Map.Entry<Double, Integer> entry : parcour.entrySet()) {
			Double cle = entry.getKey();
			Integer valeur = entry.getValue();
			if (((valeur * getVoxelVolume3D() <
			      this._chromocenterParameters._minSizeConnectedComponent) ||
			     (valeur * getVoxelVolume3D() >
			      this._chromocenterParameters._maxSizeConnectedComponent)) && valeur > 1) {
				for (int k = 0; k < this._raw[0].getNSlices(); ++k) {
					for (int i = 0; i < this._raw[0].getWidth(); ++i) {
						for (int j = 0; j < this._raw[0].getHeight(); ++j) {
							if (is.getVoxel(i, j, k) == cle) {
								is.setVoxel(i, j, k, 0);
								
							}
						}
					}
				}
			}
		}
		imgCc = BinaryImages.componentsLabeling(imgCc, 26, 16);
		return imgCc;
	}
	
	/**
	 *
	 * @param imageGradient
	 * @return
	 */
	private ImagePlus componentSizeFilter2D(ImagePlus imageGradient) {
		Histogram histogram = new Histogram();
		histogram.run(imageGradient);
		histogram.getHistogram();
		TreeMap<Double, Integer> parcour = histogram.getHistogram();
		ImagePlus imgCc = imageGradient.duplicate();
		ImageProcessor ip = imgCc.getProcessor();
		for (Map.Entry<Double, Integer> entry : parcour.entrySet()) {
			Double cle = entry.getKey();
			Integer valeur = entry.getValue();
			if (((valeur * getPixelSurface2D() <
			      this._chromocenterParameters._minSizeConnectedComponent) ||
			     (valeur * getPixelSurface2D() >
			      this._chromocenterParameters._maxSizeConnectedComponent)) && valeur > 1) {
				for (int i = 0; i < this._raw[0].getWidth(); ++i) {
					for (int j = 0; j < this._raw[0].getHeight(); ++j) {
						if (ip.getPixelValue(i, j ) == cle) {
							ip.setf(i, j,  0);
							
						}
					}
				}
			}
		}
		imgCc = BinaryImages.componentsLabeling(imgCc, 26, 32);
		return imgCc;
	}
	/**
	 * Compute volume voxel of current image analysed
	 * @return voxel volume
	 */
	public double getVoxelVolume3D(){
		return this._raw[0].getCalibration().pixelWidth*
		       this._raw[0].getCalibration().pixelHeight*
		       this._raw[0].getCalibration().pixelDepth;
	}
	/**
	 * Compute volume voxel of current image analysed
	 * @return voxel volume
	 */
	public double getPixelSurface2D(){
		return this._raw[0].getCalibration().pixelWidth*
		       this._raw[0].getCalibration().pixelHeight;
	}
}
