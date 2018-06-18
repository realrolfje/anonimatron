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
	 * Indicates if this Synonym is short-lived or transient, meaning that
     * it should not be stored in the synonyms file. Transient Synonyms
     * are not stored, need to be calculated each run time.
	 *
	 * Transient synonyms are:
	 *
	 * <ol>
	 *     <li>Not stored in memory.</li>
	 *     <li>Not stored in the synonym file.</li>
	 * </ol>
	 *
	 * @return <code>true</code> if the synonym should be thrown away
	 * after use (not stored in the synonym file).
	 */
	boolean isShortLived();

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