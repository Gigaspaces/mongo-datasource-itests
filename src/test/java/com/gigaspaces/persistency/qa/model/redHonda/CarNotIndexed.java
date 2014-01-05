package com.gigaspaces.persistency.qa.model.redHonda;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceIndex;
import com.gigaspaces.metadata.index.SpaceIndexType;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: yohana
 * Date: 5/23/13
 * Time: 11:40 AM
 * To change this template use File | Settings | File Templates.
 */
@SpaceClass
public class CarNotIndexed implements Car, Serializable {
    private Integer id;
    private String _color;
    private Integer _year;
    private Collection<Part> _parts;
    private String _model;


    public CarNotIndexed() {
    }

    public CarNotIndexed(Integer id, String color, Integer year, Collection<Part> parts, String model) {
        this.id = id;
        this._color = color;
        this._year = year;
        this._parts = parts;
        this._model = model;
    }

    @SpaceId(autoGenerate = false)
    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getColor() {
        return _color;
    }


    public void setColor(String color) {
        _color = color;
    }

    @SpaceIndex(type  = SpaceIndexType.EXTENDED)
    public Integer getYear() {
        return _year;
    }


    public void setYear(Integer year) {
        _year = year;
    }


    public Collection<Part> getParts() {
        return _parts;
    }


    public void setParts(Collection<Part> parts) {
        _parts = parts;
    }


    public String getModel() {
        return _model;
    }


    public void setModel(String model) {
        _model = model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarNotIndexed)) return false;

        CarNotIndexed car = (CarNotIndexed) o;

        if (_color != null ? !_color.equals(car._color) : car._color != null) return false;
        if (id != null ? !id.equals(car.id) : car.id != null) return false;
        if (_model != null ? !_model.equals(car._model) : car._model != null) return false;
        if (_parts != null ? !_parts.equals(car._parts) : car._parts != null) return false;
        if (_year != null ? !_year.equals(car._year) : car._year != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (_color != null ? _color.hashCode() : 0);
        result = 31 * result + (_year != null ? _year.hashCode() : 0);
        result = 31 * result + (_parts != null ? _parts.hashCode() : 0);
        result = 31 * result + (_model != null ? _model.hashCode() : 0);
        return result;
    }
}
