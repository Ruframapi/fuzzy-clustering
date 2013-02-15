package co.edu.sanmartin.fuzzyclustering.ir.test;

import org.apache.log4j.BasicConfigurator;
import org.junit.Ignore;
import org.junit.Test;

import co.edu.sanmartin.fuzzyclustering.ir.execute.InvertedIndexThreadPool;

public class IRTest {

	public IRTest(){
		BasicConfigurator.configure();
	}
	@Test
	public void makeIndexTest(){
		InvertedIndexThreadPool threadPool = new InvertedIndexThreadPool();
		threadPool.executeThreadPool();
	}
}
