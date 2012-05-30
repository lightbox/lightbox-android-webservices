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
package com.lightbox.android.data;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import com.j256.ormlite.dao.Dao;

/** 
 * SaveBatchTask 
 * @author Sarp Centel
 */
public class ClearAndSaveBatchTask<T> implements Callable<Void> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "SaveBatchTask";
	
	private Class<T> mDataClass;
	private List<T> mObjectsToSave;
	
	public ClearAndSaveBatchTask(List<T> objectsToSave, Class<T> dataClass) throws SQLException {
		mObjectsToSave = objectsToSave;
		mDataClass = dataClass;
	}

	@Override
	public Void call() throws Exception {
		Dao<T, String> dao = Data.getDao(mDataClass);
		Data.clearTable(mDataClass);
		for(T currentObject : mObjectsToSave) {
			dao.createOrUpdate(currentObject);
		}
		return null;
	}
}
