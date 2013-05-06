package co.edu.sanmartin.webscraping.execute;

import java.net.URISyntaxException;
import java.sql.SQLException;
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
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.facade.QueueFacade;
import co.edu.sanmartin.persistence.facade.SendMessageAsynch;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;

/**
 * Clase que arranca el sistema de descarga de Noticias
 * @author Ricardo Carvajal Salamanca
 *
 */


public class Dequeue implements Runnable{

	private static Logger logger = Logger.getLogger(Dequeue.class);
	
	//static Logger logger = Logger.getRootLogger();
	/**
	 * Constructor de la clase
	 * @param restart define si reinicia el proceso o continua con un proceso anterior
	 */
	public Dequeue(){
		
		try {
			this.cancelActiveQueue(EQueueEvent.DOWNLOAD_RSS);
			this.cancelActiveQueue(EQueueEvent.DOWNLOAD_TWITTER);
			this.cancelActiveQueue(EQueueEvent.DOWNLOAD_TRAIN);
			this.cancelActiveQueue(EQueueEvent.CLEAN_DOWNLOAD);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
		
		
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
				QueueFacade.getInstance().getQueueByStatusDate(EModule.WEBSCRAPPING, 
						EQueueStatus.ENQUEUE, new Date());
		for (QueueDTO queueDTO : queueCol) {
			WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
			int initDocumentId = workspace.getPersistence().getDownloadDocumentAmount()+1;
			logger.info("Init Queue documentId:"+initDocumentId);
			AtomicInteger sequence = new AtomicInteger();
			sequence.set(initDocumentId);
			switch( queueDTO.getEvent() ){
			case DOWNLOAD_RSS:
				this.downloadRSS(queueDTO, sequence);
				break;
			case DOWNLOAD_TWITTER:
				this.downloadTwitter(queueDTO, sequence);
				break;
			case DOWNLOAD_TRAIN:
				this.downloadTrain(queueDTO);
				break;
			case CANCEL_DOWNLOAD:
				this.cancelDownload(queueDTO, EQueueEvent.DOWNLOAD_RSS);
				this.cancelDownload(queueDTO, EQueueEvent.DOWNLOAD_TWITTER);
				break;
			case CLEAN_DOWNLOAD:
				this.cleanDownload(workspace);
				break;
			case RELOAD_DATA_MEMORY:
				this.reloadDataMemory(queueDTO);
				break;
			}
			
			try {
				QueueFacade.getInstance().deleteQueue(queueDTO);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Realiza la simulacion de noticias utilizando el entrenamiento Reuters RX(8 o 32)
	 * @param queueDTO
	 * @throws SQLException 
	 * @throws PropertyValueNotFoundException 
	 */
	private void downloadTrain(QueueDTO queueDTO) throws SQLException, PropertyValueNotFoundException {
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
		queueDTO.setStatus(EQueueStatus.ACTIVE);
		QueueFacade.getInstance().updateQueue(queueDTO);
		DownloadReuters downloadReuters = new DownloadReuters(workspace);
		downloadReuters.simulateDownload();
		QueueFacade.getInstance().deleteQueue(queueDTO);
	}

	/**
	 * Cancela las colas pendientes para un reinicio de la applicacion
	 * @param queueDTO
	 * @param queueEvent
	 * @throws SQLException
	 */
	private void cancelDownload(QueueDTO queueDTO, EQueueEvent queueEvent) throws SQLException{
		this.cancelActiveQueue(queueEvent);
		queueDTO.setStatus(EQueueStatus.SUCESS);
	}
	
	/**
	 * Cancela las colas pendientes para reiniciar la aplicacion con una nueva orden de descarga
	 */
	private void cancelActiveQueue(EQueueEvent queueEvent) throws SQLException{
		Collection<QueueDTO> queueActive = 
				QueueFacade.getInstance().getQueueByStatus(queueEvent, EQueueStatus.ENQUEUE);
		for (QueueDTO queueDTO2 : queueActive) {
			logger.info("Queue Cancelled id:" +queueDTO2.getId());
			try {
				QueueFacade.getInstance().deleteQueue(queueDTO2);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Limpia la carpeta de datos para realizar una nueva descarga en limpio
	 * @throws SQLException
	 */
	public void cleanDownload(WorkspaceDTO workspace) throws SQLException{
		logger.info("Cleaning Download Process");
		QueueFacade.getInstance().truncateQueue();
		workspace.getPersistence().truncateDocument();
		workspace.getPersistence().deleteFolder(EDataFolder.DATA_ROOT);
		//this.sequence = new AtomicInteger();
		Collection<SourceDTO> source = workspace.getPersistence().getAllSources(true);
		for (SourceDTO sourceDTO : source) {
			sourceDTO.setLastQuery(null);
			workspace.getPersistence().updateSource(sourceDTO);
		}
		
	}
	

	/**
	 * Desencola el proceso de descarga de fuentes RSS
	 * @param queueDTO
	 * @throws PropertyValueNotFoundException
	 * @throws SQLException
	 */
	public void downloadRSS(QueueDTO queueDTO, AtomicInteger sequence) throws PropertyValueNotFoundException, SQLException {
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
		queueDTO.setStatus(EQueueStatus.ACTIVE);
		QueueFacade.getInstance().updateQueue(queueDTO);
		DowloadRSSThreadPool threadPool = new DowloadRSSThreadPool(workspace,queueDTO, sequence);
		threadPool.executeThreadPool(ESourceType.RSS);
		QueueFacade.getInstance().updateQueue(queueDTO);
		this.addNewQueueEvent(workspace, EQueueEvent.DOWNLOAD_RSS);
	}
	
	/**
	 * Desencola el proceso de descarga de fuentes de Twitter
	 * @param queueDTO
	 */
	public void downloadTwitter(QueueDTO queueDTO, AtomicInteger sequence) throws PropertyValueNotFoundException, SQLException{
		queueDTO.setStatus(EQueueStatus.ACTIVE);
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
		QueueFacade.getInstance().updateQueue(queueDTO);
		
		DowloadTwitterThreadPool threadPool = new DowloadTwitterThreadPool(workspace,queueDTO, sequence);
		threadPool.executeThreadPool(ESourceType.TWITTER);
		QueueFacade.getInstance().updateQueue(queueDTO);
		this.addNewQueueEvent(workspace,EQueueEvent.DOWNLOAD_TWITTER);
	}
	
	/**
	 * Recarga los propiedades del sistema en memoria
	 * @param queueDTO
	 * @throws PropertyValueNotFoundException
	 * @throws SQLException
	 */
	public void reloadDataMemory(QueueDTO queueDTO) throws PropertyValueNotFoundException, SQLException{
		queueDTO.setStatus(EQueueStatus.ACTIVE);
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
		workspace.getPersistence().refreshMemoryData();
		QueueFacade.getInstance().updateQueue(queueDTO);
		logger.info("Memory Data Reloaded");
	}
	
	/**
	 * Adiciona un nuevo registro de consulta de RSS
	 * @param queueEvent
	 * @throws PropertyValueNotFoundException 
	 * @throws SQLException 
	 * @throws Exception
	 */
	public void addNewQueueEvent(WorkspaceDTO workspace, EQueueEvent queueEvent) throws PropertyValueNotFoundException, SQLException{
		QueueDTO queueDTO = new QueueDTO();
		queueDTO.setModule(EModule.WEBSCRAPPING);
		queueDTO.setProcessDate(null);
		queueDTO.setInitDate(getNextQueueDate(queueEvent, workspace));
		queueDTO.setEvent(queueEvent);
		queueDTO.setStatus(EQueueStatus.ENQUEUE);
		queueDTO.setWorkspace(workspace.getName());
		QueueFacade.getInstance().insertQueue(queueDTO);
	}
	
	/**
	 * Retorna la fecha de la ejecucion de la siguiente cola del evento
	 * @param queueEvent
	 * @return
	 * @throws PropertyValueNotFoundException 
	 */
	public Date getNextQueueDate(EQueueEvent queueEvent, WorkspaceDTO workspace) throws PropertyValueNotFoundException {
		EProperty property = null;
		
		switch (queueEvent){
			case DOWNLOAD_RSS:
				property = EProperty.WEB_SCRAPPING_RSS_DOWNLOAD_TIME;
				break;
			case DOWNLOAD_TWITTER:
				property = EProperty.WEB_SCRAPPING_TWITTER_DOWNLOAD_TIME;
				break;
		}
		PropertyDTO rssTimeDelay = 
				workspace.getPersistence().getProperty(property);
		Calendar cal = Calendar.getInstance();
		
		cal.add(Calendar.SECOND, rssTimeDelay.intValue());
		return cal.getTime();
	}
	
	
}

