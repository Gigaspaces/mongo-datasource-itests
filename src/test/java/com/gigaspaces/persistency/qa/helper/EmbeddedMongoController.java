package com.gigaspaces.persistency.qa.helper;

import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.distribution.Version;

public class EmbeddedMongoController {

	private static final String LOCALHOST = "localhost";
	private int _port = 12345;
	private MongodProcess mongodProcess;
	private MongoClient client;

	public void initMongo() {

		try {

			MongodStarter starter = MongodStarter.getDefaultInstance();

			MongodExecutable mognoExecutable = starter
					.prepare(new MongodConfig(Version.Main.PRODUCTION, _port, false));

			mongodProcess = mognoExecutable.start();

			client = new MongoClient(LOCALHOST, _port);

		} catch (Throwable e) {
			throw new AssertionError(e);
		}
	}

	public void stopMongo() {

		client.close();

		mongodProcess.stop();
	}

	public int getPort() {

		return _port;
	}

	public void createDb(String dbName) {

		client.getDB(dbName);
	}

	public void dropDb(String dbName) {

		client.dropDatabase(dbName);
	}
}
