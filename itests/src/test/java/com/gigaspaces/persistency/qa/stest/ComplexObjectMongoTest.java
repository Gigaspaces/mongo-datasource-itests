package com.gigaspaces.persistency.qa.stest;


import com.gigaspaces.persistency.qa.model.redHonda.Car;
import com.gigaspaces.persistency.qa.model.redHonda.CarNotIndexed;
import com.gigaspaces.persistency.qa.model.redHonda.Person;
import com.gigaspaces.persistency.qa.model.redHonda.PersonPojo;
import com.gigaspaces.persistency.qa.utils.AssertUtils;
import com.j_spaces.core.client.SQLQuery;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: boris
 * Date: 05/01/14
 */
public class ComplexObjectMongoTest extends AbstractSystemTestUnit {

    private static final int ENTRY_NUM = 10;
    private static final int NUM_OF_CARS = 10;
    private static final String[] COLORS = {"RED", "GREEN"};
    private static final String[] MODELS = {"Honda", "Toyota", "Subaru", "BMW"};

    @Override
    @Test
    public void test() {
        say("Complex Object Mongo com.Test Started ...");
        Collection<Person> expected = new ArrayList<Person>();
        boolean inserted;

        for (int i = 0; i < ENTRY_NUM; i++) {
            Person person = new PersonPojo();
            person.setId(i);
            person.setName("P" + i);
            Collection<Car> cars = new ArrayList<Car>();
            inserted = false;
            for (int j = 0; j < NUM_OF_CARS; j++) {
                if (i % 2 == 0) {
                    cars.add(new CarNotIndexed(j, COLORS[j % COLORS.length], (j+i)%7, null, MODELS[j % MODELS.length]));
                    if ((!inserted) && ((j+i)%7 < 4) && (COLORS[j%COLORS.length].equals("RED")) && (MODELS[j%MODELS.length].equals("Honda"))) {
                        expected.add(person);
                        inserted=true;
                    }
                } else {
                    cars.add(new CarNotIndexed(j, "BLACK", j, null, "VW"));
                }
            }
            person.setCars(cars);

            gigaSpace.write(person);
        }

        Person[] queriedPersons = gigaSpace.readMultiple(new SQLQuery<Person>(PersonPojo.class.getName(), "cars[*](color = 'RED' and model = 'Honda' and year < 4)"));

        AssertUtils.assertEquivalenceArrays("Failed because not equal to actual query.", expected.toArray(), queriedPersons);

        Person[] beforeClear = gigaSpace.readMultiple(new PersonPojo(), 20);

        assertMongoEqualsSpace(beforeClear);

        say("Complex Object Mongo com.Test Passed!");
    }

    @Override
    protected String getPUJar() {
        return "/lru.jar";
    }

    private void assertMongoEqualsSpace(Person[] beforeClear) {
        clearMemory(gigaSpace);
        Assert.assertEquals(0, gigaSpace.count(null));
        Person[] queriedPersons = gigaSpace.readMultiple(
                new PersonPojo(), 20);
        AssertUtils.assertEquivalenceArrays("Failed because not equal to actual query.", beforeClear, queriedPersons);
    }
}
