package co.edu.sanmartin.webscraping.test;

import org.junit.Test;

import twitter4j.TwitterException;

import co.edu.sanmartin.webscraping.twitter.TwitterScraping;

public class TwitterScrapingTest {


	@Test
	public void timeLineTest() throws TwitterException{
		TwitterScraping twitterScraping = new TwitterScraping();
		twitterScraping.getTimeLine();
	}
	
	@Test
	public void followersTest() throws TwitterException{
		TwitterScraping twitterScraping = new TwitterScraping();
		twitterScraping.getFriends();
	}
	
	@Test
	public void addFriendTest() throws TwitterException{
		TwitterScraping twitterScraping = new TwitterScraping();
		twitterScraping.createFriendship("@pacificrubiales");
	}
}
