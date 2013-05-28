package co.edu.sanmartin.fuzzyclustering.ir.facade;

import java.io.IOException;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;
import co.edu.sanmartin.fuzzyclustering.ir.index.DimensionallyReduced;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.fuzzyclustering.ir.index.MutualInformation;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.Cleaner;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.Stemmer;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.SendMessageAsynch;

/**
 * Fachada que gestiona los procesos de recuperación de la informacion (Information Retrieval)
 * @author Ricardo Carvajal Salamanca
 *
 */
public class IRFacade {

	private static IRFacade instance;
	private WorkspaceDTO workspace;
	private Cleaner cleaner;
	private Stemmer stemmer;
	private static Logger logger = Logger.getLogger("IRFacade");
	
	private IRFacade(WorkspaceDTO workspace){
		this.workspace = workspace;
		this.cleaner = new Cleaner(workspace);
		this.stemmer = new Stemmer(workspace);
	}
	
	public static IRFacade getInstance(WorkspaceDTO workspace){
		
		if(instance == null){
			instance = new IRFacade(workspace);
		}
		else{
			instance.workspace = workspace;
		}
		return instance;
	}

	/**
	 * Retorna el documento normalizado
	 * @param documentText texto original del documento
	 * @return
	 */
	public String getNormalizedDocumentText(String  documentText){
		
		StringBuilder dataFile = new StringBuilder();
		dataFile.append(documentText);
		dataFile = new StringBuilder(cleaner.unescapeHtml(dataFile.toString()));
		dataFile = new StringBuilder(cleaner.toLowerData(dataFile.toString()));
		dataFile = new StringBuilder(cleaner.deleteLexiconStopWords(dataFile.toString()));
		dataFile = new StringBuilder(cleaner.applyRegexExpression(dataFile.toString()));
		String stemmerOn = null;
		try {
			stemmerOn = this.workspace.getPersistence().getProperty(EProperty.TEXT_STEMMER_ON).getValue();
		} catch (PropertyValueNotFoundException e) {
			logger.error(e);
		}
		if(stemmerOn.equals("true")){
		   dataFile = new StringBuilder(stemmer.stem(dataFile.toString()));
		}
		return dataFile.toString();
	}
	
	/**
	 * Carga el indice invertido con la reducion Zipf
	 * @return
	 */
	public InvertedIndex getInvertedIndexZipf(){
		InvertedIndex invertedIndex = new InvertedIndex(workspace);
		invertedIndex.loadInvertedIndexDataZipf();
		return invertedIndex;
	}
	/**
	 * Carga el indice invertido completo
	 * @return
	 */
	public InvertedIndex getInvertedIndexOriginal(){
		InvertedIndex invertedIndex = new InvertedIndex(workspace);
		invertedIndex.loadInvertedIndexOriginal();
		return invertedIndex;
	}
	
	public void createInvertedIndex(int minTermsOcurrences, int documentsAmount){
		workspace.getPersistence().deleteFolder(EDataFolder.MATRIX);
		workspace.getPersistence().deleteFolder(EDataFolder.MACHINE_LEARNING);
		InvertedIndex invertedIndex = new InvertedIndex(workspace);	
		//invertedIndex.loadInvertedIndexData();
		invertedIndex.createInvertedIndex(minTermsOcurrences,documentsAmount);
	}
	
	public void reducedZipfInvertedIndex(int cutOnPercent,int cutOffPercent, int minTermOcurrences){
		InvertedIndex invertedIndex = new InvertedIndex(workspace);
		invertedIndex.reducedZipfInvertedIndex(cutOnPercent, cutOffPercent, minTermOcurrences);
	}
	
	public void createTermTermBigMatrix(boolean persist) throws IOException{
		InvertedIndex invertedIndex = new InvertedIndex(workspace);	
		invertedIndex.loadInvertedIndexDataZipf();
		invertedIndex.createTermTermBigMatrix(persist);
	}

	public void createPPMIBigMatrix(boolean persist){
		MutualInformation mutualInformation = new MutualInformation(workspace);
		mutualInformation.buildMutualInformation(true);
		
	}
	
	/**
	 * Realiza el proceso de reducción de dimensionalidad
	 * @param newDimension
	 * @param saveReadable
	 * @param reableRowsAmount
	 * @param fileName
	 */
	public void reducedDimensionPPMIMatrix(int newDimension, boolean saveReadable, 
			int reableRowsAmount){
		DimensionallyReduced dimensionally = new DimensionallyReduced(workspace);
		dimensionally.reducedDimensionDoubleMatrix(MutualInformation.PPMI_FILE_NAME, 
				newDimension, saveReadable, reableRowsAmount);
	}
	
	/**
	 * Construye todas las matrices necesarias para la construcion de los conjuntos difusos
	 */
	public void buildCmeanMatrix(int minQuantiryTerms, int documentsAmount) throws Exception{
		try {
			InvertedIndex invertedIndex = new InvertedIndex(workspace);
			InvertedIndexThreadPool threadPool = new InvertedIndexThreadPool(workspace,minQuantiryTerms,documentsAmount);
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
