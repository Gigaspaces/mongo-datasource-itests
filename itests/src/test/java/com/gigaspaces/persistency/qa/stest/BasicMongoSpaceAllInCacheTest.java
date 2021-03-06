package com.gigaspaces.persistency.qa.stest;

import com.gigaspaces.persistency.qa.model.IssuePojo;
import com.gigaspaces.persistency.qa.utils.AssertUtils;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BasicMongoSpaceAllInCacheTest extends AbstractSystemTestUnit {

	@Override
	@Test
	public void test() {
		List<IssuePojo> issuePojos = new ArrayList<IssuePojo>();

		for (int i = 0; i < 15; i++) {
			issuePojos.add(new IssuePojo(i + 1, "dank" + (i + 1)));
		}

		gigaSpace.writeMultiple(issuePojos.toArray(new IssuePojo[] {}));

		waitForEmptyReplicationBacklog(gigaSpace);

		List<IssuePojo> pojos = Arrays.asList(gigaSpace.readMultiple(
				new IssuePojo(), 20));

		Collections.sort(pojos);

		Assert.assertEquals("size is not equals", issuePojos.size(),
				pojos.size());

		AssertUtils.assertEquivalent("", issuePojos, pojos);
	}

	@Override
	protected String getPUJar() {
		return "/all-in-cache.jar";
	}
}
