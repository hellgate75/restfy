package com.service.restfy.selenium.server.cases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTestCase extends TestCase {
	static {
		if (System.getProperty("log4j.configurationFile")==null)
			System.setProperty("log4j.configurationFile", "log4j2.xml");
	}
	private static Logger logger = LoggerFactory.getLogger("com.service.restfy.selenium.server");
	
	private String caseName;
	private String caseURL;
	private boolean openUrl;
	private boolean retrowExcpetion;

	public BaseTestCase(String caseName, String caseURL, boolean openUrl,
			boolean retrowExcpetion) {
		super();
		this.caseName = caseName;
		this.caseURL = caseURL;
		this.openUrl = openUrl;
		this.retrowExcpetion = retrowExcpetion;
	}

	public Logger getLogger() {
		return logger;
	}
	
	@Override
	public String getCaseName() {
		return caseName;
	}

	@Override
	public String getCaseURL() {
		return caseURL;
	}

	@Override
	public boolean connectToURL() {
		return openUrl;
	}

	@Override
	public boolean rethrowException() {
		return retrowExcpetion;
	}

}
