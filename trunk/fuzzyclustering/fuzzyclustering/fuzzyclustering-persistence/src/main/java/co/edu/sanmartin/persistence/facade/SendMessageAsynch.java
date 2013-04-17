package co.edu.sanmartin.persistence.facade;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.QueueDTO;

public final class SendMessageAsynch {
	private static Logger logger = Logger.getLogger("SendMessageAsynch");

	public static void sendMessage(String message){
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
