package com.gigaspaces.persistency.qa.stest;

import com.gigaspaces.persistency.qa.model.IssuePojo;
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

    List<IssuePojo> issuePojos = new ArrayList<IssuePojo>();

    @Override
    public void test() {
        say("Mongo Mirror Failover test started ...");
        Thread t = new Thread() {
            public void run() {
                try {
                    //sleep in order to let mirror get restarted first
                    Thread.sleep(1000);
                    for (int i = 0; i < 15; i++) {
                        issuePojos.add(new IssuePojo(i + 1, "dank" + (i + 1)));
                    }
                    gigaSpace.writeMultiple(issuePojos.toArray(new IssuePojo[] {}));
                } catch (InterruptedException e) {
                    Assert.fail("fillSpace wait interrupted!");
                }
            }
        };
        t.start();
        // shut down mirror when filling space
        say("Failover mirror ... restarting");
        restartPuGscs(mirrorServicePU);
        // wait till thread finished writing to space
        try {
            t.join();
        } catch (InterruptedException e) {}

        waitForEmptyReplicationBacklog(gigaSpace);

        List<IssuePojo> pojos = Arrays.asList(gigaSpace.readMultiple(
                new IssuePojo(), 20));

        Collections.sort(pojos);

        junit.framework.Assert.assertEquals("size is not equals", issuePojos.size(),
                pojos.size());

        AssertUtils.assertEquivalent("", issuePojos, pojos);
        say("Mongo Mirror Failover test passed!");
    }

    @Override
    protected String getPUJar() {
        return "/all-in-cache-0.0.1-SNAPSHOT.jar";
    }
}
