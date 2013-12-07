package com.gigaspaces.persistency.qa.model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceProperty;


@SpaceClass
public class SmallTypeHierarcyMongoDBSpaceDataClassA implements Comparable<SmallTypeHierarcyMongoDBSpaceDataClassA> {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aProp == null) ? 0 : aProp.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		SmallTypeHierarcyMongoDBSpaceDataClassA other = (SmallTypeHierarcyMongoDBSpaceDataClassA) obj;
		if (aProp == null) {
			if (other.aProp != null)
				return false;
		} else if (!aProp.equals(other.aProp))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private String id;
	private String aProp;

	@SpaceId(autoGenerate = true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@SpaceProperty
	public String getAProp() {
		return aProp;
	}

	public void setAProp(String aProp) {
		this.aProp = aProp;
	}

	public int compareTo(SmallTypeHierarcyMongoDBSpaceDataClassA o) {
		if(id == o.id)
			return 0;
		return id.hashCode()- o.id.hashCode();
	}

}
