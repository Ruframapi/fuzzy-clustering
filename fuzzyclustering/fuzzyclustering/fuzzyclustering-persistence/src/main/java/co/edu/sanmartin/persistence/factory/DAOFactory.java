package co.edu.sanmartin.persistence.factory;
/**
 * Clase que gestión la conexión a la base de datos postgres
 * @author Ricardo Carvajal Salamanca
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import co.edu.sanmartin.persistence.constant.EDatabase;

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
	
/*	public Connection getConnection(EDatabase database) throws SQLException {
		if( this.conn !=null){
			return this.conn;
		}
		try {
			Class.forName(database.getDriverName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection conn = null;
		conn = DriverManager.getConnection(getDBUrl(database), USER, PASSWORD);
		return conn;
	}
	*/
	private Connection getConnection(EDatabase database) throws SQLException {
		Connection conn = this.connectionDataSource.getConnection();
		return conn;
	}
	
	public ConnectionDataSourcePoolMySQL getConnectionPool(EDatabase database){
		return this.connectionDataSource;
	}
	
	public String getDBUrl(EDatabase database){
		String urlConnection = null;
		switch(database){
		case POSTGRES:
			urlConnection = "jdbc:postgresql://localhost:5432/"+DATABASE_NAME;
			break;
		case MYSQL:
			urlConnection = "jdbc:mysql://localhost:3306/"+DATABASE_NAME;
			break;	
		}
		return urlConnection;
	}



}
