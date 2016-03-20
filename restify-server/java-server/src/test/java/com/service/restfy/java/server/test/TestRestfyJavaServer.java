package com.service.restfy.java.server.test;


import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.service.restfy.java.server.RestfyJavaServer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRestfyJavaServer {
	private static final Logger logger = LoggerFactory.getLogger("com.service.restfy.java.server.test");
	private static RestfyJavaServer jettyServer = null;
	private static int port = 8080;
	private static final String warAbsolutePath = new File("../java-server-war-test/target/java-server-war-test-0.0.1-SNAPSHOT.war").getAbsolutePath();
	private static final String testResult = "Test";
	
	@BeforeClass
	public static final void init() throws Throwable {
		logger.info("Initialization of tests for TestRestfyJavaServer ..." );
		jettyServer = new RestfyJavaServer(port);
		jettyServer.addClassHolder("/simple", EntryPoint.class.getCanonicalName(), null);
		jettyServer.addJar("/from-jar", "../java-server-jar-test/target/java-server-jar-test-0.0.1-SNAPSHOT.jar", "com.service.restfy.java.server.jartest.services.EntryPoint", null);
		jettyServer.start();
	}

	@Test(timeout=4000)
	public void test0InstallSimpleHolder() throws Throwable {
		logger.info("A simple Rest Service Holder should be installed ..." );
		assertEquals( jettyServer.containsHolderInContext("/simple", EntryPoint.class.getCanonicalName()), true );
	}

	@Test
	public void test1TestInstalledSimpleHolder() throws Throwable {
		logger.info("A simple Rest Service Holder should be reachable ..." );
		HTTPResponse restResponse = connectGETToRestService("http://localhost:"+port+"/simple/entry-point/test");
		assertEquals(200, restResponse.getCode());
		assertEquals(testResult, restResponse.getResponse());
	}

	@Test
	public void test3InstallJARHolder() throws Throwable {
		logger.info("A JAR Rest Service Holder should be installed ..." );
		assertEquals( jettyServer.containsHolderInContext("/from-jar", "com.service.restfy.java.server.jartest.services.EntryPoint"), true );
	}

	@Test
	public void test4TestInstalledJarHolder() throws Throwable {
		logger.info("A JAR Rest Service Holder should be reachable ..." );
		HTTPResponse restResponse = connectGETToRestService("http://localhost:"+port+"/from-jar/entry-jar-point/test");
		assertEquals(200, restResponse.getCode());
		assertEquals(testResult, restResponse.getResponse());
	}

	@Test(timeout=5000)
	public void test5InstallWARHolder() throws Throwable {
		/* Servlet and HTTPWebApplication context doesn't live together
		 * So we first stop and destroy the server than we deploy the war and restart we the server
		 * In this implementation each war must have a different server but all XAS-RS classes can be added in multiple
		 * instances as classes (Holder) or jars containng holders.
		*/
		jettyServer.stop();
		jettyServer.setWar("/java-server-war-test", warAbsolutePath);
		jettyServer.start();
		logger.info("A WAR Rest Service Holder should be installed ..." );
		assertEquals( jettyServer.containsWarInContext("/java-server-war-test", warAbsolutePath), true );
	}

	@Test
	public void test6TestInstalledWARHolder() throws Throwable {
		logger.info("A WAR Rest Service Holder should be reachable ..." );
		HTTPResponse restResponse = connectGETToRestService("http://localhost:"+port+"/java-server-war-test/rest/test");
		assertEquals(200, restResponse.getCode());
		assertEquals(testResult, restResponse.getResponse());
	}

	protected final HTTPResponse connectGETToRestService(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		int code = -1;
		String response = "";
		try {
			HttpGet httpGetRequest = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGetRequest);

			code = httpResponse.getStatusLine().getStatusCode();
			HttpEntity entity = httpResponse.getEntity();
			byte[] buffer = new byte[1024];
			if (entity != null) {
				InputStream inputStream = entity.getContent();
				try {
					int bytesRead = 0;
					BufferedInputStream bis = new BufferedInputStream(inputStream);
					while ((bytesRead = bis.read(buffer)) != -1) {
						response += new String(buffer, 0, bytesRead);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try { inputStream.close(); } catch (Exception ignore) {}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return new HTTPResponse(code, response);
	}
	
	protected class HTTPResponse {
		private int code = -1;
		private String response = "";
		public HTTPResponse(int code, String response) {
			super();
			this.code = code;
			this.response = response;
		}
		public int getCode() {
			return code;
		}
		public String getResponse() {
			return response;
		}
	}
}
