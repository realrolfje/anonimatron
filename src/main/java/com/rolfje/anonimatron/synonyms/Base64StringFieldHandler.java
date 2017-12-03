package com.rolfje.anonimatron.synonyms;

import org.castor.core.util.Base64Decoder;
import org.castor.core.util.Base64Encoder;
import org.exolab.castor.mapping.GeneralizedFieldHandler;

/**
 * Converts object values to a Base64 String and back, see
 * "Writing a GeneralizedFieldHandler" at
 * http://castor.org/xml-fieldhandlers.html
 */
public class Base64StringFieldHandler extends GeneralizedFieldHandler {

	/**
	 * @param objectValue the value fetched from the Java object
	 * @return the converted XML value.
	 */
	@Override
	public Object convertUponGet(Object objectValue) {
		if (objectValue == null || objectValue.equals("")) {
			return objectValue;
		}

		// TODO enforce encoding here.
		byte[] stringBytes = ((String)objectValue).getBytes();
		char[] base64EncodedChars = Base64Encoder.encode(stringBytes);
		return String.copyValueOf(base64EncodedChars);
	}

	/**
	 * @param xmlValue the value fetched from XML
	 * @return the value to set in the Java object.
	 */
	@Override
	public Object convertUponSet(Object xmlValue) {
		if (xmlValue == null || xmlValue.equals("")) {
			return xmlValue;
		}
		
		byte[] decodedBytes = Base64Decoder.decode((String)xmlValue);
		String decodedString = new String(decodedBytes);
		return decodedString;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getFieldType() {
		return String.class;
	}
}
