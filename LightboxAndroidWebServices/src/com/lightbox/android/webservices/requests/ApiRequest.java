/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.webservices.requests;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import android.content.Context;

import com.lightbox.android.network.HttpHelper;
import com.lightbox.android.network.HttpHelper.HttpMethod;
import com.lightbox.android.utils.AndroidUtils;
import com.lightbox.android.utils.debug.DebugLog;
import com.lightbox.android.webservices.processors.ParsingException;
import com.lightbox.android.webservices.processors.Processor;
import com.lightbox.android.webservices.responses.ApiException;
import com.lightbox.android.webservices.responses.ApiResponse;

/** 
 * ApiRequest is the main entry point for doing any call to any web service. Usually, ApiRequest objcts are created
 * through an {@link ApiRequestFactory}. You can create your own or use the default one.
 * @see ApiResponse
 * @see ApiRequestFactory
 * @author Fabien Devos
 */
public class ApiRequest {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "ApiRequest";

	private String mMethodName;
	private String mBaseUrl;
	private String mPath;
	private Processor<ApiResponse<?>> mProcessor;
	private HttpMethod mHttpMethod;
	private Class<ApiResponse<?>> mResponseClass;
	private HashMap<String, Object> mUrlParameters;
	private Object mBody;
	private HashMap<String, String> mHeaders;

	private String mUse; // used by the configuration 
		
	//----------------------------------------------
	// Constructors
	
	/**
	 * Constructor.
	 */
	public ApiRequest() {
	}

	/**
	 * Copy Constructor.
	 */
	public ApiRequest(ApiRequest apiRequest) {
		mMethodName = apiRequest.mMethodName;
		mBaseUrl = apiRequest.mBaseUrl;
		mPath = apiRequest.mPath;
		mProcessor = apiRequest.mProcessor;
		mHttpMethod = apiRequest.mHttpMethod;
		mResponseClass = apiRequest.mResponseClass;
		mUrlParameters = apiRequest.mUrlParameters;
		mBody = apiRequest.mBody;
		mHeaders = apiRequest.mHeaders;
		mUse = apiRequest.mUse;
	}
	
	//----------------------------------------------
	// Requesting
	
	public HttpResponse callApi() throws IOException {
		String path = mPath;
		HashMap<String, Object> parameters = null;		
		if (mUrlParameters != null) {
			parameters = new HashMap<String, Object>(mUrlParameters);
			// Insert parameters in path, and remove them from the parameter map
			path = insertParametersInPath(path, parameters);
		}
		
		return HttpHelper.getInstance().call(
				mHttpMethod,
				URI.create(mBaseUrl + path),
				parameters,
				mBody,
				mHeaders);
	}
	
    private static String insertParametersInPath(String url, Map<String, Object> parameters) {
        StringBuilder urlStringBuilder = new StringBuilder(url);
        if(parameters != null) {
        	for (Iterator<Entry<String, Object>> iterator = parameters.entrySet().iterator(); iterator.hasNext();) {
        		Entry<String, Object> paramEntry = iterator.next();
            	String pathParamName = String.format("{%s}", paramEntry.getKey());
                int startIndex = urlStringBuilder.indexOf(pathParamName);
                if(startIndex != -1) {
                	// We found the parameter name in the path: replace it, and remove it from the parameter map
                    int endIndex = startIndex + pathParamName.length();
                    urlStringBuilder.replace(startIndex, endIndex, paramEntry.getValue().toString());
                    iterator.remove();
                }        	
            }
        }
        return urlStringBuilder.toString();
    }
	
	public ApiResponse<?> parseInputStream(InputStream inputStream) throws ParsingException, IOException, ApiException {
		ApiResponse<?> result;
		try {
			// For debugging API response
			inputStream = printJsonInDebugMode(inputStream);
			
			result = mProcessor.parse(mResponseClass, inputStream);
			if(result.hasError()) {
				throw result.getException();
			}
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		return result;
	}
	
	private InputStream printJsonInDebugMode(InputStream inputStream) {
		Context context = AndroidUtils.getApplicationContext();
		if (context != null && AndroidUtils.isDebuggable(context)) {
			String jsonStr;
			try {
				jsonStr = IOUtils.toString(inputStream);
				inputStream = IOUtils.toInputStream(jsonStr);
				DebugLog.d(TAG, jsonStr);
			} catch (IOException e) { /* Ignore */ }
		}
		return inputStream;
	}

	/**
	 * <p>Synchronously execute this {@link ApiRequest}. <strong>Do not use on the main thread</strong>
	 * <p>Will call the proper web service, and parse the response.
	 * @return an {@link ApiResponse} that is the result of this ApiRequest. You are responsible for checking if it 
	 * contains an {@link ApiException} or not, using {@link ApiResponse#hasError()}.
	 * @throws ParsingException if something goes wrong with the parsing of the response.
	 * @throws IOException if something goes wrong with IO (usually because no network connection is available).
	 * @throws ApiException if the API returned an error.
	 */
	public ApiResponse<?> execute() throws ParsingException, IOException, ApiException {
		ApiResponse<?> apiResponse = parseInputStream(callApi().getEntity().getContent());
		return apiResponse;
	}
	
	/**
	 * <p>Asynchronously execute this {@link ApiRequest}.
	 * <p>Will call the proper web service, and parse the response.
	 * @see ApiRequestListener
	 * @param listener the listener to call back when finished (usually an Activity). <strong>Note that the reference
	 * to this listener will be kept weakly! It means that if you, or the system, do not hold a reference to this
	 * listener, you won't be called back.</strong>
	 */
	public void executeAsync(ApiRequestListener listener) {
		ApiRequestTask apiRequestTask = new ApiRequestTask(listener, this);
		apiRequestTask.execute();
	}
	

	
	//----------------------------------------------
	// Getters / Setters
	
	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return mBaseUrl;
	}
	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		mBaseUrl = baseUrl;
	}
	/**
	 * @return the path
	 */
	public String getPath() {
		return mPath;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		mPath = path;
	}
	/**
	 * @return the processor
	 */
	public Processor<ApiResponse<?>> getProcessor() {
		return mProcessor;
	}
	/**
	 * @param processor the processor to set
	 */
	public void setProcessor(Processor<ApiResponse<?>> processor) {
		mProcessor = processor;
	}

	public void setProcessorClass(Class<Processor<ApiResponse<?>>> processorClass) {
		try {
			mProcessor = processorClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the httpMethod
	 */
	public HttpMethod getHttpMethod() {
		return mHttpMethod;
	}
	/**
	 * @param httpMethod the httpMethod to set
	 */
	public void setHttpMethod(HttpMethod httpMethod) {
		mHttpMethod = httpMethod;
	}
	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return mMethodName;
	}
	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		mMethodName = methodName;
	}
	/**
	 * @return the responseClass
	 */
	public Class<ApiResponse<?>> getResponseClass() {
		return mResponseClass;
	}
	/**
	 * @param responseClass the responseClass to set
	 */
	public void setResponseClass(Class<ApiResponse<?>> responseClass) {
		mResponseClass = responseClass;
	}
	/**
	 * @return the use
	 */
	public String getUse() {
		return mUse;
	}
	/**
	 * @param use the use to set
	 */
	public void setUse(String use) {
		mUse = use;
	}

	/**
	 * @return the urlParameters
	 */
	public HashMap<String, Object> getUrlParameters() {
		return mUrlParameters;
	}

	/**
	 * @param urlParameters the urlParameters to set
	 */
	public void setUrlParameters(HashMap<String, Object> urlParameters) {
		mUrlParameters = urlParameters;
	}
	
	public void addUrlParameter(String key, Object value) {
		if (mUrlParameters == null) {
			mUrlParameters = new HashMap<String, Object>();
		}
		mUrlParameters.put(key, value);
	}

	/**
	 * @return the body
	 */
	public Object getBody() {
		return mBody;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(Object body) {
		mBody = body;
	}

	/**
	 * @return the headers
	 */
	public HashMap<String, String> getHeaders() {
		return mHeaders;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(HashMap<String, String> headers) {
		mHeaders = headers;
	}

	//----------------------------------------------
	// Compute id

	public String computeId() {
		StringBuilder idStrBuilder = new StringBuilder(mHttpMethod.toString());
		idStrBuilder.append(mBaseUrl);
		idStrBuilder.append(mPath);
		if (mUrlParameters != null) {
			idStrBuilder.append(new TreeMap<String, Object>(mUrlParameters)); // Sort the map for deterministic ordering
		}
		if (mBody != null) {
			if (mBody instanceof Map) {
				Map<?, ?> bodyMap = (Map<?, ?>) mBody;
				TreeMap<?, ?> headersTreeMap = new TreeMap<Object, Object>(bodyMap);
				idStrBuilder.append(headersTreeMap);
			} else {
				idStrBuilder.append(mBody);			
			}
		}
		
		return idStrBuilder.toString();
	}	

}
