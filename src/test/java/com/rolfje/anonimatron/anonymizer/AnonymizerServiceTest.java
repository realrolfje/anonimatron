package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.Synonym;
import junit.framework.TestCase;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AnonymizerServiceTest extends TestCase {
	AnonymizerService anonService;

	protected void setUp() throws Exception {
		anonService = new AnonymizerService();
	}

	public void testStringAnonymizer() throws Exception {
		List<Object> fromList = new ArrayList<Object>();
		fromList.add("String 1");
		fromList.add("String 2");
		fromList.add("String 3");

		String type = new StringAnonymizer().getType();

		testAnonymizer(fromList, type, type);
	}

	public void testUUIDAnonymizer() throws Exception {
		List<Object> fromList = new ArrayList<Object>();
		fromList.add("String 1");
		fromList.add("String 2");
		fromList.add("String 3");

		String type = new UUIDAnonymizer().getType();

		testAnonymizer(fromList, type, type);
	}

	public void testDateAnonymizer() {

		assertEquals(new Date(0), new Date(0));

		List<Object> fromList = new ArrayList<Object>();
		fromList.add(new Date(0));
		fromList.add(new Date(86400000L));
		fromList.add(new Date(172800000L));

		String type = Date.class.getName();

		testAnonymizer(fromList, type, "DATE");
	}

	private void testAnonymizer(List<Object> fromList, String lookupType, String synonymType) {
		List<Object> toList = new ArrayList<Object>();

		// First pass
		for (Object from : fromList) {
			Synonym s = anonService.anonymize(lookupType, from, 100);

			assertEquals(from, s.getFrom());
			assertEquals(synonymType, s.getType());
			assertNotNull(s.getTo());

			toList.add(s.getTo());
		}

		// Second pass (consistency check)
		for (int i = 0; i < fromList.size(); i++) {
			Synonym s = anonService.anonymize(lookupType, fromList.get(i), 100);
			assertEquals(toList.get(i), s.getTo());
		}

		// Test passing in null
		assertNull(anonService.anonymize(lookupType, null, 100).getTo());
	}
}
