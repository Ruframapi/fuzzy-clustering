package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.SendMessageAsynch;
import co.edu.sanmartin.persistence.file.BigDoubleMatrixFileManager;
import co.edu.sanmartin.persistence.file.BigIntegerMatrixFileManager;

/**
 * Clase encargada de generar una matrix con el valor del grado
 * de relacion entre terminos que se encuentran co-ocurrentes en el texto
 * utilizando el metodo Pointwise Mutual Infomration
 * @author Ricardo
 *
 */
public class MutualInformation {
	private static Logger logger = Logger.getLogger("MutualInformation");
	public static final String PPMI_FILE_NAME = "ppmi.dat"; 
	
	private WorkspaceDTO workspace;
	
	public MutualInformation(WorkspaceDTO workspace){
		this.workspace = workspace;
	}
	
	
	/**
	 * Crea la matrix PPMI a partir del conjunto de datos de la matrix Termino Termino y
	 * el indice invertido.
	 */
	public void buildMutualInformationBigMatrix(boolean persist) throws IOException{
		logger.info("Inicializando construccion de Matrix PPMI");
		SendMessageAsynch.sendMessage(this.workspace,"Creando PPMI Matriz");
		long time_start = 0, time_end=0;
		time_start = System.currentTimeMillis();
		InvertedIndex invertedIndex = new InvertedIndex(this.workspace);
		invertedIndex.loadInvertedIndexData();
		BigIntegerMatrixFileManager largeMatrixTermTerm = new BigIntegerMatrixFileManager(this.workspace);
		BigDoubleMatrixFileManager largeMatrixPpmi = new BigDoubleMatrixFileManager(this.workspace);
	
		double totalWordDocumentsCount = invertedIndex.getTotaldocumentWordsCount();
		try {
			largeMatrixTermTerm.loadReadOnly(EDataFolder.MATRIX,InvertedIndex.TERM_TERM_FILENAME);
			largeMatrixPpmi.loadReadWrite(EDataFolder.MATRIX,PPMI_FILE_NAME, 
					largeMatrixTermTerm.height(), largeMatrixTermTerm.width());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Error en buildMutualInformationBigMatrix", e);
		}
		for (int i = 0; i < largeMatrixTermTerm.height(); i++) {
			for (int j = 0; j < largeMatrixTermTerm.width(); j++) {
				int termCount = largeMatrixTermTerm.get(i, j);
				double ppmi = this.ppmi(termCount, invertedIndex.getCountByTerm(i), 
										invertedIndex.getCountByTerm(j), totalWordDocumentsCount);
				
				BigDecimal bigDecimal = new BigDecimal(ppmi);
				bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
				
				largeMatrixPpmi.set(i, j, bigDecimal.doubleValue());
				
			}	
		}
		largeMatrixTermTerm.close();
		if(persist){
			largeMatrixPpmi.close();
		}
		time_end = System.currentTimeMillis();
		String finalMessage = "La construccion de la Matrix PPMI tomo "+ 
				( time_end - time_start )/1000 +" segundos" + 
				(( time_end - time_start )/1000)/60 +" minutos";
		logger.info(finalMessage);
		SendMessageAsynch.sendMessage(this.workspace,"Creando PPMI Matriz");
	}
	
	
	/**
	 * Funcion Pointwise Mutual Information
	 * @param termCount valor de la matrix termino termino 
	 * @param termCount_i total termino i en todos los documentos
	 * @param termCount_j total termino j en todos los documentos
	 * @param totalWordDocumentsCount total terminos en todos los documentos
	 * @return
	 */
	public double ppmi(int termCount, int termCount_i, int termCount_j, 
						double totalWordDocumentsCount){
		double ppmi = 0.00;
		if(termCount>0){
			if(termCount_i>0.00 || termCount_j>0.00){
				double ppmiTerm_ij=termCount/totalWordDocumentsCount;
				double ppmiTermi = termCount_i/totalWordDocumentsCount;
				double ppmiTermj = termCount_j/totalWordDocumentsCount;
				ppmi = Math.log((ppmiTerm_ij)/(ppmiTermi*ppmiTermj))/Math.log(2);
			}
		}
		return ppmi;
	}
	
	@Deprecated
	public void buildMutualInformationMatrix(){
		InvertedIndex invertedIndex = new InvertedIndex(this.workspace);
		int[][] invertedIndexData = invertedIndex.getTermTermMatrix();
		double[][] ppmiData = new double[invertedIndex.getTermCount()][invertedIndex.getTermCount()]; 
		double totalWordDocumentsCount = invertedIndex.getTotaldocumentWordsCount();
		for (int i = 0; i < invertedIndex.getTermCount(); i++) {
			for (int j = 0; j < invertedIndex.getTermCount(); j++) {
				int termCount = invertedIndexData[i][j];
				double ppmi = this.ppmi(termCount, invertedIndex.getCountByTerm(i), 
										invertedIndex.getCountByTerm(j), totalWordDocumentsCount);
				
				BigDecimal bigDecimal = new BigDecimal(ppmi);
				bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
				ppmiData[i][j]=bigDecimal.doubleValue();
			}
			
		}
		invertedIndex.saveDoubleMatrix(ppmiData, PPMI_FILE_NAME);
	}
	
}
