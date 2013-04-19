package co.edu.sanmartin.queryasynch.execute;

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

	
	static{
		final String LOG_FILE = "../config/log4j_queryasynch.properties"; 
		Properties logProperties = new Properties();      
		try {      
			logProperties.load(new FileInputStream (LOG_FILE));  
			PropertyConfigurator.configure(logProperties);      
			System.out.println("Logging enabled for QueryAsynch");    
		}     
		catch(IOException e)                
		{       
			System.out.println("Logging not enabled for QueryAsynch" + e);       
		}  
	}
	private static final Logger logger = Logger.getLogger(Main.class);
	
	
	public static void main ( String args[]){
		logger.info("Init QueryAsynch App");
		Dequeue dequeue = new Dequeue();
		dequeue.run();
	}
}
