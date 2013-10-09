package co.edu.sanmartin.persistence.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDatabase;
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.factory.ConnectionDataSourcePoolMySQL;
import co.edu.sanmartin.persistence.factory.DAOFactory;

public class WorkspaceDAO{

	protected Connection connection = null;
	protected PreparedStatement statement;
	protected ResultSet rs;
	protected String sQLQuery;
	private final String DATABASE_NAME = "fuzzyclustering";
	private static Logger logger = Logger.getLogger("WorkspaceDAO");
	public WorkspaceDAO() {
	}

	public void insert(WorkspaceDTO object) throws Exception {
		try {
			connection = getConnectionPool().getMainConnection();
			sQLQuery = "INSERT INTO workspace (name,server_host) VALUES (?,?)";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, object.getName());
			statement.setString(2, object.getDatabaseHost());
			statement.executeUpdate();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
	
	}
	
	public void delete(WorkspaceDTO object) throws Exception {
		try {
			connection = getConnectionPool().getMainConnection();
			sQLQuery = "DELETE FROM workspace WHERE name =?";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, object.getName());
			statement.executeUpdate();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
		
	}
	
	/**
	 * Crea una nueva base de datos para el espacio de trabajo
	 * @param workspace
	 * @throws Exception
	 */
	public void createDatabase(WorkspaceDTO workspace) throws Exception{
		try {
			connection = getConnectionPool().getMainConnection();
			sQLQuery = "CREATE DATABASE "+ workspace.getName();
			statement = connection.prepareStatement(sQLQuery);
			statement.executeUpdate();
		}catch(Exception e){
			throw e;
		} finally {
			getConnectionPool().freeConnection(connection);
		}
	}
	
	/**
	 * Elimina la base de datos para el espacio de trabajo
	 * @param workspace
	 * @throws Exception
	 */
	public void dropDatabase(WorkspaceDTO workspace) throws Exception{
		try {
			connection = getConnectionPool().getMainConnection();
			sQLQuery = "DROP DATABASE "+ workspace.getName();
			statement = connection.prepareStatement(sQLQuery);
			statement.executeUpdate();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
	}
	
	public Collection<WorkspaceDTO> selectAllWorkpace(){
		Collection<WorkspaceDTO> workspaceColl = new ArrayList<WorkspaceDTO>();
		try {
			connection = getConnectionPool().getMainConnection();
			sQLQuery = "SELECT * FROM workspace";
			statement = connection.prepareStatement(sQLQuery);
			rs = statement.executeQuery();
			while(rs.next()){
				WorkspaceDTO workspace = new WorkspaceDTO();
				workspace.setName(rs.getString("name"));
				workspace.setDataRoot(rs.getString("name"));
				workspace.setDatabaseHost(rs.getString("server_host"));
				workspaceColl.add(workspace);
			}
		} catch (SQLException e) {
			logger.error("Error in selectAllWorkspace", e);
		}
		finally{
			getConnectionPool().freeConnection(connection);
		}
		
		return workspaceColl;
	}
	public WorkspaceDTO selectWorkspace(String name) throws Exception{
		WorkspaceDTO workspace = null;
		try {
			connection = getConnectionPool().getMainConnection();
			sQLQuery = "SELECT * FROM workspace where name = ?";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, name);
			rs = statement.executeQuery();
			if(rs.next()){
				workspace = new WorkspaceDTO();
				workspace.setName(rs.getString("name"));
				workspace.setDataRoot(rs.getString("name"));
				workspace.setDatabaseHost(rs.getString("server_host"));
				
			}
		}
		finally{
			getConnectionPool().freeConnection(connection);
		}
		return workspace;
		
	}
	
	protected ConnectionDataSourcePoolMySQL getConnectionPool(){
		ConnectionDataSourcePoolMySQL connectionPool = DAOFactory.getInstance().getConnectionPool(EDatabase.MYSQL);
		return connectionPool;
	}
	
	

}
