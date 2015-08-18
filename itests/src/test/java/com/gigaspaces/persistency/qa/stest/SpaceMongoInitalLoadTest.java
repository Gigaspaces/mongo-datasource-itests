package com.gigaspaces.persistency.qa.stest;

import com.gigaspaces.client.CountModifiers;
import com.gigaspaces.client.ReadModifiers;
import com.gigaspaces.client.WriteModifiers;
import com.gigaspaces.framework.ThreadBarrier;
import com.gigaspaces.persistency.qa.model.Issue;
import com.gigaspaces.persistency.qa.model.MongoIssuePojo;
import com.gigaspaces.persistency.qa.model.Priority;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import junit.framework.Assert;
import org.junit.Test;
import org.openspaces.admin.space.SpacePartition;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SpaceMongoInitalLoadTest extends AbstractSystemTestUnit {
	@SuppressWarnings("unchecked")
	private final BiMap<Priority, Integer> priorityMap = initPriortyMap(HashBiMap
			.create());

	// private GigaSpace gigaSpace;
	private volatile boolean work = true;
	private static final int NUMBER_OF_WRITERS = 1;
	private volatile ThreadBarrier barrier = new ThreadBarrier(
			NUMBER_OF_WRITERS + 1);
	private final AtomicInteger idGenerator = new AtomicInteger(0);
	private final ConcurrentMap<Integer, Integer> writes = new ConcurrentHashMap<Integer, Integer>();

	@Override
	@Test
	public void test() {
		try {
			fillClusterData();
			Map<Integer,HashSet<MongoIssuePojo>> objectsByPartition = getObjectsByPartition();
			teardownCluster();
			initConfigurersAndStartSpaces();
			assertValidInitialDataLoad(objectsByPartition);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * @return a mapping between a partition-id and all the objects on this partition
	 */
	private Map<Integer, HashSet<MongoIssuePojo>> getObjectsByPartition() {
		Map<Integer, HashSet<MongoIssuePojo>> res = new HashMap<Integer, HashSet<MongoIssuePojo>>();
		SpacePartition[] spacePartitions = testPU.getSpace().getPartitions();

		for (SpacePartition partition: spacePartitions){
			Integer partitionID = partition.getPartitionId();
			MongoIssuePojo[] issues = partition.getPrimary().getGigaSpace().readMultiple(new MongoIssuePojo(),
					Integer.MAX_VALUE, ReadModifiers.MEMORY_ONLY_SEARCH);
			res.put(partitionID, new HashSet<MongoIssuePojo>(Arrays.asList(issues)));
		}
		return res;
	}

	@Override
	protected String getMirrorService() {
		return "/mongodb-qa-mirror.jar";
	}

	@Override
	protected String getPUJar() {
		return "/initial-load.jar";
	}

	private void assertValidInitialDataLoad(Map<Integer, HashSet<MongoIssuePojo>> objectsByPartition) {
		Assert.assertEquals("Overall number of objects doesn't match",writes.size(),gigaSpace.count(new MongoIssuePojo(), CountModifiers.MEMORY_ONLY_SEARCH));

		SpacePartition[] spacePartitions = testPU.getSpace().getPartitions();

		for (SpacePartition partition: spacePartitions){
			int partitionID = partition.getPartitionId();
			MongoIssuePojo[] issues = partition.getPrimary().getGigaSpace().readMultiple(new MongoIssuePojo(),
					Integer.MAX_VALUE, ReadModifiers.MEMORY_ONLY_SEARCH);
			for (MongoIssuePojo issue : issues) {
				checkVaildIssue(issue);
			}
			HashSet<MongoIssuePojo> actualSet = new HashSet<MongoIssuePojo>(Arrays.asList(issues));
			Assert.assertEquals("Objects after initial load doesn't match the objects before initial load on partition " + partitionID,
					objectsByPartition.get(partitionID),actualSet);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static HashBiMap initPriortyMap(HashBiMap priorityMap) {
		priorityMap.put(Priority.BLOCKER, 0);
		priorityMap.put(Priority.CRITICAL, 1);
		priorityMap.put(Priority.MAJOR, 2);
		priorityMap.put(Priority.MINOR, 3);
		priorityMap.put(Priority.TRIVIAL, 4);
		priorityMap.put(Priority.MEDIUM, 5);
		return priorityMap;
	}

	private void initConfigurersAndStartSpaces() throws Exception {
		startWithoutDropDatabase();

		waitForActiveReplicationChannelWithMirror(gigaSpace.getSpace());
	}

	private void fillClusterData() throws Exception {
		say("starting workers");
		startWorkers();

		say("sleep 10 sec");
		Thread.sleep(10 * 1000);

		say("stopping, written so far: " + writes.size());
		barrier.inspect();
		say("stopping workers");
		work = false;
		barrier.await();

		waitForEmptyReplicationBacklog(gigaSpace.getSpace());

		say("total written: " + writes.size());
	}

	private void teardownCluster() throws Exception {
		stop();
	}

	private void startWorkers() {
		Thread[] writers = new Thread[NUMBER_OF_WRITERS];
		for (int i = 0; i < writers.length; i++) {
			writers[i] = new IssueWriter();
			writers[i].start();
		}
	}

	private MongoIssuePojo createIssue() {
		int id = idGenerator.getAndIncrement();
		MongoIssuePojo issue = new MongoIssuePojo(id, "" + id);
		issue.setPriority(priorityMap.inverse().get(id % priorityMap.size()));
		issue.setVotes(id % priorityMap.size());
		writes.put(id, id);
		return issue;
	}

	private void checkVaildIssue(Issue issue) {
		Integer key = issue.getKey();
		Assert.assertNotNull("No key", key);
		Assert.assertEquals("bad issue", key % priorityMap.size(),
				(int) issue.getVotes());
		Assert.assertEquals("bad issue",
				String.valueOf((int) issue.getVotes()), issue.getVotesRep());
		Assert.assertEquals("bad issue", "" + key, issue.getReporter()
				.getUsername());
	}

	// @Override
	// protected CachePolicy createCachePolicy() {
	// return new AllInCachePolicy();
	// }

	private class IssueWriter extends Thread {
		public void run() {
			try {
				while (work) {
					barrier.inspect();
					MongoIssuePojo[] toWrite = new MongoIssuePojo[100];
					for (int i = 0; i < toWrite.length; i++) {
						toWrite[i] = createIssue();
					}
					gigaSpace.writeMultiple(toWrite,
							WriteModifiers.MEMORY_ONLY_SEARCH);
//                    Pojo<Double> pojo = new Pojo<Double>(0);
//                    pojo.setNum(2.0);
//                    gigaSpace.write(pojo);
				}
				barrier.await();
			} catch (Exception e) {
				barrier.reset(e);
			}
		}
	}


}
