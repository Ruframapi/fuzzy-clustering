package co.edu.sanmartin.fuzzyclustering.ir.facade;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;
import co.edu.sanmartin.fuzzyclustering.ir.index.CmeansMatrixBuilder;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.fuzzyclustering.ir.index.MutualInformation;
import co.edu.sanmartin.persistence.constant.EModule;
import co.edu.sanmartin.persistence.constant.EQueueEvent;
import co.edu.sanmartin.persistence.constant.EQueueStatus;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

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

	public void createInvertedIndex(){
		invertedIndex.createInvertedIndex();
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
	 * Construye todas las matrices necesarias para la construcion de los conjuntos difusos
	 */
	public void buildCmeanMatrix() throws Exception{
		CmeansMatrixBuilder builder = new CmeansMatrixBuilder();
		builder.build();
	}
}
