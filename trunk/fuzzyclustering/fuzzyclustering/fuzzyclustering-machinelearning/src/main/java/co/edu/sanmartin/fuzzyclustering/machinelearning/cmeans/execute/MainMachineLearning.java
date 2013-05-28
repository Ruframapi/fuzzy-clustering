package co.edu.sanmartin.fuzzyclustering.machinelearning.cmeans.execute;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase que arranca el sistema de descarga de Noticias
 * @author Ricardo Carvajal Salamanca
 *
 */
public class MainMachineLearning{

	
	static{
		final String LOG_FILE = "../config/log4j_machinelearning.properties"; 
		Properties logProperties = new Properties();      
		try {      
			logProperties.load(new FileInputStream (LOG_FILE));  
			PropertyConfigurator.configure(logProperties);      
			System.out.println("Logging enabled for MachineLearning");    
		}     
		catch(IOException e)                
		{       
			System.out.println("Logging not enabled for MachineLearning" + e);       
		}  
	}
	private static final Logger logger = Logger.getLogger(MainMachineLearning.class);
	
	
	public static void main ( String args[]){
		logger.info("Init MachineLearning App");
		Dequeue dequeue = new Dequeue();
		dequeue.run();
	}
	

}
