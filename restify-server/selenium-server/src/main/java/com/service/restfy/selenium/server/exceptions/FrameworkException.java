package com.service.restfy.selenium.server.exceptions;

public class FrameworkException extends Exception {

	/**
	 * Exception Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 8963951371409012055L;

	public FrameworkException() {
		super();
	}

	public FrameworkException(String message) {
		super(message);
	}

	public FrameworkException(Throwable cause) {
		super(cause);
	}

	public FrameworkException(String message, Throwable cause) {
		super(message, cause);
	}

	public FrameworkException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
