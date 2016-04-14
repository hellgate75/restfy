package com.restify.tests.java.server.plugin;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class JavaServerPluginTest  {
	private static int port = 8080;
	private static final String testResult = "Test";

	@Test(timeout=5000)
	public void test0PluginMustBeCreated() throws Exception {
		HTTPResponse restResponse = connectGETToRestService("http://localhost:"+port+"/simple/entry-point/test");
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
