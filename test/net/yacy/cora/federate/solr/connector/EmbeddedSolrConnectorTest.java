package net.yacy.cora.federate.solr.connector;

import java.io.File;
import java.io.IOException;
import net.yacy.cora.federate.solr.instance.EmbeddedInstance;
import net.yacy.search.schema.CollectionSchema;
import net.yacy.search.schema.WebgraphSchema;
import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class EmbeddedSolrConnectorTest {

    EmbeddedSolrConnector solr;

    public EmbeddedSolrConnectorTest() {
    }

    @Before
    public void setUp() {
        File solr_config = new File("defaults/solr");
        File storage = new File("test/DATA/INDEX/webportal/SEGMENTS/text/solr/");
        storage.mkdirs();
        System.out.println("setup EmeddedSolrConnector using config dir: " + solr_config.getAbsolutePath());
        try {
            EmbeddedInstance localCollectionInstance = new EmbeddedInstance(solr_config, storage, CollectionSchema.CORE_NAME, new String[]{CollectionSchema.CORE_NAME, WebgraphSchema.CORE_NAME});
            solr = new EmbeddedSolrConnector(localCollectionInstance);
           
        } catch (final IOException ex) {
            fail("IOException starting Jetty");
        }
    }

    @After
    public void tearDown() {
        solr.close();
    }

    /**
     * Test of query solr via jetty
     */
    @Test
    public void testQuery() throws IOException {
        System.out.println("adding test document to solr");
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(CollectionSchema.id.name(), "ABCD0000abcd");
        doc.addField(CollectionSchema.title.name(), "Lorem ipsum");
        doc.addField(CollectionSchema.host_s.name(), "yacy.net");
        doc.addField(CollectionSchema.text_t.name(), "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");

        solr.add(doc);
        solr.commit(true);

        System.out.println("query solr");
        long expResult = 1;
        long result = solr.getCountByQuery(CollectionSchema.text_t.name() + ":tempor");
        System.out.println("found = " + result + " (expected = 1 )");
        assertEquals(expResult, result);
    }
}
