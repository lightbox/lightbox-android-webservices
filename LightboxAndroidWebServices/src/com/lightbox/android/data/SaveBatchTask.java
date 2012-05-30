/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.data;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import com.j256.ormlite.dao.Dao;

/** 
 * SaveBatchTask 
 * @author Fabien Devos
 */
public class SaveBatchTask<T> implements Callable<Void> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "SaveBatchTask";
	
	private Class<T> mDataClass;
	private List<T> mObjectsToSave;
	
	public SaveBatchTask(List<T> objectsToSave, Class<T> dataClass) throws SQLException {
		mObjectsToSave = objectsToSave;
		mDataClass = dataClass;
	}

	@Override
	public Void call() throws Exception {
		Dao<T, String> dao = Data.getDao(mDataClass);
		for(T currentObject : mObjectsToSave) {
			dao.createOrUpdate(currentObject);
		}
		return null;
	}
}
