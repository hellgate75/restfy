package java_server_plugin;

import java.net.URISyntaxException;
import java.util.List;

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
public class TestJavaServer extends AbstractMojo {

	@Parameter( defaultValue = "${project}", readonly = true )
	private MavenProject project;

/*	@Parameter( defaultValue = "${session}", readonly = true )
	private MavenSession session;*/

	/**
     * Host name for the server execution.
     */
	@Parameter( defaultValue = "localhost", required=true )
    private String hostname;

    /**
     * Port number for the server execution.
     */
	@Parameter( defaultValue = "8089", required=true )
    private int port;

    /**
     * List of items to be installed.
     */
	@Parameter( property = "restapps", required=false )
    private List<RestAppConfig> restapps;

    private RestfyJavaServer server = null;
    private boolean isValidDeploy = false;

    public TestJavaServer() {
	}
    
    protected void initPlugin() {
    	try {
			server = new RestfyJavaServer(true, "/", hostname, port);
		} catch (URISyntaxException e) {
			getLog().error("Error during the instance of the RestFy Java Server caused by :");
			getLog().error(e);
		}
    	if (server!=null) {
    		if (restapps!=null && restapps.size()>0) {
    			if (checkWebApps()) {
        			for(RestAppConfig config: restapps) {
        				switch(config.getType()) {
	        				case WAR:
	        					try {
	    							server.setWar(config.getContext(), config.getFile());
	    						} catch (Exception e) {
	    			    			getLog().warn("WAR for context "+config.getContext()+" not installed in Restfy Java Server for following errors :");
	    			    			getLog().error(e);
	    						}
	        					if (config.getFile()==null || !server.containsWarInContext(config.getContext(), config.getFile().getAbsolutePath())) {
	    			    			getLog().warn("WAR for context "+config.getContext()+" not present inRestfy Java Server deployments");
	        					}
	        					else {
	        	        			isValidDeploy = true;
	        					}
	        					break;
	        				case JAR:
	        					try {
	    							server.addJar(config.getContext(), config.getFile(), config.getClassNames(), config.getJerseyProperties());
	    						} catch (Exception e) {
	    			    			getLog().warn("JAR for context "+config.getContext()+" not installed in Restfy Java Server for following errors :");
	    			    			getLog().error(e);
	    						}
	        					if (config.getFile()==null || !server.containsHolderInContext(config.getContext(), config.getClassNames())) {
	    			    			getLog().warn("JAR for context "+config.getContext()+" not present inRestfy Java Server deployments");
	        					}
	        					else {
	        	        			isValidDeploy = true;
	        					}
	        					break;
	        				case CLASSLIST:
	        					try {
	    							server.addClassHolder(config.getContext(), config.getClassNames(), config.getJerseyProperties());
	    						} catch (Exception e) {
	    			    			getLog().warn("CLASSES for context "+config.getContext()+" not installed in Restfy Java Server for following errors :");
	    			    			getLog().error(e);
	    						}
	        					if (!server.containsHolderInContext(config.getContext(), config.getClassNames())) {
	    			    			getLog().warn("CLASSES for context "+config.getContext()+" not present inRestfy Java Server deployments");
	        					}
	        					else {
	        	        			isValidDeploy = true;
	        					}
	        					break;
        				}
        			}
    			}
        		else {
        			getLog().warn("Wrong deployment list for Restfy Java Server WAR and JAR, CLASSLIST do not run together");
        		}
    		}
    		else {
    			getLog().warn("No Applications found for Restfy Java Server");
    		}
    	}
    }

	public void execute() throws MojoExecutionException, MojoFailureException {
		initPlugin();
		try {
			if (server != null && isValidDeploy)
				server.start();
		} catch (Exception e) {
			getLog().error("Error during the STARTUP of the RestFy Java Server caused by :");
			getLog().error(e);
		}
	}
    
	public void stopPluginExecution() {
		try {
			if (server !=null)
				server.stop();
		} catch (Exception e) {
			getLog().error("Error during the SHUTDOWN of the RestFy Java Server caused by :");
			getLog().error(e);
		}
	}
	
    private final boolean checkWebApps() {
		boolean containsWar = false;
		boolean containsJar = false;
		boolean containsClasses = false;
		for(RestAppConfig config: restapps) {
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
