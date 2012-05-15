/**
 * Copyright (c) 2012 Lightbox
 */
package com.lightbox.tweetsnearby.responses.twitter;

import java.util.List;

import com.lightbox.tweetsnearby.model.Tweet;

/** 
 * TweetListResponse 
 * @author Fabien Devos
 */
public class TweetListResponse extends TwitterApiResponse<List<Tweet>> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "TweetListResponse";
	
	public void setResults(List<Tweet> tweetList) {
		setContent(tweetList);
	}
	
}
