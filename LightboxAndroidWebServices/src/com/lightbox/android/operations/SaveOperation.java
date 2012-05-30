/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.operations;

import java.util.List;

import com.lightbox.android.utils.debug.DebugLog;
import com.lightbox.android.webservices.requests.ApiRequest;
import com.lightbox.android.webservices.responses.ApiResponse;

/** 
 * SaveOperation 
 * @author Fabien Devos
 */
public class SaveOperation<T> extends AbstractOperation<T> {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "SaveOperation";
	
	private T mData;
	
	//----------------------------------------------
	// Constructor
	
	/**
	 * Constructor.
	 * @param data the data to save
	 * @param dataClass the class of the data
	 * @param apiRequest the API request object to use for saving
	 */
	public SaveOperation(T data, Class<T> dataClass, ApiRequest apiRequest) {
		super(dataClass, apiRequest);
		mData = data;
	}

	//----------------------------------------------
	// Getter
	
	public T getData() {
		return mData;
	}

	//----------------------------------------------
	// Operation

	@Override
	public List<T> executeLocalOperationSync() throws Exception {
		getDao().createOrUpdate(mData);
		return wrapInList(mData);
	}
	
	public List<T> executeServerOperationSync() throws Exception {
		// Try to update on the server
		ApiResponse<?> apiResponse = getApiRequest().execute();
		
		Object result = apiResponse.getContent();

		// Save data coming from the server
		saveDataComingFromServer(unwrapList(result));
	
		return wrapInList(result);
	}
	
	@Override
	public final List<T> executeSync() throws Exception {
		// Save data in the database
		mData = unwrapList(executeLocalOperationSync());
		
		// Try to save on the server
		try {
			mData = unwrapList(executeServerOperationSync());			 
		} catch (Exception e) {
			DebugLog.d(TAG, "Failed to save data on the server: %s", e);
			
			// We must wait for the next synchronization
		}

		// Return the (possibly merged and saved) object
		return wrapInList(mData);
	}
	
	private void saveDataComingFromServer(T data) throws Exception {
		if(data != null) {
			// Hook
			onSaveDataComingFromServer(data);
			
			if (data instanceof Retrievable) {
				// Since this comes from the server, we can set the retrieved time to now
				((Retrievable) data).setRetrievedTime(System.currentTimeMillis());
			}
			if (data instanceof Updatable) {
				// We can now mark all fields as NOT locally updated
				((Updatable) data).clearLocallyUpdatedFieldsMarks();
			}
			
			// Save the merged object in the database
			getDao().createOrUpdate(data);
		}
	}	
	
	//----------------------------------------------
	// Hooks for subclasses

	protected void onSaveDataComingFromServer(T data) throws Exception {
		// Nothing by default
	}
	
}
