package com.gigaspaces.persistency.qa.stest;

import com.gigaspaces.persistency.qa.model.PojoWithPrimitive;
import com.gigaspaces.persistency.qa.utils.AssertUtils;
import junit.framework.Assert;

import java.util.Arrays;


public class BasicMongoPojoWithPrimitiveTest extends AbstractSystemTestUnit {

	@Override
	public void test() {
        say("Basic Mongo Pojo With Primitive Test");
        PojoWithPrimitive[] pojos = new PojoWithPrimitive[2];
        PojoWithPrimitive pojo = new PojoWithPrimitive();
        pojo.setId(1);
        pojo.setPrimitive(1);
        pojos[0] = pojo;
        PojoWithPrimitive pojo2 = new PojoWithPrimitive();
        pojo2.setId(2);
        pojo2.setPrimitive(2);
        pojos[1] = pojo2;
        say("Trying to write pojos with primitive property to space and database");
        try {
            gigaSpace.writeMultiple(pojos);
        }
		catch (Exception e){
            Assert.fail("caught an exception when writing");
        }
		waitForEmptyReplicationBacklogAndClearMemory(gigaSpace);
        PojoWithPrimitive[] read = gigaSpace.readByIds(PojoWithPrimitive.class, new Object[] {1 , 2}).getResultsArray();
        Assert.assertEquals("length of pojos suppose to be the same as objects in space/db",read.length,pojos.length);
        AssertUtils.assertEquivalent("", Arrays.asList(read), Arrays.asList(pojos));
        say("Basic Mongo Pojo With Primitive Test Passed!");
    }

	@Override
	protected String getPUJar() {
		return "/lru.jar";
	}
}
