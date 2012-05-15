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
