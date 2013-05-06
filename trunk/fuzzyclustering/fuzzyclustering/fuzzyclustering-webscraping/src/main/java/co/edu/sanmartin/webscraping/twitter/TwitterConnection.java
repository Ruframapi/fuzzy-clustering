package co.edu.sanmartin.webscraping.twitter;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import co.edu.sanmartin.persistence.constant.EProperty;
import co.edu.sanmartin.persistence.dto.WorkspaceDTO;
import co.edu.sanmartin.persistence.exception.PropertyValueNotFoundException;
import co.edu.sanmartin.persistence.facade.PersistenceFacade;



public class TwitterConnection {
	
	private static TwitterConnection instance;
	private Twitter twitter;
	private static Logger logger = Logger.getLogger(TwitterConnection.class);
	private WorkspaceDTO workspace;
	
	private TwitterConnection(WorkspaceDTO workspace){
		this.workspace = workspace;
		try {
			if(this.twitter ==null || this.twitter.getId()==0){
				loginTwitter();
			}
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}
	
	
	public static TwitterConnection getInstance(WorkspaceDTO workspace){
		if(instance ==  null){
			instance = new TwitterConnection(workspace);
		}
		return instance;
	}
	
	
	private void loginTwitter() throws TwitterException{
		String consumerKey;
		try {
			consumerKey = this.workspace.getPersistence().getProperty(EProperty.TWITTER_CONSUMER_KEY).getValue();
			String consumerSecret = this.workspace.getPersistence().getProperty(EProperty.TWITTER_CONSUMER_SECRET).getValue();
			String accessToken = this.workspace.getPersistence().getProperty(EProperty.TWITTER_ACCESS_TOKEN).getValue();
			String accessTokenSecret = this.workspace.getPersistence().getProperty(EProperty.TWITTER_ACCESS_TOKEN_SECRET).getValue();
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey(consumerKey)
			  .setOAuthConsumerSecret(consumerSecret)
			  .setOAuthAccessToken(accessToken)
			  .setOAuthAccessTokenSecret(accessTokenSecret);
			TwitterFactory tf = new TwitterFactory(cb.build());
			this.twitter = tf.getInstance();
			this.twitter.verifyCredentials();
		} catch (PropertyValueNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public Twitter getTwitter() {
		return twitter;
	}

	
}
