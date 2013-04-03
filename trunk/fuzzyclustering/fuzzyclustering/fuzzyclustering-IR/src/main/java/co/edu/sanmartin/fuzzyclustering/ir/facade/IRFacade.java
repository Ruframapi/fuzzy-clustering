package co.edu.sanmartin.fuzzyclustering.ir.facade;

import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;

/**
 * Fachada que gestiona los procesos de recuperación de la informacion (Information Retrieval)
 * @author Ricardo Carvajal Salamanca
 *
 */
public class IRFacade {

	private static IRFacade instance;
	InvertedIndex indexManager;
	private IRFacade(){
		
	}
	
	public static IRFacade getInstance(){
		if(instance == null){
			instance = new IRFacade();
		}
		return instance;
	}

	public void createInvertedIndex(){
		indexManager = new InvertedIndex();
		InvertedIndexThreadPool threadPool = new InvertedIndexThreadPool();
		threadPool.executeThreadPool();
	}
	
	public int[][] getTermTermMatrix(){
		return indexManager.getTermTermMatrix();
	}
}
