package co.edu.sanmartin.persistence.factory;
/**
 * Clase que gestión la conexión a la base de datos postgres
 * @author Ricardo Carvajal Salamanca
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import co.edu.sanmartin.persistence.constant.EDatabase;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;

public class DAOFactory {
	public static final String USER = "fuzzyclustering";
	public static final String PASSWORD = "fuzzyclustering";
	public static final String DATABASE_NAME = "fuzzyclustering";
	private static ConnectionDataSourcePoolMySQL connectionDataSource;
	private static DAOFactory connectionFactory = null;
	
	Connection conn = null;
	
	public DAOFactory(){
		if(connectionDataSource == null){
			connectionDataSource = new ConnectionDataSourcePoolMySQL();
		}
	}
	
	public static DAOFactory getInstance() {
		if (connectionFactory == null) {
			connectionFactory = new DAOFactory();
		}
		return connectionFactory;
	}
	
	private Connection getConnection(WorkspaceDTO workspace) throws SQLException {
		Connection conn = this.connectionDataSource.getConnection(workspace);
		return conn;
	}
	
	public ConnectionDataSourcePoolMySQL getConnectionPool(EDatabase database){
		return this.connectionDataSource;
	}
	
	public String getDBUrl(EDatabase database, String databaseName){
		String urlConnection = null;
		switch(database){
		case POSTGRES:
			urlConnection = "jdbc:postgresql://localhost:5432/"+databaseName;
			break;
		case MYSQL:
			urlConnection = "jdbc:mysql://localhost:3306/"+databaseName;
			break;	
		}
		return urlConnection;
	}



}
