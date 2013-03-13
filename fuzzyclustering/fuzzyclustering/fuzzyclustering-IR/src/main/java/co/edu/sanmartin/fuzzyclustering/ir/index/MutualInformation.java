package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Clase encargada de generar una matrix con el valor del grado
 * de relacion entre terminos que se encuentran co-ocurrentes en el texto
 * utilizando el metodo Pointwise Mutual Infomration
 * @author Ricardo
 *
 */
public class MutualInformation {

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
		invertedIndex.saveMatriz(ppmiData, "ppmi.txt");
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
			double ppmiTerm_ij=termCount/totalWordDocumentsCount;
			double ppmiTermi = termCount_i/totalWordDocumentsCount;
			double ppmiTermj = termCount_j/totalWordDocumentsCount;
			ppmi = Math.log((ppmiTerm_ij)/(ppmiTermi*ppmiTermj))/Math.log(2);
		}
		return ppmi;
	}
	
}
