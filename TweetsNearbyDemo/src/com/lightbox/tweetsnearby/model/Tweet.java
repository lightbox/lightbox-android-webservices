package com.lightbox.tweetsnearby.model;

import java.net.URI;

import android.net.Uri;
import android.os.Environment;

import com.lightbox.android.bitmap.BitmapSource;
import com.lightbox.android.utils.AndroidUtils;
import com.lightbox.android.utils.debug.DebugLog;

/**
 * Tweet 
 * @author Fabien Devos
 */
public class Tweet implements BitmapSource {
	private String mProfileImageUrl;
	private String mText;
	private String mFromUser;
	
	public String getProfileImageUrl() {
		return mProfileImageUrl;
	}
	public void setProfileImageUrl(String profileImageUrl) {
		mProfileImageUrl = profileImageUrl;
	}
	public String getText() {
		return mText;
	}
	public void setText(String text) {
		mText = text;
	}
	public String getFromUser() {
		return mFromUser;
	}
	public void setFromUser(String fromUser) {
		mFromUser = fromUser;
	}
	
	//----------------------------------------------
	// Object
	
	@Override
	public String toString() {
		return mText;
	}
	
	//----------------------------------------------
	// BitmapSource
	
	protected static final String EXTERNAL_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory().toString();
	public static final String ROOT_DIRECTORY  = EXTERNAL_STORAGE_DIRECTORY + "/" + AndroidUtils.getApplicationLabel() + "/";
	public static final String CACHE_DIRECTORY = ROOT_DIRECTORY + ".Cache" + "/";
	
	@Override
	public String getAbsoluteFileName(Type type) {
		return CACHE_DIRECTORY + Uri.encode(mProfileImageUrl);
	}
	@Override
	public String getTitle() {
		return mText;
	}
	@Override
	public URI getUri(Type type) {
		DebugLog.d("Tweet", URI.create(mProfileImageUrl).toString());
		return URI.create(mProfileImageUrl);
	}
}