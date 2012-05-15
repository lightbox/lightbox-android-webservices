/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.bitmap;

/** 
 * BitmapSize 
 * @author Nilesh Patel
 */
public class BitmapSize {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "BitmapSize";
	
	public int width;
	public int height;
	
	public BitmapSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BitmapSize)) {
            return false;
        }
        BitmapSize size = (BitmapSize) obj;
        return width == size.width && height == size.height;
    }
	
	@Override
    public int hashCode() {
        return width * 313575 + height;
    }
}
