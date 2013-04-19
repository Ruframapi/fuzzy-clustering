package co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans.execute;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.fuzzyclustering.ir.index.DimensionallyReduced;
import co.edu.sanmartin.fuzzyclustering.ir.index.MutualInformation;
import co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans.FuzzyCMeansBigData;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.QueueDTO;
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
			this.cancelActiveQueue(EQueueEvent.GENERATE_INVERTED_INDEX);
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

		Collection<QueueDTO> queueCol = 
				this.persistenceFacade.getQueueByStatusDate(EModule.MACHINE_LEARNING, 
						EQueueStatus.ENQUEUE, new Date());
		for (QueueDTO queueDTO : queueCol) {
			switch( queueDTO.getEvent() ){
			case GENERATE_FUZZY_CMEANS:
				this.generateCmeans(queueDTO);
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
		int centroidsAmount = Integer.parseInt(parameter[0]);
		int iterationsAmount = Integer.parseInt(parameter[1]);
		double mValue = Double.parseDouble(parameter[2]);
		boolean buildMatrix = Boolean.parseBoolean(parameter[3]);
		FuzzyCMeansBigData cmeans = new FuzzyCMeansBigData(DimensionallyReduced.REDUCED_FILE_NAME, 
														centroidsAmount, iterationsAmount, mValue, buildMatrix);
		cmeans.init();
		cmeans.calculateFuzzyCmeans();
		try {
			PersistenceFacade.getInstance().deleteQueue(queueDTO);
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
				this.persistenceFacade.getQueueByStatus(queueEvent, EQueueStatus.ENQUEUE);
		for (QueueDTO queueDTO2 : queueActive) {
			persistenceFacade.deleteQueue(queueDTO2);
		}
	}
	
	
	
	
}

