package com.gigaspaces.persistency.qa.stest;

import com.gigaspaces.persistency.qa.model.SmallTypeHierarcyMongoDBSpaceDataClassA;
import com.gigaspaces.persistency.qa.model.SmallTypeHierarcyMongoDBSpaceDataClassB;
import com.gigaspaces.persistency.qa.model.SmallTypeHierarcyMongoDBSpaceDataClassC;
import com.gigaspaces.persistency.qa.utils.AssertUtils;

public class SmallTypeHierarcyMongoSpaceTest extends AbstractSystemTestUnit {

	@Override
	public void test() {
		SmallTypeHierarcyMongoDBSpaceDataClassA typeA = new SmallTypeHierarcyMongoDBSpaceDataClassA();
		SmallTypeHierarcyMongoDBSpaceDataClassB typeB = new SmallTypeHierarcyMongoDBSpaceDataClassB();
		SmallTypeHierarcyMongoDBSpaceDataClassC typeC = new SmallTypeHierarcyMongoDBSpaceDataClassC();

		typeA.setAProp("a");
		typeB.setAProp("a");
		typeB.setBProp("b");
		typeC.setAProp("a");
		typeC.setBProp("b");
		typeC.setCProp("c");

		gigaSpace.write(typeA);
		gigaSpace.write(typeB);
		gigaSpace.write(typeC);
		waitForEmptyReplicationBacklog(gigaSpace);
		
		SmallTypeHierarcyMongoDBSpaceDataClassA[] as = gigaSpace
				.readMultiple(new SmallTypeHierarcyMongoDBSpaceDataClassA());
		clearMemory(gigaSpace);
		SmallTypeHierarcyMongoDBSpaceDataClassB[] bs = gigaSpace
				.readMultiple(new SmallTypeHierarcyMongoDBSpaceDataClassB());
		clearMemory(gigaSpace);
		SmallTypeHierarcyMongoDBSpaceDataClassC[] cs = gigaSpace
				.readMultiple(new SmallTypeHierarcyMongoDBSpaceDataClassC());

		assertExpectedQueryResult(as, typeA, typeB, typeC);
		assertExpectedQueryResult(bs, typeB, typeC);
		assertExpectedQueryResult(cs, typeC);
	}

	private void assertExpectedQueryResult(Object[] actual, Object... expected) {
		AssertUtils.assertEquivalenceArrays("Read result", expected, actual);
	}

	@Override
	protected String getPUJar() {
		return "/lru-0.0.1-SNAPSHOT.jar";
	}
}
