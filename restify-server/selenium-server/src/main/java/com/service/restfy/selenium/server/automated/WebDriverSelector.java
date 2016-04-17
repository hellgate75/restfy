package com.service.restfy.selenium.server.automated;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.ie.InternetExplorerDriverEngine;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;

import com.service.restfy.selenium.server.automated.WebDriveFactory.SELECTOR_TYPE;
import com.service.restfy.selenium.server.exceptions.FrameworkException;
import com.service.restfy.selenium.server.utils.SeleniumUtilities;
import com.service.restfy.selenium.server.utils.SeleniumUtilities.BROWSER_TYPE;

public class WebDriverSelector {
	private static final boolean isWindows = System.getProperty("os.name").toLowerCase().indexOf("win")>=0;

	private List<Object> parameters = new ArrayList<Object>();
	private SELECTOR_TYPE selector = null;
	private WebDriver driver = null;
	private WebDriver eventFiringDriver = null;
	private ChromeDriverService chromeService=null;
	private InternetExplorerDriverService ieService=null;
	private DriverService customService=null;
	private boolean filterEventFire = false;
	
	protected WebDriverSelector(SELECTOR_TYPE selector, Object... parameters) {
		super();
		this.selector = selector;
		if (parameters!=null)
			this.parameters.addAll(Arrays.asList(parameters));
	}
	
	public void initDriver() throws FrameworkException {
		List<Object> parameters = this.parameters;
		SELECTOR_TYPE selector = this.selector;
		if (this.filterEventFire) {
			selector = (SELECTOR_TYPE)parameters.remove(0);
		}
		if (driver==null) {
			try {
				switch(selector) {
					case IE_INTERNAL_SELECTOR:
						if (isWindows) {
							URL defaultFile = getClass().getResource("/com/service/restfy/selenium/server/win32bin/IEDriverServer.exe");
							ieService = new InternetExplorerDriverService.Builder()
							.usingDriverExecutable(new File(defaultFile.getFile()))
							.usingAnyFreePort()
					        .withSilent(true)
					        .withEngineImplementation(InternetExplorerDriverEngine.AUTODETECT)
							.build();
							ieService.start();
						}
						else {
							driver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.IE);
						}
						break;
					case CHROME_INTERNAL_SELECTOR:
						if (isWindows) {
							URL defaultFile = getClass().getResource("/com/service/restfy/selenium/server/win32bin/chromedriver.exe");
							chromeService = new ChromeDriverService.Builder()
					        .usingDriverExecutable(new File(defaultFile.getFile()))
					        .usingAnyFreePort()
					        .withVerbose(false)
					        .build();
							chromeService.start();
							driver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.REMOTE, null, null, chromeService.getUrl(),DesiredCapabilities.chrome());
						}
						else {
							driver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.CROME);
						}
						break;
					case CHROME_SELECTOR:
						driver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.CROME);
						break;
					case IE_SELECTOR:
						driver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.IE);
						break;
					case OPERA_SELECTOR:
						driver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.OPERA);
						break;
					case REMOTE_SELECTOR:
						if (parameters.size()<1 || parameters.get(0)==null || parameters.get(1)==null || 
						(!CommandExecutor.class.isAssignableFrom(parameters.get(0).getClass()) &&  !DriverService.class.isAssignableFrom(parameters.get(0).getClass()) ) ||
						!Capabilities.class.isAssignableFrom(parameters.get(1).getClass()))
							throw new FrameworkException("Unable to define Remote WebDriver attributes during initialization ...");
						if (CommandExecutor.class.isAssignableFrom(parameters.get(0).getClass()))
							driver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.REMOTE, null, (CommandExecutor)parameters.get(0), null, (Capabilities)parameters.get(1));
						else {
							this.customService = (DriverService)parameters.get(0);
							if (!this.customService.isRunning())
								this.customService.start();
							driver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.REMOTE, null, null, this.customService.getUrl(), (Capabilities)parameters.get(1));
						}
						break;
					case HTML_UNIT_SELECTOR:
						driver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.HTML_UNIT);
						break;
					case EVENT_FIRING_SELECTOR:
						if (parameters.size()==0 || parameters.get(0)==null || 
						(!WebDriver.class.isAssignableFrom(parameters.get(0).getClass()) && !SELECTOR_TYPE.class.isAssignableFrom(parameters.get(0).getClass())) )
							throw new FrameworkException("Unable to locate firing WebDriver during initialization ...");
						if (WebDriver.class.isAssignableFrom(parameters.get(0).getClass()))
							eventFiringDriver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.EVENT_FIRING, (WebDriver)parameters.get(0), null, null, null);
						else {
							this.filterEventFire = true;
							initDriver();
							this.filterEventFire = false;
							eventFiringDriver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.EVENT_FIRING, this.driver, null, null, null);
						}
						break;
				}
			} catch (Throwable e) {
				this.filterEventFire = false;
				throw new FrameworkException("Unable to create driver for selector : " + this.selector + " sue to : ", e);
			}
		}
	}
	
	public WebDriver getWebDriver() throws FrameworkException {
		this.initDriver();
		if(this.eventFiringDriver!=null)
			return this.eventFiringDriver;
		return this.driver;
	}
	
	public void stopWebDriver() {
		if(this.driver!=null)
			SeleniumUtilities.closeBrowserDriver(this.driver);
		if(this.eventFiringDriver!=null)
			SeleniumUtilities.closeBrowserDriver(this.eventFiringDriver);

		if (this.chromeService!=null)
			this.chromeService.stop();
		if (this.ieService!=null)
			this.ieService.stop();
		if (this.customService!=null)
			this.customService.stop();
		
		this.eventFiringDriver = null;
		this.driver = null;
		this.chromeService = null;
		this.ieService = null;
		this.customService = null;
		this.filterEventFire = false;
	}

}
