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
@Mojo(defaultPhase=LifecyclePhase.TEST, name="disconnect")
public class DisposeJavaServerPlugin extends AbstractMojo {

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
     * Port number for the server loopback execution.
     */
	@Parameter( defaultValue = "150001", required=true )
    private int loopback;


    public DisposeJavaServerPlugin() {
	}
    

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			boolean stopped = RestfyJavaServer.stopRemoteServer(hostname, loopback);
			getLog().info("Server stopped by signal : " + stopped);
		} catch (Throwable e) {
		}
	}
	

}
