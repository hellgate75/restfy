package com.service.restfy.selenium.server.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.service.restfy.selenium.server.exceptions.ActionException;
import com.service.restfy.selenium.server.exceptions.FrameworkException;
import com.service.restfy.selenium.server.exceptions.NotFoundException;

public class SeleniumUtilities {
	static {
		if (System.getProperty("log4j.configurationFile")==null)
			System.setProperty("log4j.configurationFile", "log4j2.xml");
	}
	private static Logger logger = LoggerFactory.getLogger("com.service.restfy.selenium.server");
	private static final SeleniumHelper seleniumHelper = new SeleniumHelper();

	public static enum BROWSER_TYPE {IE, CROME, FIREFOX, OPERA, HTML_UNIT, REMOTE, EVENT_FIRING};
	
	
	public static final WebDriver getBrowserDriver(BROWSER_TYPE type) {
		if (type!=BROWSER_TYPE.REMOTE && type!=BROWSER_TYPE.EVENT_FIRING)
			return getBrowserDriver(type, null, null, null, null);
		return null;
	}
	
	public static final WebDriver getBrowserDriver(BROWSER_TYPE type, WebDriver firingDriver, CommandExecutor exec, URL remoteURL, Capabilities capabilities) {
		WebDriver webDriver = null;
		switch(type) {
			case IE:
				webDriver = new InternetExplorerDriver();
				break;
			case FIREFOX:
				webDriver = new FirefoxDriver();
				break;
			case OPERA:
				webDriver = new OperaDriver();
				break;
			case HTML_UNIT:
				webDriver = new HtmlUnitDriver();
				break;
			case REMOTE:
				if (remoteURL==null)
					webDriver = new RemoteWebDriver(exec, capabilities);
				else
					webDriver = new RemoteWebDriver(remoteURL, capabilities);
				break;
			case EVENT_FIRING:
				webDriver = new EventFiringWebDriver(firingDriver);
				break;
			default:
				webDriver = new ChromeDriver();
		}
		return webDriver;
	}

	public static final void closeBrowserDriver(WebDriver webDriver) {
		if (webDriver!=null)
			webDriver.close();
	}

	public static final void saveScreenShot(WebDriver webDriver, String screenshotFileName) throws FrameworkException{
		try {
			seleniumHelper.saveScreenshot(screenshotFileName, webDriver);
		} catch (Throwable e) {
		    logger.error("error Taking the scrinshot in file ='" + screenshotFileName + "'", e);
			throw new FrameworkException("Unable to take scrinshot ans save it to file : " + screenshotFileName + " - exception : " ,e);
		}
	}

	public static final void submitButtonElement(WebElement elem) throws ActionException {
		try {
			elem.submit();
		} catch (Throwable e) {
		    logger.error("Error running 'submit' action on the element by id='" + (elem!=null ? elem.getAttribute("id") : null) + "'", e);
			throw new ActionException("Unable to apply 'submit' action to element by id='" + (elem!=null ? elem.getAttribute("id") : null) + "' exception : ", e);
		}
	}
	
	public static final void clickButtonElement(WebElement elem) throws ActionException {
		try {
			elem.click();
		} catch (Throwable e) {
		    logger.error("Error running 'click' action on the element by id='" + (elem!=null ? elem.getAttribute("id") : null) + "'", e);
			throw new ActionException("Unable to apply 'click' action to element by id='" + (elem!=null ? elem.getAttribute("id") : null) + "' exception : ", e);
		}
	}
	
	public static final void setValueToElement(WebElement elem, String value) throws ActionException {
		try {
			elem.sendKeys(value);
		} catch (Throwable e) {
		    logger.error("Error running 'setValue' action on the element by id='" + (elem!=null ? elem.getAttribute("id") : null) + "'", e);
			throw new ActionException("Unable to apply 'setValue' action to element by id='" + (elem!=null ? elem.getAttribute("id") : null) + "' exception : ", e);
		}
	}
	
	public static final void clearValueToElement(WebElement elem) throws ActionException {
		try {
			elem.clear();
		} catch (Throwable e) {
		    logger.error("Error running 'clearValue' action on the element by id='" + (elem!=null ? elem.getAttribute("id") : null) + "'", e);
			throw new ActionException("Unable to apply 'clearValue' action to element by id='" + (elem!=null ? elem.getAttribute("id") : null) + "' exception : ", e);
		}
	}
	
	public static final WebElement findWithinElement(WebElement elem, By clause) throws NotFoundException, FrameworkException {
		try {
			WebElement subElem = elem.findElement(clause);
			if (subElem==null)
				throw new NotFoundException("Unable to locate element by clause='"+clause+"' on the user interface into the element by id='" + (elem!=null ? elem.getAttribute("id") : null) + "'");
			return subElem;
		} catch (Throwable e) {
		    logger.error("Error finding element by clause : " + clause +" within the element by id='" + (elem!=null ? elem.getAttribute("id") : null) + "'", e);
			throw new FrameworkException("Unable to find by clause '"+clause+"' into the element by id='" + (elem!=null ? elem.getAttribute("id") : null) + "' exception : ", e);
		}
	}
	
	public static final List<WebElement> findAllWithinElement(WebElement elem, By clause) throws NotFoundException, FrameworkException {
		try {
			List<WebElement> subElems = elem.findElements(clause);
			if (subElems==null || subElems.size()==0)
				throw new NotFoundException("Unable to locate element by clause='"+clause+"' on the user interface into the element by id='" + (elem!=null ? elem.getAttribute("id") : null) + "'");
			return subElems;
		} catch (Throwable e) {
		    logger.error("Error finding elements by clause : " + clause +" within the element by id='" + (elem!=null ? elem.getAttribute("id") : null) + "'", e);
			throw new FrameworkException("Unable to find by clause '"+clause+"' into the element by id='" + (elem!=null ? elem.getAttribute("id") : null) + "' exception : ", e);
		}
	}
	
	public static final WebElement searchByClause(WebDriver webDriver, By clause) throws NotFoundException, FrameworkException {
		try {
			WebElement elem = webDriver.findElement(clause);
			if (elem==null)
				throw new NotFoundException("Unable to locate element by clause='"+clause+"' on the user interface");
			return elem;
		} catch (Throwable e) {
		    logger.error("Error finding element by clause : " + clause, e);
			throw new FrameworkException("Unable to find element by clause='" + clause + "' exception : ", e);
		}
	}
	
	public static final List<WebElement> searchAllByClause(WebDriver webDriver, By clause) throws NotFoundException, FrameworkException {
		try {
			List<WebElement> elems = webDriver.findElements(clause);
			if (elems==null || elems.size()==0)
				throw new NotFoundException("Unable to locate element by clause='"+clause+"' on the user interface");
			return elems;
		} catch (Throwable e) {
		    logger.error("Error finding elements by clause : " + clause, e);
			throw new FrameworkException("Unable to find element by clause='" + clause + "' exception : ", e);
		}
	}
	
	private static class SeleniumHelper {
		  
	    public void saveScreenshot(String screenshotFileName,  WebDriver driver) throws IOException {
	      File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
	      FileUtils.copyFile(screenshot, new File(screenshotFileName));
	      logger.debug("Taken screenshot to file : " + screenshotFileName);
	    }
	  }
}
