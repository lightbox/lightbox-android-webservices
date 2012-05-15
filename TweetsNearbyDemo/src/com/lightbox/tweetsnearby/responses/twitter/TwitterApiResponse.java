/**
 * Copyright (c) 2012 Lightbox
 */
package com.lightbox.tweetsnearby.responses.twitter;

import com.lightbox.android.webservices.responses.ApiException;
import com.lightbox.android.webservices.responses.ApiResponse;

/** 
 * TwitterApiResponse 
 * @author Fabien Devos
 */
public abstract class TwitterApiResponse<TContent> implements ApiResponse<TContent> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "TwitterApiResponse";

	private String mErrorMessage;
	private TContent mContent;
	
	@Override
	public TContent getContent() {
		return mContent;
	}
	
	protected void setContent(TContent content) {
		mContent = content;
	}
	
	@Override
	public boolean hasError() {
		return mErrorMessage != null;
	}
	
	public void setError(String errorMessage) {
		mErrorMessage = errorMessage;
	}

	@Override
	public ApiException getException() {
		ApiException apiException = null;
		if (mErrorMessage != null) {
			apiException = new ApiException(-1, mErrorMessage);
		}
		return apiException;
	}

	@Override
	public Object getContext() {
		return null;
	}
}
