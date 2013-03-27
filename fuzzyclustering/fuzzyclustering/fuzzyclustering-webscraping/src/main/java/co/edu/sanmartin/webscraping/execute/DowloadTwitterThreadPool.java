package co.edu.sanmartin.webscraping.execute;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import twitter4j.TwitterException;

import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.constant.ESourceType;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.webscraping.execute.worker.RssDownloadWorkerThread;
import co.edu.sanmartin.webscraping.execute.worker.TwitterDownloadWorkerThread;

/**
 * Thead pool con fin de crear un pool de 10 hilos para la descarga de los documentos
 * @author Ricardo
 *
 */
public class DowloadTwitterThreadPool {


	private static Logger logger = Logger.getRootLogger();
	private AtomicInteger sequence;
	private QueueDTO queue;

	public DowloadTwitterThreadPool(QueueDTO queue, AtomicInteger sequence){
		this.queue = queue;
		this.sequence = sequence;
	}

	public void executeThreadPool(ESourceType sourceType){
		int threadPoolNumber = 10;
		Collection<SourceDTO> twitterSourceColl = null;
		logger.info("Init DownloadTread Twitter Execute Method for" + sourceType);
		//Se cargan las fuentes rss disponibles
		try {
			PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
			twitterSourceColl = persistenceFacade.getAllSources();
			PropertyDTO threadPoolNumberProperty = 
					persistenceFacade.getProperty(EProperty.WEB_SCRAPPING_THREAD_NUMBER);
			threadPoolNumber = threadPoolNumberProperty.intValue();
			this.queue.setProcessDate(new Date());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.queue.setStatus(EQueueStatus.ERROR);
			logger.error(e);
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			this.queue.setStatus(EQueueStatus.ERROR);
			logger.error(e);
		}
		ThreadPoolExecutor executor=(ThreadPoolExecutor)Executors.newFixedThreadPool(threadPoolNumber);
		TwitterDownloadWorkerThread twitterWorkerThread;;
		logger.info("Sources Found:" +twitterSourceColl.size());
		try{
			for (SourceDTO sourceDTO : twitterSourceColl) {
				if(sourceDTO.getType().equals(ESourceType.TWITTER)){
					twitterWorkerThread = new TwitterDownloadWorkerThread(sourceDTO, sequence);
					executor.submit(twitterWorkerThread);
				}
			}
		}catch(TwitterException e){
			logger.error("Error in executeThreadPool: " + e.getErrorMessage() + " " + "StatusCode:" + e.getStatusCode());
		}
		this.queue.setStatus(EQueueStatus.SUCESS);
		executor.shutdown();
	}
}
