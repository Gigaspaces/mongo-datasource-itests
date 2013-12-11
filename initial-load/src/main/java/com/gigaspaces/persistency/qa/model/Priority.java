package com.gigaspaces.persistency.qa.model;

/**
 * Priority of this Issue; default trivial. Priority increases at vote-levels.
 */
public enum Priority {

	TRIVIAL, MINOR, MEDIUM, MAJOR, CRITICAL, BLOCKER;

	public static boolean isTrivial(Priority p) {
		return p == TRIVIAL;
	}

	public static boolean isMinor(Priority p) {
		return p == MINOR;
	}

	public static boolean isMedium(Priority p) {
		return p == MEDIUM;
	}

	public static boolean isMajor(Priority p) {
		return p == MAJOR;
	}

	public static boolean isCritical(Priority p) {
		return p == CRITICAL;
	}

	public static boolean isBlocker(Priority p) {
		return p == BLOCKER;
	}

}
