package com.thejavageek.jaxrs;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class entityManagerUtils {

	public entityManagerUtils(){}

	private static  EntityManagerFactory emf;

	static {
		try {

			emf = Persistence.createEntityManagerFactory("JAX-RS_example");
		} catch (Throwable ex) {
			throw new RuntimeException("Exception occured in creating singleton instance");
		}
	}

	public static EntityManagerFactory getInstance(){
		return emf;
	}

}
