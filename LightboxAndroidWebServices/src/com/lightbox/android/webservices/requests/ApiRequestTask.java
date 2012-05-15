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
