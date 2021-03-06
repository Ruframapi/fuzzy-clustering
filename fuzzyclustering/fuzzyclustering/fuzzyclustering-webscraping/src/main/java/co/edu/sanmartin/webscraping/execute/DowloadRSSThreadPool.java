package co.edu.sanmartin.webscraping.execute;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.constant.ESourceType;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.facade.SendMessageAsynch;
import co.edu.sanmartin.webscraping.execute.worker.RssDownloadWorkerThread;
import co.edu.sanmartin.webscraping.execute.worker.TwitterDownloadWorkerThread;

/**
 * Thead pool con fin de crear un pool de 10 hilos para la descarga de los documentos
 * @author Ricardo
 *
 */
public class DowloadRSSThreadPool {
	

	private static Logger logger = Logger.getRootLogger();
	private AtomicInteger rssSequence;
	private QueueDTO queue;
	private WorkspaceDTO workspace;
	
	public DowloadRSSThreadPool(WorkspaceDTO workspace, QueueDTO queue, AtomicInteger sequence){
		this.workspace = workspace;
		this.queue = queue;
		this.rssSequence = sequence;
	}

	public void executeThreadPool(ESourceType sourceType){
		int threadPoolNumber = 10;
		Collection<SourceDTO> rssSourceCol = null;
		logger.info("Init DownloadTread RSS Execute Method for" + sourceType);
		//Se cargan las fuentes rss disponibles
		try {
			rssSourceCol = this.workspace.getPersistence().getAllSources();
			PropertyDTO threadPoolNumberProperty = 
					this.workspace.getPersistence().getProperty(EProperty.WEB_SCRAPPING_THREAD_NUMBER);
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
		RssDownloadWorkerThread rssWorkerThread;
		logger.info("Sources Found:" +rssSourceCol.size());
		for (SourceDTO sourceDTO : rssSourceCol) {
			logger.info("Init download:" + sourceDTO.getUrl());
			if(sourceDTO.getType().equals(ESourceType.RSS)){
				rssWorkerThread = new RssDownloadWorkerThread(this.workspace,sourceDTO, rssSequence);
				executor.submit(rssWorkerThread);
			}
		}
		this.queue.setStatus(EQueueStatus.SUCESS);
		executor.shutdown();
		SendMessageAsynch.sendMessage(this.workspace,"Nuevas Noticias Rss Descargadas. Total" + rssSequence.get());
	}
}
