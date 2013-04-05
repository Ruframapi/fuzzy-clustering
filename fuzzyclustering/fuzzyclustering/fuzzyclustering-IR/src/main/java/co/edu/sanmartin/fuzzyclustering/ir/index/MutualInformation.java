package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.log4j.Logger;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.file.BigMatrixFileManager;

/**
 * Clase encargada de generar una matrix con el valor del grado
 * de relacion entre terminos que se encuentran co-ocurrentes en el texto
 * utilizando el metodo Pointwise Mutual Infomration
 * @author Ricardo
 *
 */
public class MutualInformation {
	Logger logger = Logger.getLogger("MutualInformation");
	public void buildMutualInformationMatrix(){
		InvertedIndex invertedIndex = new InvertedIndex();
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
		invertedIndex.saveMatrix(ppmiData, "ppmi.txt");
	}
	
	/**
	 * Crea la matrix PPMI a partir del conjunto de datos de la matrix Termino Termino y
	 * el indice invertido.
	 */
	public void buildMutualInformationBigMatrix(){
		InvertedIndex invertedIndex = new InvertedIndex();
		invertedIndex.loadData();
		BigMatrixFileManager largeMatrix = new BigMatrixFileManager();
		double[][] ppmiData = new double[invertedIndex.getTermCount()][invertedIndex.getTermCount()]; 
		double totalWordDocumentsCount = invertedIndex.getTotaldocumentWordsCount();
		try {
			largeMatrix.loadReadOnly(EDataFolder.MATRIX,"termterm.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < largeMatrix.height(); i++) {
			for (int j = 0; j < largeMatrix.width(); j++) {
				int termCount = new Double(largeMatrix.get(i, j)).intValue();
				double ppmi = this.ppmi(termCount, invertedIndex.getCountByTerm(i), 
										invertedIndex.getCountByTerm(j), totalWordDocumentsCount);
				
				BigDecimal bigDecimal = new BigDecimal(ppmi);
				bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
				ppmiData[i][j]=bigDecimal.doubleValue();
				
			}	
		}

		invertedIndex.saveMatrix(ppmiData, "ppmi.txt");
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
	
}
