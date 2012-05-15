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
