package com.google.android.drdat.cl;

public class DrdatLoginException extends RuntimeException {

	/**
	 * Complain if we get bad login data.
	 */
	private static final long serialVersionUID = -1042522693242204427L;
	public DrdatLoginException(String msg) {
		super(msg);
	}
}
