package co.edu.sanmartin.webscraping.facade;

import co.edu.sanmartin.persistence.constant.EDataFolder;
import co.edu.sanmartin.persistence.dto.QueueDTO;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;
import co.edu.sanmartin.webscraping.execute.DowloadRSSThreadPool;
import co.edu.sanmartin.webscraping.rss.RssScraping;
import co.edu.sanmartin.webscraping.twitter.TwitterScraping;

/**
 * Fachada que gestiona los eventos del modulo de webscraping
 * @author Ricardo Carvjal Salamanca
 *
 */
public class WebscrapingFacade {

	private static WebscrapingFacade instance;
	private static TwitterScraping twitterScraping;
	
	private WebscrapingFacade(){
		twitterScraping = new TwitterScraping();
	}
	public static WebscrapingFacade getInstance(){
		if(instance == null){
			instance  = new WebscrapingFacade();
		}
		return instance;
	}
	
	
	public void createFriendship(String screenName){
		twitterScraping.createFriendship(screenName);
	}
	
	public void removeFriendship(String screenName){
		twitterScraping.removeFriendship(screenName);
	}
	
}
