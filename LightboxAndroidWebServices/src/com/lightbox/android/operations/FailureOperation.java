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

/** 
 * An Operation that always fail.
 * @author Fabien Devos
 */
public class FailureOperation<T> extends AbstractOperation<T> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "FailureOperation";
	
	private Exception mException;
	
	/**
	 * Constructor.
	 */
	public FailureOperation(Exception exception) {
		super(null, null);
		mException = exception;
	}

	@Override
	public final List<T> executeSync() throws Exception {
		throw mException;
	}

	@Override
	public List<T> executeLocalOperationSync() throws Exception {
		throw mException;
	}

	@Override
	public List<T> executeServerOperationSync() throws Exception {
		throw mException;
	}
	
	

}
