package com.objectUsed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name= "idU")
	private int idU;
	@Column(name = "username")
	private String username;
	@Column(name = "password")
	private String password;
	@Column(name = "email")
	private String email;
	@Column(name="passwordStatus")
	private String passwordStatus;
	@Column(name = "activationStatus")
	private String activationStatus;
	@Column(name="restoreExpired")
	private String restoreExpired;
	
	



	public User(int idU, String username, String password, String email, String passwordStatus, String activationStatus,
			String restoreExpired) {
		super();
		this.idU = idU;
		this.username = username;
		this.password = password;
		this.email = email;
		this.passwordStatus = passwordStatus;
		this.activationStatus = activationStatus;
		this.restoreExpired = restoreExpired;
	}

	public User(){
		super();
	}

	public int getId() {
		return idU;
	}
	public void setId(int id) {
		this.idU = id;
	}
	public String getUsername() {
		return username;
	}
	public void setName(String name) {
		this.username = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordStatus() {
		return passwordStatus;
	}

	public void setPasswordStatus(String passwordStatus) {
		this.passwordStatus = passwordStatus;
	}

	public String getActivationStatus() {
		return activationStatus;
	}

	public void setActivationStatus(String activationStatus) {
		this.activationStatus = activationStatus;
	}

	public String getRestoreExpired() {
		return restoreExpired;
	}

	public void setRestoreExpired(String restoreExpired) {
		this.restoreExpired = restoreExpired;
	}

	

}
