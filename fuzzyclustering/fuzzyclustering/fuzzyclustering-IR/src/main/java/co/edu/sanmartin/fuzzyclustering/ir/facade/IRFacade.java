package co.edu.sanmartin.fuzzyclustering.ir.facade;

import java.io.IOException;

import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;

/**
 * Fachada que gestiona los procesos de recuperación de la informacion (Information Retrieval)
 * @author Ricardo Carvajal Salamanca
 *
 */
public class IRFacade {

	private static IRFacade instance;
	private InvertedIndex invertedIndex;
	private IRFacade(){
		
	}
	
	public static IRFacade getInstance(){
		if(instance == null){
			instance = new IRFacade();
		}
		return instance;
	}

	public void createInvertedIndex(){
		invertedIndex = new InvertedIndex();
		InvertedIndexThreadPool threadPool = new InvertedIndexThreadPool();
		Thread thread=new Thread(threadPool);
		thread.start();
	}
	
	public void buildTermTermBigMatrix(boolean persist) throws IOException{
		this.invertedIndex.buildTermTermBigMatrix(persist);
	}

}
