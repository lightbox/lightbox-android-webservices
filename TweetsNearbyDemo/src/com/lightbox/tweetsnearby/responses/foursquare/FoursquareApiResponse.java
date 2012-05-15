/**
 * Copyright (c) 2011 Lightbox
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
