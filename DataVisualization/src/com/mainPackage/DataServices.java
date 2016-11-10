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
import com.objectUsed.ResponseInfo;

@Path("/data")
public class DataServices {

	////////*******		  GET VALUES OF NATION	*********
	
	@POST
	@Path("/getValueArea")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response dataNation(RequestClientCode RC ){
		String search = RC.getCode();
		if (search.length()!=0){
		Map<String,Area> data = new LinkedHashMap<String,Area>();
		data = GetDataMapArea.getArea(search);
		return Response.status(200).entity(data).build();
		}
		else{
			ResponseInfo response = new ResponseInfo();
			response.setCode(400);
			response.setSuccess(false);
			response.setDescription("Richiesta errata");
			return Response.status(200).entity(response).build();
			
		 }
		}
}


