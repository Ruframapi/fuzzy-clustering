package co.edu.sanmartin.persistence.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

import co.edu.sanmartin.persistence.dto.QueryDocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;

public class QueryDocumentDAO extends AbstractDAO<QueryDocumentDTO>{

	public QueryDocumentDAO(WorkspaceDTO workspace) {
		super(workspace);
	}

	@Override
	public void insert(QueryDocumentDTO queryDocument) throws SQLException {
		try {
			connection = getConnectionPool().getConnection(this.workspace);
			sQLQuery = "INSERT INTO querydocument (originalData, cleanData) VALUES (?,?)";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, queryDocument.getOriginalData());
			statement.setString(2, queryDocument.getCleanData());
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
		
	}

	@Override
	public void update(QueryDocumentDTO object) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(QueryDocumentDTO object) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void select() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<QueryDocumentDTO> selectAll() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Crea la tabla del DTO
	 * 
	 * @throws SQLException
	 */
	public void createTable(boolean dropTable) throws SQLException {
		try {
			connection = getConnectionPool().getConnection(this.workspace);
			sQLQuery = null;
			if (dropTable == true) {
				sQLQuery = "DROP TABLE IF EXISTS querydocument";
				statement = connection.prepareStatement(sQLQuery);
				statement.executeUpdate();
				}
			sQLQuery = "CREATE TABLE querydocument (" +
						"originalData text,cleanData text )";
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
	 * Trunca la tabla de colas
	 * @param queue
	 * @throws SQLException
	 */
	public void truncate() throws SQLException {
		try {
			connection = getConnectionPool().getConnection(this.workspace);
			sQLQuery = "TRUNCATE TABLE querydocument";
			statement = connection.prepareStatement(sQLQuery);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
	}

}
