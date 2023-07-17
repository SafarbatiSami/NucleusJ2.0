package gred.nucleus.core;

import gred.nucleus.segmentation.SegmentationParameters;
import gred.nucleus.utils.ConvexHullImageMaker;
import ij.ImagePlus;
import ij.ImageStack;
import inra.ijpb.morphology.Morphology;
import inra.ijpb.morphology.Strel3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;


/**
 * Segmentation using a convex hull algorithm analysis on 3D segmented image imputed for the different axis combined : - XY - XZ -
 *
 *
 * @author Tristan Dubos and Axel Poulet
 */
public class ConvexHullSegmentation {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	/**
	 * Run the convex hull detection analysis on 3D segmented image imputed for the different axis combined : XY XZ YZ
	 *
	 * @param imagePlusInput Current image segmented analysed
	 *
	 * @return segmented image
	 */
	public ImagePlus convexHullDetection(ImagePlus imagePlusInput, SegmentationParameters segmentationParameters) {
		LOGGER.info("Running Convex Hull Algorithm.");
		ConvexHullImageMaker nuc = new ConvexHullImageMaker();
		nuc.setAxes("xy");
		ImagePlus imagePlusXY = nuc.runConvexHullDetection(imagePlusInput);
		LOGGER.trace("XY done");
		nuc.setAxes("xz");
		ImagePlus imagePlusXZ = nuc.runConvexHullDetection(imagePlusInput);
		LOGGER.trace("XZ done");
		nuc.setAxes("yz");
		ImagePlus imagePlusYZ = nuc.runConvexHullDetection(imagePlusInput);
		LOGGER.trace("YZ done");

		ImagePlus result = imageMakingUnion(imagePlusInput, imagePlusXY, imagePlusXZ, imagePlusYZ);
		Strel3D strel3D = Strel3D.Shape.CUBE.fromDiameter(5);
		result.setStack(Morphology.opening(result.getStack(), strel3D));

		return result;
	}
	
	
	/**
	 * Make an union of segmented images from the different plans
	 *
	 * @param imagePlusXY Segmented image in XY dimension
	 * @param imagePlusXZ Segmented image in XZ dimension
	 * @param imagePlusYZ Segmented image in YZ dimension
	 *
	 * @return ImagePlus image results of the convex hull algorithm
	 *
	 * @see ConvexHullSegmentation#convexHullDetection(ImagePlus, SegmentationParameters) 
	 */
	private ImagePlus imageMakingUnion(ImagePlus imagePlusInput,
	                                   ImagePlus imagePlusXY,
	                                   ImagePlus imagePlusXZ,
	                                   ImagePlus imagePlusYZ) {
		ImagePlus  imagePlusOutput  = imagePlusInput.duplicate();
		imagePlusOutput.setTitle(imagePlusInput.getTitle());
		
		ImageStack imageStackXY     = imagePlusXY.getStack();
		ImageStack imageStackXZ     = imagePlusXZ.getStack();
		ImageStack imageStackYZ     = imagePlusYZ.getStack();
		ImageStack imageStackOutput = imagePlusOutput.getStack();
		
		for (int d = 0; d < imagePlusXY.getNSlices(); ++d) {
			for (int w = 0; w < imagePlusXY.getWidth(); ++w) {
				for (int h = 0; h < imagePlusXY.getHeight(); ++h) {
					if (imageStackXY.getVoxel(w, h, d) != 0 || (imageStackYZ.getVoxel(h, d, w) != 0 || imageStackXZ.getVoxel(w, d, h) != 0)) {
						if (imageStackOutput.getVoxel(w, h, d) == 0) {
							imageStackOutput.setVoxel(w, h, d, 255);
						}
					}
				}
			}
		}
		return imagePlusOutput;
	}

}
