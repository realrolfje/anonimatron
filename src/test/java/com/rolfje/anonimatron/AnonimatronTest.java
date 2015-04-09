package com.rolfje.anonimatron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class AnonimatronTest extends TestCase {

	public void testVersion() throws Exception {
		File pom = new File("pom.xml");
		assertTrue("Could not find the project pom file.",pom.exists());

		String versionString = "<version>"+Anonimatron.VERSION+"</version>";
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(pom));
			while(reader.ready()){
				String line = reader.readLine();
				if (line.contains(versionString)){
					// Version is ok, return
					return;
				}
				
				if (line.contains("<dependencies>")){
					fail("Incorrect version, pom.xml does not match version info in Anonimatron.VERSION.");
				}
			}
		} finally  {
			reader.close();
		}
		fail("Incorrect version, pom.xml does not match version info in Anonimatron.VERSION.");
	}
}
