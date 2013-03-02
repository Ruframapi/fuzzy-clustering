package co.edu.sanmartin.persistence.factory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import co.edu.sanmartin.persistence.constant.EDatabase;
/**
 * Pool de conexiones a la base de datos
 * @author Ricardo
 *
 */
public class ConnectionDataSourcePoolMySQL {

	/** Pool de conexiones */
    private DataSource dataSource;

    /**
     * Inicializa el pool de conexiones BasicDataSource y realiza una insercion
     * y una consulta
     */
    public ConnectionDataSourcePoolMySQL() {
        inicializaDataSource(EDatabase.MYSQL);
    }

    /**
     * Inicializacion de BasicDataSource
     */
    private void inicializaDataSource(EDatabase database) {
        BasicDataSource basicDataSource = new BasicDataSource();

        basicDataSource.setDriverClassName(database.getDriverName());
        basicDataSource.setUsername("fuzzyclustering");
        basicDataSource.setPassword("fuzzyclustering");
        basicDataSource.setUrl("jdbc:mysql://localhost/fuzzyclustering");
        // Opcional. Sentencia SQL que le puede servir a BasicDataSource
        // para comprobar que la conexion es correcta.
        basicDataSource.setValidationQuery("select 1");
        dataSource = basicDataSource;
    }
    
    public Connection getConnection(){
    	Connection connection = null;
    	try {
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
    private void inserta() {
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
    private void realizaConsulta() {
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
