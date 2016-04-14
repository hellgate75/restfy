package com.service.restfy.java.server.amin;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.service.restfy.java.server.RestfyJavaServer;

@Path("/console")
public class RestAdminService {
	RestfyJavaServer server;
	@Context Configuration configuration;
	@Inject Application application;
	
	@PostConstruct
	public void init() {
		System.out.println("configuration = "+(configuration!=null));
		System.out.println("application = "+(application!=null));
		for(String k:application.getProperties().keySet())
			System.out.println(k+"="+application.getProperties().get(k)+" ["+application.getProperties().get(k).getClass().getName()+"]");
	}
	
    @GET
    @Path("services")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return ""+(server!=null);
    }

}
