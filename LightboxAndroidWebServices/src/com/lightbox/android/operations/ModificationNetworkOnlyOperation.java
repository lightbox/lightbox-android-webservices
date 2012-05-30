/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.operations;

import java.util.List;

import com.lightbox.android.cache.ApiCache;
import com.lightbox.android.webservices.requests.ApiRequest;

/** 
 * ModificationNetworkOnlyOperation.
 * @author Fabien Devos
 */
public class ModificationNetworkOnlyOperation<T> extends NetworkOnlyOperation<T> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "ModificationNetworkOnlyOperation";

	/**
	 * Constructor.
	 * @param dataClass
	 * @param apiRequest
	 */
	protected ModificationNetworkOnlyOperation(Class<T> dataClass, ApiRequest apiRequest) {
		super(dataClass, apiRequest);
	}

	//----------------------------------------------
	// Operation

	@Override
	public List<T> executeServerOperationSync() throws Exception {
		List<T> listResult = super.executeServerOperationSync();
		ApiCache.getInstance().clear();
		return listResult;
	}	
}
