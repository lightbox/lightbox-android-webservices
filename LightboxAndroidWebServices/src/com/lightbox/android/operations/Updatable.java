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

import java.util.Set;

/** 
 * An Updatable object is any data that you can save/delete locally, and that will get synchronized to the
 * server.
 * <p>Steps:
 *  <ul>
 *    <li>First we mark the object (or the fields) as locally deleted/updated 
 *    <li>Then we try to update the object on the server via the server API
 *    <li>If everything goes well we clear the updated flags (or really delete the object)
 *    <li>And finally we save the time at which the final sync occurred on device (locally updated time)  
 *  </ul>
 * @author Fabien Devos
 */
public interface Updatable {

	//----------------------------------------------
	// For Save (update or create)
	
	/** This must be used as the name of the locallyUpdatedFields field in every subclasses */
	public static final String LOCALLY_UPDATED_FIELDS = "locallyUpdatedFields";

	Set<String> getLocallyUpdatedFields();
	
	public void markAsLocallyUpdated(String fieldName);

	public void clearLocallyUpdatedFieldsMarks();

	//----------------------------------------------
	// For Delete
	
	/** This must be used as the name of the isLocallyDeleted field in every subclasses */
	public static final String IS_LOCALLY_DELETED = "isLocallyDeleted";
	
	void markAsLocallyDeleted();
	
	boolean isMarkedAsLocallyDeleted();

}
