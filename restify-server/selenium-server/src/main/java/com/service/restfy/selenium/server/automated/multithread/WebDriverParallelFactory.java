package com.service.restfy.selenium.server.automated.multithread;

import com.service.restfy.selenium.server.automated.WebDriverSelector;
import com.service.restfy.selenium.server.exceptions.FrameworkException;

public interface WebDriverParallelFactory {
	WebDriverSelector nextWebDriver() throws FrameworkException;
}
