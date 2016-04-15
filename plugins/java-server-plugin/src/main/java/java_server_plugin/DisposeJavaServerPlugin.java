package java_server_plugin;

import java.net.URISyntaxException;
import java.util.List;

import java_server_plugin.config.DisconnectConfig;
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
	@Parameter(required=true )
    private List<DisconnectConfig> disconnectConfigs;

    public DisposeJavaServerPlugin() {
	}
    

	public void execute() throws MojoExecutionException, MojoFailureException {
		for(DisconnectConfig config: disconnectConfigs) {
			try {
				boolean stopped = RestfyJavaServer.stopRemoteServer(config.getHostname(), config.getLoopback());
				getLog().info("Server "+config+" stopped by signal : " + stopped);
			} catch (Throwable e) {
				getLog().info("Server "+config+" stop error : ", e);
			}
		}
	}
	

}
