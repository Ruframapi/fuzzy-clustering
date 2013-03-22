package co.edu.sanmartin.webscraping.execute;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Clase que arranca el sistema de descarga de Noticias
 * @author Ricardo Carvajal Salamanca
 *
 */
public class Main{

	private static final Logger logger = Logger.getLogger(Main.class);
	static{
		final String LOG_FILE = "../config/log4j_webscrapping.properties"; 
		Properties logProperties = new Properties();      
		try {      
			logProperties.load(new FileInputStream (LOG_FILE));  
			PropertyConfigurator.configure(logProperties);      
			logger.info("Logging enabled");    
		}     
		catch(IOException e)                
		{       
			System.out.println("Logging not enabled" + e);       
		}  
	}

	public static void main ( String args[]){
		logger.info("Init WebScrapping App");
		Dequeue dequeue = new Dequeue();
		dequeue.run();
	}
}
