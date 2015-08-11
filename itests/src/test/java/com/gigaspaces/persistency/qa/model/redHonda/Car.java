package com.gigaspaces.persistency.qa.model.redHonda;

import java.util.Collection;

public interface Car {
    public Integer getId();
    public void setId(Integer id);

    public String getColor();
    public void setColor(String color);

    public Integer getYear();
    public void setYear(Integer year);
    public Collection<Part> getParts();
    public void setParts(Collection<Part> parts);
    public String getModel();
    public void setModel(String model);

}
