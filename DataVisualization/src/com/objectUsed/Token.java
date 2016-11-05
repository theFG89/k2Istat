package com.objectUsed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_token")
public class Token {

	@Id
	@Column(name= "id_user")
	private int idUser;
	
	@Column(name = "token")
	private String assignedToken;
	
	@Column(name = "dateCreate")
	private String dateCreate;
	
	@Column(name = "dateExpired")
	private String dateExpired;

	
	public String getDateCreate() {
		return dateCreate;
	}

	public void setDateCreate(String dataCreate) {
		this.dateCreate = dataCreate;
	}

	public int getIdUser() {
		return idUser;
	}
	
	public Token(){
		
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public String getAssignedToken() {
		return assignedToken;
	}
	public void setAssignedToken(String assignedToken) {
		this.assignedToken = assignedToken;
	}
	public String getDateExpired() {
		return dateExpired;
	}
	public void setDateExpired(String dateExpired) {
		this.dateExpired = dateExpired;
	}
	
}
