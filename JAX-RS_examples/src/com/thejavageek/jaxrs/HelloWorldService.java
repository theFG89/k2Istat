package com.thejavageek.jaxrs;



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


import com.google.gson.Gson;

@Path("/HelloWorld")
public class HelloWorldService {

	//private Student st = new Student("Fabio","Giammarioli",27);

	@GET
	@Path("/getusers")
	@Produces (MediaType.APPLICATION_JSON)
	public Response  getUser(){

		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		List<User> result = em.createQuery( "from User", User.class ).getResultList();	
		em.getTransaction().commit();
		em.close();
		String resultJ = new Gson().toJson(result);
		return Response.status(200).entity(resultJ).build();
	}


	@POST
	@Path("/newUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseRequest registerUser(User u){
		ResponseRequest ResponseQuery = new ResponseRequest(); 
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		if ((u.getUsername().length()<2) || (u.getPassword().length()<2) || (u.getEmail().length()<2) ){
			ResponseQuery.setDescription("Uno o più campi vuoti");
			return ResponseQuery;
		}


		em.getTransaction().begin();
		List<User> result = em.createQuery( " from User", User.class ).getResultList();
		for (int i=0;i<result.size();i++){

			if (result.get(i).getUsername().equals(u.getUsername()) || result.get(i).getEmail().equals(u.getEmail())){
				ResponseQuery.setDescription("Utente già registrato");
				em.getTransaction().commit();
				em.close();	
				return ResponseQuery;
			}
		}


		u.setPassword(convertMD5.encrypt(u.getPassword()));
		em.persist(u);	
		em.getTransaction().commit();
		em.close();		
		ResponseQuery.setDescription("Utente registrato");
		return ResponseQuery;
	}


}