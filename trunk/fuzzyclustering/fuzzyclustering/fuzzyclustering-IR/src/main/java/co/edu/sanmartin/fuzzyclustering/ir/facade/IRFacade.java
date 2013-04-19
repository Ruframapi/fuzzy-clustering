package co.edu.sanmartin.fuzzyclustering.ir.facade;

import java.io.IOException;

import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;
import co.edu.sanmartin.fuzzyclustering.ir.index.DimensionallyReduced;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.fuzzyclustering.ir.index.MutualInformation;
import co.edu.sanmartin.persistence.facade.SendMessageAsynch;

/**
 * Fachada que gestiona los procesos de recuperación de la informacion (Information Retrieval)
 * @author Ricardo Carvajal Salamanca
 *
 */
public class IRFacade {

	private static IRFacade instance;
	private InvertedIndex invertedIndex = new InvertedIndex();
	
	private IRFacade(){
		
	}
	
	public static IRFacade getInstance(){
		if(instance == null){
			instance = new IRFacade();
		}
		return instance;
	}

	public void createInvertedIndex(int minTermsOcurrences){
		invertedIndex.createInvertedIndex(minTermsOcurrences);
	}
	
	public void createTermTermBigMatrix(boolean persist) throws IOException{
		this.invertedIndex.createTermTermBigMatrix(persist);
	}
	
	public void createPPMIBigMatrix(boolean persist){
		MutualInformation mutualInformation = new MutualInformation();
		try {
			mutualInformation.buildMutualInformationBigMatrix(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Realiza el proceso de reducción de dimensionalidad
	 * @param fileName
	 * @param newDimension
	 * @param saveReadable
	 * @param reableRowsAmount
	 */
	public void reducedDimensionPPMIMatrix(int newDimension, 
			boolean saveReadable, int reableRowsAmount){
		DimensionallyReduced dimensionally = new DimensionallyReduced();
		dimensionally.reducedDimensionDoubleMatrix(MutualInformation.PPMI_FILE_NAME, 
				newDimension, saveReadable, reableRowsAmount);
	}
	
	/**
	 * Construye todas las matrices necesarias para la construcion de los conjuntos difusos
	 */
	public void buildCmeanMatrix(int minQuantiryTerms) throws Exception{
		try {
			InvertedIndex invertedIndex = new InvertedIndex();
			InvertedIndexThreadPool threadPool = new InvertedIndexThreadPool(minQuantiryTerms);
			threadPool.run();
			invertedIndex.createTermTermBigMatrix(true);
			MutualInformation mutualInformation = new MutualInformation();
			mutualInformation.buildMutualInformationBigMatrix(true);
			SendMessageAsynch.sendMessage("PPMI Matriz Creada");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SendMessageAsynch.sendMessage("Proceso Finalizado");
	}
	
	
	
}
