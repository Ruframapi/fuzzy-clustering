package co.edu.sanmartin.webscraping.test;

import org.junit.Test;

import co.edu.sanmartin.webscraping.twitter.TwitterScraping;

public class TwitterScrapingTest {


	@Test
	public void timeLineTest(){
		TwitterScraping twitterScraping = new TwitterScraping();
		twitterScraping.getTimeLine();
	}
	
	@Test
	public void followersTest(){
		TwitterScraping twitterScraping = new TwitterScraping();
		twitterScraping.getFriends();
	}
	
	@Test
	public void addFriendTest(){
		TwitterScraping twitterScraping = new TwitterScraping();
		twitterScraping.createFriendship("@pacificrubiales");
	}
}
