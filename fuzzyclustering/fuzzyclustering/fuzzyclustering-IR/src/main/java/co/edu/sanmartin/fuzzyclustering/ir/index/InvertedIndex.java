package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.constant.ESystemProperty;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.file.BigMatrixFileManager;

/**
 * Clase encargada de cargar en memoria el indice invertido
 * @author Ricardo Carvajal Salamanca
 *
 */
public class InvertedIndex {
	
	
	//Archivo de indices invertidos
	private String invertedIndexData;
	//Lista de terminos
	private String[] termList;
	//Cantidad de terminos en el diccionario
	private int termCount;
	//Suma de cada termino en todos los documentos
	private int[] termDocumentCount;
	//Total de palabras de todos los documentos del universo
	private int totaldocumentWordsCount;
	//Total documentos procesados
	private int totalDocumentsCount;
	//Matrix Termino Termino en Memoria
	private int[][] termTermMatrix;
	
	public InvertedIndex(){
		this.loadData();
	}
	
	public String getInvertedIndexData() {
		return invertedIndexData;
	}

	public void setInvertedIndexData(String invertedIndexData) {
		this.invertedIndexData = invertedIndexData;
	}

	public String[] getTermList() {
		return termList;
	}

	public void setTermList(String[] termList) {
		this.termList = termList;
	}

	public int getTermCount() {
		return termCount;
	}

	public void setTermCount(int termCount) {
		this.termCount = termCount;
	}

	public int getTotaldocumentWordsCount() {
		return totaldocumentWordsCount;
	}

	public void setTotaldocumentWordsCount(int totaldocumentWordsCount) {
		this.totaldocumentWordsCount = totaldocumentWordsCount;
	}

	public int[] getTermDocumentCount() {
		return termDocumentCount;
	}

	public void setTermDocumentCount(int[] termDocumentCount) {
		this.termDocumentCount = termDocumentCount;
	}

	
	public int getTotalDocumentsCount() {
		return totalDocumentsCount;
	}

	public void setTotalDocumentsCount(int totalDocumentsCount) {
		this.totalDocumentsCount = totalDocumentsCount;
	}
	
	public int[][] getTermTermMatrix() {
		if(this.termTermMatrix==null || this.termTermMatrix.length==0){
			this.buildTermTermMatrix(false, true);
		}
		
		return this.termTermMatrix;
	}


	/**
	 * Retorna la informacion del archivo de indices invertidos
	 * @return
	 */
	public void loadData(){
		StringBuilder data = new StringBuilder();
		PersistenceFacade persistenceFacade = PersistenceFacade.getInstance();
		Collection<DocumentDTO> fileList = persistenceFacade.getFileList(EDataFolder.INVERTED_INDEX);
		for (DocumentDTO fileName : fileList) {
			data.append(persistenceFacade.readFile(fileName.getCompletePath())); 
		}
		if(data.length()>0){
			this.invertedIndexData = data.toString();
			this.termList =this.invertedIndexData.split("\r\n");
			this.termCount = this.termList.length;
			this.countTotalInvertedIndexTerms();
			this.countTermsDocument();
			this.countDocuments();
		}
	}
	
	/**
	 * Metodo encargado de contar la cantidad de terminos de todo el indice
	 */
	private void countTotalInvertedIndexTerms(){
		int totalTerms = 0;
		for (int i = 0; i < termCount; ++i) {
			String termData = termList[i];
			String[] row = termData.split(";");
			totalTerms+=Integer.parseInt(row[2]);
		}
		this.totaldocumentWordsCount = totalTerms;
		
	}
	
	/**
	 * Metodo encargado de contar la cantidad de terminos en todos los terminos
	 * @param termId
	 * @return
	 */
	private void countTermsDocument(){
		this.termDocumentCount = new int[this.termCount];
		for (int i = 0; i < termCount; ++i) {
			String termData = termList[i];
			String[] row = termData.split(";");
			termDocumentCount[i]=Integer.parseInt(row[2]);
		}
	}
	
	/**
	 * Cuenta los documentos que han generado el indice invertidos
	 */
	private void countDocuments(){
		this.termDocumentCount = new int[this.termCount];
		int maxDocument = 0;
		for (int i = 0; i < termCount; ++i) {
			String termData = termList[i];
			String[] row = termData.split(";");
			String[] documents = row[1].split(",");
	    	//Comienza en 1 por el el mismo termino no se tiene en cuenta
	    	for (int j = 0; j < documents.length; j++) {
	    		int documentId = Integer.parseInt(documents[j]);
	    		if(documentId>maxDocument){
	    			maxDocument=documentId;
	    		}
			}
		}
		this.totalDocumentsCount = maxDocument;
	}
	

	/**
	 * Retorna la matrix  documento termino a partir del indice invertido
	 * Filas = documentos, Columnas = Terminos
	 * @param datos del indice invertido
	 * @return
	 */
	public int[][] getTermDocumentMatrix(){
		int[][] termArray =  new int[this.totalDocumentsCount+1][termCount];
		for (int i = 0; i < termCount; ++i) {
			String termData = termList[i];
			String[] row = termData.split(";");
	    	String[] documents = row[1].split(",");
	    	for (int j = 0; j < documents.length; j++) {
	    		int documentId = Integer.parseInt(documents[j]);
				termArray[documentId][i]=termArray[documentId][i]+1;
			}
		}
		this.saveMatrix(termArray,"termdocument.txt");
	    return termArray;	
 	
	}
	
	/**
	 * Retorna la matrix termino termino a partir de la matrix termino utilizando mapeo de archivos en memoria
	 * @param datos del indice invertido
	 * @return
	 * @throws IOException 
	 */
	public void buildTermTermBigMatrix(boolean persist, boolean refresh) throws IOException{
		BigMatrixFileManager largeMatrix = 
				new BigMatrixFileManager();
		largeMatrix.loadReadWrite(EDataFolder.MATRIX,"termterm.txt", termCount, termCount);
		for (int i = 0; i < termCount; ++i) {
			String termData = termList[i];
			String[] row = termData.split(";");
			String[] documents = row[1].split(",");
	    	for (int j = 0; j < documents.length; j++) {
	    		int documentId = Integer.parseInt(documents[j]);
	    		ArrayList<int[]> termsByDocument = this.queryTermsByDocument(documentId);
	    		for (int[] termDocument : termsByDocument) {
	    			int relatedTerm = termDocument[0];
	    			if(i!=relatedTerm){ 
	    				Long value = Long.valueOf(termDocument[1]);
	    				largeMatrix.set(i, relatedTerm, value);
	    			}
				}
	    	}
		}
		if(persist){
			//this.saveMatrix(termtermMatrix,"termterm.txt");
			largeMatrix.close();
		}
	}
	/**
	 * Retorna la matrix termino termino a partir de la matrix termino
	 * documento.
	 * @param datos del indice invertido
	 * @return
	 */
	public void buildTermTermMatrix(boolean persist, boolean refresh){
		
		int[][] termtermMatrix = new int[termCount][termCount];
		for (int i = 0; i < termCount; ++i) {
			String termData = termList[i];
			String[] row = termData.split(";");
			String[] documents = row[1].split(",");
	    	for (int j = 0; j < documents.length; j++) {
	    		int documentId = Integer.parseInt(documents[j]);
	    		ArrayList<int[]> termsByDocument = this.queryTermsByDocument(documentId);
	    		for (int[] termDocument : termsByDocument) {
	    			int relatedTerm = termDocument[0];
	    			if(i!=relatedTerm){ 
	    				termtermMatrix[i][relatedTerm]= termDocument[1];
	    			}
				}
	    	}
		}
		if(persist){
			this.saveMatrix(termtermMatrix,"termterm1.txt");
		}
		this.termTermMatrix = termtermMatrix;
	}

	/**
	 * Retorna el listado de documentos que contiene el termino especificado
	 * @return
	 */
	public ArrayList<int[]> queryTermsByDocument(int queryDocumentId){
		ArrayList<int[]> terms = new ArrayList<int[]>();
		for (int i = 0; i < termCount; ++i) {
			String termData = termList[i];
			String[] row = termData.split(";");
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
				termResult = new int[2];
	    	}
	    	
		}
		return terms;
	}
	
	/**
	 * Almacena la matriz en disco
	 **/
	public void saveMatrix(int[][] matrix, String fileName){
		System.out.print("Init savematrix");
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
	
	public void saveBigMatrix(int[][] matrix, String fileName){
		long start = System.nanoTime();
		
		try {
			BigMatrixFileManager largeMatrix = 
					new BigMatrixFileManager();
			largeMatrix.loadReadWrite(EDataFolder.MATRIX,fileName, matrix.length, matrix[0].length);
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					largeMatrix.set(i, j, matrix[i][j]);
				}
			}
			largeMatrix.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long time = System.nanoTime() - start;
		System.out.print("Time"+ time);
	}
	
	/**
	 * Almacena la matriz en disco
	 **/
	public void saveMatriz(double[][] matrix, String fileName){
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
	
	/**
	 * Retorna la cantidad de cada termino en los documentos del universo
	 * @param termId identificacion del termino
	 * @return
	 */
	public int getCountByTerm(int termId){
		return termDocumentCount[termId];
		
	}
	
}
