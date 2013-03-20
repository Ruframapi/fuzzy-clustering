package co.edu.sanmartin.persistence.constant;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Enumeraci�n de las propiedades gloables del sistema
 * @author Ricardo Carvajal Salamanca
 *
 */
public enum ESystemProperty {
	
	MAIN_PATH("main.path","Ubicacion principal de la ubicacion"),
	MYSQL_DATABASE("mysql.database", "Nombre de la base de datos"),
	MYSQL_SERVER("mysql.server", "Servidor de Mysql"),
	MYSQL_USER("mysql.user","Usuario de Mysql"),
	MYSQL_PASSWORD("mysql.password", "Contrasenia de Mysql");

	private String propertyName;

	ESystemProperty(String propertyName, String label) {
		this.propertyName = propertyName;
	}
	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	public static Collection<ESystemProperty> toList(){
		List<ESystemProperty> list = Arrays.asList(ESystemProperty.values()); 
		return list;
	}
}
