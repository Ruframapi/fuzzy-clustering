package co.edu.sanmartin.webscraping.facade;

import co.edu.sanmartin.webscraping.execute.DowloadThreadPool;
import co.edu.sanmartin.webscraping.rss.RssScraping;
import co.edu.sanmartin.webscraping.twitter.TwitterScraping;

/**
 * Fachada que gestiona los eventos del modulo de webscraping
 * @author Ricardo Carvjal Salamanca
 *
 */
public class WebscrapingFacade {

	private static WebscrapingFacade instance;
	private TwitterScraping twitterScraping;
	
	private WebscrapingFacade(){
		this.twitterScraping = new TwitterScraping();
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
	
	public void createFriendship(String screenName){
		this.twitterScraping.createFriendship(screenName);
	}
	
	public void removeFriendship(String screenName){
		this.twitterScraping.removeFriendship(screenName);
	}
	
}
