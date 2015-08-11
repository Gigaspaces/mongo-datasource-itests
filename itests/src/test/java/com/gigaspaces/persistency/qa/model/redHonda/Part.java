package com.gigaspaces.persistency.qa.model.redHonda;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: yohana
 * Date: 5/23/13
 * Time: 11:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class Part implements Serializable {
    private Integer _id;
    private String _name;
    private Integer weight;
    private Collection<Part> _parts;

    public Part() {
    }

    public Part(Integer id, String name,Integer weight, Collection<Part> parts) {
        this._id = id;
        this._name = name;
        this.weight = weight;
        this._parts = parts;
    }

    public Integer getId() {
        return _id;
    }
    public void setId(Integer id) {
        _id = id;
    }

    public String getName() {
        return _name;
    }
    public void setName(String name) {
        _name = name;
    }

    public Collection<Part> getParts() {
        return _parts;
    }
    public void setParts(Collection<Part> parts) {
        _parts = parts;
    }

    public Integer getWeight() {
        return weight;
    }
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Part)) return false;

        Part part = (Part) o;

        if (_id != null ? !_id.equals(part._id) : part._id != null) return false;
        if (_name != null ? !_name.equals(part._name) : part._name != null) return false;
        if (_parts != null ? !_parts.equals(part._parts) : part._parts != null) return false;
        if (weight != null ? !weight.equals(part.weight) : part.weight != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (_name != null ? _name.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        result = 31 * result + (_parts != null ? _parts.hashCode() : 0);
        return result;
    }
}
