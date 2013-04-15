package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

public class CmeansMatrixBuilder {

	private static Logger logger = Logger.getLogger("CmeansMatrixBuilder");
	public void build(){
		try {
			this.sendMessageAsynch("Creando Indice Invertido");
			InvertedIndex invertedIndex = new InvertedIndex();
			InvertedIndexThreadPool threadPool = new InvertedIndexThreadPool();
			threadPool.run();
			this.sendMessageAsynch("Indice Invertido Creado");
			this.sendMessageAsynch("Creando Matriz de Terminos");
			invertedIndex.createTermTermBigMatrix(true);
			this.sendMessageAsynch("Matriz de Terminos creada");
			this.sendMessageAsynch("Creando PPMI Matriz");
			MutualInformation mutualInformation = new MutualInformation();
			mutualInformation.buildMutualInformationBigMatrix(true);
			this.sendMessageAsynch("PPMI Matriz Creada");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.sendMessageAsynch("Matriz PPMI Creada");
	}

	public void sendMessageAsynch(String message){
		QueueDTO queue = new QueueDTO();
		queue.setInitDate(PersistenceFacade.getInstance().getServerDate().getTime());
		queue.setEvent(EQueueEvent.SEND_MESSAGE);
		queue.setModule(EModule.QUERYASYNCH);
		queue.setParams(message);
		queue.setStatus(EQueueStatus.ENQUEUE);
		try {
			PersistenceFacade.getInstance().insertQueue(queue);
		} catch (SQLException e) {
			logger.error("Error in sendMessageAsynch",e);
		}
	}
}
