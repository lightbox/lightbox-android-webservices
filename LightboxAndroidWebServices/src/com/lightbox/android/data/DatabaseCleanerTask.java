/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.data;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.lightbox.android.operations.Retrievable;
import com.lightbox.android.operations.Updatable;
import com.lightbox.android.tasks.BackgroundTask;

/** 
 * DatabaseCleanerTask 
 * @author Fabien Devos
 */
public class DatabaseCleanerTask extends BackgroundTask<Void> {
	/** Used to tag logs */
	// @SuppressWarnings("unused")
	private static final String TAG = "DatabaseCleanerTask";
		
	// Note: this is the duration for keeping data in the database, that will allow retrieving of data 
	// if OFFLINE mode. This does NOT affect the duration for returning database results rather than trying to fetching
	// the network. See RetreivingOperation for that.
	private static final long DEFAULT_DATABASE_CACHE_DURATION = 7 * 24 * 60 * 60 * 1000; // 1 week
	private static final long USER_DATABASE_CACHE_DURATION = 3 * 7 * 24 * 60 * 60 * 1000; // 3 weeks
	
	private static ExecutorService sDatabaseCleanerExecutor;
	
	/**
	 * Constructor.
	 */
	public DatabaseCleanerTask() {
		super(getExecutor());
	}
	
	private static ExecutorService getExecutor() {
		if (sDatabaseCleanerExecutor == null) {
			sDatabaseCleanerExecutor = Executors.newSingleThreadExecutor(
					new ThreadFactory() {
						@Override
						public Thread newThread(Runnable r) {
							Thread thread = new Thread(r);
							thread.setName(TAG + " | " + thread.getName());
							thread.setPriority(Thread.MIN_PRIORITY);
							return thread;
						}
					});
		}
		return sDatabaseCleanerExecutor;
	}

	@Override
	protected Void doWorkInBackground() throws Exception {
		try {
			final long DEFAULT_CUTOFF_TIME = System.currentTimeMillis() - DEFAULT_DATABASE_CACHE_DURATION;
			for (Class<?> dataClass : DatabaseHelper.getDataClasses()) {
				if (Updatable.class.isAssignableFrom(dataClass)) {
					long cutoffTime;
//					if (dataClass == User.class) {
//						cutoffTime = System.currentTimeMillis() - USER_DATABASE_CACHE_DURATION;
//					} else {
						cutoffTime = DEFAULT_CUTOFF_TIME;
//					}
					cleanUpOlderThan(cutoffTime, dataClass);
				}
			}
			
		} catch (Throwable t) {
			// We NEVER want to crash while performing a clean-up
			Log.w(TAG, "Unable to complete database clean-up.", t);
		}
		return null;
	}
	
	private static <T> void cleanUpOlderThan(long cutoffTime, Class<T> dataClass) throws Exception {
			Dao<T, String> dao = Data.getDao(dataClass);
			PreparedQuery<T> query = dao.queryBuilder()
					.where().lt(Retrievable.RETRIEVED_TIME, cutoffTime).prepare();
			List<T> oldDataList = dao.query(query);
			for (T data : oldDataList) {
				// Prevent deletion of objects with local updates
				boolean canDelete = true;
				if (data instanceof Updatable) {
					Updatable updatable = (Updatable) data;
					canDelete = (updatable.getLocallyUpdatedFields().isEmpty() && ! updatable.isMarkedAsLocallyDeleted());
				}

				if (canDelete) {
					dao.delete(data);
				}
			}
	}

	@Override
	protected void onCompleted(Void result) {
		// Nothing
	}

	@Override
	protected void onFailed(Exception e) {
		// Nothing
	}
	
}
