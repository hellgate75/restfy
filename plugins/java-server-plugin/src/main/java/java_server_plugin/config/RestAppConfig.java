package java_server_plugin.config;

import java.io.File;
import java.util.Map;

public class RestAppConfig {
	public enum TYPE {WAR, JAR, CLASSLIST};
	public TYPE type;
	public String context;
	public File file;
	public String classNames;
	public Map<String, String> jerseyProperties;
	public RestAppConfig() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TYPE getType() {
		return type;
	}
	public void setType(TYPE type) {
		this.type = type;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public Map<String, String> getJerseyProperties() {
		return jerseyProperties;
	}
	public void setJerseyProperties(Map<String, String> jerseyProperties) {
		this.jerseyProperties = jerseyProperties;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getClassNames() {
		return classNames;
	}
	public void setClassNames(String classNames) {
		this.classNames = classNames;
	}
	
}
