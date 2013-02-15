package co.edu.sanmartin.webscraping.test;
import org.apache.log4j.BasicConfigurator;
import org.junit.Ignore;
import org.junit.Test;

import co.edu.sanmartin.webscraping.execute.DowloadThreadPool;
import co.edu.sanmartin.webscraping.rss.RssScraping;

/**
 * 
 * @author Ricardo Carvajal Salamanca
 *
 */
public class RssScrapingTest {
	public RssScrapingTest(){
		BasicConfigurator.configure();
	}
	
	@Test
	@Ignore
	public void rssDowloadTest(){
		RssScraping rssScraping = new RssScraping();
		rssScraping.getRssDocuments("http://www.eltiempo.com/economia/rss.xml");
	}
	
	@Test
	public void threadPoolTest(){
		DowloadThreadPool threadPool = new DowloadThreadPool();
		threadPool.executeThreadPool();
	}
}
