/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.tasks;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** 
 * DefaultExecutor. Designed to replace the executor used in AsyncTask which is wrongly configured with a bounded queue.
 * 
 * @author Fabien Devos
 */
public class DefaultExecutor extends ThreadPoolExecutor {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "DefaultExecutor";
	
    private static final int DEFAULT_CORE_POOL_SIZE = 5; // 5 Threads maximum running in parallel
    private static final int MAXIMUM_POOL_SIZE = Integer.MAX_VALUE; // Will be ignored as long as the queue is unbounded
    private static final int KEEP_ALIVE = 1; //(in seconds)
    
    public DefaultExecutor() {
        super(  DEFAULT_CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
    				@Override
    				public Thread newThread(Runnable r) {
    					Thread thread = new Thread(r);
    					thread.setName(TAG + " | " + thread.getName());
    					thread.setPriority(Thread.NORM_PRIORITY);
    					return thread;
    				}
    			});
    }
    

}
