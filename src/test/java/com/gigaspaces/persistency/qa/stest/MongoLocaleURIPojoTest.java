package com.gigaspaces.persistency.qa.stest;

import com.gigaspaces.persistency.qa.model.LocaleURIPojo;
import com.gigaspaces.persistency.qa.utils.AssertUtils;
import org.junit.Assert;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


public class MongoLocaleURIPojoTest extends AbstractSystemTestUnit {

    public static final int NUM_OF_OBJECTS = 10;

    @Override
    public void test() {
        say("Mongo Locale URI Pojo Test ...");
        List<LocaleURIPojo> pojos = new LinkedList<LocaleURIPojo>();
        for (int i = 0; i < NUM_OF_OBJECTS; i++) {
            LocaleURIPojo pojo = new LocaleURIPojo();
            pojo.setHandle(""+i);
            pojo.setLocale(new Locale("english"));
            URI uri = URI.create ("http://www.cnn.com");
            pojo.setUri(uri);
            pojos.add(pojo);
        }
        try {
            gigaSpace.writeMultiple(pojos.toArray(new LocaleURIPojo[] {}));
        }
        catch (Exception e){
            Assert.fail("Failed to write object which contains Locale and URI\n"+e);
        }
        waitForEmptyReplicationBacklogAndClearMemory(gigaSpace);
        LocaleURIPojo[] readPojos = gigaSpace.readMultiple(new LocaleURIPojo(), 20);
        Assert.assertEquals("Size of read pojos should be "+NUM_OF_OBJECTS,NUM_OF_OBJECTS,readPojos.length);
        assertMongoEqualsSpace(pojos);
        say("Mongo Locale URI Pojo Test Passed!");
    }

    private void assertMongoEqualsSpace(List<LocaleURIPojo> beforeClear) {
        clearMemory(gigaSpace);
        junit.framework.Assert.assertEquals(0, gigaSpace.count(null));
        List<LocaleURIPojo> docs = Arrays.asList(gigaSpace.readMultiple(
                new LocaleURIPojo(), 20));
        AssertUtils.assertEquivalent("space is not equivalent to mongo", beforeClear, docs);
    }

    @Override
    protected String getPUJar() {
        return "/lru-0.0.1-SNAPSHOT.jar";
    }
}
