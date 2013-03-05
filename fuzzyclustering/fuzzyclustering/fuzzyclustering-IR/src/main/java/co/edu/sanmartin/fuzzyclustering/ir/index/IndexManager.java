package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.constant.ESystemProperty;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;

/**
 * Clase encargada de cargar en memoria el indice invertido
 * @author Ricardo
 *
 */
public class IndexManager {

	/**
	 * Retorna la informacion del archivo de indices invertidos
	 * @return
	 */
	public String getInvertedIndexData(){
		StringBuilder data = new StringBuilder();
		PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
		Collection<String> fileList = persistenceFacade.getFileList(EDataFolder.INVERTED_INDEX);
		for (String fileName : fileList) {
			data.append(persistenceFacade.readFile(fileName)); 
		}
		return data.toString();
	}
	
	
	
	
	/**
	 * Retorna la matrix termino documento a partir del indice invertido
	 * @return
	 */
	public int[][] getTermDocumentMatrix(){
		int[][] termArray = null;
		String data = this.getInvertedIndexData();
		String[] termList = data.split("\r\n");
		
		termArray = new int[100][termList.length];
		for (int i = 0; i < termList.length; ++i) {
			String termData = termList[i];
			String[] row = termData.split(";");
			String word = row[0];
	    	String[] documents = row[1].split(",");
	    	for (int j = 0; j < documents.length; j++) {
	    		int documentId = Integer.parseInt(documents[j]);
				termArray[documentId][i]=termArray[documentId][i]+1;
				int a = 2;
			}
		}
		this.saveMatriz(termArray,"termdocument.txt");
	    return termArray;	
 	
	}
	
	/**
	 * Retorna la matrix termino termino a partir de la matrix termino
	 * documento.
	 * @return
	 */
	public int[][] getTermTermMatrix(){
		String data = this.getInvertedIndexData();
		String[] termList = data.split("\r\n");
		int[][] termtermMatrix = new int[termList.length][termList.length];
		for (int i = 0; i < termList.length; ++i) {
			String termData = termList[i];
			String[] row = termData.split(";");
			String[] documents = row[1].split(",");
	    	for (int j = 0; j < documents.length; j++) {
	    		ArrayList<int[]> termsByDocument = this.queryTermsByDocument(j,	data);
	    		for (int[] termDocument : termsByDocument) {
	    			int relatedTerm = termDocument[0];
	    			//i terrmino - relatedTerm Termino relacionado = cantidad 
					termtermMatrix[i][relatedTerm]= termDocument[1];
				}
	    	}
		}
		this.saveMatriz(termtermMatrix,"termterm.txt");
		return termtermMatrix;
	}

	/**
	 * Retorna el listado de documentos que contiene el termino especificado
	 * @return
	 */
	public ArrayList<int[]> queryTermsByDocument(int queryDocumentId, String invertedIndexData){
		ArrayList<int[]> terms = new ArrayList<int[]>();
		String[] termList = invertedIndexData.split("\r\n");
		for (int i = 0; i < termList.length; ++i) {
			String termData = termList[i];
			String[] row = termData.split(";");
			String word = row[0];
			int[] termResult = new int[2];
			int indexTerm = i;
			int count = 0;
			String[] documents = row[1].split(",");
	    	for (int j = 0; j < documents.length; j++) {
	    		int documentId = Integer.parseInt(documents[j]);
	    		if(documentId == queryDocumentId){
	    			count++;
	    		}
			}
	    	if(count>0){
	    		termResult[0]=indexTerm;
	    		termResult[1]=count;
		    	terms.add(termResult);
				count = 0;
	    	}
	    	
		}
		return terms;
	}
	
	/**
	 * Almacena la matrix en disco
	 **/
	
	public void saveMatriz(int[][] matrix, String fileName){
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < matrix.length; i++) {;
			for (int j = 0; j < matrix[i].length; j++) {
				data.append(matrix[i][j]);
				data.append(",");
			}
			data.append("\r\n");
		}
		PersistenceFacade.getInstance().writeFile(EDataFolder.MATRIX, 
													fileName, data.toString());
	}
	
}
