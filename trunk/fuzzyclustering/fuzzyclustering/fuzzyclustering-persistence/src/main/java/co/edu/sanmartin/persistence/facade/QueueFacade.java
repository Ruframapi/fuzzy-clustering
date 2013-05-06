package co.edu.sanmartin.persistence.facade;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dao.QueueDAO;
import co.edu.sanmartin.persistence.dto.QueueDTO;

/**
 * Facade que gestiona las colas de los diferentes espacios de trabajo
 * @author Ricardo
 *
 */
public class QueueFacade {
	private QueueDAO queueDAO;
	private static QueueFacade instance;
	
	private QueueFacade(){
		this.queueDAO = new QueueDAO();
	}
	
	public static QueueFacade getInstance(){
		if(instance == null){
			instance = new QueueFacade();
		}
		return instance;
	}
	
	public Collection<QueueDTO> getQueueByStatusDate(EModule module, EQueueStatus status, Date nowDate){
		return this.queueDAO.getQueueByStatusDate(module, status, nowDate);
	}
	
	public Collection<QueueDTO> getQueueByStatus(EQueueStatus status, Date nowDate){
		return this.queueDAO.getQueueByStatus(status, nowDate);
	}
	
	public Collection<QueueDTO> getQueueByStatus(EQueueEvent event, EQueueStatus status){
		return this.queueDAO.getQueueByStatus(event, status);
	}
	
	public synchronized void updateQueue(QueueDTO queue) throws SQLException {
		this.queueDAO.update(queue);
	}
	
	public synchronized void insertQueue(QueueDTO queue) throws SQLException {
		this.queueDAO.insert(queue);
	}
	
	public synchronized void deleteQueue(QueueDTO queue) throws SQLException {
		this.queueDAO.delete(queue);
	}
	
	public void truncateQueue() throws SQLException {
		this.queueDAO.truncate();
	}
	
	public void createQueueTable(boolean dropTable) throws SQLException {
		this.queueDAO.createTable(dropTable);
	}
	
	public Calendar getServerDate(){
		return queueDAO.getServerDate();
	}
	
	
	
	
}
