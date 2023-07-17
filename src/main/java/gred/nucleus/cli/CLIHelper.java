package gred.nucleus.cli;

import gred.nucleus.files.Directory;
import gred.nucleus.files.OutputTextFile;
import gred.nucleus.mains.Version;
import org.apache.commons.cli.HelpFormatter;


/** Class to generate helper */
public class CLIHelper {
	
	private CLIHelper() {
		// DO NOTHING
	}
	
	/**
	 * Main method
	 *
	 * @param args command line arguments
	 */
	public static void run(String[] args) {
		if (args.length == 2) {
			specificAction(args[1]);
		} else {
			cmdHelpFull();
		}
	}
	
	
	/**
	 * Method get help for command line with example command line
	 */
	private static void cmdHelpFull() {
		String exampleCommand = "java -jar NucleusJ_2-" + Version.get() + ".jar ";
		String exampleArgument = "-action segmentation " +
		                         "-input path/to/input/folder/ " +
		                         "-output path/to/output/folder/ ";
		String[]               exampleCMD = exampleArgument.split(" ");
		CLIActionOptionCmdLine command    = new CLIActionOptionCmdLine(exampleCMD);
		HelpFormatter          formatter  = new HelpFormatter();
		formatter.printHelp("NucleusJ2.0 cli : ", command.getOptions());
		System.console().writer().println("\nCommand line example : \n" +
		                                  exampleCommand + " " + exampleArgument + "\n\n");
		
		String exampleArgumentOMERO = "-omero " +
		                              "-action segmentation " +
		                              "-input path/to/input/folder/ " +
		                              "-output path/to/output/folder/ " +
		                              "-hostname omero-server-address " +
		                              "-port 0 " +
		                              "-group 000";
		String[]             exampleOMEROCMD = exampleArgumentOMERO.split(" ");
		CLIActionOptionOMERO commandOMERO    = new CLIActionOptionOMERO(exampleOMEROCMD);
		formatter.printHelp("NucleusJ2.0 OMERO MODE: ", commandOMERO.getOptions());
		System.console().writer().println("\nCommand line example : \n\n" +
		                                  exampleCommand + " " + exampleArgumentOMERO);
		
		System.exit(1);
	}
	
	
	/**
	 * Helper for specific action.
	 *
	 * @param action action
	 */
	private static void specificAction(String action) {
		String                 exampleCommand = "java -jar NucleusJ_2-" + Version.get() + ".jar ";
		String                 exampleArgument;
		String[]               exampleCMD;
		HelpFormatter          formatter;
		CLIActionOptionCmdLine command;
		switch (action) {
			case "segmentation":
				exampleArgument = "-action segmentation " +
				                  "-input path/to/input/folder/ " +
				                  "-output path/to/output/folder/ ";
				exampleCMD = exampleArgument.split(" ");
				command = new CLIActionOptionCmdLine(exampleCMD);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 segmentation cli : ", command.getOptions());
				System.console().writer().println("\nCommand line example : \n" +
				                                  exampleCommand + exampleArgument + "\n\n");
				
				String exampleArgumentOMERO = "-omero " +
				                              "-action segmentation " +
				                              "-input path/to/input/folder/ " +
				                              "-output path/to/output/folder/ " +
				                              "-hostname omero-server-address " +
				                              "-port 0 " +
				                              "-group 000";
				
				String[] exampleOMEROCMD = exampleArgumentOMERO.split(" ");
				CLIActionOptionOMERO commandOMERO = new CLIActionOptionOMERO(exampleOMEROCMD);
				formatter.printHelp("NucleusJ2.0 segmentation OMERO MODE: ", commandOMERO.getOptions());
				System.console().writer().println("\nCommand line example : \n\n" +
				                                  exampleCommand + " " + exampleArgumentOMERO);
				break;
			
			case "autocrop":
				exampleArgument = "-action autocrop " +
				                  "-input path/to/input/folder/ " +
				                  "-output path/to/output/folder/ ";
				exampleCMD = exampleArgument.split(" ");
				command = new CLIActionOptionCmdLine(exampleCMD);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 autocrop cli : ", command.getOptions());
				System.console().writer().println("\nCommand line example : \n" +
				                                  exampleCommand + exampleArgument + "\n\n");
				
				
				exampleArgumentOMERO = "-omero " +
				                       "-action autocrop " +
				                       "-input path/to/input/folder/ " +
				                       "-output path/to/output/folder/ " +
				                       "-hostname omero-server-address " +
				                       "-port 0 " +
				                       "-group 000";
				exampleOMEROCMD = exampleArgumentOMERO.split(" ");
				commandOMERO = new CLIActionOptionOMERO(exampleOMEROCMD);
				formatter.printHelp("NucleusJ2.0 autocrop OMERO MODE: ", commandOMERO.getOptions());
				System.console().writer().println("\nCommand line example : \n\n" +
				                                  exampleCommand + exampleArgumentOMERO);
				break;
			
			case "computeParameters":
				exampleArgument = "-action computeParameters " +
				                  "-input path/to/raw/image/folder/ " +
				                  "-input2 path/to/segmented/image/folder/ ";
				exampleCMD = exampleArgument.split(" ");
				command = new CLIActionOptionCmdLine(exampleCMD);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 computeParameters cli : ", command.getOptions());
				System.console().writer().println("\nCommand line example : \n" +
				                                  exampleCommand + exampleArgument + "\n\n");
				break;
			
			case "computeParametersDL":
				exampleArgument = "-action computeParametersDL " +
				                  "-input path/to/raw/image/folder/ " +
				                  "-input2 path/to/segmented/image/folder/ ";
				exampleCMD = exampleArgument.split(" ");
				command = new CLIActionOptionCmdLine(exampleCMD);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 computeParametersDL cli : ", command.getOptions());
				System.console().writer().println("\nCommand line example : \n" +
				                                  exampleCommand + exampleArgument + "\n\n");
				break;
			
			case "generateProjection":
				exampleArgument = "-action generateProjection " +
				                  "-input path/to/coordinate/file/folder/ " +
				                  "-input2 path/to/raw/image/folder/ ";
				exampleCMD = exampleArgument.split(" ");
				command = new CLIActionOptionCmdLine(exampleCMD);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 generateProjection cli : ", command.getOptions());
				System.console().writer().println("\nCommand line example : \n" +
				                                  exampleCommand + exampleArgument + "\n\n");
				break;
			
			case "generateProjectionFiltered":
				exampleArgument = "-action generateProjectionFiltered " +
				                  "-input path/to/coordinate/file/folder/ " +
				                  "-input2 path/to/segmented/image/folder/ " +
				                  "-input3 path/to/ZProjection/folder/";
				exampleCMD = exampleArgument.split(" ");
				command = new CLIActionOptionCmdLine(exampleCMD);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 generateProjectionFiltered cli : ", command.getOptions());
				System.console().writer().println("\nCommand line example : \n" +
				                                  exampleCommand + exampleArgument + "\n\n");
				break;
			
			case "CropFromCoordinate":
				exampleArgument = "-action CropFromCoordinate " +
				                  "-input path/to/coordinate/file/folder/ ";
				exampleCMD = exampleArgument.split(" ");
				command = new CLIActionOptionCmdLine(exampleCMD);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 CropFromCoordinate cli : ", command.getOptions());
				System.console().writer().println("\nCommand line example : \n" +
				                                  exampleCommand + exampleArgument + "\n\n");
				break;
			
			case "GenerateOverlay":
				exampleArgument = "-action GenerateOverlay " +
						"-input path/to/input/zprojection/ " +
						"-input2 path/to/input/dic_images/";
				exampleCMD = exampleArgument.split(" ");
				command = new CLIActionOptionCmdLine(exampleCMD);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 GenerateOverlay cli : ", command.getOptions());
				System.console().writer().println("\nCommand line example : \n" +
						exampleCommand + exampleArgument + "\n\n");

				exampleArgumentOMERO = "-omero " +
						"-action GenerateOverlay " +
						"-input ZProjection_dataset_ID " +
						"-input2 DIC_dataset_ID " +
						"-output output_project_ID " +
						"-port 0 " +
						"-group 000";
				exampleOMEROCMD = exampleArgumentOMERO.split(" ");
				commandOMERO = new CLIActionOptionOMERO(exampleOMEROCMD);
				formatter.printHelp("NucleusJ2.0 GenerateOverlay OMERO MODE: ", commandOMERO.getOptions());
				System.console().writer().println("\nCommand line example : \n\n" +
						exampleCommand + exampleArgumentOMERO);
				///
				break;
			
			case "configFileExample":
				
				String autocropConfigOption = "xCropBoxSize:40\n" +
				                              "yCropBoxSize:40\n" +
				                              "zCropBoxSize:20\n" +
				                              "minVolumeNucleus:1\n" +
				                              "maxVolumeNucleus:2147483647\n" +
				                              "thresholdOTSUComputing:20\n" +
				                              "channelToComputeThreshold:0\n" +
				                              "slicesOTSUComputing:0\n" +
				                              "boxesPercentSurfaceToFilter:50\n" +
				                              "boxesRegrouping:10\n" +
				                              "xCal:1\n" +
				                              "yCal:1\n" +
				                              "zCal:1";
				
				String segConfigOption =
						"thresholdOTSUComputing:20\n" +
						"ConvexHullDetection:true\n" +
						"xCal:1\n" +
						"yCal:1\n" +
						"zCal:1";
				System.console().writer().println("Two config file with default parameters generate: \n");
				
				saveFile(autocropConfigOption, "autocropConfigListParameters");
				saveFile(segConfigOption, "segmentationConfigListParameters");
				System.console().writer().println("autocrop parameters details: " +
				                                  "https://gitlab.com/DesTristus/NucleusJ2.0/-/wikis/Autocrop#list-of-available-parameters \n" +
				                                  "segmentation parameters details: " +
				                                  "https://gitlab.com/DesTristus/NucleusJ2.0/-/wikis/Nucleus-segmentation#list-of-available-parameters");
				break;
			
			default:
				exampleArgument = "-action segmentation " +
				                  "-input path/to/input/folder/ " +
				                  "-output path/to/output/folder/ ";
				exampleCMD = exampleArgument.split(" ");
				CLIActionOptions wrongAction = new CLIActionOptions(exampleCMD);
				System.console().writer().println("Invalid action \"" + action + "\" :\n");
				System.console().writer().println(wrongAction.getHelperInfo());
				break;
		}
	}
	
	
	/**
	 * Save information use to save config file parameter example.
	 *
	 * @param text     text to save
	 * @param fileName file name
	 */
	public static void saveFile(String text, String fileName) {
		Directory dirOutput = new Directory(System.getProperty("user.dir"));
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(
				dirOutput.getDirPath()
				+ dirOutput.getSeparator()
				+ fileName);
		resultFileOutputOTSU.saveTextFile(text, true);
	}
	
}
