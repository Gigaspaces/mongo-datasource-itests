package com.gigaspaces.persistency.qa.model.redHonda;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: yohana
 * Date: 5/23/13
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
@SpaceClass
public class PersonPojo implements Person, Serializable,Comparable<PersonPojo> {

    private Integer _id;
    private String _name;
    private Collection<Car> _cars;

    public PersonPojo() {
    }

    public PersonPojo(Integer id, String name, Collection<Car> cars) {
        this._id = id;
        this._name = name;
        this._cars = cars;
    }

    @Override
    @SpaceId
    public Integer getId() {
        return _id;
    }

    @Override
    public void setId(Integer id) {
       _id = id;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void setName(String name) {
        _name = name;
    }

    @Override
    public Collection<Car> getCars() {
        return _cars;
    }

    @Override
    public void setCars(Collection<Car> cars) {
       this._cars = cars;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonPojo)) return false;

        PersonPojo that = (PersonPojo) o;

        if (_id != null ? !_id.equals(that._id) : that._id != null) return false;
        if (_name != null ? !_name.equals(that._name) : that._name != null) return false;
        if (_cars != null ? !_cars.equals(that._cars) : that._cars != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (_name != null ? _name.hashCode() : 0);
        result = 31 * result + (_cars != null ? _cars.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(PersonPojo o) {
        return getId().compareTo(o.getId());
    }
}
