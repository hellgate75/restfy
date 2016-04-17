package com.service.restfy.selenium.server.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.ie.InternetExplorerDriverEngine;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.service.restfy.selenium.server.exceptions.ActionException;
import com.service.restfy.selenium.server.exceptions.FrameworkException;
import com.service.restfy.selenium.server.exceptions.NotFoundException;
import com.service.restfy.selenium.server.utils.SeleniumUtilities;
import com.service.restfy.selenium.server.utils.SeleniumUtilities.BROWSER_TYPE;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSearchOnGoogle {

	private static WebDriver chromeWebDriver = null;
	private static WebDriver firefoxWebDriver = null;
	private static WebDriver ieWebDriver = null;
	private static final String URL = "http://www.google.com";
	private static ChromeDriverService chromeService=null;
	private static InternetExplorerDriverService ieService=null;
	private static final boolean isWindows = System.getProperty("os.name").toLowerCase().indexOf("win")>=0;
	private static final String googleSearchText = "Selenium2";
	
	@BeforeClass
	public static void init() throws Exception {
		
		if (isWindows) {
			ieService = new InternetExplorerDriverService.Builder()
			.usingDriverExecutable(new File("./drivers/IEDriverServer.exe"))
			.usingAnyFreePort()
	        .withSilent(true)
	        .withEngineImplementation(InternetExplorerDriverEngine.AUTODETECT)
			.build();
			ieService.start();
			ieWebDriver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.REMOTE, null, null, ieService.getUrl(),DesiredCapabilities.internetExplorer());
			ieWebDriver.get(URL);

			chromeService = new ChromeDriverService.Builder()
	        .usingDriverExecutable(new File("./drivers/chromedriver.exe"))
	        .usingAnyFreePort()
	        .withVerbose(false)
	        .build();
			chromeService.start();
			chromeWebDriver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.REMOTE, null, null, chromeService.getUrl(),DesiredCapabilities.chrome());
			chromeWebDriver.get(URL);

			firefoxWebDriver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.FIREFOX);
			firefoxWebDriver.get(URL);
		}
		else {
			chromeWebDriver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.CROME);
			chromeWebDriver.get(URL);

			firefoxWebDriver = SeleniumUtilities.getBrowserDriver(BROWSER_TYPE.FIREFOX);
			firefoxWebDriver.get(URL);
		}
	}

	@AfterClass
	public static void dispose() {
//		SeleniumUtilities.closeBrowserDriver(firefoxWebDriver);
	}
	
	@Test(timeout=5000)
	public void run1GoogleTestOnChrome() throws NotFoundException, FrameworkException, ActionException {
		assertEquals("The page title should equal Google at the start of the test.", "Google", chromeWebDriver.getTitle());
		WebElement searchField = SeleniumUtilities.searchByClause(chromeWebDriver, By.name("q"));
		SeleniumUtilities.setValueToElement(searchField, googleSearchText);
		SeleniumUtilities.submitButtonElement(searchField);
	    assertTrue("The page title should start with the search string after the search.",
	      (new WebDriverWait(chromeWebDriver, 10)).until(new ExpectedCondition<Boolean>() {
	        public Boolean apply(WebDriver d) {
	          return d.getTitle().toLowerCase().startsWith(googleSearchText.toLowerCase());
	        }
	      })
	    );
		SeleniumUtilities.closeBrowserDriver(chromeWebDriver);
		if (chromeService!=null)
			chromeService.stop();
	}
	
	@Test(timeout=10000)
	public void run0GoogleTestOnIExplorer() throws NotFoundException, FrameworkException, ActionException {
		if (!isWindows) {
			assertEquals("The page title should equal Google at the start of the test.", "Google", ieWebDriver.getTitle());
			WebElement searchField = SeleniumUtilities.searchByClause(ieWebDriver, By.name("q"));
			SeleniumUtilities.setValueToElement(searchField, googleSearchText);
			SeleniumUtilities.submitButtonElement(searchField);
		    assertTrue("The page title should start with the search string after the search.",
		      (new WebDriverWait(ieWebDriver, 10)).until(new ExpectedCondition<Boolean>() {
		        public Boolean apply(WebDriver d) {
		          return d.getTitle().toLowerCase().startsWith(googleSearchText.toLowerCase());
		        }
		      })
		    );
			SeleniumUtilities.closeBrowserDriver(ieWebDriver);
			if (ieService!=null)
				ieService.stop();
		}
		else {
		}
	}

	@Test(timeout=5000)
	public void run2GoogleTestOnFirefox() throws NotFoundException, FrameworkException, ActionException {
		assertEquals("The page title should equal Google at the start of the test.", "Google", firefoxWebDriver.getTitle());
		WebElement searchField = SeleniumUtilities.searchByClause(firefoxWebDriver, By.name("q"));
		SeleniumUtilities.setValueToElement(searchField, googleSearchText);
		SeleniumUtilities.submitButtonElement(searchField);
	    assertTrue("The page title should start with the search string after the search.",
	      (new WebDriverWait(firefoxWebDriver, 10)).until(new ExpectedCondition<Boolean>() {
	        public Boolean apply(WebDriver d) {
	          return d.getTitle().toLowerCase().startsWith(googleSearchText.toLowerCase());
	        }
	      })
	    );
		SeleniumUtilities.closeBrowserDriver(firefoxWebDriver);
	}
	
}
