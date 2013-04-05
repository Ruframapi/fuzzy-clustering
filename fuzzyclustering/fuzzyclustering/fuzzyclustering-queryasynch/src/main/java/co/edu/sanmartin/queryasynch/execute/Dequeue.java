package co.edu.sanmartin.queryasynch.execute;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.constant.ESourceType;
import co.edu.sanmartin.persistence.constant.ESystemProperty;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
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
	/**
	 * Constructor de la clase
	 * @param restart define si reinicia el proceso o continua con un proceso anterior
	 */
	public Dequeue(){
		persistenceFacade = PersistenceFacade.getInstance();
		try {
			this.cancelActiveQueue(EQueueEvent.QUERY_DOCUMENT);
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
				Thread.sleep(1000);
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
				this.persistenceFacade.getQueueByStatusDate(EModule.QUERYASYNCH, 
						EQueueStatus.ENQUEUE, new Date());
		for (QueueDTO queueDTO : queueCol) {
			switch( queueDTO.getEvent() ){
			case QUERY_DOCUMENT:
				this.queryDocumentAsynch(queueDTO);
				break;
			case GENERATE_INVERTED_INDEX:
				this.generateInvertedIndex(queueDTO);
				break;
			}
		}
		
	}
	
	private void generateInvertedIndex(QueueDTO queueDTO) {
		// TODO Auto-generated method stub
		IRFacade irFacade = IRFacade.getInstance();
		irFacade.createInvertedIndex();	
		try {
			PersistenceFacade.getInstance().deleteQueue(queueDTO);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Realiza la consulta de documentos de forma asincronica
	 * @param queue
	 */
	private void queryDocumentAsynch(QueueDTO queue){
		try {
			PersistenceFacade persistence = PersistenceFacade.getInstance();
			//persistence.truncateQueryDocument();
			if(queue.getParams()!=null){
				int idDocument = Integer.parseInt(queue.getParams());
				
				DocumentDTO document = persistence.selectDocumentById(idDocument);
				logger.info("QueryDocumentAsynch: id:" + idDocument + " Name:" + document.getName());
				String lazyData = persistence.readFile(EDataFolder.DOWNLOAD, document.getName());
				String lazyCleanData = persistence.readFile(EDataFolder.CLEAN, document.getName());
				
				if(lazyData.length()>1000) lazyData=lazyData.substring(0, 1000);
				if(lazyCleanData.length()>1000) lazyCleanData=lazyCleanData.substring(0, 1000);
				document.setLazyData(lazyData);
				document.setLazyCleanData(lazyCleanData);
				logger.debug("Document query"+document.getId());
				if(document!=null){
					logger.debug("Calling Remote Servlet");
					CallRemoteServlet callRemoteServlet = new CallRemoteServlet();
					try {
						callRemoteServlet.sendDocument(document);
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						logger.error(e);
					}
				}
			}
			persistence.deleteQueue(queue);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error in queryDocumentAsynch" + e);
		}
	}
	
	/**
	 * Cancela las colas pendientes para reiniciar la aplicacion con una nueva orden de descarga
	 */
	private void cancelActiveQueue(EQueueEvent queueEvent) throws SQLException{
		Collection<QueueDTO> queueActive = 
				this.persistenceFacade.getQueueByStatus(queueEvent, EQueueStatus.ENQUEUE);
		for (QueueDTO queueDTO2 : queueActive) {
			persistenceFacade.deleteQueue(queueDTO2);
		}
	}
}

