package com.rolfje.anonimatron.anonymizer;

import org.apache.log4j.Logger;

import com.rolfje.anonimatron.synonyms.Synonym;

/**
 * Provides the same functionality as {@link CharacterStringAnonymizer}, but
 * uses the prefetch cycle to collect its output character set.
 * 
 * @author rolf
 */
public class CharacterStringPrefetchAnonymizer extends CharacterStringAnonymizer implements Prefetcher {
	private static Logger LOG = Logger.getLogger(CharacterStringPrefetchAnonymizer.class);

	public CharacterStringPrefetchAnonymizer() {
		CHARS = "";
	}

	@Override
	public void prefetch(Object sourceData) {
		if (sourceData == null) {
			return;
		}

		char[] sourcechars = sourceData.toString().toCharArray();
		for (char c : sourcechars) {
			if (CHARS.indexOf(c) == -1) {
				CHARS += c;
			}
		}
	}

	@Override
	public Synonym anonymize(Object from, int size) {
		if (CHARS.length() < 1) {
			LOG.warn("No characters were collected during prefetch. Using the default set '" + getDefaultCharacterString() + "'.");
			CHARS = getDefaultCharacterString();
		}
		return super.anonymize(from, size);
	}

	@Override
	public String getType() {
		return "PREFETCHCHARACTERS";
	}
}
