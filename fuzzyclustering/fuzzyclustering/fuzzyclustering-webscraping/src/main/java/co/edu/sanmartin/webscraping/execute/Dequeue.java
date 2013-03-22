package co.edu.sanmartin.webscraping.execute;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.constant.ESourceType;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase que arranca el sistema de descarga de Noticias
 * @author Ricardo Carvajal Salamanca
 *
 */


public class Dequeue implements Runnable{

	private static Logger logger = Logger.getLogger(Dequeue.class);
	PersistenceFacade persistenceFacade;
	AtomicInteger sequence = new AtomicInteger();
	//static Logger logger = Logger.getRootLogger();
	/**
	 * Constructor de la clase
	 * @param restart define si reinicia el proceso o continua con un proceso anterior
	 */
	public Dequeue(){
		persistenceFacade = PersistenceFacade.getInstance();
	}
	
	public void initProcess(){
		
	}
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			//logger.debug("Execute Queue");
			try {
				this.executeQueue();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (PropertyValueNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	/**
	 * Verifica si existen eventos encolados y los ejecuta
	 * @throws SQLException 
	 * @throws PropertyValueNotFoundException 
	 */
	public void executeQueue() throws SQLException, PropertyValueNotFoundException{

		Collection<QueueDTO> queueCol = 
				this.persistenceFacade.getQueueByStatusDate(EModule.WEBSCRAPPING, 
						EQueueStatus.ENQUEUE, new Date());
		for (QueueDTO queueDTO : queueCol) {
			switch( queueDTO.getEvent() ){
			case DOWNLOAD_RSS:
				this.downloadRSS(queueDTO);
				break;
			case CANCEL_DOWNLOAD:
				this.cancelDownload(queueDTO, EQueueEvent.DOWNLOAD_RSS);
				this.cancelDownload(queueDTO, EQueueEvent.DOWNLOAD_TWITTER);
				break;
			case CLEAN_DOWNLOAD:
				this.cleanDownload();
				break;
			}
			persistenceFacade.updateQueue(queueDTO);
		}
		
	}

	/**
	 * Cancela las colas pendientes para un reinicio de la applicacion
	 * @param queueDTO
	 * @param queueEvent
	 * @throws SQLException
	 */
	private void cancelDownload(QueueDTO queueDTO, EQueueEvent queueEvent) throws SQLException{

		Collection<QueueDTO> queueActive = 
				this.persistenceFacade.getQueueByStatus(queueEvent, EQueueStatus.ENQUEUE);
		for (QueueDTO queueDTO2 : queueActive) {
			queueDTO2.setStatus(EQueueStatus.CANCELLED);
			persistenceFacade.updateQueue(queueDTO2);
		}
		queueDTO.setStatus(EQueueStatus.SUCESS);
		
	}
	
	/**
	 * Limpia la carpeta de datos para realizar una nueva descarga en limpio
	 * @throws SQLException
	 */
	public void cleanDownload() throws SQLException{
		persistenceFacade.truncateQueue();
		persistenceFacade.deleteFolder(EDataFolder.DATA_ROOT);
		this.sequence = new AtomicInteger();
		Collection<SourceDTO> source = persistenceFacade.getAllSources(true);
		for (SourceDTO sourceDTO : source) {
			sourceDTO.setLastQuery(null);
			persistenceFacade.updateSource(sourceDTO);
		}
	}
	

	public void downloadRSS(QueueDTO queueDTO) throws PropertyValueNotFoundException, SQLException {
		DowloadRSSThreadPool threadPool = new DowloadRSSThreadPool(queueDTO, this.sequence);
		threadPool.executeThreadPool(ESourceType.RSS);
		persistenceFacade.updateQueue(queueDTO);
		this.addNewQueueEvent(EQueueEvent.DOWNLOAD_RSS);
	}
	
	
	public void downloadTwitter(QueueDTO queueDTO){
		
	}
	
	/**
	 * Adiciona un nuevo registro de consulta de rss
	 * @param queueEvent
	 * @throws PropertyValueNotFoundException 
	 * @throws SQLException 
	 * @throws Exception
	 */
	public void addNewQueueEvent(EQueueEvent queueEvent) throws PropertyValueNotFoundException, SQLException{
		QueueDTO queueDTO = new QueueDTO();
		queueDTO.setModule(EModule.WEBSCRAPPING);
		queueDTO.setProcessDate(null);
		PropertyDTO rssTimeDelay = 
				PersistenceFacade.getInstance().getProperty(EProperty.WEB_SCRAPPING_RSS_DOWNLOAD_TIME);
		Calendar cal = Calendar.getInstance();
		
		cal.add(Calendar.SECOND, rssTimeDelay.intValue());
		queueDTO.setInitDate(cal.getTime());
		queueDTO.setEvent(queueEvent);
		queueDTO.setStatus(EQueueStatus.ENQUEUE);
		persistenceFacade.insertQueue(queueDTO);
	}
}

