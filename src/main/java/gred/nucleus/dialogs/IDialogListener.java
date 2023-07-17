package gred.nucleus.dialogs;

import fr.igred.omero.exception.AccessException;
import fr.igred.omero.exception.ServiceException;

import java.util.concurrent.ExecutionException;


public interface IDialogListener {
	void OnStart() throws AccessException, ServiceException, ExecutionException;
	
}
