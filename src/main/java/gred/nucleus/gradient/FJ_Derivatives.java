package gred.nucleus.gradient;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagescience.feature.Differentiator;
import imagescience.image.Aspects;
import imagescience.image.FloatImage;
import imagescience.image.Image;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


public class FJ_Derivatives implements PlugIn, WindowListener {
	
	private static final Point pos = new Point(-1, -1);
	
	private static int xOrder = 0;
	private static int yOrder = 0;
	private static int zOrder = 0;
	
	private static String scale = "1.0";
	
	
	public void run(String arg) {
		
		if (!FJ.libCheck()) return;
		final ImagePlus imp = FJ.imageplus();
		if (imp == null) return;
		
		FJ.log(FJ.name() + " " + FJ.version() + ": Derivatives");
		
		GenericDialog  gd     = new GenericDialog(FJ.name() + ": Derivatives");
		final String[] orders = new String[11];
		for (int i = 0; i < 11; ++i) orders[i] = String.valueOf(i);
		gd.addChoice("x-order of differentiation:", orders, orders[xOrder]);
		gd.addChoice("y-order of differentiation:", orders, orders[yOrder]);
		gd.addChoice("z-order of differentiation:", orders, orders[zOrder]);
		gd.addPanel(new Panel(), GridBagConstraints.EAST, new Insets(0, 0, 0, 0));
		gd.addStringField("Smoothing scale:", scale);
		
		if (pos.x >= 0 && pos.y >= 0) {
			gd.centerDialog(false);
			gd.setLocation(pos);
		} else {
			gd.centerDialog(true);
		}
		gd.addWindowListener(this);
		gd.showDialog();
		
		if (gd.wasCanceled()) return;
		
		xOrder = gd.getNextChoiceIndex();
		yOrder = gd.getNextChoiceIndex();
		zOrder = gd.getNextChoiceIndex();
		scale = gd.getNextString();
		
		(new FJDerivatives()).run(imp, xOrder, yOrder, zOrder, scale);
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

class FJDerivatives {
	
	void run(
			final ImagePlus imp,
			final int xOrder,
			final int yOrder,
			final int zOrder,
			final String scale
	        ) {
		
		try {
			final Image   img     = Image.wrap(imp);
			final Aspects aspects = img.aspects();
			if (!FJ_Options.isotropic) img.aspects(new Aspects());
			double scaleVal;
			try {
				scaleVal = Double.parseDouble(scale);
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid smoothing scale value");
			}
			final Image          newImg = new FloatImage(img);
			final Differentiator diff   = new Differentiator();
			diff.messenger.log(FJ_Options.log);
			diff.messenger.status(FJ_Options.pgs);
			diff.progressor.display(FJ_Options.pgs);
			diff.run(newImg, scaleVal, xOrder, yOrder, zOrder);
			newImg.aspects(aspects);
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
