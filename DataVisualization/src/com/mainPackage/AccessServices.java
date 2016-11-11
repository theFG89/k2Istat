package com.mainPackage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.joda.time.DateTime;

import com.objectUsed.RequestClient;
import com.objectUsed.ResponseInfo;
import com.objectUsed.Token;
import com.objectUsed.User;
import com.utils.convertMD5;
import com.utils.entityManagerUtils;

@Path("/access")
public class AccessServices {
	//TESTING  DOWNLOAD UTENTI DA DB  E cavallo di troia

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
		return Response.status(200).entity(result).build();
	}

	///// ************ 	  NEW USER		***************	
	@POST
	@Path("/newUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerUser(User u){

		ResponseInfo ResponseQuery = new ResponseInfo(); 
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		if ((u.getUsername().trim().length()==0) || (u.getPassword().trim().length()==0) || (u.getEmail().trim().length()==0) ){
			ResponseQuery = returnResponse(false, 400, "Uno o piu' campi vuoti");
			return Response.status(200).entity(ResponseQuery).build();
		}
		em.getTransaction().begin();
		List<User> result = em.createQuery( " from User", User.class ).getResultList();
		for (int i=0;i<result.size();i++){

			if (result.get(i).getUsername().equals(u.getUsername()) || result.get(i).getEmail().equals(u.getEmail())){
				ResponseQuery = returnResponse(false, 400, "Utente gia' registrato");
				em.getTransaction().commit();
				em.close();	
				return Response.status(200).entity(ResponseQuery).build();
			}
		}
		u.setPassword(convertMD5.encrypt(u.getPassword()));
		em.persist(u);	
		em.getTransaction().commit();
		em.close();
		ResponseQuery = returnResponse(true,200,"Utente Registrato");
		return Response.status(200).entity(ResponseQuery).build();
	}
	/////// *******  METHOD LOGIN   ***************
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(User u) throws OAuthSystemException {
		//initialization variables


		ResponseInfo ResponseQuery = new ResponseInfo();
		Token tokenResponse = new Token();
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
					tokenResponse  = new Token();
					tokenResponse.setIdUser(result.get(i).getId());
					tokenResponse.setDateCreate(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
					tokenResponse.setDateExpired(getDataExpired());
					OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
					final String accessToken = oauthIssuerImpl.accessToken();
					
					tokenResponse.setAssignedToken(accessToken);
					if(expired==false)
						em.persist(tokenResponse);
					else
						em.merge(tokenResponse);	

					em.getTransaction().commit();
					em.close();
					return Response.status(200).entity(tokenResponse).build();	
				}
			} 	
		}

			ResponseQuery = returnResponse(false, 400, "Errore Password o Utente non trovato");
			return Response.status(200).entity(ResponseQuery).build();	
		}


	///////**********		METHOD CHECK TOKEN 	DATA EXPIRED	************
	private boolean checkTokenExpired(int id) {
		// inizialization variables
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		//find entity with id 
		Token outputToken = em.find(Token.class, id);
		if(outputToken!=null){
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String nowData = df.format(new Date()); 
			em.getTransaction().commit();		///CHIEDERE AD AMIR 
			em.close();
			if(nowData.compareTo(outputToken.getDateExpired())>0)  //if  nowData after expiredData  then token Expired				
				return true;	 		
		}

		return false;
	}
	//////////**********	METHOD SEARCH TOKEN INTO DB	***********
	private Token searchToken(int id) {
		Token outputToken  = new Token();
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		outputToken = em.find(Token.class, id);
		em.getTransaction().commit();
		em.close();
		return outputToken;
	}
	/////  ***********    SUM ONE DAY TO  CREATION DATE ************* 

	private String getDataExpired(){
		Date dt = new Date();
		DateTime dtOrg = new DateTime(dt);
		DateTime dtPlusOne = dtOrg.plusDays(1);
		String dataExpired = dtPlusOne.toString("yyyy-MM-dd HH:mm:ss");
		return dataExpired;
	}

	////***************		METHOD TO INSERT INFO RESPONSE OF REQUEST TO SEND FRONT-END	********
	private ResponseInfo returnResponse(boolean succ, int code, String descr){
		ResponseInfo R= new ResponseInfo();
		R.setSuccess(succ);
		R.setCode(code);
		R.setDescription(descr);
		return R;
	}




}