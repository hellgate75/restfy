package java_server_plugin.config;

import java.util.ArrayList;
import java.util.List;

public class DisconnectConfig {
    private String hostname = "localhost";
    private int loopback = 15001;
	public DisconnectConfig() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getLoopback() {
		return loopback;
	}
	public void setLoopback(int loopback) {
		this.loopback = loopback;
	}
	@Override
	public String toString() {
		return "DisconnectConfig [hostname=" + hostname + ", loopback="
				+ loopback + "]";
	}
	
}
