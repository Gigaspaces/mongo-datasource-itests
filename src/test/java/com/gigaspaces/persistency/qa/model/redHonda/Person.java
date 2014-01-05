package com.gigaspaces.persistency.qa.model.redHonda;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: yohana
 * Date: 5/23/13
 * Time: 11:29 AM
 */
public interface Person {
    /**
     * @return	Returns the id.
     */
    Integer getId();

    /**
     * @param id	The id to set.
     */
    void setId(Integer id);

    /**
     * @return	Returns the name.
     */
    String getName();

    /**
     * @param name	The name to set.
     */
    public void setName(String name);

    public Collection<Car> getCars();

    public void setCars(Collection<Car> cars);
}
