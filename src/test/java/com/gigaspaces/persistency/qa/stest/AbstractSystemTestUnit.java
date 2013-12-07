package com.gigaspaces.persistency.qa.stest;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitDeployment;
import org.openspaces.core.GigaSpace;

import com.gigaspaces.client.ClearModifiers;
import com.gigaspaces.client.CountModifiers;
import com.gigaspaces.persistency.qa.utils.IRepetitiveRunnable;
import com.j_spaces.core.IJSpace;
import com.j_spaces.core.admin.StatisticsAdmin;
import com.j_spaces.core.filters.ReplicationStatistics.ChannelState;
import com.j_spaces.core.filters.ReplicationStatistics.OutgoingChannel;
import com.j_spaces.core.filters.ReplicationStatistics.OutgoingReplication;


public abstract class AbstractSystemTestUnit
{

    private static final String QA_GROUP   = "qa_group";
    private final static String DEPLOY_DIR = "/mongodb-system-test-deploy";

    private  Admin         admin     ;

    protected GigaSpace         gigaSpace;
    protected ProcessingUnit    testPU;
    protected ProcessingUnit    mirrorServicePU;

    @Before
    public void start()
    {
        MongoSystemTestSuite.drop();

        startWithoutDropDatabase();
    }

    protected void startWithoutDropDatabase() throws AssertionError
    {
        admin = new AdminFactory().addGroup(QA_GROUP)
                .createAdmin();
        admin.getGridServiceManagers().waitForAtLeastOne(60,TimeUnit.SECONDS);
        
        if (hasMirrorService())
            deployMirrorService();

        File puArchive = new File(getDeploymentJarPath(DEPLOY_DIR, getPUJar()));

        ProcessingUnitDeployment deployment = new ProcessingUnitDeployment(puArchive);

        try
        {

            testPU = admin.getGridServiceManagers().deploy(deployment);

            testPU.waitFor(4);

            gigaSpace = testPU.getSpace().getGigaSpace();
        }
        catch (Exception ex)
        {
            throw new AssertionError(ex);
        }
    }

    @After
    public void stop()
    {

        waitForEmptyReplicationBacklog(gigaSpace);

        if (testPU != null)
            testPU.undeployAndWait();

        if (mirrorServicePU != null)
            mirrorServicePU.undeployAndWait();
        
        if(admin != null)
            admin.close();
    }

    @Test
    public abstract void test();

    protected String getDeploymentJarPath(String dirName, String jarName)
    {
        String tmp = System.getProperty("java.io.tmpdir");

        return FilenameUtils.normalize(tmp + dirName + jarName);
    }

    protected boolean hasMirrorService()
    {
        return getMirrorService() != null;
    }

    protected String getMirrorService()
    {
        return "/mongodb-qa-mirror-0.0.1-SNAPSHOT.jar";
    }

    protected abstract String getPUJar();

    private void deployMirrorService()
    {

        File mirrorPuArchive = new File(getDeploymentJarPath(DEPLOY_DIR,
                                                             getMirrorService()));

        ProcessingUnitDeployment deployment = new ProcessingUnitDeployment(mirrorPuArchive);

        try
        {

            mirrorServicePU = admin.getGridServiceManagers().deploy(deployment);

            mirrorServicePU.waitFor(1);

        }
        catch (Exception ex)
        {
            throw new AssertionError(ex);
        }

    }

    protected void say(String message)
    {
        System.err.println(message);
    }

    protected void waitForActiveReplicationChannelWithMirror(final IJSpace space)
            throws Exception
    {
        repeat(new IRepetitiveRunnable()
        {
            public void run() throws Exception
            {
                boolean channelFound = false;

                for (OutgoingChannel channel : getOutgoingReplication(space).getChannels())
                {
                    if (!channel.getTargetMemberName()
                            .contains("mirror-service"))
                    {
                        continue;
                    }

                    Assert.assertEquals("No replication with mirror",
                                        ChannelState.ACTIVE,
                                        channel.getChannelState());
                    channelFound = true;
                }

                if (!channelFound)
                {
                    Assert.fail("no replication channel with mirror");
                }
            }
        },
               60 * 1000);

    }

    protected OutgoingReplication getOutgoingReplication(IJSpace space)
            throws Exception
    {
        return ((StatisticsAdmin) space.getAdmin()).getHolder()
                .getReplicationStatistics()
                .getOutgoingReplication();
    }

    protected void waitForEmptyReplicationBacklogAndClearMemory(
            GigaSpace gigaSpace)
    {
        waitForEmptyReplicationBacklog(gigaSpace);
        clearMemory(gigaSpace);
    }

    protected void clearMemory(final GigaSpace gigaSpace)
    {

        repeat(new IRepetitiveRunnable()
        {

            public void run() throws Exception
            {
                gigaSpace.clear(null, ClearModifiers.EVICT_ONLY);

                Assert.assertEquals("gigaSpace memory did not clear",
                                    0,
                                    gigaSpace.count(null,
                                                    CountModifiers.MEMORY_ONLY_SEARCH));
            }
        },
               10 * 1000);

    }

    protected void waitForEmptyReplicationBacklog(final GigaSpace gigaSpace)
    {
        if (gigaSpace != null)
            waitForEmptyReplicationBacklog(gigaSpace.getSpace());
    }

    protected void waitForEmptyReplicationBacklog(IJSpace space)
    {
        repeat(new IRepetitiveRunnable()
        {

            public void run() throws Exception
            {
                long l = -1;

                l = ((StatisticsAdmin) gigaSpace.getSpace().getAdmin()).getHolder()
                        .getReplicationStatistics()
                        .getOutgoingReplication()
                        .getRedoLogSize();

                Assert.assertEquals("backlog not empty", 0, l);

            }
        },
               10 * 1000);
    }

    protected void repeat(IRepetitiveRunnable iRepetitiveRunnable,
            long repeateInterval)
    {
        while (true)
        {
            try
            {

                iRepetitiveRunnable.run();

                break;
            }
            catch (Throwable e)
            {
                try
                {
                    Thread.sleep(repeateInterval);
                }
                catch (InterruptedException e1)
                {
                }
            }
        }
    }
}
