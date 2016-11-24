package com.mainPackage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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


import com.objectUsed.ResponseInfo;
import com.objectUsed.Token;
import com.objectUsed.User;
import com.utils.ConvertUtils;
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
	public Response registerUser(User u) throws OAuthSystemException{		

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
		try {
			OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());	///CREATING RANDOM VALUE TOKEN FOR ACTIVATION
			u.setActivationStatus(oauthIssuerImpl.accessToken());
			u.setPasswordStatus("1");
			String bodyMessage ="Benvenuto "+u.getUsername()+",<br><br>"+
								"la registrazione è quasi completata. Per attivare l'account cliccare sul seguente link: <br><br>"+
								//"<a href ='http://www.google.it'>clicca qui</a>"+
								"<a href='http://192.168.0.181/www/DE/web/views/#/attivazioneAccount/"+u.getEmail()+"/"+u.getActivationStatus()+"'>http://www.dementiaeexploration/activation.html</a><br><br>"+
								"Cordialmente,<br><br>"+
								"Dementiae Exploration  Staff<br><br>"; 
			sendMail(u.getEmail(),bodyMessage);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.setPassword(ConvertUtils.encrypt(u.getPassword()));
		em.persist(u);	
		em.getTransaction().commit();
		em.close();
		ResponseQuery = returnResponse(true,200,"Registrazione avvenuta!Per attivare l'account seguire le istruzioni contenute nella e-mail");
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
		u.setPassword(ConvertUtils.encrypt(u.getPassword()));
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		//start

		List<User> result = em.createQuery( " from User", User.class ).getResultList();
		for (int i=0;i<result.size();i++){
			
			if (result.get(i).getUsername().equals(u.getUsername()) && result.get(i).getPassword().equals(u.getPassword())){
				if(!result.get(i).getActivationStatus().equals("1")){
					ResponseQuery = returnResponse(false, 400, "Account non ancora attivo, controllare l'e-mail e seguire le indicazioni riportate");
					return Response.status(200).entity(ResponseQuery).build();	
				}
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

	public Properties getPropertyMail(){
		Properties mailServerProperties;
		// Step1	setup Mail Server Properties.
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "465");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.ssl.enable", "true");
		return mailServerProperties;
	}
	///******		METHOD  SEND EMAIL  *****************  
	
	private void sendMail(String receiver,String body) throws AddressException, MessagingException{
		Properties mailServerProperties;
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "465");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.ssl.enable", "true");
		Session getMailSession;	
		MimeMessage generateMailMessage;
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.setFrom(new InternetAddress("dementiae.info@tiscali.it"));
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));	
		generateMailMessage.setSubject("Dementiae Exploration");
		String pathIMG = "<img src='data:image/jpeg;base64,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wqBt/wCEb0BtvAIsoyMdP7p4Pr7U3U/Deh67rkdne+Hobr7PFiOeazV4Y067QxP6AenYiukUbRQBg1OIgq8fZ4hcy7NXX3PQ8/CUaeGfNhkoPvGy/IwYPA2g+HGa8s9B0+GeCNnVrWzUTMApyqbRncRkAd8471paTqQ1WwWZbe5tV+6I502sB/njPsauEZFGOazoYelRjyUYqK7JW/I6KlapUd6jbfnqFBOKKbK2FrYzIZn5qnO3DVNO/NVJnxQBXnfA/GqzPzU0z8VUY5agBIn+arcD8VnRScVbgk4oA04H5FWoH49R/Ks+GT5qtQyZFAGlG+4U6q0MuMVZByKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAGyRrIMMu4enrWXqXhC11DO6NRu9q1qKLsLHGXfwltbjnapqvF8HbdW/1Y/Su7op3ZPKcxpvw4tbMj5V+Wt2y0mKxUBF6VaopDWgYxRRRQMKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAqvO/wA1TSnAqnNJQBFO1U52zn9amneqUz9aAI5m5qq0vzd6kmfmqzPzQAkLcVagfiiigC5E/wA4q1C/FFFAFyBqtxNxRRQA+iiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKD0oooArzGqszYzRRQBTmfrVOdqKKAKszfzqq5wxoooA//9k=' />";
		generateMailMessage.setContent(body +pathIMG, "text/html; charset=utf-8");
		Transport transport = getMailSession.getTransport("smtp");
		transport.connect("smtp.tiscali.it", "dementiae.info@tiscali.it", "Datascience0");
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
		
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
			em.getTransaction().commit();	
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
	
	///////**********		METHOD CHECK TOKEN PASSWORD DATA EXPIRED	************
	private boolean checkTokenPasswordExpired(int id) {
		// inizialization variables
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		//find entity with id 
		User u= em.find(User.class, id);
		if(u!=null){
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String nowData = df.format(new Date()); 
			em.getTransaction().commit();		
			em.close();
			if(nowData.compareTo(u.getRestoreExpired())>0)  //if  nowData after expiredData  then token password expired			
				return true;	 		
		}

		return false;
	}
	

	//////*******		TOKEN PASSWORD VALIDATION		***********
	@POST
	@Path("/tokenPasswordValidation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response tokenPasswordValidation(User u){
		//token  e email
		ResponseInfo responseInfo = new ResponseInfo();
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		List<User> result = em.createQuery( " from User", User.class ).getResultList();
		for (int i=0;i<result.size();i++){
			if (result.get(i).getEmail().equals(u.getEmail())) {
				if(checkTokenPasswordExpired(result.get(i).getId())){
					responseInfo = returnResponse(false, 401, "Token scaduto!Torna ad invio e-mail per recupero password!");
					return Response.status(200).entity(responseInfo).build();
				}
				if(result.get(i).getPasswordStatus().equalsIgnoreCase(u.getPasswordStatus().trim()))
				{
					responseInfo = returnResponse(true, 200, "Token corrispondente!");
					em.getTransaction().commit();		
					em.close();
					return Response.status(200).entity(responseInfo).build();
				}
				else{
					responseInfo = returnResponse(false, 400, "Token errato!");
					em.getTransaction().commit();		
					em.close();
					return Response.status(200).entity(responseInfo).build();
				}
			}	
				
		}
		em.getTransaction().commit();		
		em.close();
		responseInfo = returnResponse(false, 400, "Utente non trovato");
		return Response.status(200).entity(responseInfo).build();
	}

	//////*******		CHANGE PASSWORD 	***********
	@POST
	@Path("/changePassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response modifyPassword(User u){
		ResponseInfo responseInfo = new ResponseInfo();
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		List<User> result = em.createQuery( " from User", User.class ).getResultList();
		for (int i=0;i<result.size();i++){
			if(result.get(i).getEmail().equals(u.getEmail())){
				result.get(i).setPassword(ConvertUtils.encrypt(u.getPassword()));
				result.get(i).setPasswordStatus("1");
				result.get(i).setRestoreExpired(null);
				em.merge(result.get(i));	
				em.getTransaction().commit();
				em.close();
				responseInfo = returnResponse(true, 200, "Password modificata!");
				return Response.status(200).entity(responseInfo).build();
			}
		}
		em.getTransaction().commit();		
		em.close();
		responseInfo = returnResponse(false, 400, "Errore!E-mail utente non trovata");
		return Response.status(200).entity(responseInfo).build();
	}
	
	////******  METHOD SEND MAIL RESTORE******	
	@POST
	@Path("/sendMailRestore")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendForRestore(User u) throws MessagingException{
		ResponseInfo responseInfo;
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		//start
		List<User> result = em.createQuery( " from User", User.class ).getResultList();
		for (int i=0;i<result.size();i++){
			if(!result.get(i).getActivationStatus().equals("1")){	
				em.close();
				responseInfo = returnResponse(true, 400, "Prima di poter utilizzare i servizi richiesti è necessario attivare l'account, controlla la tua casella di posta");
				return Response.status(200).entity(responseInfo).build();	
			}
			if (result.get(i).getEmail().equals(u.getEmail())){
				result.get(i).setPasswordStatus(ConvertUtils.generateRandomString());
				result.get(i).setRestoreExpired(getDataExpired());
				em.merge(result.get(i));	
				//em.getTransaction().commit();
				String bodySend = "Gentile "+result.get(i).getUsername()+",<br><br>"+
						"In seguito alla tua richiesta di recupero password, ti invitiamo a inserire il seguente codice nell'apposito riquadro:<br><br>"+
						"<b>"+result.get(i).getPasswordStatus()+"</b><br><br>"+
						"Cordialmente,<br><br>"+
						"Dementiae Exploration  Staff<br><br>";
				sendMail(u.getEmail(), bodySend);				
				responseInfo = returnResponse(true, 200, "Email inviata con successo, controllare la casella di posta elettronica");
				em.getTransaction().commit();		
				em.close();
				return Response.status(200).entity(responseInfo).build();	
			}
		}
		em.getTransaction().commit();		
		em.close();
		responseInfo = returnResponse(true, 400, "Utente non trovato, inserire di nuovo l'e-mail");
		return Response.status(200).entity(responseInfo).build();		
	}

////******  METHOD ACTIVATION USER******	
	@POST
	@Path("/activation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response activation(User u) {
		ResponseInfo responseInfo = null;
		EntityManagerFactory  emf = entityManagerUtils.getInstance();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		
		List<User> result = em.createQuery("from User",User.class ).getResultList();
		for(int i=0;i<result.size();i++){
			if (result.get(i).getEmail().equals(u.getEmail())){
				if(result.get(i).getActivationStatus().equals(u.getActivationStatus())){
					result.get(i).setActivationStatus("1");					
					em.merge(result.get(i));
					em.getTransaction().commit();
					em.close();
					responseInfo = returnResponse(true, 200, "Attivazione account completata");
					return Response.status(200).entity(responseInfo).build();
				}
				if(result.get(i).getActivationStatus().equals("1")){
					em.close();
					responseInfo = returnResponse(false, 400, "Utente già attivo");
					return Response.status(200).entity(responseInfo).build();
				}
			}
		}
		responseInfo = returnResponse(false, 400, "Attivazione non riuscita");
		return Response.status(200).entity(responseInfo).build();		
	}
	



}