package co.edu.sanmartin.fuzzyclustering.ir.execute.worker;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndexBuilder;
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
	private InvertedIndexBuilder index;
	
	public InvertedIndexWorkerThread(StringBuilder dataFile, String fileName, InvertedIndexBuilder index) {
		this.dataFile = dataFile.toString();
		this.fileName = fileName;
		this.index = index;
	}

	public void run() {
		logger.debug("Init run Inverted Index Worker Thread DataFile:" + fileName);
		String[] termList = dataFile.split(" ");
		logger.debug("Term size:"+termList.length);
		for (int i = 0; i < termList.length; i++) {
			//Si el termino es vacio o si no son caracteres o letras no se tiene en cuenta
			if(termList[i].length()==0){
				continue;
			}
			if(!Character.isLetterOrDigit(termList[i].charAt(0))){
				continue;
			}
			index.addIndex(termList[i],fileName);
		}
		
	}

}
