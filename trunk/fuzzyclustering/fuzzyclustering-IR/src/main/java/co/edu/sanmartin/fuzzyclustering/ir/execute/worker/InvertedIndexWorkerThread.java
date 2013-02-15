package co.edu.sanmartin.fuzzyclustering.ir.execute.worker;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Worker encargado de la descarga de los documentos de internet
 * Cada fuente es un worker diferente
 * @author Ricardo Carvajal Salamanca
 *
 */
public class InvertedIndexWorkerThread implements Runnable{

	private static Logger logger = Logger.getRootLogger();
	private String dataFile;
	private String fileName;
	
	public InvertedIndexWorkerThread(StringBuilder dataFile, String fileName) {
		this.dataFile = dataFile.toString();
		this.fileName = fileName;
	}

	public void run() {
		logger.info("Init run Inverted Index Worker Thread");
		String[] termList = dataFile.split(",");
		logger.info("Term size:"+termList.length);
		for (int i = 0; i < termList.length; i++) {
			System.out.println(termList[i]);
			InvertedIndex.getInstance().addIndex(termList[i],fileName);
		}
		
	}

}
