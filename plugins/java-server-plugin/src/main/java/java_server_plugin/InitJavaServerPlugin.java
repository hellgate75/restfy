package java_server_plugin;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import java_server_plugin.config.DeployConfig;
import java_server_plugin.config.RestAppConfig;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.service.restfy.java.server.RestfyJavaServer;

/**
 * Goal which connect a Restify Java Server.
 */
@Mojo(defaultPhase=LifecyclePhase.PROCESS_TEST_CLASSES, name="connect")
public class InitJavaServerPlugin extends AbstractMojo {

	@Parameter( defaultValue = "${project}", readonly = true )
	private MavenProject project;

/*	@Parameter( defaultValue = "${session}", readonly = true )
	private MavenSession session;*/

    /**
     * Port number for the server execution.
     */
	@Parameter( defaultValue = "0", required=false )
    private long startupTimeout;
	
    /**
     * List of items to be installed.
     */
	@Parameter( property = "restapps", required=false )
    private List<RestAppConfig> restapps;

    private boolean isValidDeploy = false;

    public InitJavaServerPlugin() {
	}
    
    protected List<RestfyJavaServer> initPlugin() {
    	List<RestfyJavaServer> servers = new ArrayList<RestfyJavaServer>(0);
		if (restapps!=null && restapps.size()>0) {
    			for(RestAppConfig app: restapps) {
			    	RestfyJavaServer server = null;
			    	try {
						server = new RestfyJavaServer(false, "/", app.getHostname(), app.getPort());
					} catch (URISyntaxException e) {
						getLog().error("Error during the instance of the RestFy Java Server caused by :");
						getLog().error(e);
					}
			    	if (server==null)
			    		continue;
    				if (!checkWebApps(app)) {
    	    			getLog().warn("Wrong configuration for Restfy Java Server Deploy Config WAR and JAR, CLASSLIST do not run together for the application : " + app);
    					continue;

    				}
	    			for(DeployConfig config: app.getDeployConfigs()) {
	    				switch(config.getType()) {
	        				case WAR:
	        					try {
	    							server.setWar(config.getContext(), config.getFile());
	    			    			getLog().info("Loaded WAR for context "+config.getContext()+" installed from file :" + config.getFile().getCanonicalPath());
	    						} catch (Exception e) {
	    			    			getLog().error("WAR for context "+config.getContext()+" not installed in Restfy Java Server for following errors :");
	    			    			getLog().error(e);
	    						}
	        					if (config.getFile()==null || !server.containsWarInContext(config.getContext(), config.getFile().getAbsolutePath())) {
	    			    			getLog().error("WAR for context "+config.getContext()+" not present inRestfy Java Server deployments");
	        					}
	        					else {
	        	        			isValidDeploy = true;
	        					}
	        					break;
	        				case JAR:
	        					try {
	    							server.addJar(config.getContext(), config.getFile(), config.getClassNames(), config.getJerseyProperties());
	    			    			getLog().info("Loaded JAR for context "+config.getContext()+" installed from file :" + config.getFile().getCanonicalPath());
	    			    			getLog().info("JAR exposed classes "+config.getClassNames()+" properties :" + config.getJerseyProperties());
	    						} catch (Exception e) {
	    			    			getLog().error("JAR for context "+config.getContext()+" not installed in Restfy Java Server for following errors :");
	    			    			getLog().error(e);
	    						}
	        					if (config.getFile()==null || !server.containsHolderInContext(config.getContext(), config.getClassNames())) {
	    			    			getLog().error("JAR for context "+config.getContext()+" not present inRestfy Java Server deployments");
	        					}
	        					else {
	        	        			isValidDeploy = true;
	        					}
	        					break;
	        				case CLASSLIST:
	        					try {
	    							server.addClassHolder(config.getContext(), config.getClassNames(), config.getJerseyProperties());
	    			    			getLog().info("Loaded CLASSES for context "+config.getContext()+" exposed classes "+config.getClassNames()+" properties :" + config.getJerseyProperties());
	    						} catch (Exception e) {
	    			    			getLog().error("CLASSES for context "+config.getContext()+" not installed in Restfy Java Server for following errors :");
	    			    			getLog().error(e);
	    						}
	        					if (!server.containsHolderInContext(config.getContext(), config.getClassNames())) {
	    			    			getLog().error("CLASSES for context "+config.getContext()+" not present inRestfy Java Server deployments");
	        					}
	        					else {
	        	        			isValidDeploy = true;
	        					}
	        					break;
	    				}
        			servers.add(server);
    			}
			}
		}
		else {
			getLog().error("No Server Applications found for Restfy Java Server Plugin");
		}
    	return servers;
    }

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Jetty 2 Maven running ....");
		Thread t = new Thread(new Runnable() {
			private List<RestfyJavaServer> servers = initPlugin();
			public void run() {
				if (servers.size()>0) {
					for(RestfyJavaServer server: servers) {
						try {
							if (server != null && isValidDeploy) {
								server.start();
							}
							else {
								getLog().error("Unable to start the RestFy Java Server!!");
							}
						} catch (Exception e) {
							getLog().error("Error during the STARTUP of the RestFy Java Server caused by :");
							getLog().error(e);
						}
					}
				}
				else {
					getLog().error("No Server Applications initialized for Restfy Java Server Plugin : check the configuration");
				}
			}
		});
		t.setDaemon(true);
		t.start();
		getLog().info("Jetty 2 Maven waiting for initalization ....");
		try {
			Thread.sleep(startupTimeout);
		} catch (InterruptedException e) {
		}
		getLog().info("Jetty 2 Maven plugin exit ....");
	}
	
    private final boolean checkWebApps(RestAppConfig app) {
		boolean containsWar = false;
		boolean containsJar = false;
		boolean containsClasses = false;
		for(DeployConfig config: app.getDeployConfigs()) {
			switch(config.getType()) {
				case WAR:
					containsWar = true;
					break;
				case JAR:
					containsJar = true;
					break;
				case CLASSLIST:
					containsClasses = true;
					break;
			}
		}
		
    	return ( containsWar && !(containsJar || containsClasses) ) || (!containsWar && (containsJar || containsClasses));
    }

}
