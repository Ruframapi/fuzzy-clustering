package co.edu.sanmartin.webscraping.facade;

import co.edu.sanmartin.webscraping.execute.DowloadThreadPool;
import co.edu.sanmartin.webscraping.rss.RssScraping;

/**
 * Fachada que gestiona los eventos del modulo de webscraping
 * @author Ricardo Carvjal Salamanca
 *
 */
public class WebscrapingFacade {

	private static WebscrapingFacade instance;
	
	private WebscrapingFacade(){
		
	}
	public static WebscrapingFacade getInstance(){
		if(instance == null){
			instance  = new WebscrapingFacade();
		}
		return instance;
	}
	
	public void downloadSources(){
		DowloadThreadPool threadPool = new DowloadThreadPool();
		threadPool.executeThreadPool();
	}
}
