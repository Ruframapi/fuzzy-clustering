package co.edu.sanmartin.fuzzyclustering.ir.execute;

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

/**
 * Thead pool con fin de crear un pool de 10 hilos para la descarga de los documentos
 * @author Ricardo
 *
 */
public class InvertedIndexThreadPool implements Runnable{

	Logger logger = Logger.getLogger("InvertedIndexThreadPool");

	public void run() {
		logger.info("Inicializando construccion del indice invertido");
		int threadPoolNumber = 10;
		long time_start, time_end;
		time_start = System.currentTimeMillis();
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

		Collection<DocumentDTO> fileCol = PersistenceFacade.getInstance().getFileList(EDataFolder.CLEAN);
		
		InvertedIndexWorkerThread invertedIndexWorkerThread;
		
		ThreadPoolExecutor executor=(ThreadPoolExecutor)Executors.newFixedThreadPool(threadPoolNumber);
		HashMap<String, StringBuilder> dataMap = new HashMap<String,StringBuilder>();
		InvertedIndexBuilder index = new InvertedIndexBuilder(new ConcurrentLinkedDeque<String>());
		//Almacenamos los archivos en memoria
		for (DocumentDTO file : fileCol) {
			PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
			StringBuilder dataFile = new StringBuilder();
			dataFile.append(persistenceFacade.readFile(EDataFolder.CLEAN,file.getName()));
			invertedIndexWorkerThread = new InvertedIndexWorkerThread(dataFile, file.getNameWithoutExtension(), index);
			executor.execute(invertedIndexWorkerThread);
			//dataMap.put(file.getNameWithoutExtension(), dataFile);
		}

		/*Iterator<Entry<String, StringBuilder>> it = dataMap.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry<String,StringBuilder> e = (Entry<String, StringBuilder>)it.next();
			invertedIndexWorkerThread = new InvertedIndexWorkerThread(e.getValue(), e.getKey(), index);
			executor.execute(invertedIndexWorkerThread);
		}
		*/
		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		index.saveIndex();
		time_end = System.currentTimeMillis();
		
		logger.info("La construccion del indice invertido tomo "+ 
		( time_end - time_start )/1000 +" segundos" + 
		(( time_end - time_start )/1000)/60 +" minutos");
		
		//invertedIndex.getInstance().printIndex();
		
	}
}