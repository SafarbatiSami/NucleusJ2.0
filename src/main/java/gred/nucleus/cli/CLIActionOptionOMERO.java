package gred.nucleus.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.Validate.isTrue;


/** Inherited class to handle OMERO command line option */
public class CLIActionOptionOMERO extends CLIActionOptions {
	/** Host name server */
	private final Option hostname     = Option.builder("ho")
	                                          .longOpt("hostname")
	                                          .type(String.class)
	                                          .desc("Hostname of the OMERO server")
	                                          .numberOfArgs(1)
	                                          .build();
	/** Server port connection */
	private final Option port         = Option.builder("pt")
	                                          .longOpt("port")
	                                          .type(Integer.class)
	                                          .desc("Port used by OMERO")
	                                          .numberOfArgs(1)
	                                          .build();
	/** username connection */
	private final Option username     = Option.builder("u")
	                                          .longOpt("username")
	                                          .type(String.class)
	                                          .desc("Username in OMERO")
	                                          .numberOfArgs(1)
	                                          .build();
	/** OMERO password connection */
	private final Option password     = Option.builder("p")
	                                          .longOpt("password")
	                                          .type(String.class)
	                                          .desc("Password in OMERO")
	                                          .numberOfArgs(1)
	                                          .build();
	/** Group user connection */
	private final Option group        = Option.builder("g")
	                                          .longOpt("group")
	                                          .type(String.class)
	                                          .desc("Group in OMERO")
	                                          .numberOfArgs(1)
	                                          .build();


	/** Path to OMERO config file */
	private final Option omeroConfigFile = Option.builder("oc")
												.longOpt("omeroConfig")
												.type(String.class)
												.desc("Path to OMERO config file")
												.numberOfArgs(1)
												.build();


	/**
	 * Constructor with argument
	 *
	 * @param argument List of command line argument
	 */
	public CLIActionOptionOMERO(String[] argument) {
		super(argument);
		List<String> listArgs = Arrays.asList(argument);
		if( !(listArgs.contains("-oc") || listArgs.contains("-omeroConfig")) ) {
			hostname.setRequired(true);
			port.setRequired(true);
			username.setRequired(true);
			group.setRequired(true);
		}
		this.options.addOption(this.action);
		this.options.addOption(outputFolder);
		this.options.addOption(this.port);
		this.options.addOption(this.hostname);
		this.options.addOption(this.username);
		this.options.addOption(this.password);
		this.options.addOption(this.group);
		String inputDescription = "OMERO  inputs 2 information separated with slash separator :  " +
				"Type input: dataset, project, image, tag " +
				"Input id number" + "\n" +
				"Example : " + "\n" +
				"          dataset/1622";
		this.options.addOption(this.inputFolder);
		this.inputFolder.setDescription(inputDescription);
		this.options.addOption(this.inputFolder2);

		this.options.addOption(this.omeroConfigFile);
		try {
			this.cmd = this.parser.parse(this.options, argument);
			isTrue(availableActionOMERO(this.cmd.getOptionValue("action")));
			
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
	private static boolean availableActionOMERO(String action) {
		List<String> actionAvailableInOMERO = new ArrayList<>();
		actionAvailableInOMERO.add("autocrop");
		actionAvailableInOMERO.add("segmentation");
		actionAvailableInOMERO.add("generateOverlay");
		actionAvailableInOMERO.add("cropFromCoordinate");
		actionAvailableInOMERO.add("computeParameters");
		return actionAvailableInOMERO.contains(action);
	}
	
	
	private Option getGroup() {
		return this.group;
	}
	
	
	private Option getHostname() {
		return this.hostname;
	}
	
	
	private Option getPort() {
		return this.port;
	}
	
	
	private Option getUsername() {
		return this.username;
	}
	
	
	private Option getPassword() {
		return this.password;
	}
	
	
	private Option getOutputFolder() { return this.outputFolder; }

}