package com.lightbox.android.data;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.lightbox.android.utils.debug.DebugLog;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "DatabaseHelper";
	
	public static final String DATABASE_NAME = "data.db";
	private static final int DATABASE_VERSION = 1;
	
	public static final Class<?>[] DATA_CLASSES = 
			//===============================
			// New tables must be added here, separated with a comma
			{
//				UserPhoto.class,
//				Place.class
			};
			//===============================

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
//		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	public static List<Class<?>> getDataClasses() {
		return Collections.unmodifiableList(Arrays.asList(DATA_CLASSES));
	}
	
	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			DebugLog.d(TAG, "Create database");

			// Create all tables
			for (Class<?> dataClass : DATA_CLASSES) {
				TableUtils.createTable(connectionSource, dataClass);
			}

		} catch (SQLException e) {
			Log.e(TAG, "Can't create database", e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			DebugLog.d(TAG, "onUpgrade: Dropping tables");

			// Drop all tables
			for (Class<?> dataClass : DATA_CLASSES) {
				TableUtils.dropTable(connectionSource, dataClass, true);
			}

			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
			
		} catch (SQLException e) {
			Log.e(TAG, "Can't upgrade database", e);
		}
	}

}