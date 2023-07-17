package gred.nucleus.core;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import gred.nucleus.utils.Gradient;
import gred.nucleus.utils.Histogram;
import gred.nucleus.utils.VoxelRecord;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * Class computing 3D parameters from raw and his segmented image associated :
 * <p>
 * Volume Flatness Elongation Sphericity Esr SurfaceArea SurfaceAreaCorrected SphericityCorrected MeanIntensity
 * StandardDeviation MinIntensity MaxIntensity OTSUThreshold
 * <p>
 * <p>
 * //TODO reecrire cette classe ya des choses que je fais 5 fois c'est inutil
 *
 * @author Tristan Dubos and Axel Poulet
 */
public class Measure3D {
	
	ImagePlus[] imageSeg;
	ImagePlus   rawImage;
	ImagePlus _imageSeg;
	
	double xCal;
	double yCal;
	double zCal;
	
	Map<Double, Integer> segmentedNucleusHistogram = new TreeMap<>();
	Map<Double, Integer> backgroundHistogram       = new TreeMap<>();
	private ImagePlus _rawImage;
	TreeMap< Double, Integer> _segmentedNucleusHisto =new TreeMap <Double, Integer>();
	TreeMap< Double, Integer> _backgroundHisto =new TreeMap <Double, Integer>();
	
	
	public Measure3D() {
	}
	
	
	public Measure3D(double xCal, double yCal, double zCal) {
		this.xCal = xCal;
		this.yCal = yCal;
		this.zCal = zCal;
		
	}
	
	
	public Measure3D(ImagePlus[] imageSeg, ImagePlus rawImage, double xCal, double yCal, double zCal) {
		this.rawImage = rawImage;
		this.imageSeg = imageSeg;
		this.xCal = xCal;
		this.yCal = yCal;
		this.zCal = zCal;
	}
	public Measure3D(ImagePlus imageSeg, ImagePlus rawImage, double xCal, double ycal, double zCal) {
		this._rawImage = rawImage;
		this._imageSeg = imageSeg;
		this.xCal = xCal;
		this.yCal = ycal;
		this.zCal = zCal;
		this.histogramSegmentedNucleus2();
	}
	
	/**
	 * Scan of image and if the voxel belong to the object of interest, looking, if in his neighborhood there are voxel
	 * value == 0 then it is a boundary voxel. Adding the surface of the face of the voxel frontier, which are in
	 * contact with the background of the image, to the surface total.
	 *
	 * @param label label of the interest object
	 *
	 * @return the surface
	 */
	public double computeSurfaceObject(double label) {
		ImageStack imageStackInput = this.imageSeg[0].getStack();
		double     surfaceArea     = 0, voxelValue, neighborVoxelValue;
		for (int k = 1; k < this.imageSeg[0].getStackSize(); ++k) {
			for (int i = 1; i < this.imageSeg[0].getWidth(); ++i) {
				for (int j = 1; j < this.imageSeg[0].getHeight(); ++j) {
					voxelValue = imageStackInput.getVoxel(i, j, k);
					if (voxelValue == label) {
						for (int kk = k - 1; kk <= k + 1; kk += 2) {
							neighborVoxelValue = imageStackInput.getVoxel(i, j, kk);
							if (voxelValue != neighborVoxelValue) {
								surfaceArea += this.xCal * this.yCal;
							}
						}
						for (int ii = i - 1; ii <= i + 1; ii += 2) {
							neighborVoxelValue = imageStackInput.getVoxel(ii, j, k);
							if (voxelValue != neighborVoxelValue) {
								surfaceArea += this.yCal * this.zCal;
							}
						}
						for (int jj = j - 1; jj <= j + 1; jj += 2) {
							neighborVoxelValue = imageStackInput.getVoxel(i, jj, k);
							if (voxelValue != neighborVoxelValue) {
								surfaceArea += this.xCal * this.zCal;
							}
						}
					}
				}
			}
		}
		return surfaceArea;
	}
	
	
	/**
	 * This Method compute the volume of each segmented objects in imagePlus
	 *
	 * @param imagePlusInput ImagePlus segmented image
	 *
	 * @return double table which contain the volume of each image object
	 */
	public double[] computeVolumeOfAllObjects(ImagePlus imagePlusInput) {
		
		Histogram histogram = new Histogram();
		histogram.run(imagePlusInput);
		double[]             tlabel        = histogram.getLabels();
		double[]             tObjectVolume = new double[tlabel.length];
		Map<Double, Integer> histo         = histogram.getHistogram();
		for (int i = 0; i < tlabel.length; ++i) {
			int nbVoxel = histo.get(tlabel[i]);
			tObjectVolume[i] = nbVoxel * this.xCal * this.yCal * this.zCal;
		}
		return tObjectVolume;
	}
	
	
	/**
	 * Compute the volume of one object with this label
	 *
	 * @param label double label of the object of interest
	 *
	 * @return double: the volume of the label of interest
	 */
	public double computeVolumeObject2(double label) {
		Histogram histogram = new Histogram();
		histogram.run(this.imageSeg[0]);
		Map<Double, Integer> hashMapHistogram = histogram.getHistogram();
		
		return hashMapHistogram.get(label) * this.xCal * this.yCal * this.zCal;
		
	}
	
	
	private double computeVolumeObjectML() {
		double volumeTMP = 0.0;
		for (Map.Entry<Double, Integer> toto : this.segmentedNucleusHistogram.entrySet()) {
			if (toto.getValue() > 0) {
				volumeTMP += toto.getValue();
			}
		}
		return volumeTMP * this.xCal * this.yCal * this.zCal;
	}
	
	
	/**
	 * Compute the volume of one object with this label
	 *
	 * @param imagePlusInput ImagePLus of the segmented image
	 * @param label          double label of the object of interest
	 *
	 * @return double: the volume of the label of interest
	 */
	public double computeVolumeObject(ImagePlus imagePlusInput, double label) {
		Histogram histogram = new Histogram();
		histogram.run(imagePlusInput);
		Map<Double, Integer> hashMapHistogram = histogram.getHistogram();
		return hashMapHistogram.get(label) * this.xCal * this.yCal * this.zCal;
	}
	
	
	/**
	 * compute the equivalent spherical radius
	 *
	 * @param volume double of the volume of the object of interesr
	 *
	 * @return double the equivalent spherical radius
	 */
	public double equivalentSphericalRadius(double volume) {
		double radius = (3 * volume) / (4 * Math.PI);
		radius = Math.pow(radius, 1.0 / 3.0);
		return radius;
	}
	
	
	/**
	 * compute the equivalent spherical radius with ImagePlus in input
	 *
	 * @param imagePlusBinary ImagePlus of the segmented image
	 *
	 * @return double the equivalent spherical radius
	 */
	public double equivalentSphericalRadius(ImagePlus imagePlusBinary) {
		double radius = (3 * computeVolumeObject(imagePlusBinary, 255))
		                / (4 * Math.PI);
		radius = Math.pow(radius, 1.0 / 3.0);
		return radius;
	}
	
	
	/**
	 * Method which compute the sphericity : 36Pi*Volume^2/Surface^3 = 1 if perfect sphere
	 *
	 * @param volume  double volume of the object
	 * @param surface double surface of the object
	 *
	 * @return double sphercity
	 */
	public double computeSphericity(double volume, double surface) {
		return ((36 * Math.PI * (volume * volume))
		        / (surface * surface * surface));
	}
	
	
	/**
	 * Method which compute the eigen value of the matrix (differences between the coordinates of all points and the
	 * barycenter. Obtaining a symmetric matrix : xx xy xz xy yy yz xz yz zz Compute the eigen value with the pakage
	 * JAMA
	 *
	 * @param label double label of interest
	 *
	 * @return double table containing the 3 eigen values
	 */
	public double[] computeEigenValue3D(double label) {
		ImageStack  imageStackInput = this.imageSeg[0].getImageStack();
		VoxelRecord barycenter      = computeBarycenter3D(true, this.imageSeg[0], label);
		
		double xx      = 0;
		double xy      = 0;
		double xz      = 0;
		double yy      = 0;
		double yz      = 0;
		double zz      = 0;
		int    counter = 0;
		double voxelValue;
		for (int k = 0; k < this.imageSeg[0].getStackSize(); ++k) {
			double dz = ((this.zCal * (double) k) - barycenter.getK());
			for (int i = 0; i < this.imageSeg[0].getWidth(); ++i) {
				double dx = ((this.xCal * (double) i) - barycenter.getI());
				for (int j = 0; j < this.imageSeg[0].getHeight(); ++j) {
					voxelValue = imageStackInput.getVoxel(i, j, k);
					if (voxelValue == label) {
						double dy = ((this.yCal * (double) j) - barycenter.getJ());
						xx += dx * dx;
						yy += dy * dy;
						zz += dz * dz;
						xy += dx * dy;
						xz += dx * dz;
						yz += dy * dz;
						counter++;
					}
				}
			}
		}
		double[][] tValues = {{xx / counter, xy / counter, xz / counter},
		                      {xy / counter, yy / counter, yz / counter},
		                      {xz / counter, yz / counter, zz / counter}};
		Matrix matrix = new Matrix(tValues);
		
		EigenvalueDecomposition eigenValueDecomposition = matrix.eig();
		return eigenValueDecomposition.getRealEigenvalues();
	}
	
	
	/**
	 * Compute the flatness and the elongation of the object of interest
	 *
	 * @param label double label of interest
	 *
	 * @return double table containing in [0] flatness and in [1] elongation
	 */
	public double[] computeFlatnessAndElongation(double label) {
		double[] shapeParameters = new double[2];
		double[] tEigenValues    = computeEigenValue3D(label);
		shapeParameters[0] = Math.sqrt(tEigenValues[1] / tEigenValues[0]);
		shapeParameters[1] = Math.sqrt(tEigenValues[2] / tEigenValues[1]);
		return shapeParameters;
	}
	
	
	/**
	 * Method which determines object barycenter
	 *
	 * @param unit           if true the coordinates of barycenter are in µm.
	 * @param imagePlusInput ImagePlus of labelled image
	 * @param label          double label of interest
	 *
	 * @return VoxelRecord the barycenter of the object of interest
	 */
	public VoxelRecord computeBarycenter3D(boolean unit,
	                                       ImagePlus imagePlusInput,
	                                       double label) {
		ImageStack  imageStackInput       = imagePlusInput.getImageStack();
		VoxelRecord voxelRecordBarycenter = new VoxelRecord();
		int         count                 = 0;
		int         sx                    = 0;
		int         sy                    = 0;
		int         sz                    = 0;
		double      voxelValue;
		for (int k = 0; k < imagePlusInput.getStackSize(); ++k) {
			for (int i = 0; i < imagePlusInput.getWidth(); ++i) {
				for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
					voxelValue = imageStackInput.getVoxel(i, j, k);
					if (voxelValue == label) {
						sx += i;
						sy += j;
						sz += k;
						++count;
					}
				}
			}
		}
		sx /= count;
		sy /= count;
		sz /= count;
		voxelRecordBarycenter.setLocation(sx, sy, sz);
		if (unit) {
			voxelRecordBarycenter.multiply(this.xCal, this.yCal, this.zCal);
		}
		return voxelRecordBarycenter;
	}
	
	
	/**
	 * Method which compute the barycenter of each objects and return the result in a table of VoxelRecord
	 *
	 * @param imagePlusInput ImagePlus of labelled image
	 * @param unit           if true the coordinates of barycenter are in µm.
	 *
	 * @return table of VoxelRecord for each object of the input image
	 */
	public VoxelRecord[] computeObjectBarycenter(ImagePlus imagePlusInput,
	                                             boolean unit) {
		Histogram histogram = new Histogram();
		histogram.run(imagePlusInput);
		double[]      tLabel       = histogram.getLabels();
		VoxelRecord[] tVoxelRecord = new VoxelRecord[tLabel.length];
		for (int i = 0; i < tLabel.length; ++i) {
			tVoxelRecord[i] = computeBarycenter3D(unit, imagePlusInput, tLabel[i]);
		}
		return tVoxelRecord;
	}
	
	
	/**
	 * Intensity of chromocenters/ intensity of the nucleus
	 *
	 * @param imagePlusInput        ImagePlus raw image
	 * @param imagePlusSegmented    binary ImagePlus
	 * @param imagePlusChromocenter ImagePlus of the chromocemters
	 *
	 * @return double Relative Heterochromatin Fraction compute on the Intensity ratio
	 */
	public double computeIntensityRHF(ImagePlus imagePlusInput
			, ImagePlus imagePlusSegmented, ImagePlus imagePlusChromocenter) {
		double     chromocenterIntensity  = 0;
		double     nucleusIntensity       = 0;
		double     voxelValueChromocenter;
		double     voxelValueInput;
		double     voxelValueSegmented;
		ImageStack imageStackChromocenter = imagePlusChromocenter.getStack();
		ImageStack imageStackSegmented    = imagePlusSegmented.getStack();
		ImageStack imageStackInput        = imagePlusInput.getStack();
		for (int k = 0; k < imagePlusInput.getNSlices(); ++k) {
			for (int i = 0; i < imagePlusInput.getWidth(); ++i) {
				for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
					voxelValueSegmented = imageStackSegmented.getVoxel(i, j, k);
					voxelValueInput = imageStackInput.getVoxel(i, j, k);
					voxelValueChromocenter =
							imageStackChromocenter.getVoxel(i, j, k);
					
					if (voxelValueSegmented > 0) {
						if (voxelValueChromocenter > 0) {
							chromocenterIntensity += voxelValueInput;
						}
						nucleusIntensity += voxelValueInput;
					}
				}
			}
		}
		return chromocenterIntensity / nucleusIntensity;
	}
	
	
	/**
	 * Method which compute the RHF (total chromocenters volume/nucleus volume)
	 *
	 * @param imagePlusSegmented     binary ImagePlus
	 * @param imagePlusChromocenters ImagePLus of the chromocenters
	 *
	 * @return double Relative Heterochromatin Fraction compute on the Volume ratio
	 */
	public double computeVolumeRHF(ImagePlus imagePlusSegmented, ImagePlus imagePlusChromocenters) {
		double   volumeCc            = 0;
		double[] tVolumeChromocenter = computeVolumeOfAllObjects(imagePlusChromocenters);
		for (double v : tVolumeChromocenter) {
			volumeCc += v;
		}
		double[] tVolumeSegmented = computeVolumeOfAllObjects(imagePlusSegmented);
		return volumeCc / tVolumeSegmented[0];
	}
	
	
	/**
	 * Detect the number of object on segmented image.
	 *
	 * @param imagePlusInput Segmented image
	 *
	 * @return int nb of object in the image
	 */
	public int getNumberOfObject(ImagePlus imagePlusInput) {
		Histogram histogram = new Histogram();
		histogram.run(imagePlusInput);
		return histogram.getNbLabels();
	}
	
	
	/**
	 * Method to compute surface of the segmented object using gradient information.
	 *
	 * @return
	 */
	public double computeComplexSurface() {
		Gradient           gradient            = new Gradient(this.rawImage);
		List<Double>[][][] tableUnitary        = gradient.getUnitNormals();
		ImageStack         imageStackSegmented = this.imageSeg[0].getStack();
		double             surfaceArea         = 0, voxelValue, neighborVoxelValue;
		VoxelRecord        voxelRecordIn       = new VoxelRecord();
		VoxelRecord        voxelRecordOut      = new VoxelRecord();
		
		for (int k = 2; k < this.imageSeg[0].getNSlices() - 2; ++k) {
			for (int i = 2; i < this.imageSeg[0].getWidth() - 2; ++i) {
				for (int j = 2; j < this.imageSeg[0].getHeight() - 2; ++j) {
					voxelValue = imageStackSegmented.getVoxel(i, j, k);
					if (voxelValue > 0) {
						for (int kk = k - 1; kk <= k + 1; kk += 2) {
							neighborVoxelValue =
									imageStackSegmented.getVoxel(i, j, kk);
							if (voxelValue != neighborVoxelValue) {
								voxelRecordIn.setLocation(
										i,
										j,
										k);
								voxelRecordOut.setLocation(
										i,
										j,
										kk);
								surfaceArea += computeSurfelContribution(
										tableUnitary[i][j][k],
										tableUnitary[i][j][kk],
										voxelRecordIn,
										voxelRecordOut,
										((this.xCal) * (this.yCal)));
							}
						}
						for (int ii = i - 1; ii <= i + 1; ii += 2) {
							neighborVoxelValue = imageStackSegmented.getVoxel(
									ii, j, k);
							if (voxelValue != neighborVoxelValue) {
								voxelRecordIn.setLocation(
										i,
										j,
										k);
								voxelRecordOut.setLocation(
										ii,
										j,
										k);
								surfaceArea += computeSurfelContribution(
										tableUnitary[i][j][k],
										tableUnitary[ii][j][k],
										voxelRecordIn, voxelRecordOut,
										((this.yCal) * (this.zCal)));
							}
						}
						for (int jj = j - 1; jj <= j + 1; jj += 2) {
							neighborVoxelValue = imageStackSegmented.getVoxel(
									i, jj, k);
							if (voxelValue != neighborVoxelValue) {
								voxelRecordIn.setLocation(
										i,
										j,
										k);
								voxelRecordOut.setLocation(
										i,
										jj,
										k);
								surfaceArea += computeSurfelContribution(
										tableUnitary[i][j][k],
										tableUnitary[i][jj][k],
										voxelRecordIn,
										voxelRecordOut,
										((this.xCal) * (this.zCal)));
							}
						}
					}
				}
			}
		}
		return surfaceArea;
	}
	
	
	/**
	 * Method to compute surface of the segmented object using gradient information.
	 *
	 * @param imagePlusSegmented segmented image
	 * @param gradient           gradient computed from raw images
	 *
	 * @return
	 */
	public double computeComplexSurface(ImagePlus imagePlusSegmented, Gradient gradient) {
		List<Double>[][][] tableUnitary        = gradient.getUnitNormals();
		ImageStack         imageStackSegmented = imagePlusSegmented.getStack();
		double             surfaceArea         = 0, voxelValue, neighborVoxelValue;
		VoxelRecord        voxelRecordIn       = new VoxelRecord();
		VoxelRecord        voxelRecordOut      = new VoxelRecord();
		Calibration        calibration         = imagePlusSegmented.getCalibration();
		double             xCalibration        = calibration.pixelWidth;
		double             yCalibration        = calibration.pixelHeight;
		double             zCalibration        = calibration.pixelDepth;
		for (int k = 2; k < imagePlusSegmented.getNSlices() - 2; ++k) {
			for (int i = 2; i < imagePlusSegmented.getWidth() - 2; ++i) {
				for (int j = 2; j < imagePlusSegmented.getHeight() - 2; ++j) {
					voxelValue = imageStackSegmented.getVoxel(i, j, k);
					if (voxelValue > 0) {
						for (int kk = k - 1; kk <= k + 1; kk += 2) {
							neighborVoxelValue = imageStackSegmented.getVoxel(i, j, kk);
							if (voxelValue != neighborVoxelValue) {
								voxelRecordIn.setLocation(i, j, k);
								voxelRecordOut.setLocation(i, j, kk);
								surfaceArea += computeSurfelContribution(tableUnitary[i][j][k],
								                                         tableUnitary[i][j][kk],
								                                         voxelRecordIn,
								                                         voxelRecordOut,
								                                         ((xCalibration) * (yCalibration)));
							}
						}
						for (int ii = i - 1; ii <= i + 1; ii += 2) {
							neighborVoxelValue = imageStackSegmented.getVoxel(ii, j, k);
							if (voxelValue != neighborVoxelValue) {
								voxelRecordIn.setLocation(i, j, k);
								voxelRecordOut.setLocation(ii, j, k);
								surfaceArea += computeSurfelContribution(tableUnitary[i][j][k],
								                                         tableUnitary[ii][j][k],
								                                         voxelRecordIn,
								                                         voxelRecordOut,
								                                         ((yCalibration) * (zCalibration)));
							}
						}
						for (int jj = j - 1; jj <= j + 1; jj += 2) {
							neighborVoxelValue = imageStackSegmented.getVoxel(i, jj, k);
							if (voxelValue != neighborVoxelValue) {
								voxelRecordIn.setLocation(i, j, k);
								voxelRecordOut.setLocation(i, jj, k);
								surfaceArea += computeSurfelContribution(tableUnitary[i][j][k],
								                                         tableUnitary[i][jj][k],
								                                         voxelRecordIn,
								                                         voxelRecordOut,
								                                         ((xCalibration) * (zCalibration)));
							}
						}
					}
				}
			}
		}
		return surfaceArea;
	}
	
	
	/**
	 * Compute surface contribution of each voxels from gradients.
	 *
	 * @param listUnitaryIn
	 * @param listUnitaryOut
	 * @param voxelRecordIn
	 * @param voxelRecordOut
	 * @param as
	 *
	 * @return
	 */
	private double computeSurfelContribution(List<Double> listUnitaryIn,
	                                         List<Double> listUnitaryOut,
	                                         VoxelRecord voxelRecordIn,
	                                         VoxelRecord voxelRecordOut,
	                                         double as) {
		double dx = voxelRecordIn.i - voxelRecordOut.i;
		double dy = voxelRecordIn.j - voxelRecordOut.j;
		double dz = voxelRecordIn.k - voxelRecordOut.k;
		double nx = (listUnitaryIn.get(0) + listUnitaryOut.get(0)) / 2;
		double ny = (listUnitaryIn.get(1) + listUnitaryOut.get(1)) / 2;
		double nz = (listUnitaryIn.get(2) + listUnitaryOut.get(2)) / 2;
		return Math.abs((dx * nx + dy * ny + dz * nz) * as);
	}
	
	
	/**
	 * Compute an Hashmap describing the segmented object (from raw data). Key = Voxels intensity value = Number of
	 * voxels
	 * <p>
	 * If voxels ==255 in seg image add Hashmap (Voxels intensity ,+1)
	 */
	private void histogramSegmentedNucleus() {
		ImageStack imageStackRaw = this.rawImage.getStack();
		ImageStack imageStackSeg = this.imageSeg[0].getStack();
		Histogram  histogram     = new Histogram();
		histogram.run(this.rawImage);
		for (int k = 0; k < this.rawImage.getStackSize(); ++k) {
			for (int i = 0; i < this.rawImage.getWidth(); ++i) {
				for (int j = 0; j < this.rawImage.getHeight(); ++j) {
					double voxelValue = imageStackSeg.getVoxel(i, j, k);
					if (voxelValue == 255) {
						if (!this.segmentedNucleusHistogram.containsKey(imageStackRaw.getVoxel(i, j, k))) {
							this.segmentedNucleusHistogram.put(imageStackRaw.getVoxel(i, j, k), 1);
						} else {
							this.segmentedNucleusHistogram.put(imageStackRaw.getVoxel(i, j, k),
							                                   this.segmentedNucleusHistogram.get(imageStackRaw.getVoxel(
									                                   i,
									                                   j,
									                                   k)) +
							                                   1);
						}
					} else {
						if (!this.backgroundHistogram.containsKey(imageStackRaw.getVoxel(i, j, k))) {
							this.backgroundHistogram.put(imageStackRaw.getVoxel(i, j, k), 1);
						} else {
							this.backgroundHistogram.put(imageStackRaw.getVoxel(i, j, k),
							                             this.backgroundHistogram.get(imageStackRaw.getVoxel(i,
							                                                                                 j,
							                                                                                 k)) + 1);
						}
					}
				}
			}
		}
	}
	private void histogramSegmentedNucleus2() {
		
		ImageStack imageStackRaw = this._rawImage.getStack();
		ImageStack imageStackSeg = this._imageSeg.getStack();
		Histogram histogram = new Histogram();
		histogram.run(this._rawImage);
		for(int k = 0; k < this._rawImage.getStackSize(); ++k) {
			for (int i = 0; i < this._rawImage.getWidth(); ++i) {
				for (int j = 0; j < this._rawImage.getHeight(); ++j) {
					double voxelValue = imageStackSeg.getVoxel(i, j, k);
					if (voxelValue ==255) {
						if(!this._segmentedNucleusHisto.containsKey(
								imageStackRaw.getVoxel(i, j, k)) ){
							this._segmentedNucleusHisto.put(
									imageStackRaw.getVoxel(i, j, k),  1);
						}
						else{
							this._segmentedNucleusHisto.put(
									imageStackRaw.getVoxel(i, j, k),
									this._segmentedNucleusHisto.get(
											imageStackRaw.getVoxel(i, j, k)) + 1);
						}
					}
					else{
						if(!this._backgroundHisto.containsKey(
								imageStackRaw.getVoxel(i, j, k)) ){
							this._backgroundHisto.put(
									imageStackRaw.getVoxel(i, j, k),  1);
						}
						else{
							this._backgroundHisto.put(
									imageStackRaw.getVoxel(i, j, k),
									this._backgroundHisto.get(
											imageStackRaw.getVoxel(i, j, k)) + 1);
						}
					}
				}
			}
		}
		
	}
	
	
	/**
	 * Compute the mean intensity of the segmented object by comparing voxels intensity in the raw image and
	 * white/segmented voxels the segmented image.
	 *
	 * @return mean intensity of segmented object
	 */
	private double meanIntensity() {
		int    numberOfVoxel = 0;
		double mean          = 0;
		for (Map.Entry<Double, Integer> histogram : this.segmentedNucleusHistogram.entrySet()) {
			numberOfVoxel += histogram.getValue();
			mean += histogram.getKey() * histogram.getValue();
		}
		return mean / numberOfVoxel;
	}
	
	
	/**
	 * Compute mean intensity of background
	 *
	 * @return mean intensity of background
	 */
	private double meanIntensityBackground() {
		double     meanIntensity = 0;
		int        voxelCounted  = 0;
		ImageStack imageStackRaw = this.rawImage.getStack();
		ImageStack imageStackSeg = this.imageSeg[0].getStack();
		for (int k = 0; k < this.rawImage.getStackSize(); ++k) {
			for (int i = 0; i < this.rawImage.getWidth(); ++i) {
				for (int j = 0; j < this.rawImage.getHeight(); ++j) {
					if (imageStackSeg.getVoxel(i, j, k) == 0) {
						meanIntensity += imageStackRaw.getVoxel(i, j, k);
						voxelCounted++;
					}
				}
			}
		}
		meanIntensity /= voxelCounted;
		return meanIntensity;
	}
	
	
	/**
	 * Compute the standard deviation of the mean intensity
	 *
	 * @return the standard deviation of the mean intensity of segmented object
	 *
	 * @see Measure3D#meanIntensity()
	 */
	private double standardDeviationIntensity(Double mean) {
		int    numberOfVoxel = 0;
		double std           = 0;
		for (Map.Entry<Double, Integer> histogram : this.segmentedNucleusHistogram.entrySet()) {
			numberOfVoxel += histogram.getValue();
			std = Math.abs((histogram.getKey() * histogram.getValue()) - (histogram.getValue() * mean));
		}
		return std / (numberOfVoxel - 1);
		
		
	}
	
	
	/**
	 * Find the maximum intensity voxel of segmented object
	 *
	 * @return the maximum intensity voxel of segmented object
	 */
	private double maxIntensity() {
		double maxIntensity = 0;
		for (Map.Entry<Double, Integer> entry : this.segmentedNucleusHistogram.entrySet()) {
			if (maxIntensity == 0 || entry.getKey().compareTo(maxIntensity) > 0) {
				maxIntensity = entry.getKey();
			}
		}
		return maxIntensity;
		
	}
	
	
	/**
	 * Find the minimum intensity voxel of segmented object
	 *
	 * @return the minimum intensity voxel of segmented object
	 */
	private double minIntensity() {
		Iterator<Map.Entry<Double, Integer>> iterator     = segmentedNucleusHistogram.entrySet().iterator();
		int                                  count        = 0;
		double                               minIntensity = 0;
		while (iterator.hasNext() && count == 0) {
			Map.Entry<Double, Integer> pair = iterator.next();
			count = pair.getValue();
			minIntensity = pair.getKey();
		}
		return minIntensity;
	}
	
	
	/**
	 * Compute the median intensity value of raw image voxel
	 *
	 * @return median intensity value of raw image voxel
	 */
	public double medianComputingImage() {
		double    voxelMedianValue = 0;
		Histogram histogram        = new Histogram();
		histogram.run(this.rawImage);
		Map<Double, Integer> nucleusHistogram = histogram.getHistogram();
		
		int     size      = rawImage.getHeight() * rawImage.getWidth() * rawImage.getNSlices();
		int     increment = 0;
		boolean even      = false;
		for (Entry<Double, Integer> entry : nucleusHistogram.entrySet()) {
			increment += entry.getValue();
			if (size == 2 * increment) {
				voxelMedianValue = entry.getKey();
				even = true;
			} else if (size < 2 * increment) {
				voxelMedianValue += entry.getKey();
				if (even) voxelMedianValue /= 2;
				break;
			}
		}
		return voxelMedianValue;
	}
	
	
	private double medianIntensityNucleus() {
		double voxelMedianValue = 0;
		int    nbNucleusVoxels  = 0;
		for (int f : this.segmentedNucleusHistogram.values()) {
			nbNucleusVoxels += f;
		}
		int     increment = 0;
		boolean even      = false;
		for (Map.Entry<Double, Integer> entry : segmentedNucleusHistogram.entrySet()) {
			increment += entry.getValue();
			if (nbNucleusVoxels == 2 * increment) {
				voxelMedianValue = entry.getKey();
				even = true;
			} else if (nbNucleusVoxels < 2 * increment) {
				voxelMedianValue += entry.getKey();
				if (even) voxelMedianValue /= 2;
				break;
			}
		}
		return voxelMedianValue;
	}
	
	
	private double medianIntensityBackground() {
		double voxelMedianValue   = 0;
		int    nbBackgroundVoxels = 0;
		for (int f : this.segmentedNucleusHistogram.values()) {
			nbBackgroundVoxels += f;
		}
		int     increment = 0;
		boolean even      = false;
		for (Map.Entry<Double, Integer> entry : this.backgroundHistogram.entrySet()) {
			increment += entry.getValue();
			if (nbBackgroundVoxels == 2 * increment) {
				voxelMedianValue = entry.getKey();
				even = true;
			} else if (nbBackgroundVoxels < 2 * increment) {
				voxelMedianValue += entry.getKey();
				if (even) voxelMedianValue /= 2;
				break;
			}
		}
		return voxelMedianValue;
	}
	
	
	/**
	 * list of parameters compute in this method returned in tabulated format
	 *
	 * @return list of parameters compute in this method returned in tabulated format
	 */
	public String nucleusParameter3D() {
		String results;
		histogramSegmentedNucleus();
		// double volume = computeVolumeObject2(255);
		
		double   volume         = computeVolumeObjectML();
		double   surfaceArea    = computeSurfaceObject(255);
		double   surfaceAreaNew = computeComplexSurface();
		double[] tEigenValues   = computeEigenValue3D(255);
		results = this.rawImage.getTitle() + ","
		          //  + computeVolumeObject2(255) + "\t"
		          + computeVolumeObjectML() + ","
				+ computeEigenValue3D(255)[0] + ","
				+ computeEigenValue3D(255)[1] + ","
				+ computeEigenValue3D(255)[2] + ","
		          + computeFlatnessAndElongation(255)[0] + ","
		          + computeFlatnessAndElongation(255)[1] +","
		          + equivalentSphericalRadius(volume) + ","
		          + surfaceAreaNew + ","
		          + computeSphericity(volume, surfaceAreaNew) + ","
		          + meanIntensity() + ","
		          + meanIntensityBackground() + ","
		          + standardDeviationIntensity(meanIntensity()) + ","
		          + minIntensity() + ","
		          + maxIntensity() + ","
		          + medianComputingImage() +","
		          + medianIntensityNucleus() +","
		          + medianIntensityBackground() + ","
		          + this.rawImage.getHeight() * this.rawImage.getWidth() * this.rawImage.getNSlices()+",";
		return results;
	}
	
}