package com.gigaspaces.persistency.qa.model;

import com.gigaspaces.annotation.pojo.SpaceProperty;

import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceClass;

/**
 * @author Svitlana_Pogrebna
 *
 */
@SpaceClass(persist = true)
public class PojoToBeReloaded {
    
    private Integer id;
    
    private int primitivePropertyToRemove;
    private Long propertyToRemove;
    private PojoWithPrimitive pojoPropertyToRemove;
   
    @SpaceId
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    @SpaceProperty(nullValue = "-1")
    public int getPrimitivePropertyToRemove() {
        return primitivePropertyToRemove;
    }
    
    public void setPrimitivePropertyToRemove(int primitivePropertyToRemove) {
        this.primitivePropertyToRemove = primitivePropertyToRemove;
    }
    
    public Long getPropertyToRemove() {
        return propertyToRemove;
    }
   
    public void setPropertyToRemove(Long propertyToRemove) {
        this.propertyToRemove = propertyToRemove;
    }
    
    public PojoWithPrimitive getPojoPropertyToRemove() {
        return pojoPropertyToRemove;
    }
    
    public void setPojoPropertyToRemove(PojoWithPrimitive pojoPropertyToRemove) {
        this.pojoPropertyToRemove = pojoPropertyToRemove;
    }
}
