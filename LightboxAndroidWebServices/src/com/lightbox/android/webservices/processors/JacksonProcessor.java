/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.webservices.processors;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;

import android.util.Log;

/** 
 * JacksonProcessor 
 * @author Fabien Devos
 */
public class JacksonProcessor<T> implements Processor<T> {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "JacksonProcessor";
	
	public static final String PROPERTIES_FILTER = "propertiesFilter";
	
	// Can be reused as long as it is configured first 
	private static ObjectMapper sObjectMapper = new ObjectMapper(); 
	private static ObjectMapper sObjectMapperWithoutNamingStrategy = new ObjectMapper(); 
	static {
		sObjectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		sObjectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		sObjectMapperWithoutNamingStrategy.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public T parse(Class<T> parsedClass, InputStream source) throws ParsingException {
		return parse(parsedClass, source, true);
	}
	
	public T parse(Class<T> parsedClass, InputStream source, boolean useNamingStrategy) throws ParsingException {		
		T parsedObject = null;
		try {
			ObjectMapper objectMapper = useNamingStrategy ? sObjectMapper : sObjectMapperWithoutNamingStrategy;
			parsedObject = objectMapper.readValue(source, parsedClass);
		} catch (JsonParseException e) {
			Log.w(TAG, e);
			throw new ParsingException(e);
		} catch (JsonMappingException e) {
			Log.w(TAG, e);
			throw new ParsingException(e);
		} catch (IOException e) {
			Log.w(TAG, e);
			throw new ParsingException(e);
		}
		return parsedObject;
	}

	@Override
	public String generate(T sourceObject, Set<String> propertiesToInclude) throws GenerationException {
		return generate(sourceObject, propertiesToInclude, true);
	}
	
	public String generate(T sourceObject, Set<String> propertiesToInclude, boolean useNamingStrategy) throws GenerationException {
		String generatedString = null;
		try {
			ObjectMapper objectMapper = useNamingStrategy ? sObjectMapper : sObjectMapperWithoutNamingStrategy;
			// Filtering properties
			if (propertiesToInclude != null) {
				FilterProvider filterProvider = new SimpleFilterProvider().addFilter(PROPERTIES_FILTER,
						SimpleBeanPropertyFilter.filterOutAllExcept(propertiesToInclude));
				generatedString = objectMapper.writer(filterProvider).writeValueAsString(sourceObject);
			// Do not filter properties
			} else {
				generatedString = objectMapper.writeValueAsString(sourceObject);
			}
		} catch (JsonGenerationException e) {
			Log.w(TAG, e);
			throw new GenerationException(e);
		} catch (JsonMappingException e) {
			Log.w(TAG, e);
			throw new GenerationException(e);
		} catch (IOException e) {
			Log.w(TAG, e);
			throw new GenerationException(e);
		}
		return generatedString;
	}
	
	//----------------------------------------------
	// Utility methods
	
	@SuppressWarnings("unchecked")
	public static HashMap<String, Object> jsonToMap(String json) throws ParsingException {
		HashMap<String, Object> map = null;
		// To Map
		if (json != null) {
			@SuppressWarnings("rawtypes")
			JacksonProcessor<HashMap> processor = new JacksonProcessor<HashMap>();
			map = processor.parse(HashMap.class, IOUtils.toInputStream(json));
		}
		return map;
	}
	
	public static String mapToJson(HashMap<String, Object> map) throws GenerationException {
		String json = null;
		// To JSON
		if (map != null) {
			@SuppressWarnings("rawtypes")
			JacksonProcessor<HashMap> processor = new JacksonProcessor<HashMap>();
			json = processor.generate(map, null);
		}
		return json;
	}
	
}
