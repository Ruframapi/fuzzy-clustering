package co.edu.sanmartin.fuzzyclustering.ir.facade;

import co.edu.sanmartin.fuzzyclustering.ir.execute.CleanerThreadPool;
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
		indexManager = new InvertedIndex();
	}
	
	public static IRFacade getInstance(){
		if(instance == null){
			instance = new IRFacade();
		}
		return instance;
	}

	/**
	 * Metodo que realiza la creacion del indice invertido
	 */
	public void cleanText(){
		CleanerThreadPool threadPool = new CleanerThreadPool();
		threadPool.executeThreadPool();
	}
	public void createInvertedIndex(){
		InvertedIndexThreadPool threadPool = new InvertedIndexThreadPool();
		threadPool.executeThreadPool();
	}
	
	public int[][] getTermTermMatrix(){
		return indexManager.getTermTermMatrix();
	}
}
