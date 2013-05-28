package co.edu.sanmartin.fuzzyclustering.cleaner.execute.thread;

import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.Cleaner;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.Stemmer;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase encargada de realizar el proceso de normalización de los archivos
 * @author Ricardo Carvajal Salamanca
 *
 */
public class CleanerWorkerThread implements Callable<String>{
	private Cleaner cleaner;
	private Stemmer stemmer;
	private DocumentDTO document;
	private WorkspaceDTO workspace;

	private static Logger logger = Logger.getLogger(CleanerWorkerThread.class);
	
	
	public CleanerWorkerThread(WorkspaceDTO workspace,DocumentDTO document) {
		this.document = document;
		this.workspace = workspace;
		this.cleaner = new Cleaner(workspace);
		this.stemmer = new Stemmer(workspace);
	}

	public String call() throws Exception {
		logger.trace("Init CleanerWorkerThread Call Method");
		String normalizedText = IRFacade.getInstance(this.workspace).getNormalizedDocumentText(document.getLazyData());
		workspace.getPersistence().writeFile(EDataFolder.CLEAN, document.getName(), normalizedText);
		this.document.setCleanDate(new Date());
		workspace.getPersistence().updateDocument(this.document);
	    logger.info("Write Clean File: " + this.document.getName());
		return "Hecho";
		
	}

}
