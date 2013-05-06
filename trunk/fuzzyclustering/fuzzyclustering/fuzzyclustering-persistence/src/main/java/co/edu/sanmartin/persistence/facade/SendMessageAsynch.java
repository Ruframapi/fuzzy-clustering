package co.edu.sanmartin.persistence.facade;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;

public final class SendMessageAsynch {
	private static Logger logger = Logger.getLogger("SendMessageAsynch");

	public static void sendMessage(WorkspaceDTO workspace, String message){
		QueueDTO queue = new QueueDTO();
		queue.setInitDate(QueueFacade.getInstance().getServerDate().getTime());
		queue.setEvent(EQueueEvent.SEND_MESSAGE);
		queue.setModule(EModule.QUERYASYNCH);
		queue.setParams(message);
		queue.setStatus(EQueueStatus.ENQUEUE);
		queue.setWorkspace(workspace.getName());
		try {
			QueueFacade.getInstance().insertQueue(queue);
		} catch (SQLException e) {
			logger.error("Error in sendMessageAsynch",e);
		}
	}
}
