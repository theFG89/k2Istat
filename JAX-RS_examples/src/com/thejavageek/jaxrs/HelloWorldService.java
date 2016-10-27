package com.thejavageek.jaxrs;


import java.awt.Event;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

@Path("/HelloWorld")
public class HelloWorldService {

	//private Student st = new Student("Fabio","Giammarioli",27);


	@GET
	@Path("/sayHello")
	public String sayHello() {
		return "<h1>Hel World</h1>";
	}

	@GET
	@Produces("text/html")
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
	@Path("/connectDB")
//	@Consumes (MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)		
	public Response  JsonToJson(User s){

//		String username = s.getUsername();
//		String password = s.getPassword();
//		String email = s.getEmail();

		///*******		CONNESSIONE DATABASE    	*****//////


		EntityManagerFactory  emf = entityManagerUtils.getInstance();

		EntityManager em = emf.createEntityManager();
		
		
		em.getTransaction().begin();
		List<User> result = em.createQuery( " from users", User.class ).getResultList();
		
		for ( User event : result ) {
		 System.out.println( "Event (" + event.getId() + ") : "+" "+ event.getUsername() +" "+  event.getPassword()+" "+ event.getEmail() );
		}
		em.getTransaction().commit();
		em.close();


		//	connect();

		//			System.out.println(s.getName());
		//			s.setAge(s.getAge()+10);
		//		Gson gson = new Gson();
		//			String result = gson.toJson(s);
		return Response.status(200).entity(s).build();

	}


	public void connectToDB(){

	}





	@GET
	@Produces("text/html") 
	@Path("/convertToMD5")
	public String convertToMD5(){
		String convString="stringa";
		convertMD5 MD5Util = new convertMD5(); 

		return "Normale: "+convString+" --> MD5: "+MD5Util.encrypt(convString);

	}






}