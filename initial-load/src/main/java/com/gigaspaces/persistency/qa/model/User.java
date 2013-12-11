package com.gigaspaces.persistency.qa.model;

import java.io.Serializable;

/**
 * User representation
 */
public class User implements Serializable {

	/** default serial version id */
	private static final long serialVersionUID = 1L;

	private String username;

	public User() {
	}

	/**
	 * Construct a User reference
	 * 
	 * @param username
	 *            user name
	 */
	public User(String username) {
		if (username == null)
			throw new IllegalArgumentException("user's name can't be null");

		this.username = username;
	}

	/**
	 * @return the username of this User instance.
	 */
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/* @see java.lang.Object#toString() */
	@Override
	public String toString() {
		return username;
	}

	/* @see java.lang.Object#hashCode() */
	@Override
	public int hashCode() {
		return username.hashCode();
	}

	/* @see java.lang.Object#equals(java.lang.Object) */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof User))
			return false;

		User otherUser = (User) obj;

		// verify username
		if (this.username == null && otherUser.username != null)
			return false;
		else if (this.username != null
				&& !this.username.equals(otherUser.username))
			return false;

		// all properties equal
		return true;
	}

}
