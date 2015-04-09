package com.rolfje.anonimatron.synonyms;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Provides functionality for reading and writing {@link Synonym}s to an XML
 * file for later reference.
 * 
 */
public class SynonymMapper {

	@SuppressWarnings("unchecked")
	public static List<Synonym> readFromFile(String filename) throws Exception {
		Mapping mapping = getMapping();
		Unmarshaller unmarshaller = new Unmarshaller(ArrayList.class);
		unmarshaller.setMapping(mapping);

		File file = new File(filename);
		Reader reader = new FileReader(file);
		return (List<Synonym>) unmarshaller.unmarshal(reader);
	}

	public static void writeToFile(List<Synonym> synonyms, String filename)
			throws Exception {
		Mapping mapping = getMapping();

		Writer writer = new FileWriter(new File(filename));
		Marshaller marshaller = new Marshaller(writer);

		// I have no idea why this does not work, so I added a castor.propeties
		// file in the root as workaround.
		// marshaller.setProperty("org.exolab.castor.indent", "true");

		marshaller.setRootElement("synonyms");
		marshaller.setMapping(mapping);
		marshaller.setSuppressXSIType(true);
		marshaller.marshal(synonyms);
		writer.close();
	}

	private static Mapping getMapping() throws IOException, MappingException {
		URL url = SynonymMapper.class.getResource("castor-synonym-mapping.xml");
		Mapping mapping = new Mapping();
		mapping.loadMapping(url);
		return mapping;
	}
}
