package co.edu.sanmartin.fuzzyclustering.ir.execute.worker;

import co.edu.sanmartin.fuzzyclustering.ir.normalize.Cleaner;

/**
 * Clase encargada de realizar el proceso de normalización de los archivos
 * @author Ricardo
 *
 */
public class CleanerWorkerThread implements Runnable{

	private Cleaner cleaner = new Cleaner();
	private StringBuilder dataFile;
	private String fileName;
	
	public CleanerWorkerThread(StringBuilder dataFile, String fileName) {
		this.dataFile = dataFile;
		this.fileName = fileName+".txt";
	}
	public void run() {
		// TODO Auto-generated method stub
		String unescapeData = cleaner.unescapeHtml(dataFile.toString(),fileName,true);
		dataFile = new StringBuilder(unescapeData);
		
	}

}
