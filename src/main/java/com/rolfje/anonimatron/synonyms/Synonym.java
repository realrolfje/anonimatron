package com.rolfje.anonimatron.synonyms;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;

/**
 * Provides a way to connect source and target data, and identify the type of
 * data. Usually the data type is something more semantic, like "NAME" or
 * "STREET". Synonyms are produced by the {@link AnonymizerService}.
 * 
 */
public interface Synonym {

	/**
	 * @return The semantic data type of this Synonym, usually something
	 *         descriptive as "NAME" or "STREET".
	 */
	String getType();

	/**
	 * @return The data which was in the original database for this Synonym
	 */
	Object getFrom();

	/**
	 * 
	 * @return The data with which the original data in the database will be or
	 *         is replaced when the Anonymizer runs.
	 */
	Object getTo();
}