package co.edu.sanmartin.fuzzyclustering.cleaner.execute;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Ejecuta el proceso de limpieza de los archivos
 * @author Ricardo Carvajal Salamanca
 *
 */
public class Main {

	private static final Logger logger = Logger.getLogger(Main.class);
	
	static{
		final String LOG_FILE = "../config/log4j_cleaner.properties"; 
		Properties logProperties = new Properties();      
		try {      
			logProperties.load(new FileInputStream (LOG_FILE));  
			PropertyConfigurator.configure(logProperties);      
			System.out.println("Logging enabled for Cleaner");    
		}     
		catch(IOException e)                
		{       
			System.out.println("Logging not enabled for Cleaner" + e);       
		}  
	}
	

	public static void main( String args[]){
		logger.info("Init Clenaer App");
		Dequeue dequeue = new Dequeue();
		dequeue.run();
	}
	
}
