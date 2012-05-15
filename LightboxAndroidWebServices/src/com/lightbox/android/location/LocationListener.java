/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.location;

import android.location.Location;

/** 
 * LocationListener 
 * @author Fabien Devos
 */
public interface LocationListener {

	void onFoundLocation(Location location);
	
	void onUnableToFindLocation(Exception exception);
	
}
