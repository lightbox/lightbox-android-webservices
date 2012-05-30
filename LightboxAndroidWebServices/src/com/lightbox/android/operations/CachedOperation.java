/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.operations;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import android.util.Log;

import com.lightbox.android.cache.ApiCache;
import com.lightbox.android.cache.Cache;
import com.lightbox.android.utils.debug.DebugLog;
import com.lightbox.android.webservices.processors.ParsingException;
import com.lightbox.android.webservices.requests.ApiRequest;
import com.lightbox.android.webservices.responses.ApiException;
import com.lightbox.android.webservices.responses.ApiResponse;

/** 
 * CachedOperation 
 * @author Fabien Devos
 */
public class CachedOperation<T> extends AbstractOperation<T> {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "CachedOperation";
	
	private boolean mIsDataTooOld = false;
	private Object mContext = null;
	private boolean mRetrieveFromLocalDataOnly = false;

	/**
	 * Constructor.
	 * @param dataClass
	 * @param apiRequest
	 */
	public CachedOperation(Class<T> dataClass, ApiRequest apiRequest) {
		this(dataClass, apiRequest, false);
	}
	public CachedOperation(Class<T> dataClass, ApiRequest apiRequest, boolean retrieveFromLocalDataOnly) {
		super(dataClass, apiRequest);
		mRetrieveFromLocalDataOnly = retrieveFromLocalDataOnly;
	}
	
	//----------------------------------------------
	// Operation
	
	@Override
	public Object getContext() {
		return mContext;
	}

	// Override the default executeAsync to add support for memory caching on the main thread.
	@Override
	public final void executeAsync(OperationListener<T> listener) {
		List<T> resultList = null;
		try {
			resultList = executeLocalOperationSync();
		} catch (Throwable t) {
			Log.w(TAG, "Failed to retrieve data from cache", t);
		}
		
		if (resultList != null && ! resultList.isEmpty()) {
			DebugLog.d(TAG, "Getting result from cache.");
			
			// Call back on main thread
			if (listener != null) {
				listener.onSuccess(this, resultList);
			}
		}
		
		if ( ! mRetrieveFromLocalDataOnly && mIsDataTooOld) {
			// Proceed with normal operation execution
			super.executeAsync(listener);
		}
	}

	@Override
	public final List<T> executeSync() throws Exception {
		List<T> resultList = null;

		// If (there is no result OR the data are too old) AND we are not asking for local data only
		if ((resultList == null || mIsDataTooOld) && ! mRetrieveFromLocalDataOnly) {
			DebugLog.d(TAG, "No result from cache OR data is too old");
			
			// Try to retrieve data from web services
			resultList = executeServerOperationSync();

			// No need to send back result from cache because it's already done
		}
		
		return resultList;
	}

	@Override
	public List<T> executeLocalOperationSync() throws Exception {
		// Try to retrieve from disk
		Cache.Result<String> cacheResult = ApiCache.getInstance().getFromDisk(getId());
		String stringResult = cacheResult.getData();
		mIsDataTooOld = isDataTooOld(cacheResult.getUpdatedTime());
		// Data is treated as "too old" if empty
		if (stringResult == null) { return null; }
		
		// Parse the cached response
		ApiResponse<?> apiResponse = parseString(stringResult);
		Object result = apiResponse.getContent();
		mContext = apiResponse.getContext();
		if (result == null) { return null; }
		
		DebugLog.d(TAG, "Getting result from disk cache. Data is too old:" + mIsDataTooOld);
	
		return wrapInList(result);
	}

	@Override
	public List<T> executeServerOperationSync() throws Exception {
		//Log.e(TAG, "CALLING SERVER id: %s" + getId());
		
		// Query server
		InputStream inputStream = getApiRequest().callApi().getEntity().getContent();
		String stringResultFromServer = IOUtils.toString(inputStream);
		ApiResponse<?> apiResponse = getApiRequest().parseInputStream(IOUtils.toInputStream(stringResultFromServer));	
		Object result = apiResponse.getContent();
		mContext = apiResponse.getContext();
		if (result == null) { return null; }
		List<T> resultList = wrapInList(result);
		
		DebugLog.d(TAG, "Getting result from server.");
		
		// Save in disk cache
		ApiCache.getInstance().putOnDisk(getId(), stringResultFromServer);

		// Hook
		if (resultList != null) { onRetrieveDataFromServer(resultList); }
		
		return resultList;
	}
	
	//----------------------------------------------
	// Utility methods
	
	private boolean isDataTooOld(long updatedTime) {
		return (System.currentTimeMillis() - updatedTime) > RetrieveOperation.CACHE_DURATION;
	}
	
	private ApiResponse<?> parseString(String stringResult) throws ParsingException, IOException, ApiException {
		if (stringResult == null) { return null; }
		return getApiRequest().parseInputStream(IOUtils.toInputStream(stringResult));		
	}
	
	//----------------------------------------------
	// Hooks for subclasses

	protected void onRetrieveDataFromServer(List<T> result) throws Exception {
		// Nothing by default
	}
}
