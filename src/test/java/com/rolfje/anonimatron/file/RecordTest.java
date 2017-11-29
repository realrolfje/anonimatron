package com.rolfje.anonimatron.file;

import junit.framework.TestCase;

public class RecordTest extends TestCase {

	public void testToString() throws Exception {
		String[] names = {"name1", "name2", "name3"};
		Object[] values = {"value1", null, new Object()};

		Record record = new Record(names, values);

		String toString = record.toString();
		System.out.println(toString);

		assertTrue(toString.startsWith("[Record: name1:'value1', name2:null, name3:'java.lang.Object"));
		assertTrue(toString.endsWith("']"));
	}
}