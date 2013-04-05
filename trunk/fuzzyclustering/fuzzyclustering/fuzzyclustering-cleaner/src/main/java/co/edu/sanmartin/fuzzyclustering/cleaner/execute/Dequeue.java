package co.edu.sanmartin.fuzzyclustering.cleaner.execute;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.cleaner.execute.thread.CleanerThreadPool;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase que arranca el sistema de descarga de Noticias
 * @author Ricardo Carvajal Salamanca
 *
 */


public class Dequeue implements Runnable{

	PersistenceFacade persistenceFacade;
	Logger logger = Logger.getLogger(Dequeue.class);
	/**
	 * Constructor de la clase
	 * @param restart define si reinicia el proceso o continua con un proceso anterior
	 */
	public Dequeue(){
		persistenceFacade = PersistenceFacade.getInstance();
	}
	
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				this.executeQueue();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (PropertyValueNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	/**
	 * Realiza la limpieza de los ficheros
	 * @throws SQLException 
	 * @throws PropertyValueNotFoundException 
	 */
	public void executeQueue() throws SQLException, PropertyValueNotFoundException{
		logger.trace("Init executeQueue");
		CleanerThreadPool threadPool = CleanerThreadPool.getInstance();
		if(threadPool.getExecutor()!=null && threadPool.getExecutor().getQueue().size()==0){
			threadPool.executeThreadPool();
		}
		
	}

	
}
