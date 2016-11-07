package com.thejavageek.jaxrs;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.joda.time.DateTime;

import com.alzHBase.RowC;
import com.alzHBase.TestHBase;
import com.google.gson.Gson;

@Path("/HelloWorld")
public class HelloWorldService {

	//TESTING  DOWNLOAD UTENTI DA DB  E cavallo di troia

	@GET
	@Path("/getusers")
	@Produces (MediaType.APPLICATION_JSON)
	public Response  getUser( @Context HttpServletResponse servletResponse){

		
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");		//IP matteo

		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		List<User> result = em.createQuery( "from User", User.class ).getResultList();	
		em.getTransaction().commit();
		em.close();
		String resultJ = new Gson().toJson(result);
		return Response.status(200).entity(resultJ).build();
	}

	/////  ***********    SUM ONE DAY TO  CREATION DATE ************* 

	private String getDataExpired(){
		Date dt = new Date();
		DateTime dtOrg = new DateTime(dt);
		DateTime dtPlusOne = dtOrg.plusDays(1);
		String dataExpired = dtPlusOne.toString("yyyy.MM.dd.HH.mm.ss");
		return dataExpired;
	}
	
	
	
						///// ************ 	  NEW USER		***************	
	@POST
	@Path("/newUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerUser(User u, @Context HttpServletResponse servletResponse){
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		
		
		
		System.out.println(u.getUsername());
		System.out.println(u.getPassword());
		System.out.println(u.getEmail());
		ResponseRequest ResponseQuery = new ResponseRequest(); 
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		if ((u.getUsername().length()<2) || (u.getPassword().length()<2) || (u.getEmail().length()<2) ){
			ResponseQuery.setSuccess(false);
			ResponseQuery.setCode(400);
			ResponseQuery.setDescription("Uno o pi� campi vuoti");
			return Response.status(200).entity(ResponseQuery).build();
		}
		em.getTransaction().begin();
		List<User> result = em.createQuery( " from User", User.class ).getResultList();
		for (int i=0;i<result.size();i++){

			if (result.get(i).getUsername().equals(u.getUsername()) || result.get(i).getEmail().equals(u.getEmail())){
				ResponseQuery.setSuccess(false);
				ResponseQuery.setCode(400);
				ResponseQuery.setDescription("Utente gi� registrato");
				em.getTransaction().commit();
				em.close();	
				return Response.status(200).entity(ResponseQuery).build();
			}
		}
		u.setPassword(convertMD5.encrypt(u.getPassword()));
		em.persist(u);	
		em.getTransaction().commit();
		em.close();
		ResponseQuery.setSuccess(true);
		ResponseQuery.setCode(200);
		ResponseQuery.setDescription("Utente registrato");
		return Response.status(200).entity(ResponseQuery).build();
	}
	

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(User u, @Context HttpServletResponse servletResponse) throws OAuthSystemException {
		//initialization variables
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		servletResponse.setHeader("Access-Control-Allow-Headers", "*");
		servletResponse.setHeader("Access-Control-Allow-Methods", "*");
        
		ResponseRequest ResponseQuery = new ResponseRequest();
		token tokenResponse = new token();
		boolean find = false;
		boolean expired=false;
		u.setPassword(convertMD5.encrypt(u.getPassword()));
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		//start

		List<User> result = em.createQuery( " from User", User.class ).getResultList();
		for (int i=0;i<result.size();i++){

			if (result.get(i).getUsername().equals(u.getUsername()) && result.get(i).getPassword().equals(u.getPassword())){
				tokenResponse = searchToken(result.get(i).getId());
				if((expired=checkTokenExpired(result.get(i).getId()))==false && tokenResponse!=null)		// if token isn't expired and isn't null
				{									
						em.getTransaction().commit();
						em.close();
						return Response.status(200).entity(tokenResponse).build();	
					}
					else{	
						find=true;		/// user found in table users
						tokenResponse  = new token();
						tokenResponse.setIdUser(result.get(i).getId());
						tokenResponse.setDateCreate(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
						tokenResponse.setDateExpired(getDataExpired());
					}
			} 	
		}
		if(find==true){

			OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
			final String accessToken = oauthIssuerImpl.accessToken();
			tokenResponse.setAssignedToken(accessToken);
			if(expired==false){
				em.persist(tokenResponse);
			}
			else{
				em.merge(tokenResponse);	
				
			}
			em.getTransaction().commit();
			em.close();
			return Response.status(200).entity(tokenResponse).build();		
		}else{
			ResponseQuery.setSuccess(false);
			ResponseQuery.setCode(404);
			ResponseQuery.setDescription("Utente non trovato");
			return Response.status(200).entity(ResponseQuery).build();	
		}
	}
	
	@GET
	@Path("/getData")
	@Produces(MediaType.APPLICATION_JSON)
	public Response mainView(){
		Map<String,ArrayList<RowC>> dati = new HashMap<String,ArrayList<RowC>>();	
		TestHBase result = new TestHBase();
		dati = result.getAllRecord("dati_demenza");
		Iterator it = dati.entrySet().iterator();
	
		return Response.status(200).entity(dati).build();
	}
	

	private boolean checkTokenExpired(int id) {
		// inizialization variables
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		//find entity with id 
		token outputToken = em.find(token.class, id);
		if(outputToken!=null){
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		String expiredData = outputToken.getDateExpired().toString();
		String nowData = df.format(new Date()); 
		System.out.println(nowData);
		System.out.println(expiredData);
		em.getTransaction().commit();
		em.close();
		if(nowData.compareTo(expiredData)<0)  //if  nowData after expiredData  then token Expired
		{
			System.out.println("Data token scaduta");
			return true;
		}
		else{
			System.out.println("Data token non scaduta");
			return false;
			}
		}
		else{
			System.out.println("token vuoto");
			return false;
		}
	}

	private token searchToken(int id) {
		token outputToken  = new token();
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		outputToken = em.find(token.class, id);
		em.getTransaction().commit();
		em.close();
		return outputToken;
	}


}