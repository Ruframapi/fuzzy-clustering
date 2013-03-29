package co.edu.sanmartin.webscraping.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import twitter4j.TwitterException;

import co.edu.sanmartin.webscraping.twitter.TwitterScraping;

public class TwitterScrapingTest {


	@Test
	public void timeLineTest() throws TwitterException{
		TwitterScraping twitterScraping = new TwitterScraping(new AtomicInteger());
		twitterScraping.getTimeLine();
	}
	
	@Test
	public void followersTest() throws TwitterException{
		TwitterScraping twitterScraping = new TwitterScraping(new AtomicInteger());
		twitterScraping.getFriends();
	}
	
	@Test
	public void addFriendTest() throws TwitterException{
		TwitterScraping twitterScraping = new TwitterScraping(new AtomicInteger());
		twitterScraping.createFriendship("@pacificrubiales");
	}
}
