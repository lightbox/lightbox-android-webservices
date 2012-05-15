/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.net.Uri;

import com.lightbox.android.utils.AndroidUtils;
import com.lightbox.android.utils.debug.DebugLog;

/** 
 * HttpHelper 
 * @author Fabien Devos
 */
public class HttpHelper {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "HttpHelper";
	
	private static final int HTTP_TIMEOUT = 30 * 1000; // 30 sec
	private static final int SOCKET_BUFFER_SIZE = 8 * 1024; // 8 Ko
	private static final String STRING_HTTP = "http";
	private static final String STRING_HTTPS = "https";
	private static final String USER_AGENT_FORMAT_STRING = "%s/%s (Android %s; %s)";
	
	private DefaultHttpClient mHttpClient = null;
	
	/** Used to select an HTTP method. */ 
	public enum HttpMethod {
		GET,
		PUT,
		POST,
		DELETE;
		public HttpUriRequest createHttpRequest(URI uri) {
			switch(this) {
				case GET:
					return new HttpGet(uri);
				case PUT:
					return new HttpPut(uri);
				case POST:
					return new HttpPost(uri);
				case DELETE:
					return new HttpDelete(uri);
			}
			throw new IllegalArgumentException(String.format("Unknown Http Method: %s.", this.toString()));
		}
	}
	
	//----------------------------------------------------------------------------
	// Singleton pattern
	private HttpHelper() {
        mHttpClient = createHttpClient();
	}

	/** HttpHelperHolder is loaded on the first execution of HttpHelper.getInstance() 
	 * or the first access to HttpHelperHolder.INSTANCE, not before. */
	private static class HttpHelperHolder {
		private static final HttpHelper INSTANCE = new HttpHelper();
	}

	/** @return a unique instance of the class */
	public static HttpHelper getInstance() {
		return HttpHelperHolder.INSTANCE;
	}

	/** Not supported
	 * @throws CloneNotSupportedException (every time) */
	public Object clone() throws CloneNotSupportedException {
		// to prevent any kind of cheating
		throw new CloneNotSupportedException();
	}
	//----------------------------------------------------------------------------
	
	/**
	 * Create an HttpClient.
	 * @return a properly set HttpClient
	 */
	private static DefaultHttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme(STRING_HTTP, PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme(STRING_HTTPS, SSLSocketFactory.getSocketFactory(), 443));
		HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
		ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
		HttpConnectionParams.setSocketBufferSize(params, SOCKET_BUFFER_SIZE);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setHttpElementCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUserAgent(params, String.format(USER_AGENT_FORMAT_STRING,
				AndroidUtils.getApplicationLabel(),
				AndroidUtils.getVersionCode(),
				android.os.Build.VERSION.RELEASE,
				android.os.Build.MODEL));
        
		DefaultHttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);
		enableGzipCompression(client);
		return client;
	}
	
	/**
	 * Set Http Basic Authentication. 
	 * @param hostname
	 * @param username
	 * @param password
	 */
	public void setBasicAuth(String hostname, String username, String password) {
		if (hostname != null && username != null && password != null) {
			mHttpClient.getCredentialsProvider().setCredentials(
					new AuthScope(hostname, 443, AuthScope.ANY_REALM),
					new UsernamePasswordCredentials(username, password));
			mHttpClient.getCredentialsProvider().setCredentials(
				new AuthScope(hostname, 80, AuthScope.ANY_REALM),
				new UsernamePasswordCredentials(username, password));
		}
	}

	//-------------------------------------------------------------------------------------------
	// HTTP Methods call
	
	/**
	 * Call the specified {@link HttpMethod}.
	 * Convenience method for {@link #call(HttpMethod, URI, Map, Object, Map)}.
	 * @see #call(HttpMethod, URI, Map, Object, Map)
	 */
    public HttpResponse call(HttpMethod method, URI uri, Map<String, Object> urlParameters) throws IOException {
		return call(method, uri, urlParameters, null);
    }
    
	/**
	 * Call the specified {@link HttpMethod}.
	 * Convenience method for {@link #call(HttpMethod, URI, Map, Object, Map)}.
	 * @see #call(HttpMethod, URI, Map, Object, Map)
	 */
    public HttpResponse call(HttpMethod method, URI uri, Map<String, Object> urlParameters, Object body) throws IOException {
		return call(method, uri, urlParameters, body, null);
    }
	
	/**
	 * Call the specified {@link HttpMethod}.
	 * @param method
	 * @param uri
	 * @param urlParameters Optional.
	 * @param body Optional. If you pass a Map, it will be passed as Form-URLEncoded, if you pass an {@link HttpEntity}
	 * it will be used directly, otherwise the toString will be used.
	 * @param headers Optional.
	 * @return the {@link HttpResponse}
	 * @throws IOException if the request cannot be sent.
	 */
    public HttpResponse call(HttpMethod method, URI uri, Map<String, Object> urlParameters, Object body, Map<String, String> headers) throws IOException {
	
    	// Add query parameters
    	uri = addQueryParametersToUri(uri, urlParameters);
    	    	
        // Create the http request
    	HttpUriRequest httpRequest = method.createHttpRequest(uri);

    	// Add body
    	httpRequest = addBodyToHttpRequest(httpRequest, body);
    	
    	// Set language
    	httpRequest.setHeader("Accept-Language", Locale.getDefault().toString().replace('_', '-'));
    	
        // Set headers
        if(headers != null) {
            for(Map.Entry<String, String> header : headers.entrySet()) {
                httpRequest.setHeader(header.getKey(), header.getValue());
            }
        } 
        
        // Send the request
        DebugLog.d(TAG, "Calling %s", uri.toString());
        HttpResponse httpResponse = mHttpClient.execute(httpRequest);

        // Check for redirect
        final int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
            Header locationHeader = httpResponse.getFirstHeader("Location");
            if (locationHeader != null) {
                String newUrl = locationHeader.getValue();
                // call again with new URL
                return call(method, URI.create(newUrl), urlParameters, body, headers);
            } 
        }
        
        // return a httpResponse
        return httpResponse; 
    }
	    
    private static URI addQueryParametersToUri(URI uri, Map<String, Object> urlParameters) {
    	if (urlParameters != null && urlParameters.size() > 0) {
	    	Uri androidUri = Uri.parse(uri.toString());
	    	Uri.Builder uriBuilder = androidUri.buildUpon();
	    	for (Map.Entry<String, Object> param : urlParameters.entrySet()) {
	        	uriBuilder.appendQueryParameter(param.getKey(), param.getValue().toString());
			}
	    	uri = URI.create(uriBuilder.build().toString());
    	}
    	return uri;
    }
    
    private static HttpUriRequest addBodyToHttpRequest(HttpUriRequest request, Object body) throws IOException {
    	if (body != null && request instanceof HttpEntityEnclosingRequest) {
    		HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest) request;
    		
    		// Form case
    		if (body instanceof Map) {
    			Map<?, ?> bodyMap = (Map<?,?>) body;
    			entityEnclosingRequest.setEntity(new UrlEncodedFormEntity(mapToNameValueList(bodyMap), HTTP.UTF_8));
    	    // Already an entity case
    		} else if (body instanceof HttpEntity) {
    			HttpEntity entity = (HttpEntity) body;
    			entityEnclosingRequest.setEntity(entity);
    		// Fall-back: string case
    		} else {
    			entityEnclosingRequest.setEntity(new StringEntity(body.toString(), HTTP.UTF_8));
    		}
    	}
		return request;
    }
    
    public static List<NameValuePair> mapToNameValueList(Map<?, ?> map) throws UnsupportedEncodingException {
		List<NameValuePair> nameValueList = new ArrayList<NameValuePair>(map.size());
		for(Map.Entry<?, ?> param : map.entrySet()) {
			NameValuePair nameValuePair = new BasicNameValuePair(param.getKey().toString(), param.getValue().toString());
			nameValueList.add(nameValuePair);
		}
		return nameValueList;
    }
    
	//-------------------------------------------------------------------------------------------
	// GZip compression
	
	private static void enableGzipCompression(DefaultHttpClient client) {
		// Gzip support
        client.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
                if (!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }
			}
		});

        client.addResponseInterceptor(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext context)
					throws HttpException, IOException {
                HttpEntity entity = response.getEntity();
                if(entity != null) {
                    Header ceheader = entity.getContentEncoding();
                    if (ceheader != null) {
                        HeaderElement[] codecs = ceheader.getElements();
                        for (int i = 0; i < codecs.length; i++) {
                            if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                                response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                                return;
                            }
                        }
                    }
                }
			}
		});
	}
	
	/**
	 * GzipDecompressingEntity 
	 */
	private static class GzipDecompressingEntity extends HttpEntityWrapper {

        public GzipDecompressingEntity(final HttpEntity entity) {
            super(entity);
        }

        @Override
        public InputStream getContent()
            throws IOException, IllegalStateException {

            // the wrapped entity's getContent() decides about repeatability
            InputStream wrappedin = wrappedEntity.getContent();
            return new GZIPInputStream(wrappedin);
        }

        @Override
        public long getContentLength() {
            // length of ungzipped content is not known
            return -1;
        }
	}
	
}
