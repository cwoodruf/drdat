package com.google.android.drdat.cl;

public class DrdatSmi2TaskListException extends RuntimeException {

	/**
	 * Complains if you are trying to update a list of tasks without an email / password pair.
	 */
	private static final long serialVersionUID = -2558561030015519763L;
	
	public DrdatSmi2TaskListException(String msg) {
		super(msg);
	}

}
