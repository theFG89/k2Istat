package com.utils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class entityManagerUtils {

	public entityManagerUtils(){}

	private static  EntityManagerFactory emf;

	static {
		try {

			emf = Persistence.createEntityManagerFactory("DataVisualization");
		} catch (Throwable ex) {
			throw new RuntimeException("Exception occured in creating singleton instance");
		}
	}

	public static EntityManagerFactory getInstance(){
		return emf;
	}

}
