package co.edu.sanmartin.fuzzyclustering.ir.execute.worker;

import java.util.concurrent.Callable;

import co.edu.sanmartin.fuzzyclustering.ir.normalize.Cleaner;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.Stemmer;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase encargada de realizar el proceso de normalización de los archivos
 * @author Ricardo
 *
 */
public class CleanerWorkerThread implements Callable<String>{

	private Cleaner cleaner = new Cleaner();
	private Stemmer stemmer = new Stemmer();
	private StringBuilder dataFile;
	private String fileName;
	
	public CleanerWorkerThread(StringBuilder dataFile, String fileName) {
		this.dataFile = dataFile;
		this.fileName = fileName+".txt";
	}
	public void run() {
		
	}
	public String call() throws Exception {
		// TODO Auto-generated method stub
		dataFile = new StringBuilder(cleaner.unescapeHtml(dataFile.toString(),fileName,false));
		dataFile = new StringBuilder(cleaner.toLowerData(dataFile.toString(),fileName,false));
		dataFile = new StringBuilder(cleaner.applyRegexExpression(dataFile.toString(),fileName,false));
		String stemmerOn = PersistenceFacade.getInstance().getProperty(EProperty.TEXT_STEMMER_ON).getValue();
		if(stemmerOn.equals("true")){
		   dataFile = new StringBuilder(stemmer.stem(dataFile.toString(), fileName, false));
		}

	    PersistenceFacade.getInstance().writeFile(EDataFolder.CLEAN, fileName, dataFile.toString());
		return "Hecho";
		
	}

}
