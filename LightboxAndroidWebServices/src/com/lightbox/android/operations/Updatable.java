/**
 * Copyright (c) 2011 Lightbox
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
