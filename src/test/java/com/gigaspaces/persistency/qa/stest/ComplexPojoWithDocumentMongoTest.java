package com.gigaspaces.persistency.qa.stest;

import com.gigaspaces.persistency.qa.model.ComplexPojo;
import com.gigaspaces.persistency.qa.model.IssueDocument;
import com.gigaspaces.persistency.qa.utils.AssertUtils;
import com.j_spaces.core.client.SQLQuery;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: boris
 * Date: 05/01/14
 */
public class ComplexPojoWithDocumentMongoTest extends AbstractSystemTestUnit  {
    @Override
    public void test() {

        say("Complex Pojo With Document Mongo com.Test ...");
        List<ComplexPojo> complexPojos = new ArrayList<ComplexPojo>();

        for (int i = 0; i < 10; i++) {
            ComplexPojo complex = new ComplexPojo ();
            complex.setId(i+1);
            complex.setName(""+i);
            IssueDocument doc = new IssueDocument();
            doc.setKey(i+1);
            doc.setVotes(i + 1);
            complex.setDocument(doc);
            complexPojos.add(complex);
        }

        gigaSpace.writeMultiple(complexPojos.toArray(new ComplexPojo[] {}));

        waitForEmptyReplicationBacklog(gigaSpace);

        List<ComplexPojo> read = Arrays.asList(gigaSpace.readMultiple(
                new ComplexPojo(), 20));

        Collections.sort(read);

        junit.framework.Assert.assertEquals("size is not equals", complexPojos.size(),
                read.size());

        AssertUtils.assertEquivalent("", complexPojos, read);

        ComplexPojo[] queriedPersons = gigaSpace.readMultiple(new SQLQuery<ComplexPojo>(ComplexPojo.class.getName(), "document.votes > 5"));

        Assert.assertEquals("Should be 5 objects with documents with votes greater than 5",5,queriedPersons.length);

        assertMongoEqualsSpace(complexPojos);

        say("Complex Pojo With Document Mongo com.Test Passed!");
    }

    @Override
    protected String getPUJar() {
        return "/lru-0.0.1-SNAPSHOT.jar";
    }

    private void assertMongoEqualsSpace(List<ComplexPojo> beforeClear) {
        clearMemory(gigaSpace);
        Assert.assertEquals(0, gigaSpace.count(null));
        List<ComplexPojo> read = Arrays.asList(gigaSpace.readMultiple(
                new ComplexPojo(), 20));
        AssertUtils.assertEquivalent("space is not equivalent to mongo", beforeClear, read);

    }
}
