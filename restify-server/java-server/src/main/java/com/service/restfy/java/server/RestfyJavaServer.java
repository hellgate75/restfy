package com.service.restfy.java.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.server.ServerProperties;

public class RestfyJavaServer implements Runnable {
	static {
		if (System.getProperty("log4j.configurationFile")==null)
			System.setProperty("log4j.configurationFile", "log4j2.xml");
	}
	protected static final String PROVIDER_CLASSNAMES = "jersey.config.server.provider.classnames";

	private static Logger logger = LoggerFactory.getLogger("com.service.restfy.java.server");
	
	private ServletContextHandler context = null;
	private WebAppContext webAppContext = null;
	private Server jettyServer = null;
	private Map<String, String> holderMap = new HashMap<String,String>(0);
	private boolean doStopAsShutdown = false;
	private String defaultcontext = null;
	private String host = null;
	private int port = 0;
	private int loopbackPort = 0;
	private ServerSocket loopback = null;
	private Thread loopbackThread = null;
	private boolean running = false;

	public RestfyJavaServer(int port) throws URISyntaxException {
		this(true, "/", "localhost", port);
	}

	public RestfyJavaServer(boolean doStopAsShutdown, int port) throws  URISyntaxException {
		this(doStopAsShutdown, "/", "localhost", port);
	}

	public RestfyJavaServer(boolean doStopAsShutdown, String defaultcontext, int port) throws  URISyntaxException {
		this(doStopAsShutdown, defaultcontext, "localhost", port);
	}
	
	public RestfyJavaServer(boolean doStopAsShutdown, String defaultcontext, String host, int port) throws  URISyntaxException {
		super();
		logger.info("Jetty 2 Server loading ... ");
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
		logger.info("Jetty 2 Server intialization ... ");
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
	
	protected static final int START_LOOPBACK=15000;
	
	protected ServerSocket checkLoopbackPort() {
		ServerSocket socket = null;
		int cc=0;
		while (socket == null) {
			try {
				socket = new ServerSocket(START_LOOPBACK + cc);
				loopbackPort = START_LOOPBACK + cc;
				logger.info("Jetty 2 Server found loopback port : " + loopbackPort);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			cc++;
			if (cc>1000) {
				break;
			}
		}
		return socket;
	}
	
	protected ServerSocket openLoopbackPort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(loopbackPort);
			logger.info("Jetty 2 Server started loopback socket ... ");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return socket;
	}
	
	@Override
	public void run() {
		logger.info("Jetty 2 Server loopback running ... ");
		while(running) {
			Socket client = null;
			try {
				loopback.setSoTimeout(5000);
				client = loopback.accept();
				BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
				while (br.ready()) {
					String message = br.readLine();
					if (message.equalsIgnoreCase("close")) {
						this.stopInternal();
						PrintStream ps = new PrintStream(client.getOutputStream());
						ps.println("closed");
						logger.debug("Jetty 2 Closing server ....");
						this.stopLoobback();
					}
				}
			}
			catch (SocketTimeoutException e) {
				
			}
			catch (SocketException e) {
				
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			finally {
				if (client!=null) {
					try {
						client.close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
				}
			}
		}
		logger.info("Jetty 2 Server loopback closing ... ");
		
	}

	protected void addAdminService() throws SocketException {
		if (loopbackPort == 0)
			loopback = checkLoopbackPort();
		else 
			loopback = openLoopbackPort();
		if (loopback==null)
			throw new SocketException("Unable to start loop back port");
	}
	
	public void start() throws Exception {
		logger.info("Jetty 2 Server starting ... ");
		addAdminService();
		jettyServer.start();
		loopbackThread = new Thread(this);
		running = true;
		loopbackThread.start();
	}

	public void join() throws InterruptedException {
		logger.info("Jetty 2 Server joining ... ");
		jettyServer.join();
	}
	

	public void setLoopbackPort(int loopbackPort) {
		this.loopbackPort = loopbackPort;
	}

	public int getLoopbackPort() {
		return loopbackPort;
	}

	protected void stopLoobback() {
		logger.info("Jetty 2 Server stop loopback port ... ");
		if (loopbackThread!=null) {
			try {
				loopbackThread.interrupt();
			} catch (Exception e) {
			}
		}
		if (loopback!=null) {
			try {
				loopback.close();
			} catch (Exception e) {
			}
		}
		loopbackThread = null;
		loopback = null;
		loopbackPort = 0;
	}
	
	protected void stopInternal() throws Exception {
		logger.info("Jetty 2 Server stop server internal ... ");
		running = false;
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
	
	public void stop() throws Exception {
		logger.info("Jetty 2 Server stop server ... ");
		running = false;
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
		stopLoobback();
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
	
	public static boolean stopRemoteServer(String hostname, int loopbackPort) {
		Socket connector = null;
		try {
			connector = new Socket(hostname, loopbackPort);
			PrintStream ps = new PrintStream(connector.getOutputStream());
			ps.println("close");
			ps.flush();
			Thread.sleep(200);
			BufferedReader br = new BufferedReader(new InputStreamReader(connector.getInputStream()));
			while (br.ready()) {
				String line = br.readLine();
				if (line.equalsIgnoreCase("closed"))
					return true;
			}
		} catch (Throwable e) {
			//e.printStackTrace();
		}
		finally {
			if (connector!=null) {
				try {
					connector.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
		}
		return false;
	}
}
