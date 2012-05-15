/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/** 
 * ResUtils 
 * @author Fabien Devos & Nilesh Patel
 */
public final class ResUtils {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "ResUtils";
	
    //------------------------------------------------------
    // Private constructor for utility class
    private ResUtils() {
        throw new UnsupportedOperationException("Sorry, you cannot instantiate an utility class!");
    }
    //------------------------------------------------------
    
    
    public static int dpToPx(Context context, float dp) {
        final float DENSITY = context.getResources().getDisplayMetrics().density;
        int px = Math.round(dp * DENSITY);
        return px;
    }

    public static int pxToDp(Context context, int px) {
        final float DENSITY = context.getResources().getDisplayMetrics().density;
        int dp = Math.round(px / DENSITY);
        return dp;
    }
    
    public static DisplayMetrics getDefaultDisplayMetrics(Activity activity) {
    	DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		return dm;
    }
    
}
