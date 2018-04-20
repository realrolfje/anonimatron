package com.rolfje.anonimatron.configuration;

import java.util.Set;

import junit.framework.TestCase;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;

/**
 * Prints out an XML to see what the Castor mapping makes and expects.
 * 
 */
public class ConfigurationTest extends TestCase {

	/**
	 * Prints an example configuration
	 * 
	 * @throws Exception
	 */
	public void testPrintMappedConfig() throws Exception {
		String demoxml = Configuration.getDemoConfiguration();
		assertNotNull(demoxml);
		assertTrue(demoxml.length()>10);
		
		// See if all anonymizer types are represented in the demo xml
		AnonymizerService as = new AnonymizerService();
		Set<String> customtypes = as.getCustomAnonymizerTypes();
		for (String type : customtypes) {
			assertTrue("Demo xml does not contain "+type,demoxml.indexOf("type=\""+type+"\"")>0);
		}
		
		Set<String> defaulttypes = as.getDefaultAnonymizerTypes();
		for (String type : defaulttypes) {
			assertTrue("Demo xml does not contain "+type,demoxml.indexOf(type.toUpperCase().replace('.','_'))>0);
		}

		// See if we have File records in the configuration.
		assertTrue(demoxml.contains("<file inFile=\"mydatafile.in.csv\""));

		// For convenience and visual checking.
		System.out.println(demoxml);
	}

}
