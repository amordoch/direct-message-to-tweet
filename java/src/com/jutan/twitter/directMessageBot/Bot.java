package com.jutan.twitter.directMessageBot;

/**
 * 
 * @author Dan Jutan
 * Packaged, configured, and improved by Ariel Mordoch
 * 
 */

import java.util.Timer;
import java.util.TimerTask;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class Bot {

	/**
	 * @param args
	 */
	private static final int SECONDS = 120;
	private Twitter twitter;
	private Timer timer;
	public static void main(String[] args) {
		new Bot();
	}
	
	private Bot() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(false)
		  .setOAuthAccessToken("2360163056-2UL2chf9sHq1v43cqD8lBBLyd2ysQl2x6kuJrIE")
		  .setOAuthAccessTokenSecret("V3VdTgyDdbo7IeT8A95tEhvYXiCCtMgKW6sS2jXooTIHj")
		  .setOAuthConsumerKey("dNmKSVjN4lcCIbiCUaJ2sA")
		  .setOAuthConsumerSecret("tZ6GVYpnEYe1vFoLaLkha3n6t1GaafCtXiWRCxnKAA");
		
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			  @SuppressWarnings("unused")
			@Override
			  public void run() {
				  
				  try {
					  System.out.println("Checking direct messages: " + twitter.getDirectMessages().size() + " found");
					for (DirectMessage message : twitter.getDirectMessages()) {
						  String text = message.getText();
						  System.out.println(text);
						  int index;
						  if ((index = text.indexOf("&&")) == 0) {
							  if( text.contains("@") ) {
								 System.out.println("Tweet contained an @ mention, rejecting...");
								 DirectMessage rejectMessageAtMention = twitter.sendDirectMessage(message.getSenderId(),"Sorry,  I won't allow @ mentions. Please try again.");
							  } else {
								  System.out.println("Tweet successful");
								  Status status = twitter.updateStatus(text.substring(index + 2));
								  Status notifySuccess = twitter.updateStatus("Tweet successful");
								  twitter.getDirectMessages().remove( message.getId() );
							  }
						  } else {
							  System.out.println("Character '&&' not detected, rejecting...");
							  DirectMessage rejectMessageAtActivation = twitter.sendDirectMessage(message.getSenderId(), "I didn't understand that. To tweet, reply with '&& (your tweet here'.");
						  }
					  }
				} catch (TwitterException e) {
					e.printStackTrace();
					System.out.print( "Error generated at time " + System.currentTimeMillis() );
				}
			  }
			}, 0, SECONDS * 1000);
	}

}
