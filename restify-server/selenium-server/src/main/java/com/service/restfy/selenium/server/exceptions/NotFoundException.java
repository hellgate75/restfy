package com.service.restfy.selenium.server.exceptions;

public class NotFoundException extends Exception {

	/**
	 * Exception Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 8866962255414376192L;

	public NotFoundException() {
		super();
	}

	public NotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}

}
