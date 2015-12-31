package com.gigaspaces.persistency.qa.helper;

import com.gigaspaces.persistency.qa.utils.CommandLineProcess;
import com.mongodb.*;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class MongoDBController
{

    private static final String MONGO_HOME = "MONGO_HOME";
    private static final String mongoPath = System.getenv(MONGO_HOME) + "/bin";
    private static final String LOCALHOST  = "localhost";
    private static final String QA_DB      = "qadb";
    private static final int    PORT       = 27017;
    private MongodProcess       embeddedmongodProcess;

    private CommandLineProcess  configProcess;
    private CommandLineProcess  mongosProcess;
    private CommandLineProcess  mongodProcess1;
    private CommandLineProcess  mongodProcess2;

    private MongoClient         client;
    private boolean             isEmbedded;
    private List<Thread>        threads    = new ArrayList<Thread>();

    private static final String WAIT_MONGO_PROCESS_INDICATOR = "waiting for connections on port";
    protected Semaphore mongoLock = new Semaphore(0);
    public void start(boolean isEmbedded)
    {

        this.isEmbedded = isEmbedded;

        if (this.isEmbedded)
        {

            startEmbedded();
        }
        else
        {
            startCluster();
        }

        try
        {
            client = new MongoClient(LOCALHOST, PORT);

        }
        catch (UnknownHostException e)
        {
            throw new AssertionError(e);
        }

        client.getDB(QA_DB);

    }

    public void startCluster()
    {

        String path = FilenameUtils.normalize(System.getProperty("java.io.tmpdir")
                + "/temp_mongo_data");

        try
        {
            File arg0 = new File(path);
            if (arg0.exists())
                FileUtils.deleteDirectory(arg0);
        }
        catch (IOException e)
        {
            throw new AssertionError(e);
        }
        createDirectory(path);

        String configdb = createDirectory(path + "/configdb");

        startConfigServer(configdb, 27018);

        startMongos(27018);

        mongodProcess1 = startMongod(createDirectory(path + "/s1"), 27019);
        mongodProcess2 = startMongod(createDirectory(path + "/s2"), 27020);

        initialize();
    }

    private void initialize() throws AssertionError
    {
        try
        {
            client = new MongoClient(LOCALHOST, PORT);

            DB adminDB = client.getDB("admin");

            CommandResult result = adminDB.command(new BasicDBObject("listShards",
                    1));
            BasicDBList list = (BasicDBList) result.get("shards");
            if (list.size() == 0)
            {
                result = adminDB.command(new BasicDBObject("addShard",
                        "127.0.0.1:27019"));
                result = adminDB.command(new BasicDBObject("addShard",
                        "127.0.0.1:27020"));
                result = adminDB.command(new BasicDBObject("enableSharding",
                        QA_DB));
            }
        }
        catch (UnknownHostException e)
        {
            throw new AssertionError(e);
        }
    }

    public String createDirectory(String path)
    {
        File dir = new File(FilenameUtils.normalize(path));

        if (!dir.exists())
        {
            dir.mkdirs();
        }

        return dir.getAbsolutePath();
    }

    public void startConfigServer(String dir, int port)
    {
        // mongod --configsvr --dbpath /data/configdb --port 27019
        List<String> args = new ArrayList<String>();
        args.add(mongoPath+"/mongod");
        args.add("--configsvr");
        args.add("--dbpath");
        args.add(quotePathIfNeeded(dir));
        args.add("--port");
        args.add("" + port);

        configProcess = start(args);
        waitForMongoToBeIdle();

    }

    private void waitForMongoToBeIdle() {
        long timeout = 120;
        boolean success = false;
        try {
            success = mongoLock.tryAcquire(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}
        Assert.assertTrue("Mongo process didn't get idle on time, waited for the string:\n"+WAIT_MONGO_PROCESS_INDICATOR,success);
    }

    private CommandLineProcess startMongod(String dir, int port)
    {

        List<String> args = new ArrayList<String>();
        args.add(mongoPath+"/mongod");
        args.add("--dbpath");
        args.add(quotePathIfNeeded(dir));
        args.add("--port");
        args.add("" + port);
        args.add("--smallfiles");

        CommandLineProcess mongod = start(args);
        waitForMongoToBeIdle();
        return mongod;
    }

    private void startMongos(int configPort)
    {
        // mongos --configdb
        // cfg0.example.net:27019,cfg1.example.net:27019,cfg2.example.net:27019

        List<String> args = new ArrayList<String>();
        args.add(mongoPath+"/mongos");
        args.add("--configdb");
        args.add("127.0.0.1:" + configPort);
        mongosProcess = start(args);
        waitForMongoToBeIdle();
    }

    private CommandLineProcess start(List<String> cmd)
    {
        String wd = FilenameUtils.normalize(mongoPath);
        CommandLineProcess process = new CommandLineProcess(cmd, wd, WAIT_MONGO_PROCESS_INDICATOR, mongoLock);

        Thread thread = new Thread(process);

        thread.start();

        try
        {
            Thread.sleep(3000);
        }
        catch (InterruptedException e)
        {
        }

        threads.add(thread);

        return process;
    }

    private String quotePathIfNeeded(String path)
    {
        if ("\\".equals(File.separator))
            return "\"" + path + "\"";

        return path;
    }

    public void startEmbedded() throws AssertionError
    {
        try
        {

            MongodStarter starter = MongodStarter.getDefaultInstance();

            MongodExecutable mognoExecutable = starter.prepare(new MongodConfig(Version.Main.PRODUCTION,
                    PORT,
                    false));

            embeddedmongodProcess = mognoExecutable.start();

        }
        catch (Throwable e)
        {
            throw new AssertionError(e);
        }
    }

    public void drop()
    {
        if (client != null)
            client.dropDatabase(QA_DB);
    }

    public void stop()
    {
        if (isEmbedded && embeddedmongodProcess != null)
            embeddedmongodProcess.stop();
        else
        {
            if (mongodProcess1 != null)
                mongodProcess1.stop();
            if (mongodProcess2 != null)
                mongodProcess2.stop();
            if (configProcess != null)
                configProcess.stop();
            if (mongosProcess!= null)
                mongosProcess.stop();
        }

    }

    public int getPort()
    {
        return PORT;
    }

    public void createDb(String dbName)
    {
        client.getDB("admin").command(new BasicDBObject("enableSharding",
                dbName));
        client.getDB(dbName);

    }

    public void dropDb(String dbName)
    {
        client.getDB("admin").command(new BasicDBObject("disableSharding",
                dbName));

        client.dropDatabase(dbName);

    }
}
