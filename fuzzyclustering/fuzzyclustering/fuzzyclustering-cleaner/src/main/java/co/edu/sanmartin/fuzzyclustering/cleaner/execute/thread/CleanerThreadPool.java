package co.edu.sanmartin.fuzzyclustering.cleaner.execute.thread;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Thead pool con fin de crear un pool de 10 hilos para la descarga de los documentos
 * @author Ricardo Carvajal Salamanca
 *
 */
public class CleanerThreadPool {
	
	private static Logger logger = Logger.getLogger("CleanerThreadPool");
	private static CleanerThreadPool instance;
	private ThreadPoolExecutor executor;
	private int threadPoolNumber = 10;
	
	private CleanerThreadPool(){	
		try {
			PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
			PropertyDTO threadPoolNumberProperty = 
					persistenceFacade.getProperty(EProperty.CLEAN_THREAD_NUMBER);
			threadPoolNumber = threadPoolNumberProperty.intValue();
			executor=(ThreadPoolExecutor)Executors.newFixedThreadPool(threadPoolNumber);
			
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}
	
	public static CleanerThreadPool getInstance() {
		if(instance == null){
			instance = new CleanerThreadPool();
		}
		return instance;
	}

	public void executeThreadPool(){
		executor=(ThreadPoolExecutor)Executors.newFixedThreadPool(threadPoolNumber);
		Collection<DocumentDTO> fileCol = PersistenceFacade.getInstance().getDocumentsForClean();
		logger.info("Files To Clean:" + fileCol.size());
		CleanerWorkerThread cleanerWorkerThread;
		//Se realiza la limipieza de los archivos
		//Cargamos la informacion de los archivos en memoria
		for (DocumentDTO file : fileCol) {
			PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
			file.setLazyData(persistenceFacade.readFile(EDataFolder.DOWNLOAD, file.getName()));
		}
		
		for (DocumentDTO documentDTO : fileCol) {
			logger.info("Cleaning File:" + documentDTO.getName());
			try{
				cleanerWorkerThread = new CleanerWorkerThread(documentDTO);
				executor.submit(cleanerWorkerThread);
			}
			catch(Throwable e){
				logger.error("Error Cleaning File"+ e.getMessage());
			}
		}
		
		executor.shutdown();
	}

	public ThreadPoolExecutor getExecutor() {
		return executor;
	}
	
	
	
	
}
