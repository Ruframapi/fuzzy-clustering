package co.edu.sanmartin.fuzzyclustering.ir.execute;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
public class InvertedIndexThreadPool {

	public void executeThreadPool(){
		
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

		Collection<DocumentDTO> fileCol = PersistenceFacade.getInstance().getFileList(EDataFolder.CLEAN);
		
		InvertedIndexWorkerThread invertedIndexWorkerThread;
		
		//Se realiza la limipieza de los archivos
		ThreadPoolExecutor executor=(ThreadPoolExecutor)Executors.newFixedThreadPool(threadPoolNumber);
		HashMap<String, StringBuilder> dataMap = new HashMap<String,StringBuilder>();
		
		//Almacenamos los archivos en memoria
		for (DocumentDTO file : fileCol) {
			PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
			StringBuilder dataFile = new StringBuilder();
			dataFile.append(persistenceFacade.readFile(EDataFolder.CLEAN,file.getName()));
			dataMap.put(file.getNameWithoutExtension(), dataFile);
		}

		Iterator<Entry<String, StringBuilder>> it = dataMap.entrySet().iterator();
		InvertedIndexBuilder index = new InvertedIndexBuilder(new ConcurrentLinkedDeque<String>());
		while (it.hasNext()) {
			Map.Entry<String,StringBuilder> e = (Entry<String, StringBuilder>)it.next();
			invertedIndexWorkerThread = new InvertedIndexWorkerThread(e.getValue(), e.getKey(), index);
			executor.execute(invertedIndexWorkerThread);
		}
		
		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		index.saveIndex();
		
		//invertedIndex.getInstance().printIndex();
	}
}
