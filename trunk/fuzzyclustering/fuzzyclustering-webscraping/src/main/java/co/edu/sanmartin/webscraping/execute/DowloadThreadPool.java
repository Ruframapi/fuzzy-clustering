package co.edu.sanmartin.webscraping.execute;

import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.webscraping.execute.worker.RssDownloadWorkerThread;

/**
 * Thead pool con fin de crear un pool de 10 hilos para la descarga de los documentos
 * @author Ricardo
 *
 */
public class DowloadThreadPool {

	private static Logger logger = Logger.getRootLogger();

	public void executeThreadPool(){
		
		int threadPoolNumber = 10;
		Collection<SourceDTO> rssSourceCol = null;
		logger.info("Init DownloadTread Execute Method");
		//Se cargan las fuentes rss disponibles
		try {
			PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
			rssSourceCol = persistenceFacade.getAllSources();
			PropertyDTO threadPoolNumberProperty = 
					persistenceFacade.getProperty(EProperty.WEB_SCRAPPING_THREAD_NUMBER);
			threadPoolNumber = threadPoolNumberProperty.intValue();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ExecutorService executorService = Executors.newFixedThreadPool(threadPoolNumber);
		RssDownloadWorkerThread rssWorkerThread;
		logger.info("Sources Found:" +rssSourceCol.size());
		for (SourceDTO sourceDTO : rssSourceCol) {
			rssWorkerThread = new RssDownloadWorkerThread(sourceDTO);
			executorService.execute(rssWorkerThread);
		}
		executorService.shutdown();
	}
}
