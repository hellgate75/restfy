package com.service.restfy.selenium.server.automated;


public class WebDriveFactory {
	private static WebDriveFactory instance = null;

	public static enum SELECTOR_TYPE {IE_SELECTOR, REMOTE_SELECTOR, IE_INTERNAL_SELECTOR, CHROME_SELECTOR, CHROME_INTERNAL_SELECTOR,
									  OPERA_SELECTOR, HTML_UNIT_SELECTOR, EVENT_FIRING_SELECTOR}; 
	
	protected WebDriveFactory() {
		super();
	}
	
	public WebDriverSelector getDriverSelector(SELECTOR_TYPE selector, Object... parameters) {
		return new WebDriverSelector(selector, parameters);
	}
	
	public static WebDriveFactory getInstance() {
		if (instance == null)
			instance = new WebDriveFactory();
		return instance;
	}

}
