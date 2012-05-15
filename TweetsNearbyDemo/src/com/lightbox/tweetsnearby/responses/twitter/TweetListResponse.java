/**
 * Copyright (c) 2012 Lightbox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
