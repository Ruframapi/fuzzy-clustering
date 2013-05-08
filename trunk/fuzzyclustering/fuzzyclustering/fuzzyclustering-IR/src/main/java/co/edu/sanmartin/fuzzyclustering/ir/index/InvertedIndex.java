package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;

import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.DocumentDTO;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.facade.SendMessageAsynch;
import co.edu.sanmartin.persistence.file.BigDoubleMatrixFileManager;
import co.edu.sanmartin.persistence.file.BigIntegerMatrixFileManager;

/**
 * Clase encargada de cargar en memoria el indice invertido
 * @author Ricardo Carvajal Salamanca
 *
 */
public class InvertedIndex {
	public static String TERM_TERM_FILENAME="termterm.dat";
	public static String TERM_DOCUMENT_FILENAME="termdocument.dat";
	Logger logger = Logger.getLogger("InvertedIndex");
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
	
	private ArrayList<String> wordList;
	private WorkspaceDTO workspace;

	public InvertedIndex(WorkspaceDTO workspace){
		this.workspace = workspace;
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
	
	

	public ArrayList<String> getWordList() {
		return wordList;
	}

	/*
	 * Crea el indice invertido utilizando Thread Pool 
	 * @param minTermsOcurrences cantidad minima de ocurrencias que debe tener un termino para almacenarse 
	 * en el indice invertido
	 */
	public void createInvertedIndex(int minTermsOcurrences, int documentsAmount){
		InvertedIndexThreadPool threadPool = new InvertedIndexThreadPool(workspace,minTermsOcurrences, documentsAmount);
		Thread thread=new Thread(threadPool);
		thread.start();
	}

	/**
	 * Retorna la informacion del archivo de indices invertidos
	 * @return
	 */
	public void loadInvertedIndexData(){

		StringBuilder data = new StringBuilder();
		Collection<DocumentDTO> fileList = workspace.getPersistence().getFileList(EDataFolder.INVERTED_INDEX);
		for (DocumentDTO fileName : fileList) {
			data.append(workspace.getPersistence().readFile(EDataFolder.INVERTED_INDEX,fileName.getName())); 
		}
		if(data.length()>0){
			this.invertedIndexData = data.toString();
			this.termList =this.invertedIndexData.split(System.getProperty("line.separator"));
			this.termCount = this.termList.length;
			this.loadWordList();
			this.countTotalInvertedIndexTerms();
			this.countTermsDocument();
			this.countDocuments();
		}
	}
	
	private void loadWordList(){
		this.wordList = new ArrayList<String>();
		for (int i = 0; i < termList.length; i++) {
			this.wordList.add(this.termList[i].split("\t")[0]);
		}
	}
	
	/**
	 * Retorna la matrix termino termino a partir de la matrix de terminos utilizando mapeo de archivos en memoria
	 * @param datos del indice invertido
	 * @return
	 * @throws IOException 
	 */
	public void createTermTermBigMatrix(boolean persist) throws IOException{
		logger.info("Inicializando construccion de Matrix Termino Termino");
		SendMessageAsynch.sendMessage(this.workspace,"Creando Matriz de Terminos");
		long time_start = 0, time_end=0;
		time_start = System.currentTimeMillis();
		//Se almacena en memoria los punteros de los documentos para no volver a recorrer la lista
		HashMap<Integer,ArrayList<int[]>> documentsPointers = new HashMap<Integer,ArrayList<int[]>>();
		try{	
			this.loadInvertedIndexData();
			BigIntegerMatrixFileManager largeMatrix = 
					new BigIntegerMatrixFileManager(this.workspace);
			largeMatrix.loadReadWrite(EDataFolder.MATRIX,TERM_TERM_FILENAME, termCount, termCount);
			for (int i = 0; i < termCount; ++i) {
				String termData = termList[i];
				String[] row = termData.split("\t");
				logger.debug("Construyendo vector para termino:"  + row[0] );
				String[] documents = row[1].split(",");
				for (int j = 0; j < documents.length; j++) {
					int documentId = Integer.parseInt(documents[j]);
					ArrayList<int[]> termsByDocument = null;
					if(documentsPointers.get(documentId)==null){
						documentsPointers.put(documentId, this.queryTermsByDocument(documentId));
					}
					termsByDocument = documentsPointers.get(documentId);
					
					for (int[] termDocument : termsByDocument) {
						documentsPointers.put(documentId, termsByDocument);
						int relatedTerm = termDocument[0];
						if(i!=relatedTerm){ 
							int value = termDocument[1];
							largeMatrix.set(i, relatedTerm, value);
						}
					}
				}
			}
			if(persist){
				largeMatrix.close();
			}
		}catch(Throwable e){
			logger.error("Error in createTermTermBigMatrix",e);
		}
		time_end = System.currentTimeMillis();
		String finalMessage = "La construccion de la Matrix Termino Termino tomo "+ 
				( time_end - time_start )/1000 +" segundos" + 
				(( time_end - time_start )/1000)/60 +" minutos";
		logger.info(finalMessage);
		SendMessageAsynch.sendMessage(this.workspace,finalMessage);
		
	}

	/**
	 * Carga la matrix termino termino utilizando MappedFiles
	 */
	public void loadTermTermBigMatrix(){
		long start = System.nanoTime();

		try {
			BigDoubleMatrixFileManager largeMatrix = 
					new BigDoubleMatrixFileManager(this.workspace);
			largeMatrix.loadReadOnly(EDataFolder.MATRIX,TERM_TERM_FILENAME);
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < largeMatrix.width(); j++) {
					System.out.print(largeMatrix.get(i, j)+ " ");
				}
				System.out.println();
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
	 * Metodo encargado de contar la cantidad de terminos de todo el indice
	 */
	private void countTotalInvertedIndexTerms(){
		int totalTerms = 0;
		for (int i = 0; i < termCount; ++i) {
			String termData = termList[i];
			String[] row = termData.split("\t");
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
			String[] row = termData.split("\t");
			termDocumentCount[i]=Integer.parseInt(row[2]);
		}
	}

	/**
	 * Cuenta los documentos que han generado el indice invertidos
	 */
	private void countDocuments(){

		int maxDocument = 0;
		for (int i = 0; i < termCount; ++i) {
			String termData = termList[i];
			String[] row = termData.split("\t");
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
	public void createTermDocumentBigMatrix(boolean persist) throws Exception{
		this.loadInvertedIndexData();
		BigIntegerMatrixFileManager largeMatrix = 
				new BigIntegerMatrixFileManager(this.workspace);
		largeMatrix.loadReadWrite(EDataFolder.MATRIX,TERM_DOCUMENT_FILENAME, termCount,this.totalDocumentsCount+1);
		for (int i = 0; i < termCount; ++i) {
			String termData = termList[i];
			String[] row = termData.split("\t");
			String[] documents = row[1].split(",");
			for (int j = 0; j < documents.length; j++) {
				int documentId = Integer.parseInt(documents[j]);
				//Filas terminos, columnas documentos
				largeMatrix.set(i, documentId, largeMatrix.get(i,documentId)+1);
			}
		}
		if(persist){
			largeMatrix.close();
		}
	}

	/**
	 * Retorna la matrix  documento termino a partir del indice invertido
	 * Filas = documentos, Columnas = Terminos
	 * @param datos del indice invertido
	 * @return
	 */
	@Deprecated
	public void createTermDocumentMatrix(){
		this.loadInvertedIndexData();
		int[][] termArray =  new int[this.totalDocumentsCount+1][termCount];
		for (int i = 0; i < termCount; ++i) {
			String termData = termList[i];
			String[] row = termData.split("\t");
			String[] documents = row[1].split(",");
			for (int j = 0; j < documents.length; j++) {
				int documentId = Integer.parseInt(documents[j]);
				termArray[documentId][i]=termArray[documentId][i]+1;
			}
		}
		this.saveMatrix(termArray, "termdocument.txt");
	}

	

	/**
	 * Retorna el listado de documentos que contiene el termino especificado
	 * @return
	 */
	public ArrayList<int[]> queryTermsByDocument(int queryDocumentId){
		ArrayList<int[]> terms = new ArrayList<int[]>();
		for (int i = 0; i < termCount; ++i) {
			String termData = termList[i];
			String[] row = termData.split("\t");
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
	 * Retorna la cantidad de cada termino en los documentos del universo
	 * @param termId identificacion del termino
	 * @return
	 */
	public int getCountByTerm(int termId){
		return termDocumentCount[termId];
	}

	/**
	 * Retorna la matrix termino termino a partir de la matrix termino
	 * documento.
	 * @param datos del indice invertido
	 * @return
	 * @deprecated se cambia para construir los archivos utilizando mapped files
	 */
	@Deprecated
	public void buildTermTermMatrix(boolean persist ){
		this.loadInvertedIndexData();
		int[][] termtermMatrix = new int[termCount][termCount];
		for (int i = 0; i < termCount; ++i) {
			String termData = termList[i];
			String[] row = termData.split("\t");
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
	 * Almacena la matriz en disco como archivo plano
	 **/
	@Deprecated
	public void saveMatrix(int[][] matrix, String fileName){
		System.out.print("Init savematrix");
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < matrix.length; i++) {;
		for (int j = 0; j < matrix[i].length; j++) {
			data.append(matrix[i][j]);
			data.append(",");
		}
		data.append(System.getProperty("line.separator"));
		}
		this.workspace.getPersistence().writeFile(EDataFolder.MATRIX, 
				fileName, data.toString());
	}

	/**
	 * Almacena la matriz en disco
	 **/
	@Deprecated
	public void saveDoubleMatrix(double[][] matrix, String fileName){
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < matrix.length; i++) {;
		for (int j = 0; j < matrix[i].length; j++) {
			data.append(matrix[i][j]);
			data.append(",");
		}
		data.append(System.getProperty("line.separator"));
		}
		this.workspace.getPersistence().writeFile( EDataFolder.MATRIX, 
				fileName, data.toString());
	}

	/**
	 * Almacena la matrix en disco utilizando mapeo de archivos
	 * @param matrix
	 * @param fileName
	 */
	@Deprecated
	public void saveBigMatrix(int[][] matrix, String fileName){
		long start = System.nanoTime();

		try {
			BigDoubleMatrixFileManager largeMatrix = 
					new BigDoubleMatrixFileManager(this.workspace);
			largeMatrix.loadReadWrite(EDataFolder.MATRIX,fileName, matrix.length, matrix[0].length);
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					largeMatrix.set(i, j, matrix[i][j]);
				}
			}
			largeMatrix.close();
		} catch (IOException e) {
			logger.error(e);
		}
		long time = System.nanoTime() - start;
		System.out.print("Time"+ time);
	}
	
	@Deprecated
	public int[][] getTermTermMatrix() {
		this.loadInvertedIndexData();
		if(this.termTermMatrix==null || this.termTermMatrix.length==0){
			//this.buildTermTermMatrix(false);
		}
		return this.termTermMatrix;
	}

}
