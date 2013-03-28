package co.edu.sanmartin.fuzzyclustering.ir.execute.worker;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.normalize.Cleaner;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.Stemmer;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase encargada de realizar el proceso de normalización de los archivos
 * @author Ricardo
 *
 */
public class CleanerWorkerThread implements Callable<String>{

	private Cleaner cleaner = new Cleaner();
	private Stemmer stemmer = new Stemmer();
	private DocumentDTO document;

	private static Logger logger = Logger.getLogger(CleanerWorkerThread.class);
	
	
	public CleanerWorkerThread(DocumentDTO document) {
		this.document = document;
	}

	public String call() throws Exception {
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
		
	    PersistenceFacade.getInstance().writeFile(EDataFolder.CLEAN, document.getName(), dataFile.toString());
	    //Mueve los archivos procesdos de carpeta
	    PersistenceFacade.getInstance().writeFile(EDataFolder.ORIGINAL_RSS, document.getName(), this.document.getLazyData());
	    PersistenceFacade.getInstance().deleteFile(EDataFolder.DOWNLOAD_RSS, this.document.getName());
	    
	    logger.info("Write Clean File: " + this.document.getName());
		return "Hecho";
		
	}

}
