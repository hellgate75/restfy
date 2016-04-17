package com.service.restfy.selenium.server.tests;

import java.io.File;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.ie.InternetExplorerDriverEngine;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.service.restfy.selenium.server.cases.TestEngine;
import com.service.restfy.selenium.server.test.GoogleSearchTestCase;
import com.service.restfy.selenium.server.utils.SeleniumUtilities;
import com.service.restfy.selenium.server.utils.SeleniumUtilities.BROWSER_TYPE;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestEngineOnGoogleSearch {

	private static WebDriver chromeWebDriver = null;
	private static WebDriver firefoxWebDriver = null;
	private static WebDriver ieWebDriver = null;
	private static ChromeDriverService chromeService=null;
	private static InternetExplorerDriverService ieService=null;
	private static final boolean isWindows = System.getProperty("os.name").toLowerCase().indexOf("win")>=0;
	private static TestEngine testEngine = null;
	
	public static void initChromeWebDriver() throws Exception {
		
		if (isWindows) {
			chromeService = new ChromeDriverService.Builder()
	        .usingDriverExecutable(new File("./drivers/chromedriver.exe"))
	        .usingAnyFreePort()
	        .withVerbose(false)
	        .build();
			chromeService.start();
			chromeWebDriver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.REMOTE, null, null, chromeService.getUrl(),DesiredCapabilities.chrome());
		}
		else {
			chromeWebDriver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.CROME);
			testEngine = new TestEngine(chromeWebDriver, false);
			testEngine.addCase(new GoogleSearchTestCase());
		}
	}

	public static void initIeWebDriver() throws Exception {
		
		if (isWindows) {
			ieService = new InternetExplorerDriverService.Builder()
			.usingDriverExecutable(new File("./drivers/IEDriverServer.exe"))
			.usingAnyFreePort()
	        .withSilent(true)
	        .withEngineImplementation(InternetExplorerDriverEngine.AUTODETECT)
			.build();
			ieService.start();
			ieWebDriver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.REMOTE, null, null, ieService.getUrl(),DesiredCapabilities.internetExplorer());
			testEngine = new TestEngine(ieWebDriver, false);
			testEngine.addCase(new GoogleSearchTestCase());
		}
		else {
		}
	}
	
	public static void initFirefoxWebDriver() throws Exception {
			firefoxWebDriver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.FIREFOX);
	}
	
	@Test(timeout=25000)
	public void run0GoogleTestOnIExplorer() throws Throwable {
		if (isWindows) {
			initIeWebDriver();
			testEngine.run();
			testEngine.report(System.out);
			SeleniumUtilities.closeBrowserDriver(ieWebDriver);
			if (ieService!=null)
				ieService.stop();
		}
		else {
		}
	}

	@Test(timeout=25000)
	public void run1GoogleTestOnChrome() throws Throwable {
		initChromeWebDriver();
		testEngine.setWebDriver(chromeWebDriver);
		testEngine.run();
		testEngine.report(System.out);
		SeleniumUtilities.closeBrowserDriver(chromeWebDriver);
		if (chromeService!=null)
			chromeService.stop();
	}
	

	@Test(timeout=25000)
	public void run2GoogleTestOnFirefox() throws Throwable {
		initFirefoxWebDriver();
		testEngine.setWebDriver(firefoxWebDriver);
		testEngine.run();
		testEngine.report(System.out);
		SeleniumUtilities.closeBrowserDriver(firefoxWebDriver);
	}
	
}
