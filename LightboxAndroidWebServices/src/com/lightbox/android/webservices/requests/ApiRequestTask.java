/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.webservices.requests;

import com.lightbox.android.tasks.BackgroundTaskWeak;
import com.lightbox.android.webservices.responses.ApiResponse;

/** 
 * ApiRequestTask 
 * @author Fabien Devos
 */
public class ApiRequestTask extends BackgroundTaskWeak<ApiRequestListener, ApiResponse<?>> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "ApiRequestTask";
	
	private ApiRequest mApiRequest;
	
	//----------------------------------------------
	// Constructors
	
	/**
	 * Constructor.
	 * @param ref
	 */
	public ApiRequestTask(ApiRequestListener listener, ApiRequest apiRequest) {
		super(listener);
		mApiRequest = apiRequest;
	}

	//----------------------------------------------
	// BackgroundTask implementation
	
	@Override
	protected ApiResponse<?> doWorkInBackground() throws Exception {
		return mApiRequest.execute();
	}

	@Override
	protected void onCompleted(ApiResponse<?> apiResponse) {
		ApiRequestListener listener = getRef();
		if (listener != null) {
			listener.onSuccess(apiResponse.getContent());
		}		
	}

	@Override
	protected void onFailed(Exception e) {
		ApiRequestListener listener = getRef();
		if (listener != null) {
			listener.onFailure(e);
		}
	}
}
