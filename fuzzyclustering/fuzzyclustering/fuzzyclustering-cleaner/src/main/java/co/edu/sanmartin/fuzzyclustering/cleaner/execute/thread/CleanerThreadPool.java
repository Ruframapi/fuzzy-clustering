package co.edu.sanmartin.fuzzyclustering.cleaner.execute.thread;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;

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
	private WorkspaceDTO workspace;
	
	
	public CleanerThreadPool(WorkspaceDTO workspace){	
		try {
			this.workspace = workspace;
			PropertyDTO threadPoolNumberProperty = 
					this.workspace.getPersistence().getProperty(EProperty.CLEAN_THREAD_NUMBER);
			threadPoolNumber = threadPoolNumberProperty.intValue();
			executor=(ThreadPoolExecutor)Executors.newFixedThreadPool(threadPoolNumber);
			
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}
	
	/**
	 * Ejecuta el pool de hilos para realizar la limpieza de los archivos
	 */
	public void executeThreadPool(){
		executor=(ThreadPoolExecutor)Executors.newFixedThreadPool(threadPoolNumber);
		Collection<DocumentDTO> fileCol = this.workspace.getPersistence().getDocumentsForClean();
		logger.info("Files To Clean:" + fileCol.size());
		CleanerWorkerThread cleanerWorkerThread;
		//Se realiza la limipieza de los archivos
		//Cargamos la informacion de los archivos en memoria
		for (DocumentDTO file : fileCol) {
			try{
				file.setLazyData(this.workspace.getPersistence().readFile(EDataFolder.DOWNLOAD, file.getName()));
			}
			catch(Exception e){
				logger.error("Error in executeThreadPool read File", e);
			}
		}
		
		for (DocumentDTO documentDTO : fileCol) {
			logger.info("Cleaning File:" + documentDTO.getName());
			try{
				cleanerWorkerThread = new CleanerWorkerThread(this.workspace, documentDTO);
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
