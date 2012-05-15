/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.webservices.processors;

import java.io.InputStream;
import java.util.Set;

/** 
 * A Processor can parse (deserialize) or generate (serialize).
 * @author Fabien Devos
 */
public interface Processor<T> {
	
	T parse(Class<T> parsedClass, InputStream source) throws ParsingException;
		
	/**
	 * Generate a JSON string with only the specified properties, or all of them if the properties set is null.
	 * @param sourceObject the object to serialize
	 * @param propertiesToInclude a Set of properties to include in the serialization. If it's null, all of them will
	 * be included.
	 * @return the generated JSON string
	 * @throws GenerationException
	 */
	String generate(T sourceObject, Set<String> propertiesToInclude) throws GenerationException;

}
