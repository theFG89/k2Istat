package com.thejavageek.jaxrs;



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

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

	@POST
	@Path("/connectDB")
	@Consumes (MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON)
	//@Produces (MediaType.TEXT_PLAIN)
	public Response  JsonToJson(User s){

		//		String username = s.getUsername();
		//		String password = s.getPassword();
		//		String email = s.getEmail();


		System.out.println("prima di inizializzazione entitymanager OK");
		EntityManagerFactory  emf = entityManagerUtils.getInstance();

		EntityManager em = emf.createEntityManager();

		System.out.println("dopo di inizializzazione entitymanager OK");
		em.getTransaction().begin();

		List<User> result = em.createQuery( "from User", User.class ).getResultList();

		for ( User event : result ) {
			System.out.println( "Event (" + event.getId() + ") : "+" "+ event.getUsername() +" "+  event.getPassword()+" "+ event.getEmail() );
		}

		em.getTransaction().commit();
		em.close();

		System.out.println("dopo stammpa elementi OK");

		//	connect();

		//			System.out.println(s.getName());
		s.setEmail(s.getEmail().toString()+" LAST");
		Gson gson = new Gson();
		String resultJ = gson.toJson(s);
		return Response.status(200).entity(resultJ).build();

	}


	@GET
	@Path("/getusers")
	@Produces (MediaType.APPLICATION_JSON)
	public Response  getUser(){

		System.out.println("prima di inizializzazione entitymanager OK");
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		System.out.println("dopo di inizializzazione entitymanager OK");
		em.getTransaction().begin();
		List<User> result = em.createQuery( "from User", User.class ).getResultList();	
		em.getTransaction().commit();
		em.close();
		System.out.println("dopo stammpa elementi OK");
		String resultJ = new Gson().toJson(result);
		return Response.status(200).entity(resultJ).build();

	}

	@GET			////////////////DA COMPLETARE  /////////////////
	@Path("/searchUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public ResponseBuilder checkUser(User u){	
		return Response.status(200);
	}/////////////////////////////////////////////////////////////////


	@POST
	@Path("/newUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public String registerUser(User u){
		User check;
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		System.out.println("dopo di inizializzazione entitymanager OK");
		em.getTransaction().begin();
		System.out.println(u.getId()+" "+u.getUsername()+" "+ u.getPassword()+" "+u.getEmail());
		check = em.find(User.class, u.getId());
		if(check!=null)
			return "Utente già registrato";
		em.persist(u);
		em.getTransaction().commit();
		em.close();		
		return "Nuovo Utente inserito";
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