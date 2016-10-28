package com.thejavageek.jaxrs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "users")
public class User {

//	@TableGenerator(name = "users_gen", table = "id_users", pkColumnName = "gen_name", valueColumnName = "gen_val",
//			allocationSize = 1, pkColumnValue = "users,gen")
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
	
	public User(String n,String s ,String a){
		this.username=n;
		this.password=s;
		this.email=a;
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


}
