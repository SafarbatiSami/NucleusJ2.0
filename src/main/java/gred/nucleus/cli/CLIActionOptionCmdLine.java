package gred.nucleus.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.Validate.isTrue;


/** class to handle command line option */
public class CLIActionOptionCmdLine extends CLIActionOptions {
	
	/**
	 * @param args command line argument
	 */
	public CLIActionOptionCmdLine(String[] args) {
		super(args);
		this.action.setDescription(this.action.getDescription() + "\n" +
		                           "computeParameters : compute parameters \n" +
		                           "computeParametersDL : compute parameters for machine leaning\n" +
		                           "generateProjection : generate projection from coordinates\n" +
		                           "cropFromCoordinate : crop wide-field image from coordinate\n" +
		                           "generateOverlay : generate overlay from images \n");
		
		checkSpecificOptions();
		try {
			this.cmd = this.parser.parse(this.options, args);
			isTrue(availableActionCMD(this.cmd.getOptionValue("action")));
		} catch (ParseException exp) {
			System.console().writer().println(exp.getMessage() + "\n");
			System.console().writer().println(getHelperInfo());
			System.exit(1);
		} catch (Exception exp) {
			System.console().writer().println("Action option \"" +
			                                  this.cmd.getOptionValue("action") +
			                                  "\" not available" + "\n");
			System.console().writer().println(getHelperInfo());
			System.exit(1);
		}
	}
	
	
	/**
	 * Method to check action parameter
	 *
	 * @param action nucleusJ2.0 action to run
	 *
	 * @return boolean existing action
	 */
	private static boolean availableActionCMD(String action) {
		List<String> actionAvailableInOMERO = new ArrayList<>();
		actionAvailableInOMERO.add("autocrop");
		actionAvailableInOMERO.add("segmentation");
		actionAvailableInOMERO.add("computeParameters");
		actionAvailableInOMERO.add("computeParametersDL");
		actionAvailableInOMERO.add("generateProjection");
		actionAvailableInOMERO.add("generateProjectionFiltered");
		actionAvailableInOMERO.add("cropFromCoordinate");
		actionAvailableInOMERO.add("generateOverlay");
		return actionAvailableInOMERO.contains(action);
	}
	
	
	/** Method to check specific action parameters */
	private void checkSpecificOptions() {
		switch (this.cmd.getOptionValue("action")) {
			case "autocrop":
			case "segmentation":
				this.inputFolder.setDescription("Path to input folder containing images to analyse\n");
				this.options.addOption(this.outputFolder);
				break;
			
			case "computeParameters":
				this.inputFolder.setDescription("Path to input folder containing RAW images\n");
				this.inputFolder2.setDescription("Path to input folder containing SEGMENTED images\n");
				this.options.addOption(this.inputFolder2);
				this.omero.setDescription("NOT AVAILABLE");
				break;
			
			case "computeParametersDL":
				this.inputFolder.setDescription("Path to input folder containing RAW images\n");
				this.inputFolder2.setDescription("Path to input folder containing machine leaning SEGMENTED images\n");
				this.options.addOption(this.inputFolder2);
				this.omero.setDescription("NOT AVAILABLE");
				break;
			
			case "generateProjection":
				this.inputFolder.setDescription("Path to input folder containing coordinates files\n");
				this.inputFolder2.setDescription("Path to input folder containing raw data\n");
				this.options.addOption(this.inputFolder2);
				break;
			
			case "generateProjectionFiltered":
				this.inputFolder.setDescription("Path to input folder containing coordinates files\n");
				this.inputFolder2.setDescription(
						"Path to input folder containing kept images after segmentation filter\n");
				this.inputFolder3.setDescription("Path to input folder containing initial Zprojection\n");
				this.options.addOption(this.inputFolder2);
				this.options.addOption(this.inputFolder3);
				this.omero.setDescription("NOT AVAILABLE");
				break;
			
			case "cropFromCoordinate":
				this.inputFolder.setDescription("Path to tabulated file containing 2 columns :\n" +
				                                "pathToCoordinateFile   pathToRawImageAssociate\n");
				this.options.addOption(this.inputFolder2);
				this.options.addOption(this.outputFolder);
				break;
			
			case "generateOverlay":
				this.inputFolder.setDescription("Path to input folder containing Z-Projections\n");
				this.inputFolder2.setDescription("Path to input folder containing DIC images\n");
				this.options.addOption(this.inputFolder2);
				break;
		}
	}
	
}