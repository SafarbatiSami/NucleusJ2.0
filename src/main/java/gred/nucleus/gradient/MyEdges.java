package gred.nucleus.gradient;

import ij.ImagePlus;
import ij.ImageStack;
import imagescience.feature.Differentiator;
import imagescience.image.*;
import imagescience.utility.*;

/*
  Modification in this class to adapt this class for this image processing poulet axel
 */

/** Detects edges in images. */
public class MyEdges {
	/** The object used for message displaying. */
	public final Messenger      messenger      = new Messenger();
	/** The object used for progress displaying. */
	public final Progressor     progressor     = new Progressor();
	/** The object used for image differentiation. */
	public final Differentiator differentiator = new Differentiator();
	private      double[][][]   tabMask        = null;
	
	
	/** Default constructor. */
	public MyEdges() {
	}
	
	
	/**
	 * Detects edges in images.
	 *
	 * @param image     the input image in which edges are to be detected. If it is of type {@link FloatImage}, it will
	 *                  be used to store intermediate results. Otherwise it will be left unaltered. If the size of the
	 *                  image in the z-dimension equals {@code 1}, this method will compute, for every image element,
	 *                  the magnitude of the two-dimensional (2D) gradient vector. Otherwise it will compute for every
	 *                  image element the magnitude of the full three-dimensional (3D) gradient vector. These
	 *                  computations are performed on every x-y(-z) sub-image in a 5D image.
	 * @param scale     the smoothing scale at which the required image derivatives are computed. The scale is equal to
	 *                  the standard deviation of the Gaussian kernel used for differentiation and must be larger than
	 *                  {@code 0}. In order to enforce physical isotropy, for each dimension, the scale is divided by
	 *                  the size of the image elements (aspect-ratio value) in that dimension.
	 * @param nonmaxsup determines whether locally non-maximal gray-values are suppressed. To determine whether the
	 *                  gray-value of an image element is a local maximum, this method applies linear interpolation in
	 *                  the direction of the gradient vector to compute gray-values at approximately one sample distance
	 *                  on each side of the given element, which are subsequently compared to the gray-value of the
	 *                  given element.
	 *
	 * @return an image showing the locations of edges according to the algorithm. The returned image is always of type
	 * {@link FloatImage}.
	 *
	 * @throws IllegalArgumentException if {@code scale} is less than or equal to {@code 0}.
	 * @throws IllegalStateException    if the size of the image elements (aspect-ratio value) is less than or equal to
	 *                                  {@code 0} in the x-, y-, or z-dimension.
	 * @throws NullPointerException     if {@code image} is {@code null}.
	 */
	public Image run(final Image image, final double scale, final boolean nonmaxsup) {
		
		messenger.log(ImageScience.prelude() + "Edges");
		
		final Timer timer = new Timer();
		timer.messenger.log(messenger.log());
		timer.start();
		
		// Initialize:
		messenger.log("Checking arguments");
		if (scale <= 0) throw new IllegalArgumentException("Smoothing scale less than or equal to 0");
		
		final Dimensions dims = image.dimensions();
		messenger.log("Input image dimensions: (x,y,z,t,c) = (" +
		              dims.x + "," +
		              dims.y + "," +
		              dims.z + "," +
		              dims.t + "," +
		              dims.c + ")");
		
		final Aspects asps = image.aspects();
		messenger.log("Element aspect-ratios: (" +
		              asps.x + "," +
		              asps.y + "," +
		              asps.z + "," +
		              asps.t + "," +
		              asps.c + ")");
		if (asps.x <= 0) throw new IllegalStateException("Aspect-ratio value in x-dimension less than or equal to 0");
		if (asps.y <= 0) throw new IllegalStateException("Aspect-ratio value in y-dimension less than or equal to 0");
		if (asps.z <= 0) throw new IllegalStateException("Aspect-ratio value in z-dimension less than or equal to 0");
		
		final String name = image.name();
		
		Image edgeImage = (image instanceof FloatImage) ? image : new FloatImage(image);
		
		differentiator.messenger.log(messenger.log());
		differentiator.progressor.parent(progressor);
		
		// Detect edges:
		// 3D case
		
		double[] pls = {0, 0.35, 0.7, 0.98, 1};
		int      pl  = 0;
		if (nonmaxsup) pls = new double[]{0, 0.32, 0.64, 0.9, 0.92, 1};
		
		// Compute gradient vector:
		logStatus("Computing Ix");
		++pl;
		progressor.range(pls[pl], pls[pl]);
		final Image Ix = differentiator.run(edgeImage.duplicate(), scale, 1, 0, 0);
		logStatus("Computing Iy");
		++pl;
		progressor.range(pls[pl], pls[pl]);
		final Image Iy = differentiator.run(edgeImage.duplicate(), scale, 0, 1, 0);
		logStatus("Computing Iz");
		++pl;
		progressor.range(pls[pl], pls[pl]);
		final Image Iz = differentiator.run(edgeImage, scale, 0, 0, 1);
		
		// Compute gradient magnitude (Ix is reused to save memory in case
		//non-maxima suppression is not applied):
		logStatus("Computing gradient magnitude");
		progressor.steps(dims.c * dims.t * dims.z * dims.y);
		++pl;
		progressor.range(pls[pl], pls[pl]);
		edgeImage = nonmaxsup ? new FloatImage(dims) : Ix;
		Ix.axes(Axes.X);
		Iy.axes(Axes.X);
		Iz.axes(Axes.X);
		edgeImage.axes(Axes.X);
		final double[]    aIx         = new double[dims.x];
		final double[]    aIy         = new double[dims.x];
		final double[]    aIz         = new double[dims.x];
		final Coordinates coordinates = new Coordinates();
		
		progressor.start();
		for (coordinates.c = 0; coordinates.c < dims.c; ++coordinates.c) {
			for (coordinates.t = 0; coordinates.t < dims.t; ++coordinates.t) {
				for (coordinates.z = 0; coordinates.z < dims.z; ++coordinates.z) {
					for (coordinates.y = 0; coordinates.y < dims.y; ++coordinates.y) {
						Ix.get(coordinates, aIx);
						Iy.get(coordinates, aIy);
						Iz.get(coordinates, aIz);
						for (int x = 0; x < dims.x; ++x) {
							if (tabMask != null) {
								if (tabMask[x][coordinates.y][coordinates.z] > 0) {
									aIx[x] = Math.sqrt(aIx[x] * aIx[x] + aIy[x] * aIy[x] + aIz[x] * aIz[x]);
									edgeImage.set(coordinates, aIx);
									progressor.step();
								} else {
									aIx[x] = 0;
									edgeImage.set(coordinates, aIx);
								}
							}
						}
					}
				}
			}
		}
		progressor.stop();
		
		// Apply non-maxima suppression if requested (using mirror-boundary conditions and linear interpolation):
		if (nonmaxsup) {
			logStatus("Suppressing non-maxima");
			progressor.steps(dims.c * dims.t * dims.z);
			++pl;
			progressor.range(pls[pl], pls[pl]);
			Ix.axes(Axes.X + Axes.Y);
			Iy.axes(Axes.X + Axes.Y);
			Iz.axes(Axes.X + Axes.Y);
			final Image supImage = Ix;
			edgeImage.axes(Axes.X + Axes.Y);
			final double[][][] gm   = new double[3][dims.y + 2][dims.x + 2];
			final double[][]   aaIx = new double[dims.y][dims.x];
			final double[][]   aaIy = new double[dims.y][dims.x];
			final double[][]   aaIz = new double[dims.y][dims.x];
			final Coordinates  cgm  = new Coordinates();
			cgm.x = -1;
			cgm.y = -1;
			coordinates.reset();
			final int  dimsZm1 = dims.z - 1;
			double[][] atmp;
			
			progressor.start();
			for (coordinates.c = 0, cgm.c = 0; coordinates.c < dims.c; ++coordinates.c, ++cgm.c) {
				for (coordinates.t = 0, cgm.t = 0; coordinates.t < dims.t; ++coordinates.t, ++cgm.t) {
					// First slice:
					
					coordinates.z = 0;
					Ix.get(coordinates, aaIx);
					Iy.get(coordinates, aaIy);
					Iz.get(coordinates, aaIz);
					cgm.z = 0;
					edgeImage.get(cgm, gm[1]);
					cgm.z = 1;
					edgeImage.get(cgm, gm[0]);
					edgeImage.get(cgm, gm[2]);
					suppress3D(gm, aaIx, aaIy, aaIz);
					supImage.set(coordinates, aaIx);
					progressor.step();
					
					// Intermediate slices:
					for (coordinates.z = 1, cgm.z = 2; coordinates.z < dimsZm1; ++coordinates.z, ++cgm.z) {
						Ix.get(coordinates, aaIx);
						Iy.get(coordinates, aaIy);
						Iz.get(coordinates, aaIz);
						atmp = gm[0];
						gm[0] = gm[1];
						gm[1] = gm[2];
						gm[2] = atmp;
						edgeImage.get(cgm, gm[2]);
						suppress3D(gm, aaIx, aaIy, aaIz);
						supImage.set(coordinates, aaIx);
						progressor.step();
					}
					// Last slice:
					Ix.get(coordinates, aaIx);
					Iy.get(coordinates, aaIy);
					Iz.get(coordinates, aaIz);
					atmp = gm[0];
					gm[0] = gm[1];
					gm[1] = gm[2];
					gm[2] = atmp;
					cgm.z = dims.z - 2;
					edgeImage.get(cgm, gm[2]);
					suppress3D(gm, aaIx, aaIy, aaIz);
					supImage.set(coordinates, aaIx);
					progressor.step();
				}
			}
			progressor.stop();
			edgeImage = supImage;
		}
		
		messenger.status("");
		
		timer.stop();
		
		edgeImage.name(name + " edges");
		
		return edgeImage;
	}
	
	
	private void suppress3D(final double[][][] gm,
	                        final double[][] aaIx,
	                        final double[][] aaIy,
	                        final double[][] aaIz) {
		
		// Initialize:
		final int dimsY   = aaIx.length;
		final int dimsYp1 = dimsY + 1;
		final int dimsYm1 = dimsY - 1;
		final int dimsX   = aaIx[0].length;
		final int dimsXp1 = dimsX + 1;
		final int dimsXm1 = dimsX - 1;
		double    rx, ry, rz, fx, fy, fz, gmVal, gmVal1, gmVal2;
		double    fdx, fdy, fdz, f1mdx, f1mdy, f1mdz;
		int       ix, iy, iz, ixp1, iyp1, izp1;
		
		// Mirror x-borders:
		if (dimsX == 1) {
			for (int z = 0; z < 3; ++z) {
				final double[][] slice = gm[z];
				for (int y = 1; y < dimsYp1; ++y) {
					slice[y][0] = slice[y][1];
					slice[y][dimsXp1] = slice[y][dimsX];
				}
			}
		} else {
			for (int z = 0; z < 3; ++z) {
				final double[][] slice = gm[z];
				for (int y = 1; y < dimsYp1; ++y) {
					slice[y][0] = slice[y][2];
					slice[y][dimsXp1] = slice[y][dimsXm1];
				}
			}
		}
		
		// Mirror y-borders:
		if (dimsY == 1) {
			for (int z = 0; z < 3; ++z) {
				final double[] y0 = gm[z][0];
				final double[] y1 = gm[z][1];
				final double[] y2 = gm[z][2];
				for (int x = 0; x <= dimsXp1; ++x) {
					y0[x] = y1[x];
					y2[x] = y1[x];
				}
			}
		} else {
			for (int z = 0; z < 3; ++z) {
				final double[] y0       = gm[z][0];
				final double[] y2       = gm[z][2];
				final double[] yDimsYm1 = gm[z][dimsYm1];
				final double[] yDimsYp1 = gm[z][dimsYp1];
				for (int x = 0; x <= dimsXp1; ++x) {
					y0[x] = y2[x];
					yDimsYp1[x] = yDimsYm1[x];
				}
			}
		}
		
		// Suppress non-maxima:
		final double[][] gm1 = gm[1];
		
		for (int y = 0, yp1 = 1; y < dimsY; ++y, ++yp1) {
			
			final double[] gm1yp1 = gm1[yp1];
			final double[] aIx    = aaIx[y];
			final double[] aIy    = aaIy[y];
			final double[] aIz    = aaIz[y];
			
			for (int x = 0, xp1 = 1; x < dimsX; ++x, ++xp1) {
				
				gmVal = gm1yp1[xp1];
				if (gmVal == 0) {
					aIx[x] = 0;
				} else {
					// Compute direction vector:
					rx = 0.7f * aIx[x] / gmVal;
					ry = 0.7f * aIy[x] / gmVal;
					rz = 0.7f * aIz[x] / gmVal;
					
					// Compute gradient magnitude in one direction:
					fx = xp1 + rx;
					fy = yp1 + ry;
					fz = 1 + rz;
					ix = FMath.floor(fx);
					iy = FMath.floor(fy);
					iz = FMath.floor(fz);
					ixp1 = ix + 1;
					iyp1 = iy + 1;
					izp1 = iz + 1;
					fdx = fx - ix;
					fdy = fy - iy;
					fdz = fz - iz;
					f1mdx = 1 - fdx;
					f1mdy = 1 - fdy;
					f1mdz = 1 - fdz;
					
					gmVal1 = (
							f1mdz * f1mdy * f1mdx * gm[iz][iy][ix] +
							f1mdz * f1mdy * fdx * gm[iz][iy][ixp1] +
							f1mdz * fdy * f1mdx * gm[iz][iyp1][ix] +
							f1mdz * fdy * fdx * gm[iz][iyp1][ixp1] +
							fdz * f1mdy * f1mdx * gm[izp1][iy][ix] +
							fdz * f1mdy * fdx * gm[izp1][iy][ixp1] +
							fdz * fdy * f1mdx * gm[izp1][iyp1][ix] +
							fdz * fdy * fdx * gm[izp1][iyp1][ixp1]
					);
					
					// Compute gradient magnitude in opposite direction:
					fx = xp1 - rx;
					fy = yp1 - ry;
					fz = 1 - rz;
					ix = FMath.floor(fx);
					iy = FMath.floor(fy);
					iz = FMath.floor(fz);
					ixp1 = ix + 1;
					iyp1 = iy + 1;
					izp1 = iz + 1;
					fdx = fx - ix;
					fdy = fy - iy;
					fdz = fz - iz;
					f1mdx = 1 - fdx;
					f1mdy = 1 - fdy;
					f1mdz = 1 - fdz;
					
					gmVal2 = (
							f1mdz * f1mdy * f1mdx * gm[iz][iy][ix] +
							f1mdz * f1mdy * fdx * gm[iz][iy][ixp1] +
							f1mdz * fdy * f1mdx * gm[iz][iyp1][ix] +
							f1mdz * fdy * fdx * gm[iz][iyp1][ixp1] +
							fdz * f1mdy * f1mdx * gm[izp1][iy][ix] +
							fdz * f1mdy * fdx * gm[izp1][iy][ixp1] +
							fdz * fdy * f1mdx * gm[izp1][iyp1][ix] +
							fdz * fdy * fdx * gm[izp1][iyp1][ixp1]
					);
					
					// Suppress current gradient magnitude if non-maximum:
					if (gmVal1 >= gmVal || gmVal2 >= gmVal) {
						aIx[x] = 0;
					} else {
						aIx[x] = gmVal;
					}
				}
			}
		}
	}
	
	
	/**
	 * Méthode permettant d'obtenir le masque d'un objet et de le stocker dans une matrice
	 *
	 * @param imagePlus => image binaire de l'image en cours de traitement
	 */
	public void setMask(ImagePlus imagePlus) {
		ImageStack labelStack = imagePlus.getStack();
		final int  size1      = labelStack.getWidth();
		final int  size2      = labelStack.getHeight();
		final int  size3      = labelStack.getSize();
		tabMask = new double[size1][size2][size3];
		int i, j, k;
		
		for (i = 0; i < size1; ++i) {
			for (j = 0; j < size2; ++j) {
				for (k = 0; k < size3; ++k) {
					tabMask[i][j][k] = labelStack.getVoxel(i, j, k);
				}
			}
		}
	}
	
	
	private void logStatus(final String s) {
		messenger.log(s);
		messenger.status(s + "...");
	}
	
}
