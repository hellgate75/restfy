package com.service.restfy.java.server;

import java.io.File;

import com.service.restfy.java.server.test.EntryPoint;

public class Main {

	public static void main(String[] args) throws Throwable{
		RestfyJavaServer jettyServer = new RestfyJavaServer(8080);
		String warAbsolutePath = new File("../java-server-war-test/target/java-server-war-test-0.0.1-SNAPSHOT.war").getAbsolutePath();

		//jettyServer.addClassHolder("/simple/*", EntryPoint.class.getCanonicalName(), null);
		jettyServer.setWar("/java-server-war-test", warAbsolutePath);

		try {
			jettyServer.start();
			jettyServer.join();
		} finally {
			jettyServer.stop();
		}	
	}

}
