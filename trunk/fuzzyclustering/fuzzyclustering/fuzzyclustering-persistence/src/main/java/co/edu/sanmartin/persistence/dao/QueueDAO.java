package co.edu.sanmartin.persistence.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.QueueDTO;

public class QueueDAO extends AbstractDAO<QueueDTO>{

	@Override
	public void insert(QueueDTO queue) throws SQLException {
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "INSERT INTO queue (module, event,initDate,processDate,status) VALUES (?,?,?,?,?)";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, queue.getModule().name());
			statement.setString(2, queue.getEvent().name());
			statement.setTimestamp(3, new java.sql.Timestamp(queue.getInitDate().getTime()));
			if(queue.getProcessDate()!=null) {
				statement.setDate(4, new java.sql.Date(queue.getProcessDate().getTime()));
			}
			else{
				statement.setDate(4, null);
			}
			statement.setString(5, queue.getStatus().name());
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
	}

	/**
	 * Trunca la tabla de colas
	 * @param queue
	 * @throws SQLException
	 */
	public void truncate() throws SQLException {
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "TRUNCATE TABLE queue";
			statement = connection.prepareStatement(sQLQuery);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
	}
	
	/**
	 * Retonar las colas a procesar
	 * @return
	 */
	public Collection<QueueDTO> getQueue(EQueueStatus status, Date nowDate){
		
		Collection<QueueDTO> queueColl = new ArrayList<QueueDTO>();
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "SELECT * FROM queue status = ? AND initDate <= ?";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, status.name());
			java.sql.Timestamp date = new java.sql.Timestamp(nowDate.getTime());
			statement.setTimestamp(2, date);
			rs = statement.executeQuery();
			while(rs.next()){
				QueueDTO queue = new QueueDTO();
				queue.setId(rs.getInt("id"));
				queue.setModule(EModule.valueOf(rs.getString("module")));
				queue.setEvent(EQueueEvent.valueOf(rs.getString("event")));
				queue.setInitDate(rs.getTimestamp("initDate"));
				queue.setProcessDate(rs.getTimestamp("processDate"));
				queue.setStatus(status);
				queueColl.add(queue);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			getConnectionPool().freeConnection(connection);
		}
		
		return queueColl;
	}
	
	/**
	 * Retonar las colas a procesar
	 * @return
	 */
	public Collection<QueueDTO> getQueueByStatusDate(EModule module, EQueueStatus status, Date nowDate){
		
		Collection<QueueDTO> queueColl = new ArrayList<QueueDTO>();
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "SELECT * FROM queue WHERE module=? AND status = ? AND initDate <= ?";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, module.name());
			statement.setString(2, status.name());
			java.sql.Timestamp date = new java.sql.Timestamp(nowDate.getTime());
			statement.setTimestamp(3, date);
			rs = statement.executeQuery();
			while(rs.next()){
				QueueDTO queue = new QueueDTO();
				queue.setId(rs.getInt("id"));
				queue.setModule(EModule.valueOf(rs.getString("module")));
				queue.setEvent(EQueueEvent.valueOf(rs.getString("event")));
				queue.setInitDate(rs.getTimestamp("initDate"));
				queue.setProcessDate(rs.getTimestamp("processDate"));
				queue.setStatus(status);
				queueColl.add(queue);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			getConnectionPool().freeConnection(connection);
		}
		
		return queueColl;
	}
	
	
	/**
	 * Retonar las colas a procesar
	 * @return
	 */
	public Collection<QueueDTO> getQueueByStatus(EQueueStatus status, Date nowDate){
		
		Collection<QueueDTO> queueColl = new ArrayList<QueueDTO>();
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "SELECT * FROM queue WHERE status = ? AND initDate <= ?";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, status.name());
			java.sql.Timestamp date = new java.sql.Timestamp(nowDate.getTime());
			statement.setTimestamp(2, date);
			rs = statement.executeQuery();
			while(rs.next()){
				QueueDTO queue = new QueueDTO();
				queue.setId(rs.getInt("id"));
				queue.setModule(EModule.valueOf(rs.getString("module")));
				queue.setEvent(EQueueEvent.valueOf(rs.getString("event")));
				queue.setInitDate(rs.getTimestamp("initDate"));
				queue.setProcessDate(rs.getTimestamp("processDate"));
				queue.setStatus(status);
				queueColl.add(queue);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			getConnectionPool().freeConnection(connection);
		}
		
		return queueColl;
	}
	
	
	/**
	 * Retonar las colas a procesar
	 * @return
	 */
	public Collection<QueueDTO> getQueueByStatus(EQueueEvent event, EQueueStatus status){
		
		Collection<QueueDTO> queueColl = new ArrayList<QueueDTO>();
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "SELECT * FROM queue WHERE event = ?  AND status = ?";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, event.name());
			statement.setString(2, status.name());
			rs = statement.executeQuery();
			while(rs.next()){
				QueueDTO queue = new QueueDTO();
				queue.setId(rs.getInt("id"));
				queue.setModule(EModule.valueOf(rs.getString("module")));
				queue.setEvent(EQueueEvent.valueOf(rs.getString("event")));
				queue.setInitDate(rs.getTimestamp("initDate"));
				queue.setProcessDate(rs.getTimestamp("processDate"));
				queue.setStatus(status);
				queueColl.add(queue);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			getConnectionPool().freeConnection(connection);
		}
		
		return queueColl;
	}

	
	/**
	 * Crea la tabla del DTO
	 * 
	 * @throws SQLException
	 */
	public void createTable(boolean dropTable) throws SQLException {
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = null;
			if (dropTable == true) {
				sQLQuery = "DROP TABLE IF EXISTS queue";
				statement = connection.prepareStatement(sQLQuery);
				statement.executeUpdate();
			}
			sQLQuery = "CREATE TABLE fuzzyclustering.queue (id INT NOT NULL AUTO_INCREMENT , " +
					"module VARCHAR(45) NOT NULL ," +
					"event VARCHAR(45) NOT NULL ," +
					"initDate DATETIME NOT NULL ," +
					"processDate DATETIME ," +
					"status VARCHAR(45) NOT NULL , " +
					"PRIMARY KEY (id) );";
			statement = connection.prepareStatement(sQLQuery);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
	}
	

	@Override
	public void update(QueueDTO queue) throws SQLException {
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "UPDATE queue SET module=?, event=?,initDate=?,processDate=?,status=? WHERE id=?";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1,queue.getModule().name());
			statement.setString(2, queue.getEvent().name());
			statement.setTimestamp(3, new java.sql.Timestamp(queue.getInitDate().getTime()));
			if(queue.getProcessDate()!=null){
				statement.setTimestamp(4, new java.sql.Timestamp(queue.getProcessDate().getTime()));
			}
			else{
				statement.setTimestamp(4, null);
			}
			statement.setString(5, queue.getStatus().name());
			statement.setInt(6, queue.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
	}

	@Override
	public void delete(QueueDTO object) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void select() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<QueueDTO> selectAll() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	public void backupTable(){
		super.backupTable("queue");
	}
	
	public void restoreTable(){
		super.restoreTable("queue", "id,event,initDate,processDate,status");
	}


}
