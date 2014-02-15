package com.gigaspaces.persistency.qa.model;

import com.gigaspaces.annotation.pojo.SpaceId;

import java.io.Serializable;

/**
 * User: boris
 * Date: 05/01/14
 */
public class ComplexPojo  implements Serializable,Comparable<ComplexPojo>{

    private Integer _id;
    private String _name;
    private IssueDocument _document;

    public ComplexPojo() {}

    public int compareTo(ComplexPojo o) {
        return this.getId().compareTo(o.getId());
    }

    @SpaceId
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

    public IssueDocument getDocument() {
        return _document;
    }

    public void setDocument(IssueDocument document) {
        this._document = document;
    }
}
