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
package com.lightbox.tweetsnearby;

import android.app.Application;

import com.lightbox.android.utils.AndroidUtils;
import com.lightbox.android.utils.debug.DebugLog;

/** 
 * TweetsNearbyApplication 
 * @author Fabien Devos
 */
public class TweetsNearbyApplication extends Application {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "TweetsNearbyApplication";
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		AndroidUtils.setApplicationContext(this);
		DebugLog.init(this);
	}
}
