package gred.nucleus.gradient;

import ij.ImagePlus;
import imagescience.image.Aspects;
import imagescience.image.FloatImage;
import imagescience.image.Image;
import imagescience.utility.Progressor;


/**
 * Modification of plugin featureJ to integrate of this work,
 * <p>
 * => Use to imagescience.jar library
 *
 * @author poulet axel
 */
public class MyGradient {
	
	private static final boolean COMPUTE  = true;
	private static final boolean SUPPRESS = false;
	private static final String  SCALE    = "1.0";
	private static final String  LOWER    = "";
	private static final String  HIGHER   = "";
	private final        boolean mask;
	ImagePlus imagePlus;
	ImagePlus imagePlusBinary;
	
	
	public MyGradient(ImagePlus imp, ImagePlus imagePlusBinary) {
		imagePlus = imp;
		this.imagePlusBinary = imagePlusBinary;
		mask = true;
	}
	
	
	public MyGradient(ImagePlus imp) {
		imagePlus = imp;
		mask = false;
	}
	
	
	@SuppressWarnings("unused")
	public ImagePlus run() {
		ImagePlus newImagePlus = new ImagePlus();
		try {
			double  scaleVal;
			double  lowVal        = 0;
			double  highVal       = 0;
			boolean lowThreshold  = true;
			boolean highThreshold = true;
			try {
				scaleVal = Double.parseDouble(SCALE);
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid smoothing scale value");
			}
			try {
				if (LOWER.equals("")) {
					lowThreshold = false;
				} else {
					lowVal = Double.parseDouble(LOWER);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid lower threshold value");
			}
			try {
				if (HIGHER.equals("")) {
					highThreshold = false;
				} else {
					highVal = Double.parseDouble(HIGHER);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid higher threshold value");
			}
			final int   threshMode = (lowThreshold ? 10 : 0) + (highThreshold ? 1 : 0);
			final Image image      = Image.wrap(imagePlus);
			Image       newImage   = new FloatImage(image);
			double[]    pls        = {0, 1};
			int         pl         = 0;
			if ((COMPUTE || SUPPRESS) && threshMode > 0) {
				pls = new double[]{0, 0.9, 1};
			}
			final Progressor progressor = new Progressor();
			progressor.display(FJ_Options.pgs);
			if (COMPUTE || SUPPRESS) {
				final Aspects aspects = newImage.aspects();
				if (!FJ_Options.isotropic) newImage.aspects(new Aspects());
				final MyEdges myEdges = new MyEdges();
				if (mask) myEdges.setMask(imagePlusBinary);
				++pl;
				progressor.range(pls[pl], pls[pl]);
				myEdges.progressor.parent(progressor);
				myEdges.messenger.log(FJ_Options.log);
				myEdges.messenger.status(FJ_Options.pgs);
				newImage = myEdges.run(newImage, scaleVal, SUPPRESS);
				newImage.aspects(aspects);
			}
			newImagePlus = newImage.imageplus();
			imagePlus.setCalibration(newImagePlus.getCalibration());
			final double[] minMax = newImage.extrema();
			final double   min    = minMax[0];
			final double   max    = minMax[1];
			newImagePlus.setDisplayRange(min, max);
		} catch (OutOfMemoryError e) {
			FJ.error("Not enough memory for this operation");
		} catch (IllegalArgumentException | IllegalStateException e) {
			FJ.error(e.getMessage());
		}
		//catch (Exception e) {	FJ.error("An unidentified error occurred while running the plugin");	}
		return newImagePlus;
	}
	
}
