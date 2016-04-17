package com.service.restfy.selenium.server.cases;

import java.util.UUID;

import org.openqa.selenium.WebDriver;

public abstract class TestCase {
	private String uid = null;
	
	public TestCase() {
		super();
		uid = UUID.randomUUID().toString();
	}

	public final String getCaseUID() {
		return uid;
	}
	
	public abstract String getCaseName();

	public abstract String getCaseURL();

	public abstract boolean connectToURL();

	public abstract boolean rethrowException();
	
	public abstract void automatedTest(WebDriver driver) throws Throwable;

	@Override
	public final String toString() {
		return "TestCase [Unique Identifier="+uid+", Case Name=" + getCaseName() + ", Case URL="
				+ getCaseURL() + ", Connect URL=" + connectToURL() + "]";
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestCase other = (TestCase) obj;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}

	
}
