/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.data;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import com.j256.ormlite.dao.Dao;

/** 
 * DeleteBatchTask 
 * @author Fabien Devos
 */
public class DeleteBatchTask<T> implements Callable<Void> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "DeleteBatchTask";
	
	private Class<T> mDataClass;
	private List<T> mObjectsToDelete;
	
	public DeleteBatchTask(List<T> objectsToDelete, Class<T> dataClass) throws SQLException {
		mObjectsToDelete = objectsToDelete;
		mDataClass = dataClass;
	}

	@Override
	public Void call() throws Exception {
		Dao<T, String> dao = Data.getDao(mDataClass);
		for(T currentObject : mObjectsToDelete) {
			dao.delete(currentObject);
		}
		return null;
	}
}
