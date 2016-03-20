package com.service.restfy.java.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.server.ServerProperties;

public class RestfyJavaServer {
	protected static final String PROVIDER_CLASSNAMES = "jersey.config.server.provider.classnames";
	
	private ServletContextHandler context = null;
	private WebAppContext webAppContext = null;
	private Server jettyServer = null;
	private Map<String, String> holderMap = new HashMap<String,String>(0);
	private boolean doStopAsShutdown = false;
	private String defaultcontext = null;
	private String host = null;
	private int port = 0;

	public RestfyJavaServer(int port) throws URISyntaxException {
		this(true, "/", "localhost", port);
	}
	
	public RestfyJavaServer(boolean doStopAsShutdown, String defaultcontext, String host, int port) throws  URISyntaxException {
		super();
		this.doStopAsShutdown = doStopAsShutdown;
		this.defaultcontext = defaultcontext;
		this.host = host;
		this.port = port;
		context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath(defaultcontext);
		jettyServer = new Server(new InetSocketAddress(host, port));
		jettyServer.setHandler(context);
		jettyServer.setStopAtShutdown(doStopAsShutdown);
	}
	
	private void init() throws Exception {
		if (jettyServer==null) {
			if (webAppContext==null) {
				context = new ServletContextHandler(ServletContextHandler.SESSIONS);
				context.setContextPath(defaultcontext);
			}
			jettyServer = new Server(new InetSocketAddress(host, port));
			jettyServer.setHandler(webAppContext==null ? context : webAppContext);
			jettyServer.setStopAtShutdown(doStopAsShutdown);
		}
	}
	public void start() throws Exception {
		jettyServer.start();
	}

	public void join() throws InterruptedException {
		jettyServer.join();
	}

	public void stop() throws Exception {
		jettyServer.stop();
		context.stop();
		context.destroy();
		jettyServer.destroy();
		jettyServer = null;
		context = null;
		if (webAppContext!=null) {
			webAppContext.stop();
			webAppContext.destroy();
		}
		webAppContext = null;
		holderMap.clear();
		init();
	}

	
	public boolean containsHolder(String classNamesOrWarPath) {
		return holderMap.containsValue(classNamesOrWarPath);
	}
	
	public boolean containsContext(String contextPath) {
		return holderMap.containsKey(convertContext(contextPath));
	}
	
	public boolean containsHolderInContext(String contextPath, String classNamesOrWarPath) {
		return holderMap.containsKey(convertContext(contextPath)) && holderMap.get(convertContext(contextPath)).equals(classNamesOrWarPath);
	}
	
	public boolean containsWarInContext(String contextPath, String classNamesOrWarPath) {
		return holderMap.containsKey(contextPath) && holderMap.get(contextPath).equals(classNamesOrWarPath);
	}
	
	public void addClassHolder(String contextPath, String classNames, Map<String, String> serviceProperties) {
		ServletHolder jerseyServlet = context.addServlet(
				org.glassfish.jersey.servlet.ServletContainer.class, convertContext(contextPath));
		jerseyServlet.setInitOrder(holderMap.size());
		jerseyServlet.setInitParameter(
				ServerProperties.PROVIDER_CLASSNAMES,
				classNames);
		if (serviceProperties!=null) {
			for(String key: serviceProperties.keySet()) {
				jerseyServlet.setInitParameter(key, serviceProperties.get(key));
			}
		}
		holderMap.put(convertContext(contextPath), classNames);
	}

	public void addJar(String contextPath, String jarFilePath, String classNames, Map<String, String> serviceProperties) throws ClassNotFoundException, IOException {
		this.addJar(convertContext(contextPath), new File(jarFilePath), classNames, serviceProperties);
	}

	public void addJar(String contextPath, File jarFile, String classNames, Map<String, String> serviceProperties) throws ClassNotFoundException, IOException {
		for(String name: classNames.split(",")) {
			if (name.trim().length()>0)
				new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, ClassLoader.getSystemClassLoader()).loadClass(name.trim());
		}
		addURL(jarFile.toURI().toURL());
		ServletHolder jerseyServlet = context.addServlet(
				org.glassfish.jersey.servlet.ServletContainer.class, convertContext(contextPath));
		jerseyServlet.setInitOrder(holderMap.size());
		jerseyServlet.setInitParameter(
				ServerProperties.PROVIDER_CLASSNAMES,
				classNames);
		if (serviceProperties!=null) {
			for(String key: serviceProperties.keySet()) {
				jerseyServlet.setInitParameter(key, serviceProperties.get(key));
			}
		}
		holderMap.put(convertContext(contextPath), classNames);
	}
	
	public void setWar(String contextPath, String warFileAbsolutePath) {
		 webAppContext = new WebAppContext();
		 webAppContext.setContextPath(contextPath);
		 webAppContext.setWar(warFileAbsolutePath);
		 jettyServer.setHandler(webAppContext);
		 holderMap.put(contextPath, warFileAbsolutePath);
	}

	public void setWar(String contextPath, File warFile) {
		 WebAppContext webapp = new WebAppContext();
		    webapp.setContextPath(convertContext(contextPath));
		    webapp.setWar(warFile.getAbsolutePath());
		    context.insertHandler(webapp.getSessionHandler());
			holderMap.put(convertContext(contextPath), warFile.getAbsolutePath());
	}

	@Override
	protected void finalize() throws Throwable {
		if (jettyServer!=null) {
			this.stop();
		}
		super.finalize();
	}
	
	private static final String convertContext(String context) {
		return context!=null ? (context.indexOf("/*")<0 ? context+"/*" : context) : null;
	}

	private static synchronized void addURL(URL u) throws IOException
    {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> sysclass = URLClassLoader.class;

        try {
        	
            java.lang.reflect.Method method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] {u});
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }

    }

}
