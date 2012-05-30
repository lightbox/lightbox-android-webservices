/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.operations;

import java.util.List;

import com.lightbox.android.webservices.requests.ApiRequest;
import com.lightbox.android.webservices.responses.ApiResponse;

/** 
 * NetworkOperation 
 * @author Fabien Devos
 */
public class NetworkOnlyOperation<T> extends AbstractOperation<T> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "NetworkOperation";

	/**
	 * Constructor.
	 * @param dataClass
	 * @param apiRequest
	 */
	protected NetworkOnlyOperation(Class<T> dataClass, ApiRequest apiRequest) {
		super(dataClass, apiRequest);
	}


	@Override
	public List<T> executeSync() throws Exception {
		return executeServerOperationSync();
	}


	@Override
	public List<T> executeLocalOperationSync() throws Exception {
		// There is no local operation
		return null;
	}


	@Override
	public List<T> executeServerOperationSync() throws Exception {
		ApiResponse<?> apiResponse = getApiRequest().execute();				
		Object result = apiResponse.getContent();
		return wrapInList(result);
	}
}
