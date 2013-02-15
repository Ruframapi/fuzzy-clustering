package co.edu.sanmartin.fuzzyclustering.ir.execute;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.edu.sanmartin.fuzzyclustering.ir.execute.worker.CleanerWorkerThread;
import co.edu.sanmartin.fuzzyclustering.ir.execute.worker.InvertedIndexWorkerThread;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.SourceDTO;
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String originalFolderPath = null;
		try {
			originalFolderPath = PersistenceFacade.getInstance().getProperty(EProperty.FILE_ORIGINAL_SOURCE_PATH).getValue();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collection<String> fileCol = PersistenceFacade.getInstance().getFileList(originalFolderPath);
		ExecutorService executorService = Executors.newFixedThreadPool(threadPoolNumber);
		InvertedIndexWorkerThread invertedIndexWorkerThread;
		CleanerWorkerThread cleanerWorkerThread;
		//Se realiza la limipieza de los archivos
		
		HashMap<String, StringBuilder> dataMap = new HashMap<String,StringBuilder>();
		
		//Almacenamos los archivos en memoria
		for (String file : fileCol) {
			PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
			String fileName = persistenceFacade.getFileNameWithOutExtension(file);
			StringBuilder dataFile = new StringBuilder();
			dataFile.append(persistenceFacade.readFile(file));
			dataMap.put(fileName, dataFile);
		}
		
		Iterator<Entry<String, StringBuilder>> it = dataMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,StringBuilder> e = (Entry<String, StringBuilder>)it.next();
			cleanerWorkerThread = new CleanerWorkerThread(e.getValue(),e.getKey());
			executorService.execute(cleanerWorkerThread);
		}
		
		
		while (it.hasNext() && executorService.isTerminated()) {
			Map.Entry<String,StringBuilder> e = (Entry<String, StringBuilder>)it.next();
			invertedIndexWorkerThread = new InvertedIndexWorkerThread(e.getValue(), e.getKey());
			executorService.execute(invertedIndexWorkerThread);
		}
		
		executorService.shutdown();
		//invertedIndex.getInstance().printIndex();
	}
}
