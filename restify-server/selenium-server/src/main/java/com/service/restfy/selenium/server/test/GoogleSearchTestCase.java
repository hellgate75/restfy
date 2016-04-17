package com.service.restfy.selenium.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.service.restfy.selenium.server.cases.BaseTestCase;
import com.service.restfy.selenium.server.utils.SeleniumUtilities;

public class GoogleSearchTestCase extends BaseTestCase {
	private String googleSearchText = "";
	
	public GoogleSearchTestCase() {
		super("Google Test Case", "http://www.google.com", true, true);
	}

	@Override
	public void automatedTest(WebDriver driver) throws Throwable {
		assertEquals("The page title should equal Google at the start of the test.", "Google", driver.getTitle());
		WebElement searchField = SeleniumUtilities.searchByClause(driver, By.name("q"));
		SeleniumUtilities.setValueToElement(searchField, googleSearchText);
		SeleniumUtilities.submitButtonElement(searchField);
	    assertTrue("The page title should start with the search string after the search.",
	      (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
	        public Boolean apply(WebDriver d) {
	          return d.getTitle().toLowerCase().startsWith(googleSearchText.toLowerCase());
	        }
	      })
	    );
	}

}
