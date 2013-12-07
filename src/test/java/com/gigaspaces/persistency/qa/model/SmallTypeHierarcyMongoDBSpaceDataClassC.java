package com.gigaspaces.persistency.qa.model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceProperty;

@SpaceClass
public class SmallTypeHierarcyMongoDBSpaceDataClassC extends
		SmallTypeHierarcyMongoDBSpaceDataClassB {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cProp == null) ? 0 : cProp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SmallTypeHierarcyMongoDBSpaceDataClassC other = (SmallTypeHierarcyMongoDBSpaceDataClassC) obj;
		if (cProp == null) {
			if (other.cProp != null)
				return false;
		} else if (!cProp.equals(other.cProp))
			return false;
		return true;
	}

	private String cProp;

	@SpaceProperty
	public String getCProp() {
		return cProp;
	}

	public void setCProp(String cProp) {
		this.cProp = cProp;
	}
}
