package co.edu.sanmartin.fuzzyclustering.ir.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	//LinkedHashMap InvertedIndex se utiliza linked para manter el orden de insercion
	private LinkedHashMap<String,Integer[]> invertedIndexMap; 


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
	
	

	public HashMap<String, Integer[]> getInvertedIndexMap() {
		return invertedIndexMap;
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
	public void loadInvertedIndexOriginal(){
		this.loadInvertedIndex("invertedIndex.txt");
	}

	/**
	 * Retorna la informacion del archivo de indices invertidos
	 * @return
	 */
	public void loadInvertedIndexDataZipf(){
		this.loadInvertedIndex("invertedIndexZipf.txt");
	}

	/**
	 * Retorna la informacion del archivo de indices invertidos
	 * @return
	 */
	public void loadInvertedIndex(String filename){

		StringBuilder data = new StringBuilder();
		data.append(workspace.getPersistence().readFile(EDataFolder.INVERTED_INDEX,filename)); 

		this.termList =data.toString().split(System.getProperty("line.separator"));

		if(termList.length>0){
			this.termCount = this.termList.length;	
		}
		this.loadWordList();
		this.countTotalInvertedIndexTerms();
		this.countTermsDocument();
		this.countDocuments();

	}

	/**
	 * Crea el indice invertido de acuerdo a los puntos de corte segun la ley de zipf
	 * @param cutOnPercent
	 * @param cutOffPercent
	 */
	public void reducedZipfInvertedIndex(int cutOnPercent,int cutOffPercent, int minTermOcurrences){
		this.loadInvertedIndexOriginal();
		List<String> dataList = new ArrayList<String>();
		HashMap<String,Integer> filterList = new HashMap<String,Integer>();
		for (int i = 0; i < termList.length; i++) {
			filterList.put(termList[i],Integer.parseInt(termList[i].split("\t")[2]));
		}
		//Ordenamos el HashMap
		filterList = this.sortHashMapByValue(filterList);
		//Retornamos el indice filtrador
		Iterator it = filterList.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			dataList.add(e.getKey().toString());
		}

		if(cutOnPercent!=0 || cutOffPercent!=0){
			Double transitionPoint = this.getTransitionPoint(minTermOcurrences);
			//Cantidad de terminos a la izquierda(Los mas frecuentes)
			int cutOnQuantity = 0;
			int cutOffQuantity = 0;

			//Calculamos la cantidad de terminos menor al punto de transicion
			for (int i = 0; i < this.termDocumentCount.length; i++) {
				if(this.termDocumentCount[i]<transitionPoint){
					cutOnQuantity++;
				}
			}

			//Calculamos la cantidad de terminos menor al punto de transicion
			for (int i = 0; i < this.termDocumentCount.length; i++) {
				if(this.termDocumentCount[i]>transitionPoint){
					cutOffQuantity++;
				}
			}	

			//Hallamos el cutOn y CutOff
			int cutOnIndex =cutOnQuantity-(cutOnPercent*cutOnQuantity/100);
			int cutOffIndex=termCount-((100-cutOffPercent)*cutOffQuantity/100);

			List<String> filteredList = dataList.subList(cutOnIndex, cutOffIndex);
			StringBuilder stringBuilder = new StringBuilder();
			for (String string : filteredList) {
				stringBuilder.append(string);
				stringBuilder.append(System.getProperty("line.separator"));
			}
			this.workspace.getPersistence().writeFile(EDataFolder.INVERTED_INDEX, "invertedIndexZipf.txt", stringBuilder.toString());

		}
	}



	/**
	 * Retorna una HashMap Ordenado por valores
	 * @param map
	 * @return
	 */
	public  HashMap<String, Integer> sortHashMapByValue(HashMap<String, Integer> map) {
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

			public int compare(Map.Entry<String, Integer> m1, Map.Entry<String, Integer> m2) {
				return (m2.getValue()).compareTo(m1.getValue());
			}
		});

		HashMap<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	/**
	 * Determina el punto de transicion del indice invertido osea el punto intermedio 
	 * en donde se encuentran los terminos mas relevantes segun Zipf
	 * @See Fórmula de Booth para el cálculo del punto de transición
	 * http://ccdoc-tecnicasrecuperacioninformacion.blogspot.com/2012/11/el-proceso-de-indexacion.html
	 */
	public double  getTransitionPoint(int minTermOcurrences){
		//Extraemos la  cantidd de terminos que aparecen una vez o el valor de minTermOcurrences
		int oneOcurrences = this.getTermCountByOcurrences(minTermOcurrences);
		double transitionPoint = (Math.sqrt(1+8*oneOcurrences))-1/2;
		return transitionPoint;
	}

	/**
	 * Retorna la cantidad de terminos con una cantidad de ocurrencias especificada
	 * @param ocurrences La cantidad de ocurrencias que tiene el termino
	 */
	public int getTermCountByOcurrences(int ocurrences){
		int termCountByOcurrences = 0;
		for (int i = 0; i < this.termDocumentCount.length; i++) {
			if(this.termDocumentCount[i]==ocurrences){
				termCountByOcurrences++;
			}
		}
		return termCountByOcurrences;

	}

	/**
	 * Metodo encargado de retornar la cantidad de ocurrencias de un termino en un documento
	 * @param term termino
	 * @param documentId documento
	 * @return
	 */
	public int getTermOcurrencesByDocument(String term, int documentId){
		int ocurrences = 0;
		Integer[] documentsByTerm = this.invertedIndexMap.get(term);
		
		for (Integer document : documentsByTerm) {
			if(document==documentId){
				ocurrences++;
			}
		}
		return ocurrences;
	}


	/**
	 * Carga la lista con las palabras
	 */
	private void loadWordList(){
		this.wordList = new ArrayList<String>();
		this.invertedIndexMap = new LinkedHashMap<String, Integer[]>();
		for (int i = 0; i < termList.length; i++) {
			String[] termListSplit =this.termList[i].split("\t");
			String term =termListSplit[0];
			String[] documents = termListSplit[1].split(",");
			Integer[] integerArray = new Integer[documents.length];
			for (int j = 0; j < integerArray.length; j++) {
				integerArray[j]=Integer.parseInt(documents[j]);
			}
			this.invertedIndexMap.put(term, integerArray);
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
			this.loadInvertedIndexDataZipf();
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
		this.loadInvertedIndexDataZipf();
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
	 * Retorna los documentos en los que se intersectan dos terminos
	 * @see http://nlp.stanford.edu/IR-book/html/htmledition/processing-boolean-queries-1.html
	 * 
	 */
	public ArrayList<Integer> intersectionDocumentTerms(String term1, String term2){
		ArrayList<Integer> intersection = new ArrayList<Integer>();
		Integer[] documentsTerm1 = this.invertedIndexMap.get(term1);
		Integer[] documentsTerm2 = this.invertedIndexMap.get(term2);
		if(documentsTerm1!=null && documentsTerm2!=null){
			int pointerTerm1 = 0;
			int pointerTerm2 = 0;
			do{
				if(documentsTerm1[pointerTerm1].equals(documentsTerm2[pointerTerm2])){
					intersection.add(documentsTerm1[pointerTerm1]);
					pointerTerm1++;
					pointerTerm2++;
				}
				else {
					if (documentsTerm1[pointerTerm1]<documentsTerm2[pointerTerm2]){
						pointerTerm1++;
					}
					else{
						pointerTerm2++;
					}
				}
			}
			while(pointerTerm1<documentsTerm1.length && pointerTerm2<documentsTerm2.length);
		}
		return  intersection;
	}


	/**
	 * Retorna la matrix  documento termino a partir del indice invertido
	 * Filas = documentos, Columnas = Terminos
	 * @param datos del indice invertido
	 * @return
	 */
	@Deprecated
	public void createTermDocumentMatrix(){
		this.loadInvertedIndexDataZipf();
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
		this.loadInvertedIndexDataZipf();
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
		this.loadInvertedIndexDataZipf();
		if(this.termTermMatrix==null || this.termTermMatrix.length==0){
			//this.buildTermTermMatrix(false);
		}
		return this.termTermMatrix;
	}

}
