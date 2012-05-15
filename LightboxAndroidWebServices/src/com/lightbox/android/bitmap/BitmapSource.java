/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.bitmap;

import java.net.URI;

/** 
 * This class represent a universal source for retrieving bitmaps.
 * @author Fabien Devos
 */
public interface BitmapSource {
	public static final int THM_SIZE_PX = 128;
	
	/**
	 * The type of bitmap (thumbnail or large)
	 */
	public enum Type {
		LRG("lrg"),
		MED("med"),
		THM("thm");
		String mStrRep;
		Type(String strRep) {
			mStrRep = strRep;
		}
		@Override
		public String toString() {
			return mStrRep;
		}
	}
	
	String getTitle();
	
	/** Return the online URI for this Bitmap, or null if there is none. */
	URI getUri(Type type);

	/** Return the absolute file name on disk for this Bitmap, or null if there is none. */
	String getAbsoluteFileName(Type type);
	
}
