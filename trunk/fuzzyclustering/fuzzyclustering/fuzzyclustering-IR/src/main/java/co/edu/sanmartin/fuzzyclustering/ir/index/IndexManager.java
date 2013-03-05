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
	
	
	
	
	public int[][] getTermTermMatrix(){
		int[][] termArray = null;
		String data = this.getInvertedIndexData();
		String[] termList = data.split("\n");
		
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
		this.saveMatriz(termArray);
	    return termArray;	
	    	
	    	
	}
	/**
	 * Retorna la matriz termino documento en donde las columnas corresponden
	 * a los terminos y las filas a los documentos que los contienen
	 * @return
	 */
	public ArrayList<Integer[]> getTermDocumentMatrix1(){
		//[filas][columnas]
		ArrayList<Integer[]> termArray = new ArrayList<Integer[]>();
		String data = this.getInvertedIndexData();
		String[] termList = data.split("\n");
		//TODO definir la cantidad de documentos por defecto. Puede ser un valor fijo
		// o revisar si es posible que sea autoextensible
		for (String string : termList) {
			String[] termData = termList[0].split(";");
	    	String[] documents = termData[1].split(",");
	    	
	    	Integer[] vectorTerm = null;
	    	for (int j = 0; j < documents.length; j++) {
	    		Integer document =  Integer.parseInt(documents[j]);
	    		Integer[] documentVector = null;
	    		
	    		if(!termArray.contains(document)){
	    			documentVector = new Integer[termList.length];
	    		}
    		
				documentVector[document] = documentVector[document]+1;
			}
	    	termArray.add(vectorTerm);
		}
		
		
		//this.saveMatriz(termArray);
	    
		return termArray;
		
	}
	
	public void saveMatriz(int[][] matrix){
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < matrix.length; i++) {;
			for (int j = 0; j < matrix[i].length; j++) {
				data.append(matrix[i][j]);
				data.append("-");
			}
			data.append("\r\n");
		}
		PersistenceFacade.getInstance().writeFile(EDataFolder.INVERTED_INDEX, "termDocument.txt", data.toString());
	}
	
}
