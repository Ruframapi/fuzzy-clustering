package co.edu.sanmartin.fuzzyclustering.ir.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

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
		InvertedIndexThreadPool threadPool = new InvertedIndexThreadPool();
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
	public void mutualInformationBigMatrixTest(){
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
	
	

	
	@Test
	public void loadPpmiBigMatrixTest(){
		long start = System.nanoTime();
		
		try {
			BigDoubleMatrixFileManager largeMatrix = 
					new BigDoubleMatrixFileManager();
			largeMatrix.loadReadOnly(EDataFolder.MATRIX,"ppmi.txt");
			double[][] termtermMatrix = new double[largeMatrix.height()][largeMatrix.width()];
			for (int i = 0; i < largeMatrix.height(); i++) {
				for (int j = 0; j < largeMatrix.width(); j++) {
					termtermMatrix[i][j] = largeMatrix.get(i, j);
					//System.out.print(largeMatrix.get(i, j)+ " ");
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
			data.append(System.getProperty("line.separator"));
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
				data.append(matrix[i][j]);
				data.append(",");
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
			irFacade.buildCmeanMatrix();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
