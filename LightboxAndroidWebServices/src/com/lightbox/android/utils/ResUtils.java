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
