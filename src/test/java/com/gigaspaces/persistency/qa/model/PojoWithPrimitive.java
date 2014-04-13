package com.gigaspaces.persistency.qa.model;

import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceProperty;

import java.io.Serializable;

public class PojoWithPrimitive implements Serializable,Comparable<PojoWithPrimitive>
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private int primitive;

    public PojoWithPrimitive(){}

    @SpaceId(autoGenerate = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @SpaceProperty(nullValue="-1" )
    public int getPrimitive() {
        return primitive;
    }

    public void setPrimitive(int primitive) {
        this.primitive = primitive;
    }

    @Override
    public int compareTo(PojoWithPrimitive o) {
        return this.id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PojoWithPrimitive that = (PojoWithPrimitive) o;

        if (primitive != that.primitive) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + primitive;
        return result;
    }
}