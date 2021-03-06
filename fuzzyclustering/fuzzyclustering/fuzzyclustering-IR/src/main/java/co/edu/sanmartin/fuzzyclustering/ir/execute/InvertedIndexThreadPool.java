package co.edu.sanmartin.fuzzyclustering.ir.execute;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.execute.worker.InvertedIndexWorkerThread;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndexBuilder;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.PropertyDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.facade.SendMessageAsynch;

/**
 * Thead pool con fin de crear un pool de 10 hilos para la descarga de los documentos
 * @author Ricardo
 *
 */
public class InvertedIndexThreadPool implements Runnable{
	
	//Cantidad minima que debe aparecer un termino para almacenarlo en el indice.
	private int minTermsOcurrences = 0;
	private int documentsAmount = 0;
	private WorkspaceDTO workspace;
	public InvertedIndexThreadPool(WorkspaceDTO workspace, int minTermsOcurrences, int documentsAmount){
		this.minTermsOcurrences = minTermsOcurrences;
		this.documentsAmount = documentsAmount;
		this.workspace = workspace;
	}
	Logger logger = Logger.getLogger("InvertedIndexThreadPool");

	public void run() {
		logger.info("Inicializando construccion del indice invertido");
		SendMessageAsynch.sendMessage(this.workspace,"Creando Indice Invertido");
		long time_start, time_end;
		time_start = System.currentTimeMillis();
		int threadPoolNumber = 10;
		//Se cargan las fuentes rss disponibles
		try {
			PropertyDTO threadPoolNumberProperty = 
					this.workspace.getPersistence().getProperty(EProperty.INVERTED_INDEX_THREAD_NUMBER);
			threadPoolNumber = threadPoolNumberProperty.intValue();
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Collection<DocumentDTO> fileCol = this.workspace.getPersistence().getFileList(EDataFolder.CLEAN);
		InvertedIndexWorkerThread invertedIndexWorkerThread;
		
		ThreadPoolExecutor executor=(ThreadPoolExecutor)Executors.newFixedThreadPool(threadPoolNumber);
		HashMap<String, StringBuilder> dataMap = new HashMap<String,StringBuilder>();
		InvertedIndexBuilder index = new InvertedIndexBuilder(this.workspace,new ConcurrentLinkedDeque<String>());
		//Almacenamos los archivos en memoria
		int counter = 0;
		for (DocumentDTO file : fileCol) {
			//Limitamos la creacion del indice a una cantidad de documentos limitada
			if(documentsAmount>0 && counter++>this.documentsAmount) break;
			StringBuilder dataFile = new StringBuilder();
			dataFile.append(this.workspace.getPersistence().readFile(EDataFolder.CLEAN,file.getName()));
			invertedIndexWorkerThread = new InvertedIndexWorkerThread(this.workspace, dataFile, file.getNameWithoutExtension(), index);
			executor.execute(invertedIndexWorkerThread);
		}

		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Guarda el indice invertido
		index.saveIndex(this.minTermsOcurrences);
		time_end = System.currentTimeMillis();
		
		String finalMessage = "La construccion del indice invertido tomo "+ 
				( time_end - time_start )/1000 +" segundos" + 
				(( time_end - time_start )/1000)/60 +" minutos";
		logger.info(finalMessage);
		
		SendMessageAsynch.sendMessage(this.workspace,finalMessage);
		
	}
}
