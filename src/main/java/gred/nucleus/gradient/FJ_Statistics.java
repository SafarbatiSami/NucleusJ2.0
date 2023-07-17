package gred.nucleus.gradient;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.text.TextPanel;
import imagescience.feature.Statistics;
import imagescience.image.Coordinates;
import imagescience.image.Dimensions;
import imagescience.image.Image;
import imagescience.utility.Formatter;
import imagescience.utility.Messenger;
import imagescience.utility.Progressor;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;


public class FJ_Statistics implements PlugIn, ItemListener, WindowListener {
	
	private static final String[] labels = {
			" Minimum",
			" Maximum",
			" Mean",
			" Median",
			" Elements",
			" Mass",
			" Variance",
			" Mode",
			" S-Deviation",
			" A-Deviation",
			" L1-Norm",
			" L2-Norm",
			" Skewness",
			" Kurtosis"
	};
	
	private static final boolean[] values   = {
			true,
			true,
			true,
			false,
			true,
			false,
			false,
			false,
			true,
			false,
			false,
			false,
			false,
			false
	};
	private static final Point     pos      = new Point(-1, -1);
	private static       boolean   clear    = false;
	private static       boolean   name     = true;
	private static       boolean   channel  = false;
	private static       boolean   time     = false;
	private static       boolean   slice    = false;
	private static       int       decimals = 3;
	private              Checkbox  channelBox;
	private              Checkbox  timeBox;
	private              Checkbox  sliceBox;
	
	
	@SuppressWarnings("rawtypes")
	public void run(String arg) {
		
		if (!FJ.libCheck()) return;
		final ImagePlus imp = FJ.imageplus();
		if (imp == null) return;
		
		FJ.log(FJ.name() + " " + FJ.version() + ": Statistics");
		
		GenericDialog gd = new GenericDialog(FJ.name() + ": Statistics");
		gd.addCheckboxGroup(7, 2, labels, values);
		gd.addPanel(new Panel(), GridBagConstraints.EAST, new Insets(5, 0, 0, 0));
		gd.addCheckbox(" Clear previous results", clear);
		gd.addCheckbox(" Image name displaying", name);
		gd.addCheckbox(" Channel numbering", channel);
		gd.addCheckbox(" Time frame numbering", time);
		gd.addCheckbox(" Slice numbering", slice);
		final Vector checkboxes = gd.getCheckboxes();
		final int    vecLen     = checkboxes.size();
		sliceBox = (Checkbox) checkboxes.get(vecLen - 1);
		sliceBox.addItemListener(this);
		timeBox = (Checkbox) checkboxes.get(vecLen - 2);
		timeBox.addItemListener(this);
		channelBox = (Checkbox) checkboxes.get(vecLen - 3);
		channelBox.addItemListener(this);
		gd.addPanel(new Panel(), GridBagConstraints.EAST, new Insets(0, 0, 0, 0));
		final String[] decsList = new String[11];
		for (int i = 0; i < 11; ++i) decsList[i] = String.valueOf(i);
		gd.addChoice("        Decimal places:", decsList, String.valueOf(decimals));
		
		if (pos.x >= 0 && pos.y >= 0) {
			gd.centerDialog(false);
			gd.setLocation(pos);
		} else {
			gd.centerDialog(true);
		}
		gd.addWindowListener(this);
		gd.showDialog();
		
		if (gd.wasCanceled()) return;
		
		for (int i = 0; i < values.length; ++i) values[i] = gd.getNextBoolean();
		clear = gd.getNextBoolean();
		name = gd.getNextBoolean();
		channel = gd.getNextBoolean();
		time = gd.getNextBoolean();
		slice = gd.getNextBoolean();
		decimals = gd.getNextChoiceIndex();
		
		(new FJStatistics()).run(imp, values, clear, name, channel, time, slice, decimals);
	}
	
	
	public void itemStateChanged(final ItemEvent e) {
		if (e.getSource() == sliceBox) {
			if (sliceBox.getState()) {
				timeBox.setState(true);
				channelBox.setState(true);
			}
		} else if (e.getSource() == timeBox) {
			if (timeBox.getState()) {
				channelBox.setState(true);
			} else {
				sliceBox.setState(false);
			}
		} else if (e.getSource() == channelBox) {
			if (!channelBox.getState()) {
				timeBox.setState(false);
				sliceBox.setState(false);
			}
		}
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

class FJStatistics {
	private static int        number = 0;
	private final  Statistics stats  = new Statistics();
	
	private final Formatter fmt = new Formatter();
	
	private boolean[] values  = null;
	@SuppressWarnings("unused")
	private boolean   clear   = false;
	private boolean   name    = true;
	private boolean   channel = false;
	private boolean   time    = false;
	
	private boolean slice    = false;
	@SuppressWarnings("unused")
	private int     decimals = 3;
	
	
	void run(
			final ImagePlus imp,
			final boolean[] values,
			final boolean clear,
			final boolean name,
			final boolean channel,
			final boolean time,
			final boolean slice,
			final int decimals
	        ) {
		
		this.values = new boolean[values.length];
		System.arraycopy(values, 0, this.values, 0, values.length);
		this.clear = clear;
		this.name = name;
		this.channel = channel;
		this.time = time;
		this.slice = slice;
		this.decimals = decimals;
		
		try {
			// Initialize:
			if (decimals < 0 || decimals > 10) {
				throw new IllegalArgumentException("Invalid number of decimals");
			} else {
				fmt.decs(decimals);
				fmt.chop(0);
			}
			
			// Determine region of interest:
			final Image       img  = Image.wrap(imp);
			final Dimensions  dims = img.dimensions();
			final Coordinates cMin = new Coordinates();
			final Coordinates cMax = new Coordinates();
			Roi               roi  = imp.getRoi();
			if (roi == null) roi = new Roi(0, 0, dims.x, dims.y);
			ImageProcessor maskImageProcessor = null;
			switch (roi.getType()) {
				case Roi.COMPOSITE:
				case Roi.FREEROI:
				case Roi.OVAL:
				case Roi.POINT:
				case Roi.POLYGON:
				case Roi.TRACED_ROI:
					maskImageProcessor = roi.getMask();
					break;
				case Roi.RECTANGLE:
					maskImageProcessor = new ByteProcessor(1, 1);
					maskImageProcessor.set(0, 0, 255);
					break;
			}
			if (maskImageProcessor == null) throw new IllegalArgumentException("Region of interest not supported");
			final ImagePlus maskImagePlus = new ImagePlus("Mask", maskImageProcessor); // maskImagePlus.show();
			final Image     mask          = Image.wrap(maskImagePlus);
			final Rectangle bounds        = roi.getBounds();
			cMin.x = bounds.x;
			cMin.y = bounds.y;
			cMax.x = bounds.x + bounds.width - 1;
			cMax.y = bounds.y + bounds.height - 1;
			
			// Compute and show statistics:
			final String    namePrelude = name ? ("\t" + imp.getTitle()) : "";
			final TextPanel textpanel   = IJ.getTextPanel();
			final String    headings    = headings();
			if (clear || !headings.equals(textpanel.getColumnHeadings())) {
				textpanel.setColumnHeadings(headings);
				number = 0;
			}
			
			final Progressor pgs = new Progressor();
			final Messenger  msg = new Messenger();
			pgs.display(FJ_Options.pgs);
			msg.status(FJ_Options.pgs);
			
			if (slice) {
				msg.status("Computing statistics per slice / time / channel...");
				pgs.steps(dims.c * dims.t * dims.z);
				pgs.start();
				for (cMin.c = 0, cMax.c = 0; cMin.c < dims.c; ++cMin.c, ++cMax.c) {
					for (cMin.t = 0, cMax.t = 0; cMin.t < dims.t; ++cMin.t, ++cMax.t) {
						for (cMin.z = 0, cMax.z = 0; cMin.z < dims.z; ++cMin.z, ++cMax.z) {
							stats.run(img, cMin, cMax, mask);
							++number;
							final String prelude = number +
							                       namePrelude + "\t" +
							                       (cMin.c + 1) + "\t" +
							                       (cMin.t + 1) + "\t" +
							                       (cMin.z + 1);
							textpanel.append(prelude + results());
							pgs.step();
						}
					}
				}
				pgs.stop();
				msg.status("");
				
			} else if (time) {
				msg.status("Computing statistics per time / channel...");
				pgs.steps(dims.c * dims.t);
				pgs.start();
				cMax.z = dims.z - 1;
				for (cMin.c = 0, cMax.c = 0; cMin.c < dims.c; ++cMin.c, ++cMax.c) {
					for (cMin.t = 0, cMax.t = 0; cMin.t < dims.t; ++cMin.t, ++cMax.t) {
						stats.run(img, cMin, cMax, mask);
						++number;
						final String prelude = number + namePrelude + "\t" + (cMin.c + 1) + "\t" + (cMin.t + 1);
						textpanel.append(prelude + results());
						pgs.step();
					}
				}
				pgs.stop();
				msg.status("");
				
			} else if (channel) {
				msg.status("Computing statistics per channel...");
				pgs.steps(dims.c);
				pgs.start();
				cMax.z = dims.z - 1;
				cMax.t = dims.t - 1;
				for (cMin.c = 0, cMax.c = 0; cMin.c < dims.c; ++cMin.c, ++cMax.c) {
					stats.run(img, cMin, cMax, mask);
					++number;
					final String prelude = number + namePrelude + "\t" + (cMin.c + 1);
					textpanel.append(prelude + results());
					pgs.step();
				}
				pgs.stop();
				msg.status("");
			} else {
				cMax.z = dims.z - 1;
				cMax.t = dims.t - 1;
				cMax.c = dims.c - 1;
				stats.messenger.status(FJ_Options.pgs);
				stats.progressor.display(FJ_Options.pgs);
				stats.run(img, cMin, cMax, mask);
				++number;
				final String prelude = number + namePrelude;
				textpanel.append(prelude + results());
			}
		} catch (OutOfMemoryError e) {
			FJ.error("Not enough memory for this operation");
		} catch (IllegalArgumentException e) {
			FJ.error(e.getMessage());
		} catch (Exception e) {
			FJ.error("An unidentified error occurred while running the plugin");
		}
	}
	
	
	private String headings() {
		
		final StringBuilder cols = new StringBuilder();
		
		cols.append("Nr");
		if (name) cols.append("\tImage");
		if (slice) {
			cols.append("\tChan\tTime\tSlice");
		} else if (time) {
			cols.append("\tChan\tTime");
		} else if (channel) cols.append("\tChan");
		if (values[4]) cols.append("\tElements");
		if (values[0]) cols.append("\tMin");
		if (values[1]) cols.append("\tMax");
		if (values[2]) cols.append("\tMean");
		if (values[6]) cols.append("\tVar");
		if (values[8]) cols.append("\tS-Dev");
		if (values[9]) cols.append("\tA-Dev");
		if (values[3]) cols.append("\tMedian");
		if (values[7]) cols.append("\tMode");
		if (values[5]) cols.append("\tMass");
		if (values[10]) cols.append("\tL1");
		if (values[11]) cols.append("\tL2");
		if (values[12]) cols.append("\tSkew");
		if (values[13]) cols.append("\tKurt");
		
		return cols.toString();
	}
	
	
	private String results() {
		
		final StringBuilder res = new StringBuilder();
		
		if (values[4]) res.append("\t").append(fmt.d2s(stats.get(Statistics.ELEMENTS)));
		if (values[0]) res.append("\t").append(fmt.d2s(stats.get(Statistics.MINIMUM)));
		if (values[1]) res.append("\t").append(fmt.d2s(stats.get(Statistics.MAXIMUM)));
		if (values[2]) res.append("\t").append(fmt.d2s(stats.get(Statistics.MEAN)));
		if (values[6]) res.append("\t").append(fmt.d2s(stats.get(Statistics.VARIANCE)));
		if (values[8]) res.append("\t").append(fmt.d2s(stats.get(Statistics.SDEVIATION)));
		if (values[9]) res.append("\t").append(fmt.d2s(stats.get(Statistics.ADEVIATION)));
		if (values[3]) res.append("\t").append(fmt.d2s(stats.get(Statistics.MEDIAN)));
		if (values[7]) res.append("\t").append(fmt.d2s(stats.get(Statistics.MODE)));
		if (values[5]) res.append("\t").append(fmt.d2s(stats.get(Statistics.MASS)));
		if (values[10]) res.append("\t").append(fmt.d2s(stats.get(Statistics.L1NORM)));
		if (values[11]) res.append("\t").append(fmt.d2s(stats.get(Statistics.L2NORM)));
		if (values[12]) res.append("\t").append(fmt.d2s(stats.get(Statistics.SKEWNESS)));
		if (values[13]) res.append("\t").append(fmt.d2s(stats.get(Statistics.KURTOSIS)));
		
		return res.toString();
	}
	
}
