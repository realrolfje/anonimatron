package com.rolfje.anonimatron.anonymizer;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.rolfje.anonimatron.synonyms.StringSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;

public class DateAnonymizer implements Anonymizer {
	private static final String TYPE = "DATE";
	private static final long RANDOMIZATION_MILLIS = 1000 * 60 * 60 * 24 * 31;

	private Set<Date> generatedDates = new HashSet<Date>();

	@Override
	public Synonym anonymize(Object from, int size) {
		StringSynonym s = new StringSynonym();
		s.setType(TYPE);
		s.setFrom(from);

		if (from == null) {
			s.setTo(null);
		} else if (from instanceof Date) {
			long originalDate = ((Date) from).getTime();

			Date newDate;
			do {
				long deviation = Math.round(2 * RANDOMIZATION_MILLIS
						* Math.random())
						- RANDOMIZATION_MILLIS;
				newDate = new Date(originalDate + deviation);
			} while (!generatedDates.contains(newDate));

			generatedDates.add(newDate);
			s.setTo(newDate);
		} else {
			throw new UnsupportedOperationException(
					"Can not anonymize objects of type " + from.getClass());
		}

		return s;
	}

	@Override
	public String getType() {
		return TYPE;
	}
}
