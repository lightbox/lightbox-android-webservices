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
package com.lightbox.tweetsnearby.responses.foursquare;

import org.apache.http.HttpStatus;

import com.lightbox.android.webservices.responses.ApiException;
import com.lightbox.android.webservices.responses.ApiResponse;

/** 
 * FoursquareApiResponse 
 * @author Fabien Devos
 */
public abstract class FoursquareApiResponse<TContent> implements ApiResponse<TContent> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "FoursquareApiResponse";
	
	private Meta mMeta;
	private TContent mContent;
	private ApiException mApiException;

	public Meta getMeta() {
		return mMeta;
	}

	public void setMeta(Meta meta) {
		mMeta = meta;
	}

	protected void setContent(TContent content) {
		mContent = content;
	}
	
	@Override
	public TContent getContent() {
		return mContent;
	}
	
	@Override
	public Object getContext() {
		return null;
	}

	@Override
	public boolean hasError() {
		return (mMeta.getCode() >= HttpStatus.SC_BAD_REQUEST);
	}

	@Override
	public ApiException getException() {
		if(mApiException == null) {
			mApiException = new ApiException(mMeta.getCode(), mMeta.getErrorDetail());
		}
		return mApiException;
	}
		
	/********************************
	 * Meta 
	 */
	protected static class Meta {
		private int mCode;
		private String mErrorType;
		private String mErrorDetail;

		public int getCode() {
			return mCode;
		}

		public void setCode(int code) {
			mCode = code;
		}

		public String getErrorType() {
			return mErrorType;
		}

		public void setErrorType(String errorType) {
			mErrorType = errorType;
		}

		public String getErrorDetail() {
			return mErrorDetail;
		}

		public void setErrorDetail(String errorDetail) {
			mErrorDetail = errorDetail;
		}
	}
}
