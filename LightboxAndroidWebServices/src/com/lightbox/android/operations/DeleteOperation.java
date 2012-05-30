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
package com.lightbox.android.operations;

import java.util.List;

import com.lightbox.android.utils.debug.DebugLog;
import com.lightbox.android.webservices.requests.ApiRequest;

/** 
 * DeleteOperation 
 * @author Fabien Devos
 */
public class DeleteOperation<T> extends AbstractOperation<T> {
	/** Used to tag logs */
	// @SuppressWarnings("unused")
	private static final String TAG = "DeleteOperation";
	
	private T mData;
	
	//----------------------------------------------
	// Constructor
	
	/**
	 * Constructor.
	 * @param dataAction
	 * @param apiRequest
	 */
	public DeleteOperation(T data, Class<T> dataClass, ApiRequest apiRequest) {
		super(dataClass, apiRequest);
		
		mData = data;
	}
	
	//----------------------------------------------
	// Getter
	
	protected T getData() {
		return mData;
	}
	
	//----------------------------------------------
	// Operation
	
	@Override
	public List<T> executeLocalOperationSync() throws Exception {
		// Mark the object as deleted in the database
		if (mData instanceof Updatable) {
			Updatable updatable = (Updatable) mData;
			updatable.markAsLocallyDeleted();
			getDao().update(mData);
		}
		return wrapInList(mData);
	}
	
	@Override
	public List<T> executeServerOperationSync() throws Exception {
		getApiRequest().execute();
		// At this point if there is no exception, the object should be deleted on the server side
		
		// Finally delete the data from the database
		getDao().delete(mData);
		return wrapInList(mData);
	}
	
	@Override
	public final List<T> executeSync() throws Exception {		
		List<T> resultList;
		
		// Mark the object as deleted in the database
		resultList = executeLocalOperationSync();
		
		// Try to update on the server
		try {
			resultList = executeServerOperationSync();
			
		} catch (Exception e) {
			DebugLog.d(TAG, "Failed to delete data on the server: %s", e);
			
			// We must wait for the next synchronization
		}

		// Return the deleted object
		return resultList;
	}

}
