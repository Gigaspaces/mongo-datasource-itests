package com.gigaspaces.persistency.qa.stest;

import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.persistency.qa.utils.AssertUtils;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: boris
 * Date: 01/01/14
 */
public class BasicMongoInnerClassTest extends AbstractSystemTestUnit {

    static class InnerPojo implements Comparable<InnerPojo> {

        public InnerPojo() {}

        private Integer _id;

        @SpaceId(autoGenerate = false)
        public Integer getId()
        {
            return _id;
        }

        public void setId(Integer id)
        {
            this._id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            InnerPojo innerPojo = (InnerPojo) o;

            if (_id != null ? !_id.equals(innerPojo._id) : innerPojo._id != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return _id != null ? _id.hashCode() : 0;
        }

        @Override
        public int compareTo(InnerPojo o) {
            return o.getId().compareTo(getId());
        }
    }

    @Override
    public void test() {
        say("Basic Mongo Inner Class com.Test Started ...");
        List<InnerPojo> pojos = new ArrayList<InnerPojo>();
        for (int i = 0 ; i<10 ; i++){
            InnerPojo pojo = new InnerPojo();
            pojo.setId(i+1);
            pojos.add(pojo);
        }
        gigaSpace.writeMultiple(pojos.toArray(new InnerPojo[] {}));

        waitForEmptyReplicationBacklog(gigaSpace);

        List<InnerPojo> readPojos = Arrays.asList(gigaSpace.readMultiple(
                new InnerPojo(), 20));

        junit.framework.Assert.assertEquals("size is not equals", pojos.size(),
                readPojos.size());

        AssertUtils.assertEquivalent("", pojos, readPojos);

        assertMongoEqualsSpace(pojos);

        say("Basic Mongo Inner Class com.Test Passed!");
    }

    private void assertMongoEqualsSpace(List<InnerPojo> beforeClear) {
        clearMemory(gigaSpace);
        Assert.assertEquals(0, gigaSpace.count(null));
        List<InnerPojo> pojos = Arrays.asList(gigaSpace.readMultiple(
                new InnerPojo(), 20));
        AssertUtils.assertEquivalent("space is not equivalent to mongo", beforeClear, pojos);

    }

    @Override
    protected String getPUJar() {
        return "/lru.jar";
    }


}
