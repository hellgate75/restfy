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

import com.service.restfy.selenium.server.automated.SeleniumAutomatedServer;
import com.service.restfy.selenium.server.cases.TestEngine;
import com.service.restfy.selenium.server.test.GoogleSearchTestCase;
import com.service.restfy.selenium.server.utils.SeleniumUtilities;
import com.service.restfy.selenium.server.utils.SeleniumUtilities.BROWSER_TYPE;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSeleniumAutmatedServerOnGoogleSearch {

	private static SeleniumAutomatedServer automatedServer = new SeleniumAutomatedServer();
	private String configFileChrome = "src/test/resources/cromeAutomatedTest.properies";
	private String configFileIntenetExplore = "src/test/resources/internetExplorerAutomatedTest.properies";
	private String configFileFirefox = "src/test/resources/firefoxAutomatedTest.properies";
	private static final boolean isWindows = System.getProperty("os.name").toLowerCase().indexOf("win")>=0;
	
	
	@Test(timeout=25000)
	public void run0GoogleTestOnIExplorer() throws Throwable {
		if (isWindows) {
			automatedServer.readConfig(configFileIntenetExplore);
			automatedServer.startTests();
		}
		else {
		}
	}

	@Test(timeout=25000)
	public void run1GoogleTestOnChrome() throws Throwable {
		automatedServer.readConfig(configFileChrome);
		automatedServer.startTests();
	}
	

	@Test(timeout=25000)
	public void run2GoogleTestOnFirefox() throws Throwable {
		automatedServer.readConfig(configFileFirefox);
		automatedServer.startTests();
	}
	
}
