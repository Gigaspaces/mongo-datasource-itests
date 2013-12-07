package com.gigaspaces.persistency.qa.stest;

import java.sql.SQLTransientConnectionException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.ReadMultipleException;
import org.openspaces.core.TakeMultipleException;





import com.gigaspaces.client.WriteModifiers;
import com.gigaspaces.framework.ThreadBarrier;
import com.gigaspaces.persistency.qa.model.Issue;
import com.gigaspaces.persistency.qa.model.MongoIssuePojo;
import com.gigaspaces.persistency.qa.model.Priority;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class SpaceMongoLoadTest extends AbstractSystemTestUnit {

	private final Random random = new Random();
	private final BiMap<Priority, Integer> priorityMap = HashBiMap.create();

	private volatile boolean work = true;
	private static final int NUMBER_OF_WRITERS = 5;
	private static final int NUMBER_OF_TAKERS = 2;
	private static final int NUMBER_OF_READERS = 1;
	private volatile ThreadBarrier barrier = new ThreadBarrier(
			NUMBER_OF_WRITERS + NUMBER_OF_TAKERS + NUMBER_OF_READERS + 1);
	private final AtomicInteger idGenerator = new AtomicInteger(0);
	private final ConcurrentMap<Integer, Integer> takes = new ConcurrentHashMap<Integer, Integer>();
	private final ConcurrentMap<Integer, Integer> dupTakes = new ConcurrentHashMap<Integer, Integer>();
	private final ConcurrentMap<Integer, Integer> writes = new ConcurrentHashMap<Integer, Integer>();
	private final ConcurrentMap<Integer, Integer> nonValidIssues = new ConcurrentHashMap<Integer, Integer>();

	@Override
	public void test() {
		priorityMap.put(Priority.BLOCKER, 0);
		priorityMap.put(Priority.CRITICAL, 1);
		priorityMap.put(Priority.MAJOR, 2);
		priorityMap.put(Priority.MINOR, 3);
		priorityMap.put(Priority.TRIVIAL, 4);
		priorityMap.put(Priority.MEDIUM, 5);

		try {
			Date start = new Date();
			test(gigaSpace);
			Date end = new Date();

			double seconds = ((double) (end.getTime() - start.getTime()))
					/ (double) 1000;

			say("performance time take: " + seconds + " sec.");
		} catch (Throwable e) {
			throw new AssertionError(e);
		}
	}

	@Override
	protected String getPUJar() {
		return "/load-0.0.1-SNAPSHOT.jar";
	}

	private void test(final GigaSpace gigaSpace) throws Throwable {
		say("starting workers");
		startWorkers();

		say("sleep 15 sec");
		Thread.sleep(15 * 1000);

		say("written so far: " + writes.size() + ", taken so far: "
				+ takes.size());
		barrier.inspect();
		work = false;
		barrier.await();
		say("taking remaning entries. total written: " + writes.size()
				+ ", taken so far: " + takes.size());
		long startTime = System.currentTimeMillis();
		// votes values are 0..priorityMap.size()-1
		for (int i = 0; i < priorityMap.size(); i++) {
			MongoIssuePojo votesTemplate = createTemplate(i);
			MongoIssuePojo[] takenIssues = null;
			do {
				long now = System.currentTimeMillis();
				try {
					takenIssues = gigaSpace.takeMultiple(votesTemplate, 1000);
				} catch (TakeMultipleException e) {
					for (Throwable takeThrowable : e.getCauses()) {
						if (!isRetryableSpaceDataSourceException(takeThrowable)) {
							throw takeThrowable;
						}
					}
					handleTakes(i, e.getResults(), now);
					Thread.sleep(1000);
					continue;
				} catch (Exception e) {
					if (isRetryableSpaceDataSourceException(e)) {
						continue;
					} else {
						throw e;
					}
				}
				handleTakes(i, takenIssues, now);
			} while (takenIssues.length > 0);
		}

		say("taking " + writes.size() + " took "
				+ (System.currentTimeMillis() - startTime) + "ms");

		Assert.assertTrue("duplicate takes: " + dupTakes, dupTakes.isEmpty());
		Assert.assertTrue("invalid takes: " + nonValidIssues,
				nonValidIssues.isEmpty());

		say("total written: " + writes.size());
		Assert.assertEquals(writes, takes);

	}

	private void handleTakes(int i, Object[] takenIssues, long now) {
		// say("took=" + takenIssues.length + "\tvotes=" + i + " \tms="
		// + (System.currentTimeMillis() - now));
		for (Object o : takenIssues) {
			MongoIssuePojo issue = (MongoIssuePojo) o;
			checkVaildIssue(issue);
			Integer previous = takes.put(issue.getKey(), issue.getKey());
			if (previous != null) {
				dupTakes.put(previous, previous);
			}
		}
	}

	private void startWorkers() {
		Thread[] writers = new Thread[NUMBER_OF_WRITERS];
		for (int i = 0; i < writers.length; i++) {
			writers[i] = new IssueWriter();
			writers[i].start();
		}
		Thread[] takers = new Thread[NUMBER_OF_TAKERS];
		for (int i = 0; i < takers.length; i++) {
			takers[i] = new IssueTaker();
			takers[i].start();
		}
		Thread[] readers = new Thread[NUMBER_OF_READERS];
		for (int i = 0; i < readers.length; i++) {
			readers[i] = new IssueReader();
			readers[i].start();
		}
	}

	private MongoIssuePojo createIssue() {
		int id = idGenerator.getAndIncrement();
		MongoIssuePojo issue = new MongoIssuePojo(id, "" + id);
		issue.setPriority(priorityMap.inverse().get(id % priorityMap.size()));
		issue.setVotes(id % priorityMap.size());
		byte[] payload = new byte[1024];
		random.nextBytes(payload);
		issue.setPayload(payload);
		writes.put(id, id);
		return issue;
	}

	private MongoIssuePojo createTemplate() {
		return createTemplate(random.nextInt(priorityMap.size()));
	}

	private MongoIssuePojo createTemplate(int votes) {
		MongoIssuePojo template = new MongoIssuePojo();
		template.setVotes(votes);
		return template;
	}

	private void checkVaildIssue(Issue issue) {
		try {
			Integer key = issue.getKey();
			Assert.assertNotNull("No key", key);
			Assert.assertEquals("bad issue", key % priorityMap.size(),
					(int) issue.getVotes());
			Assert.assertEquals("bad issue", "" + key, issue.getReporter()
					.getUsername());
		} catch (AssertionError e) {
			nonValidIssues.put(issue.getKey(), issue.getKey());
		}
	}

	// @Override
	// protected CachePolicy createCachePolicy() {
	// return new LruCachePolicy().size(10000);
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
				}
				barrier.await();
			} catch (Exception e) {
				barrier.reset(e);
			}
		}
	}

	private class IssueTaker extends Thread {
		public void run() {
			try {
				while (work) {
					barrier.inspect();
					try {
						MongoIssuePojo[] issues = gigaSpace.takeMultiple(
								createTemplate(), 1000);
						for (MongoIssuePojo issue : issues) {
							checkVaildIssue(issue);
							Integer previous = takes.put(issue.getKey(),
									issue.getKey());
							if (previous != null) {
								dupTakes.put(previous, previous);
							}
						}
					} catch (TakeMultipleException e) {
						for (Throwable takeThrowable : e.getCauses()) {
							if (!isRetryableSpaceDataSourceException(takeThrowable)) {
								throw takeThrowable;
							}
						}

						for (Object object : e.getResults()) {
							MongoIssuePojo issue = (MongoIssuePojo) object;
							checkVaildIssue(issue);
							Integer previous = takes.put(issue.getKey(),
									issue.getKey());
							if (previous != null) {
								dupTakes.put(previous, previous);
							}
						}
						Thread.sleep(1000);
					} catch (Exception e) {
						if (!isRetryableSpaceDataSourceException(e)) {
							throw e;
						}
					}
				}
				barrier.await();
			} catch (Throwable e) {
				barrier.reset(e);
			}
		}
	}

	private class IssueReader extends Thread {
		public void run() {
			try {
				while (work) {
					barrier.inspect();
					try {
						MongoIssuePojo[] issues = gigaSpace.readMultiple(
								createTemplate(), 1000);
						for (MongoIssuePojo issue : issues) {
							checkVaildIssue(issue);
						}
					} catch (ReadMultipleException e) {
						for (Throwable t : e.getCauses()) {
							if (!isRetryableSpaceDataSourceException(t)) {
								throw t;
							}
						}
					} catch (Exception e) {
						if (!isRetryableSpaceDataSourceException(e)) {
							throw e;
						}
					}
					Thread.sleep(1000);
				}
				barrier.await();
			} catch (Throwable e) {
				barrier.reset(e);
			}
		}
	}

	private static boolean isRetryableSpaceDataSourceException(Throwable e) {
		if (!(e instanceof RuntimeException))
			return false;

		Throwable current = e;
		while (current != null) {
			if (current instanceof SQLTransientConnectionException) {
				return true;
			}
			current = current.getCause();
		}
		return false;

	}
}
