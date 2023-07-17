package gred.nucleus.autocrop;

import fr.igred.omero.Client;
import fr.igred.omero.exception.AccessException;
import fr.igred.omero.exception.ServiceException;
import fr.igred.omero.repository.DatasetWrapper;
import fr.igred.omero.repository.ImageWrapper;
import fr.igred.omero.roi.ROIWrapper;
import fr.igred.omero.roi.RectangleWrapper;
import fr.igred.omero.roi.ShapeList;
import gred.nucleus.files.Directory;
import gred.nucleus.files.OutputTextFile;
import gred.nucleus.files.OutputTiff;
import gred.nucleus.imageprocessing.Thresholding;
import gred.nucleus.utils.Histogram;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.plugin.ChannelSplitter;
import ij.plugin.Duplicator;
import ij.plugin.GaussianBlur3D;
import inra.ijpb.binary.BinaryImages;
import inra.ijpb.label.LabelImages;
import loci.formats.FormatException;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;


/**
 * Class dedicate to crop nuclei in a isolate file from 3D wide field image from microscopy. The process use a OTSU
 * threshold to detect object on the image. To detect specific object you can use different parameters as filter like -
 * volume of object detected - minimum intensity of object detected - slice used to detect defined OTSU threshold
 * <p>
 * This class output one file per object detected and a tab-delimited file which contains per line the box coordinate of
 * each object.
 * <p>
 * Note : concerning multiple channels images the OTSU threshold is compute on one channel (see
 * ChannelToComputeThreshold) and then boxes coordinate are applied on all channel. You can identify from which channel
 * crop from the file name before file extension you can see C0 for channel 0 for example.
 */
public class AutoCrop {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	/** Column names */
	private static final String HEADERS =
			"FileName\tChannelNumber\tCropNumber\tXStart\tYStart\tZStart\twidth\theight\tdepth\n";
	
	/** the path of the directory where are saved the crop of the object */
	private final String             outputDirPath;
	/** The prefix of the names of the output cropped images, which are automatically numbered */
	private final String             outputFilesPrefix;
	/** List of the path of the output files created by the cropKernels method */
	private final List<String>       outputFile     = new ArrayList<>();
	/** List of boxes coordinates */
	private final List<String>       boxCoordinates = new ArrayList<>();
	/** Parameters crop analyse */
	private final AutocropParameters autocropParameters;
	/** File to process (Image input) */
	File currentFile;
	/** Raw image */
	private ImagePlus        rawImg;
	/** Segmented image */
	private ImagePlus        imageSeg;
	/** Segmented image connect component labelled */
	private ImagePlus        imageSegLabelled;
	/** The path of the image to be processed */
	private String           imageFilePath;
	/** Number of channels in current image */
	private int              channelNumbers   = 1;
	/** Get current info image analyse */
	private String           infoImageAnalysis;
	/** OTSU threshold  used to compute segmented image */
	private int              otsuThreshold;
	/** Slice start to compute OTSU */
	private String           sliceUsedForOTSU;
	/** Default threshold */
	private boolean          defaultThreshold = false;
	/** List of boxes  to crop link to label value */
	private Map<Double, Box> boxes            = new HashMap<>();
	
	
	/**
	 * Autocrop constructor : initialisation of analyse parameter
	 *
	 * @param imageFile                 Current image analyse
	 * @param outputFilesPrefix         Prefix use for output file name
	 * @param autocropParametersAnalyse List of analyse parameter
	 *
	 * @throws IOException
	 * @throws FormatException
	 */
	public AutoCrop(File imageFile, String outputFilesPrefix, AutocropParameters autocropParametersAnalyse)
	throws IOException, FormatException {
		this.autocropParameters = autocropParametersAnalyse;
		this.currentFile = imageFile;
		this.imageFilePath = imageFile.getAbsolutePath();
		this.outputDirPath = this.autocropParameters.getOutputFolder();
		this.outputFilesPrefix = outputFilesPrefix;
		setChannelNumbers();
		if (this.rawImg.getBitDepth() > 8) {
			this.imageSeg =
					Thresholding.contrastAnd8bits(getImageChannel(this.autocropParameters.getChannelToComputeThreshold()));
		} else {
			this.imageSeg = getImageChannel(this.autocropParameters.getChannelToComputeThreshold());
		}
		this.infoImageAnalysis = autocropParametersAnalyse.getAnalysisParameters();
	}
	
	
	public AutoCrop(ImageWrapper image, AutocropParameters autocropParametersAnalyse, Client client)
	throws ServiceException, AccessException, ExecutionException {
		this.currentFile = new File(image.getName());
		this.autocropParameters = autocropParametersAnalyse;
		this.outputDirPath = this.autocropParameters.getOutputFolder();
		this.outputFilesPrefix = FilenameUtils.removeExtension(image.getName());
		setChannelNumbersOMERO(image, client);
		if (this.rawImg.getBitDepth() > 8) {
			this.imageSeg =
					Thresholding.contrastAnd8bits(getImageChannelOMERO(this.autocropParameters.getChannelToComputeThreshold(),
					                                                   image,
					                                                   client));
		} else {
			this.imageSeg = this.rawImg;
		}
		this.imageFilePath = image.getName();
		this.infoImageAnalysis = autocropParametersAnalyse.getAnalysisParameters();
		this.rawImg.setTitle(image.getName()+"-"+image.getId());
	}

	public ImagePlus getRawImage(){
		return rawImg;
	}
	
	public AutoCrop(File imageFile,
	                String outputFilesPrefix,
	                AutocropParameters autocropParametersAnalyse,
	                Map<Double, Box> boxes)
	throws IOException, FormatException {
		this.autocropParameters = autocropParametersAnalyse;
		this.currentFile = imageFile;
		this.imageFilePath = imageFile.getAbsolutePath();
		this.outputDirPath = this.autocropParameters.getOutputFolder();
		this.outputFilesPrefix = outputFilesPrefix;
		setChannelNumbers();
		this.imageSeg = this.rawImg;
		this.infoImageAnalysis = autocropParametersAnalyse.getAnalysisParameters();
		this.boxes = boxes;
	}
	
	
	/**
	 * Method to get specific channel to compute OTSU threshold
	 *
	 * @param channelNumber Number of channel to compute OTSU for crop
	 *
	 * @return image of specific channel
	 *
	 * @throws IOException
	 * @throws FormatException
	 */
	public ImagePlus getImageChannel(int channelNumber) throws IOException, FormatException {
		ImagePlus[] currentImage = BF.openImagePlus(this.imageFilePath);
		currentImage = ChannelSplitter.split(currentImage[0]);
		return currentImage[channelNumber];
	}
	
	
	public ImagePlus getImageChannelOMERO(int channelNumber, ImageWrapper image, Client client)
	throws ServiceException, AccessException, ExecutionException {
		int[] cBound = {channelNumber, channelNumber};
		return image.toImagePlus(client, null, null, cBound, null, null);
	}
	
	
	/**
	 * Method to check multichannel and initialising channelNumbers variable
	 *
	 * @throws IOException
	 * @throws FormatException
	 */
	public void setChannelNumbers() throws IOException, FormatException {
		ImagePlus[] currentImage = BF.openImagePlus(this.imageFilePath);
		currentImage = ChannelSplitter.split(currentImage[0]);
		this.rawImg = currentImage[0];
		if (currentImage.length > 1) {
			this.channelNumbers = currentImage.length;
		}
	}
	
	
	public void setChannelNumbersOMERO(ImageWrapper image, Client client)
	throws ServiceException, AccessException, ExecutionException {
		int[] cBound = {this.autocropParameters.getChannelToComputeThreshold(),
		                this.autocropParameters.getChannelToComputeThreshold()};
		this.rawImg = image.toImagePlus(client, null, null, cBound, null, null);
		this.channelNumbers = image.getPixels().getSizeC();
	}
	
	
	/**
	 * Method computing OTSU threshold and creating segmented image from this threshold. Before OTSU threshold a
	 * Gaussian Blur is applied (case of anisotropic voxels)
	 * <p> TODO add case where voxel are not anisotropic for Gaussian Blur Case where OTSU threshold is under 20
	 * computation using only half of last slice (useful in case of top slice with lot of noise) If OTSU threshold is
	 * still under 20 threshold default threshold value is 20.
	 */
	public void thresholdKernels() {
		LOGGER.info("Thresholding kernels.");
		if (this.imageSeg == null) {
			return;
		}
		this.sliceUsedForOTSU = "default";
		GaussianBlur3D.blur(this.imageSeg, 0.5, 0.5, 1);
		int thresh = Thresholding.computeOTSUThreshold(this.imageSeg);
		if (thresh < this.autocropParameters.getThresholdOTSUComputing()) {
			ImagePlus imp2;
			if (autocropParameters.getSlicesOTSUComputing() == 0) {
				this.sliceUsedForOTSU =
						"Start:" + this.imageSeg.getStackSize() / 2 + "-" + this.imageSeg.getStackSize();
				imp2 = new Duplicator().run(this.imageSeg,
				                            this.imageSeg.getStackSize() / 2,
				                            this.imageSeg.getStackSize());
			} else {
				this.sliceUsedForOTSU = "Start:" +
				                        this.autocropParameters.getSlicesOTSUComputing() +
				                        "-" +
				                        this.imageSeg.getStackSize();
				imp2 = new Duplicator().run(this.imageSeg,
				                            this.autocropParameters.getSlicesOTSUComputing(),
				                            this.imageSeg.getStackSize());
			}
			int thresh2 = Thresholding.computeOTSUThreshold(imp2);
			if (thresh2 < this.autocropParameters.getThresholdOTSUComputing()) {
				thresh = this.autocropParameters.getThresholdOTSUComputing();
				this.defaultThreshold = true;
			} else {
				thresh = thresh2;
			}
		}
		this.otsuThreshold = thresh;
		this.imageSeg = this.generateSegmentedImage(this.imageSeg, thresh);
	}
	
	
	/** MorpholibJ Method computing connected components using OTSU segmented image */
	public void computeConnectedComponent() {
		LOGGER.info("Computing connected components.");
		this.imageSegLabelled = BinaryImages.componentsLabeling(this.imageSeg, 26, 32);
	}
	
	
	/**
	 * Initializes hashMap boxes containing connected components pixel value associate to number of voxels composing it.
	 * Filter connected components based on minimum volume (default 1 ) and maximum volume (default 2147483647)
	 */
	public void componentSizeFilter() {
		LOGGER.info("Filtering components by size.");
		Histogram histogram = new Histogram();
		histogram.run(this.imageSegLabelled);
		Map<Double, Integer> histogramData = histogram.getHistogram();
		for (Map.Entry<Double, Integer> entry : new TreeMap<>(histogramData).entrySet()) {
			Double  key   = entry.getKey();
			Integer value = entry.getValue();
			if (!((value * getVoxelVolume() < this.autocropParameters.getMinVolumeNucleus()) ||
			      (value * getVoxelVolume() > this.autocropParameters.getMaxVolumeNucleus())) && value > 1) {
				Box initializedBox = new Box(Short.MAX_VALUE,
				                             Short.MIN_VALUE,
				                             Short.MAX_VALUE,
				                             Short.MIN_VALUE,
				                             Short.MAX_VALUE,
				                             Short.MIN_VALUE);
				this.boxes.put(key, initializedBox);
			}
		}
		LOGGER.debug("Number of objects found: {}", this.boxes.size());
	}
	
	
	/** MorpholibJ Method filtering border connect component */
	public void componentBorderFilter() {
		LOGGER.info("Filtering components on border.");
		LabelImages.removeBorderLabels(this.imageSegLabelled);
	}
	
	
	/**
	 * Detection of the of the bounding box for each object of the image.
	 * <p>A Connected component detection is done on the imageThresholding and all the object on the border and under
	 * or upper threshold volume are removed.
	 * <p>The coordinates allow the implementation of the box objects which define the bounding box, and these objects
	 * are stored in a List.
	 * <p>In order to use with a grey-level image, use either {@link AutoCrop#thresholdKernels()} or your own
	 * binarization method.
	 */
	public void computeBoxes2() {
		LOGGER.info("Computing boxes.");
		try {
			ImageStack imageStackInput = this.imageSegLabelled.getStack();
			Box        box;
			for (short k = 0; k < this.imageSegLabelled.getNSlices(); ++k) {
				for (short i = 0; i < this.imageSegLabelled.getWidth(); ++i) {
					for (short j = 0; j < this.imageSegLabelled.getHeight(); ++j) {
						if ((imageStackInput.getVoxel(i, j, k) > 0) &&
						    (this.boxes.containsKey(imageStackInput.getVoxel(i, j, k)))) {
							box = this.boxes.get(imageStackInput.getVoxel(i, j, k));
							box.setXMin((short) Math.min(i, box.getXMin()));
							box.setXMax((short) Math.max(i, box.getXMax()));
							box.setYMin((short) Math.min(j, box.getYMin()));
							box.setYMax((short) Math.max(j, box.getYMax()));
							box.setZMin((short) Math.min(k, box.getZMin()));
							box.setZMax((short) Math.max(k, box.getZMax()));
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("An error occurred.", e);
		}
	}
	
	
	/**
	 * Method to add X voxels in x y z around the connected component. X by default is 20 in x y z. Parameter can be
	 * modified in autocrop parameters:
	 * <ul>
	 *     <li>xCropBoxSize</li>
	 *     <li>yCropBoxSize</li>
	 *     <li>zCropBoxSize</li>
	 * </ul>
	 */
	public void addCROPParameter() {
		LOGGER.info("Adding CROP parameter.");
		for (Map.Entry<Double, Box> entry : new TreeMap<>(this.boxes).entrySet()) {
			Box box  = entry.getValue();
			int xMin = (int) box.getXMin() - this.autocropParameters.getXCropBoxSize();
			int yMin = (int) box.getYMin() - this.autocropParameters.getYCropBoxSize();
			int zMin = (int) box.getZMin() - this.autocropParameters.getZCropBoxSize();
			
			xMin = Math.max(1, xMin);
			yMin = Math.max(1, yMin);
			zMin = Math.max(1, zMin);
			
			int width = box.getXMax() + (2 * this.autocropParameters.getXCropBoxSize()) - box.getXMin();
			if (width > imageSeg.getWidth()) {
				width = imageSeg.getWidth() - 1;
			}
			if (width + xMin >= this.imageSeg.getWidth() || width < 0) {
				width = this.imageSeg.getWidth() - xMin;
			}
			int height = box.getYMax() + (2 * this.autocropParameters.getYCropBoxSize()) - box.getYMin();
			if ((height + yMin) >= this.imageSeg.getHeight() || (height < 0)) {
				height = this.imageSeg.getHeight() - yMin;
			}
			int depth = box.getZMax() + (2 * this.autocropParameters.getZCropBoxSize()) - box.getZMin();
			if (depth + zMin >= this.imageSeg.getNSlices() || depth < 0) {
				depth = this.imageSeg.getNSlices() - zMin;
			}
			box.setXMin((short) xMin);
			box.setXMax((short) (xMin + width));
			box.setYMin((short) yMin);
			box.setYMax((short) (yMin + height));
			box.setZMin((short) zMin);
			box.setZMax((short) (zMin + depth));
			entry.setValue(box);
		}
	}
	
	
	/**
	 * Method crops a box of interest, create and save a new small image.
	 * <p>This process allow the crop of all the bounding box contained in the input ArrayList and the crop is did on
	 * the ImageCore put in input in this method (crop method available in the imagej wrapper).
	 * <p>Then the image results obtained was used to create a new ImageCoreIJ, and the image is saved.
	 */
	public void cropKernels2() throws IOException, FormatException {
		LOGGER.info("Cropping kernels (2).");
		StringBuilder info      = new StringBuilder();
		Directory     dirOutput = new Directory(this.outputDirPath + "nuclei");
		dirOutput.checkAndCreateDir();
		info.append(getSpecificImageInfo()).append(HEADERS);
		for (int c = 0; c < this.channelNumbers; c++) {
			for (Map.Entry<Double, Box> entry : new TreeMap<>(this.boxes).entrySet()) {
				int i = entry.getKey().intValue();
				LOGGER.info("Processing box number: {}", i);
				Box       box    = entry.getValue();
				int       xMin   = box.getXMin();
				int       yMin   = box.getYMin();
				int       zMin   = box.getZMin();
				int       width  = box.getXMax() - box.getXMin() + 1;
				int       height = box.getYMax() - box.getYMin() + 1;
				int       depth  = box.getZMax() - box.getZMin() + 1;
				ImagePlus croppedImage;
				if (this.rawImg.getNSlices() > 1) {
					croppedImage = cropImage(xMin, yMin, zMin, width, height, depth, c);
				} else {
					croppedImage = cropImage2D(xMin, yMin, width, height, c);
				}
				Calibration cal = this.rawImg.getCalibration();
				croppedImage.setCalibration(cal);
				String tiffPath = dirOutput.getDirPath() + File.separator +
				                  this.outputFilesPrefix +
				                  "_" + String.format("%02d", i) + ".tif";
				OutputTiff fileOutput = new OutputTiff(tiffPath);
				info.append(tiffPath).append("\t")
				    .append(c).append("\t")
				    .append(i).append("\t")
				    .append(xMin).append("\t")
				    .append(yMin).append("\t")
				    .append(zMin).append("\t")
				    .append(width).append("\t")
				    .append(height).append("\t")
				    .append(depth).append("\n");
				fileOutput.saveImage(croppedImage);
				this.outputFile.add(this.outputDirPath + File.separator +
				                    this.outputFilesPrefix + File.separator +
				                    this.outputFilesPrefix + "_" +
				                    String.format("%02d", i) + ".tif");
				if (c == 0) {
					int xMax = xMin + width;
					int yMax = yMin + height;
					int zMax = zMin + depth;
					this.boxCoordinates.add(this.outputDirPath + File.separator +
					                        this.outputFilesPrefix + "_" +
					                        String.format("%02d", i) + "_C0" + "\t" +
					                        xMin + "\t" +
					                        xMax + "\t" +
					                        yMin + "\t" +
					                        yMax + "\t" +
					                        zMin + "\t" +
					                        zMax);
				}
			}
		}
		this.infoImageAnalysis += info.toString();
	}
	
	
	public void cropKernelsOMERO(ImageWrapper image, Long[] outputsDat, Client client)
	throws Exception {
		LOGGER.info("Cropping kernels (OMERO).");
		StringBuilder info = new StringBuilder();
		info.append(getSpecificImageInfo()).append(HEADERS);
		for (int c = 0; c < this.channelNumbers; c++) {
			DatasetWrapper dataset = client.getDataset(outputsDat[c]);
			List<ROIWrapper> rois = new ArrayList<>(this.boxes.size());
			for (Map.Entry<Double, Box> entry : new TreeMap<>(this.boxes).entrySet()) {
				int i = entry.getKey().intValue();
				LOGGER.info("Processing box number: {} (OMERO)", i);
				
				Box    box         = entry.getValue();
				int    xMin        = box.getXMin();
				int    yMin        = box.getYMin();
				int    zMin        = box.getZMin();
				int       width  = box.getXMax() - box.getXMin() + 1;
				int       height = box.getYMax() - box.getYMin() + 1;
				int       depth  = box.getZMax() - box.getZMin() + 1;

				ShapeList shapes = new ShapeList();
				for (int z = box.getZMin(); z < box.getZMax(); z++) {
					RectangleWrapper rectangle = new RectangleWrapper(xMin, yMin, width, height);
					rectangle.setCZT(c, z, 0);
					rectangle.setText(String.valueOf(i));
					rectangle.setFontSize(45);
					rectangle.setStroke(Color.GREEN);
					shapes.add(rectangle);
				}
				ROIWrapper roi = new ROIWrapper(shapes);
				roi.setName(String.valueOf(i));
				rois.add(roi);
				ImagePlus   croppedImage = image.toImagePlus(client, roi);
				Calibration cal          = this.rawImg.getCalibration();
				croppedImage.setCalibration(cal);
				String tiffPath = new File(".").getCanonicalPath() +
				                  File.separator +
				                  this.outputFilesPrefix + "_" +
				                  String.format("%02d", i) +".tif";
				OutputTiff fileOutput = new OutputTiff(tiffPath);
				info.append(tiffPath).append("\t")
						.append(c).append("\t")
						.append(i).append("\t")
						.append(xMin).append("\t")
						.append(yMin).append("\t")
						.append(zMin).append("\t")
						.append(width).append("\t")
						.append(height).append("\t")
						.append(depth).append("\n");
				fileOutput.saveImage(croppedImage);
				this.outputFile.add(this.outputFilesPrefix + "_" +
				                    String.format("%02d", i) + ".tif");
				dataset.importImages(client, tiffPath);
				File file = new File(tiffPath);
				try {
					Files.deleteIfExists(file.toPath());
				} catch (Exception e) {
					LOGGER.error("File not deleted: " + tiffPath, e);
				}
				if (c == 0) {
					int xMax = xMin + width;
					int yMax = yMin + height;
					int zMax = zMin + depth;
					this.boxCoordinates.add(this.outputDirPath + File.separator +
							this.outputFilesPrefix + "_" +
							String.format("%02d", i) + "_C0" + "\t" +
							xMin + "\t" +
							xMax + "\t" +
							yMin + "\t" +
							yMax + "\t" +
							zMin + "\t" +
							zMax);
				}
			}
			List<ROIWrapper> roisGetter = image.getROIs(client);
			if(roisGetter.size() == 0){
				image.saveROIs(client, rois);
			}
		}
		this.infoImageAnalysis += info.toString();
	}
	
	
	/** Method crops a box of interest, from coordinate files. */
	public void cropKernels3() throws IOException, FormatException {
		LOGGER.info("Cropping kernels (3).");
		StringBuilder info      = new StringBuilder();
		Directory     dirOutput = new Directory(this.outputDirPath + File.separator + "Nuclei");
		dirOutput.checkAndCreateDir();
		info.append(getSpecificImageInfo()).append(HEADERS);
		for (int c = 0; c < this.channelNumbers; c++) {
			for (Map.Entry<Double, Box> entry : new TreeMap<>(this.boxes).entrySet()) {
				int i = entry.getKey().intValue();
				LOGGER.info("Processing box number: {}", i);
				Box       box    = entry.getValue();
				int       xMin   = box.getXMin();
				int       yMin   = box.getYMin();
				int       zMin   = box.getZMin();
				int       width  = box.getXMax() - box.getXMin();
				int       height = box.getYMax() - box.getYMin();
				int       depth  = box.getZMax() - box.getZMin();
				ImagePlus croppedImage;
				if (this.rawImg.getNSlices() > 1) {
					croppedImage = cropImage(xMin, yMin, zMin, width, height, depth, c);
				} else {
					croppedImage = cropImage2D(xMin, yMin, width, height, c);
				}
				Calibration cal = this.rawImg.getCalibration();
				croppedImage.setCalibration(cal);
				String tiffPath = dirOutput.getDirPath() + File.separator +
				                  this.outputFilesPrefix +
				                  "_" + String.format("%02d", i) +
				                  "_C" + c + ".tif";
				OutputTiff fileOutput = new OutputTiff(tiffPath);
				info.append(tiffPath).append("\t")
				    .append(c).append("\t")
				    .append(i).append("\t")
				    .append(xMin).append("\t")
				    .append(yMin).append("\t")
				    .append(zMin).append("\t")
				    .append(width).append("\t")
				    .append(height).append("\t")
				    .append(depth).append("\n");
				fileOutput.saveImage(croppedImage);
				this.outputFile.add(this.outputDirPath + File.separator +
				                    this.outputFilesPrefix + File.separator +
				                    this.outputFilesPrefix + "_" +
				                    String.format("%02d", i) + ".tif");
				if (c == 0) {
					int xMax = xMin + width;
					int yMax = yMin + height;
					int zMax = zMin + depth;
					this.boxCoordinates.add(this.outputDirPath + File.separator +
					                        this.outputFilesPrefix +
					                        "_" +
					                        i + "\t" +
					                        xMin + "\t" +
					                        xMax + "\t" +
					                        yMin + "\t" +
					                        yMax + "\t" +
					                        zMin + "\t" +
					                        zMax);
				}
			}
		}
		this.infoImageAnalysis += info.toString();
	}
	
	
	/**
	 * Getter for the outputFile ArrayList
	 *
	 * @return outputFile: ArrayList of String for the path of the output files created.
	 */
	public List<String> getOutputFileList() {
		return this.outputFile;
	}
	
	
	/**
	 * Getter for the boxCoordinates
	 *
	 * @return boxCoordinates: ArrayList of String which contain the coordinates of the boxes
	 */
	public List<String> getFileCoordinates() {
		return this.boxCoordinates;
	}
	
	
	/**
	 * Create binary image with the threshold value gave in input
	 *
	 * @param imagePlusInput ImagePlus raw image to binarize
	 * @param threshold      integer threshold value
	 *
	 * @return
	 */
	private ImagePlus generateSegmentedImage(ImagePlus imagePlusInput, int threshold) {
		ImageStack imageStackInput     = imagePlusInput.getStack();
		ImagePlus  imagePlusSegmented  = imagePlusInput.duplicate();
		ImageStack imageStackSegmented = imagePlusSegmented.getStack();
		for (int k = 0; k < imagePlusInput.getStackSize(); ++k) {
			for (int i = 0; i < imagePlusInput.getWidth(); ++i) {
				for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
					double voxelValue = imageStackInput.getVoxel(i, j, k);
					if (voxelValue >= threshold) {
						imageStackSegmented.setVoxel(i, j, k, 255);
					} else {
						imageStackSegmented.setVoxel(i, j, k, 0);
					}
				}
			}
		}
		return imagePlusSegmented;
	}
	
	
	/**
	 * Crop of the bounding box on 3D image. The coordinates are inputs of this methods
	 *
	 * @param xMin:          coordinate x min of the crop
	 * @param yMin:          coordinate y min of the crop
	 * @param zMin:          coordinate z min of the crop
	 * @param width:         coordinate x max of the crop
	 * @param height:        coordinate y max of the crop
	 * @param depth:         coordinate z max of the crop
	 * @param channelNumber: channel to crop
	 *
	 * @return : ImageCoreIJ of the cropped image.
	 */
	public ImagePlus cropImage(int xMin, int yMin, int zMin, int width, int height, int depth, int channelNumber)
	throws IOException, FormatException {
		ImporterOptions options = new ImporterOptions();
		options.setId(this.imageFilePath);
		options.setAutoscale(true);
		options.setCrop(true);
		ImagePlus[] imps = BF.openImagePlus(options);
		ImagePlus   sort = new ImagePlus();
		imps = ChannelSplitter.split(imps[0]);
		sort.setStack(imps[channelNumber].getStack().crop(xMin, yMin, zMin, width, height, depth));
		return sort;
	}
	
	
	/**
	 * Crop of the bounding box on 2D image. The coordinates are inputs of this methods.
	 *
	 * @param xMin:          coordinate x min of the crop
	 * @param yMin:          coordinate y min of the crop
	 * @param width:         coordinate x max of the crop
	 * @param height:        coordinate y max of the crop
	 * @param channelNumber: channel to crop
	 *
	 * @return : ImageCoreIJ of the cropped image.
	 */
	public ImagePlus cropImage2D(int xMin, int yMin, int width, int height, int channelNumber)
	throws IOException, FormatException {
		ImporterOptions options = new ImporterOptions();
		options.setId(this.imageFilePath);
		options.setAutoscale(true);
		options.setCrop(true);
		ImagePlus[] imps = BF.openImagePlus(options);
		ImagePlus   sort = imps[channelNumber];
		sort.setRoi(xMin, yMin, width, height);
		sort.crop();
		return sort;
	}
	
	
	/**
	 * Getter of the number of nuclei contained in the input image
	 *
	 * @return int the nb of nuclei
	 */
	public int getNbOfNuc() {
		return this.boxes.size();
	}
	
	
	/** @return Header current image info analyse */
	public String getSpecificImageInfo() {
		return "#Image: " +
		       this.imageFilePath +
		       "\n#OTSU threshold: " +
		       this.otsuThreshold +
		       "\n#Slice used for OTSU threshold: " +
		       this.sliceUsedForOTSU +
		       "\n";
	}
	
	
	/**
	 * Getter column name for the tab delimited file
	 *
	 * @return columns name for output text file
	 */
	public String getColumnNames() {
		return HEADERS;
	}
	
	
	/**
	 * Write analysis info in output text file
	 */
	public void writeAnalyseInfo() {
		LOGGER.info("Writing analysis to file.");
		Directory dirOutput = new Directory(this.outputDirPath + "coordinates");
		dirOutput.checkAndCreateDir();
		OutputTextFile resultFileOutput = new OutputTextFile(this.outputDirPath +
		                                                     "coordinates" +
		                                                     File.separator +
		                                                     this.outputFilesPrefix +
		                                                     ".txt");
		resultFileOutput.saveTextFile(this.infoImageAnalysis, true);
	}
	
	
	/** Write analyse info in output text file */
	public void writeAnalyseInfoOMERO(Long id, Client client) {
		try {
			String path = new File(".").getCanonicalPath() + File.separator + this.outputFilesPrefix + ".txt";
			
			File           file             = new File(path);
			OutputTextFile resultFileOutput = new OutputTextFile(path);
			DatasetWrapper dataset          = client.getDataset(id);
			
			resultFileOutput.saveTextFile(this.infoImageAnalysis, false);
			dataset.addFile(client, file);
			Files.deleteIfExists(file.toPath());
		} catch (Exception e) {
			LOGGER.error("Error writing analysis information to OMERO.", e);
		}
	}
	
	
	/**
	 * Getter number of crop
	 *
	 * @return number of object detected
	 */
	public String getImageCropInfo() {
		LOGGER.info("Getting image crop info.");
		return this.imageFilePath + "\t" +
		       getNbOfNuc() + "\t" +
		       this.otsuThreshold + "\t" +
		       this.defaultThreshold + "\n";
	}
	
	
	public String getImageCropInfoOmero(String imageName) {
		return imageName + "\t" +
		       getNbOfNuc() + "\t" +
		       this.otsuThreshold + "\t" +
		       this.defaultThreshold + "\n";
	}
	
	
	/**
	 * Compute volume voxel of current image analysed
	 *
	 * @return voxel volume
	 */
	public double getVoxelVolume() {
		double calibration;
		if (this.autocropParameters.manualParameter) {
			calibration = autocropParameters.getVoxelVolume();
		} else {
			Calibration cal = this.rawImg.getCalibration();
			calibration = cal.pixelDepth * cal.pixelWidth * cal.pixelHeight;
		}
		return calibration;
	}
	
	
	/** Compute boxes merging if intersecting */
	public void boxIntersection() {
		if (this.autocropParameters.getBoxesRegrouping()) {
			LOGGER.info("Computing boxes intersections.");
			RectangleIntersection recompute = new RectangleIntersection(this.boxes, this.autocropParameters);
			recompute.runRectangleRecompilation();
			this.boxes = recompute.getNewBoxes();
		}
	}
	
	
	/**
	 * Set a list of boxes
	 *
	 * @param boxes list of boxes
	 */
	public void setBoxes(Map<Double, Box> boxes) {
		this.boxes = boxes;
	}
	
}
