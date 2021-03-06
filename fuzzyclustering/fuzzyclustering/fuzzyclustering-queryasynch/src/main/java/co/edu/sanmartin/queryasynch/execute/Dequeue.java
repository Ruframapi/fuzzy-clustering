package co.edu.sanmartin.queryasynch.execute;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.QueueDTO;
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
	/**
	 * Constructor de la clase
	 * @param restart define si reinicia el proceso o continua con un proceso anterior
	 */
	public Dequeue(){
		try {
			this.cancelActiveQueue(EQueueEvent.QUERY_DOCUMENT);
			this.cancelActiveQueue(EQueueEvent.GENERATE_INVERTED_INDEX);
			this.cancelActiveQueue(EQueueEvent.REDUCED_INVERTED_INDEX_ZIPF);
			this.cancelActiveQueue(EQueueEvent.GENERATE_TERM_TERM_MATRIX);
			this.cancelActiveQueue(EQueueEvent.GENERATE_PPMI_MATRIX);
			this.cancelActiveQueue(EQueueEvent.GENERATE_ALL_MATRIX);
			this.cancelActiveQueue(EQueueEvent.GENERATE_REDUCED_PPMI_MATRIX);
			this.cancelActiveQueue(EQueueEvent.SEND_MESSAGE);
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
		logger.trace("Init queryAsynch executeQueue");
		Collection<QueueDTO> queueCol = 
				QueueFacade.getInstance().getQueueByStatusDate(EModule.QUERYASYNCH, 
						EQueueStatus.ENQUEUE, new Date());
		for (QueueDTO queueDTO : queueCol) {
			try{
				switch( queueDTO.getEvent() ){
				case QUERY_DOCUMENT:
					this.queryDocumentAsynch(queueDTO);
					break;
				case GENERATE_INVERTED_INDEX:
					this.generateInvertedIndex(queueDTO);
					break;
				case REDUCED_INVERTED_INDEX_ZIPF:
					this.reducedInvertedIndexZipf(queueDTO);
					break;
				case GENERATE_TERM_TERM_MATRIX:
					this.generateTermTermMatrix(queueDTO);
					break;
				case GENERATE_PPMI_MATRIX:
					this.generatePPMIMatrix(queueDTO);
					break;
				case GENERATE_ALL_MATRIX:
					this.generateCmeansMatrix(queueDTO);
					break;
				case GENERATE_REDUCED_PPMI_MATRIX:
					this.generateReducedPPMIMatrix(queueDTO);
					break;
				case INVERTED_INDEX_ZIPF_IMPROVED:
					this.invertedIndexZipfImproved(queueDTO);
					break;
				case SEND_MESSAGE:
					this.sendMessage(queueDTO);
					break;
				}
			}catch(RuntimeException e){
				logger.error("Error en procesar la cola " + queueDTO.getEvent().toString(), e);
			}
		}


	}

	/**
	 * Realiza la reduccion de dimensionalidad de la Matrix PPMI
	 * @param queueDTO
	 */
	private void generateReducedPPMIMatrix(QueueDTO queueDTO) {
		logger.info("Init generateReducedPPMIMatrix");
		try {
			String[] params = queueDTO.getParams().split(",");
			WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
			int newDimension = Integer.parseInt(params[0]);
			Boolean saveReadable  = Boolean.parseBoolean(params[1]);
			int readableRowsAmount = Integer.parseInt(params[2]);
			IRFacade irFacade = IRFacade.getInstance(workspace);
			irFacade.reducedDimensionPPMIMatrix(newDimension, saveReadable, readableRowsAmount);
			
		} catch (Exception e) {
			logger.error("Error in generateReducedPPMIMatrix",e);
		}
		finally{
			try {
				QueueFacade.getInstance().deleteQueue(queueDTO);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void reducedInvertedIndexZipf(QueueDTO queueDTO){
		try {
			WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
			String[] params = queueDTO.getParams().split(",");
			int zipfCutOn = Integer.parseInt(params[0]);
			int zipfCutOff = Integer.parseInt(params[1]);
			int minTermOcurrences = Integer.parseInt(params[2]);
			IRFacade irFacade = IRFacade.getInstance(workspace);
			irFacade.reducedZipfInvertedIndex(zipfCutOn, zipfCutOff, minTermOcurrences);
			QueueFacade.getInstance().deleteQueue(queueDTO);
			
			logger.info("Reduced Inverted Index Zipf Finished");
			
		} catch (Exception e) {
			logger.error("Error in ZipfInvertedIndex",e);
		}
		finally{
			try {
				QueueFacade.getInstance().deleteQueue(queueDTO);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	private void invertedIndexZipfImproved(QueueDTO queueDTO){
		try {
			WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
			String[] params = queueDTO.getParams().split(",");
			int minTermOcurrences = Integer.parseInt(params[0]);
			IRFacade irFacade = IRFacade.getInstance(workspace);
			irFacade.invertedIndexZipf(minTermOcurrences);
			QueueFacade.getInstance().deleteQueue(queueDTO);
			logger.info("Reduced Inverted Index Zipf Finished");
			
		} catch (Exception e) {
			logger.error("Error in ZipfInvertedIndex",e);
		}
		finally{
			try {
				QueueFacade.getInstance().deleteQueue(queueDTO);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * Envia un mensaje al cliente
	 * @param queueDTO
	 */
	private void sendMessage(QueueDTO queueDTO) {
		// TODO Auto-generated method stub
		String[] params = queueDTO.getParams().split(",");

		try {
			WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
			CallRemoteServlet callRemoteServlet = new CallRemoteServlet(workspace);
			callRemoteServlet.sendMessage(queueDTO.getParams());
		} catch (Exception e) {
			logger.error("Error in generateInvertedIndex",e);
		}
		finally{
			try {
				QueueFacade.getInstance().deleteQueue(queueDTO);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Envia la solicitud de creaci�n del indice invertido
	 * @param queueDTO
	 */
	private void generateInvertedIndex(QueueDTO queueDTO) {
		// TODO Auto-generated method stub

		try {
			WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
			String[] params = queueDTO.getParams().split(",");
			int minTermsOcurrences = Integer.parseInt(params[0]);
			int documentsAmount = Integer.parseInt(params[1]);
			IRFacade irFacade = IRFacade.getInstance(workspace);
			irFacade.createInvertedIndex(minTermsOcurrences, documentsAmount);	
			QueueFacade.getInstance().deleteQueue(queueDTO);
		} catch (Exception e) {
			logger.error("Error in generateInvertedIndex",e);
		}
		finally{
			try {
				QueueFacade.getInstance().deleteQueue(queueDTO);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Envia la solicitud de creaci�n de la matrix termino termino
	 * @param queueDTO
	 */
	private void generateTermTermMatrix(QueueDTO queueDTO) {
		logger.trace("Init generateTermTermMatrix Method");
		
		try {
			WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
			SendMessageAsynch.sendMessage(workspace, "Iniciando construccion de Matrix Termino Termino");
			IRFacade irFacade = IRFacade.getInstance(workspace);
			irFacade.createTermTermBigMatrix(true);	
			SendMessageAsynch.sendMessage(workspace, "Construccion de Matrix Termino Termino Finalizada");
		} catch (Exception e) {
			logger.error("Error in generateTermTermMatrix",e);
		}
		finally{
			try {
				QueueFacade.getInstance().deleteQueue(queueDTO);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Envia la solicitud de creaci�n de la matrix PPMI
	 * @param queueDTO
	 */
	private void generatePPMIMatrix(QueueDTO queueDTO) {
		// TODO Auto-generated method stub

		try {
			
			WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
			IRFacade irFacade = IRFacade.getInstance(workspace);
			irFacade.createPPMIBigMatrix(true);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error in generatePPMIMatrix",e);
		}
		finally{
			try {
				QueueFacade.getInstance().deleteQueue(queueDTO);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Envia la solicitud de creaci�n de la matrix PPMI
	 * @param queueDTO
	 */
	private void generateCmeansMatrix(QueueDTO queueDTO) {
		try {
			WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
			if(queueDTO.getParams()!=null){
				String[] params = queueDTO.getParams().split(",");
				int minTermsOcurrences = Integer.parseInt(params[1]);
				int documentsAmount = Integer.parseInt(params[1]);
				IRFacade irFacade = IRFacade.getInstance(workspace);
				irFacade.buildCmeanMatrix(minTermsOcurrences,documentsAmount);
				QueueFacade.getInstance().deleteQueue(queueDTO);
			}
		} catch (Exception e) {
			logger.error("Error in generateCmeansMatrix",e);
		}
		finally{
			try {
				QueueFacade.getInstance().deleteQueue(queueDTO);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	

	/**
	 * Realiza la consulta de documentos de forma asincronica
	 * @param queue
	 * @throws SQLException 
	 */
	private void queryDocumentAsynch(QueueDTO queueDTO) throws SQLException{

		try {
			//persistence.truncateQueryDocument();
			if(queueDTO.getParams()!=null){
				String[] params = queueDTO.getParams().split(",");
				int idDocument = Integer.parseInt(queueDTO.getParams());
				WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
				DocumentDTO document = workspace.getPersistence().selectDocumentById(idDocument);
				logger.info("QueryDocumentAsynch: id:" + idDocument + " Name:" + document.getName());
				String lazyData = workspace.getPersistence().readFile(EDataFolder.DOWNLOAD, document.getName());
				String lazyCleanData = null;
				try{
				 lazyCleanData = workspace.getPersistence().readFile(EDataFolder.CLEAN, document.getName());
				}
				catch(Exception e){
					logger.error(e);
				}
				if(lazyData.length()>1000) lazyData=lazyData.substring(0, 1000);
				if(lazyCleanData!=null && lazyCleanData.length()>1000) lazyCleanData=lazyCleanData.substring(0, 1000);
				document.setLazyData(lazyData);
				document.setLazyCleanData(lazyCleanData);
				logger.debug("Document query"+document.getId());
				if(document!=null){
					logger.debug("Calling Remote Servlet");
					CallRemoteServlet callRemoteServlet = new CallRemoteServlet(workspace);
					try {
						callRemoteServlet.sendDocument(document);
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						logger.error(e);
					}
				}
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error in queryDocumentAsynch", e);
		}
		finally{
			try {
				QueueFacade.getInstance().deleteQueue(queueDTO);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Cancela las colas pendientes para reiniciar la aplicacion con una nueva orden de descarga
	 */
	private void cancelActiveQueue(EQueueEvent queueEvent) throws SQLException{
		Collection<QueueDTO> queueActive = 
				QueueFacade.getInstance().getQueueByStatus(queueEvent, EQueueStatus.ENQUEUE);
		for (QueueDTO queueDTO2 : queueActive) {
			QueueFacade.getInstance().deleteQueue(queueDTO2);
		}
	}
}

