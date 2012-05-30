/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.data;

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import com.lightbox.android.tasks.BackgroundTask;

/** 
 * Helper methods related to local data. 
 * @author Fabien Devos
 */
public class Data {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "Data";
	
	private static OrmLiteSqliteOpenHelper sDatabaseHelper = null;
		
    //------------------------------------------------------
    // Private constructor for utility class
    private Data() {
        throw new UnsupportedOperationException("Sorry, you cannot instantiate an utility class!");
    }
    //------------------------------------------------------

    //----------------------------------------------
	// Database Helpers
	
	/**
	 * Only call this method once, from the onCreate of the Application object.
	 * @param context
	 */
	public static void init(Context context) {
		OpenHelperManager.setOpenHelperClass(DatabaseHelper.class);

		sDatabaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
		
		// Enable least-recently-used cache TODO doesn't seams to work properly ("status" doesn't seams to be updated
		// inside the memory cache): check why.
//		for (Class<?> dataClass : DatabaseHelper.DATA_CLASSES) {
//			try {
//				getDao(dataClass).setObjectCache(new LruObjectCache(ApiCache.MAX_ENTRIES_IN_MEMORY));
//			} catch (SQLException e) {
//				Log.w(TAG, e);
//			}
//		}
	}
	
	/**
	 * Deletes all records in a table
	 * @param dataClass Class representing the table to clear
	 */
	public static int clearTable(Class<?> dataClass) throws SQLException {
		if (sDatabaseHelper == null) { throw new IllegalStateException("You must call Data.init() before accessing clearTable"); }
		
		return TableUtils.clearTable(sDatabaseHelper.getConnectionSource(), dataClass);
	}

	/**
	 * Only call this method if you are absolutely sure that you won't use the database anymore.
	 */
	public static void release() {
		OpenHelperManager.releaseHelper();
	}
	
	public static <T> Dao<T, String> getDao(Class<T> dataClass) throws SQLException {
		if (sDatabaseHelper == null) { throw new IllegalStateException("You must call Data.init() before accessing Daos."); }
		
		return sDatabaseHelper.getDao(dataClass);
	}
		
	//----------------------------------------------
	// Database cleanup
	
	public static void startDatabaseCleanup() {
		BackgroundTask<Void> cleanupTask = new DatabaseCleanerTask();
		cleanupTask.execute();
	}

}
