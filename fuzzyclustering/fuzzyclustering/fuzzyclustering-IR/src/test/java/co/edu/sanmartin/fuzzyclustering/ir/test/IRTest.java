package co.edu.sanmartin.fuzzyclustering.ir.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;
import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.fuzzyclustering.ir.index.MutualInformation;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.Cleaner;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.Stemmer;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;
import co.edu.sanmartin.persistence.file.BigDoubleMatrixFileManager;
import co.edu.sanmartin.persistence.file.BigIntegerMatrixFileManager;

public class IRTest {

	private WorkspaceDTO workspace = WorkspaceFacade.getWorkspace("noticias_economicas");
	public IRTest() {
		BasicConfigurator.configure();
	}

	@Test
	public void cleanTextLexicon(){
		Cleaner cleaner = new Cleaner(this.workspace);
		cleaner.deleteLexiconStopWords("a ante bajo co contra Ricardo Carvajal Estuvo en un lugar muy importante");
	}
	
	
	@Test
	public void invertedIndexTest() {
		workspace = WorkspaceFacade.getWorkspace("prueba_ppmi");
		InvertedIndexThreadPool threadPool = new InvertedIndexThreadPool(workspace,0,0);
		threadPool.run();
		//Thread thread = new Thread(threadPool);
		//thread.start();
	}

	
	@Test
	public void termDocumentBigMatrixTest(){
		InvertedIndex invertedIndex = new InvertedIndex(workspace);
		try {
			invertedIndex.createTermDocumentBigMatrix(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void termTermMatrixTest(){
		InvertedIndex indexManager = new InvertedIndex(workspace);
		indexManager.getTermTermMatrix();
	}
	@Test
	public void buildTermTermMatrixTest(){
		InvertedIndex indexManager = new InvertedIndex(workspace);
		indexManager.buildTermTermMatrix(true);
	}
	@Test
	public void buildTermTermBigMatrixTest(){
		InvertedIndex invertedIndex = new InvertedIndex(workspace);
		try {
			invertedIndex.createTermTermBigMatrix(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Test
	public void buildMutualInformationBigMatrixTest(){
		MutualInformation mutualInformation = new MutualInformation(workspace);
		try {
			mutualInformation.buildMutualInformationBigMatrix(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void loadTermTermBigMatrixTest(){
		long start = System.nanoTime();
		
		try {
			BigIntegerMatrixFileManager largeMatrix = 
					new BigIntegerMatrixFileManager(workspace);
			largeMatrix.loadReadOnly(EDataFolder.MATRIX,"termterm.txt");
			int[][] termtermMatrix = new int[largeMatrix.height()][largeMatrix.width()];
			for (int i = 0; i < largeMatrix.height(); i++) {
				for (int j = 0; j < largeMatrix.width(); j++) {
					termtermMatrix[i][j] = largeMatrix.get(i, j);
					//System.out.print(largeMatrix.get(i, j)+ " ");
				}
				//System.out.println();
			}
			largeMatrix.close();
			this.saveMatrixInteger(termtermMatrix, "BigMatrixTermTermLoad.txt",100);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long time = System.nanoTime() - start;
		System.out.print("Time"+ time);
	}
	
	@Test
	public void loadTermDocumentBigMatrixTest(){
		long start = System.nanoTime();
		
		try {
			BigIntegerMatrixFileManager largeMatrix = 
					new BigIntegerMatrixFileManager(workspace);
			largeMatrix.loadReadOnly(EDataFolder.MATRIX,"termdocument.txt");
			int[][] termDocument = new int[largeMatrix.height()][largeMatrix.width()];
			for (int i = 0; i < largeMatrix.height(); i++) {
				for (int j = 0; j < largeMatrix.width(); j++) {
					termDocument[i][j] = largeMatrix.get(i, j);
					//System.out.print(largeMatrix.get(i, j)+ " ");
				}
				System.out.println();
			}
			largeMatrix.close();
			this.saveMatrixInteger(termDocument, "BigMatrixTermDocumentLoad.txt",100);
			
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
	public void saveMatrix(double[][] matrix, String fileName) throws Exception{
		BigDoubleMatrixFileManager bigMatrixFileManager = new BigDoubleMatrixFileManager(this.workspace);
		WorkspaceDTO workspace = WorkspaceFacade.getWorkspace("noticias");
		bigMatrixFileManager.loadReadWrite(EDataFolder.MATRIX, fileName,matrix.length,matrix[0].length);
		System.out.println("Init savematrix");
		for (int i = 0; i < matrix.length; i++) {;
			for (int j = 0; j < matrix[i].length; j++) {
				bigMatrixFileManager.set(i,j, matrix[i][j]);
			}
		}
		bigMatrixFileManager.close();
	}
	
	@Test
	public void loadPpmiBigMatrixTest(){
		long start = System.nanoTime();
		int limit = 0;
		try {
			BigDoubleMatrixFileManager largeMatrix = 
					new BigDoubleMatrixFileManager(this.workspace);
			largeMatrix.loadReadOnly(EDataFolder.MATRIX,"ppmi.txt");
			if(limit==0){
				limit = largeMatrix.height();
			}
			double[][] termtermMatrix = new double[limit][largeMatrix.width()];
			for (int i = 0; i < limit; i++) {
				for (int j = 0; j < largeMatrix.width(); j++) {
					termtermMatrix[i][j] = largeMatrix.get(i, j);
				}
				System.out.println();
			}
			largeMatrix.close();
			this.saveMatrixDouble(termtermMatrix, "BigMatrixPpmiLoad.txt",0);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long time = System.nanoTime() - start;
		System.out.print("Time"+ time);
	}
	

	@Test
	public void buildReducedMatrixPpmi() throws Exception{
		this.workspace = WorkspaceFacade.getWorkspace("reuters_nostemmed");
		IRFacade irFacade = IRFacade.getInstance(this.workspace);
		irFacade.reducedDimensionPPMIMatrix(2, true, 0);
	}
	
	@Test
	public void buildReducedSampleMatrix() throws Exception{
		int dimensionNueva =2;
		double[][] data ={{1.0,2.0,3.0,4.0,5.0},{6.0,7.0,8.0,9.0,10}};
		this.saveMatrix(data, "testdimensiones.txt");
		BigDoubleMatrixFileManager largeMatrix = 
				new BigDoubleMatrixFileManager(this.workspace);
		largeMatrix.loadReadOnly(EDataFolder.MATRIX,"testdimensiones.txt");
		
		
		double[][] matrixReduced = {{1.0,2.0},{3.0,4.0},{5.0,6.0},{7.0,8.0},{9.0,10.0}};
		double[][] newMatrix = new double[largeMatrix.height()][matrixReduced[0].length];
	        //se necesitan tres instrucciones for para multiplicar cada
	        //fila de la una matriz por las columnas de la otra
		for(int i = 0; i < largeMatrix.height(); i++){
            for (int j = 0; j < matrixReduced.length; j++){
                for (int k = 0; k < matrixReduced[0].length; k++){
                    newMatrix [i][k] += largeMatrix.get(i,j)*matrixReduced[j][k];
                }
            }
        }
		
		this.saveMatrixDouble(newMatrix, "reducida.txt", 0);
		largeMatrix.close();
	}

	
	@Test
	public void buildReducedMatrix() throws Exception{
		int dimensionNueva =2;
		double[][] data ={{1.00,1.00},{0.00,1.00}};
		this.saveMatrix(data, "testdimensiones.txt");
		BigDoubleMatrixFileManager largeMatrix = 
				new BigDoubleMatrixFileManager(this.workspace);
		largeMatrix.loadReadOnly(EDataFolder.MATRIX,"testdimensiones.txt");
		double[][] matrixReduced = new double[largeMatrix.height()][dimensionNueva];
		double[][] newMatrix = new double[matrixReduced.length][matrixReduced[0].length];
		Random random = new Random();
		for (int i = 0; i < matrixReduced.length; i++) {
			random.setSeed(System.nanoTime());
			for (int j = 0; j < matrixReduced[i].length; j++) {
				matrixReduced[i][j]=random.nextInt(2);
			}
		}
		
	        //se necesitan tres instrucciones for para multiplicar cada
	        //fila de la una matriz por las columnas de la otra
        for(int i = 0; i < matrixReduced.length; i++){
            for (int j = 0; j < largeMatrix.width(); j++){
                for (int k = 0; k < matrixReduced[0].length; k++){
                    newMatrix [i][j] += matrixReduced[i][k] * largeMatrix.get(k,j);
                }
            }
        }
		
		this.saveMatrixDouble(newMatrix, "reducida.txt", 0);
		largeMatrix.close();
	}
	
	
	
	
	
/*	@Test
	public void queryDocumentTest(){
		IndexManager indexManager = new IndexManager();
		indexManager.queryTermsByDocument(25);
	}
*/
	@Test
	public void stemmerTest() {
		Stemmer stemmer = new Stemmer(this.workspace);
		String result = stemmer.stem("compr�");
		System.out.print(result);
	}
	
	@Test
	public void normalizeTest(){
		this.workspace = WorkspaceFacade.getWorkspace("noticias_economicas");
		String dataFile = this.workspace.getPersistence().readFile(EDataFolder.DOWNLOAD, "8112.txt");
		IRFacade.getInstance(this.workspace).getNormalizedDocumentText(dataFile);
	}
	/**
	 * Prueba de la funcion ppmi
	 */
	@Test
	public void ppmiTest(){
		MutualInformation mutualInformation = new MutualInformation(this.workspace);
		double ppmi = mutualInformation.ppmi(6, 11, 7, 19);
		Assert.assertEquals(0.58, ppmi, 0.1);
	}
	
	@Test
	public void test(){
		int a=2;
		int b=3;
		int z=0;
		z=++a+b++;
		System.out.print(z);
	}
	
	/**
	 * Almacena la matriz en disco
	 **/
	public void saveMatrixInteger(int[][] matrix, String fileName, int matrixLimit){
		System.out.print("Init savematrix");
		StringBuilder data = new StringBuilder();
		if(matrixLimit==0){
			matrixLimit = matrix.length;
		}
		for (int i = 0; i < matrixLimit; i++) {;
			for (int j = 0; j < matrix[i].length; j++) {
				data.append(matrix[i][j]);
				data.append(",");
			}
			data.append("\t");
		}
		this.workspace.getPersistence().writeFile(EDataFolder.MATRIX, 
													fileName, data.toString());
	}
	
	
	/**
	 * Almacena la matriz en disco
	 **/
	public void saveMatrixDouble(double[][] matrix, String fileName, int matrixLimit){
		System.out.print("Init savematrix");
		StringBuilder data = new StringBuilder();
		if(matrixLimit==0){
			matrixLimit = matrix.length;
		}
		for (int i = 0; i < matrixLimit; i++) {;
			for (int j = 0; j < matrix[i].length; j++) {
				BigDecimal bigDecimal = new BigDecimal(matrix[i][j]);
				bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
				data.append(bigDecimal);
				if(j+1<matrix[i].length){
					data.append(";");
				}
			}
			data.append(System.getProperty("line.separator"));
		}
		this.workspace.getPersistence().writeFile(EDataFolder.MATRIX, 
												  fileName, data.toString());
	}
	
	@Test
	public void buildAllMatrixTest(){
		IRFacade irFacade = IRFacade.getInstance(this.workspace);
		try {
			irFacade.buildCmeanMatrix(0,0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void zipfIndexTest(){
		this.workspace = WorkspaceFacade.getWorkspace("reuters_nostemmed");
		InvertedIndex invertedIndex = new InvertedIndex(workspace);
		invertedIndex.reducedZipfInvertedIndex(20, 35, 1);
	}
	@Test
	public void zipfIndexRelevanceTest(){
		this.workspace = WorkspaceFacade.getWorkspace("reuters_nostemmed");
		InvertedIndex invertedIndex = new InvertedIndex(workspace);
		invertedIndex.relevanceZipfInvertedIndex(1);
	}
	
	@Test
	public void unscape(){
		String data = "&#F1";
		String result = StringEscapeUtils.unescapeHtml(data);
		System.out.print(result);
	}
}
