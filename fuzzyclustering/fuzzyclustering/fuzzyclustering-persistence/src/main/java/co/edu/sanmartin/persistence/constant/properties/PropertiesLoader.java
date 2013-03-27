package co.edu.sanmartin.persistence.constant.properties;

import java.util.Properties;

/**
 * Carga del archivo de propiedades globales del sistema
 * @author Ricardo Carvajal Salamanca
 *
 */

public class PropertiesLoader extends Properties {
	
	private static final long serialVersionUID = -4952714784195696015L;
	private static PropertiesLoader instance = null;
	
	private PropertiesLoader(){
		try {
        	this.load(this.getClass().getClassLoader().getResourceAsStream("fuzzyclustering.properties"));
        } catch (Exception e) {
           System.out.print("Error al cargar el PropertiesLoader " + e);
        }
	}
	
	public static PropertiesLoader getInstance(){
		if(instance == null){
			instance = new PropertiesLoader();
		}
		return instance;
	}

	
	
}
