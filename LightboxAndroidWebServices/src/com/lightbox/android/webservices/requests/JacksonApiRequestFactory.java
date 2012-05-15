/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.webservices.requests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.content.res.AssetManager;

import com.lightbox.android.webservices.processors.JacksonProcessor;
import com.lightbox.android.webservices.processors.ParsingException;
import com.lightbox.android.webservices.processors.Processor;

/** 
 * <p>JacksonApiRequestFactory. Parse the API requests configuration file to create ApiRequest instances.
 * <p>NB:
 * <li>You can regroup URL base and Processor name in a common "base request", and the refer to it with "use" property.
 * <li>If you do not provide a method name, the key will be used.
 * @author Fabien Devos
 */
public class JacksonApiRequestFactory implements ApiRequestFactory {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "JacksonApiRequestFactory";

	public static String API_REQUESTS_FILENAME = "api_requests.json";
	
	private HashMap<String, ApiRequest> mTemplateApiRequests;
	private HashMap<String, String> mOverriddenBaseUrl = new HashMap<String, String>();
	
	/**
	 * Constructor.
	 */
	public JacksonApiRequestFactory(Context context) {
		Processor<ApiRequestsWrapper> processor = new JacksonProcessor<ApiRequestsWrapper>();
		AssetManager assetMgr = context.getAssets();
		
		try {
			ApiRequestsWrapper apiRequestsWrapper = (ApiRequestsWrapper) processor.parse(ApiRequestsWrapper.class, assetMgr.open(API_REQUESTS_FILENAME));
			mTemplateApiRequests = apiRequestsWrapper.getApiRequests();
		} catch (ParsingException e) {
			throw new RuntimeException(String.format("Unable to parse %s file!", API_REQUESTS_FILENAME), e);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Unable to parse %s file!", API_REQUESTS_FILENAME), e);
		}
		
		// Resolve references
		for (Entry<String, ApiRequest> entry : mTemplateApiRequests.entrySet()) {
			String key = entry.getKey();
			ApiRequest apiRequest = entry.getValue();
			String use = apiRequest.getUse();
			if (use != null) {
				ApiRequest baseApiRequest = mTemplateApiRequests.get(use);
				if (apiRequest.getMethodName() == null) {
					apiRequest.setMethodName(key);
				}
				if (apiRequest.getBaseUrl() == null) {
					apiRequest.setBaseUrl(baseApiRequest.getBaseUrl());
				}
				if (apiRequest.getProcessor() == null) {
					apiRequest.setProcessor(baseApiRequest.getProcessor());
				}
				if (apiRequest.getResponseClass() == null) {
					apiRequest.setResponseClass(baseApiRequest.getResponseClass());
				}
			}
		}
	}
	
	@Override
	public ApiRequest createApiRequest(String methodName) {
		final ApiRequest templateApiRequest = mTemplateApiRequests.get(methodName);
		if (templateApiRequest == null) { throw new IllegalArgumentException(String.format("The method %s does not exist. Please check the config in the %s file.", methodName, API_REQUESTS_FILENAME)); }
		
		ApiRequest apiRequest = new ApiRequest(templateApiRequest);

		// Override baseUrl if needed
		if (templateApiRequest.getUse() != null) {
			String newBaseUrl = mOverriddenBaseUrl.get(templateApiRequest.getUse());
			if (newBaseUrl != null) {
				apiRequest.setBaseUrl(newBaseUrl);
			}
		}
		
		return apiRequest;
	}
	
	/**
	 * Override the base URL for all requests which have "key" as their "use" property in the api request config file.
	 * Set baseUrl to null or empty string to restore default.
	 */
	@Override
	public void overrideBaseUrlForKey(String key, String baseUrl) {
		if (baseUrl == null || baseUrl.length() == 0) {
			mOverriddenBaseUrl.remove(key);
		} else {
			mOverriddenBaseUrl.put(key, baseUrl);
		}
	}
	
	@Override
	public String retrieveBaseUrlForKey(String key) {
		String baseUrl = mOverriddenBaseUrl.get(key);
		
		if (baseUrl == null) {
			for (ApiRequest templateApiRequest : mTemplateApiRequests.values()) {
				if (templateApiRequest.getUse() != null && templateApiRequest.getUse().equals(key)) {
					baseUrl = templateApiRequest.getBaseUrl();
					break;
				}
			}
		}
		return baseUrl;
	}
	
	/********************************************************
	 * ApiRequestsWrapper 
	 */
	protected static class ApiRequestsWrapper {
		private HashMap<String, ApiRequest> mApiRequests;
		
		public void setApiRequests(HashMap<String, ApiRequest> apiRequestsMap) {
			mApiRequests = apiRequestsMap;
		}
		
		public HashMap<String, ApiRequest> getApiRequests() {
			return mApiRequests;
		}
	}

}
