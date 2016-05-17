package com.rolfje.anonimatron.synonyms;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class SynonymMapperTest extends TestCase {

	public static final char[] ILLEGALSTRINGCHARACTERS = new char[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
			0x07, 0x08, 0x0B, 0x0C, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14,
			0x15, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F, 0x16, 0x17, 0x18, 0x19, 0x7F };

	public void testSynonymMapper() throws Exception {
		List<Synonym> synonyms = new ArrayList<Synonym>();

		StringSynonym s = new StringSynonym();
		s.setFrom("Foo");
		s.setTo("Bar");
		s.setType("Bunk");
		synonyms.add(s);

		s = new StringSynonym();
		s.setFrom("<>!@#$%^&*()");
		s.setTo("<>!@#$%^&*()");
		s.setType("XMLCHARS");
		synonyms.add(s);

		s = new StringSynonym();
		s.setFrom("");
		s.setTo("");
		s.setType("");
		synonyms.add(s);

		String illegalcharString = String.copyValueOf(ILLEGALSTRINGCHARACTERS);
		s = new StringSynonym();
		s.setFrom(illegalcharString);
		s.setTo("");
		s.setType("");
		synonyms.add(s);

		DateSynonym d = new DateSynonym();
		d.setFrom(new Date(System.currentTimeMillis()));
		d.setTo(new Date(System.currentTimeMillis()-1000));
		d.setType("");
		synonyms.add(d);

		File tempFile = File.createTempFile("Anonimatron-SynonymMapperTest-", ".xml");
		tempFile.deleteOnExit();

		SynonymMapper.writeToFile(synonyms, tempFile.getAbsolutePath());
		List<Synonym> synonymsFromFile = SynonymMapper.readFromFile(tempFile.getAbsolutePath());

		assertEquals("Not all synonyms were serialized.", synonyms.size(), synonymsFromFile.size());

		synonyms.removeAll(synonymsFromFile);
		assertEquals("Some synonyms were serialized incorrectly.", 0,
			synonyms.size());
	}
}
