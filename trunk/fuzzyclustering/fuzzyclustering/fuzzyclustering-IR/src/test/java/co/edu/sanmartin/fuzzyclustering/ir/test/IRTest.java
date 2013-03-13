package co.edu.sanmartin.fuzzyclustering.ir.test;

import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.ir.execute.CleanerThreadPool;
import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;
import co.edu.sanmartin.fuzzyclustering.ir.index.InvertedIndex;
import co.edu.sanmartin.fuzzyclustering.ir.index.MutualInformation;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.Stemmer;
import co.edu.sanmartin.fuzzyclustering.ir.normalize.stemmer.snowball.SnowballStemmer;

public class IRTest {

	public IRTest() {
		BasicConfigurator.configure();
	}

	@Test
	//@Ignore
	public void cleanTest() {
		CleanerThreadPool threadPool = new CleanerThreadPool();
		threadPool.executeThreadPool();
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
	public void mutualInformationTest(){
		MutualInformation mutualInformation = new MutualInformation();
		mutualInformation.buildMutualInformationMatrix();
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
