package gred.nucleus.plugins;

import fr.igred.omero.Client;
import fr.igred.omero.exception.AccessException;
import fr.igred.omero.exception.ServiceException;
import fr.igred.omero.repository.DatasetWrapper;
import fr.igred.omero.repository.ImageWrapper;
import fr.igred.omero.repository.ProjectWrapper;
import gred.nucleus.dialogs.*;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;
import ij.IJ;
import ij.plugin.PlugIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;


public class Segmentation_ implements PlugIn, IDialogListener {
	SegmentationDialog segmentationDialog;
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	/**
	 * Run method for imageJ plugin for the segmentation
	 *
	 * @param arg use by imageJ
	 */
	@Override
	public void run(String arg) {
		if (IJ.versionLessThan("1.32c")) {
			return;
		}
		segmentationDialog = new SegmentationDialog(this);
	}
	
	
	@Override
	public void OnStart() {
		if (segmentationDialog.isOmeroEnabled()) {
			runOmeroSegmentation();
			
		} else {
			runLocalSegmentation();
		}
	}
	
	
	public Client checkOMEROConnection(String hostname,
	                                   String port,
	                                   String username,
	                                   char[] password,
	                                   String group) {
		Client client = new Client();
		try {
			client.connect(hostname,
			               Integer.parseInt(port),
			               username,
			               password,
			               Long.valueOf(group));
		} catch (Exception exp) {
			IJ.error("Invalid connection values");
			return null;
		}
		return client;
	}
	
	
	private void runOmeroSegmentation() {
		// Check connection
		String hostname = segmentationDialog.getHostname();
		String port     = segmentationDialog.getPort();
		String username = segmentationDialog.getUsername();
		char[] password = segmentationDialog.getPassword();
		String group    = segmentationDialog.getGroup();
		Client client   = checkOMEROConnection(hostname, port, username, password, group);
		
		SegmentationParameters segmentationParameters = null;
		// Check config
		String configFile = segmentationDialog.getConfig();
		switch (segmentationDialog.getConfigMode()) {
			case DEFAULT:
				segmentationParameters = new SegmentationParameters(".", ".");
				break;
			case FILE:
				segmentationParameters = new SegmentationParameters(".", ".", configFile);
				break;
			case INPUT:
				SegmentationConfigDialog scd = segmentationDialog.getSegmentationConfigFileDialog();
				if (scd.isCalibrationSelected()) {
					LOGGER.info("w/ calibration");
					segmentationParameters = new SegmentationParameters(".", ".",
					                                                    Integer.parseInt(scd.getXCalibration()),
					                                                    Integer.parseInt(scd.getYCalibration()),
					                                                    Integer.parseInt(scd.getZCalibration()),
					                                                    Integer.parseInt(scd.getMinVolume()),
					                                                    Integer.parseInt(scd.getMaxVolume()),
					                                                    scd.getConvexHullDetection()
					);
				} else {
					LOGGER.info("w/out calibration");
					segmentationParameters = new SegmentationParameters(".", ".",
					                                                    Integer.parseInt(scd.getMinVolume()),
					                                                    Integer.parseInt(scd.getMaxVolume()),
					                                                    scd.getConvexHullDetection()
					);
				}
				break;
		}
		
		SegmentationCalling segmentation = new SegmentationCalling(segmentationParameters);
		segmentation.setExecutorThreads(segmentationDialog.getThreads());

		// Handle the source according to the type given
		String dataType = segmentationDialog.getDataType();
		Long   inputID  = Long.valueOf(segmentationDialog.getSourceID());
		Long   outputID = Long.valueOf(segmentationDialog.getOutputProject());
		try {
			if (dataType.equals("Image")) {
				ImageWrapper image = client.getImage(inputID);
				String       log;
				
				log = segmentation.runOneImageOMERO(image, outputID, client);
				segmentation.saveCropGeneralInfoOmero(client, outputID);
				
				if (!(log.equals(""))) {
					LOGGER.error("Nuclei which didn't pass the segmentation\n{}", log);
				}
			} else {
				List<ImageWrapper> images = null;
				
				switch (segmentationDialog.getDataType()) {
					case "Dataset":
						DatasetWrapper dataset = client.getDataset(inputID);
						images = dataset.getImages(client);
						break;
					case "Project":
						ProjectWrapper project = client.getProject(inputID);
						images = project.getImages(client);
						break;
					case "Tag":
						images = client.getImagesTagged(inputID);
						break;
				}
				String log;
				log = segmentation.runSeveralImagesOMERO(images, outputID, client, inputID);
				if (!(log.equals(""))) {
					LOGGER.error("Nuclei which didn't pass the segmentation\n{}", log);
				}
			}
			LOGGER.info("Segmentation process has ended successfully");
			IJ.showMessage("Segmentation process ended successfully on "+ segmentationDialog.getDataType()+"\\"+inputID);
		} catch (ServiceException se) {
			IJ.error("Unable to access to OMERO service");
		} catch (AccessException ae) {
			IJ.error("Cannot access " + dataType + "with ID = " + inputID + ".");
		} catch (Exception e) {
			LOGGER.error("An error occurred.", e);
		}
		client.disconnect();
	}
	
	
	public void runLocalSegmentation() {
		String input  = segmentationDialog.getInput();
		String output = segmentationDialog.getOutput();
		String config = segmentationDialog.getConfig();
		if (input == null || input.equals("")) {
			IJ.error("Input file or directory is missing");
		} else if (output == null || output.equals("")) {
			IJ.error("Output directory is missing");
		} else {
			try {
				LOGGER.info("Begin Segmentation process ");
				SegmentationParameters segmentationParameters = null;
				
				switch (segmentationDialog.getConfigMode()) {
					case FILE:
						if (config == null || config.equals("")) {
							IJ.error("Config file is missing");
						} else {
							LOGGER.info("Config file");
							segmentationParameters = new SegmentationParameters(input, output, config);
						}
						break;
					case INPUT:
						SegmentationConfigDialog scd = segmentationDialog.getSegmentationConfigFileDialog();
						if (scd.isCalibrationSelected()) {
							LOGGER.info("w/ calibration" +
							            "\nx: " + scd.getXCalibration() +
							            "\ny: " + scd.getYCalibration() +
							            "\nz: " + scd.getZCalibration());
							
							segmentationParameters = new SegmentationParameters(input, output,
							                                                    Integer.parseInt(scd.getXCalibration()),
							                                                    Integer.parseInt(scd.getYCalibration()),
							                                                    Integer.parseInt(scd.getZCalibration()),
							                                                    Integer.parseInt(scd.getMinVolume()),
							                                                    Integer.parseInt(scd.getMaxVolume()),
							                                                    scd.getConvexHullDetection()
							);
						} else {
							LOGGER.info("w/out calibration");
							segmentationParameters = new SegmentationParameters(input, output,
							                                                    Integer.parseInt(scd.getMinVolume()),
							                                                    Integer.parseInt(scd.getMaxVolume()),
							                                                    scd.getConvexHullDetection()
							);
						}
						break;
					case DEFAULT:
						LOGGER.info("w/out config");
						segmentationParameters = new SegmentationParameters(input, output);
						break;
				}
				
				SegmentationCalling otsuModified = new SegmentationCalling(segmentationParameters);
				otsuModified.setExecutorThreads(segmentationDialog.getThreads());

				File   file = new File(input);
				String log  = "";
				if (file.isDirectory()) {
					log = otsuModified.runSeveralImages2();
				} else if (file.isFile()) {
					log = otsuModified.runOneImage(input);
					otsuModified.saveCropGeneralInfo();
				}
				if (!(log.equals(""))) {
					LOGGER.error("Nuclei which didn't pass the segmentation\n{}", log);
				}
				
				LOGGER.info("Segmentation process has ended successfully");
				IJ.showMessage("Segmentation process ended successfully on "+ file.getName());
			} catch (IOException ioe) {
				IJ.error("File/Directory does not exist");
			} catch (Exception e) {
				LOGGER.error("An error occurred.", e);
			}
		}

	}
	
}
