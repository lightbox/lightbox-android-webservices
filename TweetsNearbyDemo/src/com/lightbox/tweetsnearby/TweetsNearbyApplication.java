/**
 * Copyright (c) 2012 Lightbox
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
