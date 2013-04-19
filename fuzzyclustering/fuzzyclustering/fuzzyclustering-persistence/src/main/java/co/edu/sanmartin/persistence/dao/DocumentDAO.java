package co.edu.sanmartin.persistence.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import co.edu.sanmartin.persistence.constant.ESourceType;
import co.edu.sanmartin.persistence.dto.DocumentDTO;

public class DocumentDAO extends AbstractDAO<DocumentDTO>{

	@Override
	public synchronized void insert(DocumentDTO document) throws SQLException {
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "INSERT INTO document (sourcetype, name,source,published_date, download_date) VALUES (?,?,?,?,?)";
			statement = connection.prepareStatement(sQLQuery);
			statement.setString(1, document.getSourceType().name());
			statement.setString(2, document.getName());
			statement.setString(3, document.getSource());
			statement.setTimestamp(4, new Timestamp(document.getPublishedDate().getTime()));
			statement.setTimestamp(5, new Timestamp(document.getDownloadDate().getTime()));
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
			sQLQuery = "TRUNCATE TABLE document";
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
	public synchronized void update(DocumentDTO document) throws SQLException {
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "UPDATE document SET clean_date = ? WHERE id = ?";
			statement = connection.prepareStatement(sQLQuery);
			if(document.getCleanDate()==null){
				statement.setTimestamp(1, null);
			}
			else{
				statement.setTimestamp(1, new Timestamp(document.getCleanDate().getTime()));
			}
			statement.setInt(2, document.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			getConnectionPool().freeConnection(connection);
		}
		
	}

	@Override
	public void delete(DocumentDTO object) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void select() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	public DocumentDTO selectDocumentById(int idDocument){
		DocumentDTO document= null;
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "SELECT * FROM fuzzyclustering.document WHERE id = ?";
			statement = connection.prepareStatement(sQLQuery);
			statement.setLong(1, idDocument);
			rs = statement.executeQuery();
			if(rs.next()){
				document = new DocumentDTO();
				document.setId(rs.getInt("id"));
				document.setDownloadDate(rs.getTimestamp("download_date"));
				document.setName(rs.getString("name"));
				document.setSourceType(ESourceType.valueOf(rs.getString("sourcetype")));
				document.setPublishedDate(rs.getTimestamp("published_date"));
				document.setSource(rs.getString("source"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			getConnectionPool().freeConnection(connection);
		}
		
		return document;
	}

	@Override
	public Collection<DocumentDTO> selectAll() throws SQLException {
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
			connection = getConnectionPool().getConnection();
			sQLQuery = null;
			if (dropTable == true) {
				sQLQuery = "DROP TABLE IF EXISTS document";
				statement = connection.prepareStatement(sQLQuery);
				statement.executeUpdate();
			}
			sQLQuery = "CREATE TABLE fuzzyclustering.document ( "+
					"id INT NULL AUTO_INCREMENT , " +
					"sourcetype VARCHAR(255) NOT NULL ," +
					"name VARCHAR(255) NOT NULL ," +
					"source VARCHAR(255) NOT NULL," +
					"published_date DATETIME NOT NULL," +
					"download_date DATETIME NOT NULL," +
					"clean_date DATETIME NOT NULL)";
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
	 * Retorna la cantidad de documentos descargados
	 * @return
	 */
	public int getDownloadDocumentAmount(){
		int amount = 0;
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "SELECT COUNT(1) AS amount FROM fuzzyclustering.document;";
			statement = connection.prepareStatement(sQLQuery);
			rs = statement.executeQuery();
			if(rs.next()){
				amount = rs.getInt("amount");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			getConnectionPool().freeConnection(connection);
		}
		
		return amount;
	}
	
	
	/**
	 * Retorna la lista de documentos
	 * @return
	 */
	public Collection<DocumentDTO> getPaginateDocumentsColl(long startId, int limit){
		Collection<DocumentDTO> documentColl = new ArrayList<DocumentDTO>();
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "SELECT * FROM fuzzyclustering.document WHERE id > ? ORDER BY id LIMIT ?";
			statement = connection.prepareStatement(sQLQuery);
			statement.setLong(1, startId);
			statement.setInt(2, limit);
			rs = statement.executeQuery();
			while(rs.next()){
				DocumentDTO document = new DocumentDTO();
				document.setId(rs.getInt("id"));
				document.setDownloadDate(rs.getTimestamp("download_date"));
				document.setName(rs.getString("name"));
				document.setSourceType(ESourceType.valueOf(rs.getString("sourcetype")));
				document.setPublishedDate(rs.getTimestamp("published_date"));
				document.setCleanDate(rs.getTimestamp("clean_date"));
				document.setSource(rs.getString("source"));
				documentColl.add(document);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			getConnectionPool().freeConnection(connection);
		}
		
		return documentColl;
	}
	
	/**
	 * Retorna la lista de documentos
	 * @return
	 */
	public Collection<DocumentDTO> getDocumentsForClean(){
		Collection<DocumentDTO> documentColl = new ArrayList<DocumentDTO>();
		try {
			connection = getConnectionPool().getConnection();
			sQLQuery = "SELECT * FROM fuzzyclustering.document WHERE clean_date IS NULL LIMIT 100";
			statement = connection.prepareStatement(sQLQuery);
			rs = statement.executeQuery();
			while(rs.next()){
				DocumentDTO document = new DocumentDTO();
				document.setId(rs.getInt("id"));
				document.setDownloadDate(rs.getTimestamp("download_date"));
				document.setName(rs.getString("name"));
				document.setSourceType(ESourceType.valueOf(rs.getString("sourcetype")));
				document.setPublishedDate(rs.getTimestamp("published_date"));
				document.setCleanDate(rs.getTimestamp("clean_date"));
				document.setSource(rs.getString("source"));
				documentColl.add(document);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			getConnectionPool().freeConnection(connection);
		}
		
		return documentColl;
	}
	
	public void backupTable(){
		super.backupTable("document");
	}
	

}
