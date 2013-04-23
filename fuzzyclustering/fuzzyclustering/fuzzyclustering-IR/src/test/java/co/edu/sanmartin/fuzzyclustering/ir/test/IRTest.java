package co.edu.sanmartin.fuzzyclustering.ir.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexReutersThreadPool;
import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;
import co.edu.sanmartin.fuzzyclustering.ir.facade.IRFacade;
import co.edu.sanmartin.fuzzyclustering.ir.index.DimensionallyReduced;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.fuzzyclustering.ir.index.MutualInformation;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.Cleaner;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.Stemmer;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.file.BigDoubleMatrixFileManager;
import co.edu.sanmartin.persistence.file.BigIntegerMatrixFileManager;

public class IRTest {

	public IRTest() {
		BasicConfigurator.configure();
	}

	@Test
	public void cleanTextLexicon(){
		Cleaner cleaner = new Cleaner();
		cleaner.deleteLexiconStopWords("a ante bajo co contra Ricardo Carvajal Estuvo en un lugar muy importante", "miArchivo.txt", false);
	}
	
	
	@Test
	public void invertedIndexTest() {
		InvertedIndexThreadPool threadPool = new InvertedIndexThreadPool(2);
		threadPool.run();
		//Thread thread = new Thread(threadPool);
		//thread.start();
	}
	
	@Test
	public void invertedIndexReutersTest() {
		InvertedIndexReutersThreadPool threadPool = new InvertedIndexReutersThreadPool(0);
		threadPool.run();
		//Thread thread = new Thread(threadPool);
		//thread.start();
	}

	
	@Test
	public void termDocumentBigMatrixTest(){
		InvertedIndex invertedIndex = new InvertedIndex();
		try {
			invertedIndex.createTermDocumentBigMatrix(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void termTermMatrixTest(){
		InvertedIndex indexManager = new InvertedIndex();
		indexManager.getTermTermMatrix();
	}
	@Test
	public void buildTermTermMatrixTest(){
		InvertedIndex indexManager = new InvertedIndex();
		indexManager.buildTermTermMatrix(true);
	}
	@Test
	public void buildTermTermBigMatrixTest(){
		InvertedIndex invertedIndex = new InvertedIndex();
		try {
			invertedIndex.createTermTermBigMatrix(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void mutualInformationTest(){
		MutualInformation mutualInformation = new MutualInformation();
		mutualInformation.buildMutualInformationMatrix();
	}
	
	@Test
	public void buildMutualInformationBigMatrixTest(){
		MutualInformation mutualInformation = new MutualInformation();
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
					new BigIntegerMatrixFileManager();
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
					new BigIntegerMatrixFileManager();
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
		BigDoubleMatrixFileManager bigMatrixFileManager = new BigDoubleMatrixFileManager();
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
					new BigDoubleMatrixFileManager();
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
		IRFacade irFacade = IRFacade.getInstance();
		irFacade.reducedDimensionPPMIMatrix(2, true,0);
	}
	
	@Test
	public void buildReducedSampleMatrix() throws Exception{
		int dimensionNueva =2;
		double[][] data ={{1.0,2.0,3.0,4.0,5.0},{6.0,7.0,8.0,9.0,10}};
		this.saveMatrix(data, "testdimensiones.txt");
		BigDoubleMatrixFileManager largeMatrix = 
				new BigDoubleMatrixFileManager();
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
	}

	
	@Test
	public void buildReducedMatrix() throws Exception{
		int dimensionNueva =2;
		double[][] data ={{1.00,1.00},{0.00,1.00}};
		this.saveMatrix(data, "testdimensiones.txt");
		BigDoubleMatrixFileManager largeMatrix = 
				new BigDoubleMatrixFileManager();
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
	}
	
	
	
	
	
/*	@Test
	public void queryDocumentTest(){
		IndexManager indexManager = new IndexManager();
		indexManager.queryTermsByDocument(25);
	}
*/
	@Test
	public void stemmerTest() {
		Stemmer stemmer = new Stemmer();
		String result = stemmer.stem("compró", "", false);
		System.out.print(result);
	}
	
	/**
	 * Prueba de la funcion ppmi
	 */
	@Test
	public void ppmiTest(){
		MutualInformation mutualInformation = new MutualInformation();
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
		PersistenceFacade.getInstance().writeFile(EDataFolder.MATRIX, 
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
		PersistenceFacade.getInstance().writeFile(EDataFolder.MATRIX, 
												  fileName, data.toString());
	}
	
	@Test
	public void buildAllMatrixTest(){
		IRFacade irFacade = IRFacade.getInstance();
		try {
			irFacade.buildCmeanMatrix(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
