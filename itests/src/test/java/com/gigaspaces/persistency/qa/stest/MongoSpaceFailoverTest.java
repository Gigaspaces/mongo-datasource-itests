package com.gigaspaces.persistency.qa.stest;

import com.gigaspaces.persistency.qa.model.IssueDocument;
import com.gigaspaces.persistency.qa.utils.AssertUtils;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: boris
 * Date: 25/12/13
 */
public class MongoSpaceFailoverTest extends AbstractSystemTestUnit {

    @Override
    @Test
    public void test() {

        gigaSpace.getTypeManager().registerTypeDescriptor(IssueDocument.getTypeDescriptor());

        say("Mongo Space Failover test started ...");
        List<IssueDocument> issueDocuments = new ArrayList<IssueDocument>();

        for (int i = 0; i < 10; i++) {
            IssueDocument doc = new IssueDocument ();
            doc.setKey(i+1);
            doc.setVotes(i+1);
            issueDocuments.add(doc);
        }

        gigaSpace.writeMultiple(issueDocuments.toArray(new IssueDocument[] {}));

        waitForEmptyReplicationBacklog(gigaSpace);

        //failover
        say("Failover space ... restarting");
        restartPuGscs(testPU,true);

        List<IssueDocument> docs = Arrays.asList(gigaSpace.readMultiple(
                new IssueDocument(), 20));

        Collections.sort(docs);

        junit.framework.Assert.assertEquals("size is not equals", issueDocuments.size(),
                docs.size());

        AssertUtils.assertEquivalent("", issueDocuments, docs);

        assertMongoEqualsSpace(issueDocuments);

        say("Mongo Space Failover test passed!");
    }

    private void assertMongoEqualsSpace(List<IssueDocument> beforeClear) {
        clearMemory(gigaSpace);
        Assert.assertEquals(0,gigaSpace.count(null));
        List<IssueDocument> pojos = Arrays.asList(gigaSpace.readMultiple(
                new IssueDocument(), 20));
        AssertUtils.assertEquivalent("space is not equivalent to mongo", beforeClear, pojos);

    }

    @Override
    protected String getPUJar() {
        return "/lru.jar";
    }
}
