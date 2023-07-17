package gred.nucleus.mains;

import gred.nucleus.cli.*;
import gred.nucleus.dialogs.MainGui;

import ij.IJ;
import ij.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.lang.invoke.MethodHandles;

import java.util.Arrays;
import java.util.List;


public class Main {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void main(String[] args) throws Exception {
		List<String> listArgs = Arrays.asList(args);
		
		/* Allow IJ threads from thread pool to timeout */
		//ThreadUtil.threadPoolExecutor.allowCoreThreadTimeOut(true);
		
		if (listArgs.contains("-h") || listArgs.contains("-help")) {
			CLIHelper.run(args);
		} else if ((listArgs.contains("-ome")) || (listArgs.contains("-omero"))) {
			CLIActionOptionOMERO command = new CLIActionOptionOMERO(args);
			CLIRunActionOMERO cliOMERO = new CLIRunActionOMERO(command.getCmd());
			cliOMERO.run();
		} else if (listArgs.contains("-nj") || listArgs.contains("-cli") || listArgs.contains("-CLI")){
			CLIActionOptionCmdLine command = new CLIActionOptionCmdLine(args);
			CLIRunAction cli = new CLIRunAction(command.getCmd());
			cli.run();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					MainGui gui   = new MainGui();
					gui.setVisible(true);
				}
			});
		}
		//ThreadUtil.threadPoolExecutor.shutdown();
	}
	
}


