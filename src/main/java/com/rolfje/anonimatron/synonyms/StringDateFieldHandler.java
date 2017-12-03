package com.rolfje.anonimatron.synonyms;

import org.exolab.castor.mapping.GeneralizedFieldHandler;

import java.sql.Date;

/**
 * Converts date to epoch String representation.
 */
public class StringDateFieldHandler extends GeneralizedFieldHandler {

	/**
	 * @param objectValue the value fetched from the Java object
	 * @return the converted XML value.
	 */
	@Override
	public Object convertUponGet(Object objectValue) {
		if (objectValue == null || objectValue.equals("")) {
			return objectValue;
		}

		long epoch = ((Date) objectValue).getTime();
		return String.valueOf(epoch);
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

		long epoch = Long.valueOf((String) xmlValue).longValue();
		return new Date(epoch);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getFieldType() {
		return String.class;
	}
}
