package com.service.restfy.selenium.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.service.restfy.selenium.server.automated.SeleniumAutomatedServer;

public class Main {
	static {
		if (System.getProperty("log4j.configurationFile")==null)
			System.setProperty("log4j.configurationFile", "log4j2.xml");
	}
	private static Logger logger = LoggerFactory.getLogger("com.service.restfy.selenium.server");

	public static void main(String[] args) throws Throwable{
		logger.info("****************************************************************");
		logger.info("Selenium 2 Automated Test Server Bootstrap in progress");
		logger.info("****************************************************************");
		String configFile = "src/test/resources/test.properies";
		for(int i=0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equalsIgnoreCase("--configFile")) {
				try {
					configFile = args[i+1];
				} catch (Throwable e) {
				}
			}
		}
		logger.info("Configuration : ");
		logger.info("config : " + configFile);
		logger.info("****************************************************************");
		
		SeleniumAutomatedServer seleniumServer = new SeleniumAutomatedServer();
		
		try {
			if (configFile.toLowerCase().indexOf(".xml")<0)
				seleniumServer.readConfig(configFile);
			else
				seleniumServer.readConfigXml(configFile);
			logger.info("Selenium 2 Automated Test Server Bootstrap started server");
			logger.info("****************************************************************");
			seleniumServer.startTests();
		} finally {
		}
		logger.info("****************************************************************");
		logger.info("Selenium 2 Automated Test Server Bootstrap exit");
		logger.info("****************************************************************");
	}

}
