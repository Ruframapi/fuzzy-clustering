package co.edu.sanmartin.fuzzyclustering.ir.test;

import org.apache.log4j.BasicConfigurator;
import org.junit.Ignore;
import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.ir.execute.CleanerThreadPool;
import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;
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
	public void stemmerTest() {
		Stemmer stemmer = new Stemmer();
		String result = stemmer.stem("compró", "", false);
		System.out.print(result);
	}
}
