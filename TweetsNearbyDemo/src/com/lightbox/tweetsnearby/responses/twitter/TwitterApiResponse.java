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
