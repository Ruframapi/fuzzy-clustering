package co.edu.sanmartin.persistence.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import co.edu.sanmartin.persistence.constant.ESourceType;
import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.persistence.dto.StopwordDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;

public class SourceDAO extends AbstractDAO<SourceDTO> {

	public SourceDAO(WorkspaceDTO workspace) {
		super(workspace);
	}
	Collection<SourceDTO> sourceColl = new ArrayList<SourceDTO>();
	

	public void insert(SourceDTO sourceDTO) {
		try {
			connection = getConnectionPool().getConnection(this.workspace);
			sQLQuery = "insert into source (name,url,type) VALUES (?,?,?)";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, sourceDTO.getName());
			statement.setString(2, sourceDTO.getUrl());
			statement.setString(3, sourceDTO.getType().name());
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
	}

	public void update(SourceDTO sourceDTO) {
		try {
			connection = getConnectionPool().getConnection(this.workspace);
			sQLQuery = "update source set name = ?,url =?, type=?, lastQuery = ?, sinceId = ? where name =?";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, sourceDTO.getName());
			statement.setString(2, sourceDTO.getUrl());
			statement.setString(3, sourceDTO.getType().name());
			if(sourceDTO.getLastQuery()!=null){
			   statement.setTimestamp(4, new java.sql.Timestamp(sourceDTO.getLastQuery().getTime()));
			}
			else{
				statement.setTimestamp(4, null);
			}
			statement.setLong(5, sourceDTO.getSinceId());
			statement.setString(6, sourceDTO.getName());
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}

	}

	public void delete(SourceDTO sourceDTO) {
		try {
			connection = getConnectionPool().getConnection(this.workspace);
			sQLQuery = "delete from source where name = ?";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, sourceDTO.getName());
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
	}

	@Override
	public Collection<SourceDTO> selectAll() {
		this.selectAll(true);
		if(this.sourceColl==null || this.sourceColl.isEmpty()){
			this.selectAll(true);
		}
		return this.sourceColl;
	}
	
	public Collection<SourceDTO> selectAll(boolean refresh) {

		try {
			if(refresh){
				this.sourceColl = new ArrayList<SourceDTO>();
				connection = getConnectionPool().getConnection(this.workspace);
				sQLQuery = "select * from source";
				statement = connection.prepareStatement(sQLQuery);
				rs = statement.executeQuery();

				while (rs.next()) {
					SourceDTO sourceDTO = new SourceDTO();
					sourceDTO.setName(rs.getString("name"));
					sourceDTO.setUrl(rs.getString("url"));
					sourceDTO.setLastQuery(rs.getTimestamp("lastquery"));
					ESourceType sourceType = ESourceType.valueOf(rs.getString("type"));
					sourceDTO.setType(sourceType);
					sourceDTO.setSinceId(rs.getLong("sinceId"));
					sourceColl.add(sourceDTO);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		} finally {
			getConnectionPool().freeConnection(connection);
		}
		return sourceColl;
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
				sQLQuery = "DROP TABLE IF EXISTS source";
				statement = connection.prepareStatement(sQLQuery);
				statement.executeUpdate();
			}
			sQLQuery = "CREATE TABLE source (" + "name varchar(40) NOT NULL,"
					+ "url text NOT NULL, type varchar(20) NOT NULL, lastquery DATETIME, sinceId BIGINT)";
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
	public <T> void select() throws SQLException {
		// TODO Auto-generated method stub

	}
	
	public void backupTable(){
		super.backupTable("source");
	}
	public void restoreTable(){
		super.restoreTable("source", "name,url,type");
	}
	

}
