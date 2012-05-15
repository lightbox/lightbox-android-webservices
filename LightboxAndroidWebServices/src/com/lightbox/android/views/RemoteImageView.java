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
package com.lightbox.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageSwitcher;

import com.lightbox.android.bitmap.BitmapLoaderListener;
import com.lightbox.android.bitmap.BitmapSource;


/**
 * RemoteImageView 
 * @author Nilesh Patel
 */
public abstract class RemoteImageView extends ImageSwitcher implements BitmapLoaderListener {

	/**
	 * Constructor.
	 * @param context
	 * @param attrs
	 */
	public RemoteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Constructor.
	 * @param context
	 */
	public RemoteImageView(Context context) {
		super(context);
	}
	
	public abstract void loadOnlyIfInMemCache(BitmapSource source, int position);
	
	public abstract void cancelLoading();

	public abstract void resumeLoading();
	
	public abstract void reset();
	
	
	public interface RemoteImageViewHolder {
		public void resetImageViews();
		public void startLoading(BitmapSource source);
		public void loadOnlyIfInMemCache(BitmapSource source);
		public void cancelLoading();
		public void resumeLoading();
		public void clearTouchState();
	}
}