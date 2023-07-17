package gred.nucleus.gradient;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagescience.feature.Laplacian;
import imagescience.image.Aspects;
import imagescience.image.FloatImage;
import imagescience.image.Image;
import imagescience.segment.ZeroCrosser;
import imagescience.utility.Progressor;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Panel;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


public class FJ_Laplacian implements PlugIn, WindowListener {
	
	private static boolean compute   = true;
	private static boolean zeroCross = false;
	
	private static String scale = "1.0";
	
	private static final Point pos = new Point(-1, -1);
	
	
	public void run(String arg) {
		
		if (!FJ.libCheck()) return;
		final ImagePlus imp = FJ.imageplus();
		if (imp == null) return;
		
		FJ.log(FJ.name() + " " + FJ.version() + ": Laplacian");
		
		GenericDialog gd = new GenericDialog(FJ.name() + ": Laplacian");
		gd.addCheckbox(" Compute Laplacian image    ", compute);
		gd.addStringField("                Smoothing scale:", scale);
		gd.addPanel(new Panel(), GridBagConstraints.EAST, new Insets(1, 0, 0, 0));
		gd.addCheckbox(" Detect zero-crossings    ", zeroCross);
		gd.addPanel(new Panel(), GridBagConstraints.EAST, new Insets(0, 0, 0, 0));
		
		if (pos.x >= 0 && pos.y >= 0) {
			gd.centerDialog(false);
			gd.setLocation(pos);
		} else {
			gd.centerDialog(true);
		}
		gd.addWindowListener(this);
		gd.showDialog();
		
		if (gd.wasCanceled()) return;
		
		compute = gd.getNextBoolean();
		scale = gd.getNextString();
		zeroCross = gd.getNextBoolean();
		
		(new FJLaplacian()).run(imp, compute, scale, zeroCross);
	}
	
	
	public void windowActivated(final WindowEvent e) {
	}
	
	
	public void windowClosed(final WindowEvent e) {
		
		pos.x = e.getWindow().getX();
		pos.y = e.getWindow().getY();
	}
	
	
	public void windowClosing(final WindowEvent e) {
	}
	
	
	public void windowDeactivated(final WindowEvent e) {
	}
	
	
	public void windowDeiconified(final WindowEvent e) {
	}
	
	
	public void windowIconified(final WindowEvent e) {
	}
	
	
	public void windowOpened(final WindowEvent e) {
	}
	
}

class FJLaplacian {
	
	void run(
			final ImagePlus imp,
			final boolean compute,
			final String scale,
			final boolean zeroCross
	        ) {
		
		try {
			double scaleVal;
			try {
				scaleVal = Double.parseDouble(scale);
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid smoothing scale value");
			}
			
			final Image img    = Image.wrap(imp);
			Image       newImg = new FloatImage(img);
			
			double[] pls = {0, 1};
			int      pl  = 0;
			if (compute && zeroCross) {
				pls = new double[]{0, 0.95, 1};
			}
			final Progressor progressor = new Progressor();
			progressor.display(FJ_Options.pgs);
			
			if (compute) {
				final Aspects aspects = newImg.aspects();
				if (!FJ_Options.isotropic) newImg.aspects(new Aspects());
				final Laplacian laplace = new Laplacian();
				++pl;
				progressor.range(pls[pl], pls[pl]);
				laplace.progressor.parent(progressor);
				laplace.messenger.log(FJ_Options.log);
				laplace.messenger.status(FJ_Options.pgs);
				newImg = laplace.run(newImg, scaleVal);
				newImg.aspects(aspects);
			}
			
			if (zeroCross) {
				final ZeroCrosser zc = new ZeroCrosser();
				++pl;
				progressor.range(pls[pl], pls[pl]);
				zc.progressor.parent(progressor);
				zc.messenger.log(FJ_Options.log);
				zc.messenger.status(FJ_Options.pgs);
				zc.run(newImg);
			}
			
			FJ.show(newImg, imp);
			FJ.close(imp);
			
		} catch (OutOfMemoryError e) {
			FJ.error("Not enough memory for this operation");
			
		} catch (IllegalArgumentException | IllegalStateException e) {
			FJ.error(e.getMessage());
			
		} catch (Exception e) {
			FJ.error("An unidentified error occurred while running the plugin");
			
		}
	}
	
}
