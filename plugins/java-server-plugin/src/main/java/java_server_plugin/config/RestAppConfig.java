package java_server_plugin.config;

import java.util.ArrayList;
import java.util.List;

public class RestAppConfig {
    private String hostname = "localhost";
    private int loopback = 15001;
    private int port = 8080;
    private List<DeployConfig> deployConfigs = new ArrayList<DeployConfig>(0);
	public RestAppConfig() {
		super();
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getLoopback() {
		return loopback;
	}
	public void setLoopback(int loopback) {
		this.loopback = loopback;
	}
	public List<DeployConfig> getDeployConfigs() {
		return deployConfigs;
	}
	public void setDeployConfigs(List<DeployConfig> deployConfig) {
		this.deployConfigs = deployConfig;
	}
	@Override
	public String toString() {
		return "RestAppConfig [hostname=" + hostname + ", port=" + port
				+ ", loopback=" + loopback + ", deployConfig=" + deployConfigs
				+ "]";
	}
	
}
