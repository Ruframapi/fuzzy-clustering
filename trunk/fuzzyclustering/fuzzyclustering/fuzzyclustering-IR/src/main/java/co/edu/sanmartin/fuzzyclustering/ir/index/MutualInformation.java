package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

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
	@Deprecated
	public void buildMutualInformationBigMatrix(boolean persist) throws IOException{
		logger.info("Inicializando construccion de Matrix PPMI");
		SendMessageAsynch.sendMessage(this.workspace,"Creando PPMI Matriz");
		long time_start = 0, time_end=0;
		time_start = System.currentTimeMillis();
		InvertedIndex invertedIndex = new InvertedIndex(this.workspace);
		invertedIndex.loadInvertedIndexDataZipf();
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
	 * 
	 * 				computer  data  pinch result  sugar
	 * apricot			0		0	  1     0       1
	 * pinneaple 		0		0     1     0       1
	 * digital			2       1     0     1       0
	 * information		1       6     0     4       0
	 * 
	 *  p(w=information,c=data) = 6/19 = .32 
		p(w=information) = 11/19 = .58
		p(c=data) = 7 /19 .37

	 */
	public void buildMutualInformation(boolean persist){
		int smoothing = 0;
		logger.info("Inicializando construccion de Matrix PPMI");
		SendMessageAsynch.sendMessage(this.workspace,"Creando PPMI Matriz");
		long time_start = 0, time_end=0;
		time_start = System.currentTimeMillis();
		InvertedIndex invertedIndex = new InvertedIndex(this.workspace);
		invertedIndex.loadInvertedIndexDataZipf();
		BigDoubleMatrixFileManager largeMatrixPpmi = new BigDoubleMatrixFileManager(this.workspace);
		//double totalWordDocumentsCount = invertedIndex.getTotaldocumentWordsCount();
		int countMatrixIntersection = this.getCountMatrixIntersection(invertedIndex);
		try {
			largeMatrixPpmi.loadReadWrite(EDataFolder.MATRIX,PPMI_FILE_NAME, 
					invertedIndex.getTermCount(), invertedIndex.getTermCount());
				ArrayList<String> wordsList = invertedIndex.getWordList();
				
				for (int i = 0; i < wordsList.size(); i++) {
					String term1 = wordsList.get(i);
					logger.info("Build PPMI for: " + term1);
					for (int j = 0; j < wordsList.size(); j++) {
						String term2 = wordsList.get(j);
						if(term1.equals(term2)) {
							largeMatrixPpmi.set(i, j, 0);
							continue;
						}
						ArrayList<Integer> intersection = invertedIndex.intersectionDocumentTerms(term1, term2);
						int  term2DocumentIntersectionOcurrences =0;
						//Sumamos las ocurrencias del termino 2 en los documentos intersectos
						for (Integer document : intersection) {
							term2DocumentIntersectionOcurrences =invertedIndex.getTermOcurrencesByDocument(term2, document) + smoothing;
						}
						double ppmi = this.ppmi(term2DocumentIntersectionOcurrences, invertedIndex.getCountByTerm(i), 
								invertedIndex.getCountByTerm(j), countMatrixIntersection);
						//BigDecimal bigDecimal = new BigDecimal(ppmi);
						//bigDecimal = bigDecimal.setScale(5, RoundingMode.HALF_UP);
						//double finalValue = bigDecimal.doubleValue();
						double finalValue = ppmi;
						largeMatrixPpmi.set(i, j, finalValue);
					}
					 
				}
				if(persist){
					largeMatrixPpmi.saveReadable(500);
					largeMatrixPpmi.close();
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Error en buildMutualInformationBigMatrix", e);
		}
		
		time_end = System.currentTimeMillis();
		String finalMessage = "La construccion de la Matrix PPMI tomo "+ 
				( time_end - time_start )/1000 +" segundos" + 
				(( time_end - time_start )/1000)/60 +" minutos";
		logger.info(finalMessage);
		SendMessageAsynch.sendMessage(this.workspace,"Creando PPMI Matriz");
		
	}
	
	/**
	 * Cuenta los terminos de la matrix
	 * @param invertedIndex
	 * @return
	 */
	public int getCountMatrixIntersection(InvertedIndex invertedIndex){
		int matrixCount = 0;
		ArrayList<String> wordsList = invertedIndex.getWordList();
		
		for (int i = 0; i < wordsList.size(); i++) {
			String term1 = wordsList.get(i);
			logger.info("Counting intersection for: " + term1);
			for (int j = 0; j < wordsList.size(); j++) {
				String term2 = wordsList.get(j);
				if(term1.equals(term2)) continue;
				ArrayList<Integer> intersection = invertedIndex.intersectionDocumentTerms(term1, term2);
				matrixCount+=intersection.size();
			}
		}
		return matrixCount;
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
		double ppmi = 0.0;
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
	
	
}
