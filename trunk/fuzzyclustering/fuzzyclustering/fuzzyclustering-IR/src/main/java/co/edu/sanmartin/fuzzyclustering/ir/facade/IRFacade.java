package co.edu.sanmartin.fuzzyclustering.ir.facade;

import java.io.IOException;

import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;
import co.edu.sanmartin.fuzzyclustering.ir.index.DimensionallyReduced;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.fuzzyclustering.ir.index.MutualInformation;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.SendMessageAsynch;

/**
 * Fachada que gestiona los procesos de recuperación de la informacion (Information Retrieval)
 * @author Ricardo Carvajal Salamanca
 *
 */
public class IRFacade {

	private static IRFacade instance;
	
	
	private IRFacade(){
		
	}
	
	public static IRFacade getInstance(){
		if(instance == null){
			instance = new IRFacade();
		}
		return instance;
	}

	public InvertedIndex getInvertedIndex(WorkspaceDTO workspace){
		InvertedIndex invertedIndex = new InvertedIndex(workspace);
		invertedIndex.loadInvertedIndexData();
		return invertedIndex;
	}
	public void createInvertedIndex(WorkspaceDTO workspace, int minTermsOcurrences){
		workspace.getPersistence().deleteFolder(EDataFolder.MATRIX);
		workspace.getPersistence().deleteFolder(EDataFolder.MACHINE_LEARNING);
		InvertedIndex invertedIndex = new InvertedIndex(workspace);	
		//invertedIndex.loadInvertedIndexData();
		invertedIndex.createInvertedIndex(minTermsOcurrences);
	}
	
	public void createTermTermBigMatrix(WorkspaceDTO workspace, boolean persist) throws IOException{
		InvertedIndex invertedIndex = new InvertedIndex(workspace);	
		invertedIndex.loadInvertedIndexData();
		invertedIndex.createTermTermBigMatrix(persist);
	}

	public void createPPMIBigMatrix(WorkspaceDTO workspace, boolean persist){
		MutualInformation mutualInformation = new MutualInformation(workspace);
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
	public void reducedDimensionPPMIMatrix(WorkspaceDTO workspace, int newDimension, 
			boolean saveReadable, int reableRowsAmount){
		DimensionallyReduced dimensionally = new DimensionallyReduced(workspace);
		dimensionally.reducedDimensionDoubleMatrix(MutualInformation.PPMI_FILE_NAME, 
				newDimension, saveReadable, reableRowsAmount);
	}
	
	/**
	 * Construye todas las matrices necesarias para la construcion de los conjuntos difusos
	 */
	public void buildCmeanMatrix(WorkspaceDTO workspace, int minQuantiryTerms) throws Exception{
		try {
			InvertedIndex invertedIndex = new InvertedIndex(workspace);
			InvertedIndexThreadPool threadPool = new InvertedIndexThreadPool(workspace,minQuantiryTerms);
			threadPool.run();
			invertedIndex.createTermTermBigMatrix(true);
			MutualInformation mutualInformation = new MutualInformation(workspace);
			mutualInformation.buildMutualInformationBigMatrix(true);
			SendMessageAsynch.sendMessage(workspace, "PPMI Matriz Creada");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SendMessageAsynch.sendMessage(workspace, "Proceso Finalizado");
	}
	
	
	
}
