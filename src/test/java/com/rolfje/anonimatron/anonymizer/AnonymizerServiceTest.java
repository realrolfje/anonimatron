package com.rolfje.anonimatron.anonymizer;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.rolfje.anonimatron.synonyms.Synonym;

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

		testAnonymizer(fromList, type);
	}

	public void testUUIDAnonymizer() throws Exception {
		List<Object> fromList = new ArrayList<Object>();
		fromList.add("String 1");
		fromList.add("String 2");
		fromList.add("String 3");

		String type = new UUIDAnonymizer().getType();

		testAnonymizer(fromList, type);
	}

	private void testAnonymizer(List<Object> fromList, String type) {
		List<Object> toList = new ArrayList<Object>();

		// First pass
		for (Object from : fromList) {
			Synonym s = anonService.anonymize(type, from, 100);

			assertEquals(from, s.getFrom());
			assertEquals(type, s.getType());
			assertNotNull(s.getTo());

			toList.add(s.getTo());
		}

		// Second pass (consistency check)
		for (int i = 0; i < fromList.size(); i++) {
			Synonym s = anonService.anonymize(type, fromList.get(i), 100);
			assertEquals(toList.get(i), s.getTo());
		}

		// Test passing in null
		assertNull(anonService.anonymize(type, null, 100).getTo());
	}
}
