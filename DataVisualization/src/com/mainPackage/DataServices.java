package com.mainPackage;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.alzHBase.Area;
import com.alzHBase.GetDataMapArea;
import com.objectUsed.RequestClient;
import com.objectUsed.RequestClientCode;

@Path("/data")
public class DataServices {

	////////*******		  GET VALUES OF NATION	*********
	
	@POST
	@Path("/getValueNation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response dataNation(RequestClientCode RC ){
		String search = RC.getCode();
		Map<String,Area> data = new LinkedHashMap<String,Area>();
		data = GetDataMapArea.getRegion(search);
		return Response.status(200).entity(data).build();
	}
}


