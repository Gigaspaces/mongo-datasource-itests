package com.gigaspaces.persistency.qa.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceLeaseExpiration;

/**
 * @author Svitlana_Pogrebna
 */
@SpaceClass(persist = true)
public class PojoSupportsLeaseExpiration implements Comparable<PojoSupportsLeaseExpiration> {
    
    private Integer id;
    private String description;
    private long lease;
    private LocalDate date;
    private LocalDateTime dateTime;
    private LocalTime time;
    private ZonedDateTime zonedDateTime;
    
    @SpaceId
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    @SpaceLeaseExpiration
    public long getLease() {
        return lease;
    }

    public void setLease(long lease) {
        this.lease = lease;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + ((zonedDateTime == null) ? 0 : zonedDateTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PojoSupportsLeaseExpiration other = (PojoSupportsLeaseExpiration) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (dateTime == null) {
            if (other.dateTime != null)
                return false;
        } else if (!dateTime.equals(other.dateTime))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        if (zonedDateTime == null) {
            if (other.zonedDateTime != null)
                return false;
        } else if (!zonedDateTime.equals(other.zonedDateTime))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PojoSupportsLeaseExpiration [id=" + id + ", description=" + description + ", lease=" + lease + ", date=" + date + ", dateTime=" + dateTime + ", time=" + time + ", zonedDateTime=" + zonedDateTime + "]";
    }

    @Override
    public int compareTo(PojoSupportsLeaseExpiration o) {
        return this.id.compareTo(o.id);
    }
}
