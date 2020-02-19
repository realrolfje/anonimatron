package com.rolfje.anonimatron.anonymizer;

import com.rolfje.anonimatron.synonyms.HashedFromSynonym;
import com.rolfje.anonimatron.synonyms.Synonym;
import com.rolfje.anonimatron.synonyms.SynonymMapper;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.XMLException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SynonymCache {

    private Map<String, Map<Object, Synonym>> synonymCache = new HashMap<>();
    private Hasher hasher;
    private long size = 0;

    public SynonymCache() {
    }

    /**
     * Reads the synonyms from the specified file and (re-)initializes the
     * {@link #synonymCache} with it.
     *
     * @param synonymXMLfile the xml file containing the synonyms, as written by
     *                       {@link #toFile(File)}
     * @return Synonyms as the were stored on last run.
     * @throws MappingException When synonyms can not be read from file.
     * @throws IOException      When synonyms can not be read from file.
     * @throws XMLException     When synonyms can not be read from file.
     */
    public static SynonymCache fromFile(File synonymXMLfile) throws MappingException, IOException, XMLException {

        SynonymCache synonymCache = new SynonymCache();
        List<Synonym> synonymsFromFile = SynonymMapper
                .readFromFile(synonymXMLfile.getAbsolutePath());

        for (Synonym synonym : synonymsFromFile) {
            synonymCache.put(synonym);
        }

        return synonymCache;
    }

    /**
     * Writes all known {@link Synonym}s in the cache out to the specified file
     * in XML format.
     *
     * @param synonymXMLfile an empty writeable xml file to write the synonyms
     *                       to.
     * @throws XMLException     When there is a problem writing the synonyms to file.
     * @throws IOException      When there is a problem writing the synonyms to file.
     * @throws MappingException When there is a problem writing the synonyms to file.
     */
    public void toFile(File synonymXMLfile) throws XMLException, IOException, MappingException {
        List<Synonym> allSynonyms = new ArrayList<Synonym>();

        // Flatten the type -> From -> Synonym map.
        Collection<Map<Object, Synonym>> allObjectMaps = synonymCache.values();
        for (Map<Object, Synonym> typeMap : allObjectMaps) {
            allSynonyms.addAll(typeMap.values());
        }

        SynonymMapper
                .writeToFile(allSynonyms, synonymXMLfile.getAbsolutePath());
    }

    /**
     * Stores the given {@link Synonym} in the synonym cache, except when the
     * given synonym is short-lived. Short-lived synonyms are not stored or
     * re-used.
     *
     * @param synonym to store
     */
    public void put(Synonym synonym) {
        if (synonym.isShortLived()) {
            return;
        }

        Map<Object, Synonym> map = synonymCache.get(synonym.getType());
        if (map == null) {
            map = new HashMap<Object, Synonym>();
            synonymCache.put(synonym.getType(), map);
        }

        if (hasher != null) {
            // Hash sensitive data before storing
            Synonym hashed = new HashedFromSynonym(hasher, synonym);
            if (map.put(hashed.getFrom(), hashed) == null) {
                size++;
            }
        } else {
            // Store as-is
            if (map.put(synonym.getFrom(), synonym) == null) {
                size++;
            }
        }
    }

    public Synonym get(String type, Object from) {
        Map<Object, Synonym> typemap = synonymCache.get(type);
        if (typemap == null) {
            return null;
        }

        if (hasher != null) {
            return typemap.get(hasher.base64Hash(from));
        } else {
            return typemap.get(from);
        }
    }

    public void setHasher(Hasher hasher) {
        this.hasher = hasher;
    }

    public long size() {
        return size;
    }
}
