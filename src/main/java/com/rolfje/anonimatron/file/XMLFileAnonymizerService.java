package com.rolfje.anonimatron.file;

import com.rolfje.anonimatron.anonymizer.AnonymizerService;
import com.rolfje.anonimatron.configuration.Column;
import com.rolfje.anonimatron.configuration.Configuration;
import com.rolfje.anonimatron.configuration.DataFile;
import com.rolfje.anonimatron.synonyms.Synonym;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.jdom2.filter.Filters;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.util.*;

public class XMLFileAnonymizerService extends FileAnonymizerService {
    private final Logger LOG = Logger.getLogger(FileAnonymizerService.class);
    private XPathFactory xFactory;

    public XMLFileAnonymizerService(Configuration config, AnonymizerService anonymizerService) {
        super(config, anonymizerService);
        xFactory = XPathFactory.instance();
    }

    public void printConfigurationInfo() {
        System.out.println("\nAnonymization process started\n");
        System.out.println("To do         : " + config.getFiles().size() + " files.\n");
    }

    public void anonymize() throws Exception {
        List<DataFile> files = expandDirectories(config.getFiles());
        System.out.println("Files to process: " + files.size());

        SAXBuilder jdomBuilder = new SAXBuilder();
        List<FileFilter> fileFilters = getFileFilters();

        long totalBytes = 0;
        for (DataFile file : files) {
            totalBytes += new File(file.getInFile()).length();
        }

// 		Enable this when printing is better tested.
//		printer.start();

        for (DataFile file : files) {
            File infile = new File(file.getInFile());

            boolean process = true;
            for (FileFilter fileFilter : fileFilters) {
                if (!fileFilter.accept(infile)) {
                    // Skip file
                    process = false;
                    // TODO possible bug: Which loop do we want to break out of?
                    continue;
                }
            }

            if (!process || new File(file.getOutFile()).exists()) {
                System.out.println("Skipping " + file.getInFile());
                continue;
            }

            System.out.println("Anonymizing from " + file.getInFile());
            System.out.println("              to " + file.getOutFile());

            Document inputDocument = jdomBuilder.build(infile);

            Document outputDocument = new Document();
            Element root = inputDocument.getRootElement();

            Map<String, Column> columns = toMap(file.getColumns());
            Element outputRoot = anonymize(root, columns);

            outputDocument.addContent(outputRoot);

            // serialize output document
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            xmlOutputter.output(outputDocument, new FileOutputStream(file.getOutFile()));

        }


        System.out.println("\nAnonymization process completed.\n");
    }

    public Element anonymize(Element element, Map<String, Column> pathMap) {

        Iterator<String> keys = pathMap.keySet().iterator();

        Element anonymous = element.clone();

        while (keys.hasNext()) {
            String key = keys.next();
            XPathExpression<Element> expr = xFactory.compile(key, Filters.element());
            List<Element> hits = expr.evaluate(anonymous);
            if (hits.size() > 0) {
                Column column = pathMap.get(key);

                for (Element hit : hits) {
                    String value = hit.getValue();
                    Synonym synonym = anonymizerService.anonymize(column, value);
                    hit.setText((String) synonym.getTo());
                }
            }

        }
        // clone? ...   => houd het voorlopig eens op copy
        if (LOG.isTraceEnabled()) {
            LOG.trace(anonymous);
        }

        return anonymous;
    }

}
