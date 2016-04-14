package com.service.restfy.java.server;

import java.io.File;
import java.util.Scanner;

import com.service.restfy.java.server.test.EntryPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	static {
		if (System.getProperty("log4j.configurationFile")==null)
			System.setProperty("log4j.configurationFile", "log4j2.xml");
	}
	private static Logger logger = LoggerFactory.getLogger("com.service.restfy.java.server");

	public static void main(String[] args) throws Throwable{
		logger.info("****************************************************************");
		logger.info("Jetty 2 Server Bootstrap in progress");
		logger.info("****************************************************************");
		String hostname = "localhost";
		int port = 8080;
		String context = "/";
		boolean stopOnShutdown = true;
		DeployType deployType = DeployType.CLASS_DEPLOY;
		String deployReference = "";
		String deployClassNames = EntryPoint.class.getCanonicalName();
		String deployContext = "/simple/";
		String loopBackport = null;
		for(int i=0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equalsIgnoreCase("--hostname")) {
				try {
					hostname = args[i+1];
				} catch (Throwable e) {
				}
			}
			else if (arg.equalsIgnoreCase("--port")) {
				try {
					port = Integer.parseInt(args[i+1]);
				} catch (Throwable e) {
				}
			}
			else if (arg.equalsIgnoreCase("--stopOnShutdown")) {
				try {
					stopOnShutdown = Boolean.parseBoolean(args[i+1]);
				} catch (Throwable e) {
				}
			}
			else if (arg.equalsIgnoreCase("--deployType")) {
				try {
					deployType = DeployType.valueOf(args[i+1]);
				} catch (Throwable e) {
				}
			}
			else if (arg.equalsIgnoreCase("--deployReference")) {
				try {
					deployReference = args[i+1];
				} catch (Throwable e) {
				}
			}
			else if (arg.equalsIgnoreCase("--deployContext")) {
				try {
					deployContext = args[i+1];
				} catch (Throwable e) {
				}
			}
			else if (arg.equalsIgnoreCase("--deployClassNames")) {
				try {
					deployClassNames = args[i+1];
				} catch (Throwable e) {
				}
			}
			else if (arg.equalsIgnoreCase("--loopBackPort")) {
				try {
					loopBackport = args[i+1];
				} catch (Throwable e) {
				}
			}
		}
		logger.info("Configuration : ");
		logger.info("hostname : " + hostname);
		logger.info("port : " + port);
		logger.info("server context : " + context);
		logger.info("server required loopback port : " + loopBackport);
		logger.info("server stopOnShutdown : " + stopOnShutdown);
		logger.info("deploy type : " + deployType);
		
		logger.info("deploy context : " + deployContext);
		logger.info("deploy file : " + deployReference);
		logger.info("deploy class names : " + deployClassNames);
		logger.info("****************************************************************");
		
		RestfyJavaServer jettyServer = new RestfyJavaServer(stopOnShutdown,context, hostname , 8080);
		if (null != loopBackport) {
			try {
				jettyServer.setLoopbackPort(Integer.parseInt(loopBackport));
			} catch (Throwable e) {
			}
		}
		if (deployType==DeployType.CLASS_DEPLOY) {
			jettyServer.addClassHolder(deployContext, deployClassNames, null);
		}
		else if (deployType==DeployType.JAR_DEPLOY) {
			jettyServer.addJar(deployContext, deployReference, deployClassNames, null);
		}
		else if (deployType==DeployType.WAR_DEPLOY) {
			jettyServer.setWar(deployContext, deployReference);
		}
		try {
			jettyServer.start();
			logger.info("Jetty 2 Server Bootstrap started server");
			logger.info("****************************************************************");
			logger.info("Press Q and ENTER to quit");
			Scanner sc = new Scanner(System.in);
		    while (sc.hasNext()) {
		    	String next = sc.next();
		        if (next!=null && next.length()>0 && (next.charAt(0) == 'Q'||next.charAt(0) == 'q'))
		        	break;
		    }
		    sc.close();
			logger.info("Server closed : "  + RestfyJavaServer.stopRemoteServer(hostname, jettyServer.getLoopbackPort()));
//			jettyServer.join();
		} finally {
			jettyServer.stop();
		}
		logger.info("****************************************************************");
		logger.info("Jetty 2 Server Bootstrap exit");
		logger.info("****************************************************************");
	}

}
