package com.mainPackage;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.objectUsed.RequestClient;

@Path("/data")
public class DataServices {

	////////*******		  GET VALUES OF NATION	*********
	@Path("/getValueNation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response dataNation(RequestClient RC ){
		String search = RC.getCodeNation();


		return Response.status(200).build();
	}
}


