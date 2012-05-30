/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.operations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.j256.ormlite.dao.Dao;
import com.lightbox.android.data.Data;
import com.lightbox.android.tasks.BackgroundTask;
import com.lightbox.android.utils.debug.DebugLog;
import com.lightbox.android.webservices.requests.ApiRequest;

/** 
 * Abstract the access to either the local database or the remote server.
 * @author Fabien Devos
 */
public abstract class AbstractOperation<T> implements Operation<T> {
	/** Used to tag logs */
	// @SuppressWarnings("unused")
	private static final String TAG = "AbstractOperation";
	private static final String ACCESS_TOKEN_KEY = "access_token";
		
	private Class<T> mDataClass;
	private ApiRequest mApiRequest;
	private String mId;
	
	//----------------------------------------------
	// Constructor
	
	/**
	 * Constructor.
	 */
	protected AbstractOperation(Class<T> dataClass, ApiRequest apiRequest) {
		mDataClass = dataClass;
		mApiRequest = apiRequest;
		
		if (mApiRequest != null) {
			mId = mApiRequest.computeId();
			addAuthentication();
		} else {
			mId = UUID.randomUUID().toString();
		}
	}
	
	//----------------------------------------------
	// Getters

	protected Class<T> getDataClass() {
		return mDataClass;
	}
	
	protected Dao<T, String> getDao() throws SQLException {
		return Data.getDao(mDataClass);
	}
	
	/** Do not modify the ApiRequest after the operation creation! */
	@Override
	public ApiRequest getApiRequest() {
		return mApiRequest;
	}
		
	@Override
	public Object getContext() {
		return null;
	}
	
	//----------------------------------------------
	// Operation execution	
	
	/** Must be called from main thread.
	 *  Most of the time, you won't need this method. It is provided as a convenient way to check if an operation is
	 *  already running and re-attach to it if so, with a single line of code.
	 *  <strong>Please note that the re-attachment is done automatically anyway if you simply call
	 *  {@link #executeAsync(OperationListener)} as usual.</strong>
	 *  Thus, this method is equivalent to call {@link #executeAsync(OperationListener)}
	 *  only if {@link #isRunning()} is true.
	 *  @return true if the Operation was already running and as been re-attached to the listener, false otherwise. */
	@Override
	public boolean reattachIfRunning(OperationListener<T> listener) {
		return reattachIfRunning(getId(), listener);
	}
	
	/** 
	 * @see #reattachIfRunning(OperationListener)
	 */
	public static <T> boolean reattachIfRunning(String id, OperationListener<T> listener) {
		if (isRunning(id)) {
			// If the task is already running, just update the listener
			@SuppressWarnings("unchecked")
			OperationTask<T> task = (OperationTask<T>) BackgroundTask.getRunningTask(id);
			task.setListener(listener);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void executeAsync(OperationListener<T> listener) {
		if ( ! reattachIfRunning(listener)) {
			// The task wasn't running: create a new task and execute it
			OperationTask<T> task = new OperationTask<T>(this, listener);
			task.setId(getId());
			task.execute();
		}
	}
	
	@Override
	public boolean isRunning() {
		return isRunning(getId());
	}
	
	public static boolean isRunning(String id) {
		return BackgroundTask.isRunning(id);
	}
	
	@Override
	public void cancel() {
		cancel(getId());
	}

	public static void cancel(String id) {
		BackgroundTask<?> task = BackgroundTask.getRunningTask(id);
		if (task != null) {
			task.cancel();
		}		
	}
	
	/*package*/ final List<T> performExecuteSync() throws Exception {
		List<T> resultList = null;		
		try {
			resultList = executeSync();
			// null is considered an error
			checkNotNull(resultList);
		} catch (Exception e) {			
			DebugLog.d(TAG, "%s", e);
			throw e;
		}
		return resultList;
	}

	private void checkNotNull(Object result) {
		if (result == null) {
			throw new RuntimeException("result is null");
		}
	}
	
	//----------------------------------------------
	// API for subclasses 
		
	/** Default implementation use the ApiRequest only to generate a unique id. And it generate this id when
	 *  the class is instantiated.
	 *  Subclasses that doesn't want this 
	 *  behavior should override this method, or override equals and hashCode directly. */
	@Override
	public String getId() {
		return mId;
	}
	 
	//----------------------------------------------
	// Helpers
	
	@SuppressWarnings("unchecked")
	protected List<T> wrapInList(Object object) {
		List<T> resultList = null;
		if (object != null) {
			if (object instanceof List) {
				resultList = (List<T>) object;
			} else {
				resultList = new ArrayList<T>(1);
				resultList.add((T) object);
			}
		}
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	protected T unwrapList(Object object) {
		T data;
		if (object instanceof List) {
			data = ((List<T>) object).get(0);
		} else {
			data = (T) object;
		}
		return data;
	}
	
	protected void removeLocallyDeletedData(List<T> list) {
		if (list != null) {
			for (Iterator<T> i = list.iterator(); i.hasNext(); ) {
				T object = i.next();
				if(object instanceof Updatable) {
					Updatable updatable = (Updatable) object;
					if (updatable.isMarkedAsLocallyDeleted()) {
						i.remove();
					}
				}
			}
		}
	}
	
	protected void addAuthentication() {
//    	String accessToken = CurrentUser.getAccessToken();
//    	if(accessToken != null && !accessToken.equals("")) {	
//    		getApiRequest().addUrlParameter(ACCESS_TOKEN_KEY, accessToken);				
//    	}
	}
	
	protected void removeAuthentication() {
		getApiRequest().getUrlParameters().remove(ACCESS_TOKEN_KEY);
	}
	
	//----------------------------------------------
	// Object implementation
	
    @Override
    public boolean equals(Object o) {
        if (o == this) { return true; }
        if ( ! (o instanceof AbstractOperation)) { return false; }
        
        AbstractOperation<?> operation = (AbstractOperation<?>) o;
        return getId().equals(operation.getId());
    }
    
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return getId();
    }
	
}
