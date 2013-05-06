package co.edu.sanmartin.persistence.factory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDatabase;
import co.edu.sanmartin.persistence.constant.ESystemProperty;
import co.edu.sanmartin.persistence.constant.properties.PropertiesLoader;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
/**
 * Pool de conexiones a la base de datos
 * @author Ricardo
 *
 */
public class ConnectionDataSourcePoolMySQL {
	private final String MAIN_DATABASE_NAME ="fuzzyclustering";
	Logger logger = Logger.getLogger(ConnectionDataSourcePoolMySQL.class);
	/** Pool de conexiones */
	private HashMap<String,DataSource> dataSourceCol = new HashMap<String,DataSource>();


	/**
	 * Inicializacion de BasicDataSource
	 */
	private DataSource inicializaDataSource(EDatabase database, String databaseName, String server) {
		BasicDataSource basicDataSource = new BasicDataSource();
		PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();
		String userName = propertiesLoader.getProperty(ESystemProperty.MYSQL_USER.getPropertyName());
		String password = propertiesLoader.getProperty(ESystemProperty.MYSQL_PASSWORD.getPropertyName());
		basicDataSource.setDriverClassName(database.getDriverName());
		basicDataSource.setUsername(userName);
		basicDataSource.setPassword(password);
		basicDataSource.setUrl("jdbc:mysql://"+server+"/"+databaseName);
		// Opcional. Sentencia SQL que le puede servir a BasicDataSource
		// para comprobar que la conexion es correcta.
		logger.info("Init DataSource Server:"+ server);
		basicDataSource.setValidationQuery("select 1");
		return basicDataSource;
	}
	
	public DataSource getDataSource(String databaseName, String server){
		DataSource dataSource = dataSourceCol.get(databaseName);
		if(dataSource == null){
			dataSource = inicializaDataSource(EDatabase.MYSQL, databaseName, server);
			dataSourceCol.put(databaseName, dataSource);
		}
		return dataSource;
	}

	/**
	 * Retorna la conexion a la base de datos principal
	 * @return
	 */
	public Connection getMainConnection(){
		
		Connection connection = null;
		try {
			PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();
			String server = propertiesLoader.getProperty(ESystemProperty.MYSQL_SERVER.getPropertyName());
			DataSource dataSource = this.getDataSource(MAIN_DATABASE_NAME, server);
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * Retorna la conexion a la base de datos del workspace
	 * @param workspace
	 * @return
	 */
	public Connection getConnection(WorkspaceDTO workspace){
		
		Connection connection = null;
		try {
			DataSource dataSource = this.getDataSource(workspace.getName(), workspace.getDatabaseHost());
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * Realiza una insercion, pidiendo una conexion al dataSource y cerrandola
	 * inmediatamente despues, para liberarla.
	 */
	private void inserta(DataSource dataSource) {
		Connection conexion = null;
		try {
			// BasicDataSource nos reserva una conexion y nos la devuelve.
			conexion = dataSource.getConnection();

			// La insercion.
			Statement ps = conexion.createStatement();
			ps
			.executeUpdate("insert into person values (null,22,'Pedro','Martinez')");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			freeConnection(conexion);
		}
	}


	/**
	 * Cierra la conexion. Al provenir de BasicDataSource, en realidad no se
	 * esta cerrando. La llamada a close() le indica al BasicDataSource que
	 * hemos terminado con dicha conexion y que puede asignarsela a otro que la
	 * pida.
	 * 
	 * @param conexion
	 */
	public void freeConnection(Connection conexion) {
		try {
			if (null != conexion) {
				// En realidad no cierra, solo libera la conexion.
				conexion.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Realiza una consulta a la base de datos y muestra los resultados en
	 * pantalla.
	 */
	private void realizaConsulta(DataSource dataSource) {
		Connection conexion = null;
		try {
			conexion = dataSource.getConnection();
			Statement sentencia = conexion.createStatement();
			ResultSet rs = sentencia.executeQuery("select * from person");

			// La tabla tiene cuatro campos.
			while (rs.next()) {
				System.out.println(rs.getObject("PERSON_ID"));
				System.out.println(rs.getObject("age"));
				System.out.println(rs.getObject("lastname"));
				System.out.println(rs.getObject("firstname"));
				System.out.println("--------------");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// En el finally, para asegurar que se ejecuta, se cierra la
			// conexion.
			freeConnection(conexion);
		}
	}
}
