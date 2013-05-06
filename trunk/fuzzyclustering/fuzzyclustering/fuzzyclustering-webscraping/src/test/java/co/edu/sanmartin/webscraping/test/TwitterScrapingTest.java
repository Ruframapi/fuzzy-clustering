package co.edu.sanmartin.webscraping.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import twitter4j.TwitterException;

import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.facade.WorkspaceFacade;
import co.edu.sanmartin.webscraping.twitter.TwitterScraping;

public class TwitterScrapingTest {

	private WorkspaceDTO workspace = WorkspaceFacade.getWorkspace("noticias");
	@Test
	public void timeLineTest() throws TwitterException{
		TwitterScraping twitterScraping = new TwitterScraping(this.workspace,new AtomicInteger());
		twitterScraping.getTimeLine();
	}
	
	@Test
	public void followersTest() throws TwitterException{
		TwitterScraping twitterScraping = new TwitterScraping(this.workspace,new AtomicInteger());
		twitterScraping.getFriends();
	}
	
	@Test
	public void addFriendTest() throws TwitterException{
		TwitterScraping twitterScraping = new TwitterScraping(this.workspace,new AtomicInteger());
		twitterScraping.createFriendship("@pacificrubiales");
	}
}
