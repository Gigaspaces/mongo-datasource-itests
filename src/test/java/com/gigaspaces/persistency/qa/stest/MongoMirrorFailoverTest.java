package com.gigaspaces.persistency.qa.stest;

import com.gigaspaces.persistency.qa.model.IssueDocument;
import com.gigaspaces.persistency.qa.utils.AssertUtils;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: boris
 * Date: 25/12/13
 */
public class MongoMirrorFailoverTest extends AbstractSystemTestUnit {

    List<IssueDocument> issueDocs = new ArrayList<IssueDocument>();

    @Override
    public void test() {

        gigaSpace.getTypeManager().registerTypeDescriptor(IssueDocument.getTypeDescriptor());
        say("Mongo Mirror Failover test started ...");
        Thread t = new Thread() {
            public void run() {
                try {
                    //sleep in order to let mirror get restarted first
                    Thread.sleep(1000);
                    for (int i = 0; i < 10; i++) {
                        IssueDocument doc = new IssueDocument ();
                        doc.setKey(i+1);
                        doc.setVotes(i+1);
                        issueDocs.add(doc);
                    }
                    gigaSpace.writeMultiple(issueDocs.toArray(new IssueDocument[] {}));
                } catch (InterruptedException e) {
                    Assert.fail("fillSpace wait interrupted!");
                }
            }
        };
        t.start();
        // shut down mirror when filling space
        say("Failover mirror ... restarting");
        restartPuGscs(mirrorServicePU,false);
        //waiting in case the mirror and the test pu were in the same container
        testPU.waitFor(4);
        // wait till thread finished writing to space
        try {
            t.join();
        } catch (InterruptedException e) {}

        waitForEmptyReplicationBacklog(gigaSpace);

        List<IssueDocument> docs = Arrays.asList(gigaSpace.readMultiple(
                new IssueDocument(), 20));

        Collections.sort(docs);

        junit.framework.Assert.assertEquals("size is not equals", issueDocs.size(),
                docs.size());

        AssertUtils.assertEquivalent("", issueDocs, docs);

        assertMongoEqualsSpace(issueDocs);

        say("Mongo Mirror Failover test passed!");
    }

    @Override
    protected String getPUJar() {
        return "/lru-0.0.1-SNAPSHOT.jar";
    }

    private void assertMongoEqualsSpace(List<IssueDocument> beforeClear) {
        clearMemory(gigaSpace);
        junit.framework.Assert.assertEquals(0, gigaSpace.count(null));
        List<IssueDocument> docs = Arrays.asList(gigaSpace.readMultiple(
                new IssueDocument(), 20));
        AssertUtils.assertEquivalent("space is not equivalent to mongo", beforeClear, docs);

    }
}
