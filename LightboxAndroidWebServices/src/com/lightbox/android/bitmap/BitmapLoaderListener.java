/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.bitmap;

import com.lightbox.android.bitmap.BitmapSource.Type;

import android.graphics.Bitmap;

/** 
 * BitmapLoaderListener 
 * @author Fabien Devos
 */
public interface BitmapLoaderListener {
	
	void onLoaded(Bitmap bitmap, Type type, boolean isFromMemCache);
	
	void onFailure(Exception e);

}
