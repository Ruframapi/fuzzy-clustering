package co.edu.sanmartin.webscraping.test;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import co.edu.sanmartin.persistence.dto.SourceDTO;
import co.edu.sanmartin.webscraping.execute.CallRemoteServlet;
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
	//@Ignore
	public void rssDowloadTest(){
		AtomicInteger ai = new AtomicInteger();
		RssScraping rssScraping = new RssScraping(ai);
		SourceDTO sourceDTO = new SourceDTO();
		//sourceDTO.setUrl("http://www.eltiempo.com/economia/rss.xml");
		sourceDTO.setUrl("http://es.noticias.yahoo.com/rss/economia");
		rssScraping.getRssDocuments(sourceDTO);
	}
	
	@Test
	//@Ignore
	public void downloadThreadPoolTest(){
		//DowloadRSSThreadPool threadPool = new DowloadRSSThreadPool();
		//threadPool.executeThreadPool();
	}
	
	@Test
	public void callRemoteServlet(){
		try {
			
			CallRemoteServlet callRemoteServlet = new CallRemoteServlet();
			callRemoteServlet.sendDocument();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
