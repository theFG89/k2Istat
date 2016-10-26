package com.thejavageek.jaxrs;



public class Student {


	private String name;
	private String surname;
	private int age;
	
	public Student(String n,String s ,int a){
		this.name=n;
		this.surname=s;
		this.age=a;
	}
	public Student(){
		super();
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
