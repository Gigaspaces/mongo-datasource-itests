package com.gigaspaces.persistency.qa.model.redHonda;

import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.metadata.SpaceTypeDescriptor;
import com.gigaspaces.metadata.SpaceTypeDescriptorBuilder;

import java.io.Serializable;
import java.util.Collection;


public class PersonDocument extends SpaceDocument implements Person,Serializable,Comparable<PersonDocument> {
    private static final String PERSON = "Person";
    private static final String _id = "id";
    private static final String _name = "name";
    private static final String _cars = "cars";
    private static final String _state = "state";


    public static SpaceTypeDescriptor getTypeDescriptor()
    {
        return new SpaceTypeDescriptorBuilder(PERSON)
                .idProperty(_id)
                .routingProperty(_id).supportsDynamicProperties(true)
                .documentWrapperClass(PersonDocument.class)
                .create();
    }

    public Integer getId() {
        return getProperty(_id);
    }

    public void setId(Integer id) {
       setProperty(_id,id);
    }

    public String getName() {
        return getProperty(_name);
    }

    public PersonDocument() {
        super(PERSON);
    }

    public void setName(String name) {
        setProperty(_name,name);
    }

    public Collection<Car> getCars() {
        return getProperty(_cars);
    }

    public void setCars(Collection<Car> cars) {
        setProperty(_cars,cars);
    }

    /*
     * These functions are relevant to the test RHPollingContainerTest.java, to mark the object as read.
     */
    public Integer getState() {
        return getProperty(_state);
    }

    public void setState(Integer state) {
        setProperty(_state,state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonDocument)) return false;

        PersonDocument that = (PersonDocument) o;

        if (!this.getId().equals(that.getId())) return false;
        if (this.getName() != null) {
            if (!this.getName().equals(that.getName())) {
                return false;
            }
        } else if (that.getName() != null){
            if (!that.getName().equals(this.getName())) {
                return false;
            }
        }

        if (this.getCars() != null) {
            if (!this.getCars().equals(that.getCars())) {
                return false;
            }
        } else if (that.getCars() != null){
            if (!that.getCars().equals(this.getCars())) {
                return false;
            }
        }
        return true;
    }

    public int compareTo(PersonDocument o) {
        return getId().compareTo(o.getId());
    }
}
