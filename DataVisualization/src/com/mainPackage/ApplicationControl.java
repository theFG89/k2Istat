package com.mainPackage;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

	@ApplicationPath("/www")
	public class ApplicationControl extends Application {
	 
		@Override
		 public Set<Class<?>> getClasses() 
		 {



		  Set<Class<?>> resources = new HashSet<Class<?>>();

		  //register REST modules

		  resources.add(AccessServices.class);
		  resources.add(com.utils.entityManagerUtils.class);
		  resources.add(DataServices.class);
		  return resources;
		 }
	}

