package co.edu.sanmartin.fuzzyclustering.cleaner.execute.thread;

import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.normalize.Cleaner;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.Stemmer;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase encargada de realizar el proceso de normalizaci�n de los archivos
 * @author Ricardo
 *
 */
public class CleanerWorkerThread implements Callable<String>{
	private PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
	private Cleaner cleaner = new Cleaner();
	private Stemmer stemmer = new Stemmer();
	private DocumentDTO document;

	private static Logger logger = Logger.getLogger(CleanerWorkerThread.class);
	
	
	public CleanerWorkerThread(DocumentDTO document) {
		this.document = document;
	}

	public String call() throws Exception {
		logger.trace("Init CleanerWorkerThread Call Method");
		StringBuilder dataFile = new StringBuilder();
		dataFile.append(this.document.getLazyData());
		dataFile = new StringBuilder(cleaner.unescapeHtml(dataFile.toString(),document.getName(),false));
		dataFile = new StringBuilder(cleaner.toLowerData(dataFile.toString(),document.getName(),false));
		dataFile = new StringBuilder(cleaner.deleteLexiconStopWords(dataFile.toString(), document.getName(), false));
		dataFile = new StringBuilder(cleaner.applyRegexExpression(dataFile.toString(),document.getName(),false));
		String stemmerOn = PersistenceFacade.getInstance().getProperty(EProperty.TEXT_STEMMER_ON).getValue();
		if(stemmerOn.equals("true")){
		   dataFile = new StringBuilder(stemmer.stem(dataFile.toString(), document.getName(), false));
		}
		
		persistenceFacade.writeFile(EDataFolder.CLEAN, document.getName(), dataFile.toString());
		this.document.setCleanDate(new Date());
		persistenceFacade.updateDocument(this.document);
	    logger.info("Write Clean File: " + this.document.getName());
		return "Hecho";
		
	}

}