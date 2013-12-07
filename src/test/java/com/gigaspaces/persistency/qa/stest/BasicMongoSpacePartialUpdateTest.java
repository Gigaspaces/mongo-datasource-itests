package com.gigaspaces.persistency.qa.stest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;






import com.gigaspaces.client.WriteModifiers;
import com.gigaspaces.persistency.qa.model.IssuePojo;
import com.gigaspaces.persistency.qa.model.Priority;
import com.gigaspaces.persistency.qa.utils.AssertUtils;


public class BasicMongoSpacePartialUpdateTest extends AbstractSystemTestUnit {

	@Override
	public void test() {
		List<IssuePojo> issuePojos = new LinkedList<IssuePojo>();
		for (int i = 0; i < 15; i++) {
			IssuePojo issue = new IssuePojo(i, "dank" + i);
			issue.setPriority(Priority.TRIVIAL);
			issuePojos.add(issue);
		}

		gigaSpace.writeMultiple(issuePojos.toArray(new IssuePojo[] {}));
		waitForEmptyReplicationBacklogAndClearMemory(gigaSpace);
		IssuePojo[] pojos = gigaSpace.readMultiple(new IssuePojo(), 20);

		AssertUtils.assertEquivalent("issue pojos", issuePojos,
				Arrays.asList(pojos));

		List<IssuePojo> partialUpdateIssuePojos = new LinkedList<IssuePojo>();
		for (int i = 0; i < pojos.length; i++) {
			IssuePojo issue = new IssuePojo();
			issue.setKey(pojos[i].getKey());
			issue.setPriority(Priority.BLOCKER);
			issuePojos.get(i).setPriority(Priority.BLOCKER);
			partialUpdateIssuePojos.add(issue);
		}

		
		gigaSpace.writeMultiple(
				partialUpdateIssuePojos.toArray(new IssuePojo[] {}),
				WriteModifiers.PARTIAL_UPDATE);
		waitForEmptyReplicationBacklogAndClearMemory(gigaSpace);
		pojos = gigaSpace.readMultiple(new IssuePojo(), 20);

		AssertUtils.assertEquivalent("issue pojos", issuePojos,
				Arrays.asList(pojos));

	}

	@Override
	protected String getPUJar() {		
		return "/partial-update-0.0.1-SNAPSHOT.jar";
	}
}
