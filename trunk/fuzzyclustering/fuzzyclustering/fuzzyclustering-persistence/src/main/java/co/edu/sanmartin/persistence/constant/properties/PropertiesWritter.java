package co.edu.sanmartin.persistence.constant.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Clase encargada de realizar la actualizacion de un archivo de propiedades
 * @author Ricardo Carvajal Salamanca
 *
 */
public class PropertiesWritter {
	
	private void escribirProperties(String file, String key, String value) {
		// Crear el objeto archivo
		File archivo = new File(this.getClass().getResource(file).getFile().replace("%20", " "));
		//Crear el objeto properties		
		Properties properties = new Properties();
		try {
			// Cargar las propiedades del archivo
			properties.load(new FileInputStream(archivo));
			properties.setProperty(key,value);
			// Escriber en el archivo los cambios
			FileOutputStream fos=new FileOutputStream(archivo.toString().replace("\\", "/"));
			properties.store(fos,null);

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
