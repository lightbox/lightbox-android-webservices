/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.operations;

import java.util.List;

/** 
 * OperationListener 
 * @author Fabien Devos
 */
public interface OperationListener<T> {
		
	void onSuccess(Operation<T> operation, List<T> result);
	
	void onFailure(Operation<T> operation, Exception e);

}
