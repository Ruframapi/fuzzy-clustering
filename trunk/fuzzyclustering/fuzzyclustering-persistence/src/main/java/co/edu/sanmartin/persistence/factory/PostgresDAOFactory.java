package co.edu.sanmartin.persistence.factory;
/**
 * Clase que gestión la conexión a la base de datos postgres
 * @author Ricardo Carvajal Salamanca
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresDAOFactory {
	public static final String DRIVER = "org.postgresql.Driver";
	public static final String DBURL = "jdbc:postgresql://localhost:5432/fuzzyclustering";
	public static final String USER = "fuzzyclustering";
	public static final String PASSWORD = "fuzzyclustering";

	private static PostgresDAOFactory connectionFactory = null;

	private PostgresDAOFactory() {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() throws SQLException {
		Connection conn = null;
		conn = DriverManager.getConnection(DBURL, USER, PASSWORD);
		return conn;
	}

	public static PostgresDAOFactory getInstance() {
		if (connectionFactory == null) {
			connectionFactory = new PostgresDAOFactory();
		}
		return connectionFactory;
	}

}
