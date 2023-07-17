package gred.nucleus.cli;

import gred.nucleus.autocrop.AutoCropCalling;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.autocrop.CropFromCoordinates;
import gred.nucleus.autocrop.GenerateOverlay;
import gred.nucleus.autocrop.GenerateProjectionFromCoordinates;
import gred.nucleus.core.ComputeNucleiParameters;
import gred.nucleus.machinelearning.ComputeNucleiParametersML;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;

import gred.nucleus.plugins.ChromocenterParameters;
import gred.nucleus.process.ChromocenterCalling;

import loci.formats.FormatException;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;


public class CLIRunAction {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	/** Command line */
	private final CommandLine cmd;
	
	
	public CLIRunAction(CommandLine cmd) {
		this.cmd = cmd;
	}
	
	
	public void run() throws Exception {
		switch (this.cmd.getOptionValue("action")) {
			case "autocrop":
				runAutocrop();
				break;
			case "segmentation":
				runSegmentation();
				break;
			case "computeParameters":
				runComputeNucleiParameters();
				break;
			case "computeParametersDL":
				runComputeNucleiParametersDL();
				break;
			case "generateProjection":
				runProjectionFromCoordinates();
				break;
			case "cropFromCoordinate":
				runCropFromCoordinates();
				break;
			case "generateOverlay":
				runGenerateOV();
				break;
			case "segCC":
				runSegCC();
			default:
				throw new IllegalArgumentException("Invalid action.");
		}
	}
	
	private void runSegCC()throws Exception {
		ChromocenterParameters chromocenterParameters = new ChromocenterParameters(
				this.cmd.getOptionValue("iRaw"),
				this.cmd.getOptionValue("iSeg"),
				this.cmd.getOptionValue("o"));
		if (this.cmd.hasOption("isG")) chromocenterParameters._gaussianOnRaw = true;
		if (this.cmd.hasOption("isF")) chromocenterParameters._sizeFilterConnectedComponent = true;
		if (this.cmd.hasOption("noC")) chromocenterParameters._noChange = true;
		if (this.cmd.hasOption("gX"))
			chromocenterParameters._gaussianBlurXsigma =  Double.parseDouble(cmd.getOptionValue("gX"));
		
		if (this.cmd.hasOption("gY"))
			chromocenterParameters._gaussianBlurYsigma =  Double.parseDouble(cmd.getOptionValue("gY"));
		
		if (this.cmd.hasOption("gZ"))
			chromocenterParameters._gaussianBlurZsigma =  Double.parseDouble(cmd.getOptionValue("gZ"));
		
		if (this.cmd.hasOption("min"))
			chromocenterParameters._minSizeConnectedComponent =  Double.parseDouble(cmd.getOptionValue("min"));
		if (this.cmd.hasOption("max"))
			
			chromocenterParameters._maxSizeConnectedComponent =  Double.parseDouble(cmd.getOptionValue("max"));
		if (this.cmd.hasOption("f"))
			chromocenterParameters._factor=  Double.parseDouble(cmd.getOptionValue("f"));
		if (this.cmd.hasOption("n"))
			chromocenterParameters._neigh=  Integer.parseInt(cmd.getOptionValue("n"));
		
		ChromocenterCalling ccCalling= new ChromocenterCalling(chromocenterParameters);
		try {
			
			System.out.println("-iRaw "+chromocenterParameters.inputFolder+" -iSeg "+chromocenterParameters._segInputFolder+" - "+chromocenterParameters.outputFolder);
			ccCalling.runSeveralImages2();
		} catch (Exception e) { e.printStackTrace(); }
		System.out.println("End !!! Results available:"+ chromocenterParameters.outputFolder);
		
		
	}
	private void runGenerateOV() throws Exception {
		GenerateOverlay ov = new GenerateOverlay(this.cmd.getOptionValue("input"),
												 this.cmd.getOptionValue("input2"));
		ov.run();
	}
	
	
	private void runCropFromCoordinates() throws IOException, FormatException {
		CropFromCoordinates cropFromCoordinates = new CropFromCoordinates(
				this.cmd.getOptionValue("input"),
				this.cmd.getOptionValue("input2"),
				this.cmd.getOptionValue("output")
				);
		cropFromCoordinates.run();
	}

	
	
	private void runProjectionFromCoordinates() throws Exception {
		if (this.cmd.hasOption("coordinateFiltered")) {
			GenerateProjectionFromCoordinates projection =
					new GenerateProjectionFromCoordinates(this.cmd.getOptionValue("input"),
					                                      this.cmd.getOptionValue("input2"),
					                                      this.cmd.getOptionValue("input3"));
			projection.generateProjectionFiltered();
		} else {
			GenerateProjectionFromCoordinates projection =
					new GenerateProjectionFromCoordinates(this.cmd.getOptionValue("input"),
					                                      this.cmd.getOptionValue("input2"));
			projection.generateProjection();
		}
	}
	
	
	private void runAutocrop() {
		AutocropParameters autocropParameters = new AutocropParameters(this.cmd.getOptionValue("input"),
		                                                               this.cmd.getOptionValue("output"));
		if (this.cmd.hasOption("config")) {
			autocropParameters.addGeneralProperties(this.cmd.getOptionValue("config"));
			autocropParameters.addProperties(this.cmd.getOptionValue("config"));
		}
		File path = new File(this.cmd.getOptionValue("input"));
		if (path.isFile()) {
			AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
			autoCrop.runFile(this.cmd.getOptionValue("input"));
			autoCrop.saveGeneralInfo();
		} else {
			AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
			if(this.cmd.hasOption("threads")) {
				autoCrop.setExecutorThreads(Integer.parseInt(this.cmd.getOptionValue("threads")));
			}
			autoCrop.runFolder();
		}
	}
	
	
	private void runSegmentation() throws Exception {
		SegmentationParameters segmentationParameters =
				new SegmentationParameters(this.cmd.getOptionValue("input"), this.cmd.getOptionValue("output"));
		if (this.cmd.hasOption("config")) {
			segmentationParameters.addGeneralProperties(this.cmd.getOptionValue("config"));
			segmentationParameters.addProperties(this.cmd.getOptionValue("config"));
		}
		File path = new File(this.cmd.getOptionValue("input"));
		if (path.isFile()) {
			SegmentationCalling otsuModified = new SegmentationCalling(segmentationParameters);
			try {
				String log = otsuModified.runOneImage(this.cmd.getOptionValue("input"));
				otsuModified.saveCropGeneralInfo();
				if (!(log.equals(""))) {
					LOGGER.error("Nuclei which didn't pass the segmentation\n{}", log);
				}
			} catch (IOException e) {
				LOGGER.error("An error occurred.", e);
			}
		} else {
			SegmentationCalling otsuModified = new SegmentationCalling(segmentationParameters);
			try {
				if(this.cmd.hasOption("threads")) {
					otsuModified.setExecutorThreads(Integer.parseInt(this.cmd.getOptionValue("threads")));
				}
				String log = otsuModified.runSeveralImages2();
				if (!(log.equals(""))) {
					LOGGER.error("Nuclei which didn't pass the segmentation\n{}", log);
				}
			} catch (IOException e) {
				LOGGER.error("An error occurred.", e);
			}
		}
	}
	
	
	private void runComputeNucleiParameters() {
		ComputeNucleiParameters generateParameters = new ComputeNucleiParameters(this.cmd.getOptionValue("input"),
		                                                                         this.cmd.getOptionValue("input2"));
		if (this.cmd.hasOption("config")) generateParameters.addConfigParameters(this.cmd.getOptionValue("config"));
		generateParameters.run();
	}
	
	
	private void runComputeNucleiParametersDL() throws Exception {
		ComputeNucleiParametersML computeParameters = new ComputeNucleiParametersML(this.cmd.getOptionValue("input"),
		                                                                            this.cmd.getOptionValue("input2"));
		computeParameters.run();
	}
	
}
