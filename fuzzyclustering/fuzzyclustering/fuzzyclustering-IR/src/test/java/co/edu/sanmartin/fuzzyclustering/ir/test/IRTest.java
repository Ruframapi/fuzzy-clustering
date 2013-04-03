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
		threadPool.executeThreadPool();
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
		indexManager.buildTermTermMatrix(true, false);
	}
	@Test
	public void buildTermTermBigMatrixTest(){
		InvertedIndex indexManager = new InvertedIndex();
		try {
			indexManager.buildTermTermBigMatrix(true, false);
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
	public void loadTermTermBigMatrixTest(){
		long start = System.nanoTime();
		
		try {
			BigMatrixFileManager largeMatrix = 
					new BigMatrixFileManager();
			largeMatrix.loadReadOnly(EDataFolder.MATRIX,"termterm.txt");
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
}
