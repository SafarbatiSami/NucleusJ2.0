package gred.nucleus.plugins;

import fr.igred.omero.Client;
import gred.nucleus.core.ComputeNucleiParameters;
import gred.nucleus.dialogs.ComputeParametersDialog;
import gred.nucleus.dialogs.IDialogListener;
import ij.IJ;
import ij.Prefs;
import ij.measure.Calibration;
import ij.plugin.PlugIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;


public class ComputeParametersPlugin_ implements PlugIn,IDialogListener {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	ComputeParametersDialog computeParametersDialog;
	@Override
	public void run(String arg) {
		if (IJ.versionLessThan("1.32c")) {
			return;
		}
		computeParametersDialog = new ComputeParametersDialog();
	}
	
	@Override
	public void OnStart() {
		if (computeParametersDialog.isOmeroEnabled()) {
			runOMERO();
		} else {
			runlocal();
		}
	}
	
	/** Run computing parameters method. */
	
	
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
	void runlocal(){
		if (computeParametersDialog.getCalibrationStatus()) {
			Calibration calibration = new Calibration();
			calibration.pixelDepth = computeParametersDialog.getZCalibration();
			calibration.pixelWidth = computeParametersDialog.getXCalibration();
			calibration.pixelHeight = computeParametersDialog.getYCalibration();
			calibration.setUnit(computeParametersDialog.getUnit());
			ComputeNucleiParameters generateParameters = new ComputeNucleiParameters(
					computeParametersDialog.getRawDataDirectory(),
					computeParametersDialog.getWorkDirectory(),
					calibration);
			generateParameters.run();
		} else {
			ComputeNucleiParameters generateParameters = new ComputeNucleiParameters(
					computeParametersDialog.getRawDataDirectory(),
					computeParametersDialog.getWorkDirectory());
			generateParameters.run();
		}
		
	}
	public void runOMERO() {
		// Check connection
		String hostname = computeParametersDialog.getHostname();
		String port     = computeParametersDialog.getPort();
		String username = computeParametersDialog.getUsername();
		String password = computeParametersDialog.getPassword();
		String group    = computeParametersDialog.getGroup();
		String rawID    = computeParametersDialog.getRawDatasetID();
		String segID    = computeParametersDialog.getSegDatasetID();
		
		Prefs.set("omero.host", hostname);
		Prefs.set("omero.port", port);
		Prefs.set("omero.user", username);
		
		Client client = checkOMEROConnection(hostname, port, username, password.toCharArray(), group);
		ComputeNucleiParameters generateParameters = new ComputeNucleiParameters();
	
		try {
				try {
					LOGGER.info("Begin Compute parameter process ");
					generateParameters.runFromOMERO(rawID, segID, client); // Run Compute parameters
					LOGGER.info("Compute parameter process  has ended successfully");
				} catch (Exception e) {
					LOGGER.info("Compute parameter process  has failed");
					LOGGER.error("An error occurred.", e);
				}
			
		} catch (Exception e) {
			LOGGER.error("An error occurred.", e);
		}
		client.disconnect();
	}
	
}