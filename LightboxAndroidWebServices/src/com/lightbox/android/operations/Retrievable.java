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

import java.util.Comparator;

/** 
 * A Retrievable is an object that you can retrieve from the server. The last time the data was retrieved from the
 * server is saved locally.
 * @author Fabien Devos
 */
public interface Retrievable {
	
	/** This must be used as the name of the lastRetrievedTime field in every subclasses
	 *  (means retrieved <strong>with the value of the server</strong>) */
	public static final String RETRIEVED_TIME = "retrievedTime";

	/** Retrieve the last time the object was retrieved with the value of the <strong>server</strong> */
	long getRetrievedTime();

	/** Set the last time the object was retrieved with the value of the <strong>server</strong> */
	void setRetrievedTime(long retrievedTime);

	String getId();
	
	//----------------------------------------------
	// Comparator
	
	public static final Comparator<Retrievable> BY_RETRIEVED_TIME = new Retrievable.ByRetrievedTime();

	public class ByRetrievedTime implements Comparator<Retrievable> {
		@Override
		public int compare(Retrievable retrievable1, Retrievable retrievable2) {
		    if (retrievable1 == null && retrievable2 == null) { return 0; }
		    // null values are considered as old
		    if (retrievable1 != null && retrievable2 == null) return 1; 
		    if (retrievable1 == null && retrievable2 != null) return -1;
		    
			return (int) (retrievable1.getRetrievedTime() - retrievable2.getRetrievedTime());
		}
	}
}
