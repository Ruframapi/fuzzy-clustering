package co.edu.sanmartin.fuzzyclustering.ir.test;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.fuzzyclustering.ir.index.MutualInformation;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.Cleaner;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.Stemmer;
import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.persistence.file.BigMatrixFileManager;

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
	public void invertedIndexText() {
		InvertedIndexThreadPool threadPool = new InvertedIndexThreadPool();
		threadPool.run();
		//Thread thread = new Thread(threadPool);
		//thread.start();
	}

	
	@Test
	public void termDocumentMatrixTest(){
		InvertedIndex indexManager = new InvertedIndex();
		indexManager.getTermDocumentMatrix();
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
		InvertedIndex indexManager = new InvertedIndex();
		try {
			indexManager.buildTermTermBigMatrix(true);
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
		mutualInformation.buildMutualInformationBigMatrix();
	}
	
	@Test
	public void loadTermTermBigMatrixTest(){
		long start = System.nanoTime();
		
		try {
			BigMatrixFileManager largeMatrix = 
					new BigMatrixFileManager();
			largeMatrix.loadReadOnly(EDataFolder.MATRIX,"termterm.txt");
			double[][] termtermMatrix = new double[largeMatrix.height()][largeMatrix.width()];
			for (int i = 0; i < largeMatrix.height(); i++) {
				for (int j = 0; j < largeMatrix.width(); j++) {
					termtermMatrix[i][j] = largeMatrix.get(i, j);
					//System.out.print(largeMatrix.get(i, j)+ " ");
				}
				System.out.println();
			}
			largeMatrix.close();
			this.saveMatrix(termtermMatrix, "BigMatrixLoad.txt");
			
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
	public void saveMatrix(double[][] matrix, String fileName){
		System.out.print("Init savematrix");
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < matrix.length; i++) {;
			for (int j = 0; j < matrix[i].length; j++) {
				data.append(matrix[i][j]);
				data.append(",");
			}
			data.append(System.getProperty("line.separator"));
		}
		PersistenceFacade.getInstance().writeFile(EDataFolder.MATRIX, 
													fileName, data.toString());
	}
}
