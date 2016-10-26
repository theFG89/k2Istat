
package com.thejavageek.jaxrs;
 
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

 
@ApplicationPath("/rest")
public class ExampleApplication extends Application {
 
	@Override
	 public Set<Class<?>> getClasses() 
	 {



	  Set<Class<?>> resources = new HashSet<Class<?>>();

	  //register REST modules

//	  resources.add(GsonJsonProvider.class);
//	  resources.add(ServiceRest.class);
//	  resources.add(LayerRest.class);
//	  resources.add(TenantRest.class);
	  resources.add(GsonJsonProvider.class);
	  resources.add(HelloWorldService.class);
	  resources.add(convertMD5.class);
//	  resources.add(Student.class);
	  return resources;
	 }
}