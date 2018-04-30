package com.poo3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poo3.rest.TwitterIntegration;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.TwitterException;

@Service
public class TwitterService {

	@Autowired
	public TwitterIntegration twitterIntegration;

	public void processRequest(DirectMessage directMessage) {
		String text = directMessage.getText();
		long user = directMessage.getSenderId();
		sendDirect(user, text);
	}

	public void processRequest(Status status) {
		
		String text = status.getText() + " em resposta a " + status.getUser().getScreenName();
		post(text);
	}

	public void post(String message) {

		twitterIntegration.postTweet(message);
	}

	public void sendDirect(long recipientId, String text) {
		try {
			twitterIntegration.sendDirect(recipientId, text);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

}
