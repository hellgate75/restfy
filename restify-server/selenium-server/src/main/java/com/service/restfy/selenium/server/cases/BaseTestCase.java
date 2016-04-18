package com.service.restfy.selenium.server.cases;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTestCase extends TestCase implements Cloneable {
	static {
		if (System.getProperty("log4j.configurationFile")==null)
			System.setProperty("log4j.configurationFile", "log4j2.xml");
	}
	public static enum TIMER_TYPE {TEST_CASE, SECURITY, RENDERING, TEST_ACTION};
	private static Logger logger = LoggerFactory.getLogger("com.service.restfy.selenium.server");
	
	private String caseName;
	private String caseURL;
	private boolean openUrl;
	private boolean retrowExcpetion;
	
	private Map<String, String> securityInfo = new HashMap<String, String>(0);
	
	protected long startTestCase = 0L;
	protected long stopTestCase = 0L;

	protected long startSecurityAccess = 0L;
	protected long stopSecurityAccess = 0L;

	protected long startRendering = 0L;
	protected long stopRendering = 0L;

	protected long startTestAction = 0L;
	protected long stopTestAction = 0L;
	
	public BaseTestCase(String caseName, String caseURL, boolean openUrl,
			boolean retrowExcpetion) {
		super();
		this.caseName = caseName;
		this.caseURL = caseURL;
		this.openUrl = openUrl;
		this.retrowExcpetion = retrowExcpetion;
	}
	
	public final void startTimeCounter(TIMER_TYPE type) {
		switch(type) {
			case TEST_ACTION:
				startTestAction = System.currentTimeMillis();
				break;
			case RENDERING:
				startRendering = System.currentTimeMillis();
				break;
			case SECURITY:
				startSecurityAccess = System.currentTimeMillis();
				break;
			default:
				startTestCase = System.currentTimeMillis();
				break;
		}
	}
	
	public final void stopTimeCounter(TIMER_TYPE type) {
		switch(type) {
		case TEST_ACTION:
			stopTestAction = System.currentTimeMillis();
			break;
		case RENDERING:
			stopRendering = System.currentTimeMillis();
			break;
		case SECURITY:
			stopSecurityAccess = System.currentTimeMillis();
			break;
		default:
			stopTestCase = System.currentTimeMillis();
			break;
		}
	}
	
	public long getSecurityAccessElapsedTime() {
		return stopSecurityAccess>startSecurityAccess ? stopSecurityAccess - startSecurityAccess : 0;
	}
	
	public long getTestCaseElapsedTime() {
		return stopTestCase>startTestCase ? stopTestCase - startTestCase : 0;
	}
	
	public long getTestActionElapsedTime() {
		return stopTestAction>startTestAction ? stopTestAction - startTestAction : 0;
	}
	
	public long getRenderingElapsedTime() {
		return stopRendering>startRendering ? stopRendering - startRendering : 0;
	}
	
	@Override
	public boolean isSecureConnection() {
		return false;
	}

	public final void setSecurityInfo(Map<String, String> securityInfo) {
		this.securityInfo = securityInfo;
	}

	@Override
	public final Map<String, String> getSecurityInfo() {
		return securityInfo;
	}

	@Override
	public boolean handleSecureConnection(WebDriver driver) {
		return false;
	}

	public final Logger getLogger() {
		return logger;
	}
	
	@Override
	public final String getCaseName() {
		return caseName;
	}

	@Override
	public final String getConnectionURL() {
		return caseURL;
	}

	@Override
	public final boolean isConnectionRequired() {
		return openUrl;
	}

	@Override
	public final boolean isExceptionTransversable() {
		return retrowExcpetion;
	}

	public void resetCounters() {
		startTestCase = 0L;
		stopTestCase = 0L;
		startSecurityAccess = 0L;
		stopSecurityAccess = 0L;
		startRendering = 0L;
		stopRendering = 0L;
		startTestAction = 0L;
		stopTestAction = 0L;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		try {
			Class<? extends BaseTestCase> clazz = this.getClass();
			Constructor<? extends BaseTestCase> constructor = clazz.getConstructor(String.class, String.class, Boolean.class,
				Boolean.class);
			BaseTestCase testCaseCloned =  constructor.newInstance(this.caseName, this.caseURL, this.openUrl, this.retrowExcpetion);
			return testCaseCloned;
		} catch (NoSuchMethodException e) {
			throw new CloneNotSupportedException("Not suitable constructor in class : " + this.getClass().getName());
		} catch (SecurityException e) {
			throw new CloneNotSupportedException("Security exception accessing in the class : " + this.getClass().getName());
		} catch (Throwable e) {
			throw new CloneNotSupportedException("Unspecified clone exception in class : " + this.getClass().getName() + " message : " + e.getMessage());
		}
	}

}
