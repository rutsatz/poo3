package com.poo3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poo3.rest.TwitterIntegration;

@Service
public class TwitterService {

	@Autowired
	public TwitterIntegration twitterIntegration;
	
	public void postar() {
		
		// return
		twitterIntegration.postTweet();
	}

}
