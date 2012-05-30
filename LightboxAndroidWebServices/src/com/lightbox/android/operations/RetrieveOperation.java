/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.operations;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.lightbox.android.data.SaveBatchTask;
import com.lightbox.android.utils.debug.DebugLog;
import com.lightbox.android.webservices.requests.ApiRequest;
import com.lightbox.android.webservices.responses.ApiResponse;

/** 
 * RetrieveOperation 
 * @author Fabien Devos
 */
public class RetrieveOperation<T> extends AbstractOperation<T> {
	/** Used to tag logs */
	// @SuppressWarnings("unused")
	private static final String TAG = "RetrieveOperation";
	
	/** Cache duration for the retrieve operations, should be short since we always fall back on old data if no network
	 *  connection is available. */
	public static final long CACHE_DURATION = 5 * 60 * 1000; // 5min

	private QueryBuilder<T, String> mQueryBuilder = null;
	private PreparedQuery<T> mPreparedQuery;
	private List<T> mResultList = null;
	private boolean mRetrieveFromLocalDataOnly = false;
	
	//----------------------------------------------
	// Constructor
	
	/**
	 * Constructor. Note that you <strong>must</strong> call {@link #buildQuery()}, and optionally set some statements,
	 * before any attempt to execute this RetrieveOperation.
	 * @param dataOperation
	 * @param apiRequest
	 */
	public RetrieveOperation(Class<T> dataClass, ApiRequest apiRequest, boolean retrieveFromLocalDataOnly) {
		super(dataClass, apiRequest);
		mRetrieveFromLocalDataOnly = retrieveFromLocalDataOnly;
	}
	
	//----------------------------------------------
	// Query
	
	/**
	 * Use this method to build the query for this RetrieveOperation. You <strong>must</strong> call this method before
	 * executing the Operation. If you don't set anything on the returned {@link QueryBuilder}, it will simply returns
	 * every objects of the type T present in the database.
	 * @return a {@link QueryBuilder} that you can modify to set any constraint on the retrieve operation.
	 * @throws SQLException
	 */
	public QueryBuilder<T, String> buildQuery() throws SQLException {
		if (mQueryBuilder == null) {
			mQueryBuilder = getDao().queryBuilder();
		}
		return mQueryBuilder;
	}

	private PreparedQuery<T> getPreparedQuery() throws SQLException {
		if (mQueryBuilder == null) { throw new IllegalStateException("You must call buildQuery, and optionally set some statements, before executing RetrieveOperation."); }
		if (mPreparedQuery == null) { mPreparedQuery = mQueryBuilder.prepare(); }
		return mPreparedQuery;
	}
		
	//----------------------------------------------
	// Hooks for subclasses

	protected void onRetrieveDataFromDatabase(List<T> result) throws Exception {
		// Nothing by default
	}

	protected void onRetrieveDataFromServer(List<T> result) throws Exception {
		// Nothing by default
	}
	
	//----------------------------------------------
	// Operation

	// Override the default executeAsync to add support for caching on the main thread.
	@Override
	public final void executeAsync(OperationListener<T> listener) {
		mResultList = null;
		try {
			// Try to get data locally first
			mResultList = executeLocalOperationSync();
		} catch (Exception e) {
			Log.w(TAG, "Failed to retrieve data from db: %s", e);
		}
		
		if (mResultList != null) {
			// Call back on main thread
			if (listener != null) {
				listener.onSuccess(this, mResultList);
			}
		}

		if ( ! mRetrieveFromLocalDataOnly ) {
			// Proceed with normal operation execution
			super.executeAsync(listener);
		}
	}

	@Override
	public List<T> executeLocalOperationSync() throws Exception {
		
		// Query database
		PreparedQuery<T> preparedQuery = getPreparedQuery(); 		
		List<T> resultList = getDao().query(preparedQuery);
				
		// Hook
		if (resultList != null) { onRetrieveDataFromDatabase(resultList); }
		
		// Remove locally deleted data from the result list
		removeLocallyDeletedData(resultList);

		return resultList;
	}

	@Override
	public List<T> executeServerOperationSync() throws Exception {
		List<T> resultList = null;
		
		// Query server
		ApiResponse<?> apiResponse = getApiRequest().execute();				
		Object result = apiResponse.getContent();
		if (result == null) { throw new NullPointerException("Result was null"); }
		resultList = wrapInList(result);
		
		// Hook
		if (resultList != null) { onRetrieveDataFromServer(resultList); }

		// Save in database
		saveDataFromServer(resultList);
		
		return resultList;
	}
	
	@Override
	public final List<T> executeSync() throws Exception {
		List<T> resultList = mResultList;

		// If (there is no result OR the data are too old) AND there is no local update 
		if ( (resultList == null || isDataTooOld(resultList)) && ! hasLocalUpdate(resultList) ) {
			DebugLog.d(TAG, "(No result from database OR data is too old) AND no local updates: retrieving from network.");
			
			// Try to retrieve data from web services
			resultList = executeServerOperationSync();
			
			// No need to send back result from cache because it's already done
		}
		
		return resultList;
	}
	
	private static <T> boolean isDataTooOld(List<T> list) {
		boolean isDataTooOld = true;
		
		// in the empty case, we consider the data as too old, thus we will always check the network
		if (list.size() > 0) {
			try {
				// Take the oldest item and test if it is too old, if so, we consider the whole list as too old
				@SuppressWarnings("unchecked")
				List<Retrievable> retrievableList = (List<Retrievable>) list; 
				Retrievable oldest = Collections.min(retrievableList, Retrievable.BY_RETRIEVED_TIME);				
				isDataTooOld = (System.currentTimeMillis() - oldest.getRetrievedTime()) > CACHE_DURATION;
			} catch (ClassCastException e) {
				// Ignore
			}
		}

		return isDataTooOld;
	}
		
    private static <T> boolean hasLocalUpdate(List<T> list) {
		boolean hasLocalUpdate = false;
		if (list != null) {
			for (T object : list) {
				if (object instanceof Updatable) {
					Updatable updatable = (Updatable) object;
					if (updatable.getLocallyUpdatedFields().size() > 0) {
						hasLocalUpdate = true;
						break;
					}
				}
			}
		}
		return hasLocalUpdate;
	}
	
	private void saveDataFromServer(List<T> list) {
		try {
			for (T object : list) {
				// Since this comes from the server, we can set the retrieved time to now
				if (object instanceof Retrievable) {
					Retrievable retrievable = (Retrievable) object;
					retrievable.setRetrievedTime(System.currentTimeMillis());
				}
			}
			// Save in batch
			getDao().callBatchTasks(new SaveBatchTask<T>(list, getDataClass()));

		} catch (Exception e) {
			Log.w(TAG, "Failed to save data after retrieving from network.", e);
		}
	}
}
