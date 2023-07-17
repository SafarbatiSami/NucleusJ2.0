package gred.nucleus.core;

import gred.nucleus.utils.Histogram;
import ij.ImagePlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;


/**
 * Several method to realise and create the outfile for the chromocenter Analysis
 *
 * @author Tristan Dubos and Axel Poulet
 */
public final class ChromocenterAnalysis {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	private ChromocenterAnalysis() {
	}
	
	
	/**
	 * Compute the several parameters to characterize the chromocenter of one image, and return the results on the IJ
	 * log windows
	 *
	 * @param imagePlusSegmented    image of the segmented nucleus
	 * @param imagePlusChromocenter image of the segmented chromocenter
	 */
	public static void computeParametersChromocenter(ImagePlus imagePlusSegmented, ImagePlus imagePlusChromocenter) {
		Histogram histogram = new Histogram();
		histogram.run(imagePlusChromocenter);
		Measure3D measure3D = new Measure3D();
		double[]  tVolume   = measure3D.computeVolumeOfAllObjects(imagePlusChromocenter);
		LOGGER.info("CHROMOCENTER PARAMETERS");
		LOGGER.info("Titre Volume BorderToBorderDistance BarycenterToBorderDistance BarycenterToBorderDistanceNucleus ");
		if (histogram.getNbLabels() > 0) {
			double[] tBorderToBorderDistanceTable =
					RadialDistance.computeBorderToBorderDistances(imagePlusSegmented, imagePlusChromocenter);
			double[] tBarycenterToBorderDistanceTable =
					RadialDistance.computeBarycenterToBorderDistances(imagePlusSegmented, imagePlusChromocenter);
			double[] tBarycenterToBorderDistanceTableNucleus =
					RadialDistance.computeBarycenterToBorderDistances(imagePlusSegmented, imagePlusSegmented);
			for (int i = 0; i < tBorderToBorderDistanceTable.length; ++i) {
				LOGGER.info("{}_{} {} {} {} {}",
				            imagePlusChromocenter.getTitle(),
				            i,
				            tVolume[i],
				            tBorderToBorderDistanceTable[i],
				            tBarycenterToBorderDistanceTable[i],
				            tBarycenterToBorderDistanceTableNucleus[0]);
			}
		}
	}
	
	
	/**
	 * Compute the several parameters to characterize the chromocenter of several images, and create one output file for
	 * the results
	 *
	 * @param pathResultsFile       path for the output file
	 * @param imagePlusSegmented    image of the segmented nucleus
	 * @param imagePlusChromocenter image of the chromocenter segmented
	 *
	 * @throws IOException if file doesn't exist catch the exception
	 */
	public static void computeParametersChromocenter(String pathResultsFile,
	                                                 ImagePlus imagePlusSegmented,
	                                                 ImagePlus imagePlusChromocenter) throws IOException {
		Histogram histogram = new Histogram();
		histogram.run(imagePlusChromocenter);
		if (histogram.getNbLabels() > 0) {
			File    fileResults = new File(pathResultsFile);
			boolean exist       = fileResults.exists();
			try (BufferedWriter bufferedWriterOutput = new BufferedWriter(new FileWriter(fileResults, true))) {
				Measure3D measure3D = new Measure3D(imagePlusChromocenter.getCalibration().pixelWidth,
				                                    imagePlusChromocenter.getCalibration().pixelHeight,
				                                    imagePlusChromocenter.getCalibration().pixelDepth);
				double[] tVolume =
						measure3D.computeVolumeOfAllObjects(imagePlusChromocenter);
				double[] tBorderToBorderDistanceTable =
						RadialDistance.computeBorderToBorderDistances(imagePlusSegmented, imagePlusChromocenter);
				double[] tBarycenterToBorderDistanceTableCc =
						RadialDistance.computeBarycenterToBorderDistances(imagePlusSegmented, imagePlusChromocenter);
				double[] tBarycenterToBorderDistanceTableNucleus =
						RadialDistance.computeBarycenterToBorderDistances(imagePlusSegmented, imagePlusSegmented);
				if (!exist) {
					bufferedWriterOutput.write(
							"Titre\tVolume\tBorderToBorderDistance\tBarycenterToBorderDistance\tBarycenterToBorderDistanceNucleus\n");
				}
				for (
						int i = 0;
						i < tBorderToBorderDistanceTable.length; ++i) {
					bufferedWriterOutput.write(
							imagePlusChromocenter.getTitle() + "_" + i + "\t"
							+ tVolume[i] + "\t"
							+ tBorderToBorderDistanceTable[i] + "\t"
							+ tBarycenterToBorderDistanceTableCc[i] + "\t"
							+ tBarycenterToBorderDistanceTableNucleus[0] + "\n"
					                          );
				}
				bufferedWriterOutput.flush();
			}
		}
	}
	
}