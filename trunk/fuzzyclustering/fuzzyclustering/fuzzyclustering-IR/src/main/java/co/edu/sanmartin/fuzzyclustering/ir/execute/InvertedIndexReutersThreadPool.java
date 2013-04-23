package co.edu.sanmartin.fuzzyclustering.ir.execute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.execute.worker.InvertedIndexWorkerThread;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndexBuilder;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.facade.SendMessageAsynch;

/**
 * Thead pool con fin de crear un pool de 10 hilos para la descarga de los documentos
 * @author Ricardo
 *
 */
public class InvertedIndexReutersThreadPool implements Runnable{
	
	//Cantidad minima que debe aparecer un termino para almacenarlo en el indice.
	private int minTermsOcurrences = 0;
	public InvertedIndexReutersThreadPool(int minTermsOcurrences){
		this.minTermsOcurrences = minTermsOcurrences;
	}
	Logger logger = Logger.getLogger("InvertedIndexThreadPool");

	public void run() {
		logger.info("Inicializando construccion del indice invertido");
		SendMessageAsynch.sendMessage("Creando Indice Invertido");
		long time_start, time_end;
		time_start = System.currentTimeMillis();
		int threadPoolNumber = 10;
		//Se cargan las fuentes rss disponibles
		try {
			PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
			PropertyDTO threadPoolNumberProperty = 
					persistenceFacade.getProperty(EProperty.INVERTED_INDEX_THREAD_NUMBER);
			threadPoolNumber = threadPoolNumberProperty.intValue();
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String fileReuters = PersistenceFacade.getInstance().readFile(EDataFolder.TRAIN, "r8-train-stemmed.txt");
		String[] row = fileReuters.split(System.getProperty("line.separator"));
		
		ArrayList<DocumentDTO> fileCol = new ArrayList<DocumentDTO>();
		for (int i = 0; i < row.length; i++) {
			DocumentDTO document = new DocumentDTO();
			document.setLazyCleanData(row[i].split("\t")[1]);
			fileCol.add(document);
		}
		
		InvertedIndexWorkerThread invertedIndexWorkerThread;
		
		ThreadPoolExecutor executor=(ThreadPoolExecutor)Executors.newFixedThreadPool(threadPoolNumber);
		HashMap<String, StringBuilder> dataMap = new HashMap<String,StringBuilder>();
		InvertedIndexBuilder index = new InvertedIndexBuilder(new ConcurrentLinkedDeque<String>());
		//Almacenamos los archivos en memoria
		for (int i = 0; i < fileCol.size(); i++) {
			DocumentDTO document = fileCol.get(i);
			StringBuilder data = new StringBuilder(document.getLazyCleanData());
			invertedIndexWorkerThread = new InvertedIndexWorkerThread(data, String.valueOf(i+1), index);
			executor.execute(invertedIndexWorkerThread);
		}
		
		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Guarda el indice invertido
		index.saveIndex(this.minTermsOcurrences);
		time_end = System.currentTimeMillis();
		
		String finalMessage = "La construccion del indice invertido tomo "+ 
				( time_end - time_start )/1000 +" segundos" + 
				(( time_end - time_start )/1000)/60 +" minutos";
		logger.info(finalMessage);
		
		SendMessageAsynch.sendMessage(finalMessage);
		
	}
}
