package co.edu.sanmartin.persistence.constant;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Soporte de Conexiones a base de datos
 * @author Ricardo
 *
 */
public enum EDatabase {
	POSTGRES("postgres","org.postgresql.Driver"), 
	MYSQL("mysql","com.mysql.jdbc.Driver");
	
	private String databaseName;
	private String driverName;

	EDatabase(String databaseName, String driverName) {
		this.databaseName = databaseName;
		this.driverName = driverName;
	}
	/**
	 * @return the propertyName
	 */
	public String getDatabaseName() {
		return this.databaseName;
	}

	public String getDriverName(){
		return this.driverName;
	}
	public static Collection<EDatabase> toList(){
		List<EDatabase> list = Arrays.asList(EDatabase.values()); 
		return list;
	}
}
