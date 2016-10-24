package com.thejavageek.jaxrs;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
 
@Path("/HelloWorld")
public class HelloWorldService {
 
	@GET
	@Path("/sayHello")
	public String sayHello() {
		return "<h1>Hel World</h1>";
	}
	
	@GET
	@Produces("text/json")
	@Path("/sayHello2")
	public String sayHello23() {
		String s[]= {"22ciao","ciao2","ciao3"};
		String res="";
		for(int i=0;i<s.length;i++){
			res=res+"<br> "+ "<h1><b>"+s[i].toString()+ "</h1></b>";
		}
		return res;
	}
	
	@GET
	@Produces("text/html") 
	@Path("/convertToMD5")
	public String convertToMD5(){
		String convString="stringa";
		convertMD5 MD5Util = new convertMD5(); 
        
		return "Normale: "+convString+" --> MD5: "+MD5Util.encrypt(convString);
//		
//		String a;
//		if (a.equals(MD5Util.toString()))
//			return "ok";
	}
	
	
 
}