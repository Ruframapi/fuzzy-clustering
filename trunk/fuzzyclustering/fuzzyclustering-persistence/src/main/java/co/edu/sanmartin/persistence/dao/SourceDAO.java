package co.edu.sanmartin.persistence.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import co.edu.sanmartin.persistence.dto.SourceDTO;

public class SourceDAO extends AbstractDAO<SourceDTO>{
	
	public SourceDAO(){
	}


	public void insert(SourceDTO sourceDTO) throws SQLException {
	    connection = getConnection();
		sQLQuery = "insert into source (name,url) VALUES (?,?)";
		statement = connection.prepareStatement(sQLQuery);
		statement.setString(1, sourceDTO.getName());
		statement.setString(2, sourceDTO.getUrl());
		statement.executeUpdate();
	}

	public void update(SourceDTO sourceDTO) throws SQLException {
		connection = getConnection();
		sQLQuery = "update source set name = ?,url =? where name =?";
		statement = connection.prepareStatement(sQLQuery);
		statement.setString(1, sourceDTO.getName());
		statement.setString(2, sourceDTO.getUrl());
		statement.setString(3, sourceDTO.getName());
		statement.executeUpdate();
		
	}

	public void delete(SourceDTO sourceDTO) throws SQLException {
		connection = getConnection();
		sQLQuery = "delete from source where name = ?";
		statement = connection.prepareStatement(sQLQuery);
		statement.setString(1, sourceDTO.getName());
		statement.executeUpdate();
	}


	public Collection<SourceDTO> selectAll() throws SQLException{
		
	    connection = getConnection();
		sQLQuery = "select * from source";
		statement = connection.prepareStatement(sQLQuery);
		rs = statement.executeQuery();
		Collection<SourceDTO> sourceColl = new ArrayList<SourceDTO>();
		while(rs.next()){
			SourceDTO sourceDTO = new SourceDTO();
			sourceDTO.setName(rs.getString("name"));
			sourceDTO.setUrl(rs.getString("url"));
			sourceColl.add(sourceDTO);
		}
		return sourceColl;
	}
	
	/**
	 * Crea la tabla del DTO
	 * @throws SQLException
	 */
	public void createTable( boolean dropTable ) throws SQLException{
		connection = getConnection();
		sQLQuery = null;
		if(dropTable == true){
			sQLQuery = "DROP TABLE IF EXISTS source";
			statement = connection.prepareStatement(sQLQuery);
			statement.executeUpdate();
		}
		sQLQuery = 	"CREATE TABLE source (" +
						"name varchar(40) NOT NULL," +
						"url text NOT NULL)";
		statement = connection.prepareStatement(sQLQuery);
		statement.executeUpdate();
	}


	@Override
	public <T> void select() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
}
