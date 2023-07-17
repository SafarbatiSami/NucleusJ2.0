package gred.nucleus.plugins;

import fr.igred.omero.Client;
import fr.igred.omero.exception.AccessException;
import fr.igred.omero.exception.ServiceException;
import fr.igred.omero.repository.DatasetWrapper;
import fr.igred.omero.repository.ImageWrapper;
import gred.nucleus.autocrop.AutoCropCalling;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.dialogs.AutocropConfigDialog;
import gred.nucleus.dialogs.AutocropDialog;
import gred.nucleus.dialogs.IDialogListener;
import gred.nucleus.files.FilesNames;
import ij.IJ;
import ij.plugin.PlugIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.List;


public class Autocrop_ implements PlugIn, IDialogListener {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	AutocropDialog autocropDialog;
	
	
	/**
	 * Run method for imageJ plugin for the autocrop
	 *
	 * @param arg use by imageJ
	 */
	@Override
	public void run(String arg) {
		if (IJ.versionLessThan("1.32c")) {
			return;
		}
		autocropDialog = new AutocropDialog(this);
	}
	
	
	@Override
	public void OnStart() {
		if (autocropDialog.isOmeroEnabled()) {
			runOmeroAutocrop();
		} else {
			runLocalAutocrop();
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
	
	
	private void runOmeroAutocrop() {
		// Check connection
		String hostname = autocropDialog.getHostname();
		String port     = autocropDialog.getPort();
		String username = autocropDialog.getUsername();
		char[] password = autocropDialog.getPassword();
		String group    = autocropDialog.getGroup();
		Client client   = checkOMEROConnection(hostname, port, username, password, group);
		
		AutocropParameters autocropParameters = null;
		// Check config
		String configFile = autocropDialog.getConfig();
		switch (autocropDialog.getConfigMode()) {
			case DEFAULT:
				autocropParameters = new AutocropParameters(".", ".");
				break;
			case FILE:
				autocropParameters = new AutocropParameters(".", ".", configFile);
				break;
			case INPUT:
				AutocropConfigDialog acd = autocropDialog.getAutocropConfigFileDialog();
				if (acd.isCalibrationSelected()) {
					LOGGER.info("w/ calibration");
					autocropParameters = new AutocropParameters(".",
					                                            ".",
					                                            Integer.parseInt(acd.getXCalibration()),
					                                            Integer.parseInt(acd.getYCalibration()),
					                                            Integer.parseInt(acd.getZCalibration()),
					                                            Integer.parseInt(acd.getXCropBoxSize()),
					                                            Integer.parseInt(acd.getYCropBoxSize()),
					                                            Integer.parseInt(acd.getZCropBoxSize()),
					                                            Integer.parseInt(acd.getBoxNumberFontSize()),
					                                            Integer.parseInt(acd.getSlicesOTSUComputing()),
					                                            Integer.parseInt(acd.getThresholdOTSUComputing()),
					                                            Integer.parseInt(acd.getChannelToComputeThreshold()),
					                                            Integer.parseInt(acd.getMinVolume()),
					                                            Integer.parseInt(acd.getMaxVolume()),
					                                            Integer.parseInt(acd.getBoxesPercentSurfaceToFilter()),
					                                            acd.isRegroupBoxesSelected()
					);
				} else {
					LOGGER.info("w/out calibration");
					autocropParameters = new AutocropParameters(".",
					                                            ".",
					                                            Integer.parseInt(acd.getXCropBoxSize()),
					                                            Integer.parseInt(acd.getYCropBoxSize()),
					                                            Integer.parseInt(acd.getZCropBoxSize()),
																Integer.parseInt(acd.getBoxNumberFontSize()),
					                                            Integer.parseInt(acd.getSlicesOTSUComputing()),
					                                            Integer.parseInt(acd.getThresholdOTSUComputing()),
					                                            Integer.parseInt(acd.getChannelToComputeThreshold()),
					                                            Integer.parseInt(acd.getMinVolume()),
					                                            Integer.parseInt(acd.getMaxVolume()),
					                                            Integer.parseInt(acd.getBoxesPercentSurfaceToFilter()),
					                                            acd.isRegroupBoxesSelected()
					);
				}
				break;
		}
		
		AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
		autoCrop.setExecutorThreads(autocropDialog.getThreads());

		// Handle the source according to the type given
		
		String dataType = autocropDialog.getDataType();
		Long   inputID  = Long.valueOf(autocropDialog.getSourceID());
		Long   outputID = Long.valueOf(autocropDialog.getOutputProject());
		try {
			if (dataType.equals("Image")) {
				ImageWrapper image      = client.getImage(inputID);
				int          sizeC      = image.getPixels().getSizeC();
				Long[]       outputsDat = new Long[sizeC];
				FilesNames outPutFilesNames = new FilesNames(image.getName());
				String imgPrefix = outPutFilesNames.prefixNameFile();
				for (int i = 0; i < sizeC; i++) {
					DatasetWrapper dataset = new DatasetWrapper("C" + i + "_" +imgPrefix, "");
					outputsDat[i] =
							client.getProject(outputID).addDataset(client, dataset).getId();
				}
				
				autoCrop.runImageOMERO(image, outputsDat, client); // Run segmentation
				autoCrop.saveGeneralInfoOmero(client, outputsDat);
				
			} else {
				List<ImageWrapper> images = null;
				String             name   = "";
				
				if (dataType.equals("Dataset")) {
					DatasetWrapper dataset = client.getDataset(inputID);
					name = dataset.getName();
					images = dataset.getImages(client);
				} else if (dataType.equals("Tag")) {
					images = client.getImagesTagged(inputID);
				}
				int    sizeC      = images.get(0).getPixels().getSizeC();
				Long[] outputsDat = new Long[sizeC];
				for (int i = 0; i < sizeC; i++) {
					DatasetWrapper dataset = new DatasetWrapper("raw_C" + i + "_" + name, "");
					outputsDat[i] =
							client.getProject(outputID).addDataset(client, dataset).getId();
				}
				
				autoCrop.runSeveralImageOMERO(images, outputsDat, client); // Run segmentation
				
			}
			LOGGER.info("Autocrop process has ended successfully");
			IJ.showMessage("Autocrop process ended successfully on "+ autocropDialog.getDataType()+"\\"+inputID);
		} catch (ServiceException se) {
			IJ.error("Unable to access to OMERO service");
		} catch (AccessException ae) {
			IJ.error("Cannot access " + dataType + "with ID = " + inputID + ".");
		} catch (Exception e) {
			LOGGER.error("An error occurred.", e);
		}
		client.disconnect();
	}
	
	
	private void runLocalAutocrop() {
		String input  = autocropDialog.getInput();
		String output = autocropDialog.getOutput();
		String config = autocropDialog.getConfig();
		if (input == null || input.equals("")) {
			IJ.error("Input file or directory is missing");
		} else if (output == null || output.equals("")) {
			IJ.error("Output directory is missing");
		} else {
			try {
				LOGGER.info("Begin Autocrop process ");
				
				AutocropParameters autocropParameters = null;
				
				switch (autocropDialog.getConfigMode()) {
					case FILE:
						if (config == null || config.equals("")) {
							IJ.error("Config file is missing");
						} else {
							LOGGER.info("Config file");
							autocropParameters = new AutocropParameters(input, output, config);
						}
						break;
					case INPUT:
						AutocropConfigDialog acd = autocropDialog.getAutocropConfigFileDialog();
						if (acd.isCalibrationSelected()) {
							LOGGER.info("w/ calibration");
							autocropParameters = new AutocropParameters(input,
							                                            output,
							                                            Integer.parseInt(acd.getXCalibration()),
							                                            Integer.parseInt(acd.getYCalibration()),
							                                            Integer.parseInt(acd.getZCalibration()),
							                                            Integer.parseInt(acd.getXCropBoxSize()),
							                                            Integer.parseInt(acd.getYCropBoxSize()),
							                                            Integer.parseInt(acd.getZCropBoxSize()),
																		Integer.parseInt(acd.getBoxNumberFontSize()),
							                                            Integer.parseInt(acd.getSlicesOTSUComputing()),
							                                            Integer.parseInt(acd.getThresholdOTSUComputing()),
							                                            Integer.parseInt(acd.getChannelToComputeThreshold()),
							                                            Integer.parseInt(acd.getMinVolume()),
							                                            Integer.parseInt(acd.getMaxVolume()),
							                                            Integer.parseInt(acd.getBoxesPercentSurfaceToFilter()),
							                                            acd.isRegroupBoxesSelected());
						} else {
							LOGGER.info("w/out calibration");
							autocropParameters = new AutocropParameters(input,
							                                            output,
							                                            Integer.parseInt(acd.getXCropBoxSize()),
							                                            Integer.parseInt(acd.getYCropBoxSize()),
							                                            Integer.parseInt(acd.getZCropBoxSize()),
																		Integer.parseInt(acd.getBoxNumberFontSize()),
							                                            Integer.parseInt(acd.getSlicesOTSUComputing()),
							                                            Integer.parseInt(acd.getThresholdOTSUComputing()),
							                                            Integer.parseInt(acd.getChannelToComputeThreshold()),
							                                            Integer.parseInt(acd.getMinVolume()),
							                                            Integer.parseInt(acd.getMaxVolume()),
							                                            Integer.parseInt(acd.getBoxesPercentSurfaceToFilter()),
							                                            acd.isRegroupBoxesSelected());
						}
						break;
					case DEFAULT:
						LOGGER.info("w/out config");
						autocropParameters = new AutocropParameters(input, output);
						break;
				}
				AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
				autoCrop.setExecutorThreads(autocropDialog.getThreads());
				File            file     = new File(input);
				if (file.isDirectory()) {
					autoCrop.runFolder();
				} else if (file.isFile()) {
					autoCrop.runFile(input);
					autoCrop.saveGeneralInfo();
				}
				LOGGER.info("Autocrop process has ended successfully");
				IJ.showMessage("Segmentation process ended successfully on "+ file.getName());
			} catch (Exception e) {
				LOGGER.error("An error occurred during autocrop.", e);
			}
		}
	}
	
}
