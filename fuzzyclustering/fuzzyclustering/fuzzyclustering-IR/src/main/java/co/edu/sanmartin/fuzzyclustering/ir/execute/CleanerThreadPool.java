package co.edu.sanmartin.fuzzyclustering.ir.execute;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import co.edu.sanmartin.fuzzyclustering.ir.execute.worker.CleanerWorkerThread;
import co.edu.sanmartin.fuzzyclustering.ir.execute.worker.InvertedIndexWorkerThread;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndexBuilder;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Thead pool con fin de crear un pool de 10 hilos para la descarga de los documentos
 * @author Ricardo
 *
 */
public class CleanerThreadPool {

	
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

		Collection<DocumentDTO> fileCol = PersistenceFacade.getInstance().getFileList(EDataFolder.DOWNLOAD_RSS);
		
		CleanerWorkerThread cleanerWorkerThread;
		//Se realiza la limipieza de los archivos
		ThreadPoolExecutor executor=(ThreadPoolExecutor)Executors.newFixedThreadPool(threadPoolNumber);
		
		
		//Cargamos la informacion de los archivos en memoria
		for (DocumentDTO file : fileCol) {
			PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
			file.setLazyData(persistenceFacade.readFile(file.getCompletePath()));
		}
		
		for (DocumentDTO documentDTO : fileCol) {
			cleanerWorkerThread = new CleanerWorkerThread(documentDTO);
			executor.submit(cleanerWorkerThread);
		}
		
		executor.shutdown();
		//invertedIndex.getInstance().printIndex();
	}
}
