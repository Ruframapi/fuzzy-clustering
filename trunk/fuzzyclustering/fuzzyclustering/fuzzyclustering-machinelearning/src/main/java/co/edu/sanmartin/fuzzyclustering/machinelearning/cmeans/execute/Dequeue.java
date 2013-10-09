package co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans.execute;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.index.DimensionallyReduced;
import co.edu.sanmartin.fuzzyclustering.ir.index.MutualInformation;
import co.edu.sanmartin.fuzzyclustering.machinelearning.classifier.DocumentCluster;
import co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans.FuzzyCMeansBigData;
import co.edu.sanmartin.fuzzyclustering.machinelearning.reuters.ReutersTrainer;
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
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
	PersistenceFacade persistenceFacade;
	/**
	 * Constructor de la clase
	 * @param restart define si reinicia el proceso o continua con un proceso anterior
	 */
	public Dequeue(){
		try {
			this.cancelActiveQueue(EQueueEvent.GENERATE_MEMBERSHIP_INDEX);
			this.cancelActiveQueue(EQueueEvent.GENERATE_FUZZY_CMEANS);
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
		logger.trace("Init machineLearning executeQueue");
		Collection<QueueDTO> queueCol = 
				QueueFacade.getInstance().getQueueByStatusDate(EModule.MACHINE_LEARNING, 
						EQueueStatus.ENQUEUE, new Date());
		for (QueueDTO queueDTO : queueCol) {
			switch( queueDTO.getEvent() ){
			case GENERATE_FUZZY_CMEANS:
				this.generateCmeans(queueDTO);
				break;
			case GENERATE_MEMBERSHIP_INDEX:
				this.generateMembershipIndex(queueDTO);
				break;
			case CLASSIFYFORFILE:
				this.classifyForFile(queueDTO);
				break;
		}
			
		}
		
	}
	
	/**
	 * Genera los conjuntos difusos a partir de la matrix ppmi
	 * @param queueDTO
	 */
	private void generateCmeans(QueueDTO queueDTO) {
		// TODO Auto-generated method stub
		String[] parameter = queueDTO.getParams().split(",");
		boolean reducedMatrix = Boolean.parseBoolean(parameter[0]);
		int centroidsAmount = Integer.parseInt(parameter[1]);
		int iterationsAmount = Integer.parseInt(parameter[2]);
		double mValue = Double.parseDouble(parameter[3]);
		double stopValue = Double.parseDouble(parameter[4]);
		boolean buildMatrix = Boolean.parseBoolean(parameter[5]);
		
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queueDTO.getWorkspace());
		SendMessageAsynch.sendMessage(workspace, "Iniciando proceso de generacion de conjuntos difusos");
		String fileName =  null;
		if(reducedMatrix){
			fileName = DimensionallyReduced.REDUCED_FILE_NAME;
		}
		else{
			fileName = MutualInformation.PPMI_FILE_NAME;
		}
		FuzzyCMeansBigData cmeans = new FuzzyCMeansBigData(workspace,fileName, centroidsAmount, 
															iterationsAmount, mValue, stopValue,
															buildMatrix);
		cmeans.init();
		cmeans.calculateFuzzyCmeans();
		SendMessageAsynch.sendMessage(workspace, "Proceso de generacion de conjuntos difusos finalizada");
		try {
			QueueFacade.getInstance().deleteQueue(queueDTO);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Genera la matriz de pertenencia de los terminos
	 * @param queue
	 */
	public void generateMembershipIndex(QueueDTO queue){
		
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queue.getWorkspace());
		SendMessageAsynch.sendMessage(workspace, "Iniciando proceso de indice de pertenencia");
		DocumentCluster documentCluster = new DocumentCluster(workspace);
		documentCluster.buildMembershipIndex();
		SendMessageAsynch.sendMessage(workspace, "Proceso de indice de pertenencia finalizado");
		try {
			QueueFacade.getInstance().deleteQueue(queue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Genera la matriz de pertenencia de los terminos
	 * @param queue
	 */
	public void classifyForFile(QueueDTO queue){
		
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace(queue.getWorkspace());
		SendMessageAsynch.sendMessage(workspace, "Iniciando proceso de clasificacion");
		ReutersTrainer reuters = new ReutersTrainer(workspace);
		reuters.classifyDocumentsInFile();
		SendMessageAsynch.sendMessage(workspace, "Proceso de clasificacion finalizado");
		try {
			QueueFacade.getInstance().deleteQueue(queue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

